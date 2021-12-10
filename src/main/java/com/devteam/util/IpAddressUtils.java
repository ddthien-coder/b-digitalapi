package com.devteam.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Component
public class IpAddressUtils {

	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					log.error("getIpAddress exception:", e);
				}
				ip = inet.getHostAddress();
			}
		}
		return StringUtils.substringBefore(ip, ",");
	}

	private static DbSearcher searcher;
	private static Method method;


	@PostConstruct
	private void initIp2regionResource() {
		try {
			InputStream inputStream = new ClassPathResource("/ipdb/ip2region.db").getInputStream();
			byte[] dbBinStr = FileCopyUtils.copyToByteArray(inputStream);
			DbConfig dbConfig = new DbConfig();
			searcher = new DbSearcher(dbConfig, dbBinStr);
			method = searcher.getClass().getMethod("memorySearch", String.class);
		} catch (Exception e) {
			log.error("initIp2regionResource exception:", e);
		}
	}


	public static String getCityInfo(String ip) {
		if (ip == null || !Util.isIpAddress(ip)) {
			log.error("Error: Invalid ip address");
			return null;
		}
		try {
			DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
			String ipInfo = dataBlock.getRegion();
			if (!StringUtils.isEmpty(ipInfo)) {
				ipInfo = ipInfo.replace("|0", "");
				ipInfo = ipInfo.replace("0|", "");
			}
			return ipInfo;
		} catch (Exception e) {
			log.error("getCityInfo exception:", e);
		}
		return null;
	}
}