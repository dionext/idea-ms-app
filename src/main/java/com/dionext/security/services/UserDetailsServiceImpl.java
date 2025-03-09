package com.dionext.security.services;

import com.dionext.security.entity.User;
import com.dionext.security.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${d.user.password:unknown}")
    String userPassword;
    @Value("${d.admin.password:unknown}")
    String adminPassword;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    void postConstruct() {
        //List<User> users = userRepository.findAll();

        User user = userRepository.findByUsername("user").orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername("user");
            user.setPassword(new BCryptPasswordEncoder().encode(userPassword));
            //user.setPassword("{noop}user");
            user.setRoles(User.ROLE_USER);
            userRepository.save(user);
        }

        user = userRepository.findByUsername("admin").orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername("admin");
            user.setPassword(bCryptPasswordEncoder.encode(adminPassword));
            user.setRoles(User.ROLE_ADMIN);
            userRepository.save(user);
        }

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> personOptional = userRepository.findByUsername(username);
        if (personOptional.isEmpty()) {
            throw new UsernameNotFoundException("Username %s does not exist".formatted(username));
        }
        User person = personOptional.get();
        return new org.springframework.security.core.userdetails.User(person.getUsername(),
                person.getPassword(), getAuthorities(person));
    }

    //private Collection<? extends GrantedAuthority> getAuthorities(UserDto person) {
    //todo
    //  return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + person.getRoles()));
    //}

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return User.getRolesList(user).stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList());
        // Split the authorities string and convert to a list of SimpleGrantedAuthority objects
        //return Arrays.stream(roles.split(","))
          //      .map(SimpleGrantedAuthority::new)
            //    .collect(Collectors.toList());
    }


    public void registerUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User.setRolesList(user, Collections.singleton(User.ROLE_USER)); // По умолчанию роль USER
        userRepository.save(user);
    }

}

