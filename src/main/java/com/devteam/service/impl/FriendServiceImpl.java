package com.devteam.service.impl;

import com.devteam.config.RedisKeyConfig;
import com.devteam.entity.Friend;
import com.devteam.entity.SiteSetting;
import com.devteam.mapper.FriendMapper;
import com.devteam.mapper.SiteSettingMapper;
import com.devteam.model.vo.FriendInfo;
import com.devteam.service.RedisService;
import com.devteam.util.markdown.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devteam.exception.PersistenceException;
import com.devteam.service.FriendService;

import java.util.Date;
import java.util.List;


@Service
public class FriendServiceImpl implements FriendService {
	@Autowired
    FriendMapper friendMapper;
	@Autowired
    SiteSettingMapper siteSettingMapper;
	@Autowired
    RedisService redisService;

	@Override
	public List<Friend> getFriendList() {
		return friendMapper.getFriendList();
	}

	@Override
	public List<com.devteam.model.vo.Friend> getFriendVOList() {
		return friendMapper.getFriendVOList();
	}

	@Transactional
	@Override
	public void updateFriendPublishedById(Long friendId, Boolean published) {
		if (friendMapper.updateFriendPublishedById(friendId, published) != 1) {
			throw new PersistenceException("Operation failed");
		}
	}

	@Transactional
	@Override
	public void saveFriend(Friend friend) {
		friend.setViews(0);
		friend.setCreateTime(new Date());
		if (friendMapper.saveFriend(friend) != 1) {
			throw new PersistenceException("Add failed");
		}
	}

	@Transactional
	@Override
	public void updateFriend(com.devteam.model.dto.Friend friend) {
		if (friendMapper.updateFriend(friend) != 1) {
			throw new PersistenceException("Fail to edit");
		}
	}

	@Transactional
	@Override
	public void deleteFriend(Long id) {
		if (friendMapper.deleteFriend(id) != 1) {
			throw new PersistenceException("failed to delete");
		}
	}

	@Transactional
	@Override
	public void updateViewsByNickname(String nickname) {
		if (friendMapper.updateViewsByNickname(nickname) != 1) {
			throw new PersistenceException("operation failed");
		}
	}

	@Override
	public FriendInfo getFriendInfo(boolean cache, boolean md) {
		String redisKey = RedisKeyConfig.FRIEND_INFO_MAP;
		if (cache) {
			FriendInfo friendInfoFromRedis = redisService.getObjectByValue(redisKey, FriendInfo.class);
			if (friendInfoFromRedis != null) {
				return friendInfoFromRedis;
			}
		}
		List<SiteSetting> siteSettings = siteSettingMapper.getFriendInfo();
		FriendInfo friendInfo = new FriendInfo();
		for (SiteSetting siteSetting : siteSettings) {
			if ("friendContent".equals(siteSetting.getName())) {
				if (md) {
					friendInfo.setContent(MarkdownUtils.markdownToHtmlExtensions(siteSetting.getValue()));
				} else {
					friendInfo.setContent(siteSetting.getValue());
				}
			} else if ("friendCommentEnabled".equals(siteSetting.getName())) {
				if ("1".equals(siteSetting.getValue())) {
					friendInfo.setCommentEnabled(true);
				} else {
					friendInfo.setCommentEnabled(false);
				}
			}
		}
		if (cache && md) {
			redisService.saveObjectToValue(redisKey, friendInfo);
		}
		return friendInfo;
	}

	@Transactional
	@Override
	public void updateFriendInfoContent(String content) {
		if (siteSettingMapper.updateFriendInfoContent(content) != 1) {
			throw new PersistenceException("Fail to edit");
		}
		deleteFriendInfoRedisCache();
	}

	@Transactional
	@Override
	public void updateFriendInfoCommentEnabled(Boolean commentEnabled) {
		if (siteSettingMapper.updateFriendInfoCommentEnabled(commentEnabled) != 1) {
			throw new PersistenceException("Fail to edit");
		}
		deleteFriendInfoRedisCache();
	}

	private void deleteFriendInfoRedisCache() {
		redisService.deleteCacheByKey(RedisKeyConfig.FRIEND_INFO_MAP);
	}
}
