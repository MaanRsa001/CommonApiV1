package com.maan.eway.excelupload.controller;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.excelupload.bean.TemplateEntity;
import com.maan.eway.excelupload.dto.AdditionalInfoRequest;
import com.maan.eway.excelupload.dto.AdditionalInformationFlatRequest;
import com.maan.eway.excelupload.dto.AdditionalInformationResponse;
import com.maan.eway.excelupload.dto.DownloadResponse;
import com.maan.eway.excelupload.dto.EndtDeleteReq;
import com.maan.eway.excelupload.dto.TemplateCreateRequest;
import com.maan.eway.excelupload.dto.TemplateGetReq;
import com.maan.eway.excelupload.dto.TemplateGetResponse;
import com.maan.eway.excelupload.dto.TemplateSearchRequest;
import com.maan.eway.excelupload.dto.additionalInfoGetResponse;
import com.maan.eway.excelupload.service.ExcelTemplateGenerator;
import com.maan.eway.excelupload.service.TemplateService;
import com.maan.eway.repository.HomePositionMasterRepository;

import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
public class AdminTemplateController {
    private final TemplateService templateService;
    private final ExcelTemplateGenerator generator;
    
    @Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	@PostMapping("/create")
    public ResponseEntity<CommonRes> createTemplate(
            @Valid @RequestBody TemplateCreateRequest req) {
        CommonRes res = new CommonRes();
        try {
            TemplateGetResponse response = templateService.createTemplate(req);
            res.setCommonResponse(response);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Success");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsError(true);
            res.setMessage(e.getMessage());
            res.setErrorMessage(Collections.emptyList());
            return ResponseEntity.ok(res);
        }
    }
	
	@PostMapping("/gettemplatedetails")
	public ResponseEntity<CommonRes> getTemplateByKeys( @RequestBody TemplateGetReq req ) {

	    CommonRes res = new CommonRes();
	    try {
	        TemplateGetResponse response = templateService.getTemplateResponseByKeys(req.getCompanyId(), req.getProductId(), req.getSectionId(), req.getCoverId());
	        res.setCommonResponse(response);
	        res.setIsError(false);
	        res.setErrorMessage(Collections.emptyList());
	        res.setMessage("Success");
	        return ResponseEntity.ok(res);
	    } catch (Exception e) {
	        res.setIsError(true);
	        res.setMessage(e.getMessage());
	        res.setErrorMessage(Collections.emptyList());
	        return ResponseEntity.ok(res);
	    }
	}

	@GetMapping("/by-product")
	public ResponseEntity<CommonRes> getTemplatesByProduct(
	        @RequestParam String companyId,
	        @RequestParam String productId) {

	    CommonRes res = new CommonRes();
	    try {
	        List<TemplateGetResponse> responses = templateService
	                .getTemplatesByProduct(companyId, productId);
	        res.setCommonResponse(responses);
	        res.setIsError(false);
	        res.setErrorMessage(Collections.emptyList());
	        res.setMessage("Success");
	        return ResponseEntity.ok(res);
	    } catch (Exception e) {
	        res.setIsError(true);
	        res.setMessage(e.getMessage());
	        res.setErrorMessage(Collections.emptyList());
	        return ResponseEntity.ok(res);
	    }
	}
    
    @PostMapping("/quote")
    public ResponseEntity<additionalInfoGetResponse> getAdditionalInfo(
            @RequestBody AdditionalInfoRequest request) throws Exception {

//        if (request.getQuoteNo() == null || request.getQuoteNo().isBlank()) {
//            throw new RuntimeException("QuoteNo is mandatory");
//        }

        additionalInfoGetResponse response =
                templateService.getTemplateByQuoteNo(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateEntity> getTemplate(@PathVariable Long id) {
        return templateService.getTemplate(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable Long id) throws Exception {
        TemplateEntity t = templateService.getTemplate(id).orElseThrow(() -> new RuntimeException("Template not found"));
        byte[] bytes = generator.generateXlsx(t);
        String fileName = URLEncoder.encode(t.getName() + "-template.xlsx", "UTF-8");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
    
    @PostMapping("/download")
    public ResponseEntity<DownloadResponse> downloadTemplate(
            @RequestBody TemplateSearchRequest req) throws Exception {

        TemplateEntity t = templateService
                .getTemplateByKeys(req.getCompanyId(), req.getProductId(), req.getSectionId(), req.getCoverId())
                .orElseThrow(() -> new RuntimeException("Template not found"));

        byte[] bytes = generator.generateXlsx(t);

        String base64 = Base64.getEncoder().encodeToString(bytes);
        
        base64="data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;"+base64;

        DownloadResponse response = new DownloadResponse(base64);

        return ResponseEntity.ok(response);
    }
    
    
//    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
//    @PostMapping(value = "/saveAdditionalInfo")
//    @ApiOperation(value = "This method is to Save Additional Info")
//    public ResponseEntity<CommonRes> saveAdditionalInfo(
//            @RequestBody AdditionalInformationFlatRequest req) {
//
//        return processAdditionalInformation(req);
//    }
    
    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
    @PostMapping(value = "/saveAdditionalInfo")
    public ResponseEntity<CommonRes> saveAdditionalInfo(@RequestBody AdditionalInformationFlatRequest req ,@RequestHeader("Authorization") String token) {
    	 HomePositionMaster home = null;
        CommonRes data = new CommonRes();
        if(StringUtils.isNotBlank(req.getEndtTypeId()))
        {
        	home = homeRepo.findByPolicyNo(req.getOriginalPolicyNo());
        	req.setQuoteNo(home.getQuoteNo());
        	
        }
        List<String> validationCodes =
                templateService.validateAdditionalInformation(req);
        if(home==null)
        {
        home = homeRepo.findByQuoteNo(req.getQuoteNo());
        }
        List<Error> validation = null;

        if (validationCodes != null && !validationCodes.isEmpty()) {

            CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
            comErrDescReq.setBranchCode(home.getBranchCode());
            comErrDescReq.setInsuranceId(home.getCompanyId());
            comErrDescReq.setProductId(String.valueOf(home.getProductId()));
            comErrDescReq.setModuleId("4");
            comErrDescReq.setModuleName("ADDITIONAL INFO");

            validation = errorDescService.getErrorDesc(validationCodes, comErrDescReq);

            data.setCommonResponse(null);
            data.setIsError(true);
            data.setErrorMessage(validation);
            data.setMessage("Failed");

            return new ResponseEntity<>(data, HttpStatus.OK);
        }

        AdditionalInformationResponse res =
                templateService.saveOrUpdate(req,token);

        data.setCommonResponse(res);
        data.setIsError(false);
        data.setErrorMessage(Collections.emptyList());
        data.setMessage("Success");

        return new ResponseEntity<>(data, HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
    @PostMapping(
            value = "/uploadAdditionalInfoExcel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Upload Additional Info via Excel")
    public ResponseEntity<CommonRes> uploadAdditionalInfoExcel(

            @RequestPart("file") MultipartFile file,
            @RequestParam("QuoteNo") String quoteNo,
            @RequestParam("LocationId") String locationId,
            @RequestParam("SectionId") String sectionId,
            @RequestParam("CoverId") String coverId,
            @RequestParam(value = "RiskId", required = false) String riskId) {

        CommonRes data = new CommonRes();

        try {

            HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
            if (home == null) {
                data.setIsError(true);
                data.setMessage("Invalid Quote No");
                return ResponseEntity.ok(data);
            }

            String companyId = home.getCompanyId();
            String productId = String.valueOf(home.getProductId());

            TemplateEntity template = templateService
                    .getTemplateByKeys(companyId, productId, sectionId, coverId)
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            AdditionalInformationFlatRequest req =
                    templateService.buildFlatRequestFromExcel(
                            file, template, quoteNo, locationId,
                            companyId, productId, sectionId, coverId,
                            riskId,home);

            List<String> validationCodes =
                    templateService.validateAdditionalInformation(req);

            if (validationCodes != null && !validationCodes.isEmpty()) {

                CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
                comErrDescReq.setBranchCode(home.getBranchCode());
                comErrDescReq.setInsuranceId(home.getCompanyId());
                comErrDescReq.setProductId(productId);
                comErrDescReq.setModuleId("4");
                comErrDescReq.setModuleName("ADDITIONAL INFO");

                List<Error> validation =
                        errorDescService.getErrorDesc(validationCodes, comErrDescReq);

                data.setCommonResponse(null);
                data.setIsError(true);
                data.setErrorMessage(validation);
                data.setMessage("Failed");

                return ResponseEntity.ok(data);
            }

            AdditionalInformationResponse res =
            		 templateService.saveOrUpdate(req,"");

            data.setCommonResponse(res);
            data.setIsError(false);
            data.setErrorMessage(Collections.emptyList());
            data.setMessage("Success");

            return ResponseEntity.status(HttpStatus.CREATED).body(data);

        } catch (Exception e) {

            data.setCommonResponse(null);
            data.setIsError(true);
            data.setErrorMessage(Collections.emptyList());
            data.setMessage("Exception: " + e.getMessage());

            return ResponseEntity.ok(data);
        }
    }


    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
    @PostMapping(value = "/EndtDeleteAdd")
    public ResponseEntity<CommonRes> saveEndtDeleteAdd(@RequestBody EndtDeleteReq req ,@RequestHeader("Authorization") String token) {
    	CommonRes data =templateService.saveEndtDeleteAdd(req,token);
    	return new ResponseEntity<>(data, HttpStatus.CREATED);
    }

}

