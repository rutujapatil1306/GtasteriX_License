package com.spring.jwt.controller;

import com.spring.jwt.Interfaces.ILicenseList;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.utils.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/licenseList")
public class LicenseListController {

@Autowired
private ILicenseList iLicenseList;

    @PostMapping("/saveLicense")
    public ResponseEntity<BaseResponseDTO> saveLicense(@RequestBody LicenseListDTO licenseListDTO)
    {
        try{
            LicenseListDTO licenseListDTO1 = iLicenseList.saveLicense(licenseListDTO);
            BaseResponseDTO responseDTO = new BaseResponseDTO(licenseListDTO1,"Sucess","Added License Sucessfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }catch (Exception e){
            BaseResponseDTO errorResponse = new BaseResponseDTO(e.getMessage(),"ERROR", "An error occurred: ");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/getLicenseList")
    public ResponseEntity<BaseResponseDTO> getLicenseList()
    {
        try{
            List<LicenseListDTO> licenseListDTOS = iLicenseList.getAllLicense();
            BaseResponseDTO response = new BaseResponseDTO(licenseListDTOS,"SUCCESS", "List of All License");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            BaseResponseDTO errorResponseDTO = new BaseResponseDTO(e.getMessage(),"ERROR","List of License not Found:");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }
    }

    @GetMapping("/getLicenseListByID")
    public ResponseEntity<BaseResponseDTO> getLicenseListByID (@RequestParam UUID LicenseID)
    {
        try{
            LicenseListDTO licenseListDTO = iLicenseList.getLicenseListByID(LicenseID);
            BaseResponseDTO response = new BaseResponseDTO(licenseListDTO,"SUCCESS", "License Found BY ID");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            BaseResponseDTO errorResponseDTO = new BaseResponseDTO(e.getMessage(),"ERROR"," License not Found By ID:" + LicenseID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);

        }
    }

    @DeleteMapping("/deleteLicenseList")
    public ResponseEntity<BaseResponseDTO> deleteLicenseList(@RequestParam UUID licenseListID) {
        try {
            iLicenseList.deleteLicenseById(licenseListID);
            BaseResponseDTO response = new BaseResponseDTO(null, "SUCCESS", "License deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            BaseResponseDTO errorResponse = new BaseResponseDTO(e.getMessage(), "ERROR", "Failed to delete the license");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
