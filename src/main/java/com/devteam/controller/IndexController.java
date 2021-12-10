package com.devteam.controller;

import com.devteam.entity.Category;
import com.devteam.entity.Tag;
import com.devteam.model.vo.NewBlog;
import com.devteam.model.vo.RandomBlog;
import com.devteam.model.vo.Result;
import com.devteam.service.BlogService;
import com.devteam.service.CategoryService;
import com.devteam.service.SiteSettingService;
import com.devteam.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class IndexController {
	@Autowired
    SiteSettingService siteSettingService;
	@Autowired
    BlogService blogService;
	@Autowired
    CategoryService categoryService;
	@Autowired
    TagService tagService;


	@GetMapping("/site")
	public Result site() {
		Map<String, Object> map = siteSettingService.getSiteInfo();
		List<NewBlog> newBlogList = blogService.getNewBlogListByIsPublished();
		List<Category> categoryList = categoryService.getCategoryNameList();
		List<Tag> tagList = tagService.getTagListNotId();
		List<RandomBlog> randomBlogList = blogService.getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend();
		map.put("newBlogList", newBlogList);
		map.put("categoryList", categoryList);
		map.put("tagList", tagList);
		map.put("randomBlogList", randomBlogList);
		return Result.ok("Request succeeded", map);
	}
}
