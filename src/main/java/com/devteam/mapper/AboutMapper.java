package com.devteam.mapper;

import com.devteam.entity.About;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface AboutMapper {
	List<About> getList();

	int updateAbout(String name, String value);

	String getAboutCommentEnabled();
}
