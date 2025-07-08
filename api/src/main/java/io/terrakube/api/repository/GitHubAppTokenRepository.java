package io.terrakube.api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.terrakube.api.rs.vcs.GitHubAppToken;

@Repository
public interface GitHubAppTokenRepository extends JpaRepository<GitHubAppToken, UUID> {
    GitHubAppToken findByAppIdAndInstallationId(String appId, String installationId);
    
    GitHubAppToken findByAppIdAndOwner(String appId, String owner);
}
