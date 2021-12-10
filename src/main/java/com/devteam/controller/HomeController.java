package com.devteam.controller;

import com.devteam.annotation.VisitLogger;
import com.devteam.model.vo.Result;
import com.devteam.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @Autowired
    HomeService homeService;
    @VisitLogger(behavior = "View home")
    @GetMapping("/home")
    public Result home() {
       return Result.ok("Get success", homeService.getList());
    }
}
