package com.spring.jwt.dto;

import com.spring.jwt.entity.Customer;
import com.spring.jwt.entity.LicenseList;
import com.spring.jwt.entity.Status;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
public class LicenseOfCustomerDTO {

        private UUID licenseOfCustomerId;

        private String licenseName;

        private Status status;

        private LocalDate issueDate;

        private LocalDate expiryDate;

//       private Customer customer;
//        private LicenseList license;
    }

    // private CustomerDTO customerDTO;

