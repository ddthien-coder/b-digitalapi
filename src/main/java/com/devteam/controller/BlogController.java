package com.devteam.controller;

import com.devteam.annotation.VisitLogger;
import com.devteam.entity.User;
import com.devteam.model.dto.BlogPassword;
import com.devteam.service.BlogService;
import com.devteam.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devteam.model.vo.BlogDetail;
import com.devteam.model.vo.BlogInfo;
import com.devteam.model.vo.PageResult;
import com.devteam.model.vo.Result;
import com.devteam.model.vo.SearchBlog;
import com.devteam.util.JwtUtils;
import com.devteam.util.StringUtils;

import java.util.List;


@RestController
public class BlogController {
	@Autowired
    BlogService blogService;
	@Autowired
    UserServiceImpl userService;


	@VisitLogger(behavior = "Visit page", content = "page")
	@GetMapping("/blogs")
	public Result blogs(@RequestParam(defaultValue = "1") Integer pageNum) {
		PageResult<BlogInfo> pageResult = blogService.getBlogInfoListByIsPublished(pageNum);
		return Result.ok("Request succeeded", pageResult);
	}


	@VisitLogger(behavior = "View blog")
	@GetMapping("/blog")
	public Result getBlog(@RequestParam Long id,
	                      @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		BlogDetail blog = blogService.getBlogByIdAndIsPublished(id);
		if (!"".equals(blog.getPassword())) {
			if (JwtUtils.judgeTokenIsExist(jwt)) {
				try {
					String subject = JwtUtils.getTokenBody(jwt).getSubject();
					if (subject.startsWith("admin:")) {
						String username = subject.replace("admin:", "");
						User admin = (User) userService.loadUserByUsername(username);
						if (admin == null) {
							return Result.create(403, "The token has expired, please log in again！");
						}
					} else {
						Long tokenBlogId = Long.parseLong(subject);
						if (!tokenBlogId.equals(id)) {
							return Result.create(403, "Token does not match, please re-verify the password！");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return Result.create(403, "Token has expired, please re-verify the password！");
				}
			} else {
				return Result.create(403, "This showrrel is password protected, please verify the password ！");
			}
			blog.setPassword("");
		}
		blogService.updateViewsToRedis(id);
		return Result.ok("Get success", blog);
	}


	@VisitLogger(behavior = "Verify password")
	@PostMapping("/checkBlogPassword")
	public Result checkBlogPassword(@RequestBody BlogPassword blogPassword) {
		String password = blogService.getBlogPassword(blogPassword.getBlogId());
		if (password.equals(blogPassword.getPassword())) {
			String jwt = JwtUtils.generateToken(blogPassword.getBlogId().toString(), 1000 * 3600 * 24 * 30L);
			return Result.ok("Password is correct", jwt);
		} else {
			return Result.create(403, "Wrong password");
		}
	}


	@VisitLogger(behavior = "Search")
	@GetMapping("/searchBlog")
	public Result searchBlog(@RequestParam String query) {
		if (StringUtils.isEmpty(query) || StringUtils.hasSpecialChar(query) || query.trim().length() > 20) {
			return Result.error("Parameter error");
		}
		List<SearchBlog> searchBlogs = blogService.getSearchBlogListByQueryAndIsPublished(query.trim());
		return Result.ok("Get success", searchBlogs);
	}
}
