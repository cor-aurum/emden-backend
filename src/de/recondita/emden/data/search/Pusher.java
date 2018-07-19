package de.recondita.emden.data.search;

import java.io.IOException;

public interface Pusher {

	void send() throws IOException;

	void writeJsonString(String json) throws IOException;

}
