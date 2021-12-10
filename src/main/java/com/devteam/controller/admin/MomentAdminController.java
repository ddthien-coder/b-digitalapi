package com.devteam.controller.admin;

import com.devteam.annotation.OperationLogger;
import com.devteam.entity.Moment;
import com.devteam.model.vo.Result;
import com.devteam.service.MomentService;
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

import java.util.Date;


@RestController
@RequestMapping("/admin")
public class MomentAdminController {
	@Autowired
    MomentService momentService;

	@GetMapping("/moments")
	public Result moments(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "create_time desc";
		PageHelper.startPage(pageNum, pageSize, orderBy);
		PageInfo<Moment> pageInfo = new PageInfo<>(momentService.getMomentList());
		return Result.ok("Request succeeded", pageInfo);
	}


	@OperationLogger("Update dynamic public status")
	@PutMapping("/moment/published")
	public Result updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
		momentService.updateMomentPublishedById(id, published);
		return Result.ok("Successful operation");
	}


	@GetMapping("/moment")
	public Result moment(@RequestParam Long id) {
		return Result.ok("Get success", momentService.getMomentById(id));
	}


	@OperationLogger("delete moment")
	@DeleteMapping("/moment")
	public Result deleteMoment(@RequestParam Long id) {
		momentService.deleteMomentById(id);
		return Result.ok("successfully deleted");
	}


	@OperationLogger("save moment")
	@PostMapping("/moment")
	public Result saveMoment(@RequestBody Moment moment) {
		if (moment.getCreateTime() == null) {
			moment.setCreateTime(new Date());
		}
		momentService.saveMoment(moment);
		return Result.ok("Added successfully");
	}


	@OperationLogger("update moment")
	@PutMapping("/moment")
	public Result updateMoment(@RequestBody Moment moment) {
		if (moment.getCreateTime() == null) {
			moment.setCreateTime(new Date());
		}
		momentService.updateMoment(moment);
		return Result.ok("Successfully modified");
	}
}
