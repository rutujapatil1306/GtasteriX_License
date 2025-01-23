package com.spring.jwt.repository;

import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LicenseOfCustomerRepository extends JpaRepository<LicenseOfCustomer, UUID> {



    LicenseOfCustomer getById(UUID licenseOfCustomerId);

    List<LicenseOfCustomer> findByStatus(Enum status);


    List<LicenseOfCustomer> findByStatusAndExpiryDateBefore(Status status, LocalDate today);
}
