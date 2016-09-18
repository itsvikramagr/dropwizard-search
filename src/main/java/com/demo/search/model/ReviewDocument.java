package com.demo.search.model;

public class ReviewDocument implements Comparable<ReviewDocument>{

	/*product/productId: B000LQOCH0
review/userId: ABXLMWJIXXAIN
review/profileName: Natalia Corres "Natalia Corres"
review/helpfulness: 1/1
review/score: 4.0
review/time: 1219017600
review/summary: "Delight" says it all
review/text: This is a confection that has been around a few centuries.  It is a light, pillowy citrus gelatin with nuts - in this case Filberts. And it is cut into tiny squares and then liberally coated with powdered sugar.  And it is a tiny mouthful of heaven.  Not too chewy, and very flavorful.  I highly recommend this yummy treat.  If you are familiar with the story of C.S. Lewis' "The Lion, The Witch, and The Wardrobe" 
- this is the treat that seduces Edmund into selling out his Brother and Sisters to the Witch.
	 */
	Integer docId;
	String productId;
	String userId;
	String profileName;
	String helpfulness;
	String score;
	String time;
	String summary;
	String text;
	
	public ReviewDocument(int docId, String productId, String userId, String profileName, String helpfulness, String score, String time, String summary,
			String text) {
		super();
		this.docId = docId;
		this.productId = productId;
		this.userId = userId;
		this.profileName = profileName;
		this.helpfulness = helpfulness;
		this.score = score;
		this.time = time;
		this.summary = summary;
		this.text = text;
	}
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getSummary() {
		return summary;
	}

	public String getText() {
		return text;
	}

	public Integer getDocId() {
		return docId;
	}

	@Override
	public int compareTo(ReviewDocument o) {
		// sort in descending order
		return (int)(Float.valueOf(o.getScore()) - Float.valueOf(score));
	}

	public String toString(){
		String s = new String();
		s=s.concat("Summary: ");
		s=s.concat(summary);
		s=s.concat("|");
		//s=s.concat("Text: ");
		//s=s.concat(text);
		//s=s.concat("|");
		return s;
	}

}
