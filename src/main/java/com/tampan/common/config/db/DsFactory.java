package com.tampan.common.config.db;


import javax.sql.DataSource;
import java.util.Map;

public interface DsFactory {
    javax.sql.DataSource initDataSource();
    Map<Object, Object> getTargetDataSources();
}
