package com.spring.jwt.dto;

import com.spring.jwt.entity.LicenseOfCustomer;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LicenseListDTO {

    private UUID licenseID;

    private String licenseName;

    private Integer validTill;

  //  private List<LicenseOfCustomer> licence;
}
