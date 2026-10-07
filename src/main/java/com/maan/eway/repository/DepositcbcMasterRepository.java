package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.DepositcbcMaster;

public interface DepositcbcMasterRepository extends JpaRepository<DepositcbcMaster, String>{

	List<DepositcbcMaster> findByCbcNo(String cbcNo);
	
	int countByBrokerId(String brokerId);

	List<DepositcbcMaster> findByCbcNoAndBrokerId(String cbcNo, String brokerId);

	List<DepositcbcMaster> findByBrokerIdAndProductIdLike(String brokerId,
			String productId);

	List<DepositcbcMaster> findByBrokerId(String brokerId);

	List<DepositcbcMaster> findByBrokerIdAndProductIdLikeAndStatus(String brokerId, String productId, String string2);

	List<DepositcbcMaster> findByCbcNoAndBrokerIdAndProductIdAndStatus(String cbcNo, String brokerId, String productId,
			String string);

	List<DepositcbcMaster> findByStatusAndBrokerId(String string, String brokerId);

	Long countByBrokerIdAndStatus(String string, String string2);

	List<DepositcbcMaster> findByCbcNoAndBrokerIdAndStatus(String cbcNo, String brokerId, String string);



}
