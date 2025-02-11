package com.spring.jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.Interfaces.ILicenseList;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.utils.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping("/api/licenseList")
public class LicenseListController {

@Autowired
private ILicenseList iLicenseList;

//admin
    @PostMapping("/saveLicense")
    public ResponseEntity<BaseResponseDTO> saveLicense(@RequestPart String licenseListDTOString,
                                                       @RequestPart MultipartFile images)
    {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            LicenseListDTO licenseListDTO2 = objectMapper.readValue(licenseListDTOString, LicenseListDTO.class);

            LicenseListDTO licenseListDTO1 = iLicenseList.saveLicense(licenseListDTO2,images);
            BaseResponseDTO responseDTO = new BaseResponseDTO(licenseListDTO1,"Sucess","Added License Sucessfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }catch (Exception e){
            BaseResponseDTO errorResponse = new BaseResponseDTO(e.getMessage(),"ERROR", "An error occurred: ");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //admin user
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

    @DeleteMapping("/deleteLicenseListByID")
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
    @PatchMapping("/updateLicenseStatus")
    public ResponseEntity<BaseResponseDTO> updateEnum(@RequestParam UUID licenseId,@RequestParam String present){
        try{
            LicenseListDTO dto=iLicenseList.updateEnum(licenseId,present);
            BaseResponseDTO responseDTO=new BaseResponseDTO(dto,"SUCCESS","Licence Update Successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        }

        catch(Exception e){
            BaseResponseDTO err=new BaseResponseDTO(e.getMessage(),"ERROR","Licence not updated Successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);

        }
    }

    @PostMapping("/SaveMultipleInfo")
    public ResponseEntity<BaseResponseDTO> createLicenseList(@RequestBody List<LicenseListDTO> LicenseListDTOList){
        try{
            List<LicenseListDTO> list=iLicenseList.saveLicense(LicenseListDTOList);
            BaseResponseDTO DTO=new BaseResponseDTO(list,"All Ok","License Saved Successfully");
            return ResponseEntity.status(HttpStatus.OK).body(DTO);
        }
        catch(Exception e){
            BaseResponseDTO DTO=new BaseResponseDTO(e.getMessage(),"ERROR","Failed to Saved License");
            return ResponseEntity.status(HttpStatus.OK).body(DTO);
        }
    }

}
