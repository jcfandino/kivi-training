package io.github.jcfandino.spring;

import io.dropwizard.setup.Environment;
import io.github.jcfandino.KiviConf;

public class Dropwizard {

    private KiviConf configuration;
    private Environment environment;

    public KiviConf getConfiguration() {
        return configuration;
    }

    public void setConfiguration(KiviConf aConfiguration) {
        configuration = aConfiguration;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment aEnvironment) {
        environment = aEnvironment;
    }
}
