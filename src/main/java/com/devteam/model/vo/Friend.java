package com.devteam.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class Friend {
	private String nickname;
	private String description;
	private String website;
	private String avatar;
}
