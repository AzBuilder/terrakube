package org.azbuilder.api.repository;

import org.azbuilder.api.rs.template.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {
}
