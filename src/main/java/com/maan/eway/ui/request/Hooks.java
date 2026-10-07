package com.maan.eway.ui.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hooks implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3646345124767263774L;
	
	@JsonProperty("onInit")
    @SerializedName("onInit")
    @Expose
	private Object onInit;

}
