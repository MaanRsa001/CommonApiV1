
package com.maan.eway.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class Error {

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
	public Error(String code, String field, String message) {
		super();
		this.code = code;
		this.field = field;
		this.message = message;
	}
	
	
	
}
