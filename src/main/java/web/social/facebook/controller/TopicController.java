package web.social.facebook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import web.social.facebook.entities.Topic;
import web.social.facebook.helper.GetListTopic;
import web.social.facebook.helper.GetUserLogin;
import web.social.facebook.service.TopicService;

import javax.validation.Valid;
import java.sql.SQLException;

@Controller
@RequestMapping(value = "/topic")
public class TopicController {
    private final static Logger logger = LoggerFactory.getLogger(TopicController.class);
    private final GetUserLogin getUserLogin;
    private final TopicService topicService;
    private GetListTopic getListTopic;

    public TopicController(GetUserLogin getUserLogin, TopicService topicService, GetListTopic getListTopic) {
        this.topicService = topicService;
        this.getUserLogin = getUserLogin;
        this.getListTopic = getListTopic;
    }

    @GetMapping(value = "")
    public String viewCreateTopic(Model model) {
        model.addAttribute("username", getUserLogin.getUserName());
        model.addAttribute("topicList", getListTopic.getListTopicByEmail());

        return "topic/index";
    }

    @GetMapping(value = "/create")
    public String getViewCreate(Topic topic, Model model) {
        model.addAttribute("username", getUserLogin.getUserName());
        model.addAttribute("topicList", getListTopic.getListTopicByEmail());

        return "topic/create";
    }

    @PostMapping(value = "/insert")
    public String createTopic(@Valid Topic topic, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "topic/create";
        }
        try {
            // Todo: giới hạn số topic được tạo 'topicService.getListTopicByUser(getUserLogin.getUserId()).size();'
            topicService.addNewTopic(topic);
        }catch (SQLException e) {
            logger.error("Create topic fail. SQLException: ", e);
        } catch (Exception e) {
            logger.error("Create topic fail. Exception: ", e);
        }

        return "redirect:/topic";
    }

    @GetMapping(value = "/edit/{id}")
    public String editTopic(@PathVariable("id") int id, Model model) {
        Topic topic;
        try {
            topic = topicService.findTopicById(id);
        } catch (Exception e) {
            topic = new Topic();
            model.addAttribute("message", "Invalid topic Id:" + id);
        }
        model.addAttribute("topic", topic);
        model.addAttribute("username", getUserLogin.getUserName());
        model.addAttribute("topicList", getListTopic.getListTopicByEmail());

        return "topic/edit";
    }

    @PostMapping(value = "/update/{id}")
    public String updateTopic(@PathVariable("id") int id, @Valid Topic topic, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "topic/create";
        }
        String message;
        try {
            topicService.updateTopic(id, topic);
            message = "Update success";
        }catch (Exception e) {
            logger.error("Update topic fail. Exception: ", e);
            message = "Update fail!";
        }
        model.addAttribute("message", message);
        return "redirect:/topic/edit/" + id + "";
    }

    @GetMapping(value = "/delete/{id}")
    public String deleteTopic(@PathVariable("id") int id, Model model) {
        String message;
        try {
            topicService.deleteTopic(id);
            message = "Delete topic " + id + " success.";
        } catch (Exception e) {
            logger.error("Delete topic fail. Exception: ", e);
            message = "Delete topic " + id + " fail!";
        }
        model.addAttribute("message", message);

        return "redirect:/topic";
    }
}
