package com.maan.eway.common.controller;


import com.maan.eway.common.req.RatingStarReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.RatingStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/rating")
public class RatingStarController {

    @Autowired
    private RatingStarService service;

    @PostMapping("/stars")
    public ResponseEntity<CommonRes> saveRating(@RequestBody RatingStarReq request) {

        CommonRes res = new CommonRes();

        String isSaved = service.saveRating(request);
        res.setCommonResponse(isSaved);
        res.setErrorMessage(Collections.emptyList());
        res.setIsError(false);
        res.setMessage("Success");
        if (res != null) {
            return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
