package de.recondita.emden.startup;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import de.recondita.emden.data.input.CronCrawler;
import de.recondita.emden.data.search.ElasticsearchWrapper;

public class CrawlerJob implements Job{
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		System.out.println("Cron Execution started");
		try {
			CronCrawler c =(CronCrawler)ctx.getScheduler().getContext().get("crawler");
			c.pushResults(new ElasticsearchWrapper());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
