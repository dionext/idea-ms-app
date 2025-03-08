package com.dionext.security.services;

import com.dionext.security.entity.User;
import com.dionext.security.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostConstruct
    void postConstruct() {
        List<User> users = userRepository.findAll();
        if (users.size() == 0) {

            User user = new User();
            user.setUsername("user");
            user.setPassword("{noop}user");
            user.setRoles("USER");
            userRepository.save(user);

            user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setRoles("ADMIN");
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
        return new org.springframework.security.core.userdetails.User(person.getUsername(), person.getPassword(), getAuthorities(person.getRoles()));
    }

    //private Collection<? extends GrantedAuthority> getAuthorities(UserDto person) {
    //todo
    //  return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + person.getRoles()));
    //}

    private Collection<? extends GrantedAuthority> getAuthorities(String roles) {
        // Split the authorities string and convert to a list of SimpleGrantedAuthority objects
        return Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    public void registerUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        //user.setPassword(passwordEncoder.encode(password));
        user.setPassword(password);
        User.setRolesList(user, Collections.singleton("ROLE_USER")); // По умолчанию роль USER
        userRepository.save(user);
    }

}

    /*
    public String create(String username, String password) {
        // Encodes the password and creates a new User object
        UserDto user = UserDto.builder()
                .username(username)
                .password(new BCryptPasswordEncoder().encode(password)) // Encrypts the password
                .authorities("student") // Assigns default authority
                .build();

        // Saves the new user to the database
        userRepository.save(user);

        return "Create Successfully !"; // Returns a success message
    }

     */
