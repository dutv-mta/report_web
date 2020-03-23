package web.social.facebook.api;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.social.facebook.entities.DTO.DetailDTO;
import web.social.facebook.entities.Mention;
import web.social.facebook.repository.TopicRepository;
import web.social.facebook.utils.ElasticUtils;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class DetailApi {
    private static final Logger logger = LoggerFactory.getLogger(DetailApi.class);

    private final Client client;
    private final TopicRepository topicRepository;

    public DetailApi(Client client, TopicRepository topicRepository) {
        this.client = client;
        this.topicRepository = topicRepository;
    }

    @GetMapping(value = {"/detail/{topicId}"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<DetailDTO>> getDetail(@PathVariable int topicId, @RequestParam(value = "date") String strDate) {
        int date = Integer.parseInt(strDate);
        String mustWord = topicRepository.findMustWord(topicId);
        String stopWord = topicRepository.findStopWord(topicId);
        try {
            BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeQuery(mustWord, stopWord);
            List<Mention> mentionList = ElasticUtils.getMentionInDay(client, boolQueryBuilder, topicId, 1000, date);
            List<DetailDTO> detailDTOList = DetailDTO.getAllPost(mentionList);

            logger.info(String.format("Get detail for topic id: %s on %s successfully",
                    String.valueOf(topicId),
                    String.valueOf(date)));

            return new ResponseEntity<>(detailDTOList, HttpStatus.OK);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Exception when get Detail topic information");
        }
    }

    @GetMapping(value = "/detail/{date}/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Mention> getOneDocument(@PathVariable(value = "date") String strDate, @PathVariable(value = "id") String id) {
        int date = Integer.parseInt(strDate);
        BoolQueryBuilder boolQueryBuilder = ElasticUtils.makeBoolQueryOneDocument(id);
        try {
            Mention mention = ElasticUtils.getOneDocument(client, boolQueryBuilder, date);
            logger.info(String.format("Get mention id: %s  successfully",
                    id));

            return new ResponseEntity<>(mention, HttpStatus.OK);
        } catch (JSONException e) {

            throw new IllegalArgumentException("Exception when get one document information with id: " + id, e);
        }
    }
}
