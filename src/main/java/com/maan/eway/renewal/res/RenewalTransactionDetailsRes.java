package com.maan.eway.renewal.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTransactionDetailsRes {
	
	@JsonProperty("TranId")
    private String tranId;
	
	@JsonProperty("NotificationId")
    private String notificationId;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonProperty("RequestTime")
    private Date requestTime;
	 
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonProperty("ResponseTime")
    private Date responseTime;
	
	@JsonProperty("TotalCount")
    private String totalCount;
	
	@JsonProperty("ConvertedCount")
    private String convertedCount;
	
	@JsonProperty("PendingCount")
    private String pendingCount;
}
