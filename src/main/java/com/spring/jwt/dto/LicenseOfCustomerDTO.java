package com.spring.jwt.dto;

import com.spring.jwt.entity.LicenseList;
import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.entity.Status;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LicenseOfCustomerDTO {


        private UUID licenseOfCustomerId;

        private String licenseName;

        private Status status;

        private List<byte[]> images;

        private LocalDate issueDate;

        private LocalDate expiryDate;

        private CustomerDTO customer;

        private LicenseListDTO licenseList;

    public LicenseOfCustomerDTO(LicenseOfCustomer licenseOfCustomer) {
         ModelMapper mapper = new ModelMapper();
        this.licenseOfCustomerId = licenseOfCustomer.getLicenseOfCustomerId();
        this.licenseName = licenseOfCustomer.getLicenseName();
        this.status = licenseOfCustomer.getStatus();
        this.issueDate = licenseOfCustomer.getIssueDate();
        this.expiryDate = licenseOfCustomer.getExpiryDate();
        this.customer = mapper.map(licenseOfCustomer.getCustomer(),CustomerDTO.class);
        this.licenseList = mapper.map(licenseOfCustomer.getLicense(), LicenseListDTO.class);
    }



}


