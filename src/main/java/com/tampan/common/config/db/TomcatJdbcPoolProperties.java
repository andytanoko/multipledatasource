package com.tampan.common.config.db;

import lombok.Data;

@Data
public class TomcatJdbcPoolProperties {
    private Integer initialSize;
    private Integer maxActive;
    private Integer maxWait;
    private Integer minEvictableIdleTimeMillis;
    private Integer minIdle;
    private Boolean removeAbandoned;
    private Integer removeAbandonedTimeout;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private Boolean testWhileIdle;
    private Integer timeBetweenEvictionRunsMillis;
    private Long validationInterval;
    private String validationQuery;


}
