package web.social.facebook.api;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.social.facebook.entities.Comment;
import web.social.facebook.entities.DTO.ChartInfoDTO;
import web.social.facebook.entities.DTO.DetailReaction;
import web.social.facebook.entities.DTO.Header;
import web.social.facebook.entities.DTO.Reaction;
import web.social.facebook.entities.Post;
import web.social.facebook.repository.TopicRepository;
import web.social.facebook.utils.ElasticUtils;
import web.social.facebook.utils.TimeUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class OverviewApi {
    private static final Logger logger = LoggerFactory.getLogger(OverviewApi.class);
    private final TopicRepository topicRepository;
    private final Client client;

    public OverviewApi(Client client, TopicRepository topicRepository) {
        this.client = client;
        this.topicRepository = topicRepository;
    }

    @GetMapping(value = "/overview/{topicId}/getHeader")
    public ResponseEntity<Header> showHeader(@PathVariable int topicId,
                                             @RequestParam(value = "start") String start,
                                             @RequestParam(value = "end") String end) throws JSONException {
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);

        int countDeCap = 0;
        int countPost = 0;
        int countComment = 0;
        while (!lcStart.isAfter(lcEnd)) {
            String time = lcStart.format(TimeUtils.format);
            int nTime = Integer.parseInt(time);
            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);

            int deCapNumber = ElasticUtils.countDeCap(client, boolQueryBuilder, nTime);
            countDeCap += deCapNumber;

            int postNumber = ElasticUtils.countPost(client, boolQueryBuilder, nTime);
            countPost += postNumber;

            int commentNumber = ElasticUtils.countComment(client, boolQueryBuilder, nTime);
            countComment += commentNumber;

            lcStart = lcStart.plusDays(1);
        }
        Header header = new Header(countDeCap, countPost, countComment);

        return new ResponseEntity<>(header, HttpStatus.OK);
    }

    @GetMapping(value = "/overview/{topicId}")
    public ResponseEntity<List<ChartInfoDTO>> getInteractive(@PathVariable int topicId,
                                                             @RequestParam(value = "start") String start,
                                                             @RequestParam(value = "end") String end) {
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);
        List<ChartInfoDTO> chartInfoDTOList = new ArrayList<>();
        while (!lcStart.isAfter(lcEnd)) {
            String time = lcStart.format(TimeUtils.format);

            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
            ChartInfoDTO chartInfoDTO = ElasticUtils.getNumberDocument(client, boolQueryBuilder, Integer.parseInt(time));
            chartInfoDTOList.add(chartInfoDTO);

            lcStart = lcStart.plusDays(1);
        }
        return new ResponseEntity<>(chartInfoDTOList, HttpStatus.OK);
    }

    @GetMapping(value = "/overview/{topicId}/countKeyWord")
    public ResponseEntity<List<ChartInfoDTO>> getCountKeyWord(@PathVariable int topicId,
                                                              @RequestParam(value = "start") String start,
                                                              @RequestParam(value = "end") String end) {
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);

        Map<String, Long> mapData = new HashMap<>();
        while (!lcStart.isAfter(lcEnd)) {
            String time = lcStart.format(TimeUtils.format);
            int nTime = Integer.parseInt(time);
            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
            Map<String, Long> mapCount = ElasticUtils.countKeyWord(client, boolQueryBuilder, nTime);
            for (Map.Entry<String, Long> entry : mapCount.entrySet()) {
                if (mapData.containsKey(entry.getKey())) {
                    Long valueItemMapData = mapData.get(entry.getKey());
                    Long result = valueItemMapData + entry.getValue();
                    mapData.put(entry.getKey(), result);
                } else {
                    mapData.put(entry.getKey(), entry.getValue());
                }
            }
            lcStart = lcStart.plusDays(1);
        }
        List<ChartInfoDTO> chartInfoDTOS = new ArrayList<>();

        LinkedHashMap<String, Long> finalMap = mapData.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Long>comparingByValue().reversed()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<String, Long> test : finalMap.entrySet()) {
            ChartInfoDTO chartInfoDTO = new ChartInfoDTO(test.getKey(), test.getValue().intValue());
            chartInfoDTOS.add(chartInfoDTO);

        }

        return new ResponseEntity<>(chartInfoDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/overview/{topicId}/getDecapTheoGio")
    public ResponseEntity<List<ChartInfoDTO>> getDeCapTheoGio(@PathVariable int topicId,
                                                              @RequestParam(value = "start") String start,
                                                              @RequestParam(value = "end") String end) {
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);


        Map<String, Long> mapStatic = new LinkedHashMap<>();
        for (int hour = 0; hour <= 24; hour++) {
            mapStatic.put(hour + "h - " + (hour + 1) + "h", (long) 0);
        }
        List<Map<String, Long>> listMapDeCap = new ArrayList<>();
        while (!lcStart.isAfter(lcEnd)) {
            String time = lcStart.format(TimeUtils.format);
            int nTime = Integer.parseInt(time);
            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
            Map<String, Long> deCapTheoGio = ElasticUtils.getDeCapTheoGio(client, boolQueryBuilder, nTime);
            listMapDeCap.add(deCapTheoGio);
            lcStart = lcStart.plusDays(1);
        }

        List<ChartInfoDTO> chartInfoDTOS = new ArrayList<>();
        for (Map.Entry<String, Long> entry : mapStatic.entrySet()) {
            String hour = entry.getKey();
            Long number = entry.getValue();
            for (Map<String, Long> item : listMapDeCap) {
                for (Map.Entry<String, Long> subMap : item.entrySet()) {
                    String subHour = subMap.getKey();
                    if (subHour.contains(hour)) {
                        number += subMap.getValue();
                    }
                }
            }
            chartInfoDTOS.add(new ChartInfoDTO(hour, number.intValue()));
        }

        return new ResponseEntity<>(chartInfoDTOS, HttpStatus.OK);
    }

    /**
     * Lấy danh sách bài post theo topic id.
     * @param topicId
     * @param start
     * @param end
     * @return
     */
    @GetMapping(value = "/overview/{topicId}/getListPost")
    public ResponseEntity<List<Post>> getListPostForTopic(@PathVariable int topicId,
                                                          @RequestParam(value = "start") String start,
                                                          @RequestParam(value = "end") String end) {
        List<Post> posts = new ArrayList<>();
        Set<String> set = new HashSet<>();
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);
        while (!lcStart.isAfter(lcEnd)) {
            String time = lcStart.format(TimeUtils.format);
            int nTime = Integer.parseInt(time);
            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
            try {
                List<Post> listPost = ElasticUtils.getListPost(client, boolQueryBuilder, nTime);
                for (Post post : listPost) {
                    String url = post.getUrl();
                    if (!set.contains(url)) {
                        set.add(url);
                        posts.add(post);
                    }
                }
                lcStart = lcStart.plusDays(1);
            } catch (JSONException e) {
                logger.error("JSONException when get list post ", e);
            }
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * Build biểu đồ
     * @param topicId
     * @param start
     * @param end
     * @param url
     * @return
     */
    @GetMapping(value = "/overview/{topicId}/getReactionForTime")
    public ResponseEntity<List<DetailReaction>> getListReactionPost(@PathVariable int topicId,
                                                                    @RequestParam(value = "start") String start,
                                                                    @RequestParam(value = "end") String end,
                                                                    @RequestParam(value = "url") String url) {
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);

        List<DetailReaction> detailReactions = new ArrayList<>();
        while (!lcStart.isAfter(lcEnd)) {
            String time = lcStart.format(TimeUtils.format);
            int nTime = Integer.parseInt(time);
            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
            Reaction reaction = null;
            try {
                reaction = ElasticUtils.getReaction(client, boolQueryBuilder, nTime, url);
                DetailReaction detailReaction = new DetailReaction(time, reaction);
                detailReactions.add(detailReaction);
            } catch (JSONException e) {
                logger.error("JSONException when get reaction for post");
            }
            lcStart = lcStart.plusDays(1);
        }

        return new ResponseEntity<>(detailReactions, HttpStatus.OK);
    }

    /**
     * Lấy list comment theo bài post phù hợp với keyword của topic id.
     * @param topicId
     * @param start
     * @param end
     * @param url
     * @return
     */
    @GetMapping(value = "/overview/{topicId}/getListComment")
    public ResponseEntity<List<Comment>> getListCommentFromPostId(@PathVariable int topicId,
                                                                  @RequestParam(value = "start") String start,
                                                                  @RequestParam(value = "end") String end,
                                                                  @RequestParam(value = "url") String url) {
        List<Comment> comments = new ArrayList<>();
        Set<String> set = new HashSet<>();
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);
        while (!lcStart.isAfter(lcEnd)) {
            String time = lcStart.format(TimeUtils.format);
            int nTime = Integer.parseInt(time);
            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
            try {
                List<Comment> listComment = ElasticUtils.getListCommentFromPost(client, boolQueryBuilder, nTime, url);
                for (Comment comment : listComment) {
                    String urlComment = comment.getUrl();
                    if (!set.contains(urlComment)) {
                        set.add(url);
                        comments.add(comment);
                    }
                }

                lcStart = lcStart.plusDays(1);
            } catch (JSONException e) {
                logger.error("JSONException: ", e);
            }
        }

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * Lấy tổng like, share, comment của bài post
     * @param topicId
     * @param start
     * @param end
     * @param url
     * @return
     */
    @GetMapping(value = "/overview/{topicId}/getReactionForPost")
    public ResponseEntity<Reaction> getReactionPost(@PathVariable int topicId,
                                                    @RequestParam(value = "start") String start,
                                                    @RequestParam(value = "end") String end,
                                                    @RequestParam(value = "url") String url) {
        Objects.requireNonNull(end);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);

        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);

        BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
        while (true) {
            try {
                Reaction reaction = ElasticUtils.getReaction(client, boolQueryBuilder, Integer.parseInt(format.format(lcEnd)), url);

                if (!(reaction.getLike() == 0) || !(reaction.getComment() == 0) || !(reaction.getShare() == 0)) {
                    return new ResponseEntity<>(reaction, HttpStatus.OK);
                } else {
                    lcEnd = lcEnd.minusDays(1);
                }
                if (lcEnd.isBefore(lcStart)) {
                    return new ResponseEntity<>(new Reaction(), HttpStatus.OK);
                }
            } catch (JSONException e) {
                logger.error("JSONException: ", e);
                return new ResponseEntity<>(new Reaction(), HttpStatus.OK);
            }
        }
    }

    @GetMapping(value = "/overview/{topicId}/getDetailPost")
    public ResponseEntity<Post> getDetailPost(@PathVariable int topicId,
                                              @RequestParam(value = "url") String url,
                                              @RequestParam(value = "date") String date) {

        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);
        BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
        try {
            Post post = ElasticUtils.getDetailPost(client, boolQueryBuilder, Integer.parseInt(date));
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (JSONException e) {
            logger.error("JSONException: ", e);
            return new ResponseEntity<>(new Post(), HttpStatus.OK);
        }
    }

}
