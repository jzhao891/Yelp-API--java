package yelpAPI;

public class CSVobject {
	public String id;
	public String categories;
	public String rating;
	public String reviewC;
	public CSVobject(String id,String cate,String rating,String review){
		this.id=id;
		categories=cate;
		this.rating=rating;
		reviewC=review;
	}
	public String getString() {
		return "business [id=" + id + ", categories=" + categories
				
                + ", rating=" + rating + ", review_count=" + reviewC +"]";
	}

}
