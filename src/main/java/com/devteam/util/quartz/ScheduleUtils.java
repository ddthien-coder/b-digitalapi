package com.devteam.util.quartz;

import org.quartz.*;
import com.devteam.entity.ScheduleJob;


public class ScheduleUtils {
	private final static String JOB_NAME = "TASK_";

	public static TriggerKey getTriggerKey(Long jobId) {
		return TriggerKey.triggerKey(JOB_NAME + jobId);
	}


	public static JobKey getJobKey(Long jobId) {
		return JobKey.jobKey(JOB_NAME + jobId);
	}

	public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
		try {
			return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
		} catch (SchedulerException e) {
			throw new RuntimeException("CronTrigger is error when obtaining the timing task", e);
		}
	}


	public static void createScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
		try {
			JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(scheduleJob.getJobId())).build();
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron()).withMisfireHandlingInstructionDoNothing();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getJobId())).withSchedule(scheduleBuilder).build();
			jobDetail.getJobDataMap().put(ScheduleJob.JOB_PARAM_KEY, scheduleJob);
			scheduler.scheduleJob(jobDetail, trigger);
			if (!scheduleJob.getStatus()) {
				pauseJob(scheduler, scheduleJob.getJobId());
			}
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to create a scheduled task", e);
		}
	}


	public static void updateScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
		try {
			TriggerKey triggerKey = getTriggerKey(scheduleJob.getJobId());
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron()).withMisfireHandlingInstructionDoNothing();
			CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getJobId());
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
			trigger.getJobDataMap().put(ScheduleJob.JOB_PARAM_KEY, scheduleJob);
			scheduler.rescheduleJob(triggerKey, trigger);
			if (!scheduleJob.getStatus()) {
				pauseJob(scheduler, scheduleJob.getJobId());
			}
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to update the scheduled task", e);
		}
	}


	public static void run(Scheduler scheduler, ScheduleJob scheduleJob) {
		try {
			//参数
			JobDataMap dataMap = new JobDataMap();
			dataMap.put(ScheduleJob.JOB_PARAM_KEY, scheduleJob);
			scheduler.triggerJob(getJobKey(scheduleJob.getJobId()), dataMap);
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to execute the scheduled task immediately", e);
		}
	}


	public static void pauseJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.pauseJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to pause the scheduled task ", e);
		}
	}


	public static void resumeJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.resumeJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to pause the scheduled task ", e);
		}
	}


	public static void deleteScheduleJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.deleteJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to delete scheduled task ", e);
		}
	}
}
