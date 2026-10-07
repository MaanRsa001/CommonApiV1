package com.maan.eway.error;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ResponseStatus(HttpStatus.FORBIDDEN)
@ResponseStatus(HttpStatus.CREATED)
public class CommonValidationException extends Exception{
	
private static final long serialVersionUID = 1L;
	@JsonProperty("Errors")
	private List<Error> errors;
	@JsonProperty("Request")
	private Object request; 

	

	

}
