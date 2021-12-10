package com.devteam.service;

import com.devteam.entity.User;

public interface UserService {
	User findUserByUsernameAndPassword(String username, String password);
}
