package com.dionext.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
public class IdeaportalConfiguration {
    /**
     * for legacy pattern like "app//..."
     * https://stackoverflow.com/questions/70606716/getting-404-error-when-url-contains-double-slash-after-spring-boot-2-5-to-2-6-mi
     * @return
     */
    @Bean
    HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }
}
