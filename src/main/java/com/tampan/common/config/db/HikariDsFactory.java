package com.tampan.common.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component

public class HikariDsFactory extends AbsDsFactory implements DsFactory {
    ModelMapper modelMapper = new ModelMapper();










    public void createSlaveDataSources() {
        createSlaveDataSources(slaveDbProperties.getUrl(),(url)->{
            HikariDataSource dataSource= createDataSource(slaveDbProperties);
            dataSource.setJdbcUrl(url);
            return dataSource;
        });
    }





    protected  HikariDataSource createDataSource(DBProperties dbProperties)
    {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        HikariDataSource ds = (HikariDataSource) dbProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        //
        ds.setJdbcUrl(dbProperties.getUrl());
        HikariJdbcPoolProperties prop = dbProperties.getHikari();
        if (prop!=null)
        {
            modelMapper.map(dbProperties.getHikari(),ds);

        }
        return ds;
    }


}