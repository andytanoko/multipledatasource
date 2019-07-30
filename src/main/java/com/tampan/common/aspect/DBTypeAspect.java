package com.tampan.common.aspect;

import com.tampan.common.config.db.DBType;
import com.tampan.common.config.db.DataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;


@Aspect
@Component
public class DBTypeAspect implements Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBTypeAspect.class);
	
	private int order;

	// magic number to make this ascpect invoked before the data router ask for db type
    @Value("20")
    public void setOrder(int order) {
        this.order = order;
    }
    
    @Override
	public int getOrder() {
		return this.order;
	}
	
    @Pointcut("execution(* com.*.service..*.*(..))")
    public void anyTransactionalMethod() {
    }

	@Around("anyTransactionalMethod()")
	public Object doSetDBType(ProceedingJoinPoint pjp) throws Throwable {
		LOGGER.info("Choosing DBType START");
		MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
		Method method = pjp.getTarget()
			           .getClass()
			           .getMethod(methodSignature.getMethod().getName(),     
			        		   	  methodSignature.getMethod().getParameterTypes());
		Transactional transactional = method.getAnnotation(Transactional.class);
		DataSourceContextHolder.setDBType(transactional != null && !transactional.readOnly() ? DBType.MASTER.toString() : DBType.SLAVE.currentActive());
		LOGGER.info("Use DBType : " + DataSourceContextHolder.getDBType());
		Object retVal = pjp.proceed();
		DataSourceContextHolder.clearDBType();
		LOGGER.info("Choosing DBType FINISH and clear up datasource context holder");
		return retVal;
	}

	


}
