package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.DepositDetail;

public interface DepositDetailRepository extends JpaRepository<DepositDetail, Long>{

	int countByProductIdAndQuoteNoAndPremiumAmountAndStatus(String productId, String quoteNo,
			Double valueOf, String string);
	
	List<DepositDetail> findByQuoteNo(String quoteNo);

	List<DepositDetail> findByStatus(String string);

	List<DepositDetail> findByCbcNo(String cbcNo);

	int countByQuoteNoAndPremiumAmountAndStatus(String quoteNo, Double valueOf, String string);

	List<DepositDetail> findByCbcNoAndStatus(String cbcNo, String status);

	List<DepositDetail> findByCbcNoOrderByEntryDateAsc(String cbcNo);

	List<DepositDetail> findByCbcNoAndStatusOrderByEntryDateAsc(String cbcNo, String string);

	DepositDetail findByDepositNo(Long depositNo);
	
	

}
