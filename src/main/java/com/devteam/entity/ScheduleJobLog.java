package com.devteam.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class ScheduleJobLog {
	private Long logId;
	private Long jobId;
	private String beanName;
	private String methodName;
	private String params;
	private Boolean status;
	private String error;
	private Integer times;
	private Date createTime;
}
