package web.social.facebook.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import web.social.facebook.entities.Role;
import web.social.facebook.entities.User;
import web.social.facebook.repository.RoleRepository;
import web.social.facebook.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final static Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username);
        org.springframework.security.core.userdetails.User.UserBuilder userBuilder = null;
        if (user == null) {
            logger.error("User not found!" + username);

            throw new UsernameNotFoundException("User " + username + " was not found in the database.");
        }
        List<Role> roleList = this.roleRepository.findRoleByUserId(username);
        List<String> authorities = new ArrayList<>();
        for (Role role : roleList) {
            authorities.add(role.getRoleName());
        }
        userBuilder = withUsername(username);
        userBuilder.disabled(!user.getStatus());
        userBuilder.password(user.getPassword());
        userBuilder.authorities(authorities.toArray(new String[0]));

        logger.info("Login with user: " + username);
        return userBuilder.build();
    }
}
