package de.recondita.emden.data;

public class DataField {
	private String description;
	private String data;
	public DataField(String description, String data) {
		super();
		this.description = description;
		this.data = data;
	}
	public String getDescription() {
		return description==null?"":description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getData() {
		return data==null?"":data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
