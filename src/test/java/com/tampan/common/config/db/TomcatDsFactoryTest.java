package com.tampan.common.config.db;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TomcatDsFactoryTest {


    @InjectMocks
    TomcatDsFactory tomcatDsFactory;


    @Mock
    DBProperties masterDbProperties;

    @Test
    public void testSplitConnectionString()
    {

        Mockito.when(masterDbProperties.getUrl()).thenReturn("jdbc:postgresql://dbtablemgmt-exp-master-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432/postgres");
        String result= tomcatDsFactory.getSlaveJdbcUrl("jdbc:postgresql://dbtablemgmt-exp-master-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432/postgres"
        , "jdbc:postgresql://dbtablemgmt-exp-slave1-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com,dbtablemgmt-exp-slave2-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432/postgres");

        Assert.assertEquals("","jdbc:postgresql://dbtablemgmt-exp-slave1-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com" +
                        ",dbtablemgmt-exp-slave2-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432,dbtablemgmt-exp-master-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432/postgres"
                ,result);
    }


    @Test

    public void testBuildMap()
    {

        Mockito.when(masterDbProperties.getUrl()).thenReturn("jdbc:postgresql://dbtablemgmt-exp-master-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432/postgres");

        Map<String,String> result =
               tomcatDsFactory
                       .buildSlaveDataSourceMap(
                               "jdbc:postgresql://dbtablemgmt-exp-slave1-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com" +
                                       ",dbtablemgmt-exp-slave2-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432/postgres"
                       );


        Assert.assertEquals("jdbc:postgresql://dbtablemgmt-exp-master-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432,dbtablemgmt-exp-slave1-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com/postgres"
                ,result.get("SLAVE0"));

        Assert.assertEquals("jdbc:postgresql://dbtablemgmt-exp-master-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432,dbtablemgmt-exp-slave2-andy.ch0lnhgocapc.ap-southeast-1.rds.amazonaws.com:5432/postgres"
                ,result.get("SLAVE1"));


    }

}
