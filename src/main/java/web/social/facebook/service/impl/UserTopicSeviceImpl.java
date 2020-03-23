package web.social.facebook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.social.facebook.entities.UserTopic;
import web.social.facebook.repository.UserTopicRepository;
import web.social.facebook.service.UserTopicService;

@Service
public class UserTopicSeviceImpl implements UserTopicService {
    @Autowired
    private UserTopicRepository userTopicRepository;

    @Override
    public void addNewUserTopic(UserTopic userTopic) {
        userTopicRepository.save(userTopic);
    }
}
