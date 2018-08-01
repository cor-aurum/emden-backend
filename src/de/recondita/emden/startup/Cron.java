package de.recondita.emden.startup;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import de.recondita.emden.data.Settings;
import de.recondita.emden.data.input.CronCrawler;
import de.recondita.emden.data.search.ElasticsearchWrapper;

/**
 * Dynamic Scheduler
 * 
 * @author felix
 *
 * @param <C>
 */
public class Cron<C extends CronCrawler> {

	/**
	 * Scheduler instance
	 */
	private static Scheduler scheduler;

	/**
	 * Constructs a Scheduling Service for a Crawler with a cron ConfigString
	 * 
	 * @param schedule
	 *            configString
	 * @param crawler
	 *            crawler to Schedule
	 * @throws SchedulerException
	 *             if something wents terribly wrong
	 */
	public Cron(String schedule, C crawler) throws SchedulerException {
		new ElasticsearchWrapper()
				.createIndex(Settings.getInstance().getProperty("index.basename") + crawler.getType().toLowerCase());
		JobDetail jobDetail = JobBuilder.newJob(CrawlerJob.class).withIdentity(crawler.getType(), "crawler").build();
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(crawler.getType(), "crawler")
				.withSchedule(CronScheduleBuilder.cronSchedule(schedule)).build();
		// scheduler.getContext().put("crawler", crawler);
		jobDetail.getJobDataMap().put("crawler", crawler);
		scheduler.scheduleJob(jobDetail, trigger);
	}

	/**
	 * Not needed really, without this Method, Checkstyle identifies this class as a
	 * utility class and will not build
	 * 
	 * @return scheduler
	 */
	public Scheduler getActiveScheduler() {
		return scheduler;
	}

	/**
	 * Instantiates the Scheduler
	 */
	public static void initSched() {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
