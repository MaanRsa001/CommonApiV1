package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.PaymentDeposit;

public interface PaymentDepositRepository extends JpaRepository<PaymentDeposit, Long>{

	List<PaymentDeposit> findByCbcNo(String cbcNo);

	List<PaymentDeposit> findByDepositNoIn(List<Long> depositNos);

	List<PaymentDeposit> findByDepositNoInOrderByEntryDateAsc(List<Long> depositNos);

}
