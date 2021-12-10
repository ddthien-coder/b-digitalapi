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
public class OperationLog {
	private Long id;
	private String username;
	private String uri;
	private String method;
	private String param;
	private String description;
	private String ip;
	private String ipSource;
	private String os;
	private String browser;
	private Integer times;
	private Date createTime;
	private String userAgent;

	public OperationLog(String username, String uri, String method, String description, String ip, Integer times, String userAgent) {
		this.username = username;
		this.uri = uri;
		this.method = method;
		this.description = description;
		this.ip = ip;
		this.times = times;
		this.createTime = new Date();
		this.userAgent = userAgent;
	}
}
