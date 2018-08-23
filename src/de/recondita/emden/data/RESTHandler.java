package de.recondita.emden.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Class to Handle HTTP Connections
 * 
 * @author felix
 *
 */
public class RESTHandler {

	/**
	 * Handles a HTTP Post with a json body
	 * 
	 * @param url
	 *            URL of the Resource
	 * @param json
	 *            Body
	 * @return Response
	 * @throws IOException
	 *             if something went wrong
	 */
	public String post(String url, String json) throws IOException {
		URL u = new URL(url);
		return post(u, json);
	}

	/**
	 * Handles a HTTP Post with a json body
	 * 
	 * @param u
	 *            URL of the Resource
	 * @param json
	 *            Body
	 * @return Response
	 * @throws IOException
	 *             if something went wrong
	 */
	public String post(URL u, String json) throws IOException {
		byte[] payload = json.getBytes(StandardCharsets.UTF_8);
		HttpURLConnection con = initNewConnection(u);
		con.setFixedLengthStreamingMode(payload.length);
		con.getOutputStream().write(payload);
		int resp = con.getResponseCode();
		if (resp < 200 || resp > 300) {
			String m = con.getResponseMessage();
			System.err.println(resp + ": " + m);
			throw new IOException(resp + ": " + m);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String input;
		StringBuffer response = new StringBuffer();
		while ((input = in.readLine()) != null)
			response.append(input);
		in.close();
		return response.toString();
	}

	/**
	 * HTTP Get
	 * 
	 * @param url
	 *            of the resource
	 * @return response
	 * @throws IOException
	 *             IO Error
	 */
	public String get(String url) throws IOException {
		URL u = new URL(url);
		HttpURLConnection con = initNewConnection(u, "GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String input;
		StringBuffer response = new StringBuffer();
		while ((input = in.readLine()) != null)
			response.append(input);
		in.close();
		return response.toString();
	}

	/**
	 * Deletes a Resource
	 * 
	 * @param url
	 *            Resource
	 * @throws IOException
	 *             IOException
	 */
	public void delete(String url) throws IOException {
		URL u = new URL(url);
		System.out.println("Delete " + url);
		HttpURLConnection con = initNewConnection(u, "DELETE");
		System.out.println("Deleted: " + con.getResponseCode());
	}

	/**
	 * Puts a resource
	 * 
	 * @param url
	 *            url to put
	 * @throws IOException
	 *             Exception
	 */
	public void put(String url) throws IOException {
		URL u = new URL(url);
		System.out.println("Put " + url);
		HttpURLConnection con = initNewConnection(u, "PUT");
		System.out.println("Putted: " + con.getResponseCode());
	}

	/**
	 * Gives a blank Connection with Method POST
	 * 
	 * @param u
	 *            URL to connect
	 * @return Connection
	 * @throws IOException
	 *             Something went wrong
	 */
	public HttpURLConnection initNewConnection(URL u) throws IOException {
		HttpURLConnection ret = initNewConnection(u, "POST");
		ret.setDoOutput(true);
		ret.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		return ret;
	}

	/**
	 * Gives a blank Connection
	 * 
	 * @param u
	 *            URL to connect
	 * @param method
	 *            HTTP MEthod
	 * @return Connection
	 * @throws IOException
	 *             Something went wrong
	 */
	public HttpURLConnection initNewConnection(URL u, String method) throws IOException {
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod(method);
		return con;
	}

	/**
	 * Checks, whether an endpoint is present
	 * 
	 * @param url
	 *            URL to check
	 * @return true, if the responsecode is 2xx
	 */
	public boolean existsEndpoint(String url) {
		try {
			HttpURLConnection con = initNewConnection(new URL(url), "GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			do {
				line = in.readLine();
			} while (line != null);

			in.close();
			int ret = con.getResponseCode();
			return 100 < ret && ret < 300;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
