package com.devteam.service.impl;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.Home;
import com.devteam.exception.NotFoundException;
import com.devteam.exception.PersistenceException;
import com.devteam.mapper.HomeMapper;
import com.devteam.service.HomeService;
import com.devteam.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    HomeMapper homeMapper;
    @Autowired
    RedisService redisService;

    @Override
    public List<Home> getList() {
        return homeMapper.getList();
    }

    @Transactional
    @Override
    public void updateHome(Home home) {
        if(homeMapper.updateHome(home) != 1) {
            throw new PersistenceException("update failed");
        }
        redisService.deleteCacheByKey(RedisKeyConfig.HOME_LIST);
        redisService.deleteCacheByKey(RedisKeyConfig.HOME_BLOG_INFO_LIST);
    }

    @Transactional
    @Override
    public void deleteHomeById(Long id) {
        if(homeMapper.deleteHomeById(id) != 1) {
            throw new PersistenceException("Failed to delete");
        }
        redisService.deleteCacheByKey(RedisKeyConfig.HOME_LIST);
    }

    @Transactional
    @Override
    public void saveHome(Home home) {
        if(homeMapper.saveHome(home) != 1) {
            throw new PersistenceException("Failed to add");
        }
        redisService.deleteCacheByKey(RedisKeyConfig.HOME_LIST);
    }

    @Override
    public Home getHomeById(Long id) {
        Home home = homeMapper.getHomeById(id);
        if(home == null) {
            throw new NotFoundException("Does not exist");
        }
        return home;
    }
}
