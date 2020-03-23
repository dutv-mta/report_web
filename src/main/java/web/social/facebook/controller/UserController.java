package web.social.facebook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.social.facebook.entities.User;
import web.social.facebook.service.UserService;

import javax.validation.Valid;
import java.sql.SQLException;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @GetMapping(value = "/")
    public String viewUserInfo() {
        return "user/edit";
    }

    @PutMapping(value = "/update")
    public String updateUser() {
        return "redirect:/user";
    }

    @PostMapping(value = "/reset-password")
    public String resetPassword() {
        return "redirect:/user";
    }

    @GetMapping(value = "/register")
    public String registerAccount(User user,Model model) {
        return "user/register";
    }

    @PostMapping(value = "/register/insert")
    public String registerAccountInsert(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/register";
        }
        try {
            // save user
            userService.registerAccount(user);
        } catch (SQLException e) {
            logger.error("Register fail. SQLException: ", e);
        } catch (Exception e) {
            logger.error("Register fail. Exception: ", e);
        }

        return "redirect:/";
    }
}
