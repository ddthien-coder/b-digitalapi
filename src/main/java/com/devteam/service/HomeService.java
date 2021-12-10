package com.devteam.service;

import com.devteam.entity.Home;

import java.util.List;

public interface HomeService {
    List<Home> getList();
    void updateHome(Home home);
    void deleteHomeById(Long id);
    void saveHome(Home home);
    Home getHomeById(Long id);
}
