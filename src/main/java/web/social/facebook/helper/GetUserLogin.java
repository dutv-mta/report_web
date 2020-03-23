package web.social.facebook.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import web.social.facebook.service.UserService;

import java.io.PrintWriter;
import java.security.Principal;

@Component
public class GetUserLogin {
    @Autowired
    private UserService userService;

    public Integer getUserId() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal != null) {
            return userService.findByEmail(principal.getName()).getId();
        }
        return null;
    }

    public String getUserName() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal != null) {
            return userService.findByEmail(principal.getName()).getName();
        }
        return null;
    }
}
