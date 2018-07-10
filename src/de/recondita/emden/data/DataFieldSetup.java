package de.recondita.emden.data;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class DataFieldSetup {
	private static String[] datafields=new String[] {};

	public static String[] getDatafields() {
		return datafields;
	}

	public static void setDatafields(String[] datafields) {
		DataFieldSetup.datafields = datafields;
	}

	public synchronized static void setDatafield(String datafield) {
		String[] ret=new String[datafields.length+1];
		int i=0;
		for(;i<datafields.length;i++) {
			ret[i]=datafields[i];
		}
		ret[i]=datafield;
		DataFieldSetup.datafields = ret;
	}
	
	public static JsonObject getDatafieldsAsJson() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		JsonObjectBuilder obj = Json.createObjectBuilder();
		for(String s:datafields) {
			builder.add(s);
		}
		obj.add("Datafields", builder);
		return obj.build();
	}
}
