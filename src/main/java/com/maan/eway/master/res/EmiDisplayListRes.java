package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiDisplayListRes implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("EmiMonth")
	private String noOfInstallment;

	@JsonProperty("InstallmentAmount")
	private String installment;
	
	@JsonProperty("InstallmentDesc")
	private String     installmentDesc ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("DueDate")
	private Date dueDate;


	

	
}
