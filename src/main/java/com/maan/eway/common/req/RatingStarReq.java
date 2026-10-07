package com.maan.eway.common.req;

import lombok.Data;

@Data
public class RatingStarReq {
    private String quoteNo;
    private Integer rating;
    private String  feedBack;
}
