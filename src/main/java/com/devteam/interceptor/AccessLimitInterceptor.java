package com.devteam.interceptor;

import com.devteam.annotation.AccessLimit;
import com.devteam.model.vo.Result;
import com.devteam.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.devteam.util.IpAddressUtils;
import com.devteam.util.JacksonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


@Component
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {
	@Autowired
    RedisService redisService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
			if (accessLimit == null) {
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			String ip = IpAddressUtils.getIpAddress(request);
			String method = request.getMethod();
			String requestURI = request.getRequestURI();
			String redisKey = ip + ":" + method + ":" + requestURI;
			Integer count = redisService.getObjectByValue(redisKey, Integer.class);
			if (count == null) {
				redisService.incrementByKey(redisKey, 1);
				redisService.expire(redisKey, seconds);
			} else {
				if (count >= maxCount) {
					response.setContentType("application/json;charset=utf-8");
					PrintWriter out = response.getWriter();
					Result result = Result.create(403, accessLimit.msg());
					out.write(JacksonUtils.writeValueAsString(result));
					out.flush();
					out.close();
					return false;
				} else {
					redisService.incrementByKey(redisKey, 1);
				}
			}
		}
		return true;
	}
}
