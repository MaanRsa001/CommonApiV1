package com.maan.eway.SectionCoverMaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatingFieldDto {

    @JsonProperty("RatingFieldName")
    private String ratingFieldName;

    @JsonProperty("InputTableName")
    private String inputTableName;

    @JsonProperty("InputColumnName")
    private String inputColumnName;

    @JsonProperty("IsDropdown")
    private String isDropdown;

    @JsonProperty("ApiURL")
    private String apiUrl;

    @JsonProperty("LabelName")
    private String labelName;
    
    @JsonProperty("JsonKey")
    private String jsonKey;
    
    @JsonProperty("JsonKeyDesc")
    private String jsonKeyDesc;
}