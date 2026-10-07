package com.maan.eway.excelupload.service;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.endorsment.request.NonMotEndtReq;
import com.maan.eway.excelupload.bean.AdditionalInformation;
import com.maan.eway.excelupload.bean.TemplateEntity;
import com.maan.eway.excelupload.dto.AdditionalInfoRequest;
import com.maan.eway.excelupload.dto.AdditionalInformationFlatRequest;
import com.maan.eway.excelupload.dto.AdditionalInformationResponse;
import com.maan.eway.excelupload.dto.ColumnDef;
import com.maan.eway.excelupload.dto.ColumnMeta;
import com.maan.eway.excelupload.dto.CoverResponse;
import com.maan.eway.excelupload.dto.EndtDeleteReq;
import com.maan.eway.excelupload.dto.LocationResponse;
import com.maan.eway.excelupload.dto.RiskResponse;
import com.maan.eway.excelupload.dto.SectionResponse;
import com.maan.eway.excelupload.dto.TemplateCreateRequest;
import com.maan.eway.excelupload.dto.TemplateGetResponse;
import com.maan.eway.excelupload.dto.additionalInfoGetResponse;
import com.maan.eway.excelupload.repository.AdditionalInformationRepository;
import com.maan.eway.excelupload.repository.TemplateRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {

	@Autowired
    private  TemplateRepository templateRepository;
	
	@Autowired
	private AdditionalInformationRepository additionalInformationrepo;
	
	@Autowired 
	private HomePositionMasterRepository homeRepo; 
	
	@Autowired
	private BuildingRiskDetailsRepository buildingRiskRepo;
	
	@Autowired
	private ProductSectionMasterRepository productSectionRepo;

	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	
	@Autowired
	private  EserviceBuildingDetailsRepository eserviceBuildRepo ;
	
	@Autowired
	private   EserviceCommonDetailsRepository eserviceCommonRepo;
	
	@Autowired
	private  EServiceSectionDetailsRepository eserviceSectionRepo;
	
	 @Value("${risk.add.info.endt}")
	 private String nonmotorsave;
	 
	 @Autowired
	 private RestTemplate restTemplate;
	 
	 private static final Logger log = LoggerFactory.getLogger(TemplateService.class);
	
    private final ObjectMapper objectMapper;

    
    private static final List<String> FIXED_FIRST_FIELDS =
            List.of("VALUE", "DATE");

    @Transactional
    public TemplateGetResponse createTemplate(TemplateCreateRequest req)
            throws Exception {

        validateCreateRequest(req);

        LocalDate today          = LocalDate.now();
        LocalDate effectiveStart = (req.getEffectiveStartDate() != null)
                                    ? req.getEffectiveStartDate()
                                    : today;
        LocalDate effectiveEnd   = effectiveStart.plusYears(50);

        List<TemplateEntity> existing = templateRepository.findActiveByKeys(
                req.getCompanyId(), req.getProductId(),
                req.getSectionId(), req.getCoverId());

        int newAmendId = 0;

        if (!existing.isEmpty()) {
            TemplateEntity current = existing.get(0);
            current.setEffectiveEndDate(effectiveStart.minusDays(1));
            current.setStatusTf("N");
            current.setUpdateDate(today);
            templateRepository.save(current);

            newAmendId = current.getAmendId() + 1;
        }

        List<ColumnDef> ordered = reorderColumns(req.getColumns());

        List<ColumnDef> columnDefs = ordered.stream()
                .map(c -> ColumnDef.builder()
                        .headerName(c.getHeaderName())
                        .targetField(c.getTargetField().toUpperCase())
                        .dataType(c.getDataType().toUpperCase())
                        .required(c.isRequired())
                        .numericOnly(c.isNumericOnly())
                        .dateFormat(c.getDateFormat())
                        .build())
                .collect(Collectors.toList());

        TemplateEntity newEntity = TemplateEntity.builder()
                .name(req.getName())
                .companyId(req.getCompanyId())
                .productId(req.getProductId())
                .sectionId(req.getSectionId())
                .coverId(req.getCoverId())
                .entryDate(today)
                .effectiveStartDate(effectiveStart)
                .effectiveEndDate(effectiveEnd)
                .updateDate(today)
                .amendId(newAmendId)
                .columnsJson(objectMapper.writeValueAsString(columnDefs))
                .statusTf("Y")
                .riskYn(req.getRiskYn())
                .tableName(req.getTableName())
                .columnName(req.getColumnName())
                .build();

        TemplateEntity saved = templateRepository.save(newEntity);
        return mapToGetResponse(saved);   
    }
    
    public TemplateGetResponse getTemplateById(Long id) throws Exception {

        TemplateEntity entity = templateRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Template not found for id=" + id));

        return mapToGetResponse(entity);
    }

 
    public TemplateGetResponse getTemplateResponseByKeys(
            String companyId, String productId,
            String sectionId, String coverId) throws Exception {

        if (companyId == null || companyId.isBlank())
            throw new RuntimeException("CompanyId is mandatory");
        if (productId == null || productId.isBlank())
            throw new RuntimeException("ProductId is mandatory");
        if (sectionId == null || sectionId.isBlank())
            throw new RuntimeException("SectionId is mandatory");
        if (coverId == null || coverId.isBlank())
            throw new RuntimeException("CoverId is mandatory");

        List<TemplateEntity> list = templateRepository.findActiveByKeys(
                companyId, productId, sectionId, coverId);

        if (list == null || list.isEmpty())
            throw new RuntimeException(
                "No active template found for CompanyId=" + companyId
                + ", ProductId=" + productId
                + ", SectionId=" + sectionId
                + ", CoverId=" + coverId);

        return mapToGetResponse(list.get(0));
    }

    public List<TemplateGetResponse> getTemplateHistory(
            String companyId, String productId,
            String sectionId, String coverId) throws Exception {

        List<TemplateEntity> all = templateRepository.findAllVersionsByKeys(
                companyId, productId, sectionId, coverId);

        if (all == null || all.isEmpty())
            throw new RuntimeException(
                "No template history found for the given keys");

        return all.stream()
                  .map(e -> { try { return mapToGetResponse(e); }
                              catch (Exception ex) { throw new RuntimeException(ex); } })
                  .collect(Collectors.toList());
    }

    public List<TemplateGetResponse> getTemplatesByProduct(
            String companyId, String productId) throws Exception {

        if (companyId == null || companyId.isBlank())
            throw new RuntimeException("CompanyId is mandatory");
        if (productId == null || productId.isBlank())
            throw new RuntimeException("ProductId is mandatory");

        List<TemplateEntity> entities = templateRepository
                .findActiveByProduct(companyId, productId);

        if (entities == null || entities.isEmpty())
            throw new RuntimeException(
                "No active templates found for CompanyId=" + companyId
                + ", ProductId=" + productId);

        return entities.stream()
                       .map(e -> { try { return mapToGetResponse(e); }
                                   catch (Exception ex) { throw new RuntimeException(ex); } })
                       .collect(Collectors.toList());
    }

    public List<TemplateGetResponse> getAllTemplates() throws Exception {

        List<TemplateEntity> entities = templateRepository.findAllActive();

        if (entities == null || entities.isEmpty())
            throw new RuntimeException("No active templates found");

        return entities.stream()
                       .map(e -> { try { return mapToGetResponse(e); }
                                   catch (Exception ex) { throw new RuntimeException(ex); } })
                       .collect(Collectors.toList());
    }

    private TemplateGetResponse mapToGetResponse(TemplateEntity e)
            throws Exception {

        List<ColumnDef> columnDefs = objectMapper.readValue(
                e.getColumnsJson(),
                new TypeReference<List<ColumnDef>>() {});

        return TemplateGetResponse.builder()
                .templateId(e.getId())
                .excelName(e.getName())
                .companyId(e.getCompanyId())
                .productId(e.getProductId())
                .sectionId(e.getSectionId())
                .coverId(e.getCoverId())
                .effectiveStartDate(e.getEffectiveStartDate())
                .active(e.getStatusTf())
                .columnList(columnDefs)
                .build();
    }

    private void validateCreateRequest(TemplateCreateRequest req) {

        List<ColumnDef> cols = req.getColumns();

        Set<String> seen = new HashSet<>();
        for (ColumnDef c : cols) {
            String tf = c.getTargetField().toUpperCase();
            if (!seen.add(tf))
                throw new RuntimeException(
                    "Duplicate TargetField: " + tf);
        }

        for (ColumnDef c : cols) {
//            if ("DATE".equalsIgnoreCase(c.getDataType())
//                    && (c.getDateFormat() == null || c.getDateFormat().isBlank()))
//                throw new RuntimeException(
//                    "Column '" + c.getHeaderName()
//                    + "' has DataType=DATE but dateFormat is missing.");

            // numericOnly only valid on NUMBER columns
            if (c.isNumericOnly()
                    && !"NUMBER".equalsIgnoreCase(c.getDataType()))
                throw new RuntimeException(
                    "Column '" + c.getHeaderName()
                    + "': numericOnly=true is only allowed for DataType=NUMBER.");
        }

        // VALUE column must be NUMBER + numericOnly
        cols.stream()
            .filter(c -> "VALUE".equalsIgnoreCase(c.getTargetField()))
            .findFirst()
            .ifPresent(v -> {
                if (!"NUMBER".equalsIgnoreCase(v.getDataType()))
                    throw new RuntimeException("VALUE column must have DataType=NUMBER.");
                if (!v.isNumericOnly())
                    throw new RuntimeException("VALUE column must have numericOnly=true.");
            });
    }
    
    private List<ColumnDef> reorderColumns(List<ColumnDef> cols) {
        List<ColumnDef> fixed   = new ArrayList<>();
        List<ColumnDef> rest    = new ArrayList<>();

        ColumnDef valueCol = null;
        ColumnDef dateCol  = null;

        for (ColumnDef c : cols) {
            String tf = c.getTargetField().toUpperCase();
            if ("VALUE".equals(tf))      valueCol = c;
            else if ("DATE".equals(tf))  dateCol  = c;
            else                         rest.add(c);
        }

        if (valueCol != null) fixed.add(valueCol);
        if (dateCol  != null) fixed.add(dateCol);
        fixed.addAll(rest);
        return fixed;
    }

    public Optional<TemplateEntity> getTemplate(Long id) {
        return templateRepository.findById(id);
    }

    public List<TemplateEntity> findTemplates(String companyId, String productId, String sectionId, String coverId) {
        return templateRepository.findByCompanyIdAndProductIdAndSectionIdAndCoverId(
                companyId, productId, sectionId, coverId
        );
    }

    public Optional<TemplateEntity> getTemplateByKey(String companyId,
            String productId,
            String sectionId,
            String coverId) {

			System.out.println("getTemplateByKeys: companyId=" + companyId
			+ ", productId=" + productId
			+ ", sectionId=" + sectionId
			+ ", coverId=" + coverId);
			
			List<TemplateEntity> list = templateRepository.findByKeys(
			companyId, productId, sectionId, coverId);
			
			if (list == null || list.isEmpty()) {
			return Optional.empty();
			}
			
			return Optional.of(list.get(0));
	}

    private final HomePositionMasterRepository homePositionMasterRepository;
    private final PolicyCoverDataRepository policyCoverDataRepository;
    private final ExcelTemplateGenerator generator;


    public additionalInfoGetResponse getTemplateByQuoteNo(AdditionalInfoRequest req) throws Exception {
    	
    	
    	NonMotEndtReq ent = new NonMotEndtReq();
    	if(StringUtils.isBlank(req.getEndtTypeId()))
        {
        String quoteNo = req.getQuoteNo();
        HomePositionMaster home = homePositionMasterRepository.findByQuoteNo(quoteNo);
        if (home == null) throw new RuntimeException("Invalid Quote No");
        String companyId = home.getCompanyId();
        String productId = String.valueOf(home.getProductId());
        List<PolicyCoverData> covers = policyCoverDataRepository.findByQuoteNo(quoteNo);
        if (covers == null || covers.isEmpty())
            throw new RuntimeException("No covers found for this quote");

        List<PolicyCoverData> filteredCovers = covers.stream()
                .filter(c -> "B".equalsIgnoreCase(c.getCoverageType())
                          || "O".equalsIgnoreCase(c.getCoverageType()))
                .collect(Collectors.toList());

        Map<Integer, List<PolicyCoverData>> locationMap = filteredCovers.stream()
                .collect(Collectors.groupingBy(PolicyCoverData::getLocationId));

        List<LocationResponse> locationResponses = new ArrayList<>();

        for (Map.Entry<Integer, List<PolicyCoverData>> locationEntry : locationMap.entrySet()) {

            Integer locationId           = locationEntry.getKey();
            List<PolicyCoverData> locationCovers = locationEntry.getValue();

            Map<Integer, List<PolicyCoverData>> sectionMap = locationCovers.stream()
                    .collect(Collectors.groupingBy(PolicyCoverData::getSectionId));

            List<SectionResponse> sectionResponses = new ArrayList<>();

            for (Map.Entry<Integer, List<PolicyCoverData>> sectionEntry : sectionMap.entrySet()) {

                Integer sectionId            = sectionEntry.getKey();
                List<PolicyCoverData> sectionCovers = sectionEntry.getValue();

                // ── Resolve motorYN once per section ──────────────────────────
                String motorYN = null;
                List<ProductSectionMaster> sectionMasterList =
                        productSectionRepo.findByCompanyIdAndProductIdAndSectionIdOrderByAmendIdDesc(
                                home.getCompanyId(), home.getProductId(), sectionId);
                if (sectionMasterList != null && !sectionMasterList.isEmpty()) {
                    motorYN = sectionMasterList.get(0).getMotorYn();
                }
                // ──────────────────────────────────────────────────────────────

                Map<Integer, List<PolicyCoverData>> coverMap = sectionCovers.stream()
                        .collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

                List<CoverResponse> coverResponses = new ArrayList<>();
                String sectionName = (sectionMasterList != null && !sectionMasterList.isEmpty())
                        ? sectionMasterList.get(0).getSectionName()
                        : null;

                for (Map.Entry<Integer, List<PolicyCoverData>> coverEntry : coverMap.entrySet()) {

                    Integer coverId                        = coverEntry.getKey();
                    List<PolicyCoverData> sameCoverRecords = coverEntry.getValue();
                    PolicyCoverData first                  = sameCoverRecords.get(0);

                    BigDecimal totalSumInsured = sameCoverRecords.stream()
                            .map(PolicyCoverData::getSumInsured)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    List<TemplateEntity> templates = templateRepository.findByKey(
                            companyId, productId, sectionId, coverId);

                    if (templates == null || templates.isEmpty()) continue;

                    TemplateEntity template = templates.get(0);

                    List<ColumnDef> columnDefs = Arrays.asList(
                            objectMapper.readValue(
                                    template.getColumnsJson(), ColumnDef[].class));

                    List<ColumnMeta> columnMetas = columnDefs.stream()
                            .map(col -> ColumnMeta.builder()
                                    .headerName(col.getHeaderName())
                                    .dataType(col.getDataType())
                                    .apiKey(col.getTargetField())
                                    .build())
                            .toList();

                    byte[] bytes  = generator.generateXlsx(template);
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    base64 = "data:application/vnd.openxmlformats-officedocument"
                           + ".spreadsheetml.sheet;base64," + base64;

                    // ── RiskYN=Y → build RiskList dynamically ─────────────────
                    if ("Y".equalsIgnoreCase(template.getRiskYn())) {

                        List<RiskResponse> riskList = new ArrayList<>();
                        
                        // Decide which table to query based on motorYN
                        if ("A".equalsIgnoreCase(motorYN)) {

                            // BuildingRiskDetails path
                            List<BuildingRiskDetails> riskRows = buildingRiskRepo
                                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
                                            quoteNo, companyId, productId,
                                            String.valueOf(sectionId),
                                            Integer.valueOf(coverId));

                            for (BuildingRiskDetails riskRow : riskRows) {

                            	  List<AdditionalInformation> savedRecords =
                                          additionalInformationrepo
                                              .findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndRiskIdAndStatus(
                                                      quoteNo, companyId, productId,
                                                      String.valueOf(locationId),
                                                      String.valueOf(sectionId),
                                                      String.valueOf(coverId),
                                                      String.valueOf(riskRow.getRiskId()),"Y");

                                // Dynamically read whatever column is in template.getColumnName()
                                String description = resolveColumnValue(
                                        riskRow, template.getColumnName());

                                riskList.add(RiskResponse.builder()
                                        .riskId(String.valueOf(riskRow.getRiskId()))
                                        .description(description)
                                        .sumInsured(riskRow.getSumInsured())
                                        .additionalInfo(
                                            buildAdditionalInfoList(savedRecords, columnDefs))
                                        .build());
                            }
                            
                            
                            if (StringUtils.isNotBlank(req.getEndtTypeId())) {
                                List<EserviceBuildingDetails>  eserviceList = eserviceBuildRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
                                
                              
                            }



                        } else {

                           
                            List<CommonDataDetails> riskRows = commonDataRepo
                                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
                                            quoteNo, companyId, productId,
                                            String.valueOf(sectionId),
                                            Integer.valueOf(coverId));

                            for (CommonDataDetails riskRow : riskRows) {

                                List<AdditionalInformation> savedRecords =
                                        additionalInformationrepo
                                            .findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndRiskIdAndStatus(
                                                    quoteNo, companyId, productId,
                                                    String.valueOf(locationId),
                                                    String.valueOf(sectionId),
                                                    String.valueOf(coverId),
                                                    String.valueOf(riskRow.getRiskId()),"Y");

                               
                                String description = resolveColumnValue(
                                        riskRow, template.getColumnName());

                                riskList.add(RiskResponse.builder()
                                        .riskId(String.valueOf(riskRow.getRiskId()))
                                        .description(description)
                                        .sumInsured(riskRow.getSumInsured())
                                        .additionalInfo(
                                            buildAdditionalInfoList(savedRecords, columnDefs))
                                        .build());
                            }
                        }

                        coverResponses.add(CoverResponse.builder()
                                .coverId(String.valueOf(coverId))
                                .coverName(first.getCoverName())
                                .sumInsured(totalSumInsured)
                                .columns(columnMetas)
                                .templateBase64(base64)
                                .riskList(riskList)      
                                .additionaInfo(null)     
                                .build());
                        
                        if (StringUtils.isNotBlank(req.getEndtTypeId())) {
                            List<EserviceCommonDetails>  eserviceList = eserviceCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
                            
                            if (eserviceList != null && !eserviceList.isEmpty()) {
                            	EserviceCommonDetails eservice = eserviceList.get(0);
                            	ent.setEndorsementDate(eservice.getEndorsementDate());
                                ent.setEndorsementRemarks(eservice.getEndorsementRemarks());
                                ent.setEndorsementEffdate(eservice.getEndorsementEffdate());
                                ent.setOriginalPolicyNo(eservice.getOriginalPolicyNo());
                                ent.setEndtPrevPolicyNo(eservice.getEndtPrevPolicyNo());
                                ent.setEndtPrevQuoteNo(eservice.getEndtPrevQuoteNo());
                                ent.setEndtCount(eservice.getEndtCount());
                                ent.setEndtStatus(eservice.getEndtStatus());
                                ent.setIsFinaceYn(eservice.getIsFinaceYn());
                                ent.setEndtCategDesc(eservice.getEndtCategDesc());
                                ent.setEndorsementType(eservice.getEndorsementType());
                                ent.setEndorsementTypeDesc(eservice.getEndorsementTypeDesc());
                                ent.setPolicyNo(eservice.getPolicyNo());
                                ent.setCoverModification("Y");
//                                originalPolicyNo=eservice.getOriginalPolicyNo();
//                                endtPolicyNo=eservice.getPolicyNo();
                            }
                        }

                    } else {
                    // ── RiskYN=N → existing flat behaviour ────────────────────

                        List<AdditionalInformation> savedRecords =
                                additionalInformationrepo
                                    .findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndStatus(
                                            quoteNo, companyId, productId,
                                            String.valueOf(locationId),
                                            String.valueOf(sectionId),
                                            String.valueOf(coverId),"Y");

                        coverResponses.add(CoverResponse.builder()
                                .coverId(String.valueOf(coverId))
                                .coverName(first.getCoverName())
                                .sumInsured(totalSumInsured)
                                .columns(columnMetas)
                                .templateBase64(base64)
                                .riskList(null)         
                                .additionaInfo(buildAdditionalInfoList(savedRecords, columnDefs))
                                .build());
                    }
                }

                if (!coverResponses.isEmpty()) {
                    sectionResponses.add(SectionResponse.builder()
                            .sectionId(String.valueOf(sectionId))
                            .sectionName(sectionName)
                            .covers(coverResponses)
                            .build());
                }
            }

            if (!sectionResponses.isEmpty()) {
                locationResponses.add(LocationResponse.builder()
                        .locationId(String.valueOf(locationId))
                        .sectionList(sectionResponses)
                        .build());
            }
            
        }
        if (!StringUtils.isNotBlank(req.getEndtTypeId())) {
            return additionalInfoGetResponse.builder()
                    .companyId(companyId)
                    .productId(productId)
                    .locationList(locationResponses)
                    .build();
            }
           	 
    }else
	{
		List<EserviceSectionDetails> eserviceSectionDetailsList = eserviceSectionRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
		HomePositionMaster home =homePositionMasterRepository.findByPolicyNo(req.getPolicyNo());
		
		if (eserviceSectionDetailsList == null || eserviceSectionDetailsList.isEmpty()) {
			throw new RuntimeException("No home Section Details found for this policyNo");
		}
		
		if (eserviceSectionDetailsList == null || eserviceSectionDetailsList.isEmpty()) {
			throw new RuntimeException("No Eservice Section Details found for this quote");
		}
		String quoteNo=null;
		if (home != null) {
			quoteNo =home.getQuoteNo();
		}

		String companyId2 = eserviceSectionDetailsList.get(0).getCompanyId();
		String productId2 = eserviceSectionDetailsList.get(0).getProductId();
		String originalPolicyNo="";
		String endtPolicyNo="";
		/*
		 * ============================================================ LOCATION LOOP
		 * Main looping source is now EserviceSectionDetails
		 * ============================================================
		 */
		Map<Integer, List<EserviceSectionDetails>> locationMap = eserviceSectionDetailsList.stream()
				.filter(Objects::nonNull).filter(e -> e.getLocationId() != null)
				.collect(Collectors.groupingBy(EserviceSectionDetails::getLocationId));

		List<LocationResponse> locationResponses = new ArrayList<>();

		for (Map.Entry<Integer, List<EserviceSectionDetails>> locationEntry : locationMap.entrySet()) {

			Integer locationId = locationEntry.getKey();

			List<EserviceSectionDetails> locationDetails = locationEntry.getValue();

			Map<String, List<EserviceSectionDetails>> sectionMap = locationDetails.stream()
					.filter(e -> e.getSectionId() != null)
					.collect(Collectors.groupingBy(EserviceSectionDetails::getSectionId));

			List<SectionResponse> sectionResponses = new ArrayList<>();

			for (Map.Entry<String, List<EserviceSectionDetails>> sectionEntry : sectionMap.entrySet()) {

				String sectionIdStr = sectionEntry.getKey();

				Integer sectionId = Integer.valueOf(sectionIdStr);

				List<EserviceSectionDetails> sectionDetails = sectionEntry.getValue();

				String motorYN = null;

				List<ProductSectionMaster> sectionMasterList = productSectionRepo.findByCompanyIdAndProductIdAndSectionIdOrderByAmendIdDesc(companyId2,Integer.valueOf(productId2), sectionId);

				if (sectionMasterList != null && !sectionMasterList.isEmpty()) {

					motorYN = sectionMasterList.get(0).getMotorYn();
				}

				/*
				 * ==================================================== Section Name
				 * ====================================================
				 *
				 * EserviceSectionDetails already contains SECTION_NAME
				 */
				String sectionName = sectionDetails.stream().map(EserviceSectionDetails::getSectionName)
						.filter(StringUtils::isNotBlank).findFirst()
						.orElse(sectionMasterList != null && !sectionMasterList.isEmpty()
								? sectionMasterList.get(0).getSectionName()
								: null);

				/*
				 * ==================================================== COVER LOOP
				 * ====================================================
				 */
				Map<Integer, List<EserviceSectionDetails>> coverMap = sectionDetails.stream()
						.filter(e -> e.getCoverId() != null)
						.collect(Collectors.groupingBy(EserviceSectionDetails::getCoverId));

				List<CoverResponse> coverResponses = new ArrayList<>();

				for (Map.Entry<Integer, List<EserviceSectionDetails>> coverEntry : coverMap.entrySet()) {

					Integer coverId = coverEntry.getKey();

					List<EserviceSectionDetails> sameCoverRecords = coverEntry.getValue();

					EserviceSectionDetails first = sameCoverRecords.get(0);

					List<TemplateEntity> templates = templateRepository.findByKey(companyId2, productId2, sectionId,
							coverId);

					if (templates == null || templates.isEmpty()) {
						continue;
					}

					TemplateEntity template = templates.get(0);

					/*
					 * ================================================= Column Definition
					 * =================================================
					 */
					List<ColumnDef> columnDefs = Arrays.asList(objectMapper.readValue(template.getColumnsJson(), ColumnDef[].class));

					List<ColumnMeta> columnMetas = columnDefs.stream()
							.map(col -> ColumnMeta.builder().headerName(col.getHeaderName()).dataType(col.getDataType())
									.apiKey(col.getTargetField()).build())
							.toList();

					/*
					 * ================================================= Generate Excel Template
					 * =================================================
					 */
					byte[] bytes = generator.generateXlsx(template);

					String base64 = Base64.getEncoder().encodeToString(bytes);

					base64 = "data:application/vnd.openxmlformats-officedocument" + ".spreadsheetml.sheet;base64,"
							+ base64;

					/*
					 * ================================================= RiskYN = Y
					 * =================================================
					 */
					if ("Y".equalsIgnoreCase(template.getRiskYn())) {

						List<RiskResponse> riskList = new ArrayList<>();

						/*
						 * ================================================= BUILDING motorYN = A
						 * =================================================
						 */
						if ("A".equalsIgnoreCase(motorYN)) {
							

							 List<EserviceBuildingDetails> riskRows = eserviceBuildRepo
									.findByRequestReferenceNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(req.getRequestReferenceNo(), companyId2,
											productId2, String.valueOf(sectionId), coverId);

							if (riskRows != null) {
								  if (riskRows != null && !riskRows.isEmpty()) {
	                                	EserviceBuildingDetails eservice = riskRows.get(0);
	                                	ent.setEndorsementDate(eservice.getEndorsementDate());
	                                    ent.setEndorsementRemarks(eservice.getEndorsementRemarks());
	                                    ent.setEndorsementEffdate(eservice.getEndorsementEffdate());
	                                    ent.setOriginalPolicyNo(eservice.getOriginalPolicyNo());
	                                    ent.setEndtPrevPolicyNo(eservice.getEndtPrevPolicyNo());
	                                    ent.setEndtPrevQuoteNo(eservice.getEndtPrevQuoteNo());
	                                    ent.setEndtCount(eservice.getEndtCount());
	                                    ent.setEndtStatus(eservice.getEndtStatus());
	                                    ent.setIsFinaceYn(eservice.getIsFinyn());
	                                    ent.setEndtCategDesc(eservice.getEndtCategDesc());
	                                    ent.setEndorsementType(eservice.getEndorsementType());
	                                    ent.setEndorsementTypeDesc(eservice.getEndorsementTypeDesc());
	                                    ent.setPolicyNo(eservice.getPolicyNo());
	                                    ent.setCoverModification("Y");
	                                    originalPolicyNo=eservice.getOriginalPolicyNo();
	                                    endtPolicyNo=eservice.getPolicyNo();
	                                }

								for (EserviceBuildingDetails riskRow : riskRows) {
									List<AdditionalInformation> savedRecords = additionalInformationrepo.findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndRiskIdAndStatus(
													quoteNo, companyId2, productId2, String.valueOf(locationId),
													String.valueOf(sectionId), String.valueOf(coverId),
													String.valueOf(riskRow.getRiskId()), "Y");

									String description = resolveColumnValue(riskRow, template.getColumnName());
									
									riskList.add(RiskResponse.builder().riskId(String.valueOf(riskRow.getRiskId()))
											.description(description).sumInsured(riskRow.getSumInsured())
											.additionalInfo(buildAdditionalInfoList(savedRecords, columnDefs)).build());
								}
							}

						} else {

							List<EserviceCommonDetails> riskRows = eserviceCommonRepo
									.findByRequestReferenceNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(req.getRequestReferenceNo(), companyId2,
											productId2, String.valueOf(sectionId), coverId);

							if (riskRows != null) {

								for (EserviceCommonDetails riskRow : riskRows) {

									List<AdditionalInformation> savedRecords = additionalInformationrepo
											.findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndRiskIdAndStatus(
													quoteNo, companyId2, productId2, String.valueOf(locationId),
													String.valueOf(sectionId), String.valueOf(coverId),
													String.valueOf(riskRow.getRiskId()), "Y");

									String description = resolveColumnValue(riskRow, template.getColumnName());

									riskList.add(RiskResponse.builder().riskId(String.valueOf(riskRow.getRiskId()))
											.description(description).sumInsured(riskRow.getSumInsured())
											.additionalInfo(buildAdditionalInfoList(savedRecords, columnDefs)).build());
								}
							}
						}

						
						BigDecimal totalSumInsured = riskList.stream().map(RiskResponse::getSumInsured)
								.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

						/*
						 * ===================================================== Add Cover Response
						 * =====================================================
						 */
						coverResponses.add(CoverResponse.builder().coverId(String.valueOf(coverId)).coverName(null)
								.sumInsured(totalSumInsured).columns(columnMetas).templateBase64(base64)
								.riskList(riskList).additionaInfo(null).build());

					} else {

						
						List<AdditionalInformation> savedRecords = additionalInformationrepo.findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndStatus(
										quoteNo, companyId2, productId2, String.valueOf(locationId),
										String.valueOf(sectionId), String.valueOf(coverId), "Y");
						BigDecimal totalSumInsured = BigDecimal.ZERO;
						BigDecimal sectionSumInsured=BigDecimal.ZERO;
						  List<EserviceBuildingDetails> buildingDetailsList =eserviceBuildRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
						  List<EserviceCommonDetails> riskRows = eserviceCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
						

						if (eserviceSectionDetailsList != null && !eserviceSectionDetailsList.isEmpty()) {

						    for (EserviceSectionDetails sectionDetail : eserviceSectionDetailsList) {

						      
						        if(buildingDetailsList!=null && !buildingDetailsList.isEmpty())
						        {
						        	sectionSumInsured = buildingDetailsList.stream()
						                .filter(e -> Objects.equals(e.getCompanyId(), sectionDetail.getCompanyId()))
						                .filter(e -> Objects.equals(e.getProductId(), sectionDetail.getProductId()))
						                .filter(e -> Objects.equals(e.getLocationId(), sectionDetail.getLocationId()))
						                .filter(e -> Objects.equals(e.getSectionId(), sectionDetail.getSectionId()))
						                .filter(e -> Objects.equals(e.getCoverId(), sectionDetail.getCoverId()))
						                .map(EserviceBuildingDetails::getSumInsured)
						                .filter(Objects::nonNull)
						                .reduce(BigDecimal.ZERO, BigDecimal::add);
						        totalSumInsured = totalSumInsured.add(sectionSumInsured);
						        }
						        if(riskRows!=null && !riskRows.isEmpty())
						        {
						        	sectionSumInsured = riskRows.stream()
						                .filter(e -> Objects.equals(e.getCompanyId(), sectionDetail.getCompanyId()))
						                .filter(e -> Objects.equals(e.getProductId(), sectionDetail.getProductId()))
						                .filter(e -> Objects.equals(e.getLocationId(), sectionDetail.getLocationId()))
						                .filter(e -> Objects.equals(e.getSectionId(), sectionDetail.getSectionId()))
						                .filter(e -> Objects.equals(e.getCoverId(), sectionDetail.getCoverId()))
						                .map(EserviceCommonDetails::getSumInsured)
						                .filter(Objects::nonNull)
						                .reduce(BigDecimal.ZERO, BigDecimal::add);
						        totalSumInsured = totalSumInsured.add(sectionSumInsured);
						        }
						     }
						}

						coverResponses.add(CoverResponse.builder().coverId(String.valueOf(coverId)).coverName(null)
								.sumInsured(totalSumInsured).columns(columnMetas).templateBase64(base64).riskList(null)
								.additionaInfo(buildAdditionalInfoList(savedRecords, columnDefs)).build());
					}
				}

				if (!coverResponses.isEmpty()) {

					sectionResponses.add(SectionResponse.builder().sectionId(String.valueOf(sectionId))
							.sectionName(sectionName).covers(coverResponses).build());
				}
			}

			if (!sectionResponses.isEmpty()) {

				locationResponses.add(LocationResponse.builder().locationId(String.valueOf(locationId))
						.sectionList(sectionResponses).build());
			}
		}
		return additionalInfoGetResponse.builder()
                .companyId(companyId2)
                .productId(productId2)
                .locationList(locationResponses)
                .nonMotEndtReq(ent)
                .endtTypeId(req.getEndtTypeId())
                .originalPolicyNo(originalPolicyNo)
                .endtReqRefNo(req.getRequestReferenceNo())
                .quoteNo(req.getQuoteNo())
                .endtPolicyNo(endtPolicyNo)
                .build();
		
   }
		return null;
        
        
	}
        
        
    
    
 // Dynamically reads field value from any entity using template's columnName config
    private String resolveColumnValue(Object row, String columnName) {
        if (row == null || columnName == null) return null;
        try {
            String fieldName = toCamelCase(columnName);
            Field field = row.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(row);
            return value != null ? String.valueOf(value) : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(
                "Column '" + columnName + "' not found in " +
                row.getClass().getSimpleName());
        }
    }

    // Handles both "OccupationTypeDesc" and "OCCUPATION_TYPE_DESC" formats
    private String toCamelCase(String input) {
        if (input == null) return null;
        if (!input.contains("_")) {
            // Already camelCase like "OccupationTypeDesc" → just lowercase first char
            return Character.toLowerCase(input.charAt(0)) + input.substring(1);
        }
        // Underscore format like "OCCUPATION_TYPE_DESC" → occupationTypeDesc
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : input.toLowerCase().toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                sb.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }

    private List<Map<String, Object>> buildAdditionalInfoList(
            List<AdditionalInformation> savedRecords,
            List<ColumnDef> columnDefs) {

        if (savedRecords == null || savedRecords.isEmpty())
            return Collections.emptyList();

        List<Map<String, Object>> result = new ArrayList<>();

        for (AdditionalInformation entity : savedRecords) {
            Map<String, Object> row = new LinkedHashMap<>();

            for (ColumnDef col : columnDefs) {
                String target = col.getTargetField() == null
                        ? "" : col.getTargetField().toUpperCase();
                if (target.isEmpty()) continue;

                row.put(target, resolveEntityField(entity, target));
            }
            row.put("MODYN", "");
            row.put("CONTENTId", entity.getContentId());
            result.add(row);
        }
        return result;
    }
    
    private Object resolveEntityField(AdditionalInformation e, String target) {
        switch (target) {
            case "VALUE":  return e.getValue();
            case "PARAM1": return e.getParam1();
            case "PARAM2": return e.getParam2();
            case "PARAM3": return e.getParam3();
            case "PARAM4": return e.getParam4();
            case "PARAM5": return e.getParam5();
            case "PARAM6": return e.getParam6();
            case "PARAM7": return e.getParam7();
            case "PARAM8": return e.getParam8();
            case "PARAM9": return e.getParam9();
            case "PARAM10":return e.getParam10();
           
            default:       return null;
        }
    }

    @Transactional
    public AdditionalInformationResponse saveOrUpdate(AdditionalInformationFlatRequest req , String token) {

        if (req.getAdditionaInfo() == null || req.getAdditionaInfo().isEmpty()) {
            return AdditionalInformationResponse.builder()
                    .response("No Data To Save")
                    .quoteNo(req.getQuoteNo())
                    .build();
        }

        if (!StringUtils.isNotBlank(req.getEndtTypeId())) {
        	
        	HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
        	
        if (req.getRiskId() != null && !req.getRiskId().isBlank()) {
            additionalInformationrepo
                .deleteByQuoteNoAndLocationIdAndSectionIdAndCoverIdAndRiskId(
                    req.getQuoteNo(), req.getLocationId(),
                    req.getSectionId(), req.getCoverId(),
                    req.getRiskId());
        } else {
            additionalInformationrepo
                .deleteByQuoteNoAndLocationIdAndSectionIdAndCoverId(
                    req.getQuoteNo(), req.getLocationId(),
                    req.getSectionId(), req.getCoverId());
        }

        TemplateEntity template = getTemplateByKeys(
                req.getCompanyId(), req.getProductId(),
                req.getSectionId(), req.getCoverId())
            .orElseThrow(() -> new RuntimeException("Template not found"));

        List<ColumnDef> columnDefs;
        try {
            columnDefs = objectMapper.readValue(
                    template.getColumnsJson(),
                    new TypeReference<List<ColumnDef>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Invalid Template JSON");
        }

        List<AdditionalInformation> entities = new ArrayList<>();

        for (Map<String, Object> row : req.getAdditionaInfo()) {

            AdditionalInformation.AdditionalInformationBuilder builder =
                    AdditionalInformation.builder()
                            .quoteNo(req.getQuoteNo())
                            .companyId(req.getCompanyId())
                            .productId(req.getProductId())
                            .locationId(req.getLocationId())
                            .sectionId(req.getSectionId())
                            .coverId(req.getCoverId())
                            .status("Y")
                            .endtStatus("B")
                            .entryDate(new Date())
                            .riskId(req.getRiskId())
            				.inceptionDate(home.getInceptionDate())
            				.expiryDate(home.getExpiryDate());
            				

            boolean hasAnyValue = false;

            for (ColumnDef col : columnDefs) {
                String target = col.getTargetField() == null
                        ? "" : col.getTargetField().toUpperCase();
                if (target.isBlank()) continue;

                Object rawValue = row.get(target);

                if (col.isRequired()
                        && (rawValue == null
                            || String.valueOf(rawValue).trim().isBlank())) {
                    throw new RuntimeException(
                        "Column '" + col.getHeaderName() + "' is required " +
                        "but no value was provided.");
                }

                if (rawValue == null) continue;
                String value = String.valueOf(rawValue).trim();
                if (value.isBlank()) continue;

                if (col.isNumericOnly()) {
                    try {
                        new BigDecimal(value); 
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException(
                            "Column '" + col.getHeaderName() +
                            "' must be numeric. Received: '" + value + "'");
                    }
                }

                hasAnyValue = true;
                applyToBuilder(builder, target, value);
            }

            if (hasAnyValue) entities.add(builder.build());
        }

        if (!entities.isEmpty()) {
            additionalInformationrepo.saveAllAndFlush(entities);
        }

        return AdditionalInformationResponse.builder()
                .response("Success")
                .quoteNo(req.getQuoteNo())
                .build();
        }
        else
        {
        	NonMotEndtReq nonMotEndtReq = req.getNonMotEndtReq();

     HomePositionMaster homelist =homeRepo.findByPolicyNo(req.getOriginalPolicyNo());
     String qu=null;
     if(homelist!=null)
     {
    	 qu= homelist.getQuoteNo();
    	 req.setQuoteNo(qu);
     }
    
     List<AdditionalInformation> entities = additionalInformationrepo.findByQuoteNo(req.getQuoteNo());

     // In-memory Map - contentId -> entity (fast lookup, no repeated stream filter)
     Map<Long, AdditionalInformation> entityMap = entities.stream()
             .filter(e -> e.getContentId() != null)
             .collect(Collectors.toMap(AdditionalInformation::getContentId, e -> e));

     // 1. EndtStatus "B" -> status "Y" | others -> delete (in-memory split)
     List<AdditionalInformation> toUpdate = new ArrayList<>();
     List<AdditionalInformation> toDelete = new ArrayList<>();
     Set<String> contentIdListOld = new HashSet<>();
     List<String> contentIdList = new ArrayList<>();
    
     List<AdditionalInformation> toSaveNewEntries = new ArrayList<>(); // batch save
     if ("895".equalsIgnoreCase(req.getEndtTypeId())) {
    	 List<AdditionalInformation> endtModifyList = entities.stream()
    		        .filter(f -> f.getEndtReqRefNo() != null
    		                && f.getEndtReqRefNo().equalsIgnoreCase(req.getEndtReqRefNo()))
    		        .collect(Collectors.toList());
//    	 entities.stream()
//         .filter(f -> f.getEndtReqRefNo() != null &&
//                      f.getEndtReqRefNo().equalsIgnoreCase(req.getEndtReqRefNo()))
//         .forEach(f -> {
//             if (f.getStatus() != null && f.getStatus().equalsIgnoreCase("N")) {
//                 f.setStatus("Y");
//                 f.setEndtReqRefNo(null);
//              //   toUpdate.add(f);
//             } else {
//             //    toDelete.add(f);
////                 if (f.getEndtAdd() != null) {
////                	 contentIdListOld.add(f.getEndtAdd());
////                	}
//             }
//         });

    	 	if (!toUpdate.isEmpty()) {
    	 		additionalInformationrepo.saveAllAndFlush(toUpdate);
    
    	 		toUpdate.forEach(e -> entityMap.put(e.getContentId(), e));
    	 	}
     for (Map<String, Object> row : req.getAdditionaInfo()) {
         String contentId  = row.get("CONTENTId") != null ? String.valueOf(row.get("CONTENTId")) : null;
         String sumInsured = row.get("VALUE")     != null ? String.valueOf(row.get("VALUE"))     : null;
         String conten="";
         contentIdList.add(contentId);
			if (endtModifyList != null && !endtModifyList.isEmpty() && contentId != null && !contentId.isBlank() && sumInsured != null && !sumInsured.isBlank()) {

				AdditionalInformation currentRecord = endtModifyList.stream().filter(e -> e != null
						&& e.getContentId() != null && String.valueOf(e.getContentId()).equals(contentId)).findFirst()
						.orElse(null);

				if (currentRecord != null && currentRecord.getEndtAdd() != null
						&& !currentRecord.getEndtAdd().isBlank()) {

					String baseContentId = currentRecord.getEndtAdd();

					try {
						Long baseId = Long.parseLong(baseContentId);

						AdditionalInformation baseRecord = entityMap.get(baseId);

						if (baseRecord != null && baseRecord.getValue() != null && !baseRecord.getValue().isBlank()) {

							BigDecimal baseSumInsured = new BigDecimal(baseRecord.getValue().trim());

							BigDecimal reqSumInsured = new BigDecimal(sumInsured.trim());

							// Compare BASE VALUE with REQUEST VALUE
							if (baseSumInsured.compareTo(reqSumInsured) == 0) {

								// Delete request/endorsement record
								additionalInformationrepo.delete(currentRecord);
								entityMap.remove(Long.parseLong(contentId));
								// Keep base record unchanged except below fields
								baseRecord.setStatus("Y");
								baseRecord.setEndtReqRefNo(null);

								// Do NOT modify baseRecord.VALUE
								additionalInformationrepo.save(baseRecord);
							}
							else
							{
								currentRecord.setValue(sumInsured);
								additionalInformationrepo.save(currentRecord);
								entityMap.remove(Long.parseLong(contentId));
							}
						}

					} catch (NumberFormatException e) {
						// Invalid ENDt_ADD / VALUE
						// Handle as required
					}
				}
			}
			
         boolean is =false;
         if ("895".equalsIgnoreCase(req.getEndtTypeId())) {
             // Map lookup - O(1), no stream filter
             AdditionalInformation existing = entityMap.get(Long.parseLong(contentId));
             if (existing != null && contentIdListOld != null) {
            	 
            	    final AdditionalInformation currentExisting = existing;

            	     conten = contentIdListOld.stream()
            	            .filter(id -> id.equals(currentExisting.getEndtAdd()))
            	            .findFirst()
            	            .orElse(null);

            	    if (conten != null) {

            	        System.out.println("Content ID: " + conten);

            	        existing = entityMap.get(Long.parseLong(conten));
            	        is=true;
            	        
            	    }
            	}

             if(!is)
             {
            	 conten=contentId;
             }
             
             if (existing != null) {
                 existing.setStatus("N");
                 existing.setEndtReqRefNo(req.getEndtReqRefNo());
                 toSaveNewEntries.add(existing); // batch save
            	 AdditionalInformation newEntry = AdditionalInformation.builder()
                         .quoteNo(existing.getQuoteNo())
                         .companyId(existing.getCompanyId())
                         .productId(existing.getProductId())
                         .locationId(existing.getLocationId())
                         .sectionId(existing.getSectionId())
                         .coverId(existing.getCoverId())
                         .riskId(existing.getRiskId())
                         .param1(existing.getParam1())
                         .param2(existing.getParam2())
                         .param3(existing.getParam3())
                         .param4(existing.getParam4())
                         .param5(existing.getParam5())
                         .param6(existing.getParam6())
                         .param7(existing.getParam7())
                         .param8(existing.getParam8())
                         .param9(existing.getParam9())
                         .param10(existing.getParam10())
                         .value(sumInsured)
                         .status("Y")
                         .entryDate(new Date())
                         .endtTypeId(req.getEndtTypeId())
                         .endtCount(nonMotEndtReq.getEndtCount().toPlainString())
                         .endtStatus("E")
                         .endtReqRefNo(req.getEndtReqRefNo())
                         .endtPrevQuote(nonMotEndtReq.getEndtPrevQuoteNo())
                         .endtAdd(conten)
                         .inceptionDate(new Date())
         				 .expiryDate(homelist.getExpiryDate())
                         .build();

                 toSaveNewEntries.add(newEntry); // batch-ல சேர்க்கு
                 entityMap.remove(Long.parseLong(contentId)); // processed, remove
                 
             }

         } 
     }
     
     TransactionSynchronizationManager.registerSynchronization(
 	        new TransactionSynchronization() {

 	            @Override
 	            public void afterCommit() {
 	                callResttemplateForNonmotor(req, token);
 	            }
 	        }
 	    );
     }else if ("896".equalsIgnoreCase(req.getEndtTypeId())){
    	 HomePositionMaster homeAdd = homelist;
    	 entities.stream()
    	 .filter(f -> f.getRiskId() != null
         && f.getRiskId().equalsIgnoreCase(req.getRiskId()))
    	 .filter(f -> f.getSectionId() != null
         && f.getSectionId().equalsIgnoreCase(req.getSectionId()))
    	 .filter(f -> f.getCoverId() != null
         && f.getCoverId().equalsIgnoreCase(req.getCoverId()))
    	 .filter(f -> f.getEndtReqRefNo() != null
         && f.getEndtReqRefNo().equalsIgnoreCase(req.getEndtReqRefNo()))
         .forEach(f -> {
             if (f.getEndtAdd() != null && f.getEndtAdd().equalsIgnoreCase("Y")) {
                 toDelete.add(f);
             }});
    	 if (!toDelete.isEmpty()) {
             additionalInformationrepo.deleteAllInBatch(toDelete); 
             toDelete.forEach(e -> entityMap.remove(e.getContentId()));
         }
    	 for (Map<String, Object> row : req.getAdditionaInfo()) {
    		 String contentId  = row.get("CONTENTId") != null ? String.valueOf(row.get("CONTENTId")) : null;
    		 
    		 if ("896".equalsIgnoreCase(req.getEndtTypeId()) && StringUtils.isBlank(contentId)) {
             TemplateEntity template = getTemplateByKeys(
                     req.getCompanyId(), req.getProductId(),
                     req.getSectionId(), req.getCoverId())
                     .orElseThrow(() -> new RuntimeException("Template not found"));

             List<ColumnDef> columnDefs;
             try {
                 columnDefs = objectMapper.readValue(template.getColumnsJson(), new TypeReference<List<ColumnDef>>() {});
             } catch (Exception e) {
                 throw new RuntimeException("Invalid Template JSON");
             }

             AdditionalInformation.AdditionalInformationBuilder builder =
                     AdditionalInformation.builder()
                             .quoteNo(homeAdd.getQuoteNo())
                             .companyId(req.getCompanyId())
                             .productId(req.getProductId())
                             .locationId(req.getLocationId())
                             .sectionId(req.getSectionId())
                             .coverId(req.getCoverId())
                             .status("Y")
                             .riskId(req.getRiskId())
                             .entryDate(new Date())
                             .endtTypeId(req.getEndtTypeId())
                             .endtCount(nonMotEndtReq.getEndtCount().toPlainString())
                             .endtStatus("E")
                             .endtReqRefNo(req.getEndtReqRefNo())
                             .endtPrevQuote(nonMotEndtReq.getEndtPrevQuoteNo())
                             .inceptionDate(homeAdd.getInceptionDate())
             				 .expiryDate(homeAdd.getExpiryDate())
                             .endtAdd("Y");

             boolean hasAnyValue = false;
             for (ColumnDef col : columnDefs) {
                 String target = col.getTargetField() == null ? "" : col.getTargetField().toUpperCase();
                 if (target.isBlank()) continue;
                 Object rawValue = row.get(target);
                 if (col.isRequired() && (rawValue == null || String.valueOf(rawValue).trim().isBlank())) {
                     throw new RuntimeException("Column '" + col.getHeaderName() + "' is required but no value was provided.");
                 }
                 if (rawValue == null) continue;
                 String value = String.valueOf(rawValue).trim();
                 if (value.isBlank()) continue;
                 if (col.isNumericOnly()) {
                     try { new BigDecimal(value); }
                     catch (NumberFormatException ex) {
                         throw new RuntimeException("Column '" + col.getHeaderName() + "' must be numeric. Received: '" + value + "'");
                     }
                 }
                 hasAnyValue = true;
                 applyToBuilder(builder, target, value);
             }
             if (hasAnyValue) {
                 toSaveNewEntries.add(builder.build());
             }
    		 }
    	 }
     }
     if (!toDelete.isEmpty()) {
         additionalInformationrepo.deleteAllInBatch(toDelete); // ← deleteAll instead of loop
         toDelete.forEach(e -> entityMap.remove(e.getContentId()));
     }
      
     if (!toSaveNewEntries.isEmpty()) {
         additionalInformationrepo.saveAllAndFlush(toSaveNewEntries);
     }
     return AdditionalInformationResponse.builder()
             .response("Success")
             .quoteNo(req.getQuoteNo())
             .build();
     }
    }
    
    private void callResttemplateForNonmotor(AdditionalInformationFlatRequest req, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            log.info("AdditionalInformationFlatRequest: {}", req );
            // Duplicate token fix - first token மட்டும் எடு
            String cleanToken = token;
            if (token != null && token.contains(",")) {
                cleanToken = token.split(",")[0].trim();
            }
            // "Bearer " prefix இல்லன்னா add பண்ணு
            if (cleanToken != null && !cleanToken.startsWith("Bearer ")) {
                cleanToken = "Bearer " + cleanToken;
            }
            
            headers.set("Authorization", cleanToken);

            HttpEntity<AdditionalInformationFlatRequest> entity = new HttpEntity<>(req, headers);

			ResponseEntity<CommonRes> response = restTemplate.exchange(nonmotorsave, HttpMethod.POST, entity,
					CommonRes.class);

            log.info("Non-motor save response: {}", response.getBody());

        } catch (Exception e) {
            log.error("Error calling non-motor save API: {}", e.getMessage());
        }
    }

    private void applyToBuilder(
            AdditionalInformation.AdditionalInformationBuilder builder,
            String target, String value) {
        	switch (target) {
            case "VALUE":  builder.value(value);  break;
            case "PARAM1": builder.param1(value); break;
            case "PARAM2": builder.param2(value); break;
            case "PARAM3": builder.param3(value); break;
            case "PARAM4": builder.param4(value); break;
            case "PARAM5": builder.param5(value); break;
            case "PARAM6": builder.param6(value); break;
            case "PARAM7": builder.param7(value); break;
            case "PARAM8": builder.param8(value); break;
            case "PARAM9": builder.param9(value); break;
            case "PARAM10":builder.param10(value);break;
        }
    }
    
    
    public List<String> validateAdditionalInformation(
            AdditionalInformationFlatRequest req) {

        List<String> error = new ArrayList<>();

        // Basic mandatory fields
        if (req.getQuoteNo() == null || req.getQuoteNo().isBlank()) {
            error.add("1755"); return error;
        }
        if (req.getLocationId() == null
                || req.getSectionId() == null
                || req.getCoverId() == null) {
            error.add("1760"); return error;
        }

        HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
        if (home == null) { error.add("1756"); return error; }

        Optional<TemplateEntity> templateOpt = getTemplateByKeys(
                req.getCompanyId(), req.getProductId(),
                req.getSectionId(), req.getCoverId());
        if (templateOpt.isEmpty()) { error.add("1763"); return error; }
       
        TemplateEntity template = templateOpt.get();
        List<ColumnDef> columnDefs;
        try {
            columnDefs = objectMapper.readValue(
                    template.getColumnsJson(),
                    new TypeReference<List<ColumnDef>>() {});
        } catch (Exception e) {
            error.add("1764"); return error; 
        }

        if (req.getAdditionaInfo() != null) {
            for (int rowIdx = 0; rowIdx < req.getAdditionaInfo().size(); rowIdx++) {
                Map<String, Object> row = req.getAdditionaInfo().get(rowIdx);

                for (ColumnDef col : columnDefs) {
                    String target = col.getTargetField() == null
                            ? "" : col.getTargetField().toUpperCase();
                    if (target.isBlank()) continue;

                    Object rawValue = row.get(target);
                    String strValue = rawValue == null
                            ? null : String.valueOf(rawValue).trim();

                    // Required check
                    if (col.isRequired()
                            && (strValue == null || strValue.isBlank())) {
                        error.add("1765"); 
                        return error;
                    }

                    // NumericOnly check
                    if (col.isNumericOnly()
                            && strValue != null && !strValue.isBlank()) {
                        try {
                            new BigDecimal(strValue);
                        } catch (NumberFormatException e) {
                            error.add("1762"); 
                            return error;
                        }
                    }
                }
            }
        }

        // Section existence check
        List<ProductSectionMaster> sectionList =
                productSectionRepo.findByCompanyIdAndProductIdAndSectionIdOrderByAmendIdDesc(
                        home.getCompanyId(), home.getProductId(),
                        Integer.valueOf(req.getSectionId()));
        if (sectionList == null || sectionList.isEmpty()) {
            error.add("1759"); return error;
        }

        ProductSectionMaster sectionDetails = sectionList.get(0);
        String motorYN = sectionDetails.getMotorYn();

        BigDecimal declaredSumInsured = BigDecimal.ZERO;
        
        if ("Y".equalsIgnoreCase(template.getRiskYn())
                && req.getRiskId() != null && !req.getRiskId().isBlank()) {

            // Fetch from whichever table the template points to, filtered by riskId
            if ("CommonDataDetails".equalsIgnoreCase(template.getTableName())) {

                CommonDataDetails riskRecord = commonDataRepo
                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverIdAndRiskId(
                        home.getQuoteNo(), home.getCompanyId(),
                        String.valueOf(home.getProductId()),
                        req.getSectionId(),
                        Integer.valueOf(req.getCoverId()),
                        Integer.valueOf(req.getRiskId()));        // <-- specific risk only

                if (riskRecord == null) { error.add("1758"); return error; }
                declaredSumInsured = riskRecord.getSumInsured() != null
                                     ? riskRecord.getSumInsured() : BigDecimal.ZERO;
                
                if ("Y".equalsIgnoreCase(template.getCountYn()) && StringUtils.isBlank(req.getEndtTypeId())) {
					try {

						int storedCount = riskRecord.getCount() != null
								? Integer.parseInt(String.valueOf(riskRecord.getCount()))
								: 0;

						int requestCount = req.getAdditionaInfo() != null ? req.getAdditionaInfo().size() : 0;

						// Count mismatch -> validation error
						if (storedCount != requestCount) {
							error.add("1770");
							return error;
						}

					} catch (Exception e) {

						// COUNT_COLUMN configured in DB but field doesn't exist
						error.add("1758");
						return error;
					}

				}
                

            } else if ("BuildingRiskDetails".equalsIgnoreCase(template.getTableName())) {

                BuildingRiskDetails riskRecord = buildingRiskRepo
                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverIdAndRiskId(
                        home.getQuoteNo(), home.getCompanyId(),
                        String.valueOf(home.getProductId()),
                        req.getSectionId(),
                        Integer.valueOf(req.getCoverId()),
                        Integer.valueOf(req.getRiskId()));

                if (riskRecord == null) { error.add("1758"); return error; }
                declaredSumInsured = riskRecord.getSumInsured() != null
                                     ? riskRecord.getSumInsured() : BigDecimal.ZERO;
                
				if ("Y".equalsIgnoreCase(template.getCountYn()) && StringUtils.isBlank(req.getEndtTypeId())) {
					try {

						int storedCount = riskRecord.getCount() != null
								? Integer.parseInt(String.valueOf(riskRecord.getCount()))
								: 0;

						int requestCount = req.getAdditionaInfo() != null ? req.getAdditionaInfo().size() : 0;

						// Count mismatch -> validation error
						if (storedCount != requestCount) {
							error.add("1770");
							return error;
						}

					} catch (Exception e) {

						// COUNT_COLUMN configured in DB but field doesn't exist
						error.add("1758");
						return error;
					}

				}
                
            }
            
        } else {
        

        if ("A".equalsIgnoreCase(motorYN)) {

            List<BuildingRiskDetails> buildingList = buildingRiskRepo
                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
                            home.getQuoteNo(), home.getCompanyId(),
                            String.valueOf(home.getProductId()),
                            req.getSectionId(), Integer.valueOf(req.getCoverId()));

            if (buildingList == null || buildingList.isEmpty()) {
                error.add("1758");
                return error;
            }

            declaredSumInsured = buildingList.stream()
                    .map(BuildingRiskDetails::getSumInsured)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        } else if ("H".equalsIgnoreCase(motorYN)) {

            List<CommonDataDetails> commonList = commonDataRepo
                    .findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
                            home.getQuoteNo(), home.getCompanyId(),
                            String.valueOf(home.getProductId()),
                            req.getSectionId(), Integer.valueOf(req.getCoverId()));

            if (commonList == null || commonList.isEmpty()) {
                error.add("1758");
                return error;
            }

            declaredSumInsured = commonList.stream()
                    .map(CommonDataDetails::getSumInsured)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        } else {
            error.add("1761"); return error;
        }
        }

        BigDecimal totalFromItems = BigDecimal.ZERO;

        if (req.getAdditionaInfo() != null) {
            for (Map<String, Object> row : req.getAdditionaInfo()) {

                Object rawValue = row.get("VALUE");
                if (rawValue == null) continue;

                try {
                    BigDecimal value = new BigDecimal(String.valueOf(rawValue).trim());

                    if (value.compareTo(BigDecimal.ZERO) < 0) {
                        error.add("1766"); 
                        return error;
                    }

                    totalFromItems = totalFromItems.add(value);

                } catch (NumberFormatException e) {
                    error.add("1762"); 
                    return error;
                }
            }
        }

       
        if (totalFromItems.compareTo(declaredSumInsured) > 0 && StringUtils.isBlank(req.getEndtTypeId())   ) {
            error.add("1757"); 
            return error;
        }

        return new ArrayList<>(new LinkedHashSet<>(error));
    }


    public AdditionalInformationFlatRequest buildFlatRequestFromExcel(
            MultipartFile file, TemplateEntity template,
            String quoteNo, String locationId,
            String companyId, String productId,
            String sectionId, String coverId,
            String riskId,HomePositionMaster home) throws Exception { 

        List<ColumnDef> cols = objectMapper.readValue(
                template.getColumnsJson(),
                new TypeReference<List<ColumnDef>>() {});

        List<Map<String, Object>> rowsList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             ReadableWorkbook wb = new ReadableWorkbook(is)) {

            Sheet sheet = wb.getFirstSheet();

            try (Stream<Row> rows = sheet.openStream()) {
                Iterator<Row> it = rows.iterator();
                if (!it.hasNext())
                    return new AdditionalInformationFlatRequest();

                Row header = it.next();
                Map<Integer, ColumnDef> indexToCol = new HashMap<>();

                for (int i = 0; i < header.getCellCount(); i++) {
                    String headerName = header.getCellAsString(i).orElse(null);
                    if (headerName == null) continue;
                    for (ColumnDef cd : cols) {
                        if (cd.getHeaderName().equalsIgnoreCase(headerName.trim())) {
                            indexToCol.put(i, cd);
                            break;
                        }
                    }
                }

                while (it.hasNext()) {
                    Row r = it.next();
                    Map<String, Object> rowMap = new LinkedHashMap<>();
                    boolean allEmpty = true;

                    for (Map.Entry<Integer, ColumnDef> entry : indexToCol.entrySet()) {
                        int idx    = entry.getKey();
                        ColumnDef cd = entry.getValue();

                        if (idx >= r.getCellCount()) continue;
                        
                        String raw = getCellValueAsString(r, idx);
                        if (raw == null || raw.isBlank()) continue;

                        allEmpty = false;
                        String apiKey = cd.getTargetField().toUpperCase();

                        
                        if (cd.isNumericOnly()) {
                            try {
                                BigDecimal bd = new BigDecimal(raw.trim());
                                // Store as plain string — saved as VARCHAR in DB
                                rowMap.put(apiKey, bd.stripTrailingZeros()
                                                     .toPlainString());
                            } catch (NumberFormatException ex) {
                                throw new RuntimeException(
                                    "Column '" + cd.getHeaderName() +
                                    "' must be numeric. Row contains: '" +
                                    raw + "'");
                            }
                        } else {
                            rowMap.put(apiKey, raw.trim());
                        }
                    }

                    if (!allEmpty) rowsList.add(rowMap);
                }
            }
        }
        if(StringUtils.isBlank(home.getEndtTypeId())) {
            AdditionalInformationFlatRequest req = new AdditionalInformationFlatRequest();
            req.setQuoteNo(quoteNo);
            req.setLocationId(locationId);
            req.setCompanyId(companyId);
            req.setProductId(productId);
            req.setSectionId(sectionId);
            req.setCoverId(coverId);
            req.setRiskId(riskId);
            req.setAdditionaInfo(rowsList);
            return req;
            }else
            {
            	HomePositionMaster homeEndt =homeRepo.findByPolicyNo(home.getOriginalPolicyNo());
            	 AdditionalInformationFlatRequest req = new AdditionalInformationFlatRequest();
                 req.setQuoteNo(homeEndt.getQuoteNo());
                 req.setLocationId(locationId);
                 req.setCompanyId(companyId);
                 req.setProductId(productId);
                 req.setSectionId(sectionId);
                 req.setCoverId(coverId);
                 req.setRiskId(riskId);
                 req.setAdditionaInfo(rowsList);
                 req.setEndtTypeId(home.getEndtTypeId());
                 req.setOriginalPolicyNo(home.getOriginalPolicyNo());
                 req.setEndtPolicyNo(home.getPolicyNo());
                 req.setEndtReqRefNo(home.getRequestReferenceNo());
                 NonMotEndtReq ent = new NonMotEndtReq();
                 List<EserviceBuildingDetails> building = eserviceBuildRepo
         				.findByRequestReferenceNo(req.getEndtReqRefNo());
         	          if(building!=null && !building.isEmpty())
         	          {
         	        	  if (building != null && !building.isEmpty()) {
                           	EserviceBuildingDetails eservice = building.get(0);
                           	ent.setEndorsementDate(eservice.getEndorsementDate());
                               ent.setEndorsementRemarks(eservice.getEndorsementRemarks());
                               ent.setEndorsementEffdate(eservice.getEndorsementEffdate());
                               ent.setOriginalPolicyNo(eservice.getOriginalPolicyNo());
                               ent.setEndtPrevPolicyNo(eservice.getEndtPrevPolicyNo());
                               ent.setEndtPrevQuoteNo(eservice.getEndtPrevQuoteNo());
                               ent.setEndtCount(eservice.getEndtCount());
                               ent.setEndtStatus(eservice.getEndtStatus());
                               ent.setIsFinaceYn(eservice.getIsFinyn());
                               ent.setEndtCategDesc(eservice.getEndtCategDesc());
                               ent.setEndorsementType(eservice.getEndorsementType());
                               ent.setEndorsementTypeDesc(eservice.getEndorsementTypeDesc());
                               ent.setPolicyNo(eservice.getPolicyNo());
                               ent.setCoverModification("Y");
//                               originalPolicyNo=eservice.getOriginalPolicyNo();
//                               endtPolicyNo=eservice.getPolicyNo();
                           }
         	        	  
         	          }else
         	          {
         	        	  List<EserviceCommonDetails> riskRows = eserviceCommonRepo.findByRequestReferenceNo(req.getEndtReqRefNo());
         	        	  if(riskRows!=null && !riskRows.isEmpty())
         		          {
         		        	  if (riskRows != null && !riskRows.isEmpty()) {
         	                  	EserviceCommonDetails eservice = riskRows.get(0);
         	                  	ent.setEndorsementDate(eservice.getEndorsementDate());
         	                      ent.setEndorsementRemarks(eservice.getEndorsementRemarks());
         	                      ent.setEndorsementEffdate(eservice.getEndorsementEffdate());
         	                      ent.setOriginalPolicyNo(eservice.getOriginalPolicyNo());
         	                      ent.setEndtPrevPolicyNo(eservice.getEndtPrevPolicyNo());
         	                      ent.setEndtPrevQuoteNo(eservice.getEndtPrevQuoteNo());
         	                      ent.setEndtCount(eservice.getEndtCount());
         	                      ent.setEndtStatus(eservice.getEndtStatus());
         	                      ent.setIsFinaceYn(eservice.getIsFinaceYn());
         	                      ent.setEndtCategDesc(eservice.getEndtCategDesc());
         	                      ent.setEndorsementType(eservice.getEndorsementType());
         	                      ent.setEndorsementTypeDesc(eservice.getEndorsementTypeDesc());
         	                      ent.setPolicyNo(eservice.getPolicyNo());
         	                      ent.setCoverModification("Y");
//         	                      originalPolicyNo=eservice.getOriginalPolicyNo();
//         	                      endtPolicyNo=eservice.getPolicyNo();
         	                  }
         		        	  
         	          }
         	          }
         	         req.setNonMotEndtReq(ent);
                 return req;
            }
        
    }

    private String getCellValueAsString(Row row, int index) {
    	 if (index >= row.getCellCount()) return null;
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        Object value = cell.getValue();
        if (value == null) return null;
        if (value instanceof BigDecimal)
            return ((BigDecimal) value).stripTrailingZeros().toPlainString();
        return String.valueOf(value).trim();
    }

    public Optional<TemplateEntity> getTemplateByKeys(
            String companyId, String productId,
            String sectionId, String coverId) {

        List<TemplateEntity> list = templateRepository.findByKeys(
                companyId, productId, sectionId, coverId);
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

	public CommonRes saveEndtDeleteAdd(EndtDeleteReq req, String token) {
		CommonRes res = new CommonRes();
		try {
			
			HomePositionMaster home =homeRepo.findByPolicyNo(req.getOriginalPolicyNo());
		 List<AdditionalInformation> entities = additionalInformationrepo.findByQuoteNo(home.getQuoteNo());
		 // In-memory Map - contentId -> entity (fast lookup, no repeated stream filter)
	     Map<Long, AdditionalInformation> entityMap = entities.stream()
	             .filter(e -> e.getContentId() != null)
	             .collect(Collectors.toMap(AdditionalInformation::getContentId, e -> e));
	     List<AdditionalInformation> toUpdate = new ArrayList<>();
	     entities.stream()
    	 .filter(f -> f.getRiskId() != null
         && f.getRiskId().equalsIgnoreCase(req.getRiskId()))
    	 .filter(f -> f.getSectionId() != null
         && f.getSectionId().equalsIgnoreCase(req.getSectionId()))
    	 .filter(f -> f.getCoverId() != null
         && f.getCoverId().equalsIgnoreCase(req.getCoverId()))
    	 .filter(f -> f.getEndtReqRefNo() != null
         && f.getEndtReqRefNo().equalsIgnoreCase(req.getEndtReqRefNo()))
         .forEach(f -> {
	                 if (f.getStatus() != null  &&  f.getStatus().equalsIgnoreCase("D") && f.getEndtAdd() != null  &&  f.getEndtAdd().equalsIgnoreCase("D") ) {
	                	 f.setStatus("Y");
	     	    	     f.setEndtTypeId(null);
	     	    	    f.setEndtStatus("P");
	     	    	    f.setEndtReqRefNo(null);
	     	    	    f.setEndtAdd(null);
	     	    	    f.setInceptionDate(home.getInceptionDate());
	   				    f.setExpiryDate(home.getExpiryDate());
	                     toUpdate.add(f);
	                 }
	                 
	             });

	     if (!toUpdate.isEmpty()) {
	         additionalInformationrepo.saveAllAndFlush(toUpdate);
	         toUpdate.forEach(e -> entityMap.put(e.getContentId(), e));
	     }
	     
	     Set<String> contentIdSet = new HashSet<>(req.getContentIds()); // O(1) lookup
	     List<AdditionalInformation> toDeactivate = entityMap.values().stream().filter(e -> contentIdSet.contains(String.valueOf(e.getContentId()))).collect(Collectors.toList());
	     toDeactivate.forEach(e -> {
	    	    e.setStatus("D");
	    	    e.setEndtTypeId("897");
	    	    e.setEndtStatus("D");
	    	    e.setEndtReqRefNo(req.getEndtReqRefNo());
	    	    e.setEndtAdd("D");
	    	    e.setInceptionDate(home.getInceptionDate());
				e.setExpiryDate(new Date());
	    	});
	     
	          if (!toDeactivate.isEmpty()) {
	             additionalInformationrepo.saveAll(toDeactivate);
	          }
	          AdditionalInformationFlatRequest req1 = new AdditionalInformationFlatRequest(); 
	          req1.setEndtReqRefNo(req.getEndtReqRefNo());
	          NonMotEndtReq ent = new NonMotEndtReq();
	          List<EserviceBuildingDetails> building = eserviceBuildRepo
				.findByRequestReferenceNo(req.getEndtReqRefNo());
	          if(building!=null && !building.isEmpty())
	          {
	        	  if (building != null && !building.isEmpty()) {
                  	EserviceBuildingDetails eservice = building.get(0);
                  	ent.setEndorsementDate(eservice.getEndorsementDate());
                      ent.setEndorsementRemarks(eservice.getEndorsementRemarks());
                      ent.setEndorsementEffdate(eservice.getEndorsementEffdate());
                      ent.setOriginalPolicyNo(eservice.getOriginalPolicyNo());
                      ent.setEndtPrevPolicyNo(eservice.getEndtPrevPolicyNo());
                      ent.setEndtPrevQuoteNo(eservice.getEndtPrevQuoteNo());
                      ent.setEndtCount(eservice.getEndtCount());
                      ent.setEndtStatus(eservice.getEndtStatus());
                      ent.setIsFinaceYn(eservice.getIsFinyn());
                      ent.setEndtCategDesc(eservice.getEndtCategDesc());
                      ent.setEndorsementType(eservice.getEndorsementType());
                      ent.setEndorsementTypeDesc(eservice.getEndorsementTypeDesc());
                      ent.setPolicyNo(eservice.getPolicyNo());
                      ent.setCoverModification("Y");
//                      originalPolicyNo=eservice.getOriginalPolicyNo();
//                      endtPolicyNo=eservice.getPolicyNo();
                  }
	        	  
	          }else
	          {
	        	  List<EserviceCommonDetails> riskRows = eserviceCommonRepo.findByRequestReferenceNo(req.getEndtReqRefNo());
	        	  if(riskRows!=null && !riskRows.isEmpty())
		          {
		        	  if (riskRows != null && !riskRows.isEmpty()) {
	                  	EserviceCommonDetails eservice = riskRows.get(0);
	                  	ent.setEndorsementDate(eservice.getEndorsementDate());
	                      ent.setEndorsementRemarks(eservice.getEndorsementRemarks());
	                      ent.setEndorsementEffdate(eservice.getEndorsementEffdate());
	                      ent.setOriginalPolicyNo(eservice.getOriginalPolicyNo());
	                      ent.setEndtPrevPolicyNo(eservice.getEndtPrevPolicyNo());
	                      ent.setEndtPrevQuoteNo(eservice.getEndtPrevQuoteNo());
	                      ent.setEndtCount(eservice.getEndtCount());
	                      ent.setEndtStatus(eservice.getEndtStatus());
	                      ent.setIsFinaceYn(eservice.getIsFinaceYn());
	                      ent.setEndtCategDesc(eservice.getEndtCategDesc());
	                      ent.setEndorsementType(eservice.getEndorsementType());
	                      ent.setEndorsementTypeDesc(eservice.getEndorsementTypeDesc());
	                      ent.setPolicyNo(eservice.getPolicyNo());
	                      ent.setCoverModification("Y");
//	                      originalPolicyNo=eservice.getOriginalPolicyNo();
//	                      endtPolicyNo=eservice.getPolicyNo();
	                  }
		        	  
	          }
	          }
	          req1.setNonMotEndtReq(ent);
	          req1.setQuoteNo(home.getQuoteNo());;
	          callResttemplateForNonmotor(req1, token);
	          res.setCommonResponse(toDeactivate);
	          res.setIsError(false);
	          res.setMessage("Sucess");
	          return res;
		}catch (Exception e) {
			log.info("Error in saveEndtDeleteAdd ==>" + e.getMessage());
			e.printStackTrace();
			 res.setCommonResponse(e.getMessage());
	          res.setIsError(true);
	          res.setMessage("error");
			 return res;
		}
	}

	
    
}

