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
public class Visitor {
	private Long id;
	private String uuid;
	private String ip;
	private String ipSource;
	private String os;
	private String browser;
	private Date createTime;
	private Date lastTime;
	private Integer pv;
	private String userAgent;

	public Visitor(String uuid, String ip, String userAgent) {
		this.uuid = uuid;
		this.ip = ip;
		Date date = new Date();
		this.createTime = date;
		this.lastTime = date;
		this.pv = 0;
		this.userAgent = userAgent;
	}
}
