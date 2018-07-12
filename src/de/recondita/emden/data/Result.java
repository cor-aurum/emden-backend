package de.recondita.emden.data;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

public class Result {
	private DataField[] data;

	public Result(DataField[] data) {
		this.data = data;
	}

	public Result(JsonObject json) {
		ArrayList<DataField> temp = new ArrayList<DataField>();
		for (Entry<String, JsonValue> e : json.entrySet()) {
			temp.add(new DataField(e.getKey(), ((JsonString) e.getValue()).getString()));
		}
		data = temp.toArray(new DataField[temp.size()]);
	}

	public JsonObject getData() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (DataField d : data) {
			builder.add(d.getDescription(), d.getData());
		}
		return builder.build();
	}

	public DataField[] getPlainData() {
		return data;
	}

	public String[] getFlatData() {
		String[] ret = new String[DataFieldSetup.getDatafields().length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = "";
			for (int j = 0; j < data.length; j++) {
				if (data[j].getDescription().equals(DataFieldSetup.getDatafields()[i])) {
					ret[i]=data[j].getData();
				}
			}
		}
		return ret;
	}

}
