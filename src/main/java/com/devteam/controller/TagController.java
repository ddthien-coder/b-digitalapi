package com.devteam.controller;

import com.devteam.annotation.VisitLogger;
import com.devteam.model.vo.BlogInfo;
import com.devteam.model.vo.PageResult;
import com.devteam.model.vo.Result;
import com.devteam.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TagController {
	@Autowired
    BlogService blogService;


	@VisitLogger(behavior = "View tags")
	@GetMapping("/tag")
	public Result tag(@RequestParam String tagName,
                      @RequestParam(defaultValue = "1") Integer pageNum) {
		PageResult<BlogInfo> pageResult = blogService.getBlogInfoListByTagNameAndIsPublished(tagName, pageNum);
		return Result.ok("Request succeeded", pageResult);
	}
}
