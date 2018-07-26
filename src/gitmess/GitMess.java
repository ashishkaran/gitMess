package gitmess;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvWriter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class GitMess {

	public static void main(String[] args) throws Exception {

		String orgName = "orgName";
		String username = "username";
		String password = "password";
		String commitsSince = "2018-04-01";
		String perPage = "100";

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		boolean morePages = true;
		int pageNumber = 1;

		List<String> repos = new ArrayList<String>();
		JsonParser p = new JsonParser();

		String url = "https://api.github.com/orgs/" + orgName + "/repos";

		HttpResponse<String> reposResponse = Unirest.get(url).basicAuth(username, password).headers(headers).asString();

		JsonArray o = p.parse(reposResponse.getBody()).getAsJsonArray();
		for (int i = 0; i < o.size(); i++) {
			repos.add(o.get(i).getAsJsonObject().getAsJsonPrimitive("name").getAsString());
		}
		System.out.println(repos);

		String outputFile = "users.csv";
		boolean alreadyExists = new File(outputFile).exists();
		CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
		if (!alreadyExists) {
			csvOutput.write("id");
			csvOutput.write("email");
			csvOutput.write("date");
			csvOutput.endRecord();
		}

		for (String repo : repos) {
			while (morePages) {
				 System.out.println("Fetching page: " + pageNumber);
				url = "https://api.github.com/repos/" + orgName + "/" + repo + "/commits?since" + commitsSince
						+ "=&page=" + pageNumber + "&per_page=" + perPage;
				 System.out.println(url);
				pageNumber++;
				HttpResponse<String> response = Unirest.get(url).basicAuth(username, password).headers(headers)
						.asString();

				o = p.parse(response.getBody()).getAsJsonArray();

				for (int i = 0; i < o.size(); i++) {
					JsonObject author = o.get(i).getAsJsonObject().getAsJsonObject("commit").getAsJsonObject("author");

					String name = author.getAsJsonPrimitive("name").getAsString();
					csvOutput.write(name);
					String email = author.getAsJsonPrimitive("email").getAsString();
					csvOutput.write(email);
					String date = author.getAsJsonPrimitive("date").getAsString();
					csvOutput.write(date);
					csvOutput.endRecord();
				}
				if (o.size() < 100) {
					morePages = false;
				}
				Thread.sleep(1000);
			}
		}
	}
}
