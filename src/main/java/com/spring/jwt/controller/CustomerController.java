package com.spring.jwt.controller;

import com.spring.jwt.Interfaces.ICustomer;
import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.entity.Customer;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.utils.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/customer")
public class CustomerController {

    @Autowired
    private ICustomer icustomer;

    @PostMapping("/saveCustomer")
    public ResponseEntity<BaseResponseDTO> saveCustomer(@RequestBody CustomerDTO customerDTO ){
        try{
            CustomerDTO customer=icustomer.saveCustomer(customerDTO);
            BaseResponseDTO responseDTO=new BaseResponseDTO(customer,"Success","successfully get this ");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        catch(Exception e){
            BaseResponseDTO errorResponseDTO = new BaseResponseDTO(e.getMessage(),"ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }

    }

    @GetMapping("/getAllCustomer")
    public ResponseEntity<BaseResponseDTO> getAllCustomer() {
        try {
            List<CustomerDTO> customers = icustomer.getAllCustomers();
            BaseResponseDTO response = new BaseResponseDTO(customers,"SUCCESS","Customers retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO errorResponse = new BaseResponseDTO(e.getMessage(),"ERROR","Failed to retrieve customers: ");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/getCustomerWithLicenses")
    public ResponseEntity<BaseResponseDTO> getCustomerWithLicenses(@RequestParam UUID customerId) {
        try {
            CustomerDTO customerDTO = icustomer.getCustomerWithLicenses(customerId);
            BaseResponseDTO response = new BaseResponseDTO(customerDTO, "SUCCESS", "Customer and licenses retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO errorResponse = new BaseResponseDTO(e.getMessage(), "ERROR", "Customer not found with ID: " + customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PatchMapping(value = "/assignLicence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponseDTO> assignLicenceToCustomer(@RequestParam UUID customerId,
                                                                   @RequestParam UUID licenceId,
                                                                   @RequestParam("images") List<MultipartFile> imageFiles) {
        try {
            CustomerDTO updatedCustomer = icustomer.assignLicenceAndSetStatus(customerId, licenceId, imageFiles);
            BaseResponseDTO responseDTO = new BaseResponseDTO(updatedCustomer, "SUCCESS", "Licence assigned successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            BaseResponseDTO errorResponseDTO = new BaseResponseDTO(null, "ERROR", "Operation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }
    }


    @GetMapping("/getByName")
    public ResponseEntity<BaseResponseDTO> getCustomerByName(@RequestParam String customerName) {
        try {
            List<CustomerDTO> customers = icustomer.searchCustomerByName(customerName);
            return ResponseEntity.ok(new BaseResponseDTO(customers, "SUCCESS", "Customers fetched successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO(null, "ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO(null, "ERROR", e.getMessage()));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<BaseResponseDTO> getByFilter( @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String area,
                                                        @RequestParam(required = false) String email){
        try{
            List<CustomerDTO> list=icustomer.getByFilter(name,area,email);
            return ResponseEntity.ok(new BaseResponseDTO(list,"ALL OK","Customer list Successfully"));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponseDTO(null,"ERROR",e.getMessage()));
        }
    }

    @PatchMapping("/UpdateLicense")
    public ResponseEntity<BaseResponseDTO> assignLicenceToCustomer(@RequestParam UUID customerId,
                                                                   @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = icustomer.UpdateCustomerDetail(customerId,customerDTO);
            BaseResponseDTO responseDTO = new BaseResponseDTO(updatedCustomer, "SUCCESS", "Licence Updated Successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (RuntimeException e) {
            BaseResponseDTO errorResponseDTO = new BaseResponseDTO(null, "ERROR", "Failed to Update : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }
    }

    @DeleteMapping("/DeleteCustomerById")
    public ResponseEntity<BaseResponseDTO> DeleteCustomer(@RequestParam UUID CustomerId){
        try{
            CustomerDTO customerDTO= icustomer.deleteCustomer(CustomerId);
            BaseResponseDTO responseDTO=new BaseResponseDTO(customerDTO,"success","Delete Succesfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }catch (Exception e){
            BaseResponseDTO responseDTO=new BaseResponseDTO(e.getMessage(),"Error","Failed To Delete");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }

    @PatchMapping("/updateCustomerStatus")
    public ResponseEntity<BaseResponseDTO> updateEnum(@RequestParam UUID customerId,@RequestParam String present){
        try{
            CustomerDTO dto=icustomer.updateEnum(customerId,present);
            BaseResponseDTO responseDTO=new BaseResponseDTO(dto,"SUCCESS","Licence Update Successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        }
        catch(Exception e){
            BaseResponseDTO err=new BaseResponseDTO(e.getMessage(),"ERROR","Licence not updated Successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);

        }
    }

    @PostMapping("/SaveCustomerList")
    public ResponseEntity<BaseResponseDTO> saveCustomerList (@RequestBody List<CustomerDTO> customerDTOList) {
        try {
            List<CustomerDTO> customerDTOList1 = icustomer.saveCustomerList(customerDTOList);
            BaseResponseDTO responseDTO = new BaseResponseDTO(customerDTOList1, "Success", "successfully get this ");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            BaseResponseDTO errorResponseDTO = new BaseResponseDTO(e.getMessage(), "ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }
    }

    @GetMapping("/getAllCustomerCount")
    public ResponseEntity<BaseResponseDTO> getAllCustomersCount()
    {
        try {
            Map<String, Long> customerCounts = icustomer.getAllCustomersCount();
            BaseResponseDTO response = new BaseResponseDTO(customerCounts, "SUCCESS", "Customer counts retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO errorResponse = new BaseResponseDTO(null, "ERROR", "Failed to retrieve customers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
