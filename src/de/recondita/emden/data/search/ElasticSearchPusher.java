package de.recondita.emden.data.search;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.recondita.emden.data.RESTHandler;
import de.recondita.emden.data.Settings;
import de.recondita.emden.data.input.SourceSetup;

/**
 * Used for Streaming Data via POST
 * 
 * @author felix
 *
 */
public class ElasticSearchPusher implements Pusher {
	private StringBuilder builder = new StringBuilder();
	private final static String INDEX = "{\"index\":{}}\n";
	private final static String LINEBREAK = "\n";
	private int zaehler = 0;
	private int blockSize;
	private final URL u;
	private final RESTHandler rest = new RESTHandler();
	private ExecutorService pool;
	private final ElasticsearchWrapper searchWrapper;
	private final String index;

	/**
	 * Constructor
	 * 
	 * @param index
	 *            Index to push onto
	 * @param searchWrapper
	 *            Searchwrapper for renaming the Index
	 * @throws IOException
	 *             something went terribly wrong
	 */
	public ElasticSearchPusher(String index, ElasticsearchWrapper searchWrapper) throws IOException {
		String url = Settings.getInstance().getProperty("elasticsearch.url") + "/"
				+ Settings.getInstance().getProperty("index.basename") + index.toLowerCase() + SourceSetup.APPENDIX;
		u = new URL(url + "/_doc/_bulk");
		rest.delete(url);
		this.searchWrapper = searchWrapper;
		this.index = index.toLowerCase();
		blockSize = Integer.parseInt(Settings.getInstance().getProperty("http.blocksize"));
		int poolsize = Integer.parseInt(Settings.getInstance().getProperty("http.blockcount"));
		pool = Executors.newFixedThreadPool(poolsize);
	}

	private void send(final String bulk) {
		final int zaehlerAkt = zaehler;
		pool.execute(new Runnable() {
			public void run() {
				try {
					rest.post(u, bulk);
					System.out.println("Number of uploaded entries for Index "
							+ Settings.getInstance().getProperty("index.basename") + index + SourceSetup.APPENDIX + ": "
							+ zaehlerAkt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public void send() throws IOException {
		sendPartialData();
		System.out.println("Uploading complete. " + zaehler + " entries in Index " + index);
		join();
		searchWrapper.renameIndex(Settings.getInstance().getProperty("index.basename") + index + SourceSetup.APPENDIX,
				Settings.getInstance().getProperty("index.basename") + index, zaehler);
	}

	private void join() {
		try {
			pool.shutdown();
			if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
				System.err.println("Upload Timed Out!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendPartialData() throws IOException {
		if (builder.length() <= 0)
			return;
		String s = builder.toString();
		builder.setLength(0);
		send(s);
	}

	@Override
	public synchronized void writeJsonString(String json) throws IOException {
		builder.append(INDEX);
		builder.append(json);
		builder.append(LINEBREAK);
		zaehler++;
		if (zaehler % blockSize == 0) {
			sendPartialData();
			if (zaehler % 1000000 == 0) {
				while (!searchWrapper.checkCount(
						Settings.getInstance().getProperty("index.basename") + index + SourceSetup.APPENDIX, zaehler)) {
					try {
						System.out.println("Wait for Elasticsearch to index. Next try in five seconds");
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
