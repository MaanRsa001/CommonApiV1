package com.maan.eway.jasper.risklist;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverDetailsDTO {
    private String key;  // cover name / cover id
    private List<KeyValueDTO> value;
}

