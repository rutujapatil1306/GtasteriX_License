package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
public class LicenseList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID licenseID;

    @Column(nullable = false)
    private String licenseName;

    @Column(nullable = false)
    private Integer validTill;


}
