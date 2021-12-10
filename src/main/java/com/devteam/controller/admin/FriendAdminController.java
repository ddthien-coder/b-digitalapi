package com.devteam.controller.admin;

import com.devteam.entity.Friend;
import com.devteam.model.vo.Result;
import com.devteam.service.FriendService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devteam.annotation.OperationLogger;

import java.util.Map;


@RestController
@RequestMapping("/admin")
public class FriendAdminController {
	@Autowired
    FriendService friendService;


	@GetMapping("/friends")
	public Result friends(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "create_time asc";
		PageHelper.startPage(pageNum, pageSize, orderBy);
		PageInfo<Friend> pageInfo = new PageInfo<>(friendService.getFriendList());
		return Result.ok("Request succeeded", pageInfo);
	}


	@OperationLogger("Update the public status")
	@PutMapping("/friend/published")
	public Result updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
		friendService.updateFriendPublishedById(id, published);
		return Result.ok("Successful operation");
	}


	@OperationLogger("Add")
	@PostMapping("/friend")
	public Result saveFriend(@RequestBody Friend friend) {
		friendService.saveFriend(friend);
		return Result.ok("Added successfully");
	}

	@OperationLogger("update")
	@PutMapping("/friend")
	public Result updateFriend(@RequestBody com.devteam.model.dto.Friend friend) {
		friendService.updateFriend(friend);
		return Result.ok("Successfully modified");
	}


	@OperationLogger("delete")
	@DeleteMapping("/friend")
	public Result deleteFriend(@RequestParam Long id) {
		friendService.deleteFriend(id);
		return Result.ok("successfully deleted");
	}


	@GetMapping("/friendInfo")
	public Result friendInfo() {
		return Result.ok("请求成功", friendService.getFriendInfo(false, false));
	}


	@OperationLogger("Modify the open status of comments on the friend chain page ")
	@PutMapping("/friendInfo/commentEnabled")
	public Result updateFriendInfoCommentEnabled(@RequestParam Boolean commentEnabled) {
		friendService.updateFriendInfoCommentEnabled(commentEnabled);
		return Result.ok("Successfully modified");
	}


	@OperationLogger("Modify friend link page information")
	@PutMapping("/friendInfo/content")
	public Result updateFriendInfoContent(@RequestBody Map map) {
		friendService.updateFriendInfoContent((String) map.get("content"));
		return Result.ok("Successfully modified");
	}
}
