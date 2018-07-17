package de.recondita.emden.data.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import de.recondita.emden.data.Settings;

/**
 * Used for Streaming Data via POST
 * 
 * @author felix
 *
 */
public class Pusher {
	private StringBuilder builder = new StringBuilder();
	private final static String INDEX = "{\"index\":{}}\n";
	private final static String LINEBREAK = "\n";
	private int zaehler = 0;
	private final URL u;
	private int BLOCK_SIZE;

	public Pusher(String url) throws IOException {
		u = new URL(url);
		BLOCK_SIZE = Integer.parseInt(Settings.getInstance().getProperty("http.blocksize"));
		initNewConnection();
	}

	private HttpURLConnection initNewConnection() throws IOException {
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setUseCaches(false);
		
		return con;
	}

	/**
	 * Finishes the stream
	 * 
	 * @param stream
	 *            Stream to flush
	 * @return responsecode
	 * @throws IOException
	 */
	private int flush(HttpURLConnection oldcon) throws IOException {
		oldcon.getOutputStream().flush();
		oldcon.getOutputStream().close();
		BufferedReader in = new BufferedReader(new InputStreamReader(oldcon.getInputStream()));
		while ((in.readLine()) != null)
			;

		in.close();
		return oldcon.getResponseCode();
	}

	private void send(final String bulk) {
		new Thread() {
			public void run() {
				try {
					HttpURLConnection con = initNewConnection();
					byte[] payload = bulk.getBytes(StandardCharsets.UTF_8);
					con.setFixedLengthStreamingMode(payload.length);
					con.getOutputStream().write(payload);
					System.out.println("Block send with Status Code: " + flush(con));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	/**
	 * Finishes the Stream
	 * 
	 * @throws IOException
	 */
	public void send() throws IOException {
		if(builder.length()<=0)
			return;
		String s = builder.toString();
		builder.setLength(0);
		send(s);
	}

	public void writeJsonString(String json) throws IOException {
		builder.append(INDEX);
		builder.append(json);
		builder.append(LINEBREAK);
		if (zaehler >= BLOCK_SIZE) {
			zaehler = 0;
			send();
		}

		zaehler++;
	}

}
