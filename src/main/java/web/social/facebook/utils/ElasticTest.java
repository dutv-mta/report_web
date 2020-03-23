package web.social.facebook.utils;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import web.social.facebook.entities.Comment;
import web.social.facebook.entities.DTO.ChartInfoDTO;
import web.social.facebook.entities.Mention;
import web.social.facebook.entities.Post;

import java.net.InetAddress;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ElasticTest {
    private Client client;
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ElasticTest(Client client) {
        this.client = client;
    }

    public static void main(String[] args) throws Exception {
        String indices = "indexer_20191021";
        int port = 9300;
        String host = "34.87.51.88";
        String clusterName = "dutv";
//        String field = "content";
//        List<String> must = new ArrayList<>();
//        must.add("công");
//        must.add("đức");
//
//        List<String> stop = new ArrayList<>();
//        stop.add("iphone");
//        stop.add("samsung");
        String mustWord = "quảng cáo";
        String stopWord = "iphone| samsung";


        Settings settingClient = Settings.settingsBuilder().put("cluster.name", clusterName).build();
        Client client = TransportClient.builder().settings(settingClient).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
//        BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(field, must, stop);
//        ElasticUtils.getResult(client,boolQueryBuilder,indices,1000);

//        LocalDate lcStart = LocalDate.parse(start, TimeUtils.format);
//        LocalDate lcEnd = LocalDate.parse(end, TimeUtils.format);
//
//        Map<String, Long> mapData = new HashMap<>();
//        while (!lcStart.isAfter(lcEnd)) {
//            String time = lcStart.format(TimeUtils.format);
//            int nTime = Integer.parseInt(time);
//            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
//            Map<String, Long> mapCount = ElasticUtils.countKeyWord(client, boolQueryBuilder, nTime);
//            for (Map.Entry<String, Long> entry : mapCount.entrySet()) {
//                if (mapData.containsKey(entry.getKey())) {
//                    Long valueItemMapData = mapData.get(entry.getKey());
//                    Long result = valueItemMapData + entry.getValue();
//                    mapData.put(entry.getKey(), result);
//                } else {
//                    mapData.put(entry.getKey(), entry.getValue());
//                }
//            }
//            lcStart = lcStart.plusDays(1);
//        }
//        List<ChartInfoDTO> chartInfoDTOS = new ArrayList<>();
//
//        LinkedHashMap<String, Long> finalMap = mapData.entrySet()
//                .stream()
//                .sorted((Map.Entry.<String, Long>comparingByValue().reversed()))
//                .limit(20)
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//        for (Map.Entry<String, Long> test : finalMap.entrySet()) {
//            ChartInfoDTO chartInfoDTO = new ChartInfoDTO(test.getKey(), test.getValue().intValue());
//            chartInfoDTOS.add(chartInfoDTO);
//
//        }
//        System.out.println("hihi");

//
        String start = "20191205";
        String end = "20191024";
        String url = "https://www.facebook.com/khongsocho.official/posts/2660049574279387";
        BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);

        List<Comment> listCommentFromPost = ElasticUtils.getListCommentFromPost(client, boolQueryBuilder, Integer.parseInt(start), url);
                List<Post> listPost = ElasticUtils.getListPost(client, boolQueryBuilder, Integer.parseInt(start));
        System.out.println("HIHI");

//        String reaction = "{\"summary\":{\"like\":\"356\",\"comment\":\"356\",\"share\":\"356\"},\"id\":\"1756687557948931_2657350684549276\"}";
//        JSONObject json = new JSONObject(reaction);
//        JSONObject summaryJson = json.getJSONObject("summary");
//        String like = summaryJson.getString("like");
//        String comment = summaryJson.getString("comment");
//        String share = summaryJson.getString("share");
    }

}
