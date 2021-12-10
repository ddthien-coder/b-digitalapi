package com.devteam.service.impl;

import com.devteam.entity.VisitRecord;
import com.devteam.mapper.VisitRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.devteam.service.VisitRecordService;


@Service
public class VisitRecordServiceImpl implements VisitRecordService {
	@Autowired
    VisitRecordMapper visitRecordMapper;

	@Override
	public void saveVisitRecord(VisitRecord visitRecord) {
		visitRecordMapper.saveVisitRecord(visitRecord);
	}
}
