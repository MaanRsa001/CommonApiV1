package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.RenewalTransactionDetails;
/**
 * <h2>BankMasterRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:26
 * <p>
 * Description: "RenewalTransactionDetailsRepository" Repository
 */

@Repository
public interface RenewalTransactionDetailsRepository extends JpaRepository<RenewalTransactionDetails, String> {
    List<RenewalTransactionDetails> findByTranId(String tranId);

}
