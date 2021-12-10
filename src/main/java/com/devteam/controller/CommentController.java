package com.devteam.controller;

import com.devteam.annotation.AccessLimit;
import com.devteam.entity.User;
import com.devteam.model.dto.Comment;
import com.devteam.model.vo.FriendInfo;
import com.devteam.model.vo.PageComment;
import com.devteam.model.vo.PageResult;
import com.devteam.model.vo.Result;
import com.devteam.service.AboutService;
import com.devteam.service.BlogService;
import com.devteam.service.CommentService;
import com.devteam.service.FriendService;
import com.devteam.service.impl.UserServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devteam.util.HashUtils;
import com.devteam.util.IpAddressUtils;
import com.devteam.util.JwtUtils;
import com.devteam.util.MailUtils;
import com.devteam.util.QQInfoUtils;
import com.devteam.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
public class CommentController {
	@Autowired
    CommentService commentService;
	@Autowired
    BlogService blogService;
	@Autowired
    AboutService aboutService;
	@Autowired
    UserServiceImpl userService;
	@Autowired
    FriendService friendService;
	@Autowired
	MailProperties mailProperties;
	@Autowired
	MailUtils mailUtils;
	private String bName;
	private String cmsUrl;
	private String websiteUrl;

	@Value("${custom.blog.name}")
	public void setBName(String bName) {
		this.bName = bName;
	}

	@Value("${custom.url.cms}")
	public void setCmsUrl(String cmsUrl) {
		this.cmsUrl = cmsUrl;
	}

	@Value("${custom.url.website}")
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}


	@GetMapping("/comments")
	public Result comments(@RequestParam Integer page,
                           @RequestParam(defaultValue = "") Long blogId,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		int judgeResult = judgeCommentEnabled(page, blogId);
		if (judgeResult == 2) {
			return Result.create(404, "Does not exist");
		} else if (judgeResult == 1) {
			return Result.create(403, "评论已关闭");
		} else if (judgeResult == 3) {
			if (JwtUtils.judgeTokenIsExist(jwt)) {
				try {
					String subject = JwtUtils.getTokenBody(jwt).getSubject();
					if (subject.startsWith("admin:")) {
						String username = subject.replace("admin:", "");
						User admin = (User) userService.loadUserByUsername(username);
						if (admin == null) {
							return Result.create(403, "The blogger identity token has expired, please log in again！");
						}
					} else {
						Long tokenBlogId = Long.parseLong(subject);
						if (!tokenBlogId.equals(blogId)) {
							return Result.create(403, "Token does not match, please re-verify the password ！");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return Result.create(403, "Token has expired, please re-verify the password！");
				}
			} else {
				return Result.create(403, "password protected, please verify the password ！");
			}
		}
		Integer allComment = commentService.countByPageAndIsPublished(page, blogId, null);
		Integer openComment = commentService.countByPageAndIsPublished(page, blogId, true);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<PageComment> pageInfo = new PageInfo<>(commentService.getPageCommentList(page, blogId, (long) -1));
		PageResult<PageComment> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
		Map<String, Object> map = new HashMap<>();
		map.put("allComment", allComment);
		map.put("closeComment", allComment - openComment);
		map.put("comments", pageResult);
		return Result.ok("Get success", map);
	}


	private int judgeCommentEnabled(Integer page, Long blogId) {
		if (page == 0) {//普通博客
			Boolean commentEnabled = blogService.getCommentEnabledByBlogId(blogId);
			Boolean published = blogService.getPublishedByBlogId(blogId);
			if (commentEnabled == null || published == null) {
				return 2;
			} else if (!published) {
				return 2;
			} else if (!commentEnabled) {
				return 1;
			}
			String password = blogService.getBlogPassword(blogId);
			if (!"".equals(password)) {
				return 3;
			}
		} else if (page == 1) {
			if (!aboutService.getAboutCommentEnabled()) {
				return 1;
			}
		} else if (page == 2) {
			FriendInfo friendInfo = friendService.getFriendInfo(true, false);
			if (!friendInfo.getCommentEnabled()) {
				return 1;
			}
		}
		return 0;
	}


	@AccessLimit(seconds = 30, maxCount = 1, msg = "Only one comment can be submitted within 30 seconds ")
	@PostMapping("/comment")
	public Result postComment(@RequestBody Comment comment,
	                          HttpServletRequest request,
	                          @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		if (StringUtils.isEmpty(comment.getContent()) || comment.getContent().length() > 250 ||
				comment.getPage() == null || comment.getParentCommentId() == null) {
			return Result.error("The parameter is wrong");
		}
		boolean isVisitorComment = false;
		com.devteam.entity.Comment parentComment = null;
		if (comment.getParentCommentId() != -1) {
			parentComment = commentService.getCommentById(comment.getParentCommentId());
			Integer page = parentComment.getPage();
			Long blogId = page == 0 ? parentComment.getBlog().getId() : null;
			comment.setPage(page);
			comment.setBlogId(blogId);
		} else {
			if (comment.getPage() != 0) {
				comment.setBlogId(null);
			}
		}
		int judgeResult = judgeCommentEnabled(comment.getPage(), comment.getBlogId());
		if (judgeResult == 2) {
			return Result.create(404, "Does not exist");
		} else if (judgeResult == 1) {
			return Result.create(403, "Comments are closed");
		} else if (judgeResult == 3) {
			if (JwtUtils.judgeTokenIsExist(jwt)) {
				String subject;
				try {
					subject = JwtUtils.getTokenBody(jwt).getSubject();
				} catch (Exception e) {
					e.printStackTrace();
					return Result.create(403, "Token has expired, please re-verify the password！");
				}
				if (subject.startsWith("admin:")) {
					String username = subject.replace("admin:", "");
					User admin = (User) userService.loadUserByUsername(username);
					if (admin == null) {
						return Result.create(403, "Token has expired, please log in again ！");
					}
					setAdminComment(comment, request, admin);
					isVisitorComment = false;
				} else {
					if (StringUtils.isEmpty(comment.getNickname(), comment.getEmail()) || comment.getNickname().length() > 15) {
						return Result.error("The parameter is wrong");
					}
					Long tokenBlogId = Long.parseLong(subject);
					if (!tokenBlogId.equals(comment.getBlogId())) {
						return Result.create(403, "Token does not match, please re-verify the password ！");
					}
					setVisitorComment(comment, request);
					isVisitorComment = true;
				}
			} else {
				return Result.create(403, "Password protected, please verify the password ！");
			}
		} else if (judgeResult == 0) {
			if (JwtUtils.judgeTokenIsExist(jwt)) {
				String subject;
				try {
					subject = JwtUtils.getTokenBody(jwt).getSubject();
				} catch (Exception e) {
					e.printStackTrace();
					return Result.create(403, "Token has expired, please re-verify the password");
				}
				if (subject.startsWith("admin:")) {
					String username = subject.replace("admin:", "");
					User admin = (User) userService.loadUserByUsername(username);
					if (admin == null) {
						return Result.create(403, "Token has expired, please log in again ！");
					}
					setAdminComment(comment, request, admin);
					isVisitorComment = false;
				} else {
					if (StringUtils.isEmpty(comment.getNickname(), comment.getEmail()) || comment.getNickname().length() > 15) {
						return Result.error("The parameter is wrong");
					}
					setVisitorComment(comment, request);
					isVisitorComment = true;
				}
			} else {
				if (StringUtils.isEmpty(comment.getNickname(), comment.getEmail()) || comment.getNickname().length() > 15) {
					return Result.error("The parameter is wrong");
				}
				setVisitorComment(comment, request);
				isVisitorComment = true;
			}
		}
		commentService.saveComment(comment);
		judgeSendMail(comment, isVisitorComment, parentComment);
		return Result.ok("Comment successful");
	}


	private void setAdminComment(Comment comment, HttpServletRequest request, User admin) {
		comment.setAdminComment(true);
		comment.setCreateTime(new Date());
		comment.setPublished(true);
		comment.setAvatar(admin.getAvatar());
		comment.setWebsite("/");
		comment.setNickname(admin.getNickname());
		comment.setEmail(admin.getEmail());
		comment.setIp(IpAddressUtils.getIpAddress(request));
		comment.setNotice(false);
	}


	private void setVisitorComment(Comment comment, HttpServletRequest request) {
		String commentNickname = comment.getNickname();
		try {
			if (QQInfoUtils.isQQNumber(commentNickname)) {
				comment.setQq(commentNickname);
				comment.setNickname(QQInfoUtils.getQQNickname(commentNickname));
				comment.setAvatar(QQInfoUtils.getQQAvatarURLByGithubUpload(commentNickname));
			} else {
				comment.setNickname(comment.getNickname().trim());
				setCommentRandomAvatar(comment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			comment.setNickname(comment.getNickname().trim());
			setCommentRandomAvatar(comment);
		}

		//set website
		String website = comment.getWebsite().trim();
		if (!"".equals(website) && !website.startsWith("http://") && !website.startsWith("https://")) {
			website = "http://" + website;
		}
		comment.setAdminComment(false);
		comment.setCreateTime(new Date());
		comment.setPublished(true);
		comment.setWebsite(website);
		comment.setEmail(comment.getEmail().trim());
		comment.setIp(IpAddressUtils.getIpAddress(request));
	}


	private void setCommentRandomAvatar(Comment comment) {
		long nicknameHash = HashUtils.getMurmurHash32(comment.getNickname());
		long num = nicknameHash % 6 + 1;
		String avatar = "/img/comment-avatar/" + num + ".jpg";
		comment.setAvatar(avatar);
	}


	private void judgeSendMail(Comment comment, boolean isVisitorComment, com.devteam.entity.Comment parentComment) {
		if (parentComment != null && !parentComment.getAdminComment() && parentComment.getNotice()) {
			sendMailToParentComment(parentComment, comment);
		}
		if (isVisitorComment) {
			sendMailToMe(comment);
		}
	}

	private void sendMailToParentComment(com.devteam.entity.Comment parentComment, Comment comment) {
		String path = "";
		String title = "";
		if (comment.getPage() == 0) {
			title = parentComment.getBlog().getTitle();
			path = "/blog/" + comment.getBlogId();
		} else if (comment.getPage() == 1) {
			title = "About";
			path = "/about";
		} else if (comment.getPage() == 2) {
			title = "User";
			path = "/friends";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("parentNickname", parentComment.getNickname());
		map.put("nickname", comment.getNickname());
		map.put("title", title);
		map.put("time", comment.getCreateTime());
		map.put("parentContent", parentComment.getContent());
		map.put("content", comment.getContent());
		map.put("url", websiteUrl + path);
		String toAccount = parentComment.getEmail();
		String subject = "You are " + bName + " New reply to comment";
		mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
	}


	private void sendMailToMe(Comment comment) {
		String path = "";
		String title = "";
		if (comment.getPage() == 0) {
			title = blogService.getTitleByBlogId(comment.getBlogId());
			path = "/blog/" + comment.getBlogId();
		} else if (comment.getPage() == 1) {
			title = "About";
			path = "/about";
		} else if (comment.getPage() == 2) {
			title = "User";
			path = "/friends";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("title", title);
		map.put("time", comment.getCreateTime());
		map.put("nickname", comment.getNickname());
		map.put("content", comment.getContent());
		map.put("ip", comment.getIp());
		map.put("email", comment.getEmail());
		map.put("status", comment.getPublished() ? "Public" : "Private");
		map.put("url", websiteUrl + path);
		map.put("manageUrl", cmsUrl + "/comments");
		String toAccount = mailProperties.getUsername();
		String subject = bName + " Receive new comment";
		mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "owner.html");
	}
}