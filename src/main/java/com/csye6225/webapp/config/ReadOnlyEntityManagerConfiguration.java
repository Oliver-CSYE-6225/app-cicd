package com.csye6225.webapp.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.csye6225.webapp",
        includeFilters= @ComponentScan.Filter(ReadOnlyRepository.class),
        entityManagerFactoryRef = "readEntityManagerFactory"
)
public class ReadOnlyEntityManagerConfiguration {

    @Value("${spring.datasource2.username}")
    private String username;

    @Value("${spring.datasource2.password}")
    private String password;

    @Value("${spring.datasource2.url}")
    private String readUrl;

    @Bean
    public DataSource readDataSource() throws Exception {
        return DataSourceBuilder.create()
                                .url(readUrl)
                                .username(username)
                                .password(password)
                                .driverClassName("org.postgresql.Driver")
                                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean readEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("readDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                      .packages("com.csye6225.webapp")
                      .persistenceUnit("read")
                      .build();
    }

}