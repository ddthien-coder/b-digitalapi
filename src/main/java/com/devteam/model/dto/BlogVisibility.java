package com.devteam.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogVisibility {
	private Boolean appreciation;
	private Boolean recommend;
	private Boolean commentEnabled;
	private Boolean top;
	private Boolean published;
	private String password;
}
