package com.dionext.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.DatabaseStartupValidator;

import javax.sql.DataSource;
import java.util.stream.Stream;

@Configuration
public class DatabaseConfiguration {
    //https://stackoverflow.com/questions/66993064/wait-for-database-connection-in-spring-boot-without-an-exception
    //https://deinum.biz/2020-06-30-Wait-for-database-startup/

    @Bean
    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
        return bf -> {
            String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
            Stream.of(jpa)
                    .map(bf::getBeanDefinition)
                    .forEach(it -> it.setDependsOn("databaseStartupValidator"));
        };
    }

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
        var dsv = new DatabaseStartupValidator();
        dsv.setDataSource(dataSource);
        dsv.setTimeout(60);//Set the timeout (in seconds) after which a fatal exception will be thrown. Default is 60.
        dsv.setInterval(3);//Set the interval between validation runs (in seconds). Default is 1.
        // setValidationQuery is deprecated
        return dsv;
    }

}
