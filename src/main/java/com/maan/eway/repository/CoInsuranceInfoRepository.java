package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.CoInsuranceInfo;
import com.maan.eway.bean.CoInsuranceInfoId;

public interface CoInsuranceInfoRepository extends JpaRepository<CoInsuranceInfo ,CoInsuranceInfoId> ,JpaSpecificationExecutor<CoInsuranceInfoId> {


	List<CoInsuranceInfo> findBySnoAndAmendidAndProductidAndRequestreferenceno(int sno, int amendid, int productid, String Requestreferenceno);
	
    void deleteByQuoteno(String quoteno);
    List<CoInsuranceInfo>  findByQuoteno(String QUOTENO);
	
}
