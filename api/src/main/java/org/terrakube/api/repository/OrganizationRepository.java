package org.terrakube.api.repository;

import org.terrakube.api.rs.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Organization getOrganizationByName(String name);
}
