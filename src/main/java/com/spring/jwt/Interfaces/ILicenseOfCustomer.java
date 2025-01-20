package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.CustomerDTO;

import java.util.UUID;

public interface ILicenseOfCustomer {

    CustomerDTO updateStatus(UUID licenseOfCustomerId);
}
