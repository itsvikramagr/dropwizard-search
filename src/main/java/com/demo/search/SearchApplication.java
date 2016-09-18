package com.demo.search;

import io.dropwizard.setup.Environment;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.demo.search.AppConfiguration;
import com.demo.search.initializer.ReviewManager;
import com.demo.search.model.ReviewDocument;
import com.demo.search.resource.SearchResource;

public class SearchApplication extends Application<AppConfiguration> {
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
		ReviewManager rm= new ReviewManager();
		Map<Integer, ReviewDocument> reviewDocs = new HashMap<Integer, ReviewDocument>();
		Map<String, Set<Integer>> tokenMap = new HashMap<String, Set<Integer>>();
		rm.splitFile("finefoods_small3.txt", reviewDocs);
		rm.processDoc(reviewDocs, tokenMap);		
	    environment.jersey().register(new SearchResource(rm, reviewDocs, tokenMap));
	    
}

	@Override
	public void run(AppConfiguration configuration, Environment environment) throws Exception{
		setUp(configuration, environment);
	}

}
