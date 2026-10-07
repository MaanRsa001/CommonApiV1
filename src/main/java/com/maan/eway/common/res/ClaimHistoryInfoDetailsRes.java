package com.maan.eway.common.res;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Getter
@Setter
public class ClaimHistoryInfoDetailsRes {
	
	@JsonProperty("CLHSlNo")
	@JsonFormat(shape = Shape.STRING)
	private Integer clhSlNo;
	
	@JsonProperty("CLHDateOfLoss")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate clhDateOfLoss;
	
	@JsonProperty("CLHNatureOfLoss")
	private String clhNatureOfLoss;
	
	@JsonProperty("CLHClaimedAmount")
	@JsonFormat(shape = Shape.STRING)
	private Double clhClaimedAmount;
	
	@JsonProperty("CLHClaimYear")
	@JsonFormat(shape = Shape.STRING)
	private Integer clhClaimYear;
	
	@JsonProperty("CLHRemarks")
	private String clhRemarks;

}
