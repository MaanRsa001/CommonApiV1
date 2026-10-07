package com.maan.eway.mtpintegration.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.mtpintegration.entity.MtpApiAudit;

@Repository
public interface MtpApiAuditRepository
        extends JpaRepository<MtpApiAudit, Long> {
	
	List<MtpApiAudit> findByQuoteNo(String quoteNo);
    Optional<MtpApiAudit> findTopByQuoteNoOrderByCreatedAtDesc(String quoteNo);
}