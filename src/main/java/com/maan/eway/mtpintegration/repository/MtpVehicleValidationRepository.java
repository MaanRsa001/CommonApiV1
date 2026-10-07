package com.maan.eway.mtpintegration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.mtpintegration.entity.MtpVehicleValidation;

@Repository
public interface MtpVehicleValidationRepository
        extends JpaRepository<MtpVehicleValidation, Long> {
	
	Optional<MtpVehicleValidation> findByNumberPlateAndAssessmentType(
	        String numberPlate,
	        String assessmentType);
}
