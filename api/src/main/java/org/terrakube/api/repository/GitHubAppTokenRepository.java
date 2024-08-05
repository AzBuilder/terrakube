package org.terrakube.api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.terrakube.api.rs.vcs.GitHubAppToken;

@Repository
public interface GitHubAppTokenRepository extends JpaRepository<GitHubAppToken, UUID> {
    GitHubAppToken findByInstallationId(String installationId);
    
    GitHubAppToken findByOwner(String owner);
}
