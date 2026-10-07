package com.maan.eway.bond.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class errors {
	@JsonProperty("Code")
    private String code;
	@JsonProperty("Field")
    private String field;
	@JsonProperty("Message")
    private String message;
	@JsonProperty("FieldLocal")
    private String fieldLocal;
	@JsonProperty("MessageLocal")
    private String messageLocal;
	   // ✅ Constructor with only code, field, message (used in validation handler)
    public errors(String code, String field, String message) {
        this.code = code;
        this.field = field;
        this.message = message;
	
	}
}

