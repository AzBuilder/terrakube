package org.azbuilder.api.plugin.datasource.configuration;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.datasource.azure.AzureDataSourceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        DataSourceConfigurationProperties.class,
        AzureDataSourceProperties.class
})
@ConditionalOnMissingBean(DataSource.class)
public class DataSourceAutoConfiguration {

    @Bean
    public DataSource getDataSource(DataSourceConfigurationProperties dataSourceConfigurationProperties, AzureDataSourceProperties azureDataSourceProperties) {
        log.info("DataSourceType: {}", dataSourceConfigurationProperties.getType());
        DataSource dataSource = null;
        switch (dataSourceConfigurationProperties.getType()) {
            case SQL_AZURE:
                SQLServerDataSource sqlServerDataSource = new SQLServerDataSource();
                sqlServerDataSource.setServerName(azureDataSourceProperties.getServerName());
                sqlServerDataSource.setDatabaseName(azureDataSourceProperties.getDatabaseName());
                sqlServerDataSource.setAuthentication("SqlPassword"); //https://docs.microsoft.com/en-us/sql/connect/jdbc/connecting-using-azure-active-directory-authentication?view=sql-server-ver15
                sqlServerDataSource.setUser(azureDataSourceProperties.getDatabaseUser());
                sqlServerDataSource.setPassword(azureDataSourceProperties.getDatabasePassword());
                sqlServerDataSource.setLoginTimeout(30);
                dataSource = sqlServerDataSource;
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
