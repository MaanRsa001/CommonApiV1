package com.maan.eway.mtpintegration.dto;

import lombok.Data;

/*
import lombok.Data;

@Data
public class PaymentStatusResponse {
    private String paymentStatus;
    private Integer returnCode;
    private String returnMessage;
    private String downloadLink;
    private String stickerReference;
    private String transactionId;
}
*/

@Data
public class PaymentStatusResponse {
    private String paymentStatus;
    private Integer returnCode;
    private String returnMessage;
    private String downloadLink;
    private String stickerReference;
    private String transactionId;
    private String nextAction;
    private String userMessage;

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Integer getReturnCode() { return returnCode; }
    public void setReturnCode(Integer returnCode) { this.returnCode = returnCode; }

    public String getReturnMessage() { return returnMessage; }
    public void setReturnMessage(String returnMessage) { this.returnMessage = returnMessage; }

    public String getDownloadLink() { return downloadLink; }
    public void setDownloadLink(String downloadLink) { this.downloadLink = downloadLink; }

    public String getStickerReference() { return stickerReference; }
    public void setStickerReference(String stickerReference) { this.stickerReference = stickerReference; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
}