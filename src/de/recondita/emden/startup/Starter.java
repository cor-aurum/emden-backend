package de.recondita.emden.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class Starter {
	
	@PostConstruct
	  void init() {
		System.out.println("Init");
		//CSVCrawler crawler=new CSVCrawler(new File("/home/felix/Downloads/test.csv"), ";", true, null);
		//crawler.pushResults(new ElasticsearchWrapper());
	  }

}
