package com.devteam.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class Category {
	private Long id;
	private String name;
	private String color;
	private List<Blog> blogs = new ArrayList<>();
}
