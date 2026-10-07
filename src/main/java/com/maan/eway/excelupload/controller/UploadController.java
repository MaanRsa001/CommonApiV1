package com.maan.eway.excelupload.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.maan.eway.excelupload.dto.AdditionalInformationRequest;
import com.maan.eway.excelupload.dto.AdditionalInformationResponse;
import com.maan.eway.excelupload.repository.EServiceBuildingDetailsRepository;
import com.maan.eway.excelupload.repository.TemplateRepository;
import com.maan.eway.excelupload.service.ExcelAdditionalInfoService;
import com.maan.eway.excelupload.service.FastexcelParser;
import com.maan.eway.excelupload.service.ImportService;
import com.maan.eway.excelupload.service.TemplateService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class UploadController {
    private final TemplateRepository templateRepository;
    private final FastexcelParser parser;
    private final ImportService importService;
    private final FastexcelParser fastexcelParser;
    
	@Autowired
	private EServiceBuildingDetailsRepository eserBuildingRepo ;

	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private ExcelAdditionalInfoService excelAdditionalInfoService;
	
    @PostMapping("/{templateId}/upload/preview")
    public ResponseEntity<?> preview(@PathVariable Long templateId,
                                     @RequestPart("file") MultipartFile file) throws Exception {
        TemplateEntity t = templateRepository.findById(templateId).orElseThrow(() -> new RuntimeException("Template not found"));
        FastexcelParser.PreviewResponseWrapper resp = parser.preview(file, t, 50);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{templateId}/upload/commit")
    public ResponseEntity<?> commit(@PathVariable Long templateId,
                                    @RequestPart("file") MultipartFile file,
                                    @RequestParam String requestRefNo,
                                    @RequestParam String companyId,
                                    @RequestParam String productId,
                                    @RequestParam String sectionId,
                                    @RequestParam String coverId) throws Exception {
        TemplateEntity t = templateRepository.findById(templateId).orElseThrow(() -> new RuntimeException("Template not found"));
        //var result = importService.commitUpload(file, t, requestRefNo, companyId, productId, sectionId, coverId);
        var result = 0;

        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/records/{requestRefNo}")
    public ResponseEntity<?> getRecords(@PathVariable String requestRefNo) {
        var recs = importService.getUploadedRecordsByRequestRef(requestRefNo);
        return ResponseEntity.ok(recs);
    }
    
    
    
    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
    @PostMapping(value="/savecontentitems")
    @ApiOperation(value="This method is to Save Content Risk")
    public ResponseEntity<CommonRes> savecontentitems(@RequestBody AdditionalInformationRequest req){
        return processAdditionalInformation(req);
    }

    private ResponseEntity<CommonRes> processAdditionalInformation(AdditionalInformationRequest req) {
        CommonRes data = new CommonRes();

        List<String> validationCodes = importService.validateAdditionalInformation(req);
        List<Error> validation = null;

        HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
        if (validationCodes != null && !validationCodes.isEmpty()) {

            CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
            comErrDescReq.setBranchCode(home.getBranchCode());
            comErrDescReq.setInsuranceId(home.getCompanyId());
            comErrDescReq.setProductId(String.valueOf(home.getProductId()));
            comErrDescReq.setModuleId("4");
            comErrDescReq.setModuleName("ADDITIONAL INFO");

            validation = errorDescService.getErrorDesc(validationCodes, comErrDescReq);
        }

        if (validation != null && !validation.isEmpty()) {
            data.setCommonResponse(null);
            data.setIsError(true);
            data.setErrorMessage(validation);
            data.setMessage("Failed");
            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {
            AdditionalInformationResponse res = importService.saveOrUpdate(req);
            data.setCommonResponse(res);
            data.setIsError(false);
            data.setErrorMessage(Collections.emptyList());
            data.setMessage("Success");
            if (res != null) {
                return new ResponseEntity<>(data, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
    @PostMapping(value="/uploadAdditionalInfoExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value="This method is to Upload Additional Info via Excel")
    public ResponseEntity<CommonRes> uploadAdditionalInfoExcel(
            @RequestPart("file") MultipartFile file,
            @RequestParam("QuoteNo") String quoteNo,
            @RequestParam("SectionId") String sectionId,
            @RequestParam("CoverId") String coverId) {

        CommonRes data = new CommonRes();

        try {
            // Get quote details (company/product)
            HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
            String companyId = home.getCompanyId();
            String productId = String.valueOf(home.getProductId().intValue());


            // Find template for this combination
            TemplateEntity template = templateService
                    .getTemplateByKeys(companyId, productId, sectionId, coverId)
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            // Build request from Excel using dynamic ColumnDef mapping
            AdditionalInformationRequest req = excelAdditionalInfoService
                    .buildRequestFromExcel(file, template, quoteNo, sectionId, coverId);

            // Reuse same validation + save logic
            return processAdditionalInformation(req);

        } catch (Exception e) {
            data.setCommonResponse(null);
            data.setIsError(true);
            data.setErrorMessage(Collections.emptyList());
            data.setMessage("Exception: " + e.getMessage());
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
    @PostMapping(value="/uploadAdditionalInfoExcel/preview",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> previewAdditionalInfoExcel(
            @RequestPart("file") MultipartFile file,
            @RequestParam("quoteNo") String quoteNo,
            @RequestParam("sectionId") String sectionId,
            @RequestParam("coverId") String coverId) throws Exception {

        HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
        String companyId = home.getCompanyId();
        String productId = String.valueOf(home.getProductId());

        TemplateEntity template = templateService
                .getTemplateByKeys(companyId, productId, sectionId, coverId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        FastexcelParser.PreviewResponseWrapper resp =
                fastexcelParser.preview(file, template, 50); // first 50 rows preview

        return ResponseEntity.ok(resp);
    }
    
   
}

