package de.recondita.emden.data.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import de.recondita.emden.data.Result;
import de.recondita.emden.data.ResultList;
import de.recondita.emden.data.Settings;

public class ElasticsearchWrapper implements SearchWrapper {

	private final String esUrl;

	public ElasticsearchWrapper() {
		Settings s = Settings.getInstance();
		esUrl = s.getProperty("elasticsearch.url");
	}

	@Override
	public ResultList simpleSearch(String query) {
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String resultString = "";
		try {
			resultString = get(esUrl + "/_search?q=*" + query+"*");
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonArray results = toJson(resultString).getJsonObject("hits").getJsonArray("hits");
		ArrayList<Result> ret = new ArrayList<Result>();
		for (JsonValue o : results) {
			ret.add(new Result(((JsonObject) o).getJsonObject("_source")));
		}
		return new ResultList(ret.toArray(new Result[ret.size()]));
	}

	@Override
	public Result getResult(int id) {
		return null;
	}

	private JsonObject toJson(String s) {
		JsonReader reader = Json.createReader(new StringReader(s));
		return reader.readObject();
	}

	private String get(String url) throws IOException {
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String input;
		StringBuffer response = new StringBuffer();
		while ((input = in.readLine()) != null)
			response.append(input);
		in.close();
		return response.toString();
	}
	
	private String post(String url, String json) throws IOException {
		System.out.println(json);
		byte[] payload=json.getBytes(StandardCharsets.UTF_8);
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setFixedLengthStreamingMode(payload.length);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); 
		con.getOutputStream().write(payload);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String input;
		StringBuffer response = new StringBuffer();
		while ((input = in.readLine()) != null)
			response.append(input);
		in.close();
		return response.toString();
	}

	@Override
	public void pushResult(Result r) {
		try {
			System.out.println(post(esUrl+"/logs/emden", r.getData().toString()));
		} catch (IOException e) {
			System.err.println("----------------------------------------Error while posting to Elasticsearch. This is fatal!");
			e.printStackTrace();
		}
	}

}
