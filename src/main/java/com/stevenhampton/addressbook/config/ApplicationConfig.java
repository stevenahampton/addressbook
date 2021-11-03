package com.stevenhampton.addressbook.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the application.
 */
@Configuration
public class ApplicationConfig {
    /**
     * {@link ModelMapper} bean for use throughout application.
     * @return {@link ModelMapper}
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
