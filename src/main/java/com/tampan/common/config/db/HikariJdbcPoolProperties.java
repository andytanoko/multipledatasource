package com.tampan.common.config.db;

import lombok.Data;

@Data
public class HikariJdbcPoolProperties {

    private String poolName;
    private Integer minimumIdle;
    private Integer maximumPoolSize;
    private Integer idleTimeout;
    private Integer connectionTimeout;
    private Integer maxLifeTime;
    private Integer validationTimeout;
    private String driverClassName;
}
