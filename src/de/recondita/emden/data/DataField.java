package de.recondita.emden.data;

/**
 * Wrapper Class for a Datafield, which contains a description and data
 * 
 * @author felix
 *
 */
public class DataField {
	private String description;
	private String data;

	/**
	 * Constructor for a DataField
	 * 
	 * @param description
	 *            Key
	 * @param data
	 *            Value
	 */
	public DataField(String description, String data) {
		super();
		this.description = description;
		this.data = data;
	}

	/**
	 * Getter for the Key
	 * @return Description
	 */
	public String getDescription() {
		return description == null ? "" : description;
	}

	/**
	 * Sets the Key
	 * @param description Key
 	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter for the Value
	 * @return Value
	 */
	public String getData() {
		return data == null ? "" : data;
	}

	/**
	 * Setter for the Value
	 * @param data Value
	 */
	public void setData(String data) {
		this.data = data;
	}

}
