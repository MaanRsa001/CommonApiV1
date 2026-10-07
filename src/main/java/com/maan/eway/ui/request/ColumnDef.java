
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
public class ColumnDef implements Serializable
{
	@JsonProperty("headerName")
    @SerializedName("headerName")
    @Expose
    public String headerName;
	@JsonProperty("field")
    @SerializedName("field")
    @Expose
    public String field;
	@JsonProperty("sortable")
    @SerializedName("sortable")
    @Expose
    public String sortable;
	@JsonProperty("width")
    @SerializedName("width")
    @Expose
    public String width;
    private final static long serialVersionUID = -2525274594118077974L;

}
