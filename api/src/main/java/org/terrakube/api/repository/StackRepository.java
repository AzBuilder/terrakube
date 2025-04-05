package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.model.Stack;
import java.util.UUID;

public interface StackRepository extends JpaRepository<Stack, UUID> {
} 