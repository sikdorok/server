package com.ddd.chulsi.infrastructure.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final JpaProperties jpaProperties;

    public DatabaseConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(masterDataSource());
    }

    // JPA에서 사용할 EntityManagerFactory와 TransactionManager를 설정해줍니다.
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("com.ddd.chulsi");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties.getProperties());

        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) { return new JdbcTemplate(dataSource); }

}
