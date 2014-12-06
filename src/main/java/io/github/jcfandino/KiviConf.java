package io.github.jcfandino;

import io.dropwizard.Configuration;

public class KiviConf extends Configuration {
    private String repo;
    private String theme;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String aRepo) {
        repo = aRepo;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String aTheme) {
        theme = aTheme;
    }



}
