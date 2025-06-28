package io.terrakube.api.repository;

import io.terrakube.api.rs.Organization;
import io.terrakube.api.rs.template.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {

    Template getByOrganizationNameAndName(String organizationName, String name);
    Optional<List<Template>> findByOrganization(Organization organization);
}
