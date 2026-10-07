package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubjectDetailsSaveReq {

	@JsonProperty("SubjectMatterReference")
    private String     subjectMatterReference    ;
	@JsonProperty("SubjectMatterDesc")
    private String subjectMatterDesc ;
	
}
