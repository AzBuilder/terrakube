package io.terrakube.api.plugin.datasource;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsUtilities;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class PostgresAwsIamAuthDataSource extends PGSimpleDataSource {
    public void setRegion(String region) {
        this.awsRegion = Region.of(region);
    }

    private Region awsRegion;
    private String authToken;
    private long lastTokenRefresh = 0;
    private static final long TOKEN_EXPIRY_TIME = 10L * 60L * 1000L;

    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        // If the token is older than 10 minutes, refresh it
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTokenRefresh > TOKEN_EXPIRY_TIME) {
            lastTokenRefresh = currentTime;

            log.debug("Refreshing postgres password using AWS SDK");

            RdsUtilities rdsUtilities = RdsUtilities.builder()
                    .region(awsRegion)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            authToken = rdsUtilities.generateAuthenticationToken(r -> r
                    .hostname(getServerNames()[0])
                    .port(getPortNumbers()[0])
                    .username(getUser())
                    .region(awsRegion)
                    .build()
            );
        }

        return super.getConnection(user, authToken);
    }
}
