package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.job.address.Address;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
