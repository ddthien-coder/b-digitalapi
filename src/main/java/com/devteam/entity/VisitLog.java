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
public class VisitLog {
	private Long id;
	private String uuid;
	private String uri;
	private String method;
	private String param;
	private String behavior;
	private String content;
	private String remark;
	private String ip;
	private String ipSource;
	private String os;
	private String browser;
	private Integer times;
	private Date createTime;
	private String userAgent;

	public VisitLog(String uuid, String uri, String method, String behavior, String content, String remark, String ip, Integer times, String userAgent) {
		this.uuid = uuid;
		this.uri = uri;
		this.method = method;
		this.behavior = behavior;
		this.content = content;
		this.remark = remark;
		this.ip = ip;
		this.times = times;
		this.createTime = new Date();
		this.userAgent = userAgent;
	}
}
