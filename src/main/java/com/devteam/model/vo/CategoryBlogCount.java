package com.devteam.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class CategoryBlogCount {
	private Long id;
	private String name;
	private Integer value;
}
