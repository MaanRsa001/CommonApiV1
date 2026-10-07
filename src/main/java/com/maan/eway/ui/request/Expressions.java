
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
public class Expressions implements Serializable
{
	@JsonProperty("hide")
    @SerializedName("hide")
    @Expose
    public Object hide;
    private final static long serialVersionUID = -1026868157814717810L;

}
