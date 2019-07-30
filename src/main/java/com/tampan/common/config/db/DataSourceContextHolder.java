package com.tampan.common.config.db;

public class DataSourceContextHolder {
	 
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
 
    public static void setDBType(String dBType) {
        contextHolder.set(dBType);
    }
 
    public static String getDBType() {
        return (String) contextHolder.get();
    }
 
    public static void clearDBType() {
        contextHolder.remove();
    }
}
