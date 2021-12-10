package com.devteam.aspect;

import com.devteam.config.RedisKeyConfig;
import com.devteam.model.vo.BlogDetail;
import com.devteam.model.vo.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.devteam.annotation.VisitLogger;
import com.devteam.entity.VisitLog;
import com.devteam.entity.Visitor;
import com.devteam.service.RedisService;
import com.devteam.service.VisitLogService;
import com.devteam.service.VisitorService;
import com.devteam.util.AopUtils;
import com.devteam.util.IpAddressUtils;
import com.devteam.util.JacksonUtils;
import com.devteam.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
@Aspect
public class VisitLogAspect {
	@Autowired
	VisitLogService visitLogService;
	@Autowired
	VisitorService visitorService;
	@Autowired
	RedisService redisService;

	ThreadLocal<Long> currentTime = new ThreadLocal<>();

	@Pointcut("@annotation(visitLogger)")
	public void logPointcut(VisitLogger visitLogger) {
	}

	@Around("logPointcut(visitLogger)")
	public Object logAround(ProceedingJoinPoint joinPoint, VisitLogger visitLogger) throws Throwable {
		currentTime.set(System.currentTimeMillis());
		Object result = joinPoint.proceed();
		int times = (int) (System.currentTimeMillis() - currentTime.get());
		currentTime.remove();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String identification = checkIdentification(request);
		VisitLog visitLog = handleLog(joinPoint, visitLogger, request, result, times, identification);
		visitLogService.saveVisitLog(visitLog);
		return result;
	}


	private String checkIdentification(HttpServletRequest request) {
		String identification = request.getHeader("identification");
		if (identification == null) {
			identification = saveUUID(request);
		} else {
			boolean redisHas = redisService.hasValueInSet(RedisKeyConfig.IDENTIFICATION_SET, identification);
			if (!redisHas) {
				boolean mysqlHas = visitorService.hasUUID(identification);
				if (mysqlHas) {
					redisService.saveValueToSet(RedisKeyConfig.IDENTIFICATION_SET, identification);
				} else {
					identification = saveUUID(request);
				}
			}
		}
		return identification;
	}

	private String saveUUID(HttpServletRequest request) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String timestamp = Long.toString(calendar.getTimeInMillis() / 1000);
		String ip = IpAddressUtils.getIpAddress(request);
		String userAgent = request.getHeader("User-Agent");
		String nameUUID = timestamp + ip + userAgent;
		String uuid = UUID.nameUUIDFromBytes(nameUUID.getBytes()).toString();
		response.addHeader("identification", uuid);
		response.addHeader("Access-Control-Expose-Headers", "identification");
		boolean redisHas = redisService.hasValueInSet(RedisKeyConfig.IDENTIFICATION_SET, uuid);
		if (!redisHas) {
			redisService.saveValueToSet(RedisKeyConfig.IDENTIFICATION_SET, uuid);
			Visitor visitor = new Visitor(uuid, ip, userAgent);
			visitorService.saveVisitor(visitor);
		}
		return uuid;
	}

	private VisitLog handleLog(ProceedingJoinPoint joinPoint, VisitLogger visitLogger, HttpServletRequest request, Object result,
	                           int times, String identification) {
		String uri = request.getRequestURI();
		String method = request.getMethod();
		String behavior = visitLogger.behavior();
		String content = visitLogger.content();
		String ip = IpAddressUtils.getIpAddress(request);
		String userAgent = request.getHeader("User-Agent");
		Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
		Map<String, String> map = judgeBehavior(behavior, content, requestParams, result);
		VisitLog log = new VisitLog(identification, uri, method, behavior, map.get("content"), map.get("remark"), ip, times, userAgent);
		log.setParam(StringUtils.substring(JacksonUtils.writeValueAsString(requestParams), 0, 2000));
		return log;
	}


	private Map<String, String> judgeBehavior(String behavior, String content, Map<String, Object> requestParams, Object result) {
		Map<String, String> map = new HashMap<>();
		String remark = "";
		if (behavior.equals("Visit page") && (content.equals("front page") || content.equals("dynamic"))) {
			int pageNum = (int) requestParams.get("pageNum");
			remark = "NS" + pageNum + "Page";
		} else if (behavior.equals("View blog")) {
			Result res = (Result) result;
			if (res.getCode() == 200) {
				BlogDetail blog = (BlogDetail) res.getData();
				String title = blog.getTitle();
				content = title;
				remark = "Article title：" + title;
			}
		} else if (behavior.equals("Search")) {
			Result res = (Result) result;
			if (res.getCode() == 200) {
				String query = (String) requestParams.get("query");
				content = query;
				remark = "Search content：" + query;
			}
		} else if (behavior.equals("View category")) {
			String categoryName = (String) requestParams.get("categoryName");
			int pageNum = (int) requestParams.get("pageNum");
			content = categoryName;
			remark = "Category Name：" + categoryName + "，NS" + pageNum + "Page";
		} else if (behavior.equals("View tags")) {
			String tagName = (String) requestParams.get("tagName");
			int pageNum = (int) requestParams.get("pageNum");
			content = tagName;
			remark = "Label name：" + tagName + "，NS" + pageNum + "Page";
		} else if (behavior.equals("Click on the friend chain 4")) {
			String nickname = (String) requestParams.get("nickname");
			content = nickname;
			remark = "Friends chain name：" + nickname;
		}
		map.put("remark", remark);
		map.put("content", content);
		return map;
	}
}