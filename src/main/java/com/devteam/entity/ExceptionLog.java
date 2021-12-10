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
public class ExceptionLog {
	private Long id;
	private String uri;
	private String method;
	private String param;
	private String description;
	private String error;
	private String ip;
	private String ipSource;
	private String os;
	private String browser;
	private Date createTime;
	private String userAgent;

	public ExceptionLog(String uri, String method, String description, String error, String ip, String userAgent) {
		this.uri = uri;
		this.method = method;
		this.description = description;
		this.error = error;
		this.ip = ip;
		this.createTime = new Date();
		this.userAgent = userAgent;
	}
}
