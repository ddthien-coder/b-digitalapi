package com.devteam.controller.admin;

import com.devteam.annotation.OperationLogger;
import com.devteam.entity.SiteSetting;
import com.devteam.model.vo.Result;
import com.devteam.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin")
public class SiteSettingAdminController {
	@Autowired
    SiteSettingService siteSettingService;

	@GetMapping("/siteSettings")
	public Result siteSettings() {
		Map<String, List<SiteSetting>> typeMap = siteSettingService.getList();
		return Result.ok("Request succeeded", typeMap);
	}

	@OperationLogger("update All")
	@PostMapping("/siteSettings")
	public Result updateAll(@RequestBody Map<String, Object> map) {
		List<LinkedHashMap> siteSettings = (List<LinkedHashMap>) map.get("settings");
		List<Integer> deleteIds = (List<Integer>) map.get("deleteIds");
		siteSettingService.updateSiteSetting(siteSettings, deleteIds);
		return Result.ok("update completed");
	}


	@GetMapping("/webTitleSuffix")
	public Result getWebTitleSuffix() {
		return Result.ok("Request succeeded", siteSettingService.getWebTitleSuffix());
	}
}
