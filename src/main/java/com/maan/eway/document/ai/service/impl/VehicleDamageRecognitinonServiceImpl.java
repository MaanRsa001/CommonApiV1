package com.maan.eway.document.ai.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CoverDocumentMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.DocumentUniqueDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SeqDocuniqueid;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.document.ai.bean.DamageDetails;
import com.maan.eway.document.ai.bean.ImageDetails;
import com.maan.eway.document.ai.bean.VehicleDetails;
import com.maan.eway.document.ai.controller.DocumentUploadReq;
import com.maan.eway.document.ai.repository.DamageDetailsRepository;
import com.maan.eway.document.ai.repository.ImageDetailsRepository;
import com.maan.eway.document.ai.repository.VehicleDetailsRepository;
import com.maan.eway.document.ai.res.DamagePartsDetailsDTO;
import com.maan.eway.document.ai.res.GetVehicleDamegeReq;
import com.maan.eway.document.ai.res.SectionDTO;
import com.maan.eway.document.ai.res.getDamageResponse;
import com.maan.eway.document.ai.service.VehicleDamageRecognitinonService;
import com.maan.eway.document.req.DocumentDeleteReq;
import com.maan.eway.document.service.impl.DocumentServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.SeqDocuniqueidRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class VehicleDamageRecognitinonServiceImpl implements VehicleDamageRecognitinonService{
	
	@Autowired
	private DamageDetailsRepository damageRepo;
	
	@Autowired
	private ImageDetailsRepository imageRepo;
	
	@Autowired
	private VehicleDetailsRepository vehicleRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo; 
	
	
	@Autowired
	private DocumentUniqueDetailsRepository docUniqueRepo;
	
	
	@Autowired
	private DocumentTransactionDetailsRepository docTranRepo;
	
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private SeqDocuniqueidRepository seqDocUniqueRepo;
	
	private Logger log = LogManager.getLogger(DocumentServiceImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Value("${external.api.key}")
    private String apiKey;


    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private DocumentServiceImpl documentservice;

	@Override
	public long generateAndReturnTransactionId() {
	     Long lastId = imageRepo.findMaxTransactionId();
	        if (lastId == null) {
	            return 1001L; // Start from 1001
	        }
	        return lastId + 1;

	    }
	

	@Override
	public Map<String, Object> generateReply(MultipartFile file) {
		 String base64 = convertImageToBase64(file);
	        String mimeType = file.getContentType();
	        return extractReplyFromBase64(base64, mimeType);
		
	}

	private Map<String, Object> extractReplyFromBase64(String base64, String fileType) {



        String prompt = """
Analyze the vehicle image and respond strictly in raw JSON with the following fields in exact order:

1. vehicle
2. brand
3. model
4. color
5. vehicle_number
6. damagedParts[] — array of objects each with:
   - damagePart
   - materialType
   -damageType
   - recommendation
   - damagePercentage — estimated percentage of damage to the part (as a number from 0 to 100)
   - repairCostUsd
   - repairMinCostInr
   - repairMaxCostInr
7. totalCostUsd
8. totalMinCost
9. totalMaxCost
10. analysis_scope — describe what part of the vehicle is shown, the severity of damage, and overall impression


 the image is not a vehicle, return only this JSON:
{ "error": "This is not a valid vehicle image" }

Return only valid JSON. No markdown, backticks, or explanation.
""";


        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt),
                                        Map.of("inlineData", Map.of(
                                                "mimeType", fileType,
                                                "data", base64
                                        ))
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        JsonNode candidates = response.getBody().path("candidates");
        if (candidates.isEmpty()) {
            throw new RuntimeException("Empty response from Gemini API");
        }

        String jsonResponse = candidates.get(0).path("content").path("parts").get(0).path("text").asText();

        // Clean unwanted markdown (e.g. ```json ... ```)
        jsonResponse = jsonResponse.replaceAll("(?s)```json|```", "").trim();

        try {
            JsonNode root = mapper.readTree(jsonResponse);
            
            if (root.has("error")) {
                return Map.of("error", root.path("error").asText());
            }


            Map<String, Object> result = new HashMap<>();
            result.put("vehicleType", root.path("vehicle").asText());
            result.put("vehicleMake", root.path("brand").asText());
            result.put("vehicleModel", root.path("model").asText());
            result.put("vehicleNumber", root.path("vehicle_number").asText());
            result.put("vehicleColor", root.path("color").asText());

            result.put("totalRepairCostUSD", root.path("totalCostUsd").asLong());
            result.put("totalRepairCostMinINR", root.path("totalMinCost").asLong());
            result.put("totalRepairCostMaxINR", root.path("totalMaxCost").asLong());

            result.put("analysis_scope", root.path("analysis_scope").asText());

            List<Map<String, Object>> damagedParts = new ArrayList<>();
            for (JsonNode part : root.path("damagedParts")) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", part.path("damagePart").asText());
                item.put("material_type", part.path("materialType").asText());
                item.put("damage_type", part.path("damageType").asText());
                item.put("damage_percentage",part.path("damagePercentage").asInt());
                item.put("recommendation", part.path("recommendation").asText());
                item.put("repair_cost_usd", part.path("repairCostUsd").asLong());
                item.put("repair_cost_inr_min", part.path("repairMinCostInr").asLong());
                item.put("repair_cost_inr_max", part.path("repairMaxCostInr").asLong());
                damagedParts.add(item);
            }

            result.put("Result", damagedParts);

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Gemini JSON response", e);
        }
	}

	private String convertImageToBase64(MultipartFile file) {
		  try {
	            return Base64.getEncoder().encodeToString(file.getBytes());
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to read image file", e);
	        }
	}
	
	

	
	
	
	

	@Override
	public void savevehicleDetails(MultipartFile file,String jsonString , Map<String, Object> response) {

		
		 DocumentUploadReq documentreq = null;
		try {
			documentreq = new ObjectMapper().readValue(jsonString, DocumentUploadReq.class);
		} catch (JsonProcessingException e) {
			System.out.println("catch while processing");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		String imagePath = saveFileAndGetPath(file);
		String quoteNo = documentreq.getQuoteNo();
		String doctId = documentreq.getDocumentId();
		String companyId = documentreq.getInsuranceId();
		String productId = documentreq.getProductId();
		String sectionId = documentreq.getSectionId();
		List<CoverDocumentMaster> docdesc = documentservice.getDocMasterDropdown(companyId,productId,sectionId);
		System.out.println("returning from getdoc");
		docdesc = docdesc.stream().filter(id -> id.getDocumentId().equals(Integer.valueOf(doctId))).toList();
		if (docdesc.isEmpty()) {
			  System.out.println("Error in docdes");
		    throw new RuntimeException("Document ID " + doctId + " not found in document master list.");
		}
		String angle = docdesc.get(0).getDocumentDesc();
        try {
            HomePositionMaster homeData = homeRepo.findByQuoteNo(documentreq.getQuoteNo());
    		CoverDocumentMaster docDetails = new CoverDocumentMaster();
    		CompanyProductMaster product =documentservice.getCompanyProductMasterDropdown(homeData.getCompanyId(),
					homeData.getProductId().toString());
			ProductSectionMaster secData =documentservice.getProductSectionDropdown(documentreq.getInsuranceId(), documentreq.getProductId(),
					documentreq.getSectionId());
			if (StringUtils.isNotBlank(documentreq.getTermsAndCondtionYn())
					&& documentreq.getTermsAndCondtionYn().equalsIgnoreCase("Y")) {
				docDetails = getByDocumentId(homeData.getCompanyId(), homeData.getProductId(), "99999",
						documentreq.getDocumentId());
			} else {
				docDetails = getByDocumentId(homeData.getCompanyId(), homeData.getProductId(), documentreq.getSectionId(),
						documentreq.getDocumentId());
			}
			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			
			//doc unique details
			
			DocumentUniqueDetails uniqDoc = new DocumentUniqueDetails();
			String uniqueId = genDocUniqueId();

			{
				uniqDoc.setUniqueId(Integer.valueOf(uniqueId));
				uniqDoc.setUploadedBy(documentreq.getUploadedBy());
				uniqDoc.setDocApplicable(docDetails.getDocApplicable());
				uniqDoc.setDocApplicableId(
						docDetails.getDocApplicableId() == null ? null : docDetails.getDocApplicableId().toString());
				uniqDoc.setFileName(file.getOriginalFilename());
				uniqDoc.setFilePathOrginal(imagePath);
				uniqDoc.setFilePathBackup(imagePath);
				uniqDoc.setOrginalFileName(file.getOriginalFilename());
				uniqDoc.setUploadedTime(new Date());
				uniqDoc.setDocumentId(Integer.valueOf(documentreq.getDocumentId()));
				uniqDoc.setDocumentType(docDetails.getDocumentType());
				uniqDoc.setDocumentTypeDesc(docDetails.getDocumentTypeDesc());
				uniqDoc.setDocumentDesc(docDetails.getDocumentDesc());
				uniqDoc.setDocumentName(docDetails.getDocumentName());
				uniqDoc.setEntryDate(new Date());
				uniqDoc.setUploadedTime(new Date());
				uniqDoc.setStatus("Y");
				uniqDoc.setId(documentreq.getId());
				uniqDoc.setIdType(documentreq.getIdType());
				uniqDoc.setProductType(secData == null ? product.getMotorYn() : secData.getMotorYn());
				uniqDoc.setVerifiedYn(StringUtils.isBlank(documentreq.getVerifiedYn())?"N":documentreq.getVerifiedYn());
				
				if ("Y".equalsIgnoreCase(documentreq.getEmiYn())) {
					uniqDoc.setEmiYn(documentreq.getEmiYn() == null ? null : documentreq.getEmiYn());
					uniqDoc.setInstallmentPeriod(
							documentreq.getInstallmentPeriod() == null ? null : documentreq.getInstallmentPeriod());
					uniqDoc.setNoOfInstallment(documentreq.getNoOfInstallment() == null ? null : documentreq.getNoOfInstallment());
				}
				
				//adding local description feilds 
				uniqDoc.setIdTypeLocal(documentreq.getIdType());
				//uniqDoc.setDocumentApplicableLocal(docDetails.getDocApplicableLocal());
				uniqDoc.setDocumentNameLocal(docDetails.getDocumentNameLocal());
				uniqDoc.setDocumentDescLocal(docDetails.getDocumentDescLocal());
				uniqDoc.setDocumentTypeDescLocal(docDetails.getDocumentTypeDescLocal());
				docUniqueRepo.saveAndFlush(uniqDoc);
			}
			
			
			//doc transaction details
			DocumentTransactionDetails docTran = new DocumentTransactionDetails();
			{
				docTran.setUniqueId(Integer.valueOf(uniqueId));
				docTran.setId(documentreq.getId());
				docTran.setIdType(documentreq.getIdType());
				docTran.setRequestReferenceNo(homeData.getRequestReferenceNo());
				docTran.setQuoteNo(documentreq.getQuoteNo());
				docTran.setCompanyId(homeData.getCompanyId());
				docTran.setCompanyName(homeData.getCompanyName());
				docTran.setProductId(homeData.getProductId());
				docTran.setProductName(homeData.getProductName());
				docTran.setSectionId(Integer.valueOf(documentreq.getSectionId()));
				docTran.setSectionName(secData == null ? "All" : secData.getSectionName());
				docTran.setProductType(secData == null ? product.getMotorYn() : secData.getMotorYn());
				docTran.setLocationId(Integer.valueOf(documentreq.getLocationId()));
				docTran.setLocationName(documentreq.getLocationName());
				docTran.setRiskId(Integer.valueOf(documentreq.getRiskId()));
				docTran.setCreatedBy(documentreq.getUploadedBy());
				
				docTran.setEntryDate(timestamp1);
				docTran.setStatus("Y");
				docTran.setEntryDate(new Date());

				if (StringUtils.isNotBlank(documentreq.getEndorsementType())) {
					EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(
							documentreq.getInsuranceId(), Integer.parseInt(documentreq.getProductId()), "Y",
							Integer.valueOf(documentreq.getEndorsementType()));
					if (entMaster != null) {
						docTran.setEndorsementDate(documentreq.getEndorsementDate() == null ? null : new Date());
						docTran.setEndorsementEffdate(
								documentreq.getEndorsementEffdate() == null ? null : documentreq.getEndorsementEffdate());
						docTran.setEndorsementRemarks(
								documentreq.getEndorsementRemarks() == null ? "" : documentreq.getEndorsementRemarks());
						docTran.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						docTran.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						docTran.setEndtCategDesc(entMaster.getEndtTypeCategory());
						docTran.setEndtStatus("P");
						docTran.setEndtCount(new BigDecimal(documentreq.getEndtCount()));
						docTran.setEndtPrevPolicyNo(documentreq.getEndtPrevPolicyNo());
						docTran.setEndtPrevQuoteNo(documentreq.getEndtPrevQuoteNo());
						
						//insert local description
						docTran.setEndorsementTypeDescLocal(entMaster.getEndtTypeCategory());
					}
				}
				if ("Y".equalsIgnoreCase(documentreq.getEmiYn())) {
					docTran.setEmiYn(documentreq.getEmiYn() == null ? null : documentreq.getEmiYn());
					docTran.setInstallmentPeriod(
							documentreq.getInstallmentPeriod() == null ? null : documentreq.getInstallmentPeriod());
					docTran.setNoOfInstallment(documentreq.getNoOfInstallment() == null ? null : documentreq.getNoOfInstallment());
				}
				//insert local description
				docTran.setProductNameLocal(homeData.getProductName());
				
				docTran.setSectionNameLocal(secData == null ? "All" : secData.getSectionName());
				
				docTranRepo.saveAndFlush(docTran);
			}
			
			System.out.println("both document details uploaded");
			
			
			//save image details
			
	       ImageDetails image = new ImageDetails();
	       List<Map<String, Object>> damages = (List<Map<String, Object>>) response.get("Result");
           image.setQuoteNo(quoteNo);
           image.setImageName(file.getOriginalFilename());
           image.setImageType(file.getContentType());
           image.setImageAngle(angle);
           image.setImagePath(imagePath);
           image.setUniqueId(Integer.valueOf(uniqueId));;
           if(damages.isEmpty()) {
        	   System.out.println("coming inside");
           	image.setDamageStatus("no damages");
           }

           imageRepo.save(image);
           
           
           System.out.println("image detail uploaded");

            // Save VehicleAnalysis
            VehicleDetails analysis = new VehicleDetails();
            analysis.setVehicleType((String) response.get("vehicleType"));
            analysis.setVehicleMake((String) response.get("vehicleMake"));
            analysis.setVehicleModel((String) response.get("vehicleModel"));
            analysis.setVehicleNumber((String) response.get("vehicleNumber"));
            analysis.setVehicleColor((String) response.get("vehicleColor"));
            analysis.setTotalCostInUsd(((Number) response.get("totalRepairCostUSD")).longValue());
            analysis.setTotalMinCosINR(((Number) response.get("totalRepairCostMinINR")).longValue());
            analysis.setTotalMaxCostINR(((Number) response.get("totalRepairCostMaxINR")).longValue());
            analysis.setAnalysisScope((String) response.get("analysis_scope"));
            analysis.setCompanyId(companyId);
            analysis.setVegicleRegNo(documentreq.getId());
            analysis.setQuoteNo(quoteNo);
            analysis.setVehicleImage(image); // FK to VehicleImage

            vehicleRepo.save(analysis);
            
            

           
            for (Map<String, Object> dmg : damages) {
                DamageDetails damage = new DamageDetails();
                damage.setDamagePart((String) dmg.get("name"));
                damage.setMaterialType((String) dmg.get("material_type"));
                damage.setDamageType((String) dmg.get("damage_type"));              
                damage.setRecommendation((String) dmg.get("recommendation"));
                damage.setDamagePercentage(((Number) dmg.get("damage_percentage")).longValue());
                damage.setRepairCostUsd(((Number) dmg.get("repair_cost_usd")).longValue());
                damage.setRepairCostMin(((Number) dmg.get("repair_cost_inr_min")).longValue());
                damage.setRepairCostMax(((Number) dmg.get("repair_cost_inr_max")).longValue());// FK to VehicleAnalysis
                damage.setVehicleDetails(analysis);
                damage.setQuoteNo(quoteNo);
                damage.setDamageSide(angle);
                damageRepo.save(damage);
            }
            
           
            System.out.println("Everything uploaded");
        }
        catch (Exception e) {
        	System.out.println("main exception");
        	e.printStackTrace();;
            throw new RuntimeException("Failed to save vehicle details", e);
        }

		
	}
	
	
	
	public CoverDocumentMaster getByDocumentId(String insId, Integer productId, String sectionId, String documentId) {
		CoverDocumentMaster res = new CoverDocumentMaster();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CoverDocumentMaster> query = cb.createQuery(CoverDocumentMaster.class);
			List<CoverDocumentMaster> list = new ArrayList<CoverDocumentMaster>();

			// Find All
			Root<CoverDocumentMaster> c = query.from(CoverDocumentMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("effectiveDateStart")));

			// Where
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n11 = cb.equal(c.get("status"), "R");
			Predicate n12 = cb.or(n1, n11);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"), insId);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), productId);
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("sectionId"), sectionId);
			jakarta.persistence.criteria.Predicate n7 = cb.equal(c.get("documentId"), documentId);
			query.where(n12, n3, n4, n5, n7).orderBy(orderList);

			// Get Result
			TypedQuery<CoverDocumentMaster> result = em.createQuery(query);
			list = result.getResultList();
			res = list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public synchronized String genDocUniqueId() {
		try {
			SeqDocuniqueid entity;
			entity = seqDocUniqueRepo.save(new SeqDocuniqueid());
			return String.format("%05d", entity.getDocUniqueId());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}

	}

	private String saveFileAndGetPath(MultipartFile file) {

        try {
            // Create upload directory if it doesn't exist
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Define file path
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); // unique name
            Path filePath = Paths.get(uploadDir + File.separator + fileName);

            // Save file
            Files.write(filePath, file.getBytes());

            // Return relative path or full path as needed
            return filePath.toString(); // or filePath.getFileName().toString();
        } catch (IOException e) {
        	System.out.println("file save exception");
            throw new RuntimeException("Failed to save file", e);
        }    
	}


	@Override
	public getDamageResponse getDamageDetails(GetVehicleDamegeReq req) {

		getDamageResponse ansDamageResponse = new getDamageResponse();

//	    System.out.println(req);

		List<DocumentTransactionDetails> docTrans = docTranRepo.findByQuoteNoAndStatus(req.getQuoteNo(), "Y");

		System.out.println(docTrans);

		List<SectionDTO> totalSectionsDtos = new ArrayList<>();

		String referenceId = null;
		Integer productIdString = null;
		String companyIdString = null;

		if (!docTrans.isEmpty()) {
			for (DocumentTransactionDetails details : docTrans) {

				referenceId = details.getRequestReferenceNo();
				productIdString = details.getProductId();
				companyIdString = details.getCompanyId();

				DocumentUniqueDetails docUniq = docUniqueRepo.findByUniqueIdAndStatus(details.getUniqueId(), "Y");

				ImageDetails imagedetails = imageRepo.getByUniqueId(details.getUniqueId());
				if (imagedetails != null) {
					VehicleDetails vehDetails = vehicleRepo.findByVehicleImageId(imagedetails.getId());
					List<DamageDetails> damageDetails = damageRepo.findByVehicleDetailsId(vehDetails.getId());

					List<DamagePartsDetailsDTO> damagePartsList = new ArrayList<>();

					for (DamageDetails dd : damageDetails) {
						DamagePartsDetailsDTO damagePartsDetailsDTO = new DamagePartsDetailsDTO();

						damagePartsDetailsDTO.setDamageId(dd.getId());
						damagePartsDetailsDTO.setUniqueId(imagedetails.getUniqueId());
						damagePartsDetailsDTO.setDocumentId(docUniq != null ? docUniq.getDocumentId() : null);
						damagePartsDetailsDTO.setQuoteNo(dd.getQuoteNo());
						damagePartsDetailsDTO.setName(dd.getDamagePart());
						damagePartsDetailsDTO.setMaterialType(dd.getMaterialType());
						damagePartsDetailsDTO.setDamageType(dd.getDamageType());
						;
						damagePartsDetailsDTO.setDamagePercentage(dd.getDamagePercentage());
						damagePartsDetailsDTO.setRecommendation(dd.getRecommendation());
						damagePartsDetailsDTO.setRepairCostUSD(dd.getRepairCostUsd());
						damagePartsDetailsDTO.setRepairCostInrMIN(dd.getRepairCostMin());
						damagePartsDetailsDTO.setRepairCostInrMAX(dd.getRepairCostMax());
						damagePartsDetailsDTO.setRemark(vehDetails != null ? vehDetails.getAnalysisScope() : null);
						damagePartsDetailsDTO.setEntry_DATE(docUniq.getEntryDate());

						damagePartsList.add(damagePartsDetailsDTO);
					}

					SectionDTO secList = new SectionDTO(); // ✅ Create new object every time
					secList.setSectionId(details.getSectionId());
					secList.setDocumentId(docUniq.getDocumentId());
					secList.setDocumentIdName(docUniq.getDocumentName());
					secList.setDocumentType(docUniq.getDocumentType());
					secList.setDocumentTypeName(docUniq.getDocumentTypeDesc());
					secList.setDocumentName(docUniq.getFileName());
					secList.setDamagePartsDetails(damagePartsList);

					totalSectionsDtos.add(secList); // ✅ Add to main list
				}
			}

			System.out.println(totalSectionsDtos);

			ansDamageResponse.setRequestReferenceNo(referenceId);
			ansDamageResponse.setCompanyId(companyIdString);
			ansDamageResponse.setProductId(productIdString);
			ansDamageResponse.setSectionList(totalSectionsDtos);
			System.out.println(ansDamageResponse);
			return ansDamageResponse;
		} else {
			ansDamageResponse.setRequestReferenceNo(null);
			ansDamageResponse.setCompanyId(companyIdString);
			ansDamageResponse.setProductId(productIdString);
			ansDamageResponse.setSectionList(null);
			System.out.println(ansDamageResponse);
			return ansDamageResponse;
		}

	}


	@Override
	public CommonRes deleteFile(DocumentDeleteReq req) {
		
		System.out.println("I am entering");
		
		System.out.println(req);
		CommonRes commonRes = new CommonRes();
		SuccessRes res = new SuccessRes();
		try {
			
			
		   
		   
		   DocumentTransactionDetails documentTransactionDetails = docTranRepo
		    .findByQuoteNoAndUniqueIdAndId(req.getQuoteNo(), Integer.valueOf(req.getUniqueId()), req.getId());
		   
		   docTranRepo.delete(documentTransactionDetails);	
		   
		   DocumentUniqueDetails documentUniqueDetails=docUniqueRepo.findByUniqueIdAndId(Integer.valueOf(req.getUniqueId()),req.getId());
		   
		   docUniqueRepo.delete(documentUniqueDetails);	
			
//			documentTransactionDetails.setStatus("N");
//			docTranRepo.saveAndFlush(documentTransactionDetails);
			
			ImageDetails imageDetails=imageRepo.getByUniqueId(Integer.valueOf(req.getUniqueId()));
			
			imageRepo.delete(imageDetails);
			
			
			
			res.setResponse("Document Deleted Sucessfully");
			res.setSuccessId(req.getUniqueId());

			commonRes.setCommonResponse(res);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(Collections.emptyList());
			commonRes.setMessage("file ");

		} catch (Exception e) {
			e.printStackTrace();
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01", "Failed", "Document Deleted Failed"));
			commonRes.setCommonResponse(res);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("File Upload Faild");
		}
		return commonRes;
	}
	
	

}