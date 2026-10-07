package com.maan.eway.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EserviceBuildingDetailsGetReq implements Serializable {

    private static final long serialVersionUID = 1L;
    
	@JsonProperty("CustomerRequestReferenceNo")
    private String     requestReferenceNo ;
    

}
