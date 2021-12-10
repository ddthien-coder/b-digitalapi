package com.devteam.service.impl;

import com.devteam.entity.OperationLog;
import com.devteam.exception.PersistenceException;
import com.devteam.mapper.OperationLogMapper;
import com.devteam.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devteam.util.IpAddressUtils;
import com.devteam.util.UserAgentUtils;

import java.util.List;
import java.util.Map;


@Service
public class OperationLogServiceImpl implements OperationLogService {
	@Autowired
    OperationLogMapper operationLogMapper;
	@Autowired
	UserAgentUtils userAgentUtils;

	@Override
	public List<OperationLog> getOperationLogListByDate(String startDate, String endDate) {
		return operationLogMapper.getOperationLogListByDate(startDate, endDate);
	}

	@Transactional
	@Override
	public void saveOperationLog(OperationLog log) {
		String ipSource = IpAddressUtils.getCityInfo(log.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		log.setIpSource(ipSource);
		log.setOs(os);
		log.setBrowser(browser);
		if (operationLogMapper.saveOperationLog(log) != 1) {
			throw new PersistenceException("Log addition failed");
		}
	}

	@Transactional
	@Override
	public void deleteOperationLogById(Long id) {
		if (operationLogMapper.deleteOperationLogById(id) != 1) {
			throw new PersistenceException("Failed to delete log");
		}
	}
}
