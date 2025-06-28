package io.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.terrakube.api.rs.ssh.Ssh;

import java.util.UUID;

public interface SshRepository extends JpaRepository<Ssh, UUID> {
}
