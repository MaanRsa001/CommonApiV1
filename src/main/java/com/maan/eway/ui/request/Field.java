
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
public class Field implements Serializable
{

	@JsonProperty("className")	
    @SerializedName("className")
    @Expose
    public String className;
	
	@JsonProperty("type")	
	@SerializedName("type")
    @Expose
    public String type;
	
	@JsonProperty("key")
    @SerializedName("key")
    @Expose
    public String key;
	@JsonProperty("defaultValue")
    @SerializedName("defaultValue")
    @Expose
    public String defaultValue;
	@JsonProperty("props")
    @SerializedName("props")
    @Expose
    public Props props;
	@JsonProperty("expressions")
    @SerializedName("expressions")
    @Expose
    public Expressions expressions;
	@JsonProperty("validation")
    @SerializedName("validation")
    @Expose
    public Validation validation;
	@JsonProperty("validators")
    @SerializedName("validators")
    @Expose
    public Validators validators;
	@JsonProperty("asyncValidators")    
    @SerializedName("asyncValidators")
    @Expose
    public AsyncValidators asyncValidators;
	@JsonProperty("wrappers")
    @SerializedName("wrappers")
    @Expose
    public Wrappers wrappers;
    private final static long serialVersionUID = -5899599267530731292L;

}
