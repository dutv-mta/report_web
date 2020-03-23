package web.social.facebook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import web.social.facebook.entities.User;
import web.social.facebook.entities.UserRole;
import web.social.facebook.repository.UserRepository;
import web.social.facebook.repository.UserRoleRepository;
import web.social.facebook.service.UserService;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void registerAccount(User user) throws SQLException {
        Date date = new Date();
        user.setStartDate(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 60);
        user.setEndDate(c.getTime());
        user.setStatus(true);
        user.setPassword((new BCryptPasswordEncoder()).encode(user.getPassword()));

        int id = userRepository.save(user).getId();

        UserRole userRole = new UserRole();
        userRole.setRoleId(2);
        userRole.setUserId(id);

        userRoleRepository.save(userRole);
    }
}
