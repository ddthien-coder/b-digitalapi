package com.devteam.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.devteam.entity.Category;
import com.devteam.entity.Tag;
import com.devteam.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class Blog {
	private Long id;
	private String title;
	private String videoId;
	private String content;
	private String description;
	private Boolean published;
	private Boolean recommend;
	private Boolean appreciation;
	private Boolean commentEnabled;
	private Boolean top;
	private Date createTime;
	private Date updateTime;
	private Integer views;
	private Integer words;
	private Integer readTime;
	private String password;
	private String createYear;

	private User user;
	private Category category;
	private List<Tag> tags = new ArrayList<>();

	private Object cate;
	private List<Object> tagList;
}
