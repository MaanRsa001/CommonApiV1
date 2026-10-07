package com.maan.eway.mtpintegration.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
public class ErrorResponse {
    public ErrorResponse(String status, String message) {
		//super();
		this.status = status;
		this.message = message;
	}
	private String status;
    private String message;
}
