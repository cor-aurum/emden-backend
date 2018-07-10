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
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import de.recondita.emden.data.DataFieldSetup;
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
			resultString = get(esUrl + "/_search?q=" + query + "&size="+Settings.getInstance().getProperty("max.searchresults"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonObject result = toJson(resultString);
		JsonArray results = result.getJsonObject("hits").getJsonArray("hits");
		ArrayList<Result> ret = new ArrayList<Result>();
		for (JsonValue o : results) {
			ret.add(new Result(((JsonObject) o).getJsonObject("_source")));
		}
		return new ResultList(ret.toArray(new Result[ret.size()]), result.getJsonNumber("took").toString(),
				result.getJsonObject("hits").getJsonNumber("total").toString());
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
		byte[] payload = json.getBytes(StandardCharsets.UTF_8);
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
			System.out.println(post(esUrl + "/emden/_doc", r.getData().toString()));
		} catch (IOException e) {
			System.err.println(
					"----------------------------------------Error while posting to Elasticsearch. This is fatal!");
			e.printStackTrace();
		}
	}

	@Override
	public ResultList advancedSearch(String[] query) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder querybuilder = Json.createObjectBuilder();
		JsonObjectBuilder bool = Json.createObjectBuilder();
		JsonArrayBuilder should = Json.createArrayBuilder();
		String[] fields = DataFieldSetup.getDatafields();
		for (int i = 0; i < query.length; i++) {
			if (query[i] != null && !query[i].isEmpty()) {
				JsonObjectBuilder tmp = Json.createObjectBuilder();
				JsonObjectBuilder data = Json.createObjectBuilder();
				data.add(fields[i], query[i]);
				tmp.add("match", data);
				should.add(tmp);
			}
		}
		bool.add("must", should);
		querybuilder.add("bool", bool);
		builder.add("query", querybuilder);
		builder.add("size", Settings.getInstance().getProperty("max.searchresults"));
		String resultString = "";
		try {
			resultString = post(esUrl+"/emden/_search", builder.build().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonObject result = toJson(resultString);
		JsonArray results = result.getJsonObject("hits").getJsonArray("hits");
		ArrayList<Result> ret = new ArrayList<Result>();
		for (JsonValue o : results) {
			ret.add(new Result(((JsonObject) o).getJsonObject("_source")));
		}
		return new ResultList(ret.toArray(new Result[ret.size()]), result.getJsonNumber("took").toString(),
				result.getJsonObject("hits").getJsonNumber("total").toString());
	}

}
