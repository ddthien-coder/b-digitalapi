package com.devteam.mapper;

import com.devteam.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UserMapper {
	User findByUsername(String username);
}
