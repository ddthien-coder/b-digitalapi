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
public class BlogInfo {
	private Long id;
	private String title;
	private String description;
	private Date createTime;
	private Integer views;
	private Integer words;
	private Integer readTime;
	private Boolean top;
	private String password;
	private Boolean privacy;

	private Category category;
	private List<Tag> tags = new ArrayList<>();
}
