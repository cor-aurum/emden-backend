package de.recondita.emden.startup;

import java.util.HashSet;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.recondita.emden.data.input.CronCrawler;
import de.recondita.emden.data.search.ElasticsearchWrapper;

/**
 * Job for the Crawler to be started in Cron
 * 
 * @author felix
 *
 */
public class CrawlerJob implements Job {

	private static HashSet<String> registrations = new HashSet<>();

	@Override
	public synchronized void execute(JobExecutionContext ctx) throws JobExecutionException {
		System.out.println("Cron Execution started");
		// CronCrawler c = (CronCrawler) ctx.getScheduler().getContext().get("crawler");
		CronCrawler c = (CronCrawler) ctx.getJobDetail().getJobDataMap().get("crawler");
		System.out.println("Try to register " + c.getType());
		if (register(c.getType())) {
			System.out.println(c.getType() + " registered");
			c.pushResults(new ElasticsearchWrapper());
			registrations.remove(c.getType());
		} else
			System.out
					.println("Registration failed. " + c.getType() + " already registered and shall still be running");
	}

	private synchronized boolean register(String id) {
		if (registrations.contains(id))
			return false;
		registrations.add(id);
		return true;
	}
}
