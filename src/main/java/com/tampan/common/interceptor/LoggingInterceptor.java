package com.tampan.common.interceptor;

import com.codahale.metrics.Timer;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    private static final String TIMER_CONTEXT = "TIMER_CONTEXT";
    private static final String NOT_AVAILABLE = "N/A";

    private static final String HEADER_REQUEST_ID = "x-amzn-requestid";
    private static final String HEADER_CLIENT_IP = "x-amzn-source-ip";
    private static final String HEADER_USER_AGENT = "x-amzn-user-agent";


    @Value("${spring.application.name:N/A}")
    private String serviceName;

    @Value("${spring.profiles.active:N/A}")
    private String serviceEnv;

    @Autowired
    private Timer timer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Timer.Context timerContext = timer.time();
        request.setAttribute(TIMER_CONTEXT, timerContext);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // nothing to do
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Timer.Context timerContext = (Timer.Context) request.getAttribute(TIMER_CONTEXT);
        double elapsedTime = timerContext.stop() / 1000000000.0;

        String requestId = StringUtils.isBlank(request.getHeader(HEADER_REQUEST_ID)) ? NOT_AVAILABLE : request.getHeader(HEADER_REQUEST_ID);
        String clientIp = StringUtils.isBlank(request.getHeader(HEADER_CLIENT_IP)) ? NOT_AVAILABLE : request.getHeader(HEADER_CLIENT_IP);
        String userAgent = StringUtils.isBlank(request.getHeader(HEADER_USER_AGENT)) ? NOT_AVAILABLE : request.getHeader(HEADER_USER_AGENT);

        JSONObject jsonLog = new JSONObject();
        jsonLog.put("requestId", requestId);
        jsonLog.put("clientIp", clientIp);
        jsonLog.put("userAgent", userAgent);
        jsonLog.put("elapsedTime", elapsedTime);
        jsonLog.put("requestMethod", request.getMethod());
        jsonLog.put("requestURL", request.getRequestURL());
        jsonLog.put("requestProtocol", request.getProtocol());
        jsonLog.put("responseStatus", response.getStatus());
        jsonLog.put("serviceName", serviceName);
        jsonLog.put("serviceEnv", serviceEnv);
        jsonLog.put("userAgent", userAgent);

        LOGGER.info(jsonLog.toString());

    }
}