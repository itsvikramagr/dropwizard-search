package com.demo.search.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.search.initializer.ReviewManager;
import com.demo.search.model.ReviewDocument;

@Path("v1")
public class SearchResource {
	private Map<Integer, ReviewDocument> reviewDocs;
	private Map<String, Set<Integer>> tokenMap;
        //private Map<String, Integer> tokenCount;
	private static final Logger LOG = LoggerFactory.getLogger(SearchResource.class);
	private ReviewManager rm;

	public SearchResource(ReviewManager rm, Map<Integer, ReviewDocument> reviewDocs, Map<String, Set<Integer>> tokenMap) {
		this.rm = rm;
		this.reviewDocs = reviewDocs;
		this.tokenMap = tokenMap;
	}

	@GET
	@Path("search")
	@Produces("application/json")
	public Map search(@QueryParam("token") String token) {
		String[] tokenList = token.split(",");	
		ArrayList<Integer> result = rm.reviewSearch(tokenList, reviewDocs, tokenMap);		
	    Map<Integer, Object> status = new HashMap<Integer, Object>();
	    Iterator<Integer> itr = result.iterator();
	    while(itr.hasNext()) {
	    	Integer docId = itr.next();
	    	ReviewDocument rd = reviewDocs.get(docId);
	    	status.put(docId, rd.toString());
	    }
	    return status;
	}
	
	@GET
	@Path("querySet")
	@Produces("application/json")
	public Map search() {
		rm.generateQuerySet(tokenMap, 100000);
	    Map<String, Object> status = new HashMap<String, Object>();
	    status.put("Status", "done");
	    return status;
	}
	
}
