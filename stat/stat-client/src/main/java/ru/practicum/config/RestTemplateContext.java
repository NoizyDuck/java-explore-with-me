package ru.practicum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateContext {
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate;
    }
}
