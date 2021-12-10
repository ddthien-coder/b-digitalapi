package com.devteam.mapper;

import com.devteam.entity.Home;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HomeMapper {
    List<Home> getList();
    int updateHome(Home home);
    int deleteHomeById(Long id);
    int saveHome(Home home);
    Home getHomeById(Long id);
}
