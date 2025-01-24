package com.spring.jwt.dto;

import com.spring.jwt.entity.Status;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FilterDto {
    private String licenseName;

    private Status status;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    private CustomerDTO customer;

    private LicenseListDTO licenseList;

    public FilterDto( String licenseName, Status status, LocalDate issueDate, LocalDate expiryDate, CustomerDTO customerDTO, LicenseListDTO licenseListDTO, Integer pageNo, Integer pageSize) {

        this.licenseName=licenseName;
        this.status=status;
        this.issueDate=issueDate;
        this.expiryDate=expiryDate;
        this.customer=customerDTO;

    }
}
