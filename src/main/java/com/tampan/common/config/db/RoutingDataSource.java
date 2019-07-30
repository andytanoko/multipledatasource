package com.tampan.common.config.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(RoutingDataSource.class);

	 
    @Override
    protected Object determineCurrentLookupKey() {
    	LOGGER.info("determineCurrentLookupKey invoked! --> " +  DataSourceContextHolder.getDBType() + " on thread : " + Thread.currentThread().getName());
        return DataSourceContextHolder.getDBType();
    }
 
}
