package web.social.facebook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.social.facebook.entities.Topic;
import web.social.facebook.entities.UserTopic;
import web.social.facebook.exception.NameExistsException;
import web.social.facebook.helper.GetUserLogin;
import web.social.facebook.repository.TopicRepository;
import web.social.facebook.service.TopicService;
import web.social.facebook.service.UserTopicService;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TopicServiceImpl implements TopicService {
    private GetUserLogin getUserLogin;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private UserTopicService userTopicService;

    public TopicServiceImpl(GetUserLogin getUserLogin) {
        this.getUserLogin = getUserLogin;
    }

    @Override
    public String findMustWord(int topicId) {
        return null;
    }

    @Override
    public String findStopWord(int topicId) {
        return null;
    }

    @Override
    public Topic findTopicById(int id) throws SQLException{
        return topicRepository.findById(id).get();
    }

    @Override
    public List<Topic> getListTopicByUser(int userId) throws SQLException {
        return topicRepository.getListTopicByUser(userId);
    }

    @Override
    @Transactional(rollbackOn = SQLException.class)
    public void addNewTopic(Topic topic) throws SQLException {

        topic.setCreateOn(new Date());
        topic.setFlag(1);
        topic.setMustWord(topic.getMustWord());
        topic.setStopWord(topic.getStopWord());


        // Save topic
        Integer topicId = topicRepository.save(topic).getId();

        UserTopic userTopic = new UserTopic();
        userTopic.setUserId(getUserLogin.getUserId());
        userTopic.setTopicId(topicId);
        // Save user topic
        userTopicService.addNewUserTopic(userTopic);
    }

    @Override
    public void updateTopic(int id, Topic topic) throws SQLException {
        Topic topicFind = topicRepository.findById(id).get();
        if (Objects.isNull(topicFind)) {
            throw new SQLException("");
        }
        Topic topicFindName = topicRepository.findTopicByName(topic.getName());
        topicFind.setCreateOn(new Date());
        topicFind.setName(topic.getName());
        topicFind.setMustWord(topic.getMustWord());
        topicFind.setStopWord(topic.getStopWord());

        topicRepository.save(topicFind);
    }

    @Override
    public void deleteTopic(int id) throws SQLException {
        this.topicRepository.deleteTopic(id);
    }
}
