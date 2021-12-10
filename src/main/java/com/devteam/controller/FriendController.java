package com.devteam.controller;

import com.devteam.annotation.VisitLogger;
import com.devteam.model.vo.Friend;
import com.devteam.model.vo.FriendInfo;
import com.devteam.model.vo.Result;
import com.devteam.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class FriendController {
	@Autowired
    FriendService friendService;


	@VisitLogger(behavior = "Visit page", content = "Page")
	@GetMapping("/friends")
	public Result friends() {
		List<Friend> friendList = friendService.getFriendVOList();
		FriendInfo friendInfo = friendService.getFriendInfo(true, true);
		Map<String, Object> map = new HashMap<>();
		map.put("friendList", friendList);
		map.put("friendInfo", friendInfo);
		return Result.ok("Get success", map);
	}


	@VisitLogger(behavior = "Click on the page")
	@PostMapping("/friend")
	public Result addViews(@RequestParam String nickname) {
		friendService.updateViewsByNickname(nickname);
		return Result.ok("Request succeeded");
	}
}
