package io.github.jcfandino.spring;

import org.springframework.context.annotation.Bean;

public class ParentDropwizardConfig {
    @Bean
    public Dropwizard dropwizard() {
        return new Dropwizard();
    }
}
