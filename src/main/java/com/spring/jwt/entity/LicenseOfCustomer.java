package com.spring.jwt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class LicenseOfCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID licenseOfCustomerId;

    @Version
    private int version;

    @Column(nullable = false)
    private String licenseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "license_id")

    private LicenseList license;

    public LicenseOfCustomer(LicenseOfCustomerDTO customerDTO){
        ModelMapper mapper=new ModelMapper();
        this.licenseName= customerDTO.getLicenseName();
        this.status=customerDTO.getStatus();
        this.issueDate=customerDTO.getIssueDate();
        this.expiryDate=customerDTO.getExpiryDate();
        this.customer=mapper.map(customerDTO.getCustomer(),Customer.class);
        this.license=mapper.map(customerDTO.getLicenseList(), LicenseList.class);

    }

}
