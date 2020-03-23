package web.social.facebook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import web.social.facebook.entities.Topic;
import web.social.facebook.helper.GetListTopic;
import web.social.facebook.helper.GetUserLogin;
import web.social.facebook.service.TopicService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/report")
public class ReportController {
    @Autowired
    private TopicService topicService;
    private GetListTopic getListTopic;
    private GetUserLogin getUserLogin;

    public ReportController(GetListTopic getListTopic, GetUserLogin getUserLogin) {
        this.getListTopic = getListTopic;
        this.getUserLogin = getUserLogin;
    }

    @GetMapping(value = "/overview/{id}")
    public String overviewReport(@PathVariable("id") int id, Model model) {
        model.addAttribute("username", getUserLogin.getUserName());
        model.addAttribute("topicList", getListTopic.getListTopicByEmail());
        model.addAttribute("topicId", id);

        return "index";
    }

    @GetMapping(value = "/detail/{id}")
    public String editReport(@PathVariable("id") int id, Model model) {
        model.addAttribute("username", getUserLogin.getUserName());
        model.addAttribute("topicList", getListTopic.getListTopicByEmail());
        model.addAttribute("topicId", id);
        return "detail";
    }

}
