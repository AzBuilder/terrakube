package io.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.terrakube.api.rs.tag.Tag;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Tag getByOrganizationNameAndName(String organizationName, String name);
}
