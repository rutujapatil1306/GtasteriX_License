package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.LicenseListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ICustomer {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

   // CustomerDTO createStatus(UUID customerId,UUID licenceId);

    CustomerDTO assignLicenceAndSetStatus(UUID customerId, UUID licenseID, List<MultipartFile> imageFiles) throws IOException, IOException;

    CustomerDTO getCustomerWithLicenses(UUID customerId);

    //CustomerDTO assignLicenceAndSetStatus(UUID customerId, UUID licenceId);

    List<CustomerDTO> getAllCustomers();

    List<CustomerDTO> searchCustomerByName(String name);

    List<CustomerDTO> getByFilter(String name, String area,String email);

    CustomerDTO UpdateCustomerDetail(UUID customerId, CustomerDTO customerDTO);

    CustomerDTO deleteCustomer(UUID customerId);

    CustomerDTO updateEnum(UUID customerId, String present);

    List<CustomerDTO> saveCustomerList(List<CustomerDTO> customerDTOList);

    Map<String, Long> getAllCustomersCount();
}
