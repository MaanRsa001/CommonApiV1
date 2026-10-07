package com.maan.eway.realpay.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstalmentBatchPutRequestDTO {

    @JsonProperty("InstalmentPutRequest")
    private List<InstalmentPutRequestItemDTO> instalmentPutRequest;
}
