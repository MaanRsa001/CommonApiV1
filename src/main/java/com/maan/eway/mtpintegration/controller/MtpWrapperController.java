package com.maan.eway.mtpintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.mtpintegration.dto.MtpWrapperReq;
import com.maan.eway.mtpintegration.dto.MtpWrapperRes;
import com.maan.eway.mtpintegration.service.MtpWrapperService;

@RestController
@RequestMapping("/api/mtp")
public class MtpWrapperController {

    @Autowired
    private MtpWrapperService mtpWrapperService;

    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/process")
    public ResponseEntity<MtpWrapperRes> process(
            @RequestBody MtpWrapperReq req,
            @RequestHeader("Authorization") String authHeader) {

    	String token = authHeader.replaceAll("Bearer ", "").split(",")[0];

        String rawToken = authHeader.replaceAll("Bearer ", "");
        MtpWrapperRes res = mtpWrapperService.process(req, token, rawToken);
        return new ResponseEntity<>(res, res.isError() ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }
}
