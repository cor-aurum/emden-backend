package de.recondita.emden.appearance;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Result {
	private String titel;
	private String text;
	private String url;
	private String urltext;

	public Result(String titel, String text) {
		this(titel, text, "", "");
	}

	public Result(String titel, String text, String url, String urltext) {
		this.titel = titel;
		this.text = text;
		this.url = url;
		this.urltext = urltext;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrltext() {
		return urltext;
	}

	public void setUrltext(String urltext) {
		this.urltext = urltext;
	}

	protected JsonObject getData() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("Title", titel);
		builder.add("Text", text);
		builder.add("Url", url);
		builder.add("Urltext", urltext);
		return builder.build();
	}

}
