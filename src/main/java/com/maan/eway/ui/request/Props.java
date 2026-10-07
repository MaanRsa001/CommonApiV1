
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
public class Props implements Serializable
{
	@JsonProperty("label")
    @SerializedName("label")
    @Expose
    public String label;
	@JsonProperty("placeholder")
    @SerializedName("placeholder")
    @Expose
    public String placeholder;
	@JsonProperty("required")
    @SerializedName("required")
    @Expose
    public Boolean required;
	
	@JsonProperty("min")
    @SerializedName("min")
    @Expose
    public Long min;
	@JsonProperty("max")
    @SerializedName("max")
    @Expose
    public Long max;
	@JsonProperty("type")
    @SerializedName("type")
    @Expose
    public String type;
	@JsonProperty("minLength")
    @SerializedName("minLength")
    @Expose
    public Long minLength;
	@JsonProperty("maxLength")
    @SerializedName("maxLength")
    @Expose
    public Long maxLength;
	@JsonProperty("rows")
    @SerializedName("rows")
    @Expose
    public Long rows;
	@JsonProperty("pattern")
    @SerializedName("pattern")
    @Expose
    public String pattern;
	@JsonProperty("readOnly")
    @SerializedName("readOnly")
    @Expose
    public Boolean readOnly;
	@JsonProperty("options")
    @SerializedName("options")
    @Expose
    public List<Object> options;
	@JsonProperty("groupProp")
    @SerializedName("groupProp")
    @Expose
    public String groupProp;
	@JsonProperty("valueProp")
    @SerializedName("valueProp")
    @Expose
    public String valueProp;
	@JsonProperty("labelProp")
    @SerializedName("labelProp")
    @Expose
    public String labelProp;
	@JsonProperty("addonLeft")
    @SerializedName("addonLeft")
    @Expose
    public TextAddon addonLeft;
	@JsonProperty("addonRight")
    @SerializedName("addonRight")
    @Expose
    public TextAddon addonRight;
	@JsonProperty("height")
    @SerializedName("height")
    @Expose
    public Long height;
	@JsonProperty("gridOptions")
    @SerializedName("gridOptions")
    @Expose
    public GridOptions gridOptions;
    private final static long serialVersionUID = -2151780511453228073L;

}
