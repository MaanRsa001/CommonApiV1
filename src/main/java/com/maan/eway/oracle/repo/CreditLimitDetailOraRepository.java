package com.maan.eway.oracle.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.oracle.CreditLimitDetailOra;

public interface CreditLimitDetailOraRepository extends JpaRepository<CreditLimitDetailOra,String > , JpaSpecificationExecutor<CreditLimitDetailOra> {

	List<CreditLimitDetailOra> findByRequestreferenceno(String requestreferenceno);
	
	List<CreditLimitDetailOra> findByCustomerCode(String customerCode);

}
