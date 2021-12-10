package com.devteam.controller;

import com.devteam.annotation.AccessLimit;
import com.devteam.annotation.VisitLogger;
import com.devteam.entity.Moment;
import com.devteam.entity.User;
import com.devteam.model.vo.PageResult;
import com.devteam.model.vo.Result;
import com.devteam.service.MomentService;
import com.devteam.service.impl.UserServiceImpl;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devteam.util.JwtUtils;


@RestController
public class MomentController {
	@Autowired
    MomentService momentService;
	@Autowired
    UserServiceImpl userService;


	@VisitLogger(behavior = "Visit page", content = "dynamic")
	@GetMapping("/moments")
	public Result moments(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		boolean adminIdentity = false;
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String subject = JwtUtils.getTokenBody(jwt).getSubject();
				if (subject.startsWith("admin:")) {
					String username = subject.replace("admin:", "");
					User admin = (User) userService.loadUserByUsername(username);
					if (admin != null) {
						adminIdentity = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PageInfo<Moment> pageInfo = new PageInfo<>(momentService.getMomentVOList(pageNum, adminIdentity));
		PageResult<Moment> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
		return Result.ok("Get success", pageResult);
	}


	@AccessLimit(seconds = 86400, maxCount = 1, msg = "Don't repeat likes")
	@VisitLogger(behavior = "Like dynamic")
	@PostMapping("/moment/like/{id}")
	public Result like(@PathVariable Long id) {
		momentService.addLikeByMomentId(id);
		return Result.ok("Like success");
	}
}
