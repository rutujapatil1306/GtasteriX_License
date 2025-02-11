package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.LicenseListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ILicenseList  {

    LicenseListDTO saveLicense(LicenseListDTO licenseListDTO, MultipartFile images) throws IOException;

    List<LicenseListDTO> getAllLicense();

    void deleteLicenseById(UUID licenseListID);

    LicenseListDTO getLicenseListByID(UUID licenseID);

    LicenseListDTO updateEnum(UUID licenseID,String present);

    List<LicenseListDTO> saveLicense(List<LicenseListDTO> list);
}
