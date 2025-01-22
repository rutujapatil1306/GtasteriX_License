package com.spring.jwt.service;


import com.spring.jwt.Interfaces.ILicenseList;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.entity.Customer;
import com.spring.jwt.entity.LicenseList;
import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.repository.CustomerRepository;
import com.spring.jwt.repository.LicenseListRepository;
import com.spring.jwt.repository.LicenseOfCustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LicenseListImpl implements ILicenseList {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LicenseListRepository licenseListRepository;

    @Autowired
   private LicenseOfCustomerRepository repository;

    @Autowired
    private CustomerRepository customerRepo;

    @Override
    public LicenseListDTO saveLicense(LicenseListDTO licenseListDTO)
    {
        LicenseList licenseList = modelMapper.map(licenseListDTO, LicenseList.class);
        LicenseList saveLicense = licenseListRepository.save(licenseList);
        return modelMapper.map(licenseList, LicenseListDTO.class);
    }

    @Override
    public List<LicenseListDTO> getAllLicense() {
        List<LicenseList> licenseList = licenseListRepository.findAll();
        List<LicenseListDTO> dtoList = new ArrayList<>();

        for (LicenseList license : licenseList) {
            LicenseListDTO dto = modelMapper.map(license, LicenseListDTO.class);
            dtoList.add(dto);
        }
        return dtoList;
    }





    @Transactional
    @Override
    public void deleteLicenseById(UUID licenseListID) {
        // Fetch the license from LicenseList
        LicenseList license = licenseListRepository.findById(licenseListID)
                .orElseThrow(() -> new RuntimeException("License not found with ID: " + licenseListID));

        // Fetch all LicenseOfCustomer entities associated with this license
        List<LicenseOfCustomer> licenseOfCustomers = repository.findByLicense_LicenseID(licenseListID);

        // Break the relationship between Customer and LicenseOfCustomer
        for (LicenseOfCustomer licenseOfCustomer : licenseOfCustomers) {
            Customer customer = licenseOfCustomer.getCustomer();
            if (customer != null) {
                customer.getLicence().remove(licenseOfCustomer); // Remove mapping
                customerRepo.save(customer); // Persist updated customer
            }
        }

        // Delete the LicenseOfCustomer entries
        for (LicenseOfCustomer licenseOfCustomer : licenseOfCustomers) {
            repository.delete(licenseOfCustomer);
        }

        // Finally, delete the license from LicenseList
        licenseListRepository.delete(license);
    }








    @Override
    public LicenseListDTO getLicenseListByID(UUID licenseID) {

        LicenseList licenseList = licenseListRepository.findById(licenseID)
                .orElseThrow(() -> new RuntimeException("License with ID " +licenseID + "Not Found"));

        return modelMapper.map(licenseList, LicenseListDTO.class);

    }


}
