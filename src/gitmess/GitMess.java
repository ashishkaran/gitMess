package gitmess;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class GitMess {

	   public static void main(String[] args) throws Exception {

		    String orgName = "orgName";
	        String repoName = "repoName";
	        String username = "username";
	        String password = "password";
	        String commitsSince = "2018-04-01";
	        String perPage = "100";

	        Map<String, String> headers = new HashMap<String, String>();
	        headers.put("Content-Type", "application/json");
	        headers.put("Accept", "application/json");
	        boolean morePages = true;
	        int pageNumber = 1;
	        while (morePages) {
//	            System.out.println("Fetching page: " + pageNumber);
	            String url = "https://api.github.com/repos/" + orgName + "/" + repoName + "/commits?since" 
	            + commitsSince + "=&page=" + pageNumber + "&per_page=" + perPage;
//	            System.out.println(url);
	            pageNumber++;
	            HttpResponse<String> response = Unirest.get(url)
	                    .basicAuth(username, password)
	                    .headers(headers).asString();
	            
	            JsonParser p = new JsonParser();
	            JsonArray o = p.parse(response.getBody()).getAsJsonArray();
	            
	            for (int i = 0; i < o.size(); i++) {
	            	JsonObject author = o.get(i).getAsJsonObject().getAsJsonObject("commit").getAsJsonObject("author");
	            	String name = author.getAsJsonPrimitive("name").getAsString();
	            	String date = author.getAsJsonPrimitive("date").getAsString();
	            	System.out.println(name + "," + date);
	            }
	            if (o.size() < 100) {
	                morePages = false;
	            }
	            Thread.sleep(1000);
	        }
	    }
}
