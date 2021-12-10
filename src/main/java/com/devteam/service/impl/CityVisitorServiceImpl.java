package com.devteam.service.impl;

import com.devteam.entity.CityVisitor;
import com.devteam.mapper.CityVisitorMapper;
import com.devteam.service.CityVisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CityVisitorServiceImpl implements CityVisitorService {
	@Autowired
    CityVisitorMapper cityVisitorMapper;

	@Override
	public void saveCityVisitor(CityVisitor cityVisitor) {
		cityVisitorMapper.saveCityVisitor(cityVisitor);
	}
}
