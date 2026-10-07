package com.maan.eway.res.calc;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UWReferrals implements Serializable {
	@JsonProperty("QuestionId")
	private Integer    uwQuestionId ;

    //--- ENTITY DATA FIELDS 
	@JsonProperty("QuestionDesc")
    private String     uwQuestionDesc ;

	@JsonProperty("QuestionType")
    private String     questionType ;

	@JsonProperty("Value")
    private String     value ;
}
