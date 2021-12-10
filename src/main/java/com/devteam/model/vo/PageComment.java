package com.devteam.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class PageComment {
	private Long id;
	private String nickname;
	private String content;
	private String avatar;
	private Date createTime;
	private String website;
	private Boolean adminComment;
	private String parentCommentId;
	private String parentCommentNickname;

	private List<PageComment> replyComments = new ArrayList<>();
}
