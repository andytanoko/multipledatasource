package com.tampan.common.config.db;

import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        basePackages = {"com.*.repository"}
)
public class DBConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConfig.class);


    @Value("${master.spring.jpa.database-platform}")
    private String hibernateDialect;


    @Value("${master.spring.isHikariProvider:true}")
    private boolean isHikariProvider;

    @Bean
    @ConfigurationProperties("slave.spring.datasource")
    public DBProperties slaveDataSourceProperties() {
        return new DBProperties();
    }


    @Bean
    @Primary
    @ConfigurationProperties("master.spring.datasource")
    public DBProperties masterDataSourceProperties() {
        return new DBProperties();
    }


    @Autowired
    @Qualifier("tomcatDsFactory")
    DsFactory tomcatDsFactory;

    @Autowired
    @Qualifier("hikariDsFactory")
    DsFactory hikariDsFactory;


    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.implicit_naming_strategy", ImplicitNamingStrategyLegacyHbmImpl.class.getName());
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.dialect", this.hibernateDialect);

        return props;
    }


    @Bean
    public RoutingDataSource dataSource() {


        // setting up master data source
        DataSource masterDataSource;

        DsFactory dsFactory = isHikariProvider?hikariDsFactory:tomcatDsFactory;
        // setting up slave data source (if any)

        masterDataSource = dsFactory.initDataSource();


        RoutingDataSource dataSource = new RoutingDataSource();
        dataSource.setTargetDataSources(dsFactory.getTargetDataSources());
        dataSource.setDefaultTargetDataSource(masterDataSource);

        return dataSource;
    }

}
