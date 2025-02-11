package com.spring.jwt.service;

import com.spring.jwt.entity.LicenseOfCustomer;
import com.spring.jwt.entity.Status;
import com.spring.jwt.repository.LicenseOfCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LicenseService
{

    @Autowired
    private LicenseOfCustomerRepository licenseOfCustomerRepository;

    @Scheduled(fixedRate = 86400000) // Runs every 24 hours (86400000 milliseconds)
    // @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredLicenses() {
        List<LicenseOfCustomer> licenses = licenseOfCustomerRepository.findAll();

        for (LicenseOfCustomer license : licenses) {
            // Check if the expiry date is in the past
            if (license.getExpiryDate() != null && license.getExpiryDate().isBefore(LocalDate.now())) {
                license.setStatus(Status.RENEW);
                // Save the updated status to the repository
                licenseOfCustomerRepository.save(license);
            }
        }
    }
}