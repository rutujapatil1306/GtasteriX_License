package com.spring.jwt.repository;

import com.spring.jwt.entity.Customer;
import com.spring.jwt.entity.LicenseOfCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseOfCustomerRepository extends JpaRepository<LicenseOfCustomer, UUID> {

    LicenseOfCustomer getById(UUID licenseOfCustomerId);

    List<LicenseOfCustomer> findByStatus(Enum status);

    @Query("SELECT loc FROM LicenseOfCustomer loc WHERE loc.customer.id = :customerId AND loc.license.id = :licenseId")
    Optional<LicenseOfCustomer> findByCustomerIdAndLicenseId( UUID customerId,  UUID licenseId);


    List<LicenseOfCustomer> findByLicense_LicenseID(UUID licenseListID);

    Page<LicenseOfCustomer> findAll(Specification<LicenseOfCustomer> spec, Pageable pageable);

    List<LicenseOfCustomer> findByCustomer(Customer customer);
}
