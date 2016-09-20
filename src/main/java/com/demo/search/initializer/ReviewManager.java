package com.demo.search.initializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.*;
import org.apache.commons.io.IOUtils;
import com.demo.search.model.ReviewDocument;
import com.demo.search.resource.SearchResource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewManager {
  private static final Logger LOG = LoggerFactory.getLogger(ReviewManager.class);
  public static final List<String> ignoreList = Arrays.asList("in", "the", "of", "that", "how", "where", "a", "an", "where", "you", "me", "i", "we");

  public ReviewManager() {
    super();
  }

  public void splitFile(String fileName, Map<Integer, ReviewDocument> reviewDocs) {
    String everything="";
    FileInputStream inputStream= null;
    try {
      inputStream = new FileInputStream(fileName);
      everything = IOUtils.toString(inputStream);
    } catch (Exception e){
      LOG.error("Error: " + e.getMessage());
    } finally {
      //inputStream.close();
    }
    Pattern p = Pattern.compile("\\n[\\n]+");     /*if your text file has \r\n as the newline character then use Pattern p = Pattern.compile("\\r\\n[\\r\\n]+");*/
    String[] result = p.split(everything);
    LOG.info("Number of reviews in dataset {}", result.length);
    if (result.length > 100000) {
      while (reviewDocs.size()< 100000) {
        int rnum=(int)(Math.random() * (result.length));
        String doc = result[rnum];
        String row[] = doc.split("\n");
        // We can add error handling here
        try {
          ReviewDocument rd= new ReviewDocument(rnum,row[0].split(":")[1].trim(), 
              row[1].split(":")[1].trim(), 
              row[2].split(":")[1].trim(),
              row[3].split(":")[1].trim(), 
              row[4].split(":")[1].trim(), 
              row[5].split(":")[1].trim(), 
              row[6].split(":")[1].trim(),
              row[7].split(":")[1].trim());
          reviewDocs.put(rnum, rd);
        } catch (Exception e){
          LOG.error("Error: " + e.getMessage());
        }
      }
    }
    LOG.info("Number of reviews considered for search {}", reviewDocs.size());
  }

  public void processDoc(Map<Integer, ReviewDocument> reviewDocs, Map<String, Set<Integer>> tokenMap) {
    for (Map.Entry<Integer, ReviewDocument> entry : reviewDocs.entrySet()){
      Integer docId=entry.getKey();
      ReviewDocument rd = entry.getValue();
      // tokenize summary
      Set<String> tokens = new HashSet<String>();
      for (String t : rd.getSummary().split("\\s+")){
        t= t.replaceAll("[^A-Za-z ]", "");
        if (t.length() >2 && !ignoreList.contains(t.toLowerCase())) {
          tokens.add(t.toLowerCase());					
        }
      }
      for (String t : rd.getText().split("\\s+")){
        t= t.replaceAll("[^A-Za-z ]", "");
        if (t.length()>2 && !ignoreList.contains(t.toLowerCase())) {
          tokens.add(t.toLowerCase());					
        }
      }	
      for (String t : tokens){
        //TODO remove trailing and ending comma or double quotes
        if (!tokenMap.containsKey(t)) {
          Set<Integer> newtokenSet = new HashSet<Integer>();
          tokenMap.put(t, newtokenSet);
        }
        Set<Integer> tokenSet = tokenMap.get(t);
        tokenSet.add(docId);
      }
    }
  }
  
  
  public void generateQuerySet(Map<String, Set<Integer>> tokenMap, Integer numQuery) {
    Object[] tokens = tokenMap.keySet().toArray();
    Integer num = tokens.length;
    File file = new File("querySet.txt");

    // if file doesnt exists, then create it
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    FileWriter fw = null;
    BufferedWriter bw= null;
    try {
      fw = new FileWriter(file.getAbsoluteFile());
      bw = new BufferedWriter(fw);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    for (int j=0; j<numQuery; j++) {
      int numToken = 1+(int)(Math.random()*9);
      String s="";
      for (int i=1; i<=numToken; i++) {
        int rnum=(int)(Math.random() * (tokens.length-1));
        s=s.concat(String.valueOf(tokens[rnum]));
        if (i < numToken) {
          s=s.concat(",");
        }
      }
      try {
        bw.write(s);
        bw.write("\n");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    try {
      bw.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public ArrayList<Integer> reviewSearch(String[] tokenList, Map<Integer, ReviewDocument> reviewDocs, Map<String, Set<Integer>> tokenMap) {
    Map<Integer, Set<Integer>> scoreMap = searchToken(tokenList, tokenMap);
    int size = 0;
    int max = 20;
    int remaining = max-size;
    ArrayList<Integer> result = new ArrayList<Integer>();
    result.add(-1);
    for (int i=tokenList.length; i>0; i--) {
      if (scoreMap.containsKey(i)){
        Set<Integer> docMap = scoreMap.get(i);
        LOG.debug(i + " match= {} ", docMap.toString());
        if (docMap.size() > 0 ) {
          if (docMap.size() < remaining ) {
            result.addAll(docMap);
            size = result.size()-1; // we have added -1 
            remaining = max-size;
          } 
          else {
            ArrayList<Integer> sortedDoc = getSortedDoc(reviewDocs, docMap, remaining);
            result.addAll(sortedDoc);
            break;
          }
        }
      }
    }
    result.remove(0);  // remove the first element added by us
    LOG.debug("Final result = {}" , result.toString());	
    return result;
  }
  
  public Map<Integer, Set<Integer>> searchToken(String[] tokens, Map<String, Set<Integer>> tokenMap ) {
    Map<Integer, Set<Integer>> scoreMap = new HashMap<Integer, Set<Integer>>();
    Integer maxScore = 0;
    scoreMap.put(maxScore,new HashSet<Integer>());
    for (int i=0; i< tokens.length; i++) {
      if (tokenMap.containsKey(tokens[i].trim().toLowerCase())){
        Set<Integer> tokenSet = tokenMap.get(tokens[i].trim().toLowerCase());  // B
        Set<Integer> ts = new HashSet<Integer>();
        ts.add(-1);
        if (tokenSet.size() > 0 ) {
          ts.addAll(tokenSet);
        }
        ts.remove(-1);
        LOG.debug("Checking for token: {}", tokens[i].trim());
        for (int j = maxScore; j>=0; j--) {
          Set<Integer> docMap = scoreMap.get(j);
          Set<Integer> matched = new HashSet<Integer>(); // A
          matched.add(-1);
          if (docMap.size() > 0) {
            matched.addAll(docMap); 
          }
          matched.remove(-1);

          // get intersection of matched with new tokenSet
          if (matched.size() > 0) {
            matched.retainAll(ts);  // A intersection B	
          } 
          if (docMap.size() > 0 && matched.size() > 0) {
            docMap.removeAll(matched);  // (A - (A intersection B)).
          }				
          if (ts.size() > 0 && matched.size() > 0) {
            ts.removeAll(matched); // (B - A intersection B)
          }
          
          if (!scoreMap.containsKey(j+1)) {
            Set<Integer> matchedDocId = new HashSet<Integer>(); 
            scoreMap.put(j+1, matchedDocId);
            maxScore++;
          }
          Set<Integer> matchedDocId = scoreMap.get(j+1);
          if (matched.size() > 0) {
            if (matchedDocId.size() > 0) {
              matchedDocId.addAll(matched);
            }
            else {
              matchedDocId.add(-1);
              matchedDocId.addAll(matched);
              matchedDocId.remove(-1);
            }
          }
          scoreMap.put(j+1, matchedDocId);
        }
        Set<Integer> matchedDocId = scoreMap.get(1);
        if (ts.size() > 0 ) {
          if (matchedDocId.size() > 0) {
            matchedDocId.addAll(ts);
          }
          else {
            matchedDocId = ts;
          }
        }
        scoreMap.put(1, matchedDocId);
      }
    }
    return scoreMap;
  }

  public ArrayList<Integer> getSortedDoc(Map<Integer, ReviewDocument> reviewDocs, Set<Integer> docIds, Integer numDoc) {
    ArrayList<ReviewDocument> list = new ArrayList<ReviewDocument>();
    for (Integer docId : docIds){
      list.add(reviewDocs.get(docId));
      //System.out.println(reviewDocs.get(docId).getDocId() + "  " + reviewDocs.get(docId).getScore());
    }
    Collections.sort(list);		
    Iterator<ReviewDocument> itr = list.iterator();
    ArrayList<Integer>arr = new ArrayList<Integer>();
    int count =0;
    while(itr.hasNext() && count < numDoc) {
      arr.add(itr.next().getDocId());
      count++;
    }
    return arr;
  }

}
