package com.maan.eway.mtpintegration.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.mtpintegration.entity.MtpOauthToken;

@Repository
public interface MtpOauthTokenRepository extends JpaRepository<MtpOauthToken, Long> {
	Optional<MtpOauthToken> findTopByOrderByIdDesc();
}
