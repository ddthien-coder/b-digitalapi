package com.devteam.service.impl;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.About;
import com.devteam.mapper.AboutMapper;
import com.devteam.service.AboutService;
import com.devteam.service.RedisService;
import com.devteam.util.markdown.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devteam.exception.PersistenceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class AboutServiceImpl implements AboutService {
	@Autowired
    AboutMapper aboutMapper;
	@Autowired
    RedisService redisService;

	@Override
	public Map<String, String> getAboutInfo() {
		String redisKey = RedisKeyConfig.ABOUT_INFO_MAP;
		Map<String, String> aboutInfoMapFromRedis = redisService.getMapByValue(redisKey);
		if (aboutInfoMapFromRedis != null) {
			return aboutInfoMapFromRedis;
		}
		List<About> abouts = aboutMapper.getList();
		Map<String, String> aboutInfoMap = new HashMap<>();
		for (About about : abouts) {
			if ("content".equals(about.getName())) {
				about.setValue(MarkdownUtils.markdownToHtmlExtensions(about.getValue()));
			}
			aboutInfoMap.put(about.getName(), about.getValue());
		}
		redisService.saveMapToValue(redisKey, aboutInfoMap);
		return aboutInfoMap;
	}

	@Override
	public Map<String, String> getAboutSetting() {
		List<About> abouts = aboutMapper.getList();
		Map<String, String> map = new HashMap<>();
		for (About about : abouts) {
			map.put(about.getName(), about.getValue());
		}
		return map;
	}

	@Override
	public void updateAbout(Map<String, String> map) {
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			updateOneAbout(key, map.get(key));
		}
		deleteAboutRedisCache();
	}

	@Transactional
	public void updateOneAbout(String name, String value) {
		if (aboutMapper.updateAbout(name, value) != 1) {
			throw new PersistenceException("Fail to edit ");
		}
	}

	@Override
	public boolean getAboutCommentEnabled() {
		String commentEnabledString = aboutMapper.getAboutCommentEnabled();
		return Boolean.parseBoolean(commentEnabledString);
	}

	private void deleteAboutRedisCache() {
		redisService.deleteCacheByKey(RedisKeyConfig.ABOUT_INFO_MAP);
	}
}
