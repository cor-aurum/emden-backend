package de.recondita.emden.data;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Provides the Configuration, what Datafields are present
 * 
 * @author felix
 *
 */
public final class DataFieldSetup {
	private static String[] datafields = new String[] {};

	/**
	 * You wont instantiate me!
	 */
	private DataFieldSetup() {
	}

	/**
	 * Gets the Datafields as StringArray
	 * 
	 * @return datafields
	 */
	public static String[] getDatafields() {
		return datafields;
	}

	/**
	 * Sets the Datafields
	 * 
	 * @param datafields
	 *            Array og Descriptions
	 */
	public static void setDatafields(String[] datafields) {
		DataFieldSetup.datafields = datafields;
	}

	/**
	 * adds a Single DataField
	 * 
	 * @param datafield
	 *            a Single Description, to use in DataField
	 */
	public synchronized static void setDatafield(String datafield) {
		String[] ret = new String[datafields.length + 1];
		int i = 0;
		for (; i < datafields.length; i++) {
			ret[i] = datafields[i];
		}
		ret[i] = datafield;
		DataFieldSetup.datafields = ret;
	}

	/**
	 * Gets the Datafields as JSOn, for Sending to a Client
	 * 
	 * @return Datafields as Json
	 */
	public static JsonObject getDatafieldsAsJson() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		JsonObjectBuilder obj = Json.createObjectBuilder();
		for (String s : datafields) {
			builder.add(s);
		}
		obj.add("Datafields", builder);
		return obj.build();
	}
}
