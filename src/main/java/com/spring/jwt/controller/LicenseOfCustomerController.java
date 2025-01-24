package com.spring.jwt.controller;

import com.spring.jwt.Interfaces.ILicenseOfCustomer;
import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.FilterDto;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import com.spring.jwt.entity.Status;
import com.spring.jwt.exception.PageNotFoundException;
import com.spring.jwt.utils.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("api/licenseOfCustomerController")

public class LicenseOfCustomerController {

    @Autowired
    private ILicenseOfCustomer iLicenseOfCustomer;

    @PatchMapping("/updateStatus")
    public ResponseEntity<BaseResponseDTO> updateStatus(@RequestParam UUID licenseOfCustomerId, @RequestParam String status) {
        try {
            System.out.println("Updating status for LicenseOfCustomerId: " + licenseOfCustomerId + ", New Status: " + status);

            CustomerDTO customerDTO = iLicenseOfCustomer.updateStatus(licenseOfCustomerId, status);

            BaseResponseDTO response = new BaseResponseDTO(customerDTO, "Success", "Status Updated Successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());

            BaseResponseDTO errorResponse = new BaseResponseDTO(e.getMessage(), "Error", "Status Not updated");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());

            BaseResponseDTO errorResponse = new BaseResponseDTO("An unexpected error occurred", "Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/byStatus")
    public ResponseEntity<BaseResponseDTO> statusBy(@RequestParam String status) {
        try {
            List<LicenseOfCustomerDTO> co = iLicenseOfCustomer.findByStatus(status);
            BaseResponseDTO bs = new BaseResponseDTO(co, "ALL OK", "By Status Get successfully");
            return ResponseEntity.status(HttpStatus.OK).body(bs);
        } catch (Exception e) {
            BaseResponseDTO errorResponseDTO = new BaseResponseDTO(e.getMessage(), "Error", "Status Not Get");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }
    }

    @GetMapping("/getAllLicenseOfCustomer")
    public ResponseEntity<BaseResponseDTO> getAllLicenseOfCustomer() {
        try {
            List<LicenseOfCustomerDTO> allLicenses = iLicenseOfCustomer.getAllLicenseOfCustomer();

            BaseResponseDTO response = new BaseResponseDTO(allLicenses, "ALL OK", "All LicenseOfCustomer fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            BaseResponseDTO errorResponse = new BaseResponseDTO(
                    e.getMessage(), "Error", "Unable to fetch LicenseOfCustomer");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/anyFilterApi")
    public ResponseEntity<BaseResponseDTO> getByAllFilter(
                                                          @RequestParam(required = false) String licenceName,
                                                          @RequestParam(required = false) String status,
                                                          @RequestParam(required = false) LocalDate issueDate,
                                                          @RequestParam(required = false) LocalDate expiryDate,
                                                          @RequestParam(required = false) CustomerDTO customerDTO,
                                                          @RequestParam(required = false) LicenseListDTO licenseListDTO,
                                                          @RequestParam (defaultValue = "1") Integer pageNo,
                                                          @RequestParam (defaultValue = "20")Integer pageSize){

        FilterDto license;
        Status k;
    try{
    k= Status.valueOf(status.toUpperCase());
        license=new FilterDto(licenceName,k,issueDate,expiryDate,customerDTO,licenseListDTO);

    }
    catch(Exception e){
        BaseResponseDTO errorResponse = new BaseResponseDTO("An unexpected error occurred", "Error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

    }

        try {
            List<LicenseOfCustomerDTO> listOfCar = iLicenseOfCustomer.searchByFilterPage(license, pageNo, pageSize);
            BaseResponseDTO responseAllCarDto = new BaseResponseDTO(listOfCar, "ALL OK", "All LicenseOfCustomer fetched successfully");

            return ResponseEntity.status(HttpStatus.OK).body(responseAllCarDto);
        } catch (PageNotFoundException pageNotFoundException) {
            BaseResponseDTO responseAllCarDto = new BaseResponseDTO( "An unexpected error occurred", "Error", pageNotFoundException.getMessage());;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseAllCarDto);
        } catch (Exception e) {
            BaseResponseDTO responseAllCarDto = new BaseResponseDTO("An unexpected error occurred", "Error", e.getMessage());;

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseAllCarDto);
        }



    }

}
