package com.tampan.common.config;


import com.codahale.metrics.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer timer() {
        return new Timer();
    }
}
