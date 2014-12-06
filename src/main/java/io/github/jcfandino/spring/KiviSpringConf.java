package io.github.jcfandino.spring;

import io.dropwizard.setup.Environment;
import io.github.jcfandino.KiviConf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "io.github.jcfandino" })
public class KiviSpringConf {

    @Autowired
    Dropwizard dropwizard;

    @Bean
    public KiviConf configuration() {
        return dropwizard.getConfiguration();
    }

    @Bean
    public Environment environment() {
        return dropwizard.getEnvironment();
    }
}
