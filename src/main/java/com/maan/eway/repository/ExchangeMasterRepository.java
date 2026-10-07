package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ExchangeMaster;
import com.maan.eway.bean.ExchangeMasterId;

public interface ExchangeMasterRepository extends JpaRepository<ExchangeMaster, ExchangeMasterId>, JpaSpecificationExecutor<ExchangeMaster> {

	List<ExchangeMaster> findByCurrencyIdOrderByAmendIdDesc(String currency);

	List<ExchangeMaster> findByCurrencyIdAndCompanyIdOrderByAmendIdDesc(String currency, String companyId);

}
