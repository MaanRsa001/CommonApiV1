package com.maan.eway.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoInsuranceInfoId implements Serializable{
	 private static final long serialVersionUID = 1L;
	 
	private int sno ;
    private int amendid ;
 	private int productid ;
 	private String requestreferenceno ;
}
