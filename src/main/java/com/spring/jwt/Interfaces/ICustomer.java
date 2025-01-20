package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.CustomerDTO;

import java.util.List;
import java.util.UUID;

public interface ICustomer {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

   // CustomerDTO createStatus(UUID customerId,UUID licenceId);

    CustomerDTO getCustomerWithLicenses(UUID customerId);

    CustomerDTO assignLicenceAndSetStatus(UUID customerId, UUID licenceId);

    List<CustomerDTO> getAllCustomers();

    List<CustomerDTO> searchCustomerByName(String name);

    List<CustomerDTO> getByFilter(String name, String area,String email);
}
