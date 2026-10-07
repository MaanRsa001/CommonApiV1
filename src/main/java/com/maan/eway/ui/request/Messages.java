
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
public class Messages implements Serializable
{
	@JsonProperty("pattern")
    @SerializedName("pattern")
    @Expose
    public Object pattern;
    private final static long serialVersionUID = -4822645182667445657L;

}
