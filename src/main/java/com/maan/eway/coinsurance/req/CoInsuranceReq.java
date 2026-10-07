package com.maan.eway.coinsurance.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CoInsuranceReq {

    @JsonProperty("QuoteNo")
    @NotBlank(message = "QuoteNo is Empty")
    private String quoteNo;

   @JsonProperty("Details")
   @Valid
   List<CoinsuranceDetailsReq> details;
   
   @JsonProperty("EndorsementsDetails")
   List<EndorsementsCoInsuranceReq> endorsementsdetails;

}
