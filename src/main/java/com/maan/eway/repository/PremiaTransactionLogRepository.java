package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.PremiaTransactionLog;

public interface PremiaTransactionLogRepository  extends JpaRepository<PremiaTransactionLog,Long > , JpaSpecificationExecutor<PremiaTransactionLog>{

}
