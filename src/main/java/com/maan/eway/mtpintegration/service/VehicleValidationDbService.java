package com.maan.eway.mtpintegration.service;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.maan.eway.mtpintegration.dto.ValidateVrnRequest;
import com.maan.eway.mtpintegration.dto.VehicleValidationResponse;
import com.maan.eway.mtpintegration.entity.MtpVehicleValidation;
import com.maan.eway.mtpintegration.repository.MtpVehicleValidationRepository;

@Service
//@RequiredArgsConstructor
public class VehicleValidationDbService {

	private final MtpVehicleValidationRepository repository;

	public VehicleValidationDbService(MtpVehicleValidationRepository repository) {
		this.repository = repository;
	}

	public void saveValidation(ValidateVrnRequest request, VehicleValidationResponse response) {

	    MtpVehicleValidation entity = repository
	            .findByNumberPlateAndAssessmentType(request.getNumberPlate(), request.getAssessmentType())
	            .orElse(new MtpVehicleValidation());

	    entity.setNumberPlate(request.getNumberPlate());
	    entity.setAssessmentType(request.getAssessmentType());

	    entity.setReturnCode(response.getReturnCode());
	    entity.setAmount(response.getAmount());
	    entity.setVehicleNo(response.getVehicleNo());
	    entity.setCustomerName(response.getName());
	    entity.setEngineSize(response.getEngineSize());
	    entity.setMakeName(response.getMake());
	    entity.setModelName(response.getModel());
	    entity.setServiceName(response.getService());
	    entity.setPolicyStartDate(parseDate(response.getPolicyStartDate()));
	    entity.setPolicyEndDate(parseDate(response.getPolicyEndDate()));
	    entity.setCreatedDate(LocalDateTime.now());

	    repository.save(entity);
	}

	private Date parseDate(String value) {
	    if (value == null || value.isBlank()) return null;
	    try {
	        return java.sql.Date.valueOf(java.time.LocalDate.parse(value));
	    } catch (Exception e) {
	        return null;
	    }
	}
}
