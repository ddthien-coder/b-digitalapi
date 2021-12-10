package com.devteam.controller;

import com.devteam.annotation.VisitLogger;
import com.devteam.model.vo.Result;
import com.devteam.service.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AboutController {
	@Autowired
    AboutService aboutService;


	@VisitLogger(behavior = "Visit page", content = "about")
	@GetMapping("/about")
	public Result about() {
		return Result.ok("Get success", aboutService.getAboutInfo());
	}
}
