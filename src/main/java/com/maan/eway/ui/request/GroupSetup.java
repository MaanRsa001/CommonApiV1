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
public class GroupSetup {

	@JsonProperty("type")	
    @SerializedName("type")
    @Expose
	private String type;
	@JsonProperty("fieldGroup")	
    @SerializedName("fieldGroup")
    @Expose
	private List<LayoutSetup>  fieldGroup;
	
}
