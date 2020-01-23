package com.github.cenkakin.mynewspaper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;

import javax.validation.Validator;

/**
 * Created by cenkakin
 */
@Configuration
@EnableMongoAuditing
public class MongoConfiguration {

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(Validator validator) {
        return new ValidatingMongoEventListener(validator);
    }
}
