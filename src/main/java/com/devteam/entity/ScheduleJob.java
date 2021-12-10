package com.devteam.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class ScheduleJob implements Job {
	public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

	private Long jobId;
	private String beanName;
	private String methodName;
	private String params;
	private String cron;
	private Boolean status;
	private String remark;
	private Date createTime;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
	}
}
