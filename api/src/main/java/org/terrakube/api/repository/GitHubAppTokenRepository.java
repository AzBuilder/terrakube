package org.terrakube.api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.terrakube.api.rs.vcs.GitHubAppToken;
import org.terrakube.api.rs.vcs.Vcs;

@Repository
public interface GitHubAppTokenRepository extends JpaRepository<GitHubAppToken, UUID> {
    GitHubAppToken findByVcsAndOwner(Vcs vcs, String owner);
}
