package com.spring.jwt.service;

import com.spring.jwt.Interfaces.ICustomer;
import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import com.spring.jwt.entity.Customer;
import com.spring.jwt.entity.LicenseList;
import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.entity.Status;
import com.spring.jwt.repository.CustomerRepository;
import com.spring.jwt.repository.LicenseListRepository;
import com.spring.jwt.repository.LicenseOfCustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerSerImpl implements ICustomer {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LicenseOfCustomerRepository licenseOfCustomerRepository;

    @Autowired
    private LicenseListRepository licenseListRepository;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO)
    {
        Customer customer = modelMapper.map(customerDTO, Customer.class);
        if (customerRepository.getAllMobileNumbers() != null) {
            for (int i = 0; i < customerRepository.getAllMobileNumbers().size(); i++) {
                if (customer.getMobileNumber().equals(customerRepository.getAllMobileNumbers().get(i))) {
                    throw new RuntimeException("User Already Exist");
                }
            }
        }
        Customer customer1 = customerRepository.save(customer);
        return modelMapper.map(customer1, CustomerDTO.class);
    }


    @Override
    public CustomerDTO assignLicenceAndSetStatus(UUID customerId, UUID licenseID) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        LicenseList licenseList = licenseListRepository.findById(licenseID)
                .orElseThrow(() -> new RuntimeException("License not found with ID: " + licenseID));

        LicenseOfCustomer licenseOfCustomer1 = new LicenseOfCustomer();
        licenseOfCustomer1.setLicense(licenseList);
        licenseOfCustomer1.setCustomer(customer);
        licenseOfCustomer1.setLicenseName(licenseList.getLicenseName());

        licenseOfCustomer1.setStatus(Status.PENDING);

        licenseOfCustomerRepository.save(licenseOfCustomer1);

        if (customer.getLicence() == null) {
            customer.setLicence(new ArrayList<>());
        }
        customer.getLicence().add(licenseOfCustomer1);

        Customer updatedCustomer = customerRepository.save(customer);

        CustomerDTO customerDTO = modelMapper.map(updatedCustomer, CustomerDTO.class);

        List<LicenseOfCustomerDTO> licenceDTOs = new ArrayList<>();
        for (LicenseOfCustomer lic : updatedCustomer.getLicence()) {
            LicenseOfCustomerDTO licenceDTO = new LicenseOfCustomerDTO();
            licenceDTO.setLicenseOfCustomerId(lic.getLicenseOfCustomerId());
            licenceDTO.setLicenseName(lic.getLicenseName());
            licenceDTO.setStatus(lic.getStatus());
            licenceDTOs.add(licenceDTO);
        }
        customerDTO.setLicenceDTOS(licenceDTOs);
        return customerDTO;
    }

    @Override
    public CustomerDTO getCustomerWithLicenses(UUID customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);

        List<LicenseOfCustomer> licenceDTOs = new ArrayList<>();
        for (LicenseOfCustomer licence : customer.getLicence()) {
            LicenseOfCustomerDTO licenceDTO = modelMapper.map(licence, LicenseOfCustomerDTO.class);
            licenceDTOs.add(licence);
        }
        customer.setLicence(licenceDTOs);
        return customerDTO;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        List<CustomerDTO> customerDTOs = new ArrayList<>();
        for (Customer customer : customers) {

            CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);

            List<LicenseOfCustomerDTO> licenceDTOs = new ArrayList<>();
            for (LicenseOfCustomer licenseOfCustomer : customer.getLicence()) {
                LicenseOfCustomerDTO licenceDTO = modelMapper.map(licenseOfCustomer, LicenseOfCustomerDTO.class);
                licenceDTOs.add(licenceDTO);
            }

            customerDTO.setLicenceDTOS(licenceDTOs);
            customerDTOs.add(customerDTO);
        }

        return customerDTOs;
    }







}


