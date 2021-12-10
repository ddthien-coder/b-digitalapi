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
public class Moment {
	private Long id;
	private String content;
	private Date createTime;
	private Integer likes;
	private Boolean published;
}
