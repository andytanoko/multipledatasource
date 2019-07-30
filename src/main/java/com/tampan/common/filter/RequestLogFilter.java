package com.tampan.common.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CORE-1161
 *
 * This class use to get request and header information from
 * all incoming request. All request and header information
 * save in MDC context current thread, and clear the MDC context
 * when request is finished. 
 * 
 * The MDC context information is use for sentry additional data 
 * log for cleared information of error.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestLogFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = new RequestWrapper((HttpServletRequest) request);
		
		Enumeration<String> headerNames = httpRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			String headerValue = httpRequest.getHeader(headerName);
			
			MDC.put("Header-" + headerName, headerValue);
		}
		
		String requestBody = ((RequestWrapper) httpRequest).getBody();
		if (!requestBody.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(requestBody, Object.class);
			requestBody = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);	
		}
		
		MDC.put("Request-Uri", httpRequest.getRequestURI());
		MDC.put("Request-Query-String", httpRequest.getQueryString());
		MDC.put("Request-Body", requestBody);
		MDC.put("Request-Method", httpRequest.getMethod());
		MDC.put("Request-Remote-Host", httpRequest.getRemoteHost());
		MDC.put("Request-Remote-Addr", httpRequest.getRemoteAddr());
		MDC.put("Request-Remote-Port", Integer.toString(httpRequest.getRemotePort()));
	
		chain.doFilter(httpRequest, response);
		
		if (httpRequest.getInputStream() != null) {
			httpRequest.getInputStream().close();
		}
		if (httpRequest.getReader() != null) {
			httpRequest.getReader().close();
		}
		
		Optional.ofNullable(MDC.getCopyOfContextMap())
		.ifPresent(mdcContext -> mdcContext.forEach((k, v) -> {
			LOGGER.debug("MDC Removing: " + k + " => " + v);
			MDC.remove(k);
		}));
	}

	@Override
	public void destroy() {}
	
	private static class RequestWrapper extends HttpServletRequestWrapper {

		private String body;
		
		public RequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = null;
			
			try {
				InputStream inputStream = request.getInputStream();
				if (null != inputStream) {
					
					bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					char[] charBuffer = new char[128];
					int bytesRead = -1;
					while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
						stringBuilder.append(charBuffer, 0, bytesRead);
					}	
				} 
			} catch (IOException e) {
				LOGGER.info(e.getMessage());
			} finally {
				if (null != bufferedReader) {
					try {
						bufferedReader.close();
					} catch (IOException e) {}
				}
			}
			
			body = stringBuilder.toString();
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
			ServletInputStream servletInputStream = new ServletInputStream() {
				
				@Override
				public int read() throws IOException {
					return byteArrayInputStream.read();
				}

				@Override
				public boolean isFinished() {
					return false;
				}

				@Override
				public boolean isReady() {
					return false;
				}

				@Override
				public void setReadListener(ReadListener readListener) {}
			};

			return servletInputStream;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(this.getInputStream()));
		}

		public String getBody() {
			return body;
		}

	}
}
