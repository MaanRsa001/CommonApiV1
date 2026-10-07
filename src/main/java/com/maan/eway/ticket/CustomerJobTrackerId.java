package com.maan.eway.ticket;

import java.io.Serializable;

import com.maan.eway.bean.EserviceMotorDetailsId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CustomerJobTrackerId implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String     requestReferenceNo ;
	private String     companyId ;

}
