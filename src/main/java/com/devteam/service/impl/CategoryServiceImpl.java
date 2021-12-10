package com.devteam.service.impl;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.Category;
import com.devteam.exception.NotFoundException;
import com.devteam.exception.PersistenceException;
import com.devteam.mapper.CategoryMapper;
import com.devteam.service.CategoryService;
import com.devteam.service.RedisService;
import com.devteam.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
    CategoryMapper categoryMapper;
	@Autowired
    TagService tagService;
	@Autowired
    RedisService redisService;

	@Override
	public List<Category> getCategoryList() {
		return categoryMapper.getCategoryList();
	}

	@Override
	public List<Category> getCategoryNameList() {
		String redisKey = RedisKeyConfig.CATEGORY_NAME_LIST;
		List<Category> categoryListFromRedis = redisService.getListByValue(redisKey);
		if (categoryListFromRedis != null) {
			return categoryListFromRedis;
		}
		List<Category> categoryList = categoryMapper.getCategoryNameList();
		redisService.saveListToValue(redisKey, categoryList);
		return categoryList;
	}

	@Transactional
	@Override
	public void saveCategory(Category category) {
		if (categoryMapper.saveCategory(category) != 1) {
			throw new PersistenceException("Failed to add category");
		}
		redisService.deleteCacheByKey(RedisKeyConfig.CATEGORY_NAME_LIST);
	}

	@Override
	public Category getCategoryById(Long id) {
		Category category = categoryMapper.getCategoryById(id);
		if (category == null) {
			throw new NotFoundException("Does not exist");
		}
		return category;
	}

	@Override
	public Category getCategoryByName(String name) {
		return categoryMapper.getCategoryByName(name);
	}

	@Transactional
	@Override
	public void deleteCategoryById(Long id) {
		if (categoryMapper.deleteCategoryById(id) != 1) {
			throw new PersistenceException("Failed to delete category");
		}
		redisService.deleteCacheByKey(RedisKeyConfig.CATEGORY_NAME_LIST);
	}

	@Transactional
	@Override
	public void updateCategory(Category category) {
		if (categoryMapper.updateCategory(category) != 1) {
			throw new PersistenceException("update failed");
		}
		redisService.deleteCacheByKey(RedisKeyConfig.CATEGORY_NAME_LIST);
		redisService.deleteCacheByKey(RedisKeyConfig.HOME_BLOG_INFO_LIST);
	}
}
