package com.spring.jwt.service;

import com.spring.jwt.Interfaces.ILicenseOfCustomer;
import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.FilterDto;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import com.spring.jwt.entity.Customer;
import com.spring.jwt.entity.LicenseList;
import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.entity.Status;

import com.spring.jwt.exception.PageNotFoundException;
import com.spring.jwt.repository.CustomerRepository;
import com.spring.jwt.repository.LicenseOfCustomerRepository;
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LicenseOfCustomerImpl implements ILicenseOfCustomer {

    @Autowired
    private LicenseOfCustomerRepository licenseOfCustomerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    public CustomerDTO updateStatus(UUID licenseOfCustomerId, String status) {
        LicenseOfCustomer licenseOfCustomer = licenseOfCustomerRepository.findById(licenseOfCustomerId)
                .orElseThrow(() -> new RuntimeException("License not found with ID: " + licenseOfCustomerId));

        // Check if the current status is ACTIVE
        if (licenseOfCustomer.getStatus() == Status.ACTIVE)
        {
            throw new RuntimeException("Cannot change status. The license is already ACTIVE.");
        }

        // Check if the current status is PENDING
        if (licenseOfCustomer.getStatus() == Status.PENDING)
        {
            if (!status.equalsIgnoreCase("ACTIVE") && !status.equalsIgnoreCase("REJECTED")) {
                throw new RuntimeException("Cannot change status from PENDING to " + status + ". Only ACTIVE or REJECTED are allowed.");
            }
        }

        // Check if the current status is REJECTED
        if (licenseOfCustomer.getStatus() == Status.REJECTED) {
            // Allow transition to ACTIVE or PENDING
            if (!status.equalsIgnoreCase("ACTIVE") && !status.equalsIgnoreCase("PENDING")) {
                throw new RuntimeException("Cannot change status from REJECTED to " + status + ". Only ACTIVE or PENDING are allowed.");
            }
        }

        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }

        // Directly set the new status
        licenseOfCustomer.setStatus(newStatus);

        if (newStatus == Status.ACTIVE) {
            licenseOfCustomer.setIssueDate(LocalDate.now());
            LicenseList licenseList = licenseOfCustomer.getLicense();
            if (licenseList == null || licenseList.getValidTill() == null || licenseList.getValidTill() <= 0) {
                throw new RuntimeException("Invalid or missing 'validTill' in LicenseList.");
            }
            licenseOfCustomer.setExpiryDate(LocalDate.now().plusYears(licenseList.getValidTill()));
        }

        licenseOfCustomerRepository.save(licenseOfCustomer);  // Save the updated license

        // Map customer to DTO
        Customer customer = licenseOfCustomer.getCustomer();
        if (customer == null) {
            throw new RuntimeException("No associated customer found for this license.");
        }

        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
        customerDTO.setLicenseOfCustomerDTOS(customer.getLicence().stream()
                .map(lic -> modelMapper.map(lic, LicenseOfCustomerDTO.class))
                .collect(Collectors.toList()));

        return customerDTO;
    }

    @Override
    public List<LicenseOfCustomerDTO> getAllLicenseOfCustomer() {
        List<LicenseOfCustomerDTO> licenseOfCustomerDTOs = new ArrayList<>();

        List<LicenseOfCustomer> licenseOfCustomers = licenseOfCustomerRepository.findAll();

        for (LicenseOfCustomer license : licenseOfCustomers) {
            // Check if the expiry date is in the past and update status if necessary
            if (license.getExpiryDate() != null && license.getExpiryDate().isBefore(LocalDate.now())) {
                license.setStatus(Status.RENEW);
                // Save the updated status to the repository
                licenseOfCustomerRepository.save(license);
            }

            LicenseOfCustomerDTO licenseDTO = modelMapper.map(license, LicenseOfCustomerDTO.class);

            // Map Customer
            if (license.getCustomer() != null) {
                CustomerDTO customerDTO = modelMapper.map(license.getCustomer(), CustomerDTO.class);
                licenseDTO.setCustomer(customerDTO);
            }

            // Map License List
            if (license.getLicense() != null) {
                LicenseListDTO licenseListDTO = modelMapper.map(license.getLicense(), LicenseListDTO.class);
                licenseDTO.setLicenseList(licenseListDTO);
            }

            // Update expiry date if status is RENEW
            if (license.getStatus() == Status.RENEW) {
                LicenseList licenseList = license.getLicense();
                if (licenseList != null && licenseList.getValidTill() != null && licenseList.getValidTill() > 0) {
                    license.setExpiryDate(LocalDate.now().plusYears(licenseList.getValidTill()));
                    licenseOfCustomerRepository.save(license); // Save the updated expiry date
                }
            }

            licenseOfCustomerDTOs.add(licenseDTO);
        }

        return licenseOfCustomerDTOs;
    }




//    @Override
//    public CustomerDTO updateStatus(UUID licenseOfCustomerId, String status) {
//
//        LicenseOfCustomer licenseOfCustomer = licenseOfCustomerRepository.findById(licenseOfCustomerId)
//                .orElseThrow(() -> new RuntimeException("Licence not found with ID: " + licenseOfCustomerId));
//
//        Status newStatus;
//        try {
//            newStatus = Status.valueOf(status.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid status value: " + status);
//        }
//
//        // Directly set the new status without any validation
//        licenseOfCustomer.setStatus(newStatus);
//
//        if (newStatus == Status.ACTIVE) {
//            licenseOfCustomer.setIssueDate(LocalDate.now());
//            LicenseList licenseList = licenseOfCustomer.getLicense();
//            if (licenseList == null || licenseList.getValidTill() == null || licenseList.getValidTill() <= 0) {
//                throw new RuntimeException("Invalid or missing 'validTill' in LicenseList.");
//            }
//            licenseOfCustomer.setExpiryDate(LocalDate.now().plusYears(licenseList.getValidTill()));
//        }
//
//        licenseOfCustomerRepository.save(licenseOfCustomer);  // Save the updated license
//
//        // Map customer to DTO
//        Customer customer = licenseOfCustomer.getCustomer();
//        if (customer == null) {
//            throw new RuntimeException("No associated customer found for this license.");
//        }
//
//        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
//        customerDTO.setLicenseOfCustomerDTOS(customer.getLicence().stream()
//                .map(lic -> modelMapper.map(lic, LicenseOfCustomerDTO.class))
//                .collect(Collectors.toList()));
//
//        return customerDTO;
//    }

    @Override
    public List<LicenseOfCustomerDTO> findByStatus(String status) {
        Status st = Status.valueOf(status.toUpperCase());
        List<LicenseOfCustomer> ll = licenseOfCustomerRepository.findByStatus(st);
        List<LicenseOfCustomerDTO> li = new ArrayList<>();
        for (LicenseOfCustomer ofCustomer : ll) {
            li.add(modelMapper.map(ofCustomer, LicenseOfCustomerDTO.class));
        }
        return li;
    }

//    @Override
//    public List<LicenseOfCustomerDTO> getAllLicenseOfCustomer() {
//        List<LicenseOfCustomerDTO> licenseOfCustomerDTOs = new ArrayList<>();
//
//        List<LicenseOfCustomer> licenseOfCustomers = licenseOfCustomerRepository.findAll();
//
//        for (LicenseOfCustomer license : licenseOfCustomers) {
//            LicenseOfCustomerDTO licenseDTO = modelMapper.map(license, LicenseOfCustomerDTO.class);
//
//            if (license.getCustomer() != null) {
//                CustomerDTO customerDTO = modelMapper.map(license.getCustomer(), CustomerDTO.class);
//                licenseDTO.setCustomer(customerDTO);
//            }
//            if (license.getLicense() != null) {
//                LicenseListDTO licenseListDTO = modelMapper.map(license.getLicense(), LicenseListDTO.class);
//                licenseDTO.setLicenseList(licenseListDTO);
//            }
//
//            licenseOfCustomerDTOs.add(licenseDTO);
//
//        }
//            return licenseOfCustomerDTOs;
//
//    }


    @Override
    public List<LicenseOfCustomerDTO> searchByFilterPage(FilterDto filterDto, Integer pageNo, Integer pageSize) {
        Specification<LicenseOfCustomer> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by license name
            if (filterDto.getLicenseName() != null && !filterDto.getLicenseName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("licenseName")),
                        "%" + filterDto.getLicenseName().toLowerCase() + "%"
                ));
            }

            // Filter by status
            if (filterDto.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filterDto.getStatus()));
            }

            // Filter by issue date
            if (filterDto.getIssueDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("issueDate"), filterDto.getIssueDate()));
            }

            // Filter by expiry date
            if (filterDto.getExpiryDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expiryDate"), filterDto.getExpiryDate()));
            }

            // Filter by customer fields
            if (filterDto.getCustomer() != null) {
                CustomerDTO customer = filterDto.getCustomer();

                if (customer.getCustomerId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), customer.getCustomerId()));
                }
                if (customer.getFirstName() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("customer").get("firstName")),
                            "%" + customer.getFirstName().toLowerCase() + "%"
                    ));
                }
                if (customer.getLastName() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("customer").get("lastName")),
                            "%" + customer.getLastName().toLowerCase() + "%"
                    ));
                }
                if (customer.getMobileNumber() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("customer").get("mobileNumber"), customer.getMobileNumber()));
                }
                if (customer.getEmail() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("customer").get("email")),
                            "%" + customer.getEmail().toLowerCase() + "%"
                    ));
                }
                if (customer.getArea() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("customer").get("area")),
                            "%" + customer.getArea().toLowerCase() + "%"
                    ));
                }
                if (customer.getCity() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("customer").get("city")),
                            "%" + customer.getCity().toLowerCase() + "%"
                    ));
                }
                if (customer.getState() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("customer").get("state")),
                            "%" + customer.getState().toLowerCase() + "%"
                    ));
                }
                if (customer.getPincode() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("customer").get("pincode"), customer.getPincode()));
                }
                if (customer.getPresent() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("customer").get("present"), customer.getPresent()));
                }
            }


            query.orderBy(criteriaBuilder.desc(root.get("id")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize); // Zero-based indexing for pagination
        Page<LicenseOfCustomer> licensePage = licenseOfCustomerRepository.findAll(spec, pageable);

        if (licensePage.isEmpty()) {
            throw new PageNotFoundException("No licenses found for the specified filter and page.");
        }

        return licensePage.stream()
                .map(LicenseOfCustomerDTO::new)
                .toList();
    }

    @Override
    public LicenseListDTO deleteById(UUID licenseOfCustomerId) {
      LicenseOfCustomer licenseOfCustomer=  licenseOfCustomerRepository.findById(licenseOfCustomerId)
              .orElseThrow(() -> new RuntimeException("Customer not found with ID: " +licenseOfCustomerId));
       licenseOfCustomerRepository.delete(licenseOfCustomer);
       return null;
    }

    @Override
    public List<LicenseOfCustomerDTO> getByMailID(String mailID) {
        // Fetch list of customers by email
        List<Customer> customers = customerRepository.findByEmail(mailID);
        if (customers.isEmpty()) {
            throw new RuntimeException("Customer Not Found by mailID: " + mailID);
        }

        // Take the first customer from the list
        Customer customer = customers.get(0);

        // Fetch all licenses associated with the customer
        List<LicenseOfCustomer> licenses = licenseOfCustomerRepository.findByCustomer(customer);
        List<LicenseOfCustomerDTO> licenseDTOs = new ArrayList<>();

        // Convert to DTO using for loop
        for (LicenseOfCustomer license : licenses) {
            licenseDTOs.add(modelMapper.map(license, LicenseOfCustomerDTO.class));
        }

        return licenseDTOs;
    }
}










