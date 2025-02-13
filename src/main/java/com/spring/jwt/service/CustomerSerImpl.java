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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    public CustomerDTO assignLicenceAndSetStatus(UUID customerId, UUID licenseID, List<MultipartFile> imageFiles) throws IOException, IOException {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        LicenseList licenseList = licenseListRepository.findById(licenseID)
                .orElseThrow(() -> new RuntimeException("License not found with ID: " + licenseID));

        if (customer.getPresent() == isPresent.UNAVAILABLE) {
            throw new RuntimeException("Customer is Inactive");
        }
        if (licenseList.getPresent() == isPresent.UNAVAILABLE) {
            throw new RuntimeException("License is Inactive");
        }

        Optional<LicenseOfCustomer> existingLicense = licenseOfCustomerRepository.findByCustomerIdAndLicenseId(customerId, licenseID);
        if (existingLicense.isPresent()) {
            throw new RuntimeException("Customer already has this license.");
        }

        LicenseOfCustomer licenseOfCustomer1 = new LicenseOfCustomer();
        licenseOfCustomer1.setLicense(licenseList);
        licenseOfCustomer1.setCustomer(customer);
        licenseOfCustomer1.setLicenseName(licenseList.getLicenseName());
        licenseOfCustomer1.setStatus(Status.PENDING);

        // Convert images to byte[]
        List<byte[]> imageData = new ArrayList<>();
        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                imageData.add(file.getBytes());
            }
        }
        licenseOfCustomer1.setImages(imageData);

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
            licenceDTO.setImages(lic.getImages()); // Include actual image data
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

        @Override
        public List<CustomerDTO> saveCustomerList(List<CustomerDTO> customerDTOList) {
            List<Customer> customers = new ArrayList<>();

            for (CustomerDTO customerDTO : customerDTOList) {
                Customer customer = modelMapper.map(customerDTO, Customer.class);

                if (customerRepository.getAllMobileNumbers() != null &&
                        customerRepository.getAllMobileNumbers().contains(customer.getMobileNumber())) {
                    throw new RuntimeException("User with mobile number " + customer.getMobileNumber() + " already exists");
                }

                customer.setPresent(isPresent.AVAILABLE);
                customers.add(customer);
            }

            List<Customer> savedCustomers = customerRepository.saveAll(customers);

            return savedCustomers.stream()
                    .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                    .collect(Collectors.toList());
        }

    @Override
    public Map<String, Long> getAllCustomersCount() {
        List<Customer> customers = customerRepository.findAll();

        long activeCount = 0, pendingCount = 0, rejectedCount = 0, renewCount = 0;
        long availableCount = 0, unavailableCount = 0;

        for (Customer customer : customers) {
            if (customer.getPresent() == isPresent.AVAILABLE) {
                availableCount++;
            } else if (customer.getPresent() == isPresent.UNAVAILABLE) {
                unavailableCount++;
            }

            for (LicenseOfCustomer license : customer.getLicence()) {
                switch (license.getStatus()) {
                    case ACTIVE: activeCount++; break;
                    case PENDING: pendingCount++; break;
                    case REJECTED: rejectedCount++; break;
                    case RENEW: renewCount++; break;
                    default: break;
                }
            }

        }

        List<LicenseOfCustomer> s= licenseOfCustomerRepository.findAll();
        long LicenseOftotalCustomers=s.size();

        long totalCustomers = activeCount + pendingCount + rejectedCount + renewCount + availableCount + unavailableCount;

        Map<String, Long> customerCountMap = new HashMap<>();
        customerCountMap.put("TOTAL_CUSTOMERS", totalCustomers);
        customerCountMap.put("TOTAL License Of CUSTOMERS", LicenseOftotalCustomers);

        customerCountMap.put("ACTIVE", activeCount);
        customerCountMap.put("PENDING", pendingCount);
        customerCountMap.put("REJECTED", rejectedCount);
        customerCountMap.put("RENEW", renewCount);
        customerCountMap.put("AVAILABLE", availableCount);
        customerCountMap.put("UNAVAILABLE", unavailableCount);

        return customerCountMap;
    }


}












