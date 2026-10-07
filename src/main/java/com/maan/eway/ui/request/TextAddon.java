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
public class TextAddon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3501484604828826825L;

	@JsonProperty("class")
    @SerializedName("class")
    @Expose
	private Object className;
	
	@JsonProperty("text")
    @SerializedName("text")
    @Expose
	private Object text;
}
