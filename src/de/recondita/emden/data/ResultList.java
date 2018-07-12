package de.recondita.emden.data;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class ResultList {

	private Result[] results;
	private String took;
	private String count;

	public ResultList(Result[] results, String took, String count) {
		this.results = results;
		this.took = took;
		this.count = count;
	}

	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder array = Json.createArrayBuilder();

		for (Result r : results) {
			array.add(r.getData());
		}
		builder.add("Results", array);
		builder.add("Took", took);
		builder.add("Count", count);

		return builder.build();
	}

	public int getLength() {
		return results.length;
	}
}
