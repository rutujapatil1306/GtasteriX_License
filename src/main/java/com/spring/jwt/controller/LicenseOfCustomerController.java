package com.spring.jwt.controller;

import com.spring.jwt.Interfaces.ILicenseOfCustomer;
import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.LicenseOfCustomerDTO;
import com.spring.jwt.utils.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

            BaseResponseDTO errorResponse = new BaseResponseDTO("An unexpected error occurred", "Error", "Status Not updated");
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

}
