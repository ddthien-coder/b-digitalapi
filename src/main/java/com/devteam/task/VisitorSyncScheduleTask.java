package com.devteam.task;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.CityVisitor;
import com.devteam.entity.VisitRecord;
import com.devteam.model.dto.VisitLogUuidTime;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.devteam.service.CityVisitorService;
import com.devteam.service.RedisService;
import com.devteam.service.VisitLogService;
import com.devteam.service.VisitRecordService;
import com.devteam.service.VisitorService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
public class VisitorSyncScheduleTask {
	@Autowired
    RedisService redisService;
	@Autowired
    VisitLogService visitLogService;
	@Autowired
    VisitorService visitorService;
	@Autowired
    VisitRecordService visitRecordService;
	@Autowired
    CityVisitorService cityVisitorService;


	public void syncVisitInfoToDatabase() {
		redisService.deleteCacheByKey(RedisKeyConfig.IDENTIFICATION_SET);
		List<VisitLogUuidTime> yesterdayLogList = visitLogService.getUUIDAndCreateTimeByYesterday();
		Set<String> uuidSet = new HashSet<>();
		Map<String, Integer> PVMap = new HashMap<>();
		Map<String, Date> lastTimeMap = new HashMap<>();
		yesterdayLogList.forEach(log -> {
			String uuid = log.getUuid();
			Date createTime = log.getTime();
			uuidSet.add(uuid);
			PVMap.merge(uuid, 1, Integer::sum);
			lastTimeMap.putIfAbsent(uuid, createTime);
		});
		int pv = yesterdayLogList.size();
		int uv = uuidSet.size();
		String date = new SimpleDateFormat("MM-dd").format(DateUtils.addDays(new Date(), -1));
		visitRecordService.saveVisitRecord(new VisitRecord(pv, uv, date));
		uuidSet.forEach(uuid -> {
			VisitLogUuidTime uuidPVTimeDTO = new VisitLogUuidTime(uuid, lastTimeMap.get(uuid), PVMap.get(uuid));
			visitorService.updatePVAndLastTimeByUUID(uuidPVTimeDTO);
		});
		List<String> ipSource = visitorService.getNewVisitorIpSourceByYesterday();
		Map<String, Integer> cityVisitorMap = new HashMap<>();
		ipSource.forEach(i -> {
			if (i.startsWith("english")) {
				String[] split = i.split("\\|");
				if (split.length == 4) {
					String city = split[2];
					cityVisitorMap.merge(city, 1, Integer::sum);
				}
			}
		});
		cityVisitorMap.forEach((k, v) -> {
			cityVisitorService.saveCityVisitor(new CityVisitor(k, v));
		});
	}
}
