package de.recondita.emden.startup;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import de.recondita.emden.data.input.CronCrawler;

public class Cron<C extends CronCrawler> {

	public Cron(String schedule, C crawler) throws SchedulerException {
		JobDetail jobDetail=JobBuilder.newJob(CrawlerJob.class).withDescription(crawler.getType()).build();
		CronTrigger trigger = TriggerBuilder.newTrigger().withDescription(crawler.getType())
				.withSchedule(CronScheduleBuilder.cronSchedule(schedule)).build();
		Scheduler scheduler=new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.getContext().put("crawler",crawler);
		scheduler.scheduleJob(jobDetail, trigger);
	}
}
