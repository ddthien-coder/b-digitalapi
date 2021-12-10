package com.devteam.service.impl;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.SiteSetting;
import com.devteam.exception.PersistenceException;
import com.devteam.mapper.SiteSettingMapper;
import com.devteam.model.bean.Badge;
import com.devteam.model.bean.Copyright;
import com.devteam.model.bean.Favorite;
import com.devteam.model.vo.Introduction;
import com.devteam.service.RedisService;
import com.devteam.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devteam.util.JacksonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class SiteSettingServiceImpl implements SiteSettingService {
	@Autowired
    SiteSettingMapper siteSettingMapper;
	@Autowired
    RedisService redisService;

	private static final Pattern PATTERN = Pattern.compile("\"(.*?)\"");

	@Override
	public Map<String, List<SiteSetting>> getList() {
		List<SiteSetting> siteSettings = siteSettingMapper.getList();
		Map<String, List<SiteSetting>> map = new HashMap<>();
		List<SiteSetting> type1 = new ArrayList<>();
		List<SiteSetting> type2 = new ArrayList<>();
		List<SiteSetting> type3 = new ArrayList<>();
		for (SiteSetting s : siteSettings) {
			if (s.getType() == 1) {
				type1.add(s);
			} else if (s.getType() == 2) {
				type2.add(s);
			} else if (s.getType() == 3) {
				type3.add(s);
			}
		}
		map.put("type1", type1);
		map.put("type2", type2);
		map.put("type3", type3);
		return map;
	}

	@Override
	public Map<String, Object> getSiteInfo() {
		String redisKey = RedisKeyConfig.SITE_INFO_MAP;
		Map<String, Object> siteInfoMapFromRedis = redisService.getMapByValue(redisKey);
		if (siteInfoMapFromRedis != null) {
			return siteInfoMapFromRedis;
		}
		List<SiteSetting> siteSettings = siteSettingMapper.getList();
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> siteInfo = new HashMap<>();
		List<Badge> badges = new ArrayList<>();
		Introduction introduction = new Introduction();
		List<Favorite> favorites = new ArrayList<>();
		List<String> rollTexts = new ArrayList<>();
		for (SiteSetting s : siteSettings) {
			if (s.getType() == 1) {
				if ("copyright".equals(s.getName())) {
					Copyright copyright = JacksonUtils.readValue(s.getValue(), Copyright.class);
					siteInfo.put(s.getName(), copyright);
				} else {
					siteInfo.put(s.getName(), s.getValue());
				}
			} else if (s.getType() == 2) {
				Badge badge = JacksonUtils.readValue(s.getValue(), Badge.class);
				badges.add(badge);
			} else if (s.getType() == 3) {
				if ("avatar".equals(s.getName())) {
					introduction.setAvatar(s.getValue());
				} else if ("name".equals(s.getName())) {
					introduction.setName(s.getValue());
				} else if ("github".equals(s.getName())) {
					introduction.setGithub(s.getValue());
				} else if ("qq".equals(s.getName())) {
					introduction.setQq(s.getValue());
				} else if ("bilibili".equals(s.getName())) {
					introduction.setBilibili(s.getValue());
				} else if ("netease".equals(s.getName())) {
					introduction.setNetease(s.getValue());
				} else if ("email".equals(s.getName())) {
					introduction.setEmail(s.getValue());
				} else if ("favorite".equals(s.getName())) {
					Favorite favorite = JacksonUtils.readValue(s.getValue(), Favorite.class);
					favorites.add(favorite);
				} else if ("rollText".equals(s.getName())) {
					Matcher m = PATTERN.matcher(s.getValue());
					while (m.find()) {
						rollTexts.add(m.group(1));
					}
				}
			}
		}
		introduction.setFavorites(favorites);
		introduction.setRollText(rollTexts);
		map.put("introduction", introduction);
		map.put("siteInfo", siteInfo);
		map.put("badges", badges);
		redisService.saveMapToValue(redisKey, map);
		return map;
	}

	@Override
	public String getWebTitleSuffix() {
		return siteSettingMapper.getWebTitleSuffix();
	}

	@Override
	public void updateSiteSetting(List<LinkedHashMap> siteSettings, List<Integer> deleteIds) {
		for (Integer id : deleteIds) {
			deleteOneSiteSettingById(id);
		}
		for (LinkedHashMap s : siteSettings) {
			SiteSetting siteSetting = JacksonUtils.convertValue(s, SiteSetting.class);
			if (siteSetting.getId() != null) {
				updateOneSiteSetting(siteSetting);
			} else {
				saveOneSiteSetting(siteSetting);
			}
		}
		deleteSiteInfoRedisCache();
	}

	@Transactional
	public void saveOneSiteSetting(SiteSetting siteSetting) {
		if (siteSettingMapper.saveSiteSetting(siteSetting) != 1) {
			throw new PersistenceException("Failed to add configuration");
		}
	}

	@Transactional
	public void updateOneSiteSetting(SiteSetting siteSetting) {
		if (siteSettingMapper.updateSiteSetting(siteSetting) != 1) {
			throw new PersistenceException("Configuration modification failed");
		}
	}

	@Transactional
	public void deleteOneSiteSettingById(Integer id) {
		if (siteSettingMapper.deleteSiteSettingById(id) != 1) {
			throw new PersistenceException("Configuration deletion failed");
		}
	}


	private void deleteSiteInfoRedisCache() {
		redisService.deleteCacheByKey(RedisKeyConfig.SITE_INFO_MAP);
	}
}
