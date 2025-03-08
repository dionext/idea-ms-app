package com.dionext.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private String roles;

    public User() {
    }

    public User(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRoles() {
        return roles;
    }

    static public Collection<String> getRolesList(User user){
        if (user.getRoles() == null) return new ArrayList<String>();
        else return Stream.of(user.getRoles().split(",", -1))
                .collect(Collectors.toList());
    }
    static public void setRolesList(User user, Collection<String> roles){
        if (roles.size() > 0) user.setRoles(String.join(",", roles));
        else user.setRoles(null);
    }

}
