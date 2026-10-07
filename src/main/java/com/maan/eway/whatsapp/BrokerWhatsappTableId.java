package com.maan.eway.whatsapp;

import java.io.Serializable;

import com.maan.eway.bean.BranchMasterId;

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
public class BrokerWhatsappTableId implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 //--- ENTITY KEY ATTRIBUTES 
    private String     loginId ;
    private Long    sno ;

}
