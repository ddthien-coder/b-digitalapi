package com.devteam.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;


public class AopUtils {

	public static Map<String, Object> getRequestParams(JoinPoint joinPoint) {
		Map<String, Object> map = new LinkedHashMap<>();
		String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			if (!isFilterObject(args[i])) {
				map.put(parameterNames[i], args[i]);
			}
		}
		return map;
	}


	private static boolean isFilterObject(final Object o) {
		return o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof MultipartFile;
	}
}
