package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.bean.SmsConfigMasterId;

public interface SmsMasterRepository  extends JpaRepository<SmsConfigMaster, SmsConfigMasterId>, JpaSpecificationExecutor<SmsConfigMaster>{

}
