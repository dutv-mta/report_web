package web.social.facebook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web.social.facebook.entities.Topic;
import web.social.facebook.helper.GetListTopic;
import web.social.facebook.helper.GetUserLogin;
import web.social.facebook.service.TopicService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private final static Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final GetUserLogin getUserLogin;
    private final TopicService topicService;
    private final GetListTopic getListTopic;


    public HomeController(GetUserLogin getUserLogin, TopicService topicService, GetListTopic getListTopic) {
        this.getUserLogin = getUserLogin;
        this.topicService = topicService;
        this.getListTopic = getListTopic;
    }

    @GetMapping("/home")
    public String home( Model model) {
        model.addAttribute("username", getUserLogin.getUserName());
        model.addAttribute("topicList", getListTopic.getListTopicByEmail());

        return "home";
    }

    @GetMapping("/header")
    public String header(Model model) {
        List<Topic> topicList;
        try {
            topicList = this.topicService.getListTopicByUser(getUserLogin.getUserId());
        } catch (Exception e) {
            topicList = new ArrayList<>();
        }
        model.addAttribute("topicList", topicList);

        return "header";
    }

    @GetMapping(value = {"/", "/login"})
    public String login() {
        return "login";
    }

    @GetMapping("/403")
    public String forbidden() {
        return "403";
    }

    @GetMapping("/404")
    public String notFound() {
        return "404";
    }

    @GetMapping("/500")
    public String internalServer() {
        return "500";
    }
}
