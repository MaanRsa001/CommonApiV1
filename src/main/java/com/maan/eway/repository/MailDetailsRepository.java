package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.MailDetails;

public interface MailDetailsRepository extends JpaRepository<MailDetails, String>, JpaSpecificationExecutor<MailDetails>{



}
