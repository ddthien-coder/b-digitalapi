package com.devteam.controller;

import com.devteam.entity.User;
import com.devteam.model.dto.LoginInfo;
import com.devteam.model.vo.Result;
import com.devteam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.devteam.util.JwtUtils;

import java.util.HashMap;
import java.util.Map;


@RestController
public class LoginController {
	@Autowired
    UserService userService;


	@PostMapping("/login")
	public Result login(@RequestBody LoginInfo loginInfo) {
		User user = userService.findUserByUsernameAndPassword(loginInfo.getUsername(), loginInfo.getPassword());
		if (!"ROLE_admin".equals(user.getRole())) {
			return Result.create(403, "No permission");
		}
		user.setPassword(null);
		String jwt = JwtUtils.generateToken("admin:" + user.getUsername());
		Map<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("token", jwt);
		return Result.ok("login successful", map);
	}
}
