package com.devteam.controller.admin;

import com.devteam.entity.Tag;
import com.devteam.model.vo.Result;
import com.devteam.service.BlogService;
import com.devteam.service.TagService;
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
import com.devteam.util.StringUtils;


@RestController
@RequestMapping("/admin")
public class TagAdminController {
	@Autowired
    BlogService blogService;
	@Autowired
    TagService tagService;


	@GetMapping("/tags")
	public Result tags(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "id desc";
		PageHelper.startPage(pageNum, pageSize, orderBy);
		PageInfo<Tag> pageInfo = new PageInfo<>(tagService.getTagList());
		return Result.ok("Request succeeded", pageInfo);
	}


	@OperationLogger("save Tag")
	@PostMapping("/tag")
	public Result saveTag(@RequestBody Tag tag) {
		return getResult(tag, "save");
	}


	@OperationLogger("update Tag")
	@PutMapping("/tag")
	public Result updateTag(@RequestBody Tag tag) {
		return getResult(tag, "update");
	}


	private Result getResult(Tag tag, String type) {
		if (StringUtils.isEmpty(tag.getName())) {
			return Result.error("Parameter cannot be empty");
		}
		Tag tag1 = tagService.getTagByName(tag.getName());
		if (tag1 != null && !tag1.getId().equals(tag.getId())) {
			return Result.error("The label already exists");
		}
		if ("save".equals(type)) {
			tagService.saveTag(tag);
			return Result.ok("Added successfully");
		} else {
			tagService.updateTag(tag);
			return Result.ok("update completed");
		}
	}


	@OperationLogger("delete tag")
	@DeleteMapping("/tag")
	public Result delete(@RequestParam Long id) {
		int num = blogService.countBlogByTagId(id);
		if (num != 0) {
			return Result.error("There are already  associated with this label and cannot be deleted");
		}
		tagService.deleteTagById(id);
		return Result.ok("successfully deleted");
	}
}
