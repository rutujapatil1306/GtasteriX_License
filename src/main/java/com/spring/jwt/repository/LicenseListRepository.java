package com.spring.jwt.repository;

import com.spring.jwt.entity.LicenseList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LicenseListRepository extends JpaRepository<LicenseList,UUID> {
}
