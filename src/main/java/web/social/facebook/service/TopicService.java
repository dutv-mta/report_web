package web.social.facebook.service;

import web.social.facebook.entities.Topic;

import java.sql.SQLException;
import java.util.List;

public interface TopicService {
    String findMustWord(int topicId);

    String findStopWord(int topicId);

    Topic findTopicById(int id) throws SQLException;

    List<Topic> getListTopicByUser(int userId)throws SQLException;

    void addNewTopic(Topic topic) throws SQLException;

    void updateTopic(int id, Topic topic) throws SQLException;

    void deleteTopic(int id) throws SQLException;
}
