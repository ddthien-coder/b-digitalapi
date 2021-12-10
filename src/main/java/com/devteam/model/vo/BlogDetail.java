package com.devteam.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.devteam.entity.Category;
import com.devteam.entity.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogDetail {
	private Long id;
	private String title;
	private String videoId;
	private String createYear;
	private String content;
	private Boolean appreciation;
	private Boolean commentEnabled;
	private Boolean top;
	private Date createTime;
	private Date updateTime;
	private Integer views;
	private Integer words;
	private Integer readTime;
	private String password;

	private Category category;
	private List<Tag> tags = new ArrayList<>();
}
