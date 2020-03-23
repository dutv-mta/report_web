package web.social.facebook.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.social.facebook.entities.Topic;
import web.social.facebook.service.TopicService;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetListTopic {
    @Autowired
    private TopicService topicService;
    private GetUserLogin getUserLogin;

    public GetListTopic(GetUserLogin getUserLogin) {
        this.getUserLogin = getUserLogin;
    }

    public List<Topic> getListTopicByEmail() {
        List<Topic> topicList;
        try {
            topicList = this.topicService.getListTopicByUser(getUserLogin.getUserId());
        } catch (Exception e) {
            topicList = new ArrayList<>();
        }
        return topicList;
    }
}
