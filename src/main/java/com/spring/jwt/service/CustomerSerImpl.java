package com.spring.jwt.service;

import com.spring.jwt.Interfaces.ICustomer;
import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import com.spring.jwt.entity.*;
import com.spring.jwt.repository.CustomerRepository;
import com.spring.jwt.repository.LicenseListRepository;
import com.spring.jwt.repository.LicenseOfCustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        customer.setPresent(isPresent.AVAILABLE);
        Customer customer1 = customerRepository.save(customer);
        return modelMapper.map(customer1, CustomerDTO.class);
    }

    @Override
    public CustomerDTO assignLicenceAndSetStatus(UUID customerId, UUID licenseID) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));


        LicenseList licenseList = licenseListRepository.findById(licenseID)
                .orElseThrow(() -> new RuntimeException("License not found with ID: " + licenseID));

        if(customer.getPresent()==isPresent.UNAVAILABLE){
            throw new RuntimeException("Customer is Inactive");
        }
        if(licenseList.getPresent() == isPresent.UNAVAILABLE){
            throw new RuntimeException("License  is Inactive");
        }

        Optional<LicenseOfCustomer> existingLicense = licenseOfCustomerRepository.findByCustomerIdAndLicenseId(customerId, licenseID);
        if (existingLicense.isPresent()) {
            throw new RuntimeException("Customer already has this license with ID: " + licenseID);
        }

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
        customerDTO.setLicenseOfCustomerDTOS(licenceDTOs);
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

    @Override
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

            customerDTO.setLicenseOfCustomerDTOS(licenceDTOs);
            customerDTOs.add(customerDTO);
        }

        return customerDTOs;
    }



    @Override
    public List<CustomerDTO> searchCustomerByName(String name) {
        List<Customer> foundCustomers = customerRepository.findByFirstNameContainingIgnoreCaseOrderByFirstNameAsc(name);
        System.out.println(foundCustomers.size());

        List<CustomerDTO> customerDTOs = new ArrayList<>();
        for (Customer customer : foundCustomers) {
            CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);
            customerDTOs.add(dto);
        }
        return customerDTOs;
    }

    @Override
    public List<CustomerDTO> getByFilter(String firstName, String area, String email) {
        List<Customer> customerList;
        if (firstName != null) {
            customerList = customerRepository.findByFirstName(firstName);
        } else if (area != null) {
            customerList = customerRepository.findByArea(area);
        } else if (email != null) {
            customerList = customerRepository.findByEmail(email);
        } else {
            customerList = customerRepository.findAll();
        }

        System.out.println("Number of customers found: " + customerList.size());

        return mapToDTOList(customerList);
    }


    private List<CustomerDTO> mapToDTOList(List<Customer> customers) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        for (Customer customer : customers) {
            customerDTOList.add(modelMapper.map(customer, CustomerDTO.class));
        }
        return customerDTOList;
    }

    @Override
    public CustomerDTO UpdateCustomerDetail(UUID customerId, CustomerDTO customerDTO) {
        Customer customer=customerRepository.findById(customerId).orElseThrow(()->new RuntimeException("Id Not Found"));

        if(customerDTO.getFirstName()!=null){
            customer.setFirstName(customerDTO.getFirstName());
        }
        if(customerDTO.getLastName()!=null){
            customer.setLastName(customerDTO.getLastName());
        }
        if(customerDTO.getEmail()!=null){
            customer.setEmail(customerDTO.getEmail());
        }
        if(customerDTO.getArea()!=null){
            customer.setArea(customerDTO.getArea());
        }
        if (customerDTO.getMobileNumber()!=null){
            customer.setMobileNumber(customerDTO.getMobileNumber());
        }
        if (customerDTO.getPincode()!=null){
            customer.setPincode(customerDTO.getPincode());
        }
        if (customerDTO.getCity()!=null){
            customer.setCity(customerDTO.getCity());
        }
        if(customerDTO.getState()!=null){
            customer.setState(customerDTO.getState());
        }
        Customer savecustomer=customerRepository.save(customer);
        return modelMapper.map(savecustomer,CustomerDTO.class);
    }

    @Override
    public CustomerDTO deleteCustomer(UUID customerId) {
        Customer customer= customerRepository.findById(customerId).
                orElseThrow(()->new RuntimeException(" Id not Found"+customerId));
        customerRepository.delete(customer);

        return null;
    }

    @Override
    public CustomerDTO updateEnum(UUID licenseId, String present) {

        isPresent availability;
        try {
            availability = isPresent.valueOf(present.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for 'present': " + present);
        }


        Customer list = customerRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("License with ID " + licenseId + " Not Found"));


        if (list.getPresent() == isPresent.AVAILABLE) {
            list.setPresent(isPresent.UNAVAILABLE);
        } else {
            list.setPresent(isPresent.AVAILABLE);
        }


        list = customerRepository.save(list);
        return modelMapper.map(list, CustomerDTO.class);
    }
//    @Override
//    public List<Customer> saveCustomerList(List<CustomerDTO> customerDTOList) {
//        List<Customer> customers = new ArrayList<>();
//
//        for (CustomerDTO customerDTO : customerDTOList) {
//            Customer customer = modelMapper.map(customerDTO, Customer.class);
//            customers.add(customer);
//        }
//        customerRepository.saveAll(customers);
//        return customers;
//    }
}








