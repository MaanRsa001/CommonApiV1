package com.maan.eway.excelupload.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.excelupload.bean.AdditionalInformation;
import com.maan.eway.excelupload.bean.UploadedRecord;
import com.maan.eway.excelupload.dto.AdditionalInfoGetReq;
import com.maan.eway.excelupload.dto.AdditionalInformationRequest;
import com.maan.eway.excelupload.dto.AdditionalInformationResponse;
import com.maan.eway.excelupload.dto.ContentItem;
import com.maan.eway.excelupload.dto.ContentItemResponse;
import com.maan.eway.excelupload.repository.AdditionalInformationRepository;
import com.maan.eway.excelupload.repository.UploadedRecordRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportService {
	
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	
	@Autowired
    private AdditionalInformationRepository additionalInformationrepo;
	
	
	@Autowired
	private BuildingRiskDetailsRepository buildingRiskRepo;
	
	@Autowired
	private CompanyProductMasterRepository companyrepo;

	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	
	
    private final ObjectMapper objectMapper;
    private final UploadedRecordRepository uploadedRecordRepository;

    private static final int BATCH_SIZE = 500;

  

    public List<UploadedRecord> getUploadedRecordsByRequestRef(String requestRefNo) {
        return uploadedRecordRepository.findByRequestRefNo(requestRefNo);
    }
    
    @Transactional
	public AdditionalInformationResponse saveOrUpdate(AdditionalInformationRequest req) {

	    HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
	    if (home == null) {
	        throw new RuntimeException("No Home record found for QuoteNo: " + req.getQuoteNo());
	    }

	    List<AdditionalInformation> allEntities = new ArrayList<>();

	    for (ContentItem item : req.getContentItems()) {

	        String sectionId = item.getSectionId(); 
	        String coverId = item.getCoverId();     

	        additionalInformationrepo.deleteByQuoteNoAndRequestReferenceNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
	                req.getQuoteNo(), home.getRequestReferenceNo(), home.getCompanyId(),
	                String.valueOf(home.getProductId()), sectionId, coverId);

	        AdditionalInformation entity = AdditionalInformation.builder()
	                .quoteNo(req.getQuoteNo())
	                .requestReferenceNo(home.getRequestReferenceNo())
	                .companyId(home.getCompanyId())
	                .productId(String.valueOf(home.getProductId()))
	                .sectionId(sectionId)
	                .coverId(coverId)
	                .param1(item.getParam1())
	                .value(item.getValue())
	                .param3(item.getParam3())
	                .param4(item.getParam4())
	                .param5(item.getParam5())
	                .param6(item.getParam6())
	                .param7(item.getParam7())
	                .param8(item.getParam8())
	                .param9(item.getParam9())
	                .param10(item.getParam10())
	                .build();

	        allEntities.add(entity);
	    }

	    // 6️⃣ Save All
	    additionalInformationrepo.saveAll(allEntities);

	    // 7️⃣ Prepare Response
	    List<ContentItemResponse> contentItems = allEntities.stream()
	            .map(e -> ContentItemResponse.builder()
	                    .contentId(e.getContentId())
	                    .sectionId(e.getSectionId())
	                    .coverId(e.getCoverId())
	                    .param1(e.getParam1())
	                    .value(e.getValue())
	                    .param3(e.getParam3())
	                    .param4(e.getParam4())
	                    .param5(e.getParam5())
	                    .param6(e.getParam6())
	                    .param7(e.getParam7())
	                    .param8(e.getParam8())
	                    .param9(e.getParam9())
	                    .param10(e.getParam10())
	                    .build())
	            .collect(Collectors.toList());

	    return AdditionalInformationResponse.builder()
	            .response("Success")
	            .quoteNo(req.getQuoteNo())
	            .requestReferenceNo(home.getRequestReferenceNo())
	            .companyId(home.getCompanyId())
	            .productId(String.valueOf(home.getProductId()))
	            .contentItems(contentItems)
	            .build();
	}

	
	public AdditionalInformationResponse getByQuoteAndRef(AdditionalInfoGetReq req) {

	    List<AdditionalInformation> list =
	            additionalInformationrepo.findByQuoteNoAndRequestReferenceNo(
	                    req.getQuoteNo(), req.getRequestReferenceNo());

	    if (list == null || list.isEmpty()) {
	        return AdditionalInformationResponse.builder()
	                .response("No Data")
	                .quoteNo(req.getQuoteNo())
	                .requestReferenceNo(req.getRequestReferenceNo())
	                .companyId(null)
	                .productId(null)
	                .contentItems(Collections.emptyList())
	                .build();
	    }

	    AdditionalInformation first = list.get(0);

	    List<ContentItemResponse> contentItems = list.stream()
	            .map(e -> ContentItemResponse.builder()
	                    .contentId(e.getContentId())
	                    .sectionId(e.getSectionId())
	                    .coverId(e.getCoverId())
	                    .param1(e.getParam1())
	                    .value(e.getValue())
	                    .param3(e.getParam3())
	                    .param4(e.getParam4())
	                    .param5(e.getParam5())
	                    .param6(e.getParam6())
	                    .param7(e.getParam7())
	                    .param8(e.getParam8())
	                    .param9(e.getParam9())
	                    .param10(e.getParam10())
	                    .build())
	            .collect(Collectors.toList());

	    return AdditionalInformationResponse.builder()
	            .response("Success")
	            .quoteNo(req.getQuoteNo())
	            .requestReferenceNo(req.getRequestReferenceNo())
	            .companyId(first.getCompanyId())
	            .productId(first.getProductId())
	            .contentItems(contentItems)
	            .build();
	}

	
	public List<String> validateAdditionalInformation(AdditionalInformationRequest req) {
	    List<String> error = new ArrayList<>();

	    // 1️⃣ Basic validation
	    if (req.getQuoteNo() == null || req.getQuoteNo().isBlank()) {
	        error.add("1755"); // Missing QuoteNo
	        return error;
	    }

	    HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
	    if (home == null) {
	        error.add("1756"); // Home record not found
	        return error;
	    }

	    // 2️⃣ Get product type
	    List<CompanyProductMaster> companyProductList =
	            companyrepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(
	                    home.getCompanyId(), home.getProductId());

	    if (companyProductList.isEmpty()) {
	        error.add("1759"); // Company product not configured
	        return error;
	    }

	    CompanyProductMaster companyProduct = companyProductList.get(0);
	    String motorYN = companyProduct.getMotorYn(); // A / H

	    // 3️⃣ Group ContentItems by (sectionId, coverId)
	    Map<String, List<ContentItem>> groupedBySecCover = req.getContentItems() == null
	            ? Collections.emptyMap()
	            : req.getContentItems().stream()
	                .filter(Objects::nonNull)
	                .collect(Collectors.groupingBy(ci -> (ci.getSectionId() + "|" + ci.getCoverId())));

	    // Flag to ensure we add "1757" only once overall
	    boolean sumMismatchAdded = false;

	    for (Map.Entry<String, List<ContentItem>> entry : groupedBySecCover.entrySet()) {

	        List<ContentItem> groupItems = entry.getValue();
	        if (groupItems == null || groupItems.isEmpty()) {
	            continue;
	        }

	        String sectionId = groupItems.get(0).getSectionId();
	        String coverId   = groupItems.get(0).getCoverId();

	        if (sectionId == null || coverId == null) {
	            error.add("1760");
	            continue;
	        }

	        BigDecimal declaredSumInsured = BigDecimal.ZERO;

	        // 4️⃣ Fetch declaredSumInsured ONCE per sectionId/coverId based on product type
	        if ("A".equalsIgnoreCase(motorYN)) {
	            // 🔹 BuildingRiskDetails
	            List<BuildingRiskDetails> buildingList = buildingRiskRepo
	                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
	                            home.getQuoteNo(),
	                            home.getCompanyId(),
	                            String.valueOf(home.getProductId()),
	                            sectionId,
	                            Integer.valueOf(coverId)
	                    );

	            if (buildingList == null || buildingList.isEmpty()) {
	                error.add("1758"); // No building data
	                continue;
	            }

	            declaredSumInsured = buildingList.get(0).getSumInsured() == null
	                    ? BigDecimal.ZERO
	                    : buildingList.get(0).getSumInsured();

	        } else if ("H".equalsIgnoreCase(motorYN)) {
	            // 🔹 CommonDataDetails
	            List<CommonDataDetails> commonList = commonDataRepo
	                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
	                            home.getQuoteNo(),
	                            home.getCompanyId(),
	                            String.valueOf(home.getProductId()),
	                            sectionId,
	                            Integer.valueOf(coverId)
	                    );

	            if (commonList == null || commonList.isEmpty()) {
	                error.add("1758"); // No common data
	                continue;
	            }

	            declaredSumInsured = commonList.get(0).getSumInsured() == null
	                    ? BigDecimal.ZERO
	                    : commonList.get(0).getSumInsured();
	        } else {
	            error.add("1761"); // Product type not supported
	            continue;
	        }

	        // 5️⃣ Calculate TOTAL from request for this sectionId/coverId group
	        BigDecimal totalFromItems = BigDecimal.ZERO;
	        boolean invalidNumber = false;

	        for (ContentItem ci : groupItems) {
	            if (ci == null) continue;
	            String value = ci.getValue();
	            if (value == null || value.isBlank()) {
	                continue;
	            }
	            try {
	                // Trim to avoid whitespace issues
	                totalFromItems = totalFromItems.add(new BigDecimal(value.trim()));
	            } catch (NumberFormatException e) {
	                // map this to a code if you have one, else add descriptive text
	                error.add("1762"); // invalid numeric value (use code if available)
	                invalidNumber = true;
	                break;
	            }
	        }

	        if (invalidNumber) {
	            // don't compare sums if value itself is invalid
	            continue;
	        }

	        // 6️⃣ Compare ONCE per section/cover – but add "1757" only once overall
	        if (declaredSumInsured.stripTrailingZeros()
	                .compareTo(totalFromItems.stripTrailingZeros()) != 0) {
	            if (!sumMismatchAdded) {
	                error.add("1757"); // Sum insured mismatch – add only once
	                sumMismatchAdded = true;
	            }
	            // If you want to stop validating other groups after first mismatch, uncomment:
	            // break;
	        }
	    }

	    // 7️⃣ Optional: remove duplicate error codes so each code appears only once overall
	    List<String> deduped = new ArrayList<>(new LinkedHashSet<>(error));

	    return deduped;
	}


}

