package com.maan.eway.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class BasicValidationService {

	// Date null check
	public boolean dateNullValidation (Date date  ) {
		if(date == null  ) {
			return true ;
		} else {
			return false ;
		}
	}
	
	//Effective Date
	public boolean effectiveDateValidation (Date date  ) {
		Date today = new Date();
		if (date.compareTo(today) > 0) {
			return true;
		} else {
			return false;
		}
	}

	
}
