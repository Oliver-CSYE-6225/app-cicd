package com.csye6225.webapp.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class rdsConfig {

    // @Bean
    // @Primary
    // @ConfigurationProperties("spring.datasource")
    // public DataSourceProperties firstDataSourceProperties() {
    //     return new DataSourceProperties();
    // }

    // @Bean(name = "datasource1")
    // @Primary
    // @ConfigurationProperties("spring.datasource.configuration")
    // public HikariDataSource firstDataSource(DataSourceProperties firstDataSourceProperties) {
    //     return firstDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    // }

    // @Bean(name = "datasource1")
    // @ConfigurationProperties("spring.datasource2")
    // public BasicDataSource firstDataSource() {
    //     return DataSourceBuilder.create().type(BasicDataSource.class).build();
    // }

    // @Bean(name = "datasource2")
    // @ConfigurationProperties("spring.datasource2")
    // public BasicDataSource secondDataSource() {
    //     return DataSourceBuilder.create().type(BasicDataSource.class).build();
    // }


    @Bean(name = "writeDataSource")
    @ConfigurationProperties(prefix="spring.datasource")
    @Primary
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    

    @Bean(name = "readDataSource")
    @ConfigurationProperties(prefix="spring.datasource2")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource routingDataSource(
        @Qualifier("writeDataSource") DataSource writeDataSource,
        @Qualifier("readDataSource") DataSource readDataSource) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
    
        Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
        dataSourceMap.put("write", writeDataSource);
        dataSourceMap.put("read", readDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);
    
        return routingDataSource;
    }
    
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
    // @Bean(name = "tm1")
    // @Autowired
    // @Primary
    // DataSourceTransactionManager tm1(@Qualifier("datasource1") HikariDataSource datasource) {
    //     DataSourceTransactionManager txm = new DataSourceTransactionManager(datasource);
    //     return txm;
    // }

    // @Bean(name = "tm2")
    // @Autowired
    // DataSourceTransactionManager tm2(@Qualifier("datasource2") BasicDataSource datasource) {
    //     DataSourceTransactionManager txm = new DataSourceTransactionManager(datasource);
    //     return txm;
    // }

    // @Bean
    // public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    // JpaTransactionManager transactionManager = new JpaTransactionManager();
    // transactionManager.setEntityManagerFactory(emf);

    // return transactionManager;

    // }
    // @Primary
    // @Bean
    // public PlatformTransactionManager userTransactionManager() {
 
    //     JpaTransactionManager transactionManager
    //       = new JpaTransactionManager();
    //     transactionManager.setEntityManagerFactory(
    //       userEntityManager().getObject());
    //     return transactionManager;
    // }
}
