package org.terrakube.api.plugin.datasource;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
                log.info("postgresql datasource using SSL Mode: {}", dataSourceConfigurationProperties.getSslMode());
                PGSimpleDataSource ds;
                if (dataSourceConfigurationProperties.isAwsIamAuth()) {
                    PostgresAwsIamAuthDataSource dsAwsIam = new PostgresAwsIamAuthDataSource();
                    dsAwsIam.setRegion(dataSourceConfigurationProperties.getAwsRegion());
                    ds = dsAwsIam;
                } else {
                    ds = new PGSimpleDataSource();
                }
                ds.setServerNames(new String[]{dataSourceConfigurationProperties.getHostname()});
                ds.setPortNumbers(new int[]{Integer.parseInt(dataSourceConfigurationProperties.getDatabasePort())});
                ds.setDatabaseName(dataSourceConfigurationProperties.getDatabaseName());
                ds.setUser(dataSourceConfigurationProperties.getDatabaseUser());
                ds.setPassword(dataSourceConfigurationProperties.getDatabasePassword());
                ds.setCurrentSchema(dataSourceConfigurationProperties.getDatabaseSchema());
                ds.setSslMode(dataSourceConfigurationProperties.getSslMode());
                dataSource = ds;
                break;
            default:
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
                config.setUsername("sa");
                config.setPassword("sa");
                config.setDriverClassName("org.h2.Driver");

// Pool configuration
                config.setPoolName("H2ConnectionPool");
                config.setMinimumIdle(2);
                config.setMaximumPoolSize(10);
                config.setAutoCommit(true);
                config.setIdleTimeout(30000);
                config.setMaxLifetime(1800000);
                config.setConnectionTimeout(30000);
                config.setLeakDetectionThreshold(60000);

// Performance optimizations
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                dataSource = new HikariDataSource(config);

                break;
        }
        return dataSource;
    }
}
