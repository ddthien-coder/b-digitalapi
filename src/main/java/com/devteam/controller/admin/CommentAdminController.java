package com.devteam.controller.admin;

import com.devteam.entity.Blog;
import com.devteam.entity.Comment;
import com.devteam.model.vo.Result;
import com.devteam.service.BlogService;
import com.devteam.service.CommentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devteam.annotation.OperationLogger;
import com.devteam.util.StringUtils;

import java.util.List;


@RestController
@RequestMapping("/admin")
public class CommentAdminController {
	@Autowired
    CommentService commentService;
	@Autowired
    BlogService blogService;

	@GetMapping("/comments")
	public Result comments(@RequestParam(defaultValue = "") Integer page,
                           @RequestParam(defaultValue = "") Long blogId,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "create_time desc";
		PageHelper.startPage(pageNum, pageSize, orderBy);
		List<Comment> comments = commentService.getListByPageAndParentCommentId(page, blogId, (long) -1);
		PageInfo<Comment> pageInfo = new PageInfo<>(comments);
		return Result.ok("Request succeeded", pageInfo);
	}


	@GetMapping("/blogIdAndTitle")
	public Result blogIdAndTitle() {
		List<Blog> blogs = blogService.getIdAndTitleList();
		return Result.ok("Request succeeded", blogs);
	}


	@OperationLogger("Update comment public status")
	@PutMapping("/comment/published")
	public Result updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
		commentService.updateCommentPublishedById(id, published);
		return Result.ok("Successful operation");
	}

	@OperationLogger("Update status of comment email reminder")
	@PutMapping("/comment/notice")
	public Result updateNotice(@RequestParam Long id, @RequestParam Boolean notice) {
		commentService.updateCommentNoticeById(id, notice);
		return Result.ok("Successful operation");
	}

	@OperationLogger("delete comment")
	@DeleteMapping("/comment")
	public Result delete(@RequestParam Long id) {
		commentService.deleteCommentById(id);
		return Result.ok("successfully deleted");
	}


	@OperationLogger("updateComment")
	@PutMapping("/comment")
	public Result updateComment(@RequestBody Comment comment) {
		if (StringUtils.isEmpty(comment.getNickname(), comment.getAvatar(), comment.getEmail(), comment.getIp(), comment.getContent())) {
			return Result.error("The parameter is wrong");
		}
		commentService.updateComment(comment);
		return Result.ok("Comment modified successfully");
	}
}
