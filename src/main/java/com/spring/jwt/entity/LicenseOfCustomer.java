package com.spring.jwt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class LicenseOfCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID licenseOfCustomerId;

    @Column(nullable = false)
    private String licenseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

  //  @Column(nullable = false)
    private LocalDate issueDate;

//    @Column(nullable = false)
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", nullable = false)
    private LicenseList license;
}
