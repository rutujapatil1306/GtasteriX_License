package com.spring.jwt.service;


import com.spring.jwt.Interfaces.ILicenseList;
import com.spring.jwt.dto.LicenseListDTO;
import com.spring.jwt.entity.LicenseList;
import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.entity.isPresent;
import com.spring.jwt.repository.CustomerRepository;
import com.spring.jwt.repository.LicenseListRepository;
import com.spring.jwt.repository.LicenseOfCustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public LicenseListDTO saveLicense(LicenseListDTO licenseListDTO, MultipartFile images) throws IOException {
        LicenseList licenseList = modelMapper.map(licenseListDTO, LicenseList.class);
        licenseList.setPresent(isPresent.AVAILABLE);

        byte [] image = images.getBytes();

        if (image.length == 0){
            throw new RuntimeException("Image Can not be null");
        }

        if (images != null && !images.isEmpty()) {
            licenseList.setImages(images.getBytes());
        }

        LicenseList savedLicense = licenseListRepository.save(licenseList);

        return modelMapper.map(savedLicense, LicenseListDTO.class);
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

        LicenseList license = licenseListRepository.findById(licenseListID)
                .orElseThrow(() -> new RuntimeException("License not found with ID: " + licenseListID));


        List<LicenseOfCustomer> licenseOfCustomers = repository.findByLicense_LicenseID(licenseListID);


        for (LicenseOfCustomer licenseOfCustomer : licenseOfCustomers) {
            licenseOfCustomer.setLicense(null);
            repository.save(licenseOfCustomer);
        }


        licenseListRepository.delete(license);
    }

    @Override
    public LicenseListDTO getLicenseListByID(UUID licenseID) {

        LicenseList licenseList = licenseListRepository.findById(licenseID)
                .orElseThrow(() -> new RuntimeException("License with ID " +licenseID + "Not Found"));

        return modelMapper.map(licenseList, LicenseListDTO.class);

    }

    @Override
    public LicenseListDTO updateEnum(UUID licenseId, String present) {

        isPresent availability;
        try {
            availability = isPresent.valueOf(present.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for 'present': " + present);
        }

        LicenseList list = licenseListRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("License with ID " + licenseId + " Not Found"));


        if (list.getPresent() == isPresent.AVAILABLE) {
            list.setPresent(isPresent.UNAVAILABLE);
        } else {
            list.setPresent(isPresent.AVAILABLE);
        }
        list = licenseListRepository.save(list);
        return modelMapper.map(list, LicenseListDTO.class);
    }

    @Override
    public List<LicenseListDTO> saveLicense(List<LicenseListDTO> list){
        List<LicenseList> license=new ArrayList<>();
        for(LicenseListDTO dto:list){
            license.add(modelMapper.map(dto,LicenseList.class));
        }
        for (LicenseList licenseList : license) {
            licenseListRepository.save(licenseList);
        }
        for(LicenseList licenseList:license){
            list.add(modelMapper.map(licenseList,LicenseListDTO.class));
        }
        return list;
    }

}
