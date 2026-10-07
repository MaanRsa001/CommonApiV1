package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.CreditLimitDetail;

public interface CreditLimitDetailRepository extends JpaRepository<CreditLimitDetail,String > , JpaSpecificationExecutor<CreditLimitDetail> {

	List<com.maan.eway.bean.CreditLimitDetail> findByRequestreferenceno(String reqRefNo);

	List<CreditLimitDetail> findByCustomerCode(String customerId);

}
