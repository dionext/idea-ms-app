package com.dionext.security.configuration;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.*;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfiguration {

    /*  simple InMemoryUser
    do not use password encoding
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        User.UserBuilder users = User.builder();
        UserDetails user = users
                .username("user")
                .password("{noop}user")
                .roles("USER")
                .build();
        UserDetails admin = users
                .username("admin")
                .password("{noop}admin")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    */

    /*
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    */




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                //configure Spring Security Java configuration to allow dispatcher types like FORWARD and ERROR
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/admin/**").authenticated()//.hasAuthority("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults())
                //.formLogin(withDefaults())
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .defaultSuccessUrl("/index.htm") // Redirect after successful login
                        .failureUrl("/login?error=true") // Redirect after failed login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Logout URL
                        .logoutSuccessUrl("/login?logout=true") // Redirect after logout
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        //return http.build();

        return http.build();

        /*
        //https://alexkosarev.name/2023/05/31/basic-authentication-in-spring-security/
        return http
                .httpBasic(httpBasic -> httpBasic
                        .securityContextRepository(
                                // Хранение контекста безопасности в HTTP-сессии
                                new HttpSessionSecurityContextRepository()))
                // Создание HTTP-сессии при необходимости
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .build();

         */
    }

}
