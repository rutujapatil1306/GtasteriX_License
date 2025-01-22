package com.spring.jwt.repository;

import com.spring.jwt.entity.LicenseOfCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LicenseOfCustomerRepository extends JpaRepository<LicenseOfCustomer, UUID> {

    List<LicenseOfCustomer> findByLicense_LicenseID(UUID licenseID);

  // List<LicenseOfCustomer> getByLicenceId(UUID licenseOfListId);

    List<LicenseOfCustomer> findByStatus(Enum status);
}
