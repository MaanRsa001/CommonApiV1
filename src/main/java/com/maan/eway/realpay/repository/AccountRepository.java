package com.maan.eway.realpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.realpay.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
}
