package com.maan.eway.mtpintegration.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.mtpintegration.entity.MtpPolicyDetails;

@Repository
public interface MtpPolicyDetailsRepository extends JpaRepository<MtpPolicyDetails, String> {

    Optional<MtpPolicyDetails> findByRegno(String regno);

    boolean existsByRegno(String regno);
}
