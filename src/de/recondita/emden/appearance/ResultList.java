package de.recondita.emden.appearance;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class ResultList {

	private Result[] results;

	public ResultList(Result[] results) {
		this.results=results;
	}

	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder array = Json.createArrayBuilder();
		
		for (Result r : results) {
			array.add(r.getData());
		}
		builder.add("Results", array);
		
		return builder.build();
	}
}
