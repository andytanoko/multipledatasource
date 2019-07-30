package com.tampan.common.config.db;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

@Data

public class DBProperties extends DataSourceProperties {
    private TomcatJdbcPoolProperties tomcat;
    private HikariJdbcPoolProperties hikari;
}
