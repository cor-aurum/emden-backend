package de.recondita.emden.data.search;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import de.recondita.emden.data.DataFieldSetup;
import de.recondita.emden.data.RESTHandler;
import de.recondita.emden.data.Result;
import de.recondita.emden.data.ResultList;
import de.recondita.emden.data.Settings;
import de.recondita.emden.data.input.SourceSetup;

/**
 * SearchWrapper for ElasticSearch
 * 
 * @author felix
 *
 */
public class ElasticsearchWrapper implements SearchWrapper {

	private final String esUrl;
	private final RESTHandler rest = new RESTHandler();

	/**
	 * Constructor..
	 */
	public ElasticsearchWrapper() {
		Settings s = Settings.getInstance();
		esUrl = s.getProperty("elasticsearch.url");
	}

	@Override
	public ResultList simpleSearch(String query) {
		return simpleSearch(query, SourceSetup.getSourceCSV());
	}

	/**
	 * Simple Search above an index
	 * 
	 * @param query
	 *            Searchterm
	 * @param index
	 *            Index to search
	 * @return Resultlist
	 */
	public ResultList simpleSearch(String query, String index) {
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String resultString = "";
		try {
			resultString = rest.get(esUrl + "/" + index + "/_search?q=" + query + "&size="
					+ Settings.getInstance().getProperty("max.searchresults"));
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

	private JsonObject toJson(String s) {
		JsonReader reader = Json.createReader(new StringReader(s));
		return reader.readObject();
	}

	@Override
	public boolean pushResult(Result r, String index) {
		try {
			if (advancedSearch(r.getFlatData(), true).getLength() <= 0) {
				rest.post(esUrl + Settings.getInstance().getProperty("index.basename") + index.toLowerCase() + "/_doc",
						r.getData().toString());
				return true;
			}
		} catch (IOException e) {
			System.err.println(
					"----------------------------------------Error while posting to Elasticsearch. This is fatal!");
			e.printStackTrace();

		}
		return false;
	}

	private String exactSearchJson(String[] query) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder querybuilder = Json.createObjectBuilder();
		JsonObjectBuilder bool = Json.createObjectBuilder();
		JsonObjectBuilder filter = Json.createObjectBuilder();
		JsonObjectBuilder score = Json.createObjectBuilder();
		JsonArrayBuilder should = Json.createArrayBuilder();
		String[] fields = DataFieldSetup.getDatafields();
		for (int i = 0; i < query.length; i++) {
			if (query[i] != null && !query[i].isEmpty()) {
				JsonObjectBuilder tmp = Json.createObjectBuilder();
				JsonObjectBuilder data = Json.createObjectBuilder();
				data.add(fields[i], query[i]);
				tmp.add("match_phrase", data);
				should.add(tmp);
			}
		}
		bool.add("must", should);
		querybuilder.add("bool", bool);
		filter.add("filter", querybuilder);
		score.add("constant_score", filter);
		builder.add("query", score);
		// builder.add("minimum_should_match",1.0);
		builder.add("size", Settings.getInstance().getProperty("max.searchresults"));
		return builder.build().toString();
	}

	private String searchJson(String[] query) {
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
		return builder.build().toString();
	}

	private boolean checkCount(String index, int expected) {
		ResultList r = simpleSearch("*", index);
		int t=Integer.parseInt(r.getCount());

		return t>=expected;
	}

	/**
	 * Copies a index to a new name. If the new Index is already present, it will be
	 * deleted.
	 * 
	 * @param oldIndexname
	 *            old Index
	 * @param newIndexname
	 *            Index to copy to
	 * @throws IOException
	 */
	void renameIndex(String oldIndexname, String newIndexname, int countOfOldIndex) throws IOException {
		System.out.println("Replace " + newIndexname + " with " + oldIndexname);
		SourceSetup.doBackup(newIndexname);
		String indexstub = Settings.getInstance().getProperty("elasticsearch.url") + "/";
		if (rest.existsEndpoint(indexstub + oldIndexname)) {
			String rename = "{\"source\": {\"index\": \"" + oldIndexname + "\"},\"dest\": {\"index\": \"" + newIndexname
					+ "\"}}";
			/**
			 * Wait for Elasticsearch to complete indexing
			 */
			while (!checkCount(oldIndexname, countOfOldIndex)) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			rest.delete(indexstub + newIndexname);
			long aktTime = System.currentTimeMillis();
			rest.post(esUrl + "/_reindex", rename);
			rest.delete(indexstub + oldIndexname);
			System.out.println("Time taken to rename index: " + ((System.currentTimeMillis() - aktTime) / 1000) + "s");
			SourceSetup.undoBackup(newIndexname);
		}
	}

	@Override
	public ResultList advancedSearch(String[] query, boolean exact) {
		String resultString = "";
		String searchString = exact ? exactSearchJson(query) : searchJson(query);
		// System.out.println("Suchstring: " + searchString);
		try {
			resultString = rest.post(esUrl + "/" + SourceSetup.getSourceCSV() + "/_search", searchString);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResultList(new Result[] {}, "0", "0");
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
	public Pusher pushResults(String index) {
		try {
			return new ElasticSearchPusher(index, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
