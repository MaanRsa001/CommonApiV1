package com.maan.eway.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.RSTAPushDetails;

public interface RSTAPushDetailsRepository extends JpaRepository<RSTAPushDetails, BigDecimal>{

}
