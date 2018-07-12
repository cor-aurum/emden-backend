package de.recondita.emden.data;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Holds a List of single Results
 * @author felix
 *
 */
public class ResultList {

	private Result[] results;
	private String took;

	/**
	 * Constructs a List of Results
	 * @param results Array of Results
	 * @param took Time needed to collect results
	 */
	public ResultList(Result[] results, String took) {
		this.results = results;
		this.took = took;
	}

	/**
	 * Returns Results as JSON
	 * @return JSON
	 */
	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder array = Json.createArrayBuilder();

		for (Result r : results) {
			array.add(r.getData());
		}
		builder.add("Results", array);
		builder.add("Took", took);
		builder.add("Count", ""+results.length);

		return builder.build();
	}

	/**
	 * Gives the length of the list
	 * @return length
	 */
	public int getLength() {
		return results.length;
	}
}
