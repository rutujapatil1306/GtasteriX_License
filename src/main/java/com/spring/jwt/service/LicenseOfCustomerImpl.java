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


//    public CustomerDTO updateStatus(UUID licenseOfCustomerId,String status) {
//
//
//        LicenseOfCustomer licenseOfCustomer = licenseOfCustomerRepository.findById(licenseOfCustomerId)
//                .orElseThrow(() -> new RuntimeException("Licence not found with ID: " + licenseOfCustomerId));
//
//        if (licenseOfCustomer.getStatus() == Status.PENDING) {
//            licenseOfCustomer.setStatus(Status.ACTIVE);
//
//            licenseOfCustomer.setIssueDate(LocalDate.now());
//
//            LicenseList licenseList = licenseOfCustomer.getLicense();
//
//            if (licenseList != null) {
//                Integer validTill = licenseList.getValidTill();
//                if (validTill != null && validTill > 0) {
//                    licenseOfCustomer.setExpiryDate(LocalDate.now().plusYears(validTill));
//                } else {
//                    throw new RuntimeException("Invalid 'validTill' value. It must be a positive number.");
//                }
//            } else {
//                throw new RuntimeException("No associated LicenseList found for this licence");
//            }
//        } else {
//            throw new RuntimeException("Status can only be updated when the current status is PENDING");
//        }
//
//        licenseOfCustomerRepository.save(licenseOfCustomer);
//
//        Customer customer = licenseOfCustomer.getCustomer();
//        if (customer == null) {
//            throw new RuntimeException("No associated customer found for this licence");
//        }
//
//        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
//
//        List<LicenseOfCustomerDTO> licenseOfCustomers = new ArrayList<>();
//        for (LicenseOfCustomer customerLicence : customer.getLicence()) {
//            LicenseOfCustomerDTO licenceDTO = modelMapper.map(customerLicence, LicenseOfCustomerDTO.class);
//            licenseOfCustomers.add(licenceDTO);
//        }
//
//        customerDTO.setLicenceDTOS(licenseOfCustomers);
//
//        return customerDTO;
//    }

    public CustomerDTO updateStatus(UUID licenseOfCustomerId, String status) {

        LicenseOfCustomer licenseOfCustomer = licenseOfCustomerRepository.findById(licenseOfCustomerId)
                .orElseThrow(() -> new RuntimeException("Licence not found with ID: " + licenseOfCustomerId));

        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }

        // Directly set the new status without any validation
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
    public List<LicenseOfCustomerDTO> findByStatus(String status) {
        Status st = Status.valueOf(status.toUpperCase());
        List<LicenseOfCustomer> ll = licenseOfCustomerRepository.findByStatus(st);
        List<LicenseOfCustomerDTO> li = new ArrayList<>();
        for (LicenseOfCustomer ofCustomer : ll) {
            li.add(modelMapper.map(ofCustomer, LicenseOfCustomerDTO.class));
        }
        return li;
    }

    @Override
    public List<LicenseOfCustomerDTO> getAllLicenseOfCustomer() {
        List<LicenseOfCustomerDTO> licenseOfCustomerDTOs = new ArrayList<>();

        List<LicenseOfCustomer> licenseOfCustomers = licenseOfCustomerRepository.findAll();

        for (LicenseOfCustomer license : licenseOfCustomers) {
            LicenseOfCustomerDTO licenseDTO = modelMapper.map(license, LicenseOfCustomerDTO.class);

            if (license.getCustomer() != null) {
                CustomerDTO customerDTO = modelMapper.map(license.getCustomer(), CustomerDTO.class);
                licenseDTO.setCustomer(customerDTO);
            }
            if (license.getLicense() != null) {
                LicenseListDTO licenseListDTO = modelMapper.map(license.getLicense(), LicenseListDTO.class);
                licenseDTO.setLicenseList(licenseListDTO);
            }

            licenseOfCustomerDTOs.add(licenseDTO);

        }
            return licenseOfCustomerDTOs;

    }

//    @Override
//    public List<LicenseOfCustomerDTO> searchByFilterPage(FilterDto filterDto, Integer pageNo, Integer pageSize) {
//        Specification<LicenseOfCustomer> spec = (root, query, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            // Filtering by license name
//            if (filterDto.getLicenseName() != null && !filterDto.getLicenseName().isEmpty()) {
//                predicates.add(criteriaBuilder.like(
//                        criteriaBuilder.lower(root.get("licenseName")),
//                        "%" + filterDto.getLicenseName().toLowerCase() + "%"
//                ));
//            }
//
//            // Filtering by status
////            if (filterDto.getStatus() != null) {
//               // predicates.add(criteriaBuilder.equal(root.get("status"), filterDto.getStatus()));
////            }
//
//            // Filtering by issue date
//            if (filterDto.getIssueDate() != null) {
//                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("issueDate"), filterDto.getIssueDate()));
//            }
//
//            // Filtering by expiry date
//            if (filterDto.getExpiryDate() != null) {
//                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expiryDate"), filterDto.getExpiryDate()));
//            }
//
//            // Filtering by customer details (if applicable)
//            if (filterDto.getCustomer() != null && filterDto.getCustomer().getCustomerId() != null) {
//                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), filterDto.getCustomer().getCustomerId()));
//            }
////            Predicate statusPredicate = criteriaBuilder.or(
////                    criteriaBuilder.equal(root.get("status"), Status.ACTIVE),
////                    criteriaBuilder.equal(root.get("status"), Status.PENDING),
////                    criteriaBuilder.equal(root.get("status"),Status.REJECTED ),
////                    criteriaBuilder.equal(root.get("status"),Status.RENEW)
////            );
////            predicates.add(statusPredicate);
//            // Query sorting by most recent license ID
//            query.orderBy(criteriaBuilder.desc(root.get("id")));
//
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//        };
//
//        // Create pageable object
//        Pageable pageable = PageRequest.of(pageNo - 1, pageSize); // Convert to zero-based index for page number
//
//        // Fetch filtered data from repository
//        Page<LicenseOfCustomer> licensePage = licenseOfCustomerRepository.findAll(spec, pageable);
//
//        // Check if the result is empty
//        if (licensePage.isEmpty()) {
//            throw new PageNotFoundException("No licenses found for the specified filter and page.");
//        }
//        // Convert entity to DTO and return
//        List<LicenseOfCustomerDTO> licenseDtoList = licensePage.stream()
//                .map(LicenseOfCustomerDTO::new)
//                .toList();
//
//        return licenseDtoList;
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










