package com.devteam.util;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class UserAgentUtils {
	private UserAgentAnalyzer uaa;

	public UserAgentUtils() {
		this.uaa = UserAgentAnalyzer
				.newBuilder()
				.hideMatcherLoadStats()
				.withField("OperatingSystemNameVersionMajor")
				.withField("AgentNameVersion")
				.build();
	}

	public Map<String, String> parseOsAndBrowser(String userAgent) {
		UserAgent agent = uaa.parse(userAgent);
		String os = agent.getValue("OperatingSystemNameVersionMajor");
		String browser = agent.getValue("AgentNameVersion");
		Map<String, String> map = new HashMap<>();
		map.put("os", os);
		map.put("browser", browser);
		return map;
	}
}
