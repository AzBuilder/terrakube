package org.terrakube.api.repository;

import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.template.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {

    Template getByOrganizationNameAndName(String organizationName, String name);
    Optional<List<Template>> findByOrganization(Organization organization);
}
