package org.azbuilder.api.plugin.datasource;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        DataSourceConfigurationProperties.class
})
@ConditionalOnMissingBean(DataSource.class)
public class DataSourceAutoConfiguration {

    @Bean
    public DataSource getDataSource(DataSourceConfigurationProperties dataSourceConfigurationProperties) {
        log.info("DataSourceType: {}", dataSourceConfigurationProperties.getType());
        DataSource dataSource = null;
        switch (dataSourceConfigurationProperties.getType()) {
            case SQL_AZURE:
                SQLServerDataSource sqlServerDataSource = new SQLServerDataSource();
                sqlServerDataSource.setServerName(dataSourceConfigurationProperties.getHostname());
                sqlServerDataSource.setDatabaseName(dataSourceConfigurationProperties.getDatabaseName());
                sqlServerDataSource.setAuthentication("SqlPassword"); //https://docs.microsoft.com/en-us/sql/connect/jdbc/connecting-using-azure-active-directory-authentication?view=sql-server-ver15
                sqlServerDataSource.setUser(dataSourceConfigurationProperties.getDatabaseUser());
                sqlServerDataSource.setPassword(dataSourceConfigurationProperties.getDatabasePassword());
                sqlServerDataSource.setLoginTimeout(30);
                dataSource = sqlServerDataSource;
                break;
            case POSTGRESQL:
                PGSimpleDataSource ds = new PGSimpleDataSource();
                ds.setServerNames(new String[]{dataSourceConfigurationProperties.getHostname()});
                ds.setDatabaseName(dataSourceConfigurationProperties.getDatabaseName());
                ds.setUser(dataSourceConfigurationProperties.getDatabaseUser());
                ds.setPassword(dataSourceConfigurationProperties.getDatabasePassword());
                ds.setSslMode("require");
                dataSource = ds;
                break;
            case MYSQL:
                DriverManagerDataSource mysql = new DriverManagerDataSource();
                mysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
                mysql.setUrl(String.format("jdbc:mysql://%s:3306/%s?serverTimezone=UTC", dataSourceConfigurationProperties.getHostname(), dataSourceConfigurationProperties.getDatabaseName()));
                mysql.setUsername(dataSourceConfigurationProperties.getDatabaseUser());
                mysql.setPassword(dataSourceConfigurationProperties.getDatabasePassword());
                dataSource = mysql;
                break;
            default:
                DriverManagerDataSource h2DataSource = new DriverManagerDataSource();
                h2DataSource.setDriverClassName("org.h2.Driver");
                h2DataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
                h2DataSource.setUsername("sa");
                h2DataSource.setPassword("sa");
                dataSource = h2DataSource;
                break;
        }
        return dataSource;
    }
}
