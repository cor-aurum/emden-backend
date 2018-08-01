package de.recondita.emden.data.search;

import java.io.IOException;

/**
 * Object to handle long Datauploading to a SearchWrapper
 * 
 * @author felix
 *
 */
public interface Pusher {

	/**
	 * Finish the send
	 * 
	 * @throws IOException
	 *             if there is an IO Error
	 */
	void send() throws IOException;

	/**
	 * Push Data
	 * 
	 * @param json
	 *            partial Data in completed JSON Statements
	 * @throws IOException
	 *             if there is an IO Error
	 */
	void writeJsonString(String json) throws IOException;

}
