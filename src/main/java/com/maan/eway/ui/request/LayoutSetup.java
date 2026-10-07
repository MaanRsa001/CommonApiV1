package com.maan.eway.ui.request;

import java.util.List;

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
public class LayoutSetup {

	@JsonProperty("fieldGroupClassName")	
    @SerializedName("fieldGroupClassName")
    @Expose
	private String fieldGroupClassName;
	
	@JsonProperty("props")	
    @SerializedName("props")
    @Expose
	private Props props;
	
	@JsonProperty("fieldGroup")	
    @SerializedName("fieldGroup")
    @Expose
	private List<Field> fieldGroup;
	
	
	
}
