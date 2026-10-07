package com.maan.eway.jasper.service.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.MsAssetDetailsRepository;
import com.maan.eway.repository.MsHumanDetailsRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class jasperServiceNew {

	  @Autowired
	    private EntityManager em;
	  
	  @Autowired
	  private FactorRateRequestDetailsRepository factorRepo;
	  
	  @Autowired
	  private PolicyCoverDataRepository coverDataRepository;
	  
	  @Autowired
	  private InsuranceCompanyMasterRepository insuranceComMasRepo;
	  
	  @Autowired
	  private ExcessMasterRepository excessRepo;
	  
	  @Autowired
	  private EServiceMotorDetailsRepository eServiceMotorDetailsRepo;
	  
	  @Autowired
	  private EserviceCommonDetailsRepository eServiceCommonDetailsRepo;
	  
	  @Autowired
	  private EserviceCustomerDetailsRepository eServiceCustomerDetailsRepo;
	  
	  @Autowired
	  private EServiceSectionDetailsRepository eServiceSectionDetailsRepo;
	  
	  @Autowired
	  private EserviceBuildingDetailsRepository eServiceBuildingDetailsRepo;
	  
	  @Autowired
	  private MsAssetDetailsRepository msAssetDetailsRepo;
	  
	  @Autowired
	  private MsHumanDetailsRepository msHumanDetailsRepo;
	  
	  Logger log = LogManager.getLogger(jasperServiceNew.class);
	  

	    
	    private Map<String, Object> convertBuildingToMap(EserviceBuildingDetails b) {
	        Map<String, Object> map = new HashMap<>();

	        map.put("requestReferenceNo", b.getRequestReferenceNo());
	        map.put("riskId", b.getRiskId());
	        map.put("locationId", b.getLocationId());
	        map.put("customerReferenceNo", b.getCustomerReferenceNo());
	        map.put("sectionId", b.getSectionId());
	        map.put("coverId", b.getCoverId());

	        map.put("domesticPackageYn", b.getDomesticPackageYn());
	        map.put("productId", b.getProductId());
	        map.put("productDesc", b.getProductDesc());
	        map.put("sectionDesc", b.getSectionDesc());
	        map.put("companyId", b.getCompanyId());
	        map.put("companyName", b.getCompanyName());
	        map.put("branchCode", b.getBranchCode());
	        map.put("branchName", b.getBranchName());
	        map.put("inbuildConstructType", b.getInbuildConstructType());
	        map.put("buildingFloors", b.getBuildingFloors());
	        map.put("outbuildConstructType", b.getOutbuildConstructType());
	        map.put("buildingUsageYn", b.getBuildingUsageYn());
	        map.put("buildingUsageId", b.getBuildingUsageId());
	        map.put("buildingUsageDesc", b.getBuildingUsageDesc());
	        map.put("buildingOwnerYn", b.getBuildingOwnerYn());
	        map.put("buildingType", b.getBuildingType());
	        map.put("buildingOccupationType", b.getBuildingOccupationType());
	        map.put("buildingCondition", b.getBuildingCondition());
	        map.put("buildingAge", b.getBuildingAge());
	        map.put("buildingAreaSqm", b.getBuildingAreaSqm());
	        map.put("entryDate", b.getEntryDate());
	        map.put("createdBy", b.getCreatedBy());
	        map.put("status", b.getStatus());
	        map.put("updatedDate", b.getUpdatedDate());
	        map.put("updatedBy", b.getUpdatedBy());
	        map.put("quoteNo", b.getQuoteNo());
	        map.put("customerId", b.getCustomerId());
	        map.put("acExecutiveId", b.getAcExecutiveId());
	        map.put("applicationId", b.getApplicationId());
	        map.put("brokerCode", b.getBrokerCode());
	        map.put("subUserType", b.getSubUserType());
	        map.put("loginId", b.getLoginId());
	        map.put("agencyCode", b.getAgencyCode());
	        map.put("policyStartDate", b.getPolicyStartDate());
	        map.put("policyEndDate", b.getPolicyEndDate());
	        map.put("policyPeriord", b.getPolicyPeriord());
	        map.put("currency", b.getCurrency());
	        map.put("exchangeRate", b.getExchangeRate());
	        map.put("adminLoginId", b.getAdminLoginId());
	        map.put("adminRemarks", b.getAdminRemarks());
	        map.put("rejectReason", b.getRejectReason());
	        map.put("referalRemarks", b.getReferalRemarks());
	        map.put("actualPremiumFc", b.getActualPremiumFc());
	        map.put("actualPremiumLc", b.getActualPremiumLc());
	        map.put("overallPremiumFc", b.getOverallPremiumFc());
	        map.put("overallPremiumLc", b.getOverallPremiumLc());
	        map.put("oldReqRefNo", b.getOldReqRefNo());
	        map.put("brokerBranchCode", b.getBrokerBranchCode());
	        map.put("brokerBranchName", b.getBrokerBranchName());
	        map.put("commissionType", b.getCommissionType());
	        map.put("commissionTypeDesc", b.getCommissionTypeDesc());
	        map.put("havePromocode", b.getHavepromocode());
	        map.put("promocode", b.getPromocode());
	        map.put("insuranceType", b.getInsuranceType());
	        map.put("occupationType", b.getOccupationType());
	        map.put("occupationTypeDesc", b.getOccupationTypeDesc());
	        map.put("categoryId", b.getCategoryId());
	        map.put("categoryDesc", b.getCategoryDesc());
	        map.put("policyNo", b.getPolicyNo());
	        map.put("bankCode", b.getBankCode());
	        map.put("sourceType", b.getSourceType());
	        map.put("bdmCode", b.getBdmCode());
	        map.put("customerCode", b.getCustomerCode());
	        map.put("manualReferalYn", b.getManualReferalYn());
	        map.put("industryId", b.getIndustryId());
	        map.put("industryDesc", b.getIndustryDesc());
	        map.put("endorsementType", b.getEndorsementType());
	        map.put("endorsementTypeDesc", b.getEndorsementTypeDesc());
	        map.put("endorsementDate", b.getEndorsementDate());
	        map.put("endorsementRemarks", b.getEndorsementRemarks());
	        map.put("endorsementEffdate", b.getEndorsementEffdate());
	        map.put("originalPolicyNo", b.getOriginalPolicyNo());
	        map.put("endtPrevPolicyNo", b.getEndtPrevPolicyNo());
	        map.put("endtPrevQuoteNo", b.getEndtPrevQuoteNo());
	        map.put("endtCount", b.getEndtCount());
	        map.put("endtStatus", b.getEndtStatus());
	        map.put("isFinyn", b.getIsFinyn());
	        map.put("endtCategDesc", b.getEndtCategDesc());
	        map.put("endtPremium", b.getEndtPremium());
	        map.put("wallType", b.getWallType());
	        map.put("wallTypeDesc", b.getWallTypeDesc());
	        map.put("roofType", b.getRoofType());
	        map.put("roofTypeDesc", b.getRoofTypeDesc());
	        map.put("natureOfTradeId", b.getNatureOfTradeId());
	        map.put("natureOfTradeDesc", b.getNatureOfTradeDesc());
	        map.put("insuranceForId", b.getInsuranceForId());
	        map.put("insuranceForDesc", b.getInsuranceForDesc());
	        map.put("internalWallType", b.getInternalWallType());
	        map.put("internalWallDesc", b.getInternalWallDesc());
	        map.put("ceilingType", b.getCeilingType());
	        map.put("ceilingTypeDesc", b.getCeilingTypeDesc());
	        map.put("address", b.getAddress());
	        map.put("regionCode", b.getRegionCode());
	        map.put("regionDesc", b.getRegionDesc());
	        map.put("districtCode", b.getDistrictCode());
	        map.put("districtDesc", b.getDistrictDesc());
	        map.put("occupiedYear", b.getOccupiedYear());
	        
	        return map;
	    }
	    
	 
	    
	    private Map<String, Object> convertAssetToMap(MsAssetDetails a) {
	        Map<String, Object> map = new HashMap<>();

	        map.put("vdRefno", a.getVdRefno());
	        map.put("requestReferenceNo", a.getRequestReferenceNo());
	        map.put("locationId", a.getLocationId());
	        map.put("riskId", a.getRiskId());
	        map.put("productId", a.getProductId());
	        map.put("sectionId", a.getSectionId());
	        map.put("companyId", a.getCompanyId());
	        map.put("branchCode", a.getBranchCode());
	        map.put("endtTypeId", a.getEndtTypeId());
	        map.put("endtCategoryId", a.getEndtCategoryId());
	        map.put("coverId", a.getCoverId());

	        map.put("buildingAge", a.getBuildingAge());
	        map.put("buildingFloors", a.getBuildingFloors());
	        map.put("buildingUsageId", a.getBuildingUsageId());
	        map.put("periodOfInsurance", a.getPeriodOfInsurance());
	        map.put("currency", a.getCurrency());
	        map.put("exchangeRate", a.getExchangeRate());
	        map.put("entryDate", a.getEntryDate());
	        map.put("createdBy", a.getCreatedBy());
	        map.put("status", a.getStatus());
	        map.put("groupCount", a.getGroupCount());
	        map.put("havepromocode", a.getHavepromocode());
	        map.put("promocode", a.getPromocode());
	        map.put("inbuildConstructType", a.getInbuildConstructType());
	        map.put("internalWallType", a.getInternalWallType());
	        map.put("categoryId", a.getCategoryId());

	        return map;
	    }




	    private Map<String, Object> convertHumanToMap(MsHumanDetails h) {

	        Map<String, Object> map = new HashMap<>();

	        map.put("requestReferenceNo", h.getRequestReferenceNo());
	        map.put("humanId", h.getHumanId());
	        map.put("locationId", h.getLocationId());
	        map.put("vdRefno", h.getVdRefno());
	        map.put("groupId", h.getGroupId());
	        map.put("endtTypeId", h.getEndtTypeId());
	        map.put("endtCategoryId", h.getEndtCategoryId());

	        map.put("travelCoverId", h.getTravelCoverId());
	        map.put("sourceCountry", h.getSourceCountry());
	        map.put("destinationCountry", h.getDestinationCountry());
	        map.put("categoryId", h.getCategoryId());
	        map.put("sportsCoverYn", h.getSportsCoverYn());
	        map.put("terrorismCoverYn", h.getTerrorismCoverYn());
	        map.put("planTypeId", h.getPlanTypeId());
	        map.put("totalPassengers", h.getTotalPassengers());
	        map.put("age", h.getAge());
	        map.put("sumInsured", h.getSumInsured());
	        map.put("periodOfInsurance", h.getPeriodOfInsurance());

	        map.put("entryDate", h.getEntryDate());
	        map.put("createdBy", h.getCreatedBy());
	        map.put("status", h.getStatus());
	        map.put("havePromocode", h.getHavepromocode());
	        map.put("promocode", h.getPromocode());
	        map.put("currency", h.getCurrency());
	        map.put("exchangeRate", h.getExchangeRate());
	        map.put("covidCoverYn", h.getCovidCoverYn());
	        map.put("groupCount", h.getGroupCount());

	        map.put("natureOfBusinessId", h.getNatureOfBusinessId());
	        map.put("totalNoOfEmployees", h.getTotalNoOfEmployees());
	        map.put("totalExcludedEmployees", h.getTotalExcludedEmployees());
	        map.put("totalRejoinedEmployees", h.getTotalRejoinedEmployees());
	        map.put("accountOutstandingEmployees", h.getAccountOutstandingEmployees());
	        map.put("accountAuditentType", h.getAccountAuditentType());
	        map.put("totalOutstandingAmount", h.getTotalOutstandingAmount());


	        return map;
	    }
	    
	    private Map<String, Object> mapFactor(FactorRateRequestDetails f) {

	        Map<String, Object> map = new HashMap<>();

	        map.put("requestReferenceNo", f.getRequestReferenceNo());
	        map.put("companyId", f.getCompanyId());
	        map.put("vehicleId", f.getVehicleId());
	        map.put("coverageType", f.getCoverageType());
	        map.put("coverId", f.getCoverId());
	        map.put("subCoverId", f.getSubCoverId());
	        map.put("rate", f.getRate());
	        map.put("sumInsured", f.getSumInsured());
	        map.put("premiumBeforeDiscountLc", f.getPremiumBeforeDiscountLc());
	        map.put("premiumAfterDiscountLc", f.getPremiumAfterDiscountLc());
	        map.put("premiumIncludedTaxLc", f.getPremiumIncludedTaxLc());
	        map.put("premiumExcludedTaxLc", f.getPremiumExcludedTaxLc());
	        map.put("taxId", f.getTaxId());
	        map.put("taxRate", f.getTaxRate());
	        map.put("taxAmount", f.getTaxAmount());

	        return map;
	    }
	    
	    @SuppressWarnings("unchecked")
	    public List<Map<String, Object>> buildLocationSectionCover(List<FactorRateRequestDetails> inputList) {

	        Map<String, Map<String, Object>> locationMap = new LinkedHashMap<>();

	        for (FactorRateRequestDetails row : inputList) {

	            // --------- LOCATION LEVEL --------- //
	            String locId = row.getLocationId().toString();
	            Map<String, Object> locationObj = locationMap.get(locId);

	            if (locationObj == null) {
	                locationObj = new LinkedHashMap<>();
	                locationObj.put("LocationId", locId);
//	                locationObj.put("LocationName", row.getLocationName());

	                // Store section as Map (avoid duplicates)
	                locationObj.put("SectionDetails", new LinkedHashMap<String, Map<String, Object>>());
	                locationMap.put(locId, locationObj);
	            }


	            Map<String, Map<String, Object>> sectionMap =
	                    (Map<String, Map<String, Object>>) locationObj.get("SectionDetails");


	            // --------- SECTION LEVEL --------- //
	            String secId = row.getSectionId().toString();
	            Map<String, Object> sectionObj = sectionMap.get(secId);

	            if (sectionObj == null) {
	                sectionObj = new LinkedHashMap<>();
	                sectionObj.put("SectionId", secId);
//	                sectionObj.put("SectionName", row.getSectionName());
//	                sectionObj.put("SumInsured", row.getSumInsured());
//	                sectionObj.put("Premium", row.getPremium());

	                // NEW: Coverage Type List Buckets
	                sectionObj.put("BaseCovers", new ArrayList<>());
	                sectionObj.put("PremiumCovers", new ArrayList<>());
	                sectionObj.put("OptionalCovers", new ArrayList<>());
	                sectionObj.put("LoadingCovers", new ArrayList<>());
	                sectionObj.put("OverallPremium", new ArrayList<>());
	                sectionObj.put("AddOnCovers", new ArrayList<>());

	                sectionMap.put(secId, sectionObj);
	            }

	            
	                   

	            // --------- COVER OBJECT --------- //
	            Map<String, Object> coverObj = new LinkedHashMap<>();
	            coverObj.put("CoverName", row.getCoverName());
	            coverObj.put("requestReferenceNo", row.getRequestReferenceNo());
	            coverObj.put("companyId", row.getCompanyId());
	            coverObj.put("vehicleId", row.getVehicleId());
	            coverObj.put("coverageType", row.getCoverageType());
	            coverObj.put("coverId", row.getCoverId());
	            coverObj.put("subCoverId", row.getSubCoverId());
	            coverObj.put("rate", row.getRate());
	            coverObj.put("sumInsured", row.getSumInsured());
	            coverObj.put("premiumBeforeDiscountLc", row.getPremiumBeforeDiscountLc());
	            coverObj.put("premiumAfterDiscountLc", row.getPremiumAfterDiscountLc());
	            coverObj.put("premiumIncludedTaxLc", row.getPremiumIncludedTaxLc());
	            coverObj.put("premiumExcludedTaxLc", row.getPremiumExcludedTaxLc());
	            coverObj.put("coverageLimit", row.getCoverageLimit());
	            coverObj.put("taxId", row.getTaxId());
	            coverObj.put("taxRate", row.getTaxRate());
	            coverObj.put("taxAmount", row.getTaxAmount());
	            
	            
	            List<Map<String, Object>> excessdetails =new ArrayList<>();
	            coverObj.put("excessdetails", excessdetails);
	            
	            
	            
	            List<ExcessMaster> excessList = excessRepo
                        .findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
                                row.getCompanyId() == null ? "" : row.getCompanyId().toString(),
                                row.getProductId() == null ? "" : row.getProductId().toString(),
                                row.getSectionId() == null ? "" : row.getSectionId().toString(),
                                row.getCoverId() == null ? "" : row.getCoverId().toString()
                        );

                if (excessList != null && !excessList.isEmpty()) {

                    excessList.forEach(e2 -> {
                        Map<String,Object> e = new HashMap<>();
                        e.put("excessdesc", safe(e2.getExcessDescription()));
                        e.put("excessamount", e2.getExcessAmount() == null ? 0.00 : e2.getExcessAmount());
                        e.put("excessper", e2.getExcessPercentage() == null ? 0 : e2.getExcessPercentage());
                        e.put("excessName", safe(e2.getCoverName()));
                        excessdetails.add(e);
                    });
                }
                


	            // --------- COVERAGE TYPE GROUPING --------- //
	            switch (row.getCoverageType()) {

	                case "B":
	                    ((List<Map<String, Object>>) sectionObj.get("BaseCovers")).add(coverObj);
	                    break;

	                case "T":
	                    ((List<Map<String, Object>>) sectionObj.get("PremiumCovers")).add(coverObj);
	                    break;

	                case "O":
	                    ((List<Map<String, Object>>) sectionObj.get("OptionalCovers")).add(coverObj);
	                    break;

	                case "L":
	                    ((List<Map<String, Object>>) sectionObj.get("LoadingCovers")).add(coverObj);
	                    break;
	                    
	                case "A":
	                    ((List<Map<String, Object>>) sectionObj.get("AddOnCovers")).add(coverObj);
	                    break;
	                    
	          

	            }

	            // Overall Premium (vehicleId == 99999)
	            if (row.getVehicleId() != null && row.getVehicleId() == 99999) {
	                ((List<Map<String, Object>>) sectionObj.get("OverallPremium")).add(coverObj);
	            }
	        }


	        // --------- FINAL OUTPUT FORMAT (convert sectionMap values to list) --------- //
	        List<Map<String, Object>> locationFinalList = new ArrayList<>();

	        for (Map<String, Object> loc : locationMap.values()) {

	            Map<String, Map<String, Object>> sectionMap =
	                    (Map<String, Map<String, Object>>) loc.get("SectionDetails");

	            loc.put("SectionDetails", new ArrayList<>(sectionMap.values())); // final list
	            locationFinalList.add(loc);
	        }

	        return locationFinalList;
	    }





	    
	    public Map<String, Object> getAllDataSingleQuery(String reqRefNo,String productId) {


	        Map<String, Object> response = new HashMap<>();

	        List<Map<String, Object>> customerList = new ArrayList<>();
	        List<Map<String, Object>> buildingList = new ArrayList<>();
	        List<Map<String, Object>> commonList = new ArrayList<>();
	        List<Map<String, Object>> assetList = new ArrayList<>();
	        List<Map<String,Object>> excessConDetails = new ArrayList<Map<String,Object>>();
	        List<Map<String, Object>> humanList = new ArrayList<>();
	        Map<String, Object> companyDet = new HashMap<>();
	        List<Map<String,Object>> sectionList=new ArrayList<>();
	        
	        Object motorObj = null;
	        if ("5".equalsIgnoreCase(productId)) {
	            // motor product
	            EserviceMotorDetails motorRow = eServiceMotorDetailsRepo.findFirstByRequestReferenceNo(reqRefNo);
	            if (motorRow != null) {
	                motorObj = motorRow;
	            }
	        } else {
	            // other product -> section
	            EserviceSectionDetails sectionRow = eServiceSectionDetailsRepo.findFirstByRequestReferenceNo(reqRefNo);
	            if (sectionRow != null) {
	                motorObj = sectionRow;
	            }
	        }

	        String customerRefNo = "";

	        if ("5".equalsIgnoreCase(productId)) {
	            EserviceMotorDetails motor = (EserviceMotorDetails) motorObj;
	            customerRefNo = motor.getCustomerReferenceNo();
	        } else {
	            EserviceSectionDetails motor = (EserviceSectionDetails) motorObj;
	            customerRefNo = motor.getCustomerReferenceNo();
	        }

	        List<EserviceCustomerDetails> customers = eServiceCustomerDetailsRepo.findFirstByCustomerReferenceNo(customerRefNo);
	        if (customers != null && !customers.isEmpty()) {
	            customerList.add(convertToMap(customers.get(0), List.of("mobile1")));
	        }

	        // building
	        List<EserviceBuildingDetails> buildings = eServiceBuildingDetailsRepo.findByRequestReferenceNo(reqRefNo);
	        if (buildings != null && !buildings.isEmpty()) {
	            buildingList.add(convertBuildingToMap(buildings.get(0)));
	        }

	        // common
	        List<EserviceCommonDetails> commons = eServiceCommonDetailsRepo.findByRequestReferenceNo(reqRefNo);
	        if (commons != null && !commons.isEmpty()) {
	            commonList.add(convertCommonToMap(commons.get(0)));
	        }

	        // asset
	        List<MsAssetDetails> assets = msAssetDetailsRepo.findByRequestReferenceNo(reqRefNo);
	        if (assets != null && !assets.isEmpty()) {
	            assetList.add(convertAssetToMap(assets.get(0)));
	        }

	        // human
	        List<MsHumanDetails> humans = msHumanDetailsRepo.findByRequestReferenceNo(reqRefNo);
	        if (humans != null && !humans.isEmpty()) {
	            humanList.add(convertHumanToMap(humans.get(0)));
	        }

	        // factor / location details (you already have method)
	        List<FactorRateRequestDetails> factorList1 = factorRepo.findByRequestReferenceNo(reqRefNo);
	        List<Map<String, Object>> finalLocationData = buildLocationSectionCover(factorList1);

	        // --- 4) Get company/policy/section values from whichever entity is present (section preferred) ---
	        String companyId = "";
	        String policyNo = "";
	        String sectionId = "";

	        // Prefer section entity (you already used it earlier), otherwise use motorRow if present
	        EserviceSectionDetails sectionRow = eServiceSectionDetailsRepo.findFirstByRequestReferenceNo(reqRefNo);
	        if (sectionRow != null) {
	            companyId = safe(sectionRow.getCompanyId());
	            policyNo  = safe(sectionRow.getPolicyNo());
	            sectionId = safe(sectionRow.getSectionId());
	        } else {
	            // try motor
	            EserviceMotorDetails motorRow = eServiceMotorDetailsRepo.findFirstByRequestReferenceNo(reqRefNo);
	            if (motorRow != null) {
	                companyId = safe(motorRow.getCompanyId());
	                policyNo  = safe(motorRow.getPolicyNo());
	                sectionId = safe(motorRow.getSectionId());
	            }
	        }
	        
	    	// CONDITIONS
			List<Map<String,Object>> conditionList = getConditionList(policyNo, reqRefNo, sectionId);

			// EXCLUSION
			List<Map<String,Object>> exclusionRes = getExclusionList(policyNo, reqRefNo, sectionId);
			List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
				Map<String,Object> eMap = new HashMap<String,Object>();
				eMap.put("conditionTerms", k.get("exclusioTerms"));
				eMap.put("SectionId", k.get("SectionId"));
				return eMap;
			}).collect(Collectors.toList());
			
			//WARRANTY
			List<Map<String,Object>> warrantyList = getWarrantyDescription(policyNo, reqRefNo, sectionId);
			
			List<Map<String,Object>> allLists = new ArrayList<>();
			allLists.addAll(warrantyList);
			allLists.addAll(conditionList);
			allLists.addAll(exclusionList);
			
			List<LinkedHashMap<String, Object>> termsAndconditions =allLists.stream()
					.sorted(Comparator.comparing(p1 -> {
					    if (p1.get("Sno") == null || p1.get("Sno").toString().isEmpty()) {	
					        return Integer.MAX_VALUE;
					    }
					    try {
					        return Integer.parseInt(p1.get("Sno").toString());
					    } catch (NumberFormatException e) {
					        return Integer.MAX_VALUE;
					    }
					}))
					.map(u -> {
							LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
							m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
							return m;
						}).distinct().collect(Collectors.toList());

				int conditionsize = termsAndconditions.size();
				List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
				if(conditionsize>10) {
					int midIndex = conditionsize / 2;
					firstHalf = termsAndconditions.subList(0, midIndex);
					secondHalf = termsAndconditions.subList(midIndex, conditionsize);
				}else {
					firstHalf = termsAndconditions.subList(0, conditionsize);
				}
				
				final String finalSectionId = sectionId;
				List<PolicyCoverData> coverData = coverDataRepository.findByRequestReferenceNo(reqRefNo);
				List<PolicyCoverData> excessCon = coverData.stream()
						.filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getCoverageType()
						.equalsIgnoreCase("B") && f.getSectionId()==Integer.parseInt(finalSectionId))
						.distinct().collect(Collectors.toList());
				if(!excessCon.isEmpty()) {
					PolicyCoverData cpd = excessCon.get(0);
					Map<String,Object> excessMap = new HashMap<String,Object>();
					excessMap.put("excessPercent", cpd.getExcessPercent());
					excessMap.put("excessAmount", cpd.getExcessAmount());
					excessMap.put("excessDesc", cpd.getExcessDesc());
					excessMap.put("currency", cpd.getCurrency());
					excessConDetails.add(excessMap);
				}
				
				InsuranceCompanyMaster companyDetails = insuranceComMasRepo.findByCompanyId(companyId);
				if (companyDetails != null) {
					companyDet.put("companyName", safe(companyDetails.getCompanyName()));
//					companyDet.put("companylogo", safe(companyDetails.getCompanyLogo()));
					String logo = companyDetails.getCompanyLogo();
					if (logo == null || logo.trim().isEmpty()) {
					    companyDet.put("companylogo", null); 
					} else {
					    companyDet.put("companylogo", logo);
					}
					companyDet.put("companyWebsite", safe(companyDetails.getCompanyWebsite()));
					companyDet.put("companyMail", safe(companyDetails.getCompanyEmail()));
					companyDet.put("companyPhone", safe(companyDetails.getCompanyPhone()));
					companyDet.put("companyAddress", safe(companyDetails.getCompanyAddress()));
					companyDet.put("companyPoBox", safe(companyDetails.getPOBox()));
					companyDet.put("companyVrnNumber", safe(companyDetails.getVrnNumber()));
					companyDet.put("companyremarks", safe(companyDetails.getRemarks()));
					companyDet.put("signature", safe(companyDetails.getSignature()));

				}
				if(productId.equalsIgnoreCase("59")) {
				List<EserviceBuildingDetails> buildDetails=eServiceBuildingDetailsRepo.findByRequestReferenceNo(reqRefNo);
				if(!buildDetails.isEmpty()) {
					for(EserviceBuildingDetails bd:buildDetails) {
					Map<String,Object> map=new HashMap<>();
					map.put("sectionDesc",safe(bd.getSectionDesc()));
					map.put("sumInsured",safe(bd.getSumInsured()));
					sectionList.add(map);
					}
				}
				}
	       if(productId.equalsIgnoreCase("87")) {
	    		List<EserviceBuildingDetails> mem = eServiceBuildingDetailsRepo
				        .findByRequestReferenceNoAndCoverId(reqRefNo, 632);

				List<Map<String, Object>> membersList = new ArrayList<>();

				if (mem != null && !mem.isEmpty()) {

					 for (EserviceBuildingDetails item : mem) {
						 
						 Map<String, Object> membersMap = new LinkedHashMap<>();

				        String categoryDesc = Optional.ofNullable(item.getCategoryDesc()).orElse("");
				        String categoryId   = Optional.ofNullable(item.getCategoryId()).orElse("");

				        String contentId = Optional.ofNullable(item.getContentId()).orElse("");
				        String contentDesc = Optional.ofNullable(item.getContentDesc()).orElse("");
				        Integer coverId1 = Optional.ofNullable(item.getCoverId()).orElse(0);
//				        String sectionId = Optional.ofNullable(item.getSectionId()).orElse("");
//				        String companyId = Optional.ofNullable(item.getCompanyId()).orElse("");
//				        String productId = Optional.ofNullable(item.getProductId()).orElse("");
				        String wallType = Optional.ofNullable(item.getWallType()).orElse("");
				        String wallTypeDesc = Optional.ofNullable(item.getWallTypeDesc()).orElse("");
				        Integer buildingFloors = Optional.ofNullable(item.getBuildingFloors()).orElse(0);
				        BigDecimal sumInsured = Optional.ofNullable(item.getSumInsured()).orElse(BigDecimal.ZERO);
				        String indemityPeriod = Optional.ofNullable(item.getIndemityPeriod()).orElse("");
				        
				        membersMap.put("categoryDesc", categoryDesc);
				        membersMap.put("categoryId", categoryId);

				     
				        membersMap.put("contentId", contentId);
				        membersMap.put("contentDesc", contentDesc);
				        membersMap.put("coverId", coverId1);
				        membersMap.put("sectionId", sectionId);
				        membersMap.put("companyId", companyId);
				        membersMap.put("productId", productId);
				        membersMap.put("wallType", wallType);
				        membersMap.put("wallTypeDesc", wallTypeDesc);
				        membersMap.put("buildingFloors", buildingFloors);
				        membersMap.put("sumInsured", sumInsured);
				        membersMap.put("indemityPeriod", indemityPeriod);

				        membersList.add(membersMap);
				    }
				}

				response.put("membersList", membersList);

				EserviceBuildingDetails mem1 = eServiceBuildingDetailsRepo
				        .findFirstByRequestReferenceNoAndCoverId(reqRefNo, 632);
				BigDecimal sumInsured = BigDecimal.ZERO;
				String indemityPeriod = "";
				String wallType="";
				String wallTypeDesc="";
				if(mem1 != null) {
					  sumInsured = Optional.ofNullable(mem1.getSumInsured()).orElse(BigDecimal.ZERO);
				      indemityPeriod = Optional.ofNullable(mem1.getIndemityPeriod()).orElse("");
				      wallType = Optional.ofNullable(mem1.getWallType()).orElse("");
				      wallTypeDesc = Optional.ofNullable(mem1.getWallTypeDesc()).orElse("");
				}
				response.put("GrossIncome",sumInsured);
				response.put("limitIndemity",indemityPeriod);
				response.put("PerLimitOfIndemity",wallType);
				response.put("PerLimitOfIndemityDesc",wallTypeDesc);
	       }

	        response.put("status", "SUCCESS");
	        response.put("message", "Fetched Successfully");
	        response.put("motorDetails",
	                motorObj == null ? null : Arrays.asList(motorObj));
	        response.put("customerDetails",
	                customerList.isEmpty() ? null : customerList.get(0));

	        response.put("buildingDetails",
	                buildingList.isEmpty() ? null : buildingList.get(0));

	        response.put("commonDetails",
	                commonList.isEmpty() ? null : commonList.get(0));

	        response.put("assetDetails",
	                assetList.isEmpty() ? null : assetList.get(0));

	        response.put("humanDetails",
	                humanList.isEmpty() ? null : humanList.get(0));
	        response.put("companyDetails", companyDet);
	        response.put("firstHalfconditions", firstHalf);
	        response.put("secondHalfconditions", secondHalf);
	        response.put("excessConditions", excessConDetails);
	        response.put("sectionList", sectionList);

//	        response.put("baseCovers", baseCovers);
//	        response.put("premiumCovers", premiumCovers);
//	        response.put("optionalCovers", optionalCovers);
//	        response.put("loadingCovers", loadingCovers);
//	        response.put("overallPremium", overallPremium);
	        
	        response.put("LocationDetails", finalLocationData);
	        return response;
	    }

	    private String safe(Object value) {
	        return value == null ? "" : value.toString();
	    }
	    
	    public Map<String, Object> convertToMap(Object obj, List<String> exclude) {
	        Map<String, Object> map = new HashMap<>();

	        for (Field field : obj.getClass().getDeclaredFields()) {
	            try {
	                field.setAccessible(true);
	                if (!exclude.contains(field.getName())) {
	                    Object val = field.get(obj);
	                    if (val != null) {
	                        map.put(field.getName(), val);
	                    }
	                }
	            } catch (Exception ignored) {}
	        }
	        return map;
	    }

	    public Map<String, Object> convertCommonToMap(EserviceCommonDetails c) {
	        return convertToMap(c, List.of());
	    }

//	    FAK-MOT-05906

	    public List<Map<String,Object>> getConditionList(String policyNo,String requestReferenceNo, String sectionId){
			List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> conditionRes = new ArrayList<>();
			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
				
				List<Predicate> predicates = new ArrayList<Predicate>();
				
				if(StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot2.get("policyNo"), policyNo));
				}else {
					predicates.add(cb.equal(hpmRoot2.get("requestReferenceNo"), requestReferenceNo));
				}
				if(i == 1) {
					Subquery<Tuple> CquoteIn = cq2.subquery(Tuple.class);
					Root<TermsAndCondition> StacRoot = CquoteIn.from(TermsAndCondition.class);
					CquoteIn.select(StacRoot.get("requestReferenceNo")).where(cb.equal(StacRoot.get("requestReferenceNo"), requestReferenceNo),cb.equal(StacRoot.get("id"), "6"));
					
					Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sectionId), cb.equal(cmRoot2.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("requestReferenceNo"), hpmRoot2.get("requestReferenceNo")));
						predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")), cb.equal(cmRoot2.get("sectionId"), "99999")));
					}
					cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"),cmRoot2.get("sectionId").alias("sectionId"),cmRoot2.get("clausesId").alias("clausesId"));
					predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
					predicates.add(cb.equal(cmRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(cmRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(cmRoot2.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), cmRoot2.get("effectiveDateStart"), cmRoot2.get("effectiveDateEnd")));
					predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
					predicates.add(cb.equal(cmRoot2.get("typeId"), "D"));
					predicates.add(cb.not(cb.in(hpmRoot2.get("requestReferenceNo")).value(CquoteIn)));
				}else {
					Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(tacRoot2.get("sectionId"), sectionId), cb.equal(tacRoot2.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("requestReferenceNo"), hpmRoot2.get("requestReferenceNo")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), sddRoot2.get("sectionId")));
					}
					cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"),tacRoot2.get("sectionId").alias("sectionId"),tacRoot2.get("sno").alias("clausesId"));
					predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));
					predicates.add(cb.equal(tacRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
					predicates.add(cb.in(hpmRoot2.get("requestReferenceNo")).value(tacRoot2.get("requestReferenceNo")));
					predicates.add(cb.equal(tacRoot2.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(tacRoot2.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot2.get("id"), "6"));
				}
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
			}
			conditionList = conditionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
				Cmap.put("conditionTerms", c.get("conditionTerms")==null?"":c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Cmap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				return Cmap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getConditionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return conditionList;
		}
		
		public List<Map<String,Object>> getExclusionList(String policyNo,String requestReferenceNo,String sectionId){
			List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				List<Tuple> exclusionRes = new ArrayList<>();
				
				for(int i=1;i<=2;i++) {
					CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
					Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
					
					List<Predicate> predicates = new ArrayList<Predicate>();
					if(StringUtils.isNotBlank(policyNo)) {
						predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
					}else {
						predicates.add(cb.equal(hpmRoot3.get("requestReferenceNo"), requestReferenceNo));
					}
					if(i == 1) {
						
						Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
						Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
						EquoteIn.select(SEtacRoot.get("requestReferenceNo")).where(cb.equal(SEtacRoot.get("requestReferenceNo"), requestReferenceNo),cb.equal(SEtacRoot.get("id"), "7"));
						
						Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);
						if(StringUtils.isNotBlank(sectionId)) {
							predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sectionId), cb.equal(emRoot3.get("sectionId"), "99999")));
						}else {
							Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
							predicates.add(cb.equal(sddRoot3.get("requestReferenceNo"), hpmRoot3.get("requestReferenceNo")));
							predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId")), cb.equal(emRoot3.get("sectionId"), "99999")));
						}
						cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"),emRoot3.get("sectionId").alias("sectionId"),emRoot3.get("exclusionId").alias("exclusionId"));
						predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));
						predicates.add(cb.equal(emRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
						predicates.add(cb.or(cb.equal(emRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(emRoot3.get("branchCode"), "99999")));
						predicates.add(cb.between(cb.literal(new Date()), emRoot3.get("effectiveDateStart"), emRoot3.get("effectiveDateEnd")));
						predicates.add(cb.equal(emRoot3.get("status"), "Y"));
						predicates.add(cb.equal(emRoot3.get("typeId"), "D"));
						predicates.add(cb.not(cb.in(hpmRoot3.get("requestReferenceNo")).value(EquoteIn)));
						Predicate [] predicatArray = new Predicate[predicates.size()];
						predicates.toArray(predicatArray);
						exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
					}else {
						Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
						if(StringUtils.isNotBlank(sectionId)) {
							predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
						}else {
							Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
							predicates.add(cb.equal(sddRoot3.get("requestReferenceNo"), hpmRoot3.get("requestReferenceNo")));
							predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
						}
						
						cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("exclusionId"));
						predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
						predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
						
						predicates.add(cb.in(hpmRoot3.get("requestReferenceNo")).value(tacRoot3.get("requestReferenceNo")));
						predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
						predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
						predicates.add(cb.equal(tacRoot3.get("id"), "7"));
						Predicate [] predicatArray = new Predicate[predicates.size()];
						predicates.toArray(predicatArray);
						exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
					}
				}
				exclusionList = exclusionRes.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
					Emap.put("exclusioTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
					Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
					return Emap;
				}).collect(Collectors.toList());
			}catch(Exception e) {
				log.info("Error in getExclusionList ==> "+e.getMessage());
				e.printStackTrace();
			}
			return exclusionList;
		}
		
		public List<Map<String,Object>> getWarrantyDescription(String policyNo,String requestReferenceNo, String sectionId){
			List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				List<Tuple> warrantyRes = new ArrayList<>();
				
				for(int i=1;i<=2;i++) {
					CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
					Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
					List<Predicate> predicates = new ArrayList<Predicate>();
					if(StringUtils.isNotBlank(policyNo)) {
						predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
					}else {
						predicates.add(cb.equal(hpmRoot3.get("requestReferenceNo"), requestReferenceNo));
					}
					if(i == 1) {
						
						Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
						Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
						EquoteIn.select(SEtacRoot.get("requestReferenceNo")).where(cb.equal(SEtacRoot.get("requestReferenceNo"), requestReferenceNo),cb.equal(SEtacRoot.get("id"), "4"));
						Root<WarrantyMaster> wmRoot3 = cq3.from(WarrantyMaster.class);
						cq3.multiselect(wmRoot3.get("warrantyDescription").alias("warrantyTerms"),wmRoot3.get("sectionId").alias("sectionId"),wmRoot3.get("warrantyId").alias("warrantyId"));
						predicates.add(cb.equal(wmRoot3.get("companyId"), hpmRoot3.get("companyId")));
						predicates.add(cb.equal(wmRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
						predicates.add(cb.or(cb.equal(wmRoot3.get("sectionId"), sectionId), cb.equal(wmRoot3.get("sectionId"), "99999")));
						predicates.add(cb.or(cb.equal(wmRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(wmRoot3.get("branchCode"), "99999")));
						predicates.add(cb.between(cb.literal(new Date()), wmRoot3.get("effectiveDateStart"), wmRoot3.get("effectiveDateEnd")));
						predicates.add(cb.equal(wmRoot3.get("status"), "Y"));
						predicates.add(cb.equal(wmRoot3.get("typeId"), "D"));
						predicates.add(cb.not(cb.in(hpmRoot3.get("requestReferenceNo")).value(EquoteIn)));
						Predicate [] predicatArray = new Predicate[predicates.size()];
						predicates.toArray(predicatArray);
						warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
					}else {
						Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
						cq3.multiselect(tacRoot3.get("subIdDesc").alias("warrantyTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("warrantyId"));
						predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
						predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
						predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
						predicates.add(cb.in(hpmRoot3.get("requestReferenceNo")).value(tacRoot3.get("requestReferenceNo")));
						predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
						predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
						predicates.add(cb.equal(tacRoot3.get("id"), "4"));
						Predicate [] predicatArray = new Predicate[predicates.size()];
						predicates.toArray(predicatArray);
						warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
					}
				}
				warrantyList = warrantyRes.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
					Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
					Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
					Emap.put("Sno", c.get("warrantyId")==null?"":c.get("warrantyId").toString());
					return Emap;
				}).collect(Collectors.toList());
			}catch(Exception e) {
				log.info("Error in getWarrantyDescription ==> "+e.getMessage());
				e.printStackTrace();
			}
			return warrantyList;
			
		}
	    
	    
	    
}
