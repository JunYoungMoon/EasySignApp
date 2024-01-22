package com.member.easysignapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean(name = "masterDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.master.datasource.hikari")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "spring.slave.datasource.hikari")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }
}
