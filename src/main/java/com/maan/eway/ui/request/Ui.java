package com.maan.eway.ui.request;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Builder
@ToString
public class Ui {
	@JsonProperty("Companyname")
	private String     companyName  ;
	@JsonProperty("Productname")
	private String     productName  ;
}
