package com.devteam.controller.admin;

import com.devteam.annotation.OperationLogger;
import com.devteam.model.vo.Result;
import com.devteam.service.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/admin")
public class AboutAdminController {
	@Autowired
    AboutService aboutService;


	@GetMapping("/about")
	public Result about() {
		return Result.ok("Request succeeded", aboutService.getAboutSetting());
	}


	@OperationLogger("Modify About Me Page")
	@PutMapping("/about")
	public Result updateAbout(@RequestBody Map<String, String> map) {
		aboutService.updateAbout(map);
		return Result.ok("Successfully modified");
	}
}
