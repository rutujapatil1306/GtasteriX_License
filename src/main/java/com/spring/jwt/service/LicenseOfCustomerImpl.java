package com.spring.jwt.service;

import com.spring.jwt.Interfaces.ILicenseOfCustomer;
import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import com.spring.jwt.entity.Customer;
import com.spring.jwt.entity.LicenseList;
import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.entity.Status;

import com.spring.jwt.repository.CustomerRepository;
import com.spring.jwt.repository.LicenseOfCustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class LicenseOfCustomerImpl implements ILicenseOfCustomer {

    @Autowired
    private LicenseOfCustomerRepository licenseOfCustomerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;


    public CustomerDTO updateStatus(UUID licenseOfCustomerId) {

        LicenseOfCustomer licenseOfCustomer = licenseOfCustomerRepository.findById(licenseOfCustomerId)
                .orElseThrow(() -> new RuntimeException("Licence not found with ID: " + licenseOfCustomerId));

        if (licenseOfCustomer.getStatus() == Status.PENDING) {
            licenseOfCustomer.setStatus(Status.ACTIVE);

            licenseOfCustomer.setIssueDate(LocalDate.now());

            LicenseList licenseList = licenseOfCustomer.getLicense();

            if (licenseList != null) {
                Integer validTill = licenseList.getValidTill();
                if (validTill != null && validTill > 0) {
                    licenseOfCustomer.setExpiryDate(LocalDate.now().plusYears(validTill));
                } else {
                    throw new RuntimeException("Invalid 'validTill' value. It must be a positive number.");
                }
            } else {
                throw new RuntimeException("No associated LicenseList found for this licence");
            }
        } else {
            throw new RuntimeException("Status can only be updated when the current status is PENDING");
        }

        licenseOfCustomerRepository.save(licenseOfCustomer);

        Customer customer = licenseOfCustomer.getCustomer();
        if (customer == null) {
            throw new RuntimeException("No associated customer found for this licence");
        }

        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);

        List<LicenseOfCustomerDTO> licenseOfCustomers = new ArrayList<>();
        for (LicenseOfCustomer customerLicence : customer.getLicence()) {
            LicenseOfCustomerDTO licenceDTO = modelMapper.map(customerLicence, LicenseOfCustomerDTO.class);
            licenseOfCustomers.add(licenceDTO);
        }

        customerDTO.setLicenceDTOS(licenseOfCustomers);

        return customerDTO;
    }

}


