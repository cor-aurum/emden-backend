package de.recondita.emden.appearance;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Result {
	private String title;
	private String text;
	private String url;
	private String urltext;

	public Result(String titel, String text) {
		this(titel, text, "", "");
	}

	public Result(String titel, String text, String url, String urltext) {
		this.title = titel;
		this.text = text;
		this.url = url;
		this.urltext = urltext;
	}

	public Result(JsonObject json) {
		this.title = json.getString("Title","");
		this.text = json.getString("Text","");
		this.url = json.getString("Url","");
		this.urltext = json.getString("Urltext","");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
		builder.add("Title", title);
		builder.add("Text", text);
		builder.add("Url", url);
		builder.add("Urltext", urltext);
		return builder.build();
	}

}
