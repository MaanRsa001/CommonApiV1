
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
public class Validation implements Serializable
{
	@JsonProperty("messages")
    @SerializedName("messages")
    @Expose
    public Messages messages;
	@JsonProperty("show")
    @SerializedName("show")
    @Expose
    public Object show;
    private final static long serialVersionUID = -1611239068065449225L;

}
