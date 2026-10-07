package com.maan.eway.mtpintegration.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.mtpintegration.entity.MtpErrorLog;



public interface MtpErrorLogRepository extends JpaRepository<MtpErrorLog, Long> {
}