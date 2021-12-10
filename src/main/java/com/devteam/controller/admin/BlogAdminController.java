package com.devteam.controller.admin;

import com.devteam.annotation.OperationLogger;
import com.devteam.entity.Blog;
import com.devteam.entity.Category;
import com.devteam.entity.Tag;
import com.devteam.entity.User;
import com.devteam.model.dto.BlogVisibility;
import com.devteam.model.vo.Result;
import com.devteam.service.BlogService;
import com.devteam.service.CategoryService;
import com.devteam.service.CommentService;
import com.devteam.service.TagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devteam.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin")
public class BlogAdminController {
	@Autowired
    BlogService blogService;
	@Autowired
    CategoryService categoryService;
	@Autowired
    TagService tagService;
	@Autowired
    CommentService commentService;


	@GetMapping("/blogs")
	public Result blogs(@RequestParam(defaultValue = "") String title,
                        @RequestParam(defaultValue = "") Integer categoryId,
                        @RequestParam(defaultValue = "1") Integer pageNum,
                        @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "create_time desc";
		PageHelper.startPage(pageNum, pageSize, orderBy);
		PageInfo<Blog> pageInfo = new PageInfo<>(blogService.getListByTitleAndCategoryId(title, categoryId));
		List<Category> categories = categoryService.getCategoryList();
		Map<String, Object> map = new HashMap<>();
		map.put("blogs", pageInfo);
		map.put("categories", categories);
		return Result.ok("Request succeeded", map);
	}

	@OperationLogger("Delete blog")
	@DeleteMapping("/blog")
	public Result delete(@RequestParam Long id) {
		blogService.deleteBlogTagByBlogId(id);
		blogService.deleteBlogById(id);
		commentService.deleteCommentsByBlogId(id);
		return Result.ok("successfully deleted");
	}


	@GetMapping("/categoryAndTag")
	public Result categoryAndTag() {
		List<Category> categories = categoryService.getCategoryList();
		List<Tag> tags = tagService.getTagList();
		Map<String, Object> map = new HashMap<>();
		map.put("categories", categories);
		map.put("tags", tags);
		return Result.ok("Request succeeded", map);
	}


	@OperationLogger("Update blog sticking status")
	@PutMapping("/blog/top")
	public Result updateTop(@RequestParam Long id, @RequestParam Boolean top) {
		blogService.updateBlogTopById(id, top);
		return Result.ok("Successful operation");
	}


	@OperationLogger("Update blog recommendation status")
	@PutMapping("/blog/recommend")
	public Result updateRecommend(@RequestParam Long id, @RequestParam Boolean recommend) {
		blogService.updateBlogRecommendById(id, recommend);
		return Result.ok("Successful operation");
	}


	@OperationLogger("Update blog visibility status")
	@PutMapping("blog/{id}/visibility")
	public Result updateVisibility(@PathVariable Long id, @RequestBody BlogVisibility blogVisibility) {
		blogService.updateBlogVisibilityById(id, blogVisibility);
		return Result.ok("Successful operation");
	}


	@GetMapping("/blog")
	public Result getBlog(@RequestParam Long id) {
		Blog blog = blogService.getBlogById(id);
		return Result.ok("Get success", blog);
	}

	@OperationLogger("Post a blog")
	@PostMapping("/blog")
	public Result saveBlog(@RequestBody com.devteam.model.dto.Blog blog) {
		return getResult(blog, "save");
	}

	@OperationLogger("Update blog")
	@PutMapping("/blog")
	public Result updateBlog(@RequestBody com.devteam.model.dto.Blog blog) {
		return getResult(blog, "update");
	}


	private Result getResult(com.devteam.model.dto.Blog blog, String type) {
		if (StringUtils.isEmpty(blog.getTitle(), blog.getVideoId(), blog.getContent(), blog.getDescription())
				|| blog.getWords() == null || blog.getWords() < 0) {
			return Result.error("The parameter is wrong");
		}

		Object cate = blog.getCate();
		if (cate == null) {
			return Result.error("Category cannot be empty");
		}
		if (cate instanceof Integer) {
			Category c = categoryService.getCategoryById(((Integer) cate).longValue());
			blog.setCategory(c);
		} else if (cate instanceof String) {
			Category category = categoryService.getCategoryByName((String) cate);
			if (category != null) {
				return Result.error("Cannot add existing categories");
			}
			Category c = new Category();
			c.setName((String) cate);
			categoryService.saveCategory(c);
			blog.setCategory(c);
		} else {
			return Result.error("Incorrect");
		}

		List<Object> tagList = blog.getTagList();
		List<Tag> tags = new ArrayList<>();
		for (Object t : tagList) {
			if (t instanceof Integer) {
				Tag tag = tagService.getTagById(((Integer) t).longValue());
				tags.add(tag);
			} else if (t instanceof String) {
				Tag tag1 = tagService.getTagByName((String) t);
				if (tag1 != null) {
					return Result.error("Cannot add existing tags");
				}
				Tag tag = new Tag();
				tag.setName((String) t);
				tagService.saveTag(tag);
				tags.add(tag);
			} else {
				return Result.error("Incorrect label");
			}
		}

		Date date = new Date();
		if (blog.getReadTime() == null || blog.getReadTime() < 0) {
			blog.setReadTime((int) Math.round(blog.getWords() / 200.0));
		}
		if (blog.getViews() == null || blog.getViews() < 0) {
			blog.setViews(0);
		}
		if ("save".equals(type)) {
			blog.setCreateTime(date);
			blog.setUpdateTime(date);
			User user = new User();
			user.setId((long) 1);
			blog.setUser(user);

			blogService.saveBlog(blog);
			for (Tag t : tags) {
				blogService.saveBlogTag(blog.getId(), t.getId());
			}
			return Result.ok("Added successfully");
		} else {
			blog.setUpdateTime(date);
			blogService.updateBlog(blog);
			blogService.deleteBlogTagByBlogId(blog.getId());
			for (Tag t : tags) {
				blogService.saveBlogTag(blog.getId(), t.getId());
			}
			return Result.ok("Update completed");
		}
	}
}
