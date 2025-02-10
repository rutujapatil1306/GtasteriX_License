package com.spring.jwt.repository;

import com.spring.jwt.entity.Customer;
import com.spring.jwt.entity.LicenseOfCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer,UUID> {

    @Query("SELECT c.mobileNumber FROM Customer c")
    List<String> getAllMobileNumbers();

    @Query("SELECT lc FROM LicenseOfCustomer lc JOIN FETCH lc.customer WHERE lc.licenseOfCustomerId = :licenseOfCustomerId")
    Optional<LicenseOfCustomer> findLicenseWithCustomerById(@Param("licenseOfCustomerId") UUID licenseOfCustomerId);

    List<Customer> findByFirstNameContainingIgnoreCaseOrderByFirstNameAsc(String firstName);

   List<Customer> findByFirstName(String firstName);

   List<Customer> findByArea(String area);

   List<Customer> findByEmail(String Email);

}
