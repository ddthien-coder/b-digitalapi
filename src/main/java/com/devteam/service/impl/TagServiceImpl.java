package com.devteam.service.impl;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.Tag;
import com.devteam.exception.NotFoundException;
import com.devteam.exception.PersistenceException;
import com.devteam.mapper.TagMapper;
import com.devteam.service.RedisService;
import com.devteam.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TagServiceImpl implements TagService {
	@Autowired
    TagMapper tagMapper;
	@Autowired
    RedisService redisService;

	@Override
	public List<Tag> getTagList() {
		return tagMapper.getTagList();
	}

	@Override
	public List<Tag> getTagListNotId() {
		String redisKey = RedisKeyConfig.TAG_CLOUD_LIST;
		List<Tag> tagListFromRedis = redisService.getListByValue(redisKey);
		if (tagListFromRedis != null) {
			return tagListFromRedis;
		}
		List<Tag> tagList = tagMapper.getTagListNotId();
		redisService.saveListToValue(redisKey, tagList);
		return tagList;
	}

	@Override
	public List<Tag> getTagListByBlogId(Long blogId) {
		return tagMapper.getTagListByBlogId(blogId);
	}

	@Transactional
	@Override
	public void saveTag(Tag tag) {
		if (tagMapper.saveTag(tag) != 1) {
			throw new PersistenceException("Failed to add label");
		}
		redisService.deleteCacheByKey(RedisKeyConfig.TAG_CLOUD_LIST);
	}

	@Override
	public Tag getTagById(Long id) {
		Tag tag = tagMapper.getTagById(id);
		if (tag == null) {
			throw new NotFoundException("Label does not exist");
		}
		return tag;
	}

	@Override
	public Tag getTagByName(String name) {
		return tagMapper.getTagByName(name);
	}

	@Transactional
	@Override
	public void deleteTagById(Long id) {
		if (tagMapper.deleteTagById(id) != 1) {
			throw new PersistenceException("Label deletion failed");
		}
		redisService.deleteCacheByKey(RedisKeyConfig.TAG_CLOUD_LIST);
	}

	@Transactional
	@Override
	public void updateTag(Tag tag) {
		if (tagMapper.updateTag(tag) != 1) {
			throw new PersistenceException("Label update failed");
		}
		redisService.deleteCacheByKey(RedisKeyConfig.TAG_CLOUD_LIST);
		redisService.deleteCacheByKey(RedisKeyConfig.HOME_BLOG_INFO_LIST);
	}
}
