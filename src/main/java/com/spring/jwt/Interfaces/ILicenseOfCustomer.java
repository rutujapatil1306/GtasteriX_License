package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.FilterDto;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import com.spring.jwt.entity.Status;

import java.util.List;
import java.util.UUID;

public interface ILicenseOfCustomer {

    CustomerDTO updateStatus(UUID licenseOfCustomerId,String Status);


    List<LicenseOfCustomerDTO> findByStatus(String status);

    List<LicenseOfCustomerDTO> getAllLicenseOfCustomer();

    List<LicenseOfCustomerDTO> searchByFilterPage(FilterDto license, Integer pageNo, Integer pageSize);

    LicenseListDTO deleteById(UUID licenseOfCustomerId);

    List<LicenseOfCustomerDTO> getByMailID(String mailID);
}
