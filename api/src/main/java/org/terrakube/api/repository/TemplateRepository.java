package org.terrakube.api.repository;

import org.terrakube.api.rs.template.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {

    Template getByOrganizationNameAndName(String organizationName, String name);
}
