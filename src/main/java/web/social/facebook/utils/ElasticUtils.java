package web.social.facebook.utils;

import com.google.common.base.Splitter;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.social.facebook.entities.Comment;
import web.social.facebook.entities.DTO.ChartInfoDTO;
import web.social.facebook.entities.DTO.Reaction;
import web.social.facebook.entities.Mention;
import web.social.facebook.entities.Post;

import java.util.*;
import java.util.stream.Collectors;

public class ElasticUtils {
    private static final Logger logger = LoggerFactory.getLogger(ElasticUtils.class);

    private static final String PRE_INDICE_NAME = "indexer_";

    private static BoolQueryBuilder makeANDBoolQuery(String field, List<String> keywords) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(keywords);

        if (keywords.isEmpty())
            throw new IllegalArgumentException("keywords cannot be empty");

        BoolQueryBuilder boolQuery = new BoolQueryBuilder();

        for (String keyword : keywords) {
            keyword = keyword.trim();
            if (keyword.isEmpty())
                continue;

            if (!keyword.startsWith("!")) {
                boolQuery.must(QueryBuilders.matchPhraseQuery(field, keyword));
            } else {
                keyword = keyword.substring(1);
                if (keyword.trim().isEmpty())
                    continue;

                boolQuery.mustNot(QueryBuilders.matchPhraseQuery(field, keyword));
            }
        }

        return boolQuery;
    }

    private static BoolQueryBuilder orAndBoolQuery(List<List<String>> conditions) {
        Objects.requireNonNull(conditions);

        if (conditions.isEmpty())
            throw new IllegalArgumentException("conditions cannot be empty");

        // create list Title and Content

        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        for (List<String> keywords : conditions) {
            boolQuery.should(makeANDBoolQuery("content", keywords));

        }

        return boolQuery;
    }

    private static BoolQueryBuilder orAndBoolQueryWrapper(List<String> conditions) {
        List<List<String>> collect = conditions.stream().map(s -> Splitter.on(",").splitToList(s))
                .collect(Collectors.toList());

        return orAndBoolQuery(collect);
    }

    private static BoolQueryBuilder queryForTitleAndContent(String mustWord, String stopWords) {
        List<String> mustList = Splitter.on(",").splitToList(mustWord);
        // TODO
        BoolQueryBuilder boolMaster = new BoolQueryBuilder();

        // Bool Must
        BoolQueryBuilder boolMust = new BoolQueryBuilder();
        BoolQueryBuilder boolMustContent = orAndBoolQueryWrapper(mustList);
        boolMust.should(boolMustContent);

        // Bool Stop
        if (!stopWords.isEmpty()) {
            List<String> stopList = Splitter.on(",").splitToList(stopWords);
            BoolQueryBuilder boolStop = new BoolQueryBuilder();
            if (!stopList.isEmpty()) {
                BoolQueryBuilder boolStopContent = orAndBoolQueryWrapper(stopList);
                boolStop.should(boolStopContent);
                boolMaster.mustNot(boolStop);
            }
        }
        boolMaster.must(boolMust);

        return boolMaster;

    }

    public static BoolQueryBuilder makeQuery(String mustWord, String stopWord) {
        return queryForTitleAndContent(mustWord, stopWord);
    }

    private static Set<String> getIndices(Client client) {
        ImmutableOpenMap<String, IndexMetaData> map = client.admin().cluster().prepareState().get().getState()
                .getMetaData().getIndices();
        Set<String> indices = new HashSet<>();
        indices.addAll(Arrays.asList(map.keys().toArray(String.class)));
        return indices;
    }

    private static String getIndexTime(String preFix, int day, Client client) {
        Set<String> indices = new HashSet<>();
        if (client != null) {
            indices = ElasticUtils.getIndices(client);
        }
        String indice = preFix + day;
        if (indices.contains(indice)) {
            return indice;
        }
        return "";
    }

    public static List<Mention> getMentionInDay(Client client, BoolQueryBuilder boolQueryBuilder, int topicId, int size, int date) throws JSONException {
        Objects.requireNonNull(client);
        Objects.requireNonNull(boolQueryBuilder);

        List<Mention> mentions = new ArrayList<>();

        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return mentions;
        }

        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setQuery(boolQueryBuilder)
                .setSize(size)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        for (SearchHit searchHit : results) {
            String sourceAsString = searchHit.getSourceAsString();

            String id = searchHit.getId();
            String url = new JSONObject(sourceAsString).getString("url");
            String title = new JSONObject(sourceAsString).getString("title").replaceAll("<br> See Translation", "");
            String content = new JSONObject(sourceAsString).getString("content").replaceAll("<br> See Translation", "");
            String pubDate = new JSONObject(sourceAsString).getString("pubDate");

            String publisher = new JSONObject(sourceAsString).getString("publisher");
            JSONObject json = new JSONObject(publisher);
            String userPub = json.getString("name");

            Mention mention = new Mention(id, topicId, url, title, content, userPub, pubDate, true);
            mentions.add(mention);
        }

        return mentions;
    }

    public static BoolQueryBuilder makeBoolQueryOneDocument(String id) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("_id", id));
        return boolQueryBuilder;
    }

    public static Mention getOneDocument(Client client, BoolQueryBuilder boolQueryBuilder, int date) throws JSONException {
        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return new Mention();
        }
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setQuery(boolQueryBuilder)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        Mention mention = new Mention();
        for (SearchHit searchHit : results) {
            String id = searchHit.getId();
            String sourceAsString = searchHit.getSourceAsString();
            String url = new JSONObject(sourceAsString).getString("url");
            String title = new JSONObject(sourceAsString).getString("title").replaceAll("<br> See Translation", "");
            String content = new JSONObject(sourceAsString).getString("content").replaceAll("<br> See Translation", "");
            String pubDate = new JSONObject(sourceAsString).getString("pubDate");

            String publisher = new JSONObject(sourceAsString).getString("publisher");
            JSONObject json = new JSONObject(publisher);
            String userPub = json.getString("name");

            mention.setId(id);
            mention.setTopicId(0);
            mention.setUrl(url);
            mention.setTitle(title);
            mention.setContent(content);
            mention.setUserPub(userPub);
            mention.setPubTime(pubDate);
            mention.setShow(true);
        }
        return mention;
    }

    public static ChartInfoDTO getNumberDocument(Client client, BoolQueryBuilder boolQueryBuilder, int date) {
        Objects.requireNonNull(boolQueryBuilder);

        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return new ChartInfoDTO(String.valueOf(date), 0);
        }
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setScroll(new TimeValue(60000))
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .execute()
                .actionGet();
        int numberDocument = 0;
        while (true) {
            SearchHit[] results = searchResponse.getHits().getHits();
            numberDocument += results.length;
            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000))
                    .execute().actionGet();
            if (searchResponse.getHits().getHits().length == 0) {
                break;
            }
        }
        return new ChartInfoDTO(String.valueOf(date), numberDocument);
    }

    public static Map<String, Long> countKeyWord(Client client, BoolQueryBuilder boolQueryBuilder, int date) {
        Objects.requireNonNull(boolQueryBuilder);

        Map<String, Long> map = new HashMap<>();
        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            map.put("", 0L);
            return map;
        }
        TermsBuilder termsBuilder = AggregationBuilders
                .terms("count_keyword")
                .field("content")
                .size(20);
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .addAggregation(termsBuilder)
                .execute()
                .actionGet();
        Terms terms = searchResponse.getAggregations().get("count_keyword");
        for (Terms.Bucket entry : terms.getBuckets()) {
            if (!entry.getKeyAsString().isEmpty()) {
                map.put(entry.getKeyAsString(), entry.getDocCount());
            }
        }

        return map;
    }

    public static Map<String, Long> getDeCapTheoGio(Client client, BoolQueryBuilder boolQueryBuilder, int date) {
        Objects.requireNonNull(boolQueryBuilder);

        Map<String, Long> map = new HashMap<>();
        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            map.put("", 0L);
            return map;
        }
        DateHistogramBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("theo_gio").field("pubDate")
                .timeZone("Asia/Ho_Chi_Minh").interval(DateHistogramInterval.hours(1));
        SearchResponse searchResponse = client.prepareSearch(indices)
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .addAggregation(dateHistogramBuilder).get();
        Histogram histogram = searchResponse.getAggregations().get("theo_gio");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            String key = String.valueOf(entry.getKey());
            int hour = Integer.parseInt(key.split("T")[1].split(":")[0]);
            String strHour = hour + "h - " + (hour + 1) + "h";
            long docCount = entry.getDocCount();
            map.put(strHour, docCount);
        }
        return map;
    }

    public static int countDeCap(Client client, BoolQueryBuilder boolQueryBuilder, int date) {
        Objects.requireNonNull(boolQueryBuilder);

        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return 0;
        }
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        return results.length;
    }

    public static int countPost(Client client, BoolQueryBuilder boolQueryBuilder, int date) throws JSONException {
        Objects.requireNonNull(boolQueryBuilder);

        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return 0;
        }
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        int count = 0;
        for (SearchHit searchHit : results) {
            String sourceAsString = searchHit.getSourceAsString();
            String url = new JSONObject(sourceAsString).getString("url");
            if (url.contains("post") && !url.contains("comment_id")) {
                count++;
            }
        }
        return count;

    }

    public static int countComment(Client client, BoolQueryBuilder boolQueryBuilder, int date) throws JSONException {
        Objects.requireNonNull(boolQueryBuilder);

        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return 0;
        }
        int count = 0;
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        for (SearchHit searchHit : results) {
            String sourceAsString = searchHit.getSourceAsString();
            String url = new JSONObject(sourceAsString).getString("url");
            if (url.contains("comment_id")) {
                count++;
            }
        }
        return count;
    }

    public static List<Post> getListPost(Client client, BoolQueryBuilder boolQueryBuilder, int date) throws JSONException {

        List<Post> posts = new ArrayList<>();

        Objects.requireNonNull(boolQueryBuilder);
        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return posts;
        }
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        for (SearchHit searchHit : results) {
            String sourceAsString = searchHit.getSourceAsString();
            String url = new JSONObject(sourceAsString).getString("url");
            if (url.contains("post") && !url.contains("comment_id")) {
                String title = new JSONObject(sourceAsString).getString("title").replaceAll("<br> See Translation", "");
                String content = new JSONObject(sourceAsString).getString("content").replaceAll("<br> See Translation", "");
                String pubDate = new JSONObject(sourceAsString).getString("pubDate");
                String reaction = new JSONObject(sourceAsString).getString("post");
                JSONObject json = new JSONObject(reaction);
                JSONObject summaryJson = json.getJSONObject("summary");
                String like = summaryJson.getString("like");
                String comment = summaryJson.getString("comment");
                String share = summaryJson.getString("share");
                double score = calculateScore(like, comment, share);

                Post post = new Post();
                post.setUrl(url);

                if (like.isEmpty()) {
                    post.setLikeTotal(0);
                } else {
                    post.setLikeTotal(Integer.parseInt(like));
                }

                if (comment.isEmpty()) {
                    post.setCommentTotal(0);
                } else {
                    post.setCommentTotal(Integer.parseInt(comment));
                }

                if (share.isEmpty()) {
                    post.setShareTotal(0);
                } else {
                    post.setShareTotal(Integer.parseInt(share));
                }
                post.setTitle(title);
                post.setContent(content);
                post.setDate(pubDate);
                post.setScore(score);

                // add to list post
                posts.add(post);

            }
        }
        return posts;
    }

    public static Reaction getReaction(Client client, BoolQueryBuilder boolQueryBuilder, int date, String urlPost) throws JSONException {
        Objects.requireNonNull(boolQueryBuilder);
        Objects.requireNonNull(urlPost);

        int like = 0;
        int comment = 0;
        int share = 0;
        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return new Reaction();
        }

        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setQuery(QueryBuilders.matchPhraseQuery("url", urlPost))
                .setSize(10000)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        for (SearchHit searchHit : results) {
            String sourceAsString = searchHit.getSourceAsString();
            String url = new JSONObject(sourceAsString).getString("url");
            if (url.equals(urlPost) && !url.contains("comment_id")) {
                String reactionJson = new JSONObject(sourceAsString).getString("post");
                JSONObject json = new JSONObject(reactionJson);
                JSONObject summaryJson = json.getJSONObject("summary");
                String strLike = summaryJson.getString("like");
                if (!strLike.isEmpty()) {
                    like = Integer.parseInt(strLike);
                }
                String strComment = summaryJson.getString("comment");
                if (!strComment.isEmpty()) {
                    comment = Integer.parseInt(strComment);
                }

                String strShare = summaryJson.getString("share");
                if (!strShare.isEmpty()) {
                    share = Integer.parseInt(strShare);
                }
            }
        }
        return new Reaction(like, comment, share);
    }

    public static List<Comment> getListCommentFromPost(Client client, BoolQueryBuilder boolQueryBuilder, int date, String url) throws JSONException {
        Objects.requireNonNull(boolQueryBuilder);
        Objects.requireNonNull(url);

        List<Comment> comments = new ArrayList<>();
        String postId = url.substring(url.indexOf("posts/") + 6, url.lastIndexOf(""));
        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return comments;
        }
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setQuery(boolQueryBuilder)
                .setSize(10000)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        for (SearchHit searchHit : results) {
            String sourceAsString = searchHit.getSourceAsString();
            String urlComment = new JSONObject(sourceAsString).getString("url");
            if (urlComment.contains(postId) && urlComment.contains("comment_id")) {
                String content = new JSONObject(sourceAsString).getString("content").replaceAll("<br> See Translation", "");
                String pubDate = new JSONObject(sourceAsString).getString("pubDate");

                String publisher = new JSONObject(sourceAsString).getString("publisher");
                JSONObject json = new JSONObject(publisher);
                String userName = json.getString("name");
                String userId = json.getString("id");

                Comment comment = new Comment();
                comment.setUrl(urlComment);
                comment.setContent(content);
                comment.setPubDate(pubDate);
                comment.setUserName(userName);
                comment.setUserId(userId);

                comments.add(comment);
            }
        }

        return comments;
    }

    private static double calculateScore(String like, String comment, String share) {
        Objects.requireNonNull(like);
        Objects.requireNonNull(comment);
        Objects.requireNonNull(share);

        double scoreLike = 0;
        double scoreComment = 0;
        double scoreShare = 0;
        if (!like.isEmpty()) {
            scoreLike = Integer.parseInt(like) * 0.2;
        }
        if (!comment.isEmpty()) {
            scoreComment = Integer.parseInt(comment) * 0.5;
        }
        if (!share.isEmpty()) {
            scoreShare = Integer.parseInt(share) * 0.3;
        }

        return scoreLike + scoreComment + scoreShare;
    }

    public static Post getDetailPost(Client client, BoolQueryBuilder boolQueryBuilder, int date) throws JSONException {
        Objects.requireNonNull(boolQueryBuilder);

        String indices = ElasticUtils.getIndexTime(PRE_INDICE_NAME, date, client);
        if (indices.isEmpty()) {
            return new Post();
        }
        SearchResponse searchResponse = client
                .prepareSearch(indices)
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .execute()
                .actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        Post post = new Post();
        for (SearchHit searchHit : results) {
            String sourceAsString = searchHit.getSourceAsString();
            String url = new JSONObject(sourceAsString).getString("url");
            String title = new JSONObject(sourceAsString).getString("title").replaceAll("<br> See Translation", "");
            String content = new JSONObject(sourceAsString).getString("content").replaceAll("<br> See Translation", "");
            String pubDate = new JSONObject(sourceAsString).getString("pubDate");
            String reaction = new JSONObject(sourceAsString).getString("post");
            JSONObject json = new JSONObject(reaction);
            JSONObject summaryJson = json.getJSONObject("summary");
            String like = summaryJson.getString("like");
            String comment = summaryJson.getString("comment");
            String share = summaryJson.getString("share");
            double score = calculateScore(like, comment, share);


            post.setUrl(url);

            if (like.isEmpty()) {
                post.setLikeTotal(0);
            } else {
                post.setLikeTotal(Integer.parseInt(like));
            }

            if (comment.isEmpty()) {
                post.setCommentTotal(0);
            } else {
                post.setCommentTotal(Integer.parseInt(comment));
            }

            if (share.isEmpty()) {
                post.setShareTotal(0);
            } else {
                post.setShareTotal(Integer.parseInt(share));
            }
            post.setTitle(title);
            post.setContent(content);
            post.setDate(pubDate);
            post.setScore(score);
        }

        return post;
    }
}
