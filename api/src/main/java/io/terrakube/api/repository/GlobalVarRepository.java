package io.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.terrakube.api.rs.Organization;
import io.terrakube.api.rs.globalvar.Globalvar;
import io.terrakube.api.rs.workspace.parameters.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GlobalVarRepository extends JpaRepository<Globalvar, UUID> {

    Globalvar getGlobalvarByOrganizationAndCategoryAndKey(Organization organization, Category category, String key);
    List<Globalvar> findByOrganization(Organization organization);
    Optional<Globalvar> findByOrganizationAndKey(Organization organization, String key);
}
