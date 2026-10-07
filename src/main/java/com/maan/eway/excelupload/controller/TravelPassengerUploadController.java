package com.maan.eway.excelupload.controller;


import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.excelupload.dto.TemplateDownloadRes;
import com.maan.eway.excelupload.service.TravelPassengerTemplateService;
import com.maan.eway.excelupload.service.TravelPassengerUploadService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class TravelPassengerUploadController {

    @Autowired
    private TravelPassengerTemplateService templateService;

    @Autowired
    private TravelPassengerUploadService uploadService;

    @ApiOperation(value = "Download travel passenger upload template")
    @GetMapping("/travel/template/download")
    public ResponseEntity<TemplateDownloadRes> downloadTemplate(
            @RequestParam("RequestReferenceNo") String requestReferenceNo) throws Exception {

        byte[] bytes = templateService.generateTemplate(requestReferenceNo);

        String base64 = Base64.getEncoder().encodeToString(bytes);

        TemplateDownloadRes response = new TemplateDownloadRes();
        response.setRequestReferenceNo(requestReferenceNo);
        response.setTemplate(
                "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64," + base64);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
    @ApiOperation(value = "Upload travel passengers via Excel")
    @PostMapping(value = "/travel/uploadPassengers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadPassengers(
            @RequestPart("file") MultipartFile file,
            @RequestParam("QuoteNo") String quoteNo,
            @RequestParam("RequestReferenceNo") String requestReferenceNo,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").split(",")[0];

        return uploadService.processUpload(file, quoteNo, requestReferenceNo, token);
    }
}
