package com.devteam.entity;

import com.devteam.model.vo.BlogIdAndTitle;
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
public class Comment {
	private Long id;
	private String nickname;
	private String email;
	private String content;
	private String avatar;
	private Date createTime;
	private String website;
	private String ip;
	private Boolean published;
	private Boolean adminComment;
	private Integer page;
	private Boolean notice;
	private Long parentCommentId;
	private String qq;

	private BlogIdAndTitle blog;
	private List<Comment> replyComments = new ArrayList<>();
}
