package com.maan.eway.mtpintegration.mtppayment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.mtpintegration.mtppayment.entity.MtpConfigMaster;

public interface MtpConfigMasterRepository extends JpaRepository<MtpConfigMaster, Long> {
    Optional<MtpConfigMaster> findByCompanyIdAndStatus(String companyId, String status);
    
 // MtpConfigMasterRepository.java
    Optional<MtpConfigMaster> findFirstByCompanyIdAndStatusOrderByIdDesc(String companyId, String status);
}
