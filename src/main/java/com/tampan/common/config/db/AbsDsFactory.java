package com.tampan.common.config.db;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbsDsFactory implements DsFactory {

    protected Map<Object, Object> targetDataSources = new LinkedHashMap<>();

    @Autowired
    @Qualifier("masterDataSourceProperties")
    protected DBProperties masterDbProperties;

    @Autowired
    @Qualifier("slaveDataSourceProperties")
    protected DBProperties slaveDbProperties;


    @Override
    public DataSource initDataSource() {
        DataSource ds = createMasterDataSource();
        createSlaveDataSources();
        return ds;
    }

    protected String getSlaveJdbcUrl(String masterJdbcUrl, String jdbcUrl) {
        String[] masterNodes = getArrNodesFromJdbcUrl(masterJdbcUrl);
        String slaveNodes = getNodesFromJdbcUrl(jdbcUrl);
        return jdbcUrl.replace(slaveNodes, slaveNodes + "," + masterNodes[0]);
    }

    protected String getNodesFromJdbcUrl(String jdbcUrl) {
        Pattern p = Pattern.compile("\\/\\/(.*)\\/");
        Matcher m = p.matcher(jdbcUrl);
        m.find();
        return m.group(1);
    }

    protected String[] getArrNodesFromJdbcUrl(String jdbcUrl) {
        return getNodesFromJdbcUrl(jdbcUrl).split(",");
    }

    protected Map<String, String> buildSlaveDataSourceMap(String jdbcUrl) {
        Map<String, String> mapDS = new HashMap<>();
        String[] arrNodes = getArrNodesFromJdbcUrl(jdbcUrl);

        for (int i = 0; i < arrNodes.length; i++) {
            StringBuilder newStringNodes = new StringBuilder(arrNodes[i]);
            {
                for (int j = 0; j < arrNodes.length; j++) {
                    if (i != j) newStringNodes.append(",").append(arrNodes[j]);
                }
            }
            String result = getSlaveJdbcUrl(jdbcUrl.replace(getNodesFromJdbcUrl(jdbcUrl), newStringNodes.toString()), masterDbProperties.getUrl());
            mapDS.put(DBType.SLAVE.toString() + i, result);
        }
        return mapDS;
    }


    protected void createSlaveDataSources(String slaveUrl, Function<String, DataSource> slaveDS) {
        if (StringUtils.isNotBlank(slaveUrl)) {

            Map<String, String> jdbcUrlMap = buildSlaveDataSourceMap(slaveUrl);


            for (Map.Entry<String, String> entrySet : jdbcUrlMap.entrySet()) {


                targetDataSources.put(entrySet.getKey(), slaveDS.apply(entrySet.getValue()));
            }
            DBType.SLAVE.setSlaveCount(jdbcUrlMap.size());

        }
    }


    protected DataSource createMasterDataSource() {

        DataSource dataSource = createDataSource(masterDbProperties);
        targetDataSources.put(DBType.MASTER.name(), dataSource);
        return dataSource;
    }

    abstract protected DataSource createDataSource(DBProperties dbProperties);

    abstract protected void createSlaveDataSources();

    public Map<Object, Object> getTargetDataSources() {
        return targetDataSources;
    }


}
