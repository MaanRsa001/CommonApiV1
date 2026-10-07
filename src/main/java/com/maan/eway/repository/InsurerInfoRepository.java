package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.InsurerInfo;
import com.maan.eway.bean.InsurerInfoId;

public interface InsurerInfoRepository  extends JpaRepository<InsurerInfo,InsurerInfoId > , JpaSpecificationExecutor<InsurerInfo> {
	 

	InsurerInfo findByCustomerId(String customerId);
 
	List<InsurerInfo> findByCompanyIdAndCustomerId(String companyId,String customerId);

	Long countByCustomerId(String customerId);
   
	@Transactional
	void deleteByCustomerId(String customerId);
	
	Long countByInsuredReferenceNo(String insuredReferenceNo);	
	
	void deleteByInsuredReferenceNo(String insuredReferenceNo);
} 
