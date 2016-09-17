package com.demo.search;

import io.dropwizard.setup.Environment;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;

public class SearchApplication  extends Application<AppConfiguration> {
    public static final String APPLICATION_NAME = "review-search";

    public static void main(String[] args) throws Exception {
      new SearchApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
      bootstrap.addBundle(new AssetsBundle("/assets", "/ui", "index.html"));
    }

    private void setUp(AppConfiguration configuration,
        Environment environment) throws ClassNotFoundException {          
    }
    
    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception{
      setUp(configuration, environment);
    }

}
