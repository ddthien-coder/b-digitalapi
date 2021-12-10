package com.devteam.service.impl;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.Visitor;
import com.devteam.exception.PersistenceException;
import com.devteam.mapper.VisitorMapper;
import com.devteam.model.dto.VisitLogUuidTime;
import com.devteam.service.RedisService;
import com.devteam.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devteam.util.IpAddressUtils;
import com.devteam.util.UserAgentUtils;

import java.util.List;
import java.util.Map;


@Service
public class VisitorServiceImpl implements VisitorService {
	@Autowired
    VisitorMapper visitorMapper;
	@Autowired
    RedisService redisService;
	@Autowired
	UserAgentUtils userAgentUtils;

	@Override
	public List<Visitor> getVisitorListByDate(String startDate, String endDate) {
		return visitorMapper.getVisitorListByDate(startDate, endDate);
	}

	@Override
	public List<String> getNewVisitorIpSourceByYesterday() {
		return visitorMapper.getNewVisitorIpSourceByYesterday();
	}

	@Override
	public boolean hasUUID(String uuid) {
		return visitorMapper.hasUUID(uuid) == 0 ? false : true;
	}

	@Transactional
	@Override
	public void saveVisitor(Visitor visitor) {
		String ipSource = IpAddressUtils.getCityInfo(visitor.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(visitor.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		visitor.setIpSource(ipSource);
		visitor.setOs(os);
		visitor.setBrowser(browser);
		if (visitorMapper.saveVisitor(visitor) != 1) {
				throw new PersistenceException("Failed to add guest");
		}
	}

	@Override
	public void updatePVAndLastTimeByUUID(VisitLogUuidTime dto) {
		visitorMapper.updatePVAndLastTimeByUUID(dto);
	}

	@Transactional
	@Override
	public void deleteVisitor(Long id, String uuid) {
		redisService.deleteValueBySet(RedisKeyConfig.IDENTIFICATION_SET, uuid);
		if (visitorMapper.deleteVisitorById(id) != 1) {
			throw new PersistenceException("Failed to delete guest");
		}
	}
}
