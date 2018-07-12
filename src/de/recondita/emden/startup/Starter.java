package de.recondita.emden.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import de.recondita.emden.data.PathProvider;

/**
 * Autostart routine
 * 
 * @author felix
 *
 */
@Startup
@Singleton
public class Starter {

	/**
	 * Do stuff on init
	 */
	@PostConstruct
	void init() {
		new ConfigParser(PathProvider.getInstance().getConfig());
		// CSVCrawler c=new CSVCrawler(new File("/home/felix/Downloads/convertcsv.csv"),
		// ",", true, new String[] {"Title","Text"});
		// c.pushResults(new ElasticsearchWrapper());
	}

}
