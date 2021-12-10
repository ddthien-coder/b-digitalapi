package com.devteam.util.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.devteam.entity.ScheduleJobLog;
import com.devteam.service.ScheduleJobService;
import com.devteam.util.common.SpringContextUtils;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Slf4j
public class ScheduleJob extends QuartzJobBean {
	private ExecutorService service = Executors.newSingleThreadExecutor();

	@Override
	protected void executeInternal(JobExecutionContext context) {
		com.devteam.entity.ScheduleJob scheduleJob = (com.devteam.entity.ScheduleJob) context.getMergedJobDataMap().get(com.devteam.entity.ScheduleJob.JOB_PARAM_KEY);
		ScheduleJobService scheduleJobService = (ScheduleJobService) SpringContextUtils.getBean("scheduleJobServiceImpl");
		ScheduleJobLog jobLog = new ScheduleJobLog();
		jobLog.setJobId(scheduleJob.getJobId());
		jobLog.setBeanName(scheduleJob.getBeanName());
		jobLog.setMethodName(scheduleJob.getMethodName());
		jobLog.setParams(scheduleJob.getParams());
		jobLog.setCreateTime(new Date());
		long startTime = System.currentTimeMillis();
		log.info("Task is ready to be executed, task ID：{}", scheduleJob.getJobId());
		try {
			ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(), scheduleJob.getMethodName(), scheduleJob.getParams());
			Future<?> future = service.submit(task);
			future.get();
			long times = System.currentTimeMillis() - startTime;
			jobLog.setTimes((int) times);
			jobLog.setStatus(true);
			log.info("The task was executed successfully, task ID: {}, total time-consuming: {} milliseconds ", scheduleJob.getJobId(), times);
		} catch (Exception e) {
			long times = System.currentTimeMillis() - startTime;
			jobLog.setTimes((int) times);
			jobLog.setStatus(false);
			jobLog.setError(e.toString());
			log.error("Task execution failed, task ID: {} ", scheduleJob.getJobId(), e);
		} finally {
			scheduleJobService.saveJobLog(jobLog);
		}
	}
}
