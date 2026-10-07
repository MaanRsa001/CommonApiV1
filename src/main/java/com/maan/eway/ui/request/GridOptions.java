
package com.maan.eway.ui.request;

import java.io.Serializable;
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
public class GridOptions implements Serializable
{

	@JsonProperty("rowHeight")
    @SerializedName("rowHeight")
    @Expose
    public String rowHeight;
	@JsonProperty("columnDefs")
    @SerializedName("columnDefs")
    @Expose
    public List<ColumnDef> columnDefs;
    private final static long serialVersionUID = -8246801209270814500L;

}
