package com.maan.eway.repository;

import com.maan.eway.bean.ClaimCustomerDetails;
import com.maan.eway.bean.ClaimCustomerDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimCustomerDetailsRepository extends JpaRepository<ClaimCustomerDetails, ClaimCustomerDetailsId> {


    ClaimCustomerDetails findByPolCustCode(String polCustCode);

    ClaimCustomerDetails findByCustomerReferenceNo(String customerReferenceNo);
}

