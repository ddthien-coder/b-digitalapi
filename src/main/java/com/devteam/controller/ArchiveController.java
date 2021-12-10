package com.devteam.controller;

import com.devteam.annotation.VisitLogger;
import com.devteam.model.vo.Result;
import com.devteam.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class ArchiveController {
	@Autowired
    BlogService blogService;


	@VisitLogger(behavior = "Visit page", content = "showrrel")
	@GetMapping("/archives")
	public Result archives() {
		Map<String, Object> archiveBlogMap = blogService.getArchiveBlogAndCountByIsPublished();
		return Result.ok("Request succeeded", archiveBlogMap);
	}
}
