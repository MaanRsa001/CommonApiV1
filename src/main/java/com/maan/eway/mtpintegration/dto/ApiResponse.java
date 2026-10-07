package com.maan.eway.mtpintegration.dto;
public class ApiResponse<T> {

    private String status;
    private String message;
    private String nextAction;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String status, String message, String nextAction, T data) {
        this.status = status;
        this.message = message;
        this.nextAction = nextAction;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, String nextAction, T data) {
        return new ApiResponse<>("SUCCESS", message, nextAction, data);
    }

    public static <T> ApiResponse<T> pending(String message, T data) {
        return new ApiResponse<>("PENDING", message, "CHECK_PAYMENT_STATUS", data);
    }

    public static <T> ApiResponse<T> failed(String message, T data) {
        return new ApiResponse<>("FAILED", message, "STOP", data);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}

