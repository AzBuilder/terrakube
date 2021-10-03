package org.azbuilder.api.rs.vcs;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitBucketToken;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitbucketTokenService;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.azbuilder.api.rs.Organization;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@Slf4j
@ReadPermission(expression = "team view vcs OR user is a service")
@CreatePermission(expression = "team manage vcs")
@UpdatePermission(expression = "team manage vcs")
@DeletePermission(expression = "team manage vcs")
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Vcs {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "vcs_type")
    private VcsType vcsType;

    @Column(name = "description")
    private String description;

    @Column(name = "client_id")
    private String clientId;

    @ReadPermission(expression = "service read vcs secret")
    @Column(name = "client_secret")
    private String clientSecret;

    @ReadPermission(expression = "service read vcs secret")
    @Column(name = "access_token")
    private String accessToken;

    @Exclude
    @Column(name = "refresh_token")
    private String refreshToken;

    @Exclude
    @Temporal(TemporalType.TIME)
    @Column(name = "token_expiration")
    private Date tokenExpiration;

    @ManyToOne
    private Organization organization;

    public String getAccessToken() {
        log.info("Token Expiration: {}", tokenExpiration);
        //Refresh token every 1.5 hours, Bitbucket Token expire after 2 hours (7200 seconds)
        if (tokenExpiration != null && tokenExpiration.before(new Date(System.currentTimeMillis() + 5400 * 1000))) {
            log.info("Refreshing Token {}", this.vcsType);
            try {
                BitbucketTokenService bitbucketTokenService = new BitbucketTokenService();
                BitBucketToken bitBucketToken = bitbucketTokenService.refreshAccessToken(this.clientId, this.clientSecret, this.refreshToken);
                this.accessToken = bitBucketToken.getAccess_token();
                this.tokenExpiration = new Date(System.currentTimeMillis() + bitBucketToken.getExpires_in() * 1000);
                log.info("New Token Expiration: {}", this.tokenExpiration);
            } catch (TokenException e) {
                log.error(e.getMessage());
            }

        }
        return accessToken;
    }

}
