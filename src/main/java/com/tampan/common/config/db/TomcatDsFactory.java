package com.tampan.common.config.db;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TomcatDsFactory extends AbsDsFactory implements DsFactory {







    ModelMapper modelMapper = new ModelMapper();


    public void createSlaveDataSources() {
        createSlaveDataSources(slaveDbProperties.getUrl(),(url)->{
            org.apache.tomcat.jdbc.pool.DataSource dataSource= createDataSource(slaveDbProperties);
            dataSource.setUrl(url);
            return dataSource;
        });

    }


    protected DataSource createDataSource(DBProperties dbProperties) {
        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName(dbProperties.getDriverClassName());
        dataSource.setUsername(dbProperties.getUsername());
        dataSource.setPassword(dbProperties.getPassword());

        TomcatJdbcPoolProperties tomcatJdbcPoolProperties = dbProperties.getTomcat();

        if (tomcatJdbcPoolProperties != null) {
            setTomcatJdbcPoolProperties(dataSource, tomcatJdbcPoolProperties);
        }
        dataSource.setUrl(dbProperties.getUrl());
        return dataSource;
    }

    private void setTomcatJdbcPoolProperties(DataSource dataSource, TomcatJdbcPoolProperties tomcatJdbcPoolProperties) {

       modelMapper.map(tomcatJdbcPoolProperties, dataSource);

    }
}
