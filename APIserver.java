package yelpAPI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.Scanner;

public class APIserver {
	
	 public static class YelpAPI {

	   private static final String API_HOST = "api.yelp.com";
	   private static final String DEFAULT_TERM = "dinner";
	   private static final String DEFAULT_LOCATION = "Pittsburgh";
	   private static final int SEARCH_LIMIT =20;
	   private static final String SEARCH_PATH = "/v2/search";
	   private static final String BUSINESS_PATH = "/v2/business";
	   private static final String RADIUS_FILTER="3500";
	   private static final String CATE_FILTER="American";//category_filter
	  // private static final String CLL="40.44,-80.02";
	   private static final String[] CLL={"40.50,-80.01","40.46,-79.92","40.43,-79.98","40.43,-79.92",
					"40.44,-80.02","40.41,-80.00","40.38,-79.92"};

	   /*
	    * Update OAuth credentials below from the Yelp Developers API site:
	    * http://www.yelp.com/developers/getting_started/api_access
	    */
	   private static final String CONSUMER_KEY = "Tx0iTVSrshPOudRbCCOH_w";
	   private static final String CONSUMER_SECRET = "pR30w99W7urBZywsVxwe_hjENGE";
	   private static final String TOKEN = "Hu8sLW9qkyiGx9AziIMBz0pC3Cx8cFoc";
	   private static final String TOKEN_SECRET = "7H9lOIOyZut19ZZzoG-nwzDOp9w";

	   OAuthService service;
	   Token accessToken;

	   /**
	    * Setup the Yelp API OAuth credentials.
	    * 
	    * @param consumerKey Consumer key
	    * @param consumerSecret Consumer secret
	    * @param token Token
	    * @param tokenSecret Token secret
	    */
	   public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
	     this.service =
	        new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
	            .apiSecret(consumerSecret).build();
	     this.accessToken = new Token(token, tokenSecret);
	   }

	   /**
	    * Creates and sends a request to the Search API by term and location.
	    * <p>
	    * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
	    * for more info.
	    * 
	    * @param term <tt>String</tt> of the search term to be queried
	    * @param location <tt>String</tt> of the location
	    * @param ll <tt>String</tt> of the search coordination to be queried
	    * @param radius_filter <tt>String</tt> of the radius_filter
	    * @param category_filter <tt>String</tt> of the category_filter
	    * @return <tt>String</tt> JSON Response
	    */
	   public String searchForBusinessesByLocation(String category,String term,String location) {
	     OAuthRequest request = createOAuthRequest(SEARCH_PATH);
	     request.addQuerystringParameter("term", term);
	     //request.addQuerystringParameter("location", DEFAULT_LOCATION);
	     request.addQuerystringParameter("ll", location);
	     request.addQuerystringParameter("radius_filter", RADIUS_FILTER);
	     request.addQuerystringParameter("category_filter", category);
	     request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
	     return sendRequestAndGetResponse(request);
	   }

	   /**
	    * Creates and sends a request to the Business API by business ID.
	    * <p>
	    * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
	    * for more info.
	    * 
	    * @param businessID <tt>String</tt> business ID of the requested business
	    * @return <tt>String</tt> JSON Response
	    */
	   public String searchByBusinessId(String businessID) {
	     OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
	     return sendRequestAndGetResponse(request);
	   }

	   /**
	    * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
	    * 
	    * @param path API endpoint to be queried
	    * @return <tt>OAuthRequest</tt>
	    */
	   private OAuthRequest createOAuthRequest(String path) {
	     OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
	     return request;
	   }

	   /**
	    * Sends an {@link OAuthRequest} and returns the {@link Response} body.
	    * 
	    * @param request {@link OAuthRequest} corresponding to the API request
	    * @return <tt>String</tt> body of API response
	    */
	   private String sendRequestAndGetResponse(OAuthRequest request) {
	     System.out.println("Querying " + request.getCompleteUrl() + " ...");
	     this.service.signRequest(this.accessToken, request);
	     Response response = request.send();
	     return response.getBody();
	   }

	   /**
	    * Queries the Search API based on the command line arguments and takes the first result to query
	    * the Business API.
	    * 
	    * @param yelpApi <tt>YelpAPI</tt> service instance
	    * @param yelpApiCli <tt>YelpAPICLI</tt> command line arguments
	    */
	   private static void queryAPI(YelpAPI yelpApi, YelpAPICLI yelpApiCli) {

		 WriteCSV csv=new WriteCSV();
		for(int k=0;k<CLL.length;k++){
		   
			 	String searchResponseJSON =
			         yelpApi.searchForBusinessesByLocation(yelpApiCli.cate,yelpApiCli.term,yelpApiCli.cll[k]);

			     JSONParser parser = new JSONParser();
			     JSONObject response = null;
			     try {
			       response = (JSONObject) parser.parse(searchResponseJSON);
			     } catch (ParseException pe) {
			       System.out.println("Error: could not parse JSON response:");
			       System.out.println(searchResponseJSON);
			       System.exit(1);
			     }

			     JSONArray businesses = (JSONArray) response.get("businesses");
			     if(businesses!=null){
				     JSONObject[] firstBusiness = new JSONObject[businesses.size()];
				     for(int i=0;i<businesses.size();i++){
				    	 firstBusiness[i]=(JSONObject)businesses.get(i);
				    	 CSVobject biz=new CSVobject(firstBusiness[i].get("id").toString(),firstBusiness[i].get("categories").toString(),firstBusiness[i].get("rating").toString(),firstBusiness[i].get("review_count").toString());
				    	 
				    	 csv.writeCSV(biz);
				    	 System.out.println(firstBusiness[i]);
				     }
			     }
			     
		}
		csv.close();
	    
	   }

	   /**
	    * Command-line interface for the sample Yelp API runner.
	    */
	   private static class YelpAPICLI {
	     @Parameter(names = {"-q", "--term"}, description = "Search Query Term")
	     public String term = DEFAULT_TERM;

	     @Parameter(names = {"-l", "--location"}, description = "Location to be Queried")
	     public String location = DEFAULT_LOCATION;
	     
	     @Parameter(names = {"-cl", "--CLL"}, description = "coordinate to be Queried")
	     public String[] cll = CLL;
	     
	     @Parameter(names={"-c","--category_filter"}, description="category_filter to be Queried")
	     public String cate=CATE_FILTER;
	     
	     @Parameter(names = {"-r", "--radius_filter"}, description = "radius to be Queried")
	     public String radius = RADIUS_FILTER;
	   }

	   /**
	    * Main entry for sample Yelp API requests.
	    * <p>
	    * After entering your OAuth credentials, execute <tt><b>run.sh</b></tt> to run this example.
	    */
	   public static void main(String[] args) {
		 Scanner sc=new Scanner(System.in);
		 String token=sc.next();
		 String cate=sc.next();
	     YelpAPICLI yelpApiCli = new YelpAPICLI();
	    String[] s={"-q",token,"-c",cate};
	     new JCommander(yelpApiCli,s);
	     sc.close();

	     YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
	     queryAPI(yelpApi, yelpApiCli);
	   }
}
}
