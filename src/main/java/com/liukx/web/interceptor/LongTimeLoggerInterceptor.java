package com.liukx.web.interceptor;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Enumeration;

public class LongTimeLoggerInterceptor extends HandlerInterceptorAdapter {

	private static final String ATTR_CONTROLLER_EXECUTION_START_TIME = "ControllerExcutionStartTime";
	private static final int EXECUTION_TIME_FOR_LOGGING = 1000;
	private final Logger logger = LoggerFactory.getLogger(LongTimeLoggerInterceptor.class);	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		request.setAttribute(ATTR_CONTROLLER_EXECUTION_START_TIME, System.currentTimeMillis());
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		String handleName;
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			handleName =  handlerMethod.getShortLogMessage();			
		}else{
			handleName = handler.getClass().getName();
		}
		
		long startTime = (long) request.getAttribute(ATTR_CONTROLLER_EXECUTION_START_TIME);
		long costTime = System.currentTimeMillis()- startTime;
		if(costTime>EXECUTION_TIME_FOR_LOGGING){
			StringBuilder logMsg = new StringBuilder().append("longtime,")
					.append(DateFormatUtils.format(new Date(), "yyyy-MM-dd,HH:mm"))
					.append(",")
					.append(handleName)
					.append(",")
					.append(costTime)
//					.append(",")
//					.append(user == null ? "anonymous" : user.getId() + "(" + user.getUserName() + ")")
					.append(",")
					.append(request.getRequestURL())
					.append(",");
			Enumeration<String> params = request.getParameterNames();
			while(params.hasMoreElements()){
				String param = params.nextElement();
				logMsg.append(param).append("=").append(request.getParameter(param)).append("|");
			}
			logMsg.append("\nheaders:");
			Enumeration<String> headerNames = request.getHeaderNames();
			while(headerNames.hasMoreElements()){
				String header = headerNames.nextElement();
				logMsg.append(header).append("=").append(request.getHeader(header)).append("\n");
			}
			logger.warn(logMsg.toString());
		
		}
	}
}
