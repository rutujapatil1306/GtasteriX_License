package com.spring.jwt.repository;

import com.spring.jwt.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer,UUID> {

  @Query("SELECT c.mobileNumber FROM Customer c")
  List<String> getAllMobileNumbers();
}
