package com.maan.eway.jasper.risklist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;

@Service
public class RiskDetailsService {

    @Autowired
    private BuildingRiskDetailsRepository buildingRepo;

    @Autowired
    private CommonDataDetailsRepository commonRepo;

    private static final List<String> EXCLUDED_FIELDS = List.of("loginId","brokerTiraCode","domesticPackageYn",
    		"serialVersionUID","customerName","subUserType","sourceType","productId","companyName","customerCode"
    		,"productDesc","applicationId","companyId",
        "requestReferenceNo", "riskId", "quoteNo", "locationId", "customerReferenceNo",
        "locationName", "policyNo", "branchCode", "branchName", "agencyCode", "entryDate",
        "createdBy", "status", "updatedDate", "updatedBy", "policyStartDate", "policyEndDate",
        "actualPremiumFc", "actualPremiumLc", "overallPremiumFc", "overallPremiumLc",
        "oldReqRefNo", "havepromocode", "promocode", "brokerBranchCode", "brokerBranchName",
        "brokerCode", "customerId", "bdmCode", "manualReferalYn", "adminLoginId",
        "adminRemarks", "rejectReason", "referalRemarks", "tiraCoverNoteNo", "industryI",
        "endorsementType", "endorsementTypeDesc", "endorsementDate", "endorsementRemarks",
        "endorsementEffdate", "originalPolicyNo", "endtPrevPolicyNo", "endtPrevQuoteNo",
        "endtCount", "endtStatus", "endtCategDesc", "commissionPercentage", "vatCommission",
        "vatPremium", "endtVatPremium", "vdRefNo", "cdRefno", "msRefno", "bondType",
        "bondYear", "currency", "exchangeRate", "acExecutiveId", "bdmCode", "salePointCode",
        "sourceTypeId", "policyPeriord" 
    );

    public RiskDataResponse getDetailsByQuoteNo(String quoteNo) {

        List<BuildingRiskDetails> buildingRecords = buildingRepo.findByQuoteNo(quoteNo);
        List<SectionDetailsDTO> buildingSectionList = buildSectionResponse(buildingRecords);

        List<CommonDataDetails> commonRecords = commonRepo.findByQuoteNo(quoteNo);
        List<SectionDetailsDTO> commonSectionList = buildSectionResponse(commonRecords);

        return new RiskDataResponse(buildingSectionList, commonSectionList);
    }


    private <T> List<SectionDetailsDTO> buildSectionResponse(List<T> records) {

        Map<String, SectionDetailsDTO> sectionMap = new LinkedHashMap<>();

        for (T row : records) {

            String sectionId   = getField(row, "sectionId");
            String sectionName = getField(row, "sectionDesc");
            String coverId     = getField(row, "coverId");

            Map<String, Object> fieldMap = convertToFilteredMap(row);

            List<KeyValueDTO> kvList = fieldMap.entrySet().stream()
                    .map(e -> new KeyValueDTO(e.getKey(), e.getValue()))
                    .toList();

            CoverDetailsDTO cover = new CoverDetailsDTO(coverId, kvList);

            SectionDetailsDTO secDto = sectionMap.computeIfAbsent(
                    sectionId,
                    k -> new SectionDetailsDTO(sectionId, sectionName, new ArrayList<>())
            );

            secDto.getCoverDetails().add(cover);
        }

        return sectionMap.values().stream().toList();
    }


    private String getField(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            Object val = field.get(obj);
            return val == null ? null : String.valueOf(val);
        } catch (Exception e) {
            return null;
        }
    }


    private Map<String, Object> convertToFilteredMap(Object entity) {
        Map<String, Object> map = new HashMap<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();

                if (EXCLUDED_FIELDS.contains(fieldName)) {
                    continue;
                }

                Object value = field.get(entity);

                if (value == null) continue;
                if (value instanceof String && ((String) value).trim().isEmpty()) continue;

                map.put(fieldName, value);

            } catch (Exception ignored) {}
        }

        return map;
    }
}
