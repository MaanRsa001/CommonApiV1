package com.maan.eway.excelupload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
//package com.maan.eway.excelupload.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnDef {


@NotBlank(message = "HeaderName is mandatory")
 @JsonProperty("HeaderName")
 private String headerName;    

@NotBlank(message = "TargetField is mandatory")
@Pattern(
    regexp = "^(VALUE|PARAM[1-9]|PARAM10)$",
    message = "TargetField must be VALUE or PARAM1 to PARAM10"
)
 @JsonProperty("TargetField")
 private String targetField;   

@NotBlank(message = "DataType is mandatory")
@Pattern(
    regexp = "^(STRING|NUMBER|DATE)$",
    message = "DataType must be STRING, NUMBER, or DATE"
)
 @JsonProperty("DataType")
 private String dataType;      


 @JsonProperty("Required")
 private boolean required;     

 @JsonProperty("NumericOnly")
 private boolean numericOnly;  

 @JsonProperty("DateFormat")
 private String dateFormat;    
}
