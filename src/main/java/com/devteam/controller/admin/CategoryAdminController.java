package com.devteam.controller.admin;

import com.devteam.annotation.OperationLogger;
import com.devteam.entity.Category;
import com.devteam.model.vo.Result;
import com.devteam.service.BlogService;
import com.devteam.service.CategoryService;
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
import com.devteam.util.StringUtils;


@RestController
@RequestMapping("/admin")
public class CategoryAdminController {
	@Autowired
    BlogService blogService;
	@Autowired
    CategoryService categoryService;


	@GetMapping("/categories")
	public Result categories(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "id desc";
		PageHelper.startPage(pageNum, pageSize, orderBy);
		PageInfo<Category> pageInfo = new PageInfo<>(categoryService.getCategoryList());
		return Result.ok("Request succeeded", pageInfo);
	}


	@OperationLogger("add category")
	@PostMapping("/category")
	public Result saveCategory(@RequestBody Category category) {
		return getResult(category, "save");
	}


	@OperationLogger("update category")
	@PutMapping("/category")
	public Result updateCategory(@RequestBody Category category) {
		return getResult(category, "update");
	}


	private Result getResult(Category category, String type) {
		if (StringUtils.isEmpty(category.getName())) {
			return Result.error("Category name cannot be empty");
		}
		Category category1 = categoryService.getCategoryByName(category.getName());
		if (category1 != null && !category1.getId().equals(category.getId())) {
			return Result.error("This category already exists");
		}
		if ("save".equals(type)) {
			categoryService.saveCategory(category);
			return Result.ok("Category added successfully");
		} else {
			categoryService.updateCategory(category);
			return Result.ok("updated successfully");
		}
	}


	@OperationLogger("delete category")
	@DeleteMapping("/category")
	public Result delete(@RequestParam Long id) {
		int num = blogService.countBlogByCategoryId(id);
		if (num != 0) {
			return Result.error("There are already blogs associated with this category and cannot be deleted ");
		}
		categoryService.deleteCategoryById(id);
		return Result.ok("Successfully deleted");
	}
}
