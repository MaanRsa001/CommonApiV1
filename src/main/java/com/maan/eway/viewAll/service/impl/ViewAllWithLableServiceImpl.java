package com.maan.eway.viewAll.service.impl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.AviationInfo;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EngineerInfo;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExcessTransactionDetails;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.FirstLossPayee;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MarineHullInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.res.AttachMentRes;
import com.maan.eway.jasper.res.getEmiDetailsListRes;
import com.maan.eway.master.req.LovGetReq;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.ExcessTransactionDetailsRepository;
import com.maan.eway.repository.FirstLossPayeeRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TermsAndConditionRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.service.QRCodeService;
import com.maan.eway.viewAll.dto.BrokerDtailsforOman;
import com.maan.eway.viewAll.dto.CommonCWESetRes;
import com.maan.eway.viewAll.dto.CompanyInfoDto;
import com.maan.eway.viewAll.dto.CoverClassWarrantyExclusionRes;
import com.maan.eway.viewAll.dto.DropDownReq;
import com.maan.eway.viewAll.dto.EntityColumnMetaDto;
import com.maan.eway.viewAll.dto.FieldQueryTableQueryDto;
import com.maan.eway.viewAll.dto.GetALLCoverDto;
import com.maan.eway.viewAll.dto.KeyAndValueDto;
import com.maan.eway.viewAll.dto.LocationInformationKeyValueRes;
import com.maan.eway.viewAll.dto.OmanJasperDto;
import com.maan.eway.viewAll.dto.OverAllResForView;
import com.maan.eway.viewAll.dto.PolicyDetailsForOman;
import com.maan.eway.viewAll.dto.PremiumDto;
import com.maan.eway.viewAll.dto.RiskFieldFlowDto;
import com.maan.eway.viewAll.dto.SectionDetailsKeyValueDto;
import com.maan.eway.viewAll.dto.SponsorDetailsFroOman;
import com.maan.eway.viewAll.dto.WorkerDtails;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.entity.EmployeeInfo;
import com.maan.eway.viewAll.entity.EmployeeInfoRepo;
import com.maan.eway.viewAll.entity.FieldQueryTableQuery;
import com.maan.eway.viewAll.entity.FieldQueryTableQueryRepository;
import com.maan.eway.viewAll.entity.RiskFieldFlow;
import com.maan.eway.viewAll.entity.RiskFieldFlowRepo;
import com.maan.eway.viewAll.entity.RiskInfoPdf;
import com.maan.eway.viewAll.entity.RiskInfoPdfRepo;
import com.maan.eway.viewAll.service.ViewAllWithLableService;
import com.maan.eway.workflow.dto.WorkEngine;
import com.maan.eway.workflow.service.JsonMapperFromDB;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;

@Service
public class ViewAllWithLableServiceImpl implements ViewAllWithLableService {

	@Autowired
	private HomePositionMasterRepository homerepo;

	@Autowired
	private PersonalInfoRepository piRepo;

	@Autowired
	private LoginUserInfoRepository logrepo;

	@Autowired
	private InsuranceCompanyMasterRepository incomRepo;

	@Autowired
	private BuildingRiskDetailsRepository buldinRepo;

	@Autowired
	private PolicyCoverDataRepository policyCDRepo;

	@Autowired
	private PolicyDrcrDetailRepository policydrcr;

	@Autowired
	private JsonMapperFromDB jsonMapper;

	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;

	@Autowired
	private CommonDataDetailsRepository commonRepo;

	@Autowired
	private ExcessMasterRepository excessRepo;

	@Autowired
	private RiskFieldFlowRepo riskFFRepo;

	@Autowired
	private FieldQueryTableQueryRepository fQTrepo;

	@Autowired
	private RiskInfoPdfRepo riskInfoPdfRepo;



	@Autowired
	private EmiTransactionDetailsRepository emiRepo;

	@Autowired
	private EServiceMotorDetailsRepository esMotorRepo;

	@Autowired
	private MotorDataDetailsRepository motorRepo;

	@Autowired
	private FirstLossPayeeRepository firstRepo;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ExcessTransactionDetailsRepository extransactionRepo;

	@Autowired
	private LoginProductMasterRepository productRepo;
	
	@Autowired
	private TermsAndConditionRepository termsConRepo;

	@Value("${file.directoryPath}")
	private String directaryPath;

	@Value("${libre.office.path}")
	private String libreOfficePath;

	@Value("${qr.verification.url}")
	private String qrVerificationBaseUrl;

	@Autowired
	private EmployeeInfoRepo employeRepo;

	private Logger log = LogManager.getLogger(ViewAllWithLableServiceImpl.class);

	@Autowired
	private QRCodeService qrCodeService;

	private static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };

	private static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };

	private final SecureRandom secureRandom = new SecureRandom();

	@Override
	public OverAllResForView viewAllinKeyAndValue(viewAllReq req) {
		// CommonRes res = new CommonRes();
		OverAllResForView ovRes = new OverAllResForView();
		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			HomePositionMaster home = homerepo.findByQuoteNo(req.getQuoteNo());
			List<BuildingRiskDetails> buildList = buldinRepo
					.findByQuoteNoAndStatusNotOrderByRiskIdAsc(home.getQuoteNo(), "D");
			List<CommonDataDetails> commonList = commonRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(home.getQuoteNo(),
					"D");
			List<SectionDataDetails> sectionList = sectionDataRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(home.getQuoteNo(), "D");
			if (home != null) {
				String brokerYn = "N";
				if (StringUtils.isBlank(home.getPolicyNo())) {
					brokerYn = "Y";
				}

				RiskInfoPdf pdf = riskInfoPdfRepo.findByQuoteNoAndCompanyidAndProductIdAndBrokerQuotationyn(req.getQuoteNo(), home.getCompanyId(), home.getProductId(), brokerYn);
				if (pdf != null && pdf.getPdfSt() != null && !"Y".equalsIgnoreCase(brokerYn)) {

					ObjectMapper objectMapper = new ObjectMapper();

					JsonNode rootNode = objectMapper.readTree(pdf.getPdfSt());

					JsonNode resultNode = rootNode.get("Result");

					if (resultNode != null && !resultNode.isNull()) {

						return objectMapper.treeToValue(resultNode, OverAllResForView.class);
					}
				}
				ovRes.setInceptionDate(home.getInceptionDate() != null ? displayFormat.format(home.getInceptionDate()) : "");
				ovRes.setExpiryDate(home.getExpiryDate() != null ? displayFormat.format(home.getExpiryDate()) : "");
				ovRes.setEffectiveDate(
						home.getEffectiveDate() != null ? displayFormat.format(home.getEffectiveDate()) : "");
				ovRes.setPolicyNo(home.getPolicyNo());
				ovRes.setPolicyNumn(home.getPolicyNo());
				ovRes.setQuoteNo(home.getQuoteNo());
				if ("Y".equalsIgnoreCase(brokerYn)) {
					ovRes.setProductName(home.getProductName() + " - BROKER QUOTATION");
				} else {
					ovRes.setProductName(home.getProductName() + " - POLICY SCHEDULE");
				}
				List<InsuranceCompanyMaster> comName = incomRepo.findTopByCompanyIdOrderByAmendIdDesc(home.getCompanyId());
				ovRes.setCurrencyName(home.getCurrency());

				CommonCWESetRes comCn = new CommonCWESetRes();
				LoginUserInfo login = new LoginUserInfo();
				String customerId = home.getCustomerId();
				List<PersonalInfo> pi = piRepo.findByCustomerIdAndCompanyId(customerId, home.getCompanyId());
				login = logrepo.findByLoginId(home.getLoginId());
				PersonalInfo single = pi.get(0);
				ovRes.setClientName(single.getClientName());
				List<KeyAndValueDto> piList = piMethodFforSet(pi, home);
				List<KeyAndValueDto> policyDetails = buildPolicyDetails(home, buildList, commonList, brokerYn, comName);
				List<KeyAndValueDto> brokerDetails = buildBrokerDetails(home, login);
				if ("1".equalsIgnoreCase(home.getApplicationId())) {
					ovRes.setLoginId(home.getLoginId());
					ovRes.setBrokerName(login.getUserName());
					ovRes.setCreatedBy(home.getLoginId());
					ovRes.setApprovedBy(home.getLoginId());
					if ("RA".equalsIgnoreCase(home.getAdminReferralStatus())) {
						ovRes.setApprovedBy(home.getEndtBy());
					}
				} else {
					ovRes.setLoginId(home.getApplicationId());
					ovRes.setBrokerName(home.getBdmName());
					ovRes.setCreatedBy(home.getApplicationId());
					ovRes.setApprovedBy(home.getApplicationId());
					if ("RA".equalsIgnoreCase(home.getAdminReferralStatus())) {
						ovRes.setApprovedBy(home.getEndtBy());
					}
				}
				ovRes.setBranchName(home.getBranchName());
				List<Map<String, Object>> policycrdr = totalPremeumlist(req.getQuoteNo(), home);
				List<Map<String, Object>> conditionList = new ArrayList<>();
				List<Map<String, Object>> warrantyDescription = new ArrayList<>();
				List<Map<String, Object>> exclusionList = new ArrayList<>();
				Set<String> sectionIds = new HashSet<>();
				if (sectionList != null && !sectionList.isEmpty()) {
					sectionIds.addAll(sectionList.stream().map(SectionDataDetails::getSectionId).collect(Collectors.toSet()));
				}
				
				List<AttachMentRes> attachMentList = new ArrayList<>();
				int size = sectionIds.size();
				boolean is = size > 1 ? true : false;
				String motorSec = "";
				if ("5".equalsIgnoreCase(home.getProductId().toString())) {
					List<MotorDataDetails> vehicleDetails = motorRepo
							.findByQuoteNoOrderByVehicleIdAsc(home.getQuoteNo()).stream()
							.filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());
					if (vehicleDetails != null && !vehicleDetails.isEmpty()) {
						MotorDataDetails k = vehicleDetails.get(0);
						motorSec = k.getSectionName() == null ? "" : k.getSectionName();
						List<List<KeyAndValueDto>> dynamicSetMotor = dynamicSetMotor(home, is, brokerYn,
								vehicleDetails);
						ovRes.setVehicleIds(dynamicSetMotor);
						attachMentList.addAll(getAttachMentList(home.getCompanyId(), "5", "ATTACHMENTS", "CH"));
					}
				}
				List<LocationInformationKeyValueRes> dynamicSetAssert = dynamicSetAssert(home, buildList, commonList,
						is, brokerYn,sectionList);
//				if (!"5".equalsIgnoreCase(home.getProductId().toString())) {
//					conditionList = getConditionListSectionCover(home.getPolicyNo(), home.getQuoteNo(),
//							sectionIds);
//					warrantyDescription = getWarrantyDescriptionSectionCover(home.getPolicyNo(),
//							home.getQuoteNo(), sectionIds);
//					exclusionList = getExclusionListSectionCover(home.getPolicyNo(), home.getQuoteNo(),
//							sectionIds);
//				}
//				else
//				{
//					conditionList = getConditionListSectionCoverTermsOnly(home.getPolicyNo(), home.getQuoteNo(),
//							sectionIds);
//					warrantyDescription = getWarrantyDescriptionSectionCoverTermsOnly(home.getPolicyNo(),
//							home.getQuoteNo(), sectionIds);
//					exclusionList = getExclusionListSectionCoverTermsOnly(home.getPolicyNo(), home.getQuoteNo(),
//							sectionIds);
//
//				}
				comCn.setCondition(conditionList);
				comCn.setWarranty(warrantyDescription);
				comCn.setExclusionList(exclusionList);
		
				for (String sec : sectionIds) {
						AttachMentRes attach = getBySectionId(sec, home);
						if (StringUtils.isNotBlank(attach.getDocloction())
								&& StringUtils.isNotBlank(attach.getDocRefNo())) {
							attachMentList.add(attach);
					}
				}
				if (!comName.isEmpty() && comName != null) {
					CompanyInfoDto companyInfo = companyInfo(comName);
					ovRes.setCompany(companyInfo);
					ovRes.setCountryName(companyInfo.getCompanyName());
				}
				if ("Y".equalsIgnoreCase(brokerYn) && "5".equalsIgnoreCase(home.getProductId().toString()) && Arrays
						.asList("100046", "100047", "100048", "100049", "100050").contains(home.getCompanyId())) {
					ovRes.setProductName(
							home.getProductName().toUpperCase() + " " + motorSec.toUpperCase() + " - BROKER QUOTATION");
				} else if ("5".equalsIgnoreCase(home.getProductId().toString()) && Arrays
						.asList("100046", "100047", "100048", "100049", "100050").contains(home.getCompanyId())) {
					ovRes.setProductName(home.getProductName().toUpperCase() + " " + motorSec.toUpperCase() + " - POLICY SCHEDULE");
				}

				if (Arrays.asList("100046", "100047", "100048", "100049", "100050").contains(home.getCompanyId())) {
					CompanyInfoDto companyInfo = companyInfo(comName);
					if (!comName.isEmpty() && comName != null) {
						if ("RA".equalsIgnoreCase(home.getAdminReferralStatus())) {
							List<LoginProductMaster> desc = productRepo.findByLoginIdAndCompanyIdAndProductIdOrderByAmendIdDesc(home.getEndtBy(),home.getCompanyId(), home.getProductId());
							companyInfo.setApprovedSign(desc.get(0).getSignature());
							ovRes.setApprovedBy(home.getEndtBy());
						}
						ovRes.setCompany(companyInfo);
						ovRes.setCountryName(companyInfo.getCompanyName());
					}
				}
				ovRes.setPi(piList);
				ovRes.setAttachment(attachMentList);
				ovRes.setBrokerInfo(brokerDetails);
				ovRes.setPolicyInfo(policyDetails);
				ovRes.setTotalPre(policycrdr);
				ovRes.setLocation(dynamicSetAssert);
				ovRes.setComCon(comCn);
				setpdfJson(ovRes, home, brokerYn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in viewAllinKeyAndValue ===> {}", e.getMessage());
		}
		return ovRes;
	}

//	private List<VehicleBasedInfo> dynamicSetMotor(HomePositionMaster home, boolean is, String brokerYn) {
//	    
//	    List<VehicleBasedInfo> vehicleIds = new ArrayList<>();
//	    
//	    try {
//	        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.##");
//	        
//	        List<EserviceMotorDetails> eserviceMotor = esMotorRepo.findByQuoteNoOrderByRiskIdAsc(home.getQuoteNo())
//	                .stream()
//	                .filter(f -> !f.getStatus().equalsIgnoreCase("D"))
//	                .collect(Collectors.toList());
//
//	        List<MotorDataDetails> vehicleDetails = motorRepo.findByQuoteNoOrderByVehicleIdAsc(home.getQuoteNo())
//	                .stream()
//	                .filter(f -> !f.getStatus().equalsIgnoreCase("D"))
//	                .collect(Collectors.toList());
//
//	        List<PolicyCoverData> policyDataList = policyCDRepo.findByQuoteNo(home.getQuoteNo());
//	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//
//	        // GROUP PolicyCoverData by vehicleId
//	        Map<Integer, List<PolicyCoverData>> groupedByVehicle = policyDataList.stream()
//	                .filter(p -> p.getStatus() == null || !p.getStatus().equalsIgnoreCase("D"))
//	                .collect(Collectors.groupingBy(PolicyCoverData::getVehicleId));
//
//	        vehicleDetails.forEach(k -> {
//
//	            Integer vehicleId = k.getVehicleId() == null ? null : Integer.parseInt(k.getVehicleId());
//
//	            // --- Eservice Motor ---
//	            EserviceMotorDetails eserMotor = eserviceMotor.stream()
//	                    .filter(f -> String.valueOf(f.getRiskId()).equals(k.getVehicleId()))
//	                    .findFirst()
//	                    .orElse(null);
//
//	            String tracker = "No";
//	            String ncbDesc = "No";
//	            String veDesc = "No";
//
//	            if (eserMotor != null) {
//	                tracker = "Y".equalsIgnoreCase(eserMotor.getGpsTrackingInstalled()) ? "Yes" : "No";
//
//	                LovGetReq ncbReq = new LovGetReq();
//	                ncbReq.setItemType("No_Claim_Bonus");
//	                ncbReq.setItemCode(eserMotor.getClaimType());
//	                ncbReq.setInsuranceId(eserMotor.getCompanyId());
//	                List<DropDownRes> ncbList = getByItemValue(ncbReq);
//	                if (ncbList != null && !ncbList.isEmpty()) {
//	                    ncbDesc = ncbList.get(0).getParam1();
//	                }
//
//	                LovGetReq veReq = new LovGetReq();
//	                veReq.setItemType("Voluntary discounts");
//	                veReq.setItemCode(eserMotor.getExcess());
//	                veReq.setInsuranceId(eserMotor.getCompanyId());
//	                List<DropDownRes> veList = getByItemValue(veReq);
//	                if (veList != null && !veList.isEmpty()) {
//	                    veDesc = veList.get(0).getCodeDesc();
//	                }
//	            }
//
//	            // --- Third Party Liability ---
//	            String formatted = "";
//	            if ("103".equalsIgnoreCase(k.getSectionId() == null ? "" : k.getSectionId().toString())) {
//	                formatted = String.format("%,d", 1000000L);
//	            }
//
//	            // --- Premium from PolicyCoverData for this vehicle ---
//	            List<PolicyCoverData> vehiclePolicyCover = groupedByVehicle.getOrDefault(vehicleId, new ArrayList<>());
//
//	            double premiumLc = 0d;
//	            double premiumTotal = 0d;
//
//	            if (is) {
//	                premiumLc = vehiclePolicyCover.stream()
//	                        .filter(o -> o.getDiscLoadId().equals(0)
//	                                && o.getTaxId().equals(0)
//	                                && o.getPremiumExcludedTaxLc() != null
//	                                && o.getPremiumExcludedTaxLc().doubleValue() > 0D)
//	                        .mapToDouble(o -> o.getPremiumExcludedTaxLc().doubleValue())
//	                        .sum();
//
//	                premiumTotal = vehiclePolicyCover.stream()
//	                        .filter(o -> o.getDiscLoadId().equals(0)
//	                                && o.getTaxId().equals(0)
//	                                && o.getPremiumIncludedTaxLc() != null
//	                                && o.getPremiumIncludedTaxLc().doubleValue() > 0D)
//	                        .mapToDouble(o -> o.getPremiumIncludedTaxLc().doubleValue())
//	                        .sum();
//	            }
//
//	            // --- Build Key-Value list ---
//	            List<KeyAndValueDto> keyValueList = new ArrayList<>();
//	            keyValueList.add(new KeyAndValueDto("VehicleId",                k.getVehicleId() == null ? "" : k.getVehicleId().toString()));
//	            keyValueList.add(new KeyAndValueDto("RegistrationNumber",       k.getRegistrationNumber() == null ? "" : k.getRegistrationNumber().toString()));
//	            keyValueList.add(new KeyAndValueDto("VehicleMake",              k.getVehicleMakeDesc() == null ? "" : k.getVehicleMakeDesc().toString()));
//	            keyValueList.add(new KeyAndValueDto("VehicleModel",             k.getVehcileModelDesc() == null ? "" : k.getVehcileModelDesc().toString()));
//	            keyValueList.add(new KeyAndValueDto("VehicleType",              k.getVehicleTypeDesc() == null ? "" : k.getVehicleTypeDesc().toString()));
//	            keyValueList.add(new KeyAndValueDto("CubicCapacity",            k.getCubicCapacity() == null ? "" : k.getCubicCapacity().toString()));
//	            keyValueList.add(new KeyAndValueDto("ManufactureYear",          k.getManufactureYear() == null ? "" : k.getManufactureYear().toString()));
//	            keyValueList.add(new KeyAndValueDto("SeatingCapacity",          k.getSeatingCapacity() == null ? null : (k.getSeatingCapacity() % 1 == 0 ? String.valueOf(k.getSeatingCapacity().intValue()) : String.valueOf(k.getSeatingCapacity()))));
//	            keyValueList.add(new KeyAndValueDto("Color",                    k.getColorDesc() == null ? "NA" : k.getColorDesc().toString()));
//	            keyValueList.add(new KeyAndValueDto("PolicyType",               k.getSectionName() == null ? "" : k.getSectionName()));
//	            keyValueList.add(new KeyAndValueDto("WindScreenSumInsured",     k.getWindScreenSumInsured() == null ? null : new BigDecimal(Double.parseDouble(k.getWindScreenSumInsured().toString())).toString()));
//	            keyValueList.add(new KeyAndValueDto("SumInsured",               k.getSumInsured() == null ? null : formatter.format(new BigDecimal(k.getSumInsured().toString()))));
//	          //  keyValueList.add(new KeyAndValueDto("StickerNumber",            getStrickerNo(k.getQuoteNo(), k.getVehicleId())));
//	            keyValueList.add(new KeyAndValueDto("GrossWeight",              k.getGrossWeight() == null ? null : k.getGrossWeight().toString()));
//	            keyValueList.add(new KeyAndValueDto("InsuranceType",            k.getInsuranceTypeDesc() == null ? "NA" : k.getInsuranceTypeDesc()));
//	            keyValueList.add(new KeyAndValueDto("EngineNumber",             k.getEngineNumber() == null ? "NA" : k.getEngineNumber()));
//	            keyValueList.add(new KeyAndValueDto("TPPDIncreaseLimit",        k.getTppdIncreaeLimit() == null ? null : new BigDecimal(Double.parseDouble(k.getTppdIncreaeLimit().toString())).toString()));
//	            keyValueList.add(new KeyAndValueDto("ChassisNumber",            k.getChassisNumber() == null ? "NA" : k.getChassisNumber()));
//	            keyValueList.add(new KeyAndValueDto("FuelType",                 k.getFuelTypeDesc() == null ? "NA" : k.getFuelTypeDesc()));
//	            keyValueList.add(new KeyAndValueDto("InceptionDate",            home.getInceptionDate() == null ? "" : sdf.format(home.getInceptionDate())));
//	            keyValueList.add(new KeyAndValueDto("ExpiryDate",               home.getExpiryDate() == null ? "" : sdf.format(home.getExpiryDate())));
//	            keyValueList.add(new KeyAndValueDto("ThirdPartyLiabilityLimit", formatted));
//	            keyValueList.add(new KeyAndValueDto("Tracker",                  tracker));
//	            keyValueList.add(new KeyAndValueDto("NCB",                      ncbDesc));
//	            keyValueList.add(new KeyAndValueDto("Voluntary",                veDesc));
//
//	            // --- Premium from PolicyCoverData (is = LC flag) ---
//	            if (is) {
//	                keyValueList.add(new KeyAndValueDto("Premium",              formatter.format(premiumLc)));
//	                keyValueList.add(new KeyAndValueDto("Total Premium (Including Tax)", formatter.format(premiumTotal)));
//	            } else {
//	                double premiumFc = vehiclePolicyCover.stream()
//	                        .filter(o -> o.getDiscLoadId().equals(0)
//	                                && o.getTaxId().equals(0)
//	                                && o.getPremiumExcludedTaxFc() != null
//	                                && o.getPremiumExcludedTaxFc().doubleValue() > 0D)
//	                        .mapToDouble(o -> o.getPremiumExcludedTaxFc().doubleValue())
//	                        .sum();
//
//	                double premiumTotalFc = vehiclePolicyCover.stream()
//	                        .filter(o -> o.getDiscLoadId().equals(0)
//	                                && o.getTaxId().equals(0)
//	                                && o.getPremiumIncludedTaxFc() != null
//	                                && o.getPremiumIncludedTaxFc().doubleValue() > 0D)
//	                        .mapToDouble(o -> o.getPremiumIncludedTaxFc().doubleValue())
//	                        .sum();
//
//	                keyValueList.add(new KeyAndValueDto("Premium",              formatter.format(premiumFc)));
//	                keyValueList.add(new KeyAndValueDto("Total Premium (Including Tax)", formatter.format(premiumTotalFc)));
//	            }
//
//	            VehicleBasedInfo vBasedInfo = new VehicleBasedInfo();
//	            vBasedInfo.setVehicledetails(keyValueList);
//	            vehicleIds.add(vBasedInfo);
//	        });
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        log.error("Exception in dynamicSetMotor ===> {}", e.getMessage());
//	    }
//
//	    return vehicleIds;
//	}

	private List<List<KeyAndValueDto>> dynamicSetMotor(HomePositionMaster home, boolean is, String brokerYn,
			List<MotorDataDetails> vehicleDetails) {

		List<List<KeyAndValueDto>> vehicleIds = new ArrayList<>();

		try {
			java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.##");

			List<EserviceMotorDetails> eserviceMotor = esMotorRepo.findByQuoteNoOrderByRiskIdAsc(home.getQuoteNo())
					.stream().filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());

			List<PolicyCoverData> policyDataList = policyCDRepo.findByQuoteNo(home.getQuoteNo());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			Map<Integer, List<PolicyCoverData>> groupedByVehicle = policyDataList.stream()
					.filter(p -> p.getStatus() == null || !p.getStatus().equalsIgnoreCase("D"))
					.collect(Collectors.groupingBy(PolicyCoverData::getVehicleId));

			vehicleDetails.forEach(k -> {

				Integer vehicleId = k.getVehicleId() == null ? null : Integer.parseInt(k.getVehicleId());

				EserviceMotorDetails eserMotor = eserviceMotor.stream()
						.filter(f -> String.valueOf(f.getRiskId()).equals(k.getVehicleId())).findFirst().orElse(null);

				String tracker = "No";
				String ncbDesc = "No";
				String veDesc = "No";

				if (eserMotor != null) {
					tracker = "Y".equalsIgnoreCase(eserMotor.getGpsTrackingInstalled()) ? "Yes" : "No";

					LovGetReq ncbReq = new LovGetReq();
					ncbReq.setItemType("No_Claim_Bonus");
					ncbReq.setItemCode(eserMotor.getClaimType());
					ncbReq.setInsuranceId(eserMotor.getCompanyId());
					List<DropDownRes> ncbList = getByItemValue(ncbReq);
					if (ncbList != null && !ncbList.isEmpty()) {
						ncbDesc = ncbList.get(0).getParam1();
					}

					LovGetReq veReq = new LovGetReq();
					veReq.setItemType("Voluntary discounts");
					veReq.setItemCode(eserMotor.getExcess());
					veReq.setInsuranceId(eserMotor.getCompanyId());
					List<DropDownRes> veList = getByItemValue(veReq);
					if (veList != null && !veList.isEmpty()) {
						veDesc = veList.get(0).getCodeDesc();
					}
				}

				String formatted = "";
				if ("103".equalsIgnoreCase(k.getSectionId() == null ? "" : k.getSectionId().toString())) {
					formatted = String.format("%,d", 1000000L);
				}

				List<PolicyCoverData> vehiclePolicyCover = groupedByVehicle.getOrDefault(vehicleId, new ArrayList<>());

				double premiumLc = 0d;
				double premiumTotal = 0d;

				if (is) {
					premiumLc = vehiclePolicyCover.stream()
							.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
									&& o.getPremiumExcludedTaxLc() != null
									&& o.getPremiumExcludedTaxLc().doubleValue() > 0D)
							.mapToDouble(o -> o.getPremiumExcludedTaxLc().doubleValue()).sum();

					premiumTotal = vehiclePolicyCover.stream()
							.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
									&& o.getPremiumIncludedTaxLc() != null
									&& o.getPremiumIncludedTaxLc().doubleValue() > 0D)
							.mapToDouble(o -> o.getPremiumIncludedTaxLc().doubleValue()).sum();
				}

				List<KeyAndValueDto> keyValueList = new ArrayList<>();
				keyValueList.add(new KeyAndValueDto("Vehicle Id",
						k.getVehicleId() != null ? k.getVehicleId().toString() : "NA"));
				keyValueList.add(new KeyAndValueDto("Registration Number",
						k.getRegistrationNumber() != null ? k.getRegistrationNumber() : "NA"));
				keyValueList.add(new KeyAndValueDto("Vehicle Make",
						k.getVehicleMakeDesc() != null ? k.getVehicleMakeDesc() : "NA"));
				keyValueList.add(new KeyAndValueDto("Vehicle Model",
						k.getVehcileModelDesc() != null ? k.getVehcileModelDesc() : "NA"));
				keyValueList.add(new KeyAndValueDto("Vehicle Type",k.getVehicleTypeDesc() != null ? k.getVehicleTypeDesc() : "NA"));
				keyValueList.add(new KeyAndValueDto("Cubic Capacity",k.getCubicCapacity() != null ? k.getCubicCapacity().toString() : "NA"));
				keyValueList.add(new KeyAndValueDto("Manufacture Year",k.getManufactureYear() != null ? k.getManufactureYear().toString() : "NA"));
				keyValueList.add(new KeyAndValueDto("Seating Capacity",k.getSeatingCapacity() != null? (k.getSeatingCapacity() % 1 == 0 ? String.valueOf(k.getSeatingCapacity().intValue()): String.valueOf(k.getSeatingCapacity())): "NA"));
				keyValueList.add(new KeyAndValueDto("Color", k.getColorDesc() != null ? k.getColorDesc() : "NA"));
				keyValueList.add(new KeyAndValueDto("PolicyType", k.getSectionName() != null ? k.getSectionName() : "NA"));
				keyValueList.add(new KeyAndValueDto("SumInsured",k.getSumInsured() != null ? formatter.format(new BigDecimal(k.getSumInsured().toString()))
								: "NA"));
				keyValueList.add(
						new KeyAndValueDto("Engine Number", k.getEngineNumber() != null ? k.getEngineNumber() : "NA"));
				keyValueList.add(new KeyAndValueDto("Chassis Number",
						k.getChassisNumber() != null ? k.getChassisNumber() : "NA"));
				keyValueList.add(new KeyAndValueDto("Inception Date",
						home.getInceptionDate() != null ? sdf.format(home.getInceptionDate()) : "NA"));
				keyValueList.add(new KeyAndValueDto("Expiry Date",
						home.getExpiryDate() != null ? sdf.format(home.getExpiryDate()) : "NA"));
				// keyValueList.add(new KeyAndValueDto("Third PartyLiability Limit",
				// nvl(formatted)));
				keyValueList.add(new KeyAndValueDto("Tracker", nvl(tracker)));
				keyValueList.add(new KeyAndValueDto("NCB", nvl(ncbDesc)));
				keyValueList.add(new KeyAndValueDto("Voluntary", nvl(veDesc)));

				if (is) {
					keyValueList.add(new KeyAndValueDto("Premium", formatter.format(premiumLc)));
					keyValueList
							.add(new KeyAndValueDto("Total Premium (Including Tax)", formatter.format(premiumTotal)));
				} else {
					double premiumFc = vehiclePolicyCover.stream()
							.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
									&& o.getPremiumExcludedTaxFc() != null
									&& o.getPremiumExcludedTaxFc().doubleValue() > 0D)
							.mapToDouble(o -> o.getPremiumExcludedTaxFc().doubleValue()).sum();

					double premiumTotalFc = vehiclePolicyCover.stream()
							.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
									&& o.getPremiumIncludedTaxFc() != null
									&& o.getPremiumIncludedTaxFc().doubleValue() > 0D)
							.mapToDouble(o -> o.getPremiumIncludedTaxFc().doubleValue()).sum();

					keyValueList.add(new KeyAndValueDto("Premium", formatter.format(premiumFc)));
					keyValueList.add(new KeyAndValueDto("Total Premium (Including Tax)", formatter.format(premiumTotalFc)));
				}
				if ("100046".equalsIgnoreCase(home.getCompanyId())) {
					keyValueList.add(new KeyAndValueDto("Comesa Number Of days",
							eserMotor.getClassType() != null ? eserMotor.getClassType().toString() : "NA"));
					keyValueList.add(new KeyAndValueDto("Comesa Number Of countrys",
							k.getZone() != null ? k.getZone().toString() : "NA"));
				}
				if("Y".equalsIgnoreCase(k.getCollateralYn()))
				{
					keyValueList.add(new KeyAndValueDto("Financial Interest Bank Name", k.getCollateralName() != null ? k.getCollateralName() : "NA"));
					keyValueList.add(new KeyAndValueDto("First Loss Payee Name", k.getFirstLossPayee() != null ? k.getFirstLossPayee() : "NA"));
				}
				vehicleIds.add(keyValueList);
			});

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in dynamicSetMotor ===> {}", e.getMessage());
		}

		return vehicleIds;
	}

	public List<DropDownRes> getByItemValue(LovGetReq req) {

		List<DropDownRes> resList = new ArrayList<DropDownRes>();

		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
//			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			if (StringUtils.isNotBlank(req.getParam1())) {
				Predicate a5 = cb.equal(b.get("param1"), ocpm1.get("param1"));
				amendId.where(a1, a3, a4, a5);
			} else {
				amendId.where(a1, a3, a4);
			}
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(b.get("branchCode"),
					StringUtils.isBlank(req.getBranchCode()) ? "99999" : req.getBranchCode());
			Predicate n8 = cb.equal(cb.upper(b.get("itemType")), cb.upper(cb.literal(req.getItemType())));
			Predicate n10 = cb.equal(b.get("itemCode"), req.getItemCode());
			/*
			 * if(!StringUtils.isBlank(req.getTitletype())) { Predicate
			 * n9=cb.equal(b.get("param1"),req.getTitletype());
			 * query.where(n1,n2,n4,n8,n9).orderBy(orderList); }
			 */
			Predicate statusPredicate = cb.equal(b.get("status"), "Y");

			if (StringUtils.isNotBlank(req.getParam1())) {
				Predicate n9 = cb.equal(b.get("param1"), req.getParam1());
				query.where(n1, n2, n4, n8, n9, n10, statusPredicate).orderBy(orderList);
			} else {
				query.where(n1, n2, n4, n8, n10, statusPredicate).orderBy(orderList);
			}

			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemId()))).collect(Collectors.toList());
			if (StringUtils.isNotBlank(req.getItemType()) && ("BOND_YEAR".equalsIgnoreCase(req.getItemType())
					|| "BURGLARY_FIRST_LOSS".equalsIgnoreCase(req.getItemType())
					|| "FIDELITY_SI".equalsIgnoreCase(req.getItemType()))) {
				list = list.stream()
						.sorted((o1, o2) -> Long.valueOf(o1.getItemCode()).compareTo(Long.valueOf(o2.getItemCode())))
						.collect(Collectors.toList());
			} else {
				list.sort(Comparator.comparing(ListItemValue::getItemValue));
			}
			// Map
			if (!StringUtils.isBlank(req.getTitletype())) {

				list = list.stream().filter(item -> req.getTitletype().equals(item.getParam1()))
						.collect(Collectors.toList());
			}

			for (ListItemValue data : list) {
				DropDownRes res = new DropDownRes();
				res.setTitletype(data.getParam1());
				res.setCode(data.getItemCode().toString());
				res.setCodeDesc(data.getItemValue().toString());
				res.setCodeDescLocal(data.getItemValueLocal());
				res.setStatus(data.getStatus() == null ? "" : data.getStatus().toString());
				res.setParam1(data.getParam1());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}

	private List<getEmiDetailsListRes> getEMIdetails(List<EmiTransactionDetails> emiList) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			DecimalFormat df = new DecimalFormat("#,##0.00");

			return emiList.stream().map(emi -> {
				getEmiDetailsListRes res = new getEmiDetailsListRes();

				res.setInstallmentId(emi.getInstalment());
				res.setDueDate(emi.getDueDate() != null ? sdf.format(emi.getDueDate()) : "");
				res.setDueAmount(emi.getDueAmount() != null ? df.format(emi.getDueAmount()) : "0.00");
				res.setInstallmentIdDesc(emi.getInstallmentTypeDesc() != null ? emi.getInstallmentTypeDesc() : "");
				return res;
			}).collect(Collectors.toList());
		} catch (Exception e) {
			log.info("Error in getEMIdetails ==>" + e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private AttachMentRes getBySectionId(String sec, HomePositionMaster home) {
		AttachMentRes res = new AttachMentRes();

		try {

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);
			List<ProductSectionMaster> list = new ArrayList<ProductSectionMaster>();

			// Find All
			Root<ProductSectionMaster> c = query.from(ProductSectionMaster.class);

			// Select
			query.select(c);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductSectionMaster> ocpm1 = amendId.from(ProductSectionMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			jakarta.persistence.criteria.Predicate a2 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			amendId.where(a1, a2, a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("effectiveDateStart")));

			// Where

			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("amendId"), amendId);
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("sectionId"), sec);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("productId"), home.getProductId());
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"), home.getCompanyId());
			query.where(n1, n2, n3, n4).orderBy(orderList);

			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getSectionId())))
					.collect(Collectors.toList());
			res.setDocloction(
					StringUtils.isBlank(list.get(0).getFilePathOrginal()) ? "" : list.get(0).getFilePathOrginal());
			res.setDocRefNo(StringUtils.isBlank(list.get(0).getFileName()) ? "" : list.get(0).getFileName());
//			res.setOriginalFileName(
//					StringUtils.isBlank(list.get(0).getOrginalFileName()) ? "" : list.get(0).getOrginalFileName());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	private void setpdfJson(OverAllResForView ovRes, HomePositionMaster home, String brokerYn) {
		try {
			RiskInfoPdf pdfsave = new RiskInfoPdf();
			CommonRes res = new CommonRes();
			res.setCommonResponse(ovRes);
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString = objectMapper.writeValueAsString(res);
			pdfsave.setCompanyid(home.getCompanyId());
			pdfsave.setPdfSt(jsonString);
			pdfsave.setQuoteNo(home.getQuoteNo());
			pdfsave.setProductId(home.getProductId());
			pdfsave.setProductDesc(home.getProductName());
			pdfsave.setBrokerQuotationyn(brokerYn);
			pdfsave.setEntryDate(new Date());
			riskInfoPdfRepo.saveAndFlush(pdfsave);
		} catch (Exception e) {
			log.error("Exception in setpdfJson ===> {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private List<KeyAndValueDto> buildPolicyDetails(HomePositionMaster home, List<BuildingRiskDetails> buildList,
			List<CommonDataDetails> commonList, String brokerYn, List<InsuranceCompanyMaster> comName) {

		List<KeyAndValueDto> list = new ArrayList<>();

		try {
			InsuranceCompanyMaster insurance = comName.get(0);
			if (home == null) {
				return list;
			}

			String installmentTypeDesc = "";
			if ("Y".equalsIgnoreCase(home.getEmiYn())) {
				List<EmiTransactionDetails> emi = emiRepo.findByQuoteNo(home.getQuoteNo());
				installmentTypeDesc = emi.get(0).getInstallmentTypeDesc();
			} else {
				installmentTypeDesc = "Yearly";
			}

			SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			if (StringUtils.isNotBlank(home.getPolicyNo())) {
				list.add(new KeyAndValueDto("Policy Number", nvl(home.getPolicyNo())));
				list.add(new KeyAndValueDto("Debit Note", nvl(home.getDebitNoteNo())));
				list.add(new KeyAndValueDto("Credit Note", nvl(home.getCreditNo())));
				list.add(new KeyAndValueDto("Quote Number", nvl(home.getQuoteNo())));
				list.add(new KeyAndValueDto("Inception Date",
						home.getInceptionDate() != null ? displayFormat.format(home.getInceptionDate()) : "NA"));
				list.add(new KeyAndValueDto("Expiry Date",
						home.getExpiryDate() != null ? displayFormat.format(home.getExpiryDate()) : "NA"));
				list.add(new KeyAndValueDto("Renewal Date",
						home.getExpiryDate() != null
								? displayFormat.format(
										new java.util.Date(home.getExpiryDate().getTime() + (24 * 60 * 60 * 1000)))
								: "NA"));

				if (!"100002".equalsIgnoreCase(home.getCompanyId())) {
					list.add(new KeyAndValueDto("Payment Frequency", nvl(installmentTypeDesc)));
					list.add(new KeyAndValueDto("Class of Insurance", nvl(home.getProductName())));
					list.add(new KeyAndValueDto("Territorial Limits", nvl(insurance.getTerritorialLimit())));
					list.add(new KeyAndValueDto("Court of Jurisdiction", nvl(insurance.getCourtOfjurisDiction())));
				} else {
					list.add(new KeyAndValueDto("Product Name", nvl(home.getProductName())));
					list.add(new KeyAndValueDto("Currency", nvl(home.getCurrency())));
					list.add(new KeyAndValueDto("Prepared By", nvl(home.getLoginId())));
				}
			} else {
				list.add(new KeyAndValueDto("Quote Number", nvl(home.getQuoteNo())));
				list.add(new KeyAndValueDto("Inception Date",
						home.getInceptionDate() != null ? displayFormat.format(home.getInceptionDate()) : "NA"));
				list.add(new KeyAndValueDto("Expiry Date",
						home.getExpiryDate() != null ? displayFormat.format(home.getExpiryDate()) : "NA"));
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, 15);
				list.add(new KeyAndValueDto("Quotation Validate", displayFormat.format(calendar.getTime())));
				list.add(new KeyAndValueDto("Product Name", nvl(home.getProductName())));
				list.add(new KeyAndValueDto("Currency", nvl(home.getCurrency())));
				list.add(new KeyAndValueDto("Prepared By", nvl(home.getLoginId())));
			}

			// Industry Name
			String indus = null;
			Set<String> collect = new HashSet<>();

			if (buildList != null && !buildList.isEmpty()) {
				collect = buildList.stream().map(BuildingRiskDetails::getIndustryDesc).collect(Collectors.toSet());
			} else if (commonList != null && !commonList.isEmpty()) {
				collect = commonList.stream().map(CommonDataDetails::getIndustryName).collect(Collectors.toSet());
			}

			if (!collect.isEmpty()) {
				indus = collect.iterator().next();
			}

			list.add(new KeyAndValueDto("Industry Name", nvl(indus)));

		} catch (Exception e) {
			log.error("Exception in buildPolicyDetails ===> {}", e.getMessage());
			e.printStackTrace();
		}

		return list;
	}

	// Helper method - same class-ல add பண்ணு
	private String nvl(String value) {
		return StringUtils.isNotBlank(value) ? value : "NA";
	}

	private List<Map<String, Object>> totalPremeumlist(String quoteNo, HomePositionMaster home) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		try {
			java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.00");
			Map<String, Object> inwords = new HashMap<>();
			Map<String, Object> totalPre = new HashMap<>();
			List<PolicyDrcrDetail> policyDrcr = policydrcr.findByQuoteNo(quoteNo);
			if (policyDrcr != null && !policyDrcr.isEmpty()) {
				double sum = policyDrcr.stream().filter(d -> "DR".equalsIgnoreCase(d.getDrcrFlag()))
						.mapToDouble(o -> o.getAmountFc().doubleValue()).sum();
				String sumLcFormat = formatter.format(sum);
				String premiumInWords = convertAmountToWords(new BigDecimal(sum));
				totalPre.put("Key", "Total Premium (Including Tax)");
				totalPre.put("Value", sumLcFormat);
				inwords.put("Key", "In Words :" + premiumInWords);
				inwords.put("Value", "");
				for (PolicyDrcrDetail drcr : policyDrcr) {
					if ("DR".equalsIgnoreCase(drcr.getDrcrFlag())
				            && drcr.getAmountLc() != null
				            && drcr.getAmountLc().compareTo(BigDecimal.ZERO) > 0) {
						Map<String, Object> map = new HashMap<>();
						map.put("Value", formatter.format(drcr.getAmountLc()));
						map.put("Key", drcr.getChargeAccountDesc());
						mapList.add(map);
					}
				}
				mapList.add(totalPre);
				mapList.add(inwords);
				if ("Y".equalsIgnoreCase(home.getEmiYn())) {
					List<EmiTransactionDetails> emiList = emiRepo.findByQuoteNoAndCompanyIdAndProductId(
							home.getQuoteNo(), home.getCompanyId(), home.getProductId().toString());
					if (!emiList.isEmpty() && emiList != null) {
						List<getEmiDetailsListRes> emiDetails = getEMIdetails(emiList);
						emiDetails.sort(Comparator.comparingInt(e -> Integer.parseInt(e.getInstallmentId())));
						Map<String, Object> map = new HashMap<>();
						map.put("Value", emiDetails.get(0).getDueAmount());
						map.put("Key", "1st Month Premium");
						mapList.add(map);
						Map<String, Object> map1 = new HashMap<>();
						map1.put("Value", emiDetails.get(1).getDueAmount());
						map1.put("Key", emiDetails.get(1).getInstallmentIdDesc());
						mapList.add(map1);
					}
				}
			} else {
				List<PolicyCoverData> coverData = policyCDRepo.findByQuoteNo(quoteNo);
				if ("100019".equalsIgnoreCase(home.getCompanyId())) {
					if (coverData != null && !coverData.isEmpty()) {
						BigDecimal totalPremiumwithoutax = coverData.stream()
								.filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
								.filter(c -> "B".equalsIgnoreCase(c.getCoverageType())
										|| "O".equalsIgnoreCase(c.getCoverageType()))
								.map(c -> c.getPremiumExcludedTaxFc() == null ? BigDecimal.ZERO
										: c.getPremiumExcludedTaxFc())
								.reduce(BigDecimal.ZERO, BigDecimal::add);
						Map<Integer, List<PolicyCoverData>> groupedTaxes = coverData.stream()
								.filter(c -> c.getSectionId() != null && c.getSectionId().equals(99999) )
								.filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
								.filter(c -> c.getTaxId() != null && c.getTaxId() != 0).collect(Collectors
										.groupingBy(PolicyCoverData::getTaxId, LinkedHashMap::new, Collectors.toList()));
						BigDecimal totalPremium = coverData.stream()
						        .filter(o -> o.getDiscLoadId().equals(0)
						                && o.getTaxId().equals(0)
						                && o.getPremiumIncludedTaxLc() != null
						                && o.getPremiumIncludedTaxLc().compareTo(BigDecimal.ZERO) > 0
						                && !"D".equalsIgnoreCase(o.getStatus())
						                && o.getSectionId().equals(99999))
						        .map(o -> o.getPremiumIncludedTaxLc())
						        .reduce(BigDecimal.ZERO, BigDecimal::add);
						Map<String, Object> premium = new LinkedHashMap<>();
						premium.put("Key", "Premium");
						premium.put("Value", formatter.format(totalPremiumwithoutax));
						mapList.add(premium);
						for (Map.Entry<Integer, List<PolicyCoverData>> entry : groupedTaxes.entrySet()) {

							List<PolicyCoverData> taxes = entry.getValue();

							BigDecimal taxAmount = taxes.stream()
									.map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
									.reduce(BigDecimal.ZERO, BigDecimal::add);

							PolicyCoverData firstTax = taxes.get(0);

							String key = firstTax.getTaxDesc();

							if ("Y".equalsIgnoreCase(firstTax.getTaxCalcType())) {
								key += " " + firstTax.getTaxRate().stripTrailingZeros().toPlainString() + "%";
							}

							
							if (taxAmount != null && taxAmount.compareTo(BigDecimal.ZERO) > 0) {
								Map<String, Object> taxMap = new LinkedHashMap<>();
								taxMap.put("Key", key);
								taxMap.put("Value", formatter.format(taxAmount));
								mapList.add(taxMap);
							}
							
						}
						Map<String, Object> totalPremiumMap = new LinkedHashMap<>();
						totalPremiumMap.put("Key", "Total Premium (Including Tax)");
						totalPremiumMap.put("Value", formatter.format(totalPremium));
						mapList.add(totalPremiumMap);
						Map<String, Object> inWords = new LinkedHashMap<>();
						inWords.put("Key", "In Words : " + convertAmountToWords(totalPremium));
						inWords.put("Value", "");
						mapList.add(inWords);
					}
					
				}else {
				if (coverData != null && !coverData.isEmpty()) {
					BigDecimal totalPremiumwithoutax = coverData.stream()
							.filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
							.filter(c -> "B".equalsIgnoreCase(c.getCoverageType())
									|| "O".equalsIgnoreCase(c.getCoverageType()))
							.map(c -> c.getPremiumExcludedTaxFc() == null ? BigDecimal.ZERO
									: c.getPremiumExcludedTaxFc())
							.reduce(BigDecimal.ZERO, BigDecimal::add);

					BigDecimal totalPremium = coverData.stream()
							.filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
							.filter(c -> "B".equalsIgnoreCase(c.getCoverageType())
									|| "O".equalsIgnoreCase(c.getCoverageType()))
							.map(c -> c.getPremiumIncludedTaxFc() == null ? BigDecimal.ZERO
									: c.getPremiumIncludedTaxFc())
							.reduce(BigDecimal.ZERO, BigDecimal::add);

					Map<String, Object> premium = new LinkedHashMap<>();
					premium.put("Key", "Premium");
					premium.put("Value", formatter.format(totalPremiumwithoutax));
					mapList.add(premium);
					Map<Integer, List<PolicyCoverData>> groupedTaxes = coverData.stream()
							.filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
							.filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
							.filter(c -> c.getTaxId() != null && c.getTaxId() != 0).collect(Collectors
									.groupingBy(PolicyCoverData::getTaxId, LinkedHashMap::new, Collectors.toList()));

					for (Map.Entry<Integer, List<PolicyCoverData>> entry : groupedTaxes.entrySet()) {

						List<PolicyCoverData> taxes = entry.getValue();

						BigDecimal taxAmount = taxes.stream()
								.map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
								.reduce(BigDecimal.ZERO, BigDecimal::add);

						PolicyCoverData firstTax = taxes.get(0);

						String key = firstTax.getTaxDesc();

						if (firstTax.getTaxRate() != null) {
							key += " " + firstTax.getTaxRate().stripTrailingZeros().toPlainString() + "%";
						}

						Map<String, Object> taxMap = new LinkedHashMap<>();
						taxMap.put("Key", key);
						taxMap.put("Value", formatter.format(taxAmount));

						mapList.add(taxMap);
					}
					Map<String, Object> totalPremiumMap = new LinkedHashMap<>();
					totalPremiumMap.put("Key", "Total Premium (Including Tax)");
					totalPremiumMap.put("Value", formatter.format(totalPremium));
					mapList.add(totalPremiumMap);

					Map<String, Object> inWords = new LinkedHashMap<>();
					inWords.put("Key", "In Words : " + convertAmountToWords(totalPremium));
					inWords.put("Value", "");
					mapList.add(inWords);
				}
			}
			}
		} catch (Exception e) {
			log.error("Exception in buildPolicyDetails ===> {}", e.getMessage());
		}
		return mapList;
	}

	private CompanyInfoDto companyInfo(List<InsuranceCompanyMaster> comName) {
		CompanyInfoDto com = new CompanyInfoDto();
		try {
			InsuranceCompanyMaster company = comName.get(0);
			com.setCompanyName(company.getCompanyName());
			com.setCompanyId(company.getCompanyId());
			com.setCompanyWebSite(company.getCompanyWebsite());
			com.setCompanyEmail(company.getCompanyEmail());
			com.setCompanyPhone(company.getCompanyPhone());
			com.setCompanyAddress(company.getCompanyAddress());
			com.setSignature(StringUtils.isNotBlank(company.getSignature()) ? company.getSignature() : "");
			com.setTinNumber(company.getTinNumber());
			com.setFooterImage(company.getFooterImage());
			com.setCompanyLogo(company.getCompanyLogo());
			com.setFooterDescription(company.getFooterDescription());
		} catch (Exception e) {
			log.error("Exception in companyInfo ===> {}", e.getMessage());
		}
		return com;
	}

//	private List<LocationInformationKeyValueRes> dynamicSetAssert(HomePositionMaster home, List<BuildingRiskDetails> buildList,
//			List<CommonDataDetails> commonList,boolean is,String brokeryn) {
//		List<LocationInformationKeyValueRes> locationList = new ArrayList<>();
//		try {
//			    
//			   java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.00");
//			    List<SectionDataDetails> sectionList = sectionDataRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(home.getQuoteNo(), "D");
//			    List<PolicyCoverData> policyDataList = policyCDRepo.findByQuoteNo(home.getQuoteNo());
//				if (sectionList != null && !sectionList.isEmpty()) {
//				
//					Map<Integer, Map<String, Map<Integer, Set<Integer>>>> groupedData = sectionList.stream()
//							.collect(Collectors.groupingBy(SectionDataDetails::getLocationId,Collectors.groupingBy(SectionDataDetails::getSectionId, Collectors.groupingBy(
//											SectionDataDetails::getCoverId,Collectors.mapping(SectionDataDetails::getRiskId, Collectors.toSet())))));
//			    groupedData.forEach((locId, sectionMap) -> {
//				// LOCATION LEVEL
//			    
//			    LocationInformationKeyValueRes locaRes = new LocationInformationKeyValueRes();
//				List<SectionDataDetails> locationData = sectionList.stream().filter(l -> l.getLocationId().equals(locId)).collect(Collectors.toList());
//				List<BuildingRiskDetails> collect = buildList.stream().filter(l -> l.getLocationId().equals(locId)).collect(Collectors.toList());
//				List<CommonDataDetails> collect2 = commonList.stream().filter(l -> l.getLocationId().equals(locId)).collect(Collectors.toList());
//				String address = null;
//
//				if (!collect.isEmpty()) {
//				    address = collect.get(0).getAddress();
//				} else if (!collect2.isEmpty()) {
//				    address = collect2.get(0).getAddress();
//				}
//				if (locationData.isEmpty()) {
//					return;
//				}
//				List<SectionDetailsKeyValueDto> secResList = new  ArrayList<>();
//				List<KeyAndValueDto> keVLocaList = new ArrayList<>();
//				keVLocaList.add(new KeyAndValueDto("Location Name", locationData.get(0).getLocationName()));
//				keVLocaList.add(new KeyAndValueDto("Address", address));
//				locaRes.setLocationName(keVLocaList);
//				// SECTION LEVEL
//				sectionMap.forEach((sec, coverMap) -> {
//					Double premiumLc=0d;
//					Double premiumTotal=0d;
//					
//					List<CoverClassWarrantyExclusionRes> cwwList= new ArrayList<>();
//					SectionDetailsKeyValueDto secRes = new SectionDetailsKeyValueDto();
//					productWishAddInfo(home,secRes,sec,locId,buildList);
//					setOrderBySection(secRes,sec,home);
//					secRes.setSectionId(sec);
//					List<SectionDataDetails> seclist = sectionList.stream().filter(l -> l.getSectionId().equals(sec) && l.getLocationId().equals(locId)).collect(Collectors.toList());
//					List<KeyAndValueDto> sectionPre = new ArrayList<>();
//					if(is) {
//					premiumLc = policyDataList.stream()
//						    .filter(o ->
//						        o.getDiscLoadId().equals(0)
//						            && o.getTaxId().equals(0)
//						            && o.getPremiumExcludedTaxLc() != null
//						            && o.getPremiumExcludedTaxLc().doubleValue() > 0D
//						            && !o.getStatus().equalsIgnoreCase("D")
//						            && o.getSectionId().equals(Integer.valueOf(sec))
//						            && o.getLocationId().equals(locId)
//						    )
//						    .mapToDouble(o -> o.getPremiumExcludedTaxLc().doubleValue())
//						    .sum();	
//					premiumTotal = policyDataList.stream()
//						    .filter(o ->
//						        o.getDiscLoadId().equals(0)
//						            && o.getTaxId().equals(0)
//						            && o.getPremiumIncludedTaxLc() != null
//						            && o.getPremiumIncludedTaxLc().doubleValue() > 0D
//						            && !o.getStatus().equalsIgnoreCase("D")
//						            && o.getSectionId().equals(Integer.valueOf(sec))
//						            && o.getLocationId().equals(locId)
//						    )
//						    .mapToDouble(o -> o.getPremiumIncludedTaxLc().doubleValue())
//						    .sum();	
//					String premiumTotalFormat = formatter.format(premiumTotal);
//					String premiumLcFormat = formatter.format(premiumLc);
//					sectionPre.add(new KeyAndValueDto("Premium", premiumLcFormat));
//					sectionPre.add(new KeyAndValueDto("Total Premium (Including Tax)", premiumTotalFormat));
//					}
//					sectionPre.add(new KeyAndValueDto("Section Name", seclist.get(0).getSectionDesc()));
//					if("100002".equalsIgnoreCase(home.getCompanyId()) && "N".equalsIgnoreCase(brokeryn))
//					{
//					sectionPre.add(new KeyAndValueDto("Tira CoverNote Number", seclist.get(0).getCoverNoteReferenceNo()));
//					}
//					secRes.setPremium(sectionPre);
//					coverMap.forEach((coverId, riskSet) -> {
//						
//						riskSet.forEach(risk -> {
//							List<Map<String, Object>> conditionListCover = new ArrayList<>();
//							List<Map<String, Object>> warrantyDescriptionCover = new ArrayList<>();
//							List<Map<String, Object>> exclusionListCover =new ArrayList<>();
//							List<ExcessMaster> excessList =new ArrayList<>();
//							CoverClassWarrantyExclusionRes cwe = new CoverClassWarrantyExclusionRes();
//							List<Map<String, Object>> excessdetails= new ArrayList<>();
//							cwe.setCoverId(coverId==null?null:coverId.toString());
//							cwe.setRiskId(risk==null?null:risk.toString());
//							if(!"5".equalsIgnoreCase(home.getProductId().toString())) {
//							 conditionListCover = jasperCus.getConditionListCover(home.getPolicyNo(),home.getQuoteNo(),sec,coverId.toString(),locId);
//							 warrantyDescriptionCover = jasperCus.getWarrantyDescriptionCover(home.getPolicyNo(),home.getQuoteNo(),sec,coverId.toString(),locId);
//							 exclusionListCover = jasperCus.getExclusionListCover(home.getPolicyNo(),home.getQuoteNo(),sec,coverId.toString(),locId);
//							 excessList = excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(home.getCompanyId(),home.getProductId().toString(),sec.toString(),coverId.toString());
//							}
//							if(excessList!=null && excessList.size()>0) {
//								excessList.forEach(e_l -> {
//									Map<String,Object> e = new HashMap<String,Object>();
//									e.put("excessName", e_l.getCoverName()==null?"":e_l.getCoverName());
//									e.put("ExcessDescription", e_l.getExcessDescription()==null?"":e_l.getExcessDescription());
//									e.put("ExcessPercentage", e_l.getExcessPercentage()==null?0:e_l.getExcessPercentage());
//									e.put("ExcessAmount", e_l.getExcessAmount()==null?0.00:e_l.getExcessAmount());
//									excessdetails.add(e);
//								});
//								
//							}
//							cwe.setCondition(conditionListCover);
//							cwe.setExclusionList(exclusionListCover);
//							cwe.setWarranty(warrantyDescriptionCover);
//							cwe.setExcess(excessdetails);
//							log.info("Enter in Dynamic Cover ===> {}");
//							List<Map<String, Object>> dyresponse = CoverTojson(home, sec, coverId, risk, locId );
//							if(dyresponse!=null && !dyresponse.isEmpty()) {
//							Map<String, Object> cover = dyresponse.get(0);
//
//							int count = 0;
//							for (int i = 1; i <= 20; i++) {
//							    if (cover.containsKey("Key" + i)) {
//							        count++;
//							    }
//							}
//							cwe.setColumnCount(count+"");
//						}else
//						{
//							cwe.setColumnCount("");
//						}
//							cwe.setCoverdetals(dyresponse);
//							cwwList.add(cwe);
//							});
//						
//					});
//					List<Map<String, Object>> addcoverdetails= new ArrayList<>();
//					List<Map<String, Object>> excessdetails= new ArrayList<>();
//					List<Map<String, Object>> conditionListCover = new ArrayList<>();
//					List<Map<String, Object>> warrantyDescriptionCover = new ArrayList<>();
//					List<Map<String, Object>> exclusionListCover =new ArrayList<>();
//					List<PolicyCoverData> additionalCover = policyDataList.stream().filter(u -> "A".equalsIgnoreCase(u.getCoverageType()) && u.getSectionId() == Integer.parseInt(sec))
//							.collect(Collectors.toList());
//					if(additionalCover!=null && additionalCover.size()>0) 
//					{
//						additionalCover.forEach(p -> {
//							Map<String,Object> e = new HashMap<String,Object>();
//							e.put("covername", p.getCoverDesc()==null?"":capitalizeFirstLetter(p.getCoverDesc().toString()));
//							e.put("coverlimit", p.getCoverageLimit()==null?BigDecimal.ZERO:formatAmount(p.getCoverageLimit()));
//							e.put("excessAmount", p.getExcessAmount()==null?BigDecimal.ZERO:p.getExcessAmount());
//							e.put("ExcessDescription", p.getExcessDesc()==null?BigDecimal.ZERO:p.getExcessDesc());
//							addcoverdetails.add(e);
//						});
//					}
//					if(!"5".equalsIgnoreCase(home.getProductId().toString())) {
//					List<ExcessMaster> excessList = excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(home.getCompanyId(),home.getProductId().toString(),"99999","99999");
//					if(excessList!=null && excessList.size()>0) {
//						excessList.forEach(e_l -> {
//							Map<String,Object> e = new HashMap<String,Object>();
//							e.put("excessName", e_l.getCoverName()==null?"":e_l.getCoverName());
//							e.put("ExcessDescription", e_l.getExcessDescription()==null?"":e_l.getExcessDescription());
//							e.put("ExcessPercentage", e_l.getExcessPercentage()==null?0:e_l.getExcessPercentage());
//							e.put("ExcessAmount", e_l.getExcessAmount()==null?0.00:e_l.getExcessAmount());
//							excessdetails.add(e);
//						});
//						
//					}
//					}
//					CoverClassWarrantyExclusionRes cwe = new CoverClassWarrantyExclusionRes();
//					if(!"5".equalsIgnoreCase(home.getProductId().toString())) {
//					 conditionListCover = jasperCus.getConditionListCover(home.getPolicyNo(),home.getQuoteNo(),sec,"99999",locId);
//					 warrantyDescriptionCover = jasperCus.getWarrantyDescriptionCover(home.getPolicyNo(),home.getQuoteNo(),sec,"99999",locId);
//					 exclusionListCover = jasperCus.getExclusionListCover(home.getPolicyNo(),home.getQuoteNo(),sec,"99999",locId);
//					}
//					cwe.setCondition(conditionListCover);
//					cwe.setExclusionList(exclusionListCover);
//					cwe.setWarranty(warrantyDescriptionCover);
//					cwe.setExcess(excessdetails);
//					cwwList.add(cwe);
//					secRes.setBinifit(addcoverdetails);
//					secRes.setCoverCount(cwwList.size());
//					secRes.setCweSetlist(cwwList);
//					
//					secResList.add(secRes);
//					secResList.sort(Comparator.comparingInt(SectionDetailsKeyValueDto::getSectionOrder));
//					locaRes.setSectionName(secResList);
//				});
//				locationList.add(locaRes);
//
//			});
//		}
//				
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error("Exception in dynamicSetAssert ===> {}", e.getMessage());
//		}
//
//		return locationList;
//	}

	private List<LocationInformationKeyValueRes> dynamicSetAssert(HomePositionMaster home,
			List<BuildingRiskDetails> buildList, List<CommonDataDetails> commonList, boolean is, String brokeryn,List<SectionDataDetails> sectionList) {

		List<LocationInformationKeyValueRes> locationList = new ArrayList<>();
		try {
			java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.00");

			List<PolicyCoverData> policyDataList = policyCDRepo.findByQuoteNo(home.getQuoteNo());
			
			// Filter active records only (status != D)
			List<PolicyCoverData> activePolicyData = policyDataList.stream()
			        .filter(p -> !"D".equalsIgnoreCase(p.getStatus())
			                && !Integer.valueOf(99999).equals(p.getLocationId())
			                && !Integer.valueOf(99999).equals(p.getSectionId())
			                && !"A".equalsIgnoreCase(p.getCoverageType())
			                && !"T".equalsIgnoreCase(p.getCoverageType()) 
			                && !"L".equalsIgnoreCase(p.getStatus()))
			        .collect(Collectors.toList());

			if (activePolicyData != null && !activePolicyData.isEmpty()) {

				// GROUP: Location → Section → Cover → Set<VehicleId> (distinct)
				Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> groupedData = activePolicyData.stream()
						.collect(Collectors.groupingBy(PolicyCoverData::getLocationId,
								Collectors.groupingBy(PolicyCoverData::getSectionId, Collectors.groupingBy(
										PolicyCoverData::getCoverId,
										Collectors.mapping(PolicyCoverData::getVehicleId, Collectors.toSet())))));

				groupedData.forEach((locId, sectionMap) -> {

					// ─── LOCATION LEVEL ───────────────────────────────────────
					LocationInformationKeyValueRes locaRes = new LocationInformationKeyValueRes();

					List<PolicyCoverData> locationData = activePolicyData.stream()
							.filter(p -> p.getLocationId().equals(locId)).collect(Collectors.toList());

					List<BuildingRiskDetails> collect = buildList.stream().filter(l -> l.getLocationId().equals(locId)).collect(Collectors.toList());

					List<CommonDataDetails> collect2 = commonList.stream().filter(l -> l.getLocationId().equals(locId)).collect(Collectors.toList());

					String address = null;
					if (!collect.isEmpty()) {
						address = collect.get(0).getAddress();
					} else if (!collect2.isEmpty()) {
						address = collect2.get(0).getAddress();
					}

					if (locationData.isEmpty())
						return;

					// Location name - get from buildList or commonList (policyDataList doesn't have
					// locationName)
					// Keep using buildList/commonList for location metadata as before
					String locationName = !collect.isEmpty() ? collect.get(0).getLocationName()
							: (!collect2.isEmpty() ? collect2.get(0).getLocationName() : "");

					List<SectionDetailsKeyValueDto> secResList = new ArrayList<>();
					List<KeyAndValueDto> keVLocaList = new ArrayList<>();
					keVLocaList.add(new KeyAndValueDto("Location Name", locationName));
					keVLocaList.add(new KeyAndValueDto("Address", address));
					locaRes.setLocationName(keVLocaList);

					// ─── SECTION LEVEL ────────────────────────────────────────
					sectionMap.forEach((secId, coverMap) -> {

						Double premiumLc = 0d;
						Double premiumTotal = 0d;
						List<CoverClassWarrantyExclusionRes> cwwList = new ArrayList<>();

						SectionDetailsKeyValueDto secRes = new SectionDetailsKeyValueDto();
						productWishAddInfo(home, secRes, secId.toString(), locId, buildList);
						setOrderBySection(secRes, secId.toString(), home);
						secRes.setSectionId(secId.toString());

						// Get section-level policy data for metadata (sectionDesc,
						// coverNoteReferenceNo)
						List<PolicyCoverData> secList = activePolicyData.stream()
								.filter(p -> p.getSectionId().equals(secId) && p.getLocationId().equals(locId))
								.collect(Collectors.toList());

						List<KeyAndValueDto> sectionPre = new ArrayList<>();

						if (is  || "100019".equalsIgnoreCase(home.getCompanyId())) {
							premiumLc = policyDataList.stream()
									.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
											&& o.getPremiumExcludedTaxLc() != null
											&& o.getPremiumExcludedTaxLc().doubleValue() > 0D
											&& !"D".equalsIgnoreCase(o.getStatus()) && o.getSectionId().equals(secId)
											&& o.getLocationId().equals(locId))
									.mapToDouble(o -> o.getPremiumExcludedTaxLc().doubleValue()).sum();
							if("100019".equalsIgnoreCase(home.getCompanyId())) {
							premiumTotal = policyDataList.stream()
									.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
											&& o.getPremiumIncludedTaxLc() != null
											&& o.getPremiumIncludedTaxLc().doubleValue() > 0D
											&& !"D".equalsIgnoreCase(o.getStatus()) && o.getSectionId().equals(99999))
									.mapToDouble(o -> o.getPremiumIncludedTaxLc().doubleValue()).sum();
							}
							else
							{
								premiumTotal = policyDataList.stream()
										.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
												&& o.getPremiumIncludedTaxLc() != null
												&& o.getPremiumIncludedTaxLc().doubleValue() > 0D
												&& !"D".equalsIgnoreCase(o.getStatus()) && o.getSectionId().equals(secId)
												&& o.getLocationId().equals(locId))
										.mapToDouble(o -> o.getPremiumIncludedTaxLc().doubleValue()).sum();
							}
							sectionPre.add(new KeyAndValueDto("Premium", formatter.format(premiumLc)));
							sectionPre.add(new KeyAndValueDto("Total Premium (Including Tax)",
									formatter.format(premiumTotal)));
						}
						String sectionDesc = sectionList.stream()
								.filter(s -> s.getSectionId() != null && s.getSectionId().equals(secId.toString()))
								.map(SectionDataDetails::getSectionDesc).findFirst().orElse("NA");

						sectionPre.add(new KeyAndValueDto("Section Name", sectionDesc));

						if ("100002".equalsIgnoreCase(home.getCompanyId()) && "N".equalsIgnoreCase(brokeryn)) {
							sectionPre.add(new KeyAndValueDto("Tira CoverNote Number",
									sectionList.get(0).getCoverNoteReferenceNo()));
						}

						secRes.setPremium(sectionPre);

						// ─── COVER LEVEL ──────────────────────────────────────
						coverMap.forEach((coverId, vehicleSet) -> {

							// ─── VEHICLE LEVEL (distinct) ─────────────────────
							vehicleSet.forEach(vehicleId -> {

								List<Map<String, Object>> conditionListCover = new ArrayList<>();
								List<Map<String, Object>> warrantyDescriptionCover = new ArrayList<>();
								List<Map<String, Object>> exclusionListCover = new ArrayList<>();
								List<ExcessMaster> excessList = new ArrayList<>();
								

								CoverClassWarrantyExclusionRes cwe = new CoverClassWarrantyExclusionRes();
								cwe.setCoverId(coverId == null ? null : coverId.toString());
								cwe.setRiskId(vehicleId == null ? null : vehicleId.toString()); // vehicleId replaces
																								// riskId

								if (!"5".equalsIgnoreCase(home.getProductId().toString())) {
									conditionListCover = getConditionListCover(home.getPolicyNo(),home.getQuoteNo(), secId.toString(), coverId.toString(), locId);
									warrantyDescriptionCover = getWarrantyDescriptionCover(home.getPolicyNo(),home.getQuoteNo(), secId.toString(), coverId.toString(), locId);
									exclusionListCover = getExclusionListCover(home.getPolicyNo(),home.getQuoteNo(), secId.toString(), coverId.toString(), locId);
									List<Map<String, Object>> excessListCover = getExcessListCover(home , secId.toString(), coverId.toString(), locId );
									cwe.setExcess(excessListCover);		
											
								}
								else
								{
									conditionListCover = getConditionListCoverTermsOnly(home.getPolicyNo(),home.getQuoteNo(), secId.toString(), coverId.toString(), locId);
									warrantyDescriptionCover = getWarrantyDescriptionCoverTermsOnly(home.getPolicyNo(),home.getQuoteNo(), secId.toString(), coverId.toString(), locId);
									exclusionListCover = getExclusionListCoverTermsOnly(home.getPolicyNo(),home.getQuoteNo(), secId.toString(), coverId.toString(), locId);
								}

								

								cwe.setCondition(conditionListCover);
								cwe.setExclusionList(exclusionListCover);
								cwe.setWarranty(warrantyDescriptionCover);
								

								log.info("Enter in Dynamic Cover ===> {}");
								// Pass vehicleId where riskId was used before
								List<Map<String, Object>> dyresponse = CoverTojson(home, secId.toString(), coverId,
										vehicleId, locId);
								if (dyresponse == null || dyresponse.isEmpty()) {
									return; // skip this vehicleId/coverId — don't add to cwwList
								}

								if (dyresponse != null && !dyresponse.isEmpty()) {
									Map<String, Object> cover = dyresponse.get(0);
									int count = 0;
									for (int i = 1; i <= 20; i++) {
										if (cover.containsKey("Key" + i))
											count++;
									}
									cwe.setColumnCount(count + "");
								} else {
									cwe.setColumnCount("");
								}

								cwe.setCoverdetals(dyresponse);
								cwwList.add(cwe);
							});
						});

						// ─── SECTION-LEVEL ADDITIONAL COVERS & EXCESS ────────
						List<Map<String, Object>> addcoverdetails = new ArrayList<>();
						List<Map<String, Object>> sectionExcessDetails = new ArrayList<>();
						List<Map<String, Object>> sectionConditions = new ArrayList<>();
						List<Map<String, Object>> sectionWarranty = new ArrayList<>();
						List<Map<String, Object>> sectionExclusions = new ArrayList<>();

						List<PolicyCoverData> additionalCover = policyDataList.stream()
								.filter(u -> "A".equalsIgnoreCase(u.getCoverageType()) && u.getSectionId().equals(secId)
										&& u.getLocationId().equals(locId) && !"D".equalsIgnoreCase(u.getStatus()))
								.collect(Collectors.toList());

						if (additionalCover != null && !additionalCover.isEmpty()) {
							additionalCover.forEach(p -> {
								Map<String, Object> e = new HashMap<>();
								e.put("covername",
										p.getCoverDesc() == null ? "" : capitalizeFirstLetter(p.getCoverDesc()));
								e.put("coverlimit", p.getCoverageLimit() == null ? BigDecimal.ZERO
										: formatAmount(p.getCoverageLimit()));
								e.put("excessAmount",
										p.getExcessAmount() == null ? BigDecimal.ZERO : p.getExcessAmount());
								e.put("ExcessDescription",
										p.getExcessDesc() == null ? BigDecimal.ZERO : p.getExcessDesc());
								addcoverdetails.add(e);
							});
						}

						if (!"5".equalsIgnoreCase(home.getProductId().toString())) {
							List<ExcessMaster> sectionExcessList = excessRepo
									.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
											home.getCompanyId(), home.getProductId().toString(), "99999", "99999");
							if (sectionExcessList != null && !sectionExcessList.isEmpty()) {
								sectionExcessList.forEach(e_l -> {
									Map<String, Object> e = new HashMap<>();
									e.put("excessName", e_l.getCoverName() == null ? "" : e_l.getCoverName());
									e.put("ExcessDescription",
											e_l.getExcessDescription() == null ? "" : e_l.getExcessDescription());
									e.put("ExcessPercentage",
											e_l.getExcessPercentage() == null ? 0 : e_l.getExcessPercentage());
									e.put("ExcessAmount", e_l.getExcessAmount() == null ? 0.00 : e_l.getExcessAmount());
									sectionExcessDetails.add(e);
								});
							}

							sectionConditions = getConditionListCover(home.getPolicyNo(), home.getQuoteNo(),
									secId.toString(), "99999", locId);
							sectionWarranty = getWarrantyDescriptionCover(home.getPolicyNo(),
									home.getQuoteNo(), secId.toString(), "99999", locId);
							sectionExclusions = getExclusionListCover(home.getPolicyNo(), home.getQuoteNo(),
									secId.toString(), "99999", locId);
						}
						else
						{
							sectionConditions = getConditionListCoverTermsOnly(home.getPolicyNo(), home.getQuoteNo(),
									secId.toString(), "99999", locId);
							sectionWarranty = getWarrantyDescriptionCoverTermsOnly(home.getPolicyNo(),
									home.getQuoteNo(), secId.toString(), "99999", locId);
							sectionExclusions = getExclusionListCoverTermsOnly(home.getPolicyNo(), home.getQuoteNo(),
									secId.toString(), "99999", locId);
						}

						CoverClassWarrantyExclusionRes sectionCwe = new CoverClassWarrantyExclusionRes();
						sectionCwe.setCondition(sectionConditions);
						sectionCwe.setExclusionList(sectionExclusions);
						sectionCwe.setWarranty(sectionWarranty);
						sectionCwe.setExcess(sectionExcessDetails);
						cwwList.add(sectionCwe);

						secRes.setBinifit(addcoverdetails);
						secRes.setCoverCount(cwwList.size());
						secRes.setCweSetlist(cwwList);

						secResList.add(secRes);
						secResList.sort(Comparator.comparingInt(SectionDetailsKeyValueDto::getSectionOrder));
						locaRes.setSectionName(secResList);
					});

					locationList.add(locaRes);
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in dynamicSetAssert ===> {}", e.getMessage());
		}

		return locationList;
	}

	private void setOrderBySection(SectionDetailsKeyValueDto secRes, String sec, HomePositionMaster home) {
		try {
			List<RiskFieldFlow> riOrder = riskFFRepo.findByCompanyIdAndProductIdAndSectionIdOrderByKeyIdDesc(
					Integer.valueOf(home.getCompanyId()), home.getProductId(), Integer.valueOf(sec));
			List<Integer> orderList = riOrder.stream().map(RiskFieldFlow::getSectionOrder).filter(Objects::nonNull)
					.sorted().collect(Collectors.toList());
			if (orderList != null && !orderList.isEmpty()) {
				Integer order = orderList.get(0);
				secRes.setSectionOrder(order);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in setOrderBySection ===> {}", e.getMessage());
		}

	}

	private void addInfo(List<Map<String, Object>> list, String label, Object value) {
		Map<String, Object> item = new HashMap<>();
		item.put("Key", label);
		item.put("Value", value);
		list.add(item);
	}

	private void productWishAddInfo(HomePositionMaster home, SectionDetailsKeyValueDto secRes, String sectionId,
			Integer loc, List<BuildingRiskDetails> buildList) {
		try {
			if (home.getProductId() == 49) {
				Integer sectionIdInt = sectionId != null ? Integer.valueOf(sectionId) : null;
				List<PolicyCoverData> coverDataList = policyCDRepo
						.findByRequestReferenceNoAndLocationIdAndProductIdAndCompanyIdAndSectionIdAndCoverageType(
								home.getRequestReferenceNo(), loc, home.getProductId(), home.getCompanyId(),
								sectionIdInt, "B");

				Set<Integer> coverIds = coverDataList.stream().map(PolicyCoverData::getCoverId)
						.collect(Collectors.toSet());

				List<BuildingRiskDetails> matchedBuildList = buildList.stream()
						.filter(b -> coverIds.contains(b.getCoverId())).collect(Collectors.toList());

				if (!matchedBuildList.isEmpty()) {

					BuildingRiskDetails b = matchedBuildList.get(0);

					List<Map<String, Object>> response = new ArrayList<>();

					String transitType = "";

					if ("1".equals(b.getParam2())) {
						transitType = "Import";
					} else if ("2".equals(b.getParam2())) {
						transitType = "Export";
					} else if ("3".equals(b.getParam2())) {
						transitType = "Both Import and Export";
					}

					addInfo(response, "Mode of Transport", b.getParam1());
					addInfo(response, "Transit Type", transitType);
					addInfo(response, "Territorial Limit", b.getParam3());
					addInfo(response, "Packaging", b.getParam4());
					addInfo(response, "GoodsInTransitSumInsured", b.getSumInsured());
					addInfo(response, "Transit Coverage", b.getBuildingUsageId());
					addInfo(response, "CoverageType", b.getCategoryDesc());
					addInfo(response, "vehicleCount", b.getContentId());
					addInfo(response, "MaximumLimitTrips", b.getGeographicalCoverage());
					addInfo(response, "TripsMonth", b.getModeOfTransport());

					if (response.size() > 5) {
						int mid = (response.size() + 1) / 2;
						secRes.setAddInfo1(new ArrayList<>(response.subList(0, mid)));
						secRes.setAddInfo2(new ArrayList<>(response.subList(mid, response.size())));
					} else {
						secRes.setAddInfo1(response);
					}

				} else {
					processDynamicAddInfo(home, secRes, sectionId, loc);
				}

			} else {
				processDynamicAddInfo(home, secRes, sectionId, loc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in productWishAddInfo ===> {}", e.getMessage());
		}
	}

	private void processDynamicAddInfo(HomePositionMaster home, SectionDetailsKeyValueDto secRes, String sectionId,
			Integer loc) {
		WorkEngine e = new WorkEngine();
		e.setCompanyId(home.getCompanyId());
		e.setProductId(home.getProductId().toString());
		e.setQuoteNo(home.getQuoteNo());
		e.setRequestReferenceNo(home.getRequestReferenceNo());
		e.setSectionId(sectionId);
		e.setCoverId(88888);
		e.setRiskId(1);
		e.setLocationId(loc.toString());
		e.setIntegType("DYNC_INTEG");
		System.out.println(e);

		List<Map<String, Object>> dyresponse = jsonMapper.createDynamicPdf(e);
		List<FirstLossPayee> flist = firstRepo
				.findByRequestReferenceNoAndLocationIdAndSectionId(home.getRequestReferenceNo(), loc, sectionId);
		if (flist != null && !flist.isEmpty()) {
			for (FirstLossPayee f : flist) {

				Map<String, Object> borrowerType = new LinkedHashMap<>();
				borrowerType.put("Key", "Borrower Type");
				borrowerType.put("Value", f.getBorrowerTypeDesc() == null ? "" : f.getBorrowerTypeDesc());
				dyresponse.add(borrowerType);

				Map<String, Object> collateral = new LinkedHashMap<>();
				collateral.put("Key", "Collateral / Bank");
				collateral.put("Value", f.getCollateralName() == null ? "" : f.getCollateralName());
				dyresponse.add(collateral);

				Map<String, Object> payeeName = new LinkedHashMap<>();
				payeeName.put("Key", "Payee Name");
				payeeName.put("Value", f.getFirstLossPayeeDesc() == null ? "" : f.getFirstLossPayeeDesc());
				dyresponse.add(payeeName);
			}
		}

		if (dyresponse != null && !dyresponse.isEmpty()) {
			System.out.println("Map Size: " + dyresponse.size());
			if (dyresponse.size() > 5) {
				int mid = dyresponse.size() / 2;
				secRes.setAddInfo1(new ArrayList<>(dyresponse.subList(0, mid + 1)));
				secRes.setAddInfo2(new ArrayList<>(dyresponse.subList(mid + 1, dyresponse.size())));
			} else {
				secRes.setAddInfo1(dyresponse);
			}
		}
	}

	private List<Map<String, Object>> CoverTojson(HomePositionMaster home, String sec, Integer coverId, Integer risk,
			Integer loc) {
		try {
			WorkEngine e = new WorkEngine();
			e.setCompanyId(home.getCompanyId());
			e.setProductId(home.getProductId().toString());
			e.setQuoteNo(home.getQuoteNo());
			e.setRequestReferenceNo(home.getRequestReferenceNo());
			e.setSectionId(sec);
			e.setCoverId(coverId);
			e.setRiskId(risk);
			e.setLocationId(loc.toString());
			e.setRequestReferenceNo(home.getRequestReferenceNo());
			e.setIntegType("DYNC_INTEG");
			System.out.println(e);
			List<Map<String, Object>> quotation = jsonMapper.createDynamicPdfCover(e);
			if (quotation != null && !quotation.isEmpty()) {
				List<Map<String, Object>> request = (List<Map<String, Object>>) quotation.get(0).get("Request");
				return request;
			} else {
				return Collections.emptyList();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private String formatAmount(Object value) {
		if (value == null || value.toString().trim().isEmpty()) {
			return "0";
		}
		try {
			BigDecimal bd = new BigDecimal(value.toString().trim()).setScale(0, RoundingMode.HALF_UP);
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
			nf.setMinimumFractionDigits(0);
			nf.setMaximumFractionDigits(0);
			return nf.format(bd);
		} catch (Exception e) {
			return value.toString();
		}
	}

	private List<KeyAndValueDto> buildBrokerDetails(HomePositionMaster home, LoginUserInfo login) {

		List<KeyAndValueDto> list = new ArrayList<>();

		try {
			if ("1".equalsIgnoreCase(home.getApplicationId())) {
				if ("100002".equalsIgnoreCase(home.getCompanyId())) {
					list.add(new KeyAndValueDto("Sale Point Code",
							home.getSalePointCode() != null ? home.getSalePointCode().toString() : "NA"));
					list.add(new KeyAndValueDto("Broker Tira Code",
							home.getBrokerTiraCode() != null ? home.getBrokerTiraCode().toString() : "NA"));
				}
				list.add(new KeyAndValueDto("Broker Name", nvl(login.getUserName())));
				list.add(new KeyAndValueDto("Broker Code", nvl(login.getCoreAppBrokerCode())));
				list.add(new KeyAndValueDto("Mobile", nvl(login.getWhatsappCode()) + "-" + nvl(login.getWhatsappNo())));
				list.add(new KeyAndValueDto("Branch Name", nvl(home.getBrokerBranchName())));
				list.add(new KeyAndValueDto("Agency Code",
						home.getAgencyCode() != null ? home.getAgencyCode().toString() : "NA"));

			} else {

				if ("100002".equalsIgnoreCase(home.getCompanyId())) {
					list.add(new KeyAndValueDto("Intermediary Name", nvl(home.getBdmName())));
					list.add(new KeyAndValueDto("Agency Code",
							home.getBdmCode() != null ? home.getBdmCode().toString() : "NA"));
					list.add(new KeyAndValueDto("Sale Point Code",
							home.getSalePointCode() != null ? home.getSalePointCode().toString() : "NA"));
					list.add(new KeyAndValueDto("Broker Tira Code",
							home.getBrokerTiraCode() != null ? home.getBrokerTiraCode().toString() : "NA"));

				} else {
					if (login != null) {
						list.add(new KeyAndValueDto("Broker Name", nvl(login.getUserName())));
						list.add(new KeyAndValueDto("Address", nvl(login.getAddress1())));
						list.add(new KeyAndValueDto("Telephone Number", nvl(login.getUserMobile())));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in buildBrokerDetails ===> {}", e.getMessage());
		}

		return list;
	}

	private List<KeyAndValueDto> piMethodFforSet(List<PersonalInfo> piList, HomePositionMaster home) {

		List<KeyAndValueDto> result = new ArrayList<>();

		try {

			if (!("100002".equalsIgnoreCase(home.getCompanyId())) || ("100019".equalsIgnoreCase(home.getCompanyId()))) {
				PersonalInfo pi = piList.get(0);
				result.add(new KeyAndValueDto("Name", pi.getClientName()));
				result.add(new KeyAndValueDto("Occupation", pi.getOccupationDesc()));
				result.add(new KeyAndValueDto("Mobile", pi.getMobileCodeDesc1() + "-" + pi.getMobileNo1()));
				result.add(new KeyAndValueDto("Physical Address", pi.getAddress1()));
				result.add(new KeyAndValueDto("Postal Address",
						pi.getAddress1() + " , " + (StringUtils.isNotBlank(pi.getStateName()) ? pi.getStateName() : "")
								+ (StringUtils.isNotBlank(pi.getCityName()) ? " , " + pi.getCityName() : "")
								+ (StringUtils.isNotBlank(pi.getPinCode()) ? " , " + pi.getPinCode() : "")));
				result.add(new KeyAndValueDto("Contact Details",
						"Mobile: " + pi.getMobileCodeDesc1() + "-" + pi.getMobileNo1() + ", Email: " + pi.getEmail1()));
			} else {
				if (piList != null && !piList.isEmpty()) {
					PersonalInfo pi = piList.get(0);
					result.add(new KeyAndValueDto("Name", pi.getClientName()));
					result.add(new KeyAndValueDto("Mobile", pi.getMobileCodeDesc1() + "-" + pi.getMobileNo1()));
					result.add(new KeyAndValueDto("Address", pi.getAddress1()));
					result.add(new KeyAndValueDto("State / CityName",
							(StringUtils.isNotBlank(pi.getStateName()) ? pi.getStateName() : "")
									+ (StringUtils.isNotBlank(pi.getCityName()) ? " , " + pi.getCityName() : "")));
					result.add(new KeyAndValueDto("Email Id", pi.getEmail1()));
					// result.add(new KeyAndValueDto("Customer Id", pi.getCustomerId()));
					// result.add(new KeyAndValueDto("Quote No", home.getQuoteNo()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in piMethodFforSet ===> {}", e.getMessage());
		}

		return result;
	}

	public static String convertAmountToWords(BigDecimal amount) {

		long rupees = amount.longValue();

//	    int paise = amount
//	            .subtract(new BigDecimal(rupees))
//	            .multiply(new BigDecimal(100))
//	            .setScale(0, RoundingMode.HALF_UP)
//	            .intValue();
		String words = convertNumberToWords(rupees);

//	    if (paise > 0) {
//	        words += " and " + convertNumberToWords(paise);
//	    }
		return words + " Only";
	}

	public static String convertNumberToWords(long number) {

		if (number == 0) {
			return "Zero";
		}

		if (number < 0) {
			return "Minus " + convertNumberToWords(-number);
		}

		if (number < 20) {
			return units[(int) number];
		}

		if (number < 100) {
			return tens[(int) number / 10] + ((number % 10 != 0) ? " " + units[(int) number % 10] : "");
		}

		if (number < 1000) {
			return convertNumberToWords(number / 100) + " Hundred"
					+ ((number % 100 != 0) ? " " + convertNumberToWords(number % 100) : "");
		}

		if (number < 1_000_000L) { // Thousand
			return convertNumberToWords(number / 1000) + " Thousand"
					+ ((number % 1000 != 0) ? " " + convertNumberToWords(number % 1000) : "");
		}

		if (number < 1_000_000_000L) { // Million
			return convertNumberToWords(number / 1_000_000) + " Million"
					+ ((number % 1_000_000 != 0) ? " " + convertNumberToWords(number % 1_000_000) : "");
		}

		if (number < 1_000_000_000_000L) { // Billion
			return convertNumberToWords(number / 1_000_000_000) + " Billion"
					+ ((number % 1_000_000_000 != 0) ? " " + convertNumberToWords(number % 1_000_000_000) : "");
		}

		// Trillion
		return convertNumberToWords(number / 1_000_000_000_000L) + " Trillion"
				+ ((number % 1_000_000_000_000L != 0) ? " " + convertNumberToWords(number % 1_000_000_000_000L) : "");
	}

	private String capitalizeFirstLetter(String str) {
		if (StringUtils.isNotBlank(str)) {
			return Arrays.stream(str.trim().split("\\s+"))
					.map(m -> Character.toUpperCase(m.charAt(0)) + m.substring(1).toLowerCase())
					.collect(Collectors.joining(" "));
		}
		return null;
	}

	@Override
	public CommonRes insertcoverForRFL(RiskFieldFlowDto req) {
		CommonRes res = new CommonRes();
		try {

			FieldQueryTableQuery fqt = new FieldQueryTableQuery();
			List<Integer> coverId = List.of(99999, req.getCoverId());
			List<Integer> sectionId = List.of(99999, req.getSectionId());
			List<Integer> product = List.of(99999, req.getProductId());
			List<RiskFieldFlow> flList = new ArrayList<>();
			List<RiskFieldFlow> fl = riskFFRepo.findByCompanyIdAndProductIdInAndSectionIdInAndCoverIdInOrderByKeyIdDesc(
					req.getCompanyId(), product, sectionId, coverId);
			// List<ProductSectionMaster> prod =
			// productRepo.findByCompanyIdAndProductIdAndSectionIdAndStatusOrderByAmendIdDesc(req.getCompanyId().toString(),req.getProductId(),
			// req.getSectionId(),"Y");

			if (req.getCoverId() == null) {
				req.setCoverId(99999);
			}

			if (fl == null || fl.isEmpty()) {
				RiskFieldFlow rff = convertToEntity(req, fl, "H");
				flList.add(rff);
				riskFFRepo.saveAllAndFlush(flList);
				res.setCommonResponse(rff);
			} else {
				RiskFieldFlow rff = convertToEntity(req, fl, "N");
				riskFFRepo.saveAndFlush(rff);
				res.setCommonResponse(rff);
			}
			res.setErroCode(0);
			res.setIsError(false);
			res.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in insertcoverForRFL ===> " + e.getMessage());
			res.setCommonResponse("Exception in insertcoverForRFL ===> " + e.getMessage());
			res.setErroCode(0);
			res.setIsError(false);
			res.setMessage("Success");
		}
		return res;
	}

	public String getColumnName(String fieldName) {

		try {
			Field field = BuildingRiskDetails.class.getDeclaredField(fieldName);
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				return column.name();
			}

			return fieldName;

		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Field not found: " + fieldName);
		}
	}

	public RiskFieldFlow convertToEntity(RiskFieldFlowDto dto, List<RiskFieldFlow> fl, String yesorno) {
		Integer queryIdforcover;
		String queryId = dto.getQueryId().toString();
		if ("2001".equalsIgnoreCase(queryId) || "2002".equalsIgnoreCase(queryId) || "2003".equalsIgnoreCase(queryId)) {
			queryIdforcover = dto.getCoverId();
		} else {
			queryIdforcover = Integer.valueOf(88888);
				}

		Integer keyId = null;
		if ("H".equalsIgnoreCase(yesorno)) {
			keyId = 2;
		} else if (dto.getKeyId() == null) {
			if (fl != null && !fl.isEmpty()) {
				keyId = fl.get(0).getKeyId() + 1;
			} else {
				keyId = 1;
			}
		} else {
			keyId = dto.getKeyId();
		}

		return RiskFieldFlow.builder().companyId(dto.getCompanyId()).productId(dto.getProductId()).keyId(keyId).amount(dto.getAmount())
				.sectionId(dto.getSectionId()).coverId(queryIdforcover).jsonKey(dto.getJsonKey()).isHeader("No")
				.headerKeyid("1").isarray("No").datatype(dto.getDatatype()).pattern("No").defaultYn("N")
				.status(dto.getStatus()).queryId(dto.getQueryId()).orderBy(dto.getOrderBy())
				.companyName(dto.getCompanyName()).productName(dto.getProductName()).sectionName(dto.getSectionName())
				.coverName(dto.getCoverName()).queryCol(dto.getQueryCol()).queryAlias(dto.getQueryCol())
				.integType("DYNC_INTEG").build();
	}
//	private RiskFieldFlow headerSet(RiskFieldFlowDto dto) {
//
//		
//	    return RiskFieldFlow.builder()
//	            .companyId(dto.getCompanyId())
//	            .productId(dto.getProductId())
//	            .keyId(BigDecimal.ONE)
//	            .sectionId(dto.getSectionId())
//	            .coverId(dto.getCoverId())
//	            .jsonKey("Root")
//	            .isHeader("Y")
//	            .headerKeyid("99999")
//	            .isarray("N")
//	            .datatype("String")
//	            .defaultYn("N")
//	            .defaultValue(null)
//	            .status("Y")
//	            .queryId(dto.getQueryId())
//	            .queryCol("")
//	            .queryAlias("")
//	            .integType("DYNC_INTEG")
//	            .build();
//	}

	private RiskFieldFlowDto convertToDto(RiskFieldFlow entity) {

		return RiskFieldFlowDto.builder().companyId(entity.getCompanyId()).productId(entity.getProductId())
				.keyId(entity.getKeyId()).sectionId(entity.getSectionId()).coverId(entity.getCoverId())
				.jsonKey(entity.getJsonKey()).status(entity.getStatus()).queryId(entity.getQueryId())
				.queryCol(entity.getQueryCol())
				// .queryAlias(entity.getQueryAlias())
				.orderBy(entity.getOrderBy()).amount(entity.getAmount())
//	            .companyName(entity.getCompanyName())
//	            .productName(entity.getProductName())
//	            .sectionName(entity.getSectionName())
//	            .coverName(entity.getCoverName())
				.build();
	}

	@Override
	public CommonRes getAllcoverForRFL(GetALLCoverDto req) {

		CommonRes res = new CommonRes();

		try {
			List<RiskFieldFlow> fl = new ArrayList<>();
			if (req.getCoverId() != null) {
				List<Integer> coverId = List.of(99999, req.getCoverId());
				fl = riskFFRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdInOrderByKeyIdDesc(req.getCompanyId(),
						req.getProductId(), req.getSectionId(), coverId);
			} else {
				fl = riskFFRepo.findByCompanyIdAndProductIdAndSectionIdOrderByKeyIdDesc(req.getCompanyId(),
						req.getProductId(), req.getSectionId());
			}

			if (fl != null && !fl.isEmpty()) {
				List<RiskFieldFlowDto> response = fl.stream().map(this::convertToDto).toList();
				res.setCommonResponse(response);
				res.setIsError(false);
				res.setErroCode(0);
				res.setMessage("Success");
				return res;

			}

			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErroCode(0);
			res.setMessage("No data is present");
			return res;
		} catch (Exception e) {

			e.printStackTrace();
			log.error("Exception in getAllcoverForRFL ===> {}", e.getMessage());

			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(1);
			res.setMessage("Exception in getAllcoverForRFL ==> " + e.getMessage());
			return res;
		}

	}

	@Override
	public CommonRes getQueryDropDown() {
		CommonRes res = new CommonRes();
		try {
			List<FieldQueryTableQuery> all = fQTrepo.findAll();
			List<FieldQueryTableQueryDto> response = all.stream().map(this::setFQT).toList();
			res.setCommonResponse(response);
			res.setIsError(false);
			res.setErroCode(0);
			res.setMessage("Success");

		} catch (Exception e) {

			e.printStackTrace();
			log.error("Exception in getQueryDropDown ===> {}", e.getMessage());
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(1);
			res.setMessage("Exception in getQueryDropDown ==> " + e.getMessage());
		}

		return res;
	}

	private FieldQueryTableQueryDto setFQT(FieldQueryTableQuery entity) {

		return FieldQueryTableQueryDto.builder().queryId(entity.getQueryId()).queryName(entity.getQueryName())
				.sqlQuery(entity.getSqlQuery()).productYn(entity.getProductType()).pdfYn(entity.getPdfYn()).build();
	}

	@Override
	public CommonRes getQueryInsert(FieldQueryTableQueryDto req) {
		CommonRes res = new CommonRes();
		try {
			FieldQueryTableQuery fQTen = null;
			fQTen = fQTrepo.findByQueryId(req.getQueryId());
			if (fQTen != null) {
				fQTen.setQueryName(req.getQueryName());
				fQTen.setSqlQuery(req.getSqlQuery());
				fQTrepo.save(fQTen);
			} else {
				fQTen = new FieldQueryTableQuery();
				BigDecimal maxQueryId = fQTrepo.findMaxQueryId();
				BigDecimal nextQueryId = maxQueryId.add(BigDecimal.ONE);
				fQTen.setQueryId(nextQueryId);
				fQTen.setQueryName(req.getQueryName());
				fQTen.setSqlQuery(req.getSqlQuery());
				fQTrepo.save(fQTen);
			}
			res.setCommonResponse(fQTen);
			res.setIsError(false);
			res.setErroCode(0);
			res.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in getQueryDropDown ===> {}", e.getMessage());
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(1);
			res.setMessage("Exception in getQueryDropDown ==> " + e.getMessage());
		}
		return res;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public CommonRes getFieldDropDown(DropDownReq req) {
		CommonRes res = new CommonRes();
		try {
			// List<ProductSectionMaster> prod =
			// productRepo.findByCompanyIdAndProductIdAndSectionIdAndStatusOrderByAmendIdDesc(req.getCompanyId().toString(),req.getProductId(),
			// req.getSectionId(),"Y");

			if ("A".equalsIgnoreCase(req.getProductYn())) {
				List<EntityColumnMetaDto> dto = getEntityMeta(BuildingRiskDetails.class);
				res.setCommonResponse(dto);
				res.setMessage(BuildingRiskDetails.class.toString());
			} else if ("H".equalsIgnoreCase(req.getProductYn())) {
				List<EntityColumnMetaDto> dto = getEntityMeta(CommonDataDetails.class);
				res.setCommonResponse(dto);
				res.setMessage(CommonDataDetails.class.toString());
			} else if ("E".equalsIgnoreCase(req.getProductYn())) {
				List<EntityColumnMetaDto> dto = getEntityMeta(EngineerInfo.class);
				res.setCommonResponse(dto);
				res.setMessage(EngineerInfo.class.toString());
			} else if ("P".equalsIgnoreCase(req.getProductYn())) {
				List<EntityColumnMetaDto> dto = getEntityMeta(PolicyCoverData.class);
				res.setCommonResponse(dto);
				res.setMessage(PolicyCoverData.class.toString());
			} else if ("I".equalsIgnoreCase(req.getProductYn())) {
				List<EntityColumnMetaDto> dto = getEntityMeta(AviationInfo.class);
				res.setCommonResponse(dto);
				res.setMessage(AviationInfo.class.toString());
			} else if ("M".equalsIgnoreCase(req.getProductYn())) {
				List<EntityColumnMetaDto> dto = getEntityMeta(MarineHullInfo.class);
				res.setCommonResponse(dto);
				res.setMessage(MarineHullInfo.class.toString());
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		return res;
	}

	public List<EntityColumnMetaDto> getEntityMeta(Class<?> entityClass) {

		List<EntityColumnMetaDto> response = new ArrayList<>();

		Metamodel metamodel = em.getMetamodel();

		EntityType<?> entityType = metamodel.entity(entityClass);

		entityType.getAttributes().forEach(attribute -> {

			try {

				Field field = entityClass.getDeclaredField(attribute.getName());

				Column column = field.getAnnotation(Column.class);

				String columnName;

				if (column != null && !column.name().isEmpty()) {

					columnName = column.name();

				} else {

					columnName = attribute.getName().toUpperCase();

				}

				EntityColumnMetaDto dto = new EntityColumnMetaDto();

				dto.setDatatype(field.getType().getSimpleName());

				dto.setCode(columnName);

				dto.setCodeDes(field.getName());

				response.add(dto);

			} catch (Exception e) {

				e.printStackTrace();

			}

		});

		return response;

	}

	@Override
	public CommonRes deleteRecord(viewAllReq req) {
		CommonRes data = new CommonRes();
		try {
			int deleted = riskInfoPdfRepo.deleteByQuoteNo(req.getQuoteNo());
			if (deleted > 0) {
				data.setCommonResponse("Record Deleted Successfully");
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("No Record Found");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		return data;
	}

	@Override
	public CommonRes omanRecordForall(viewAllReq req) {OmanJasperDto res = new OmanJasperDto();
	CommonRes resp = new CommonRes();
	try {
		SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		HomePositionMaster home = homerepo.findByQuoteNo(req.getQuoteNo());
		PolicyDetailsForOman policyres = new PolicyDetailsForOman();
		SponsorDetailsFroOman cusRes = new SponsorDetailsFroOman();
		BrokerDtailsforOman broker = new BrokerDtailsforOman();
		WorkerDtails worker = new WorkerDtails();
		PremiumDto pre = new PremiumDto();
		
		String idExpDate = "",visaExpDate="";
		if (home != null) {
			policyres.setInceptionDate(
					home.getInceptionDate() != null ? displayFormat.format(home.getInceptionDate()) : "");
			policyres.setPolicyToDate(
					home.getExpiryDate() != null ? displayFormat.format(home.getExpiryDate()) : "");
			policyres.setEffectiveDate(
					home.getEffectiveDate() != null ? displayFormat.format(home.getEffectiveDate()) : "");
			policyres.setPolicyNo(home.getPolicyNo());
			policyres.setPolicyFromDate(
					home.getInceptionDate() != null ? displayFormat.format(home.getInceptionDate()) : "");
			res.setPolcy(policyres);
			LoginUserInfo login = new LoginUserInfo();
			String customerId = home.getCustomerId();
			List<PersonalInfo> pi = piRepo.findByCustomerIdAndCompanyId(customerId, home.getCompanyId());
			List<EmployeeInfo> Emplist = employeRepo
					.findByRequestReferenceNoOrderByEmpIdDesc(home.getRequestReferenceNo());
			PersonalInfo single = pi.get(0);
			
			List<String> addressParts = Arrays.asList(
					single.getStateName(),
			        single.getCityName(),
			        single.getPinCode() 
			);
			
			String sponsorAddress = addressParts.stream()
			        .filter(Objects::nonNull)
			        .map(String::trim)
			        .filter(s -> !s.isEmpty())
			        .collect(Collectors.joining(", "));
			cusRes.setSponsorName(single.getClientName());
			cusRes.setSponsorAdress(sponsorAddress);
			cusRes.setMobileNo(single.getMobileNo1());
			res.setCustomer(cusRes);
			if ("1".equalsIgnoreCase(home.getApplicationId())) {
				login = logrepo.findByLoginId(home.getLoginId());
				broker.setBrokerName(login.getUserName());
				broker.setCRNo(login.getCrNo());
				broker.setVATorTaxNo(login.getVatRegNo());
				broker.setMobileNoBroker(login.getUserMobile());
				broker.setEmailAddres(login.getUserMail());
				res.setBroker(broker);
			} else {
				login = logrepo.findByLoginId(home.getApplicationId());
				broker.setBrokerName(login.getUserName());
				broker.setCRNo(login.getCrNo());
				broker.setVATorTaxNo(login.getVatRegNo());
				broker.setMobileNoBroker(login.getUserMobile());
				broker.setEmailAddres(login.getUserMail());
				res.setBroker(broker);
			}
			if (Emplist != null && !Emplist.isEmpty()) {
				EmployeeInfo empInfo = Emplist.get(0);

				
				//worker.setIdOrVisa(empInfo.getIdOrVisa());
				worker.setEmployeeName(empInfo.getEmployeeName());
				worker.setNationality(empInfo.getNationalityDesc());
				worker.setPassportNo(empInfo.getPassportNo());
				worker.setOccupation(empInfo.getOccupation());
				worker.setOccupationDesc(empInfo.getOccupationDesc());
				worker.setTypeOfContractor(empInfo.getTypeOfContractor());
				if (empInfo.getPassportExpiryDate() != null) {
					worker.setPassportExpiryDate(
							new SimpleDateFormat("dd/MM/yyyy").format(empInfo.getPassportExpiryDate()));
				}

				if (empInfo.getVisaExpiryDate() != null) {
					worker.setVisaExpiryDate(
							new SimpleDateFormat("dd/MM/yyyy").format(empInfo.getVisaExpiryDate()));
				}
				
				
				if("I".equalsIgnoreCase(empInfo.getIdOrVisa())) {
					worker.setIdOrVisa(empInfo.getVisaNo());
					worker.setVisaNo(null);
					idExpDate = worker.getVisaExpiryDate();
				}else if("V".equalsIgnoreCase(empInfo.getIdOrVisa())) {
					worker.setIdOrVisa(null);
					worker.setVisaNo(empInfo.getVisaNo());
					visaExpDate = worker.getVisaExpiryDate();
				}
			}
			res.setWoker(worker);
			List<PolicyCoverData> coverData = policyCDRepo.findByQuoteNo(req.getQuoteNo());
			BigDecimal basePremium = coverData.stream()
					.filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
					.filter(c -> c.getCoverId() != null && Integer.valueOf(741).equals(c.getCoverId()))
					.map(c -> c.getPremiumIncludedTaxFc() == null ? BigDecimal.ZERO : c.getPremiumIncludedTaxFc())
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.000");
			String bP = formatter.format(basePremium);
			pre.setService(bP);
			BigDecimal optionalPremium = coverData.stream()
					.filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
					.filter(c -> c.getCoverId() != null && Integer.valueOf(742).equals(c.getCoverId()))
					.map(c -> c.getPremiumIncludedTaxFc() == null ? BigDecimal.ZERO : c.getPremiumIncludedTaxFc())
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			String Op = formatter.format(optionalPremium);
			pre.setAirservice(Op);
			BigDecimal randSfee = coverData.stream()
					.filter(c -> c.getSectionId() != null && c.getSectionId() == 99999)
					.filter(c -> c.getCoverId() != null && Integer.valueOf(5).equals(c.getCoverId()))
					.filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
					.filter(c -> c.getTaxId() != null && Integer.valueOf(2).equals(c.getTaxId()))
					.map(c -> c.getTaxAmount() == null ? BigDecimal.ZERO : c.getTaxAmount()).findFirst()
					.orElse(BigDecimal.ZERO);

			String li = formatter.format(randSfee);
			pre.setLevies(li);

			BigDecimal emergencyFund = coverData.stream()
					.filter(c -> c.getSectionId() != null && c.getSectionId() == 99999)
					.filter(c -> c.getCoverId() != null && (Integer.valueOf(5).equals(c.getCoverId())))
					.filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
					.filter(c -> c.getTaxId() != null && (Integer.valueOf(3).equals(c.getTaxId())))
					.map(c -> c.getTaxAmount() == null ? BigDecimal.ZERO : c.getTaxAmount()).findFirst()
					.orElse(BigDecimal.ZERO);
			String em = formatter.format(emergencyFund);
			pre.setEmergencyFund(em);
			BigDecimal vat = coverData.stream().filter(c -> c.getSectionId() != null && c.getSectionId() == 99999)
					.filter(c -> c.getCoverId() != null && Integer.valueOf(5).equals(c.getCoverId()))
					.filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
					.filter(c -> c.getTaxId() != null && Integer.valueOf(1).equals(c.getTaxId()))
					.map(c -> c.getTaxAmount() == null ? BigDecimal.ZERO : c.getTaxAmount()).findFirst()
					.orElse(BigDecimal.ZERO);
			String vt = formatter.format(vat);
			pre.setVat(vt);

			BigDecimal TP = coverData.stream().filter(c -> c.getSectionId() != null && c.getSectionId() == 99999)
					.filter(c -> !"T".equalsIgnoreCase(c.getCoverageType()))
					.map(c -> c.getPremiumIncludedTaxLc() == null ? BigDecimal.ZERO : c.getPremiumIncludedTaxLc())
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			String total = formatter.format(TP);
			pre.setTotalPremium(total);
			res.setPremium(pre);

			// InputStream in = getClass()
			// .getResourceAsStream("/template/OmanPolicy.docx");

			// IXDocReport report =
			// XDocReportRegistry.getRegistry()
			// .loadReport(in, TemplateEngineKind.Freemarker);

			// IContext context = report.createContext();
			String contractTypeDesc = "";
			String itemType = "CONTRACTORS_OMAN";
			List<ListItemValue> getList = getListItem(itemType,worker.getTypeOfContractor(), "100055");
			
			if(!getList.isEmpty()) {
				contractTypeDesc = getList.get(0).getItemValue();
			}
			String qrValidationCode = "";
			if (StringUtils.isBlank(home.getQrValidationCode())) {

				byte[] bytes = new byte[32];

				secureRandom.nextBytes(bytes);

				qrValidationCode = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

				home.setQrValidationCode(qrValidationCode);

				homerepo.saveAndFlush(home);

			} else {
				qrValidationCode = home.getQrValidationCode();
			}

			String verificationUrl = qrVerificationBaseUrl + qrValidationCode;

			// Generate QR image
			byte[] qrImageBytes = qrCodeService.generateQRCode(verificationUrl);

			InputStream in = getClass().getResourceAsStream("/template/OmanPolicyDocNew.docx");
			
			InputStream omrInputStream =
			        getClass().getResourceAsStream("/template/omr-symbol.png");

			if (omrInputStream == null) {
			    throw new FileNotFoundException("OMR symbol PNG not found");
			}

			byte[] omrImageBytes = omrInputStream.readAllBytes();

			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(in);

			PhysicalFonts.discoverPhysicalFonts();

			Mapper fontMapper = new IdentityPlusMapper();

			// English fonts
			fontMapper.put("Arial", PhysicalFonts.get("Arial"));
			fontMapper.put("Times New Roman", PhysicalFonts.get("Times New Roman"));
			fontMapper.put("Tahoma", PhysicalFonts.get("Tahoma"));

			// Arabic fonts
			// fontMapper.put("Amiri", PhysicalFonts.get("Amiri"));
			// fontMapper.put("Noto Naskh Arabic", PhysicalFonts.get("Noto Naskh Arabic"));
			// fontMapper.put("Noto Sans Arabic", PhysicalFonts.get("Noto Naskh Arabic"));

			wordMLPackage.setFontMapper(fontMapper);

			VariablePrepare.prepare(wordMLPackage);

			System.out.println("Arial               : " + PhysicalFonts.get("Arial"));
			System.out.println("Times New Roman     : " + PhysicalFonts.get("Times New Roman"));
			System.out.println("Carlito             : " + PhysicalFonts.get("Carlito"));
			System.out.println("Amiri               : " + PhysicalFonts.get("Amiri"));
			System.out.println("Noto Naskh Arabic : " + PhysicalFonts.get("Noto Naskh Arabic"));

			System.out.println("Tahoma : " + PhysicalFonts.get("Tahoma"));

			MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

			System.out.println("policyNo " + policyres.getPolicyNo());

			Map<String, String> context = new HashMap<>();

			context.put("certificateNo", nvl(policyres.getPolicyNo()));// nvl(policyres.getPolicyNo())
			context.put("policyIssueDate", nvl(policyres.getEffectiveDate()));
			context.put("policyTodate", nvl(policyres.getPolicyToDate()));
			context.put("policyFromDate", nvl(policyres.getPolicyFromDate()));

			context.put("insuredName", nvl(cusRes.getSponsorName()));
			context.put("insuredAddress", nvl(cusRes.getSponsorAdress()));
			context.put("insuredMobile", nvl(cusRes.getMobileNo()));

			context.put("customerName", nvl(broker.getBrokerName()));
			context.put("customerCode", nvl(broker.getCRNo()));
			context.put("customerVat", nvl(broker.getVATorTaxNo()));
			context.put("customerMobile", nvl(broker.getMobileNoBroker()));
			context.put("customeEmail", nvl(broker.getEmailAddres()));

			context.put("employeeName", nvl(worker.getEmployeeName()));
			context.put("employeeNationality", nvl(worker.getNationality()));
			context.put("employeePassportNo", nvl(worker.getPassportNo()));
			context.put("employeePassportExpDate", nvl(worker.getPassportExpiryDate()));
			context.put("employeeVisaNo", nvl(worker.getVisaNo()));
			context.put("employeeVisaExpDate", nvl(visaExpDate));
			context.put("employeeIdNo", nvl(worker.getIdOrVisa()));
			context.put("employeeIdExpDate", nvl(idExpDate));
			context.put("employeeTitle", nvl(""));
			context.put("employeeTitleDesc", nvl(worker.getOccupationDesc()));
			context.put("contractType", nvl(contractTypeDesc));
			
			context.put("serviceFee", nvlNumeric(pre.getService()));
			context.put("airTravelTicket", nvlNumeric(pre.getAirservice()));
			context.put("levies", nvlNumeric(pre.getLevies()));
			context.put("vat", nvlNumeric(pre.getVat()));
			context.put("totalPremium", nvlNumeric(pre.getTotalPremium()));
			context.put("emergencyFund", nvlNumeric(pre.getEmergencyFund()));

			documentPart.variableReplace(context);
			
			insertOmrSymbol(wordMLPackage, omrImageBytes);

			// Insert QR image
			insertQrCode(wordMLPackage, qrImageBytes);

			String reportName = "PolicyReport";

			String policyNo = policyres.getPolicyNo();
			String docFileName = reportName + "_" + policyNo.replaceAll("[\\\\/:*?\"<>|]", "_") + ".docx";

			Path generatedDocx = Paths.get(directaryPath, docFileName);

			Files.createDirectories(generatedDocx.getParent());

			wordMLPackage.save(generatedDocx.toFile());

			System.out.println("Generated DOCX: " + generatedDocx);

			// Files.write(outputPath, pdfArray);

			byte[] pdfArray = convertDocxToPdf(generatedDocx);

			// String reportName = "PolicyReport";

			// String policyNo = policyres.getPolicyNo();
			String fileName = reportName + "_" + policyNo.replaceAll("[\\\\/:*?\"<>|]", "_") + ".pdf";
			Path outputPath = Paths.get(directaryPath, fileName);// directaryPath "D:/ReportDocument"

			Files.createDirectories(outputPath.getParent());

			Files.write(outputPath, pdfArray);

			System.out.println("Document Generated Successfully");

			byte[] imageByte = Files.readAllBytes(outputPath);
			String imgStr = Base64.getEncoder().encodeToString(imageByte);
			String mimeType = Files.probeContentType(outputPath);
			String imgData = "data:" + mimeType + ";base64," + imgStr;

			Map<String, Object> docRes = new HashMap<>();

			docRes.put("FileName", fileName);
			docRes.put("Base64", imgData);

			resp.setCommonResponse(docRes);
			resp.setErrorMessage(null);
			resp.setIsError(false);
			resp.setMessage("Success");
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.error("Exception in viewAllinKeyAndValue ===> {}", e.getMessage());
		resp.setCommonResponse(e);
		resp.setErrorMessage(null);
		resp.setIsError(true);
		resp.setMessage("Failed");
	}
	return resp;
	
}
	
	private List<ListItemValue> getListItem(String itemType,String itemCode, String companyId) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			effectiveDate.where(a1, a2, b1, b2);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate b4 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			effectiveDate2.where(a3, a4, b3, b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			// Predicate n5 = cb.equal(c.get("companyId"), "99999");
			//Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			// Predicate n8 = cb.or(n4,n5);
			//Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			Predicate n11 = cb.equal(c.get("status"), "R");
			Predicate n12 = cb.or(n1, n11);
			Predicate n13 = cb.equal(c.get("itemCode"), itemCode);

			query.where(n2, n3, n4, n7, n10, n12,n13).orderBy(orderList);

			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			// list = list.stream().filter(distinctByKey(o ->
			// Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
			// list = list.stream().sorted((o1,
			// o2)->Long.valueOf(o1.getItemValue()).compareTo(Long.valueOf(o2.getItemValue()))).collect(Collectors.toList());

			if (itemType.equals("AGGREGATED_VALUE")) {
				list.sort(Comparator.comparing(ListItemValue::getItemId));
			} else {
				list.sort(Comparator.comparing(ListItemValue::getItemValue));
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	/*private void insertQrCode(WordprocessingMLPackage wordMLPackage, byte[] qrImageBytes) throws Exception {

		MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

		//List<Object> paragraphs = mainDocumentPart.getJAXBNodesViaXPath("//w:p", true);
		List<Object> paragraphs = mainDocumentPart.getContent();

		System.out.println("Total paragraphs: " + paragraphs.size());

		for (Object paragraphObj : paragraphs) {

			Object unwrapped = XmlUtils.unwrap(paragraphObj);

			if (!(unwrapped instanceof P)) {
				continue;
			}

			P paragraph = (P) unwrapped;

			String paragraphText = getParagraphText(paragraph);

			System.out.println("Paragraph text: [" + paragraphText + "]");

			if (paragraphText != null && paragraphText.contains("QR_CODE_PLACEHOLDER")) {

				System.out.println("QR_CODE_PLACEHOLDER FOUND");

				// Remove placeholder
				paragraph.getContent().clear();

				// ------------------------------------------------
				// 1. Create image part
				// ------------------------------------------------

				BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage,
						qrImageBytes);

				System.out.println("Image part created");

				// ------------------------------------------------
				// 2. Create inline image
				// ------------------------------------------------

				Inline inline = imagePart.createImageInline("QRCode.png", "Policy QR Code", 1, 2, false);

				// ------------------------------------------------
				// 3. Set QR size
				// ------------------------------------------------

				long width = 100L * 9525L;
				long height = 100L * 9525L;

				inline.getExtent().setCx(width);
				inline.getExtent().setCy(height);

				// ------------------------------------------------
				// 4. Create w:r
				// ------------------------------------------------

				org.docx4j.wml.ObjectFactory wmlFactory = new org.docx4j.wml.ObjectFactory();

				R run = wmlFactory.createR();

				// ------------------------------------------------
				// 5. Create w:drawing
				// ------------------------------------------------

				org.docx4j.wml.Drawing drawing = wmlFactory.createDrawing();

				// ------------------------------------------------
				// 6. Add wp:inline into w:drawing
				// ------------------------------------------------

				drawing.getAnchorOrInline().add(inline);

				// ------------------------------------------------
				// 7. Add drawing into run
				// ------------------------------------------------

				run.getContent().add(drawing);

				// ------------------------------------------------
				// 8. Add run into paragraph
				// ------------------------------------------------

				paragraph.getContent().add(run);

				System.out.println("QR Code inserted successfully");

				break;
			}
		}
	}*/
	
	private void insertQrCode(WordprocessingMLPackage wordMLPackage, byte[] qrImageBytes) throws Exception {

		MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, qrImageBytes);

		Inline inline = imagePart.createImageInline("QRCode.png", "Policy QR Code", 1, 2, false);

		long width = 70L * 9525L;
		long height = 70L * 9525L;

		inline.getExtent().setCx(width);
		inline.getExtent().setCy(height);

		org.docx4j.wml.ObjectFactory wmlFactory = new org.docx4j.wml.ObjectFactory();

		R run = wmlFactory.createR();

		org.docx4j.wml.Drawing drawing = wmlFactory.createDrawing();

		drawing.getAnchorOrInline().add(inline);

		run.getContent().add(drawing);

		boolean found = replaceQrPlaceholder(mainDocumentPart.getContent(), run);

		if (found) {
			System.out.println("QR Code inserted successfully");
		} else {
			System.out.println("QR_CODE_PLACEHOLDER not found");
		}
	}

	private String getParagraphText(P paragraph) {

		StringBuilder text = new StringBuilder();

		for (Object obj : paragraph.getContent()) {

			Object unwrapped = XmlUtils.unwrap(obj);

			if (unwrapped instanceof R) {

				R run = (R) unwrapped;

				for (Object runObj : run.getContent()) {

					Object runContent = XmlUtils.unwrap(runObj);

					if (runContent instanceof org.docx4j.wml.Text) {

						org.docx4j.wml.Text textElement = (org.docx4j.wml.Text) runContent;

						text.append(textElement.getValue());
					}
				}
			}
		}

		return text.toString();
	}

	private List<ListItemValue> getListValues(String companyId, String branchCode) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();

		try {
			String itemType = "ARABIC_CONTENT";
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			effectiveDate.where(a1, a2, b1, b2);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate b4 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			effectiveDate2.where(a3, a4, b3, b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			query.where(n1, n2, n3, n8, n9, n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode())))
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue::getItemValue));

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	private static String nvl(Object value) {
		return value == null ? "N/A" : String.valueOf(value);
	}

	private static String nvlNumeric(Object value) {
		return value == null ? "0.0" : String.valueOf(value);
	}

	/*
	 * public byte[] convertDocxToPdf(WordprocessingMLPackage wordMLPackage) throws
	 * Exception {
	 * 
	 * ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
	 * 
	 * Docx4J.toPDF(wordMLPackage, pdfOut);
	 * 
	 * return pdfOut.toByteArray(); }
	 */

	public byte[] convertDocxToPdf(Path docxPath) throws Exception {

		Path outputDirectory = docxPath.getParent();

		ProcessBuilder processBuilder = new ProcessBuilder(libreOfficePath, "--headless", "--convert-to", "pdf",
				"--outdir", outputDirectory.toString(), docxPath.toString());

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		String output = new String(process.getInputStream().readAllBytes());

		int exitCode = process.waitFor();

		System.out.println("LibreOffice output: " + output);

		if (exitCode != 0) {
			throw new RuntimeException("LibreOffice conversion failed. Exit code: " + exitCode);
		}

		String pdfFileName = docxPath.getFileName().toString().replaceFirst("(?i)\\.docx$", ".pdf");

		Path pdfPath = outputDirectory.resolve(pdfFileName);

		if (!Files.exists(pdfPath)) {
			throw new FileNotFoundException("PDF was not generated: " + pdfPath);
		}

		return Files.readAllBytes(pdfPath);
	}
	
	public List<Map<String, Object>> getConditionListSectionCover(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> conditionList = new ArrayList<Map<String, Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> conditionRes = new ArrayList<>();

			for (int i = 1; i <= 2; i++) {
				CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<>();
				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot2.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot2.get("quoteNo"), quoteNo));
				}

				if (i == 1 ) {

					Subquery<Tuple> CquoteIn = cq2.subquery(Tuple.class);
					Root<TermsAndCondition> StacRoot = CquoteIn.from(TermsAndCondition.class);

					CquoteIn.select(StacRoot.get("quoteNo")).where(cb.equal(StacRoot.get("quoteNo"), quoteNo),cb.equal(StacRoot.get("id"), "6"));

					Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

						predicates.add(cb.equal(cmRoot2.get("sectionId"), "99999"));

					} else {
						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
					//	predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")),cb.equal(cmRoot2.get("sectionId"), "99999")));
						predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")),cb.equal(cmRoot2.get("sectionId"), "99999")));
					}

					cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"),
							cmRoot2.get("sectionId").alias("sectionId"), cmRoot2.get("clausesId").alias("clausesId"),
							cmRoot2.get("clausesShortDesc").alias("conditionTitle"),
							cmRoot2.get("pdfLocation").alias("pdfLocation"), cmRoot2.get("pdfName").alias("pdfName"));

					predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
					predicates.add(cb.equal(cmRoot2.get("productId").as(String.class),
							hpmRoot2.get("productId").as(String.class)));

					predicates.add(cb.or(cb.equal(cmRoot2.get("branchCode"), hpmRoot2.get("branchCode")),
							cb.equal(cmRoot2.get("branchCode"), "99999")));

					predicates.add(cb.between(cb.literal(new Date()), cmRoot2.get("effectiveDateStart"),
							cmRoot2.get("effectiveDateEnd")));

					predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
					predicates.add(cb.equal(cmRoot2.get("typeId"), "D"));
					predicates.add(cb.not(cb.in(hpmRoot2.get("quoteNo")).value(CquoteIn)));

				} else {

					Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
					if (sectionIds != null && !sectionIds.isEmpty()) {

					//	predicates.add(cb.or(tacRoot2.get("sectionId").in(sectionIds),cb.equal(tacRoot2.get("sectionId"), "99999")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), sddRoot2.get("sectionId")));
					}

					cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"),
							tacRoot2.get("sectionId").alias("sectionId"), tacRoot2.get("sno").alias("clausesId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),
							cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));

					predicates.add(cb.equal(tacRoot2.get("productId").as(String.class),
							hpmRoot2.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot2.get("quoteNo")).value(tacRoot2.get("quoteNo")));

					predicates.add(cb.equal(tacRoot2.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")),
							cb.equal(tacRoot2.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot2.get("id"), "6"));
				}

				Predicate[] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);

				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
			}

			conditionList = conditionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Cmap = new LinkedHashMap<String, Object>();

				Cmap.put("conditionTerms",
						c.get("conditionTerms") == null ? ""
								: c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Cmap.put("title",
						c.get("conditionTitle") == null ? ""
								: c.get("conditionTitle").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Cmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Cmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {
			log.info("Error in getConditionListSectionCover ==> " + e.getMessage());
			e.printStackTrace();
		}

		return conditionList;
	}
	public List<Map<String, Object>> getWarrantyDescriptionSectionCover(String policyNo,
			String quoteNo, Set<String> sectionIds) {

		List<Map<String, Object>> warrantyList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();

			for (int i = 1; i <= 2; i++) {

				CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<>();

				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot.get("quoteNo"), quoteNo));
				}

				// MASTER WARRANTY
				if (i == 1) {

					Subquery<Tuple> WquoteIn = cq.subquery(Tuple.class);
					Root<TermsAndCondition> StacRoot = WquoteIn.from(TermsAndCondition.class);

					WquoteIn.select(StacRoot.get("quoteNo")).where(cb.equal(StacRoot.get("quoteNo"), quoteNo),
							cb.equal(StacRoot.get("id"), "4"));

					Root<WarrantyMaster> wmRoot = cq.from(WarrantyMaster.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(wmRoot.get("sectionId").in(sectionIds),
//								cb.equal(wmRoot.get("sectionId"), "99999")));
						predicates.add(cb.equal(wmRoot.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

						predicates.add(cb.or(cb.equal(wmRoot.get("sectionId"), sddRoot.get("sectionId")),
								cb.equal(wmRoot.get("sectionId"), "99999")));
					}

					cq.multiselect(wmRoot.get("warrantyDescription").alias("warrantyTerms"),
							wmRoot.get("sectionId").alias("sectionId"), wmRoot.get("warrantyId").alias("warrantyId"),
							wmRoot.get("pdfLocation").alias("pdfLocation"), wmRoot.get("pdfName").alias("pdfName"));

					predicates.add(cb.equal(wmRoot.get("companyId"), hpmRoot.get("companyId")));

					predicates.add(cb.equal(wmRoot.get("productId").as(String.class),
							hpmRoot.get("productId").as(String.class)));

					predicates.add(cb.or(cb.equal(wmRoot.get("branchCode"), hpmRoot.get("branchCode")),
							cb.equal(wmRoot.get("branchCode"), "99999")));

					predicates.add(cb.between(cb.literal(new Date()), wmRoot.get("effectiveDateStart"),
							wmRoot.get("effectiveDateEnd")));

					predicates.add(cb.equal(wmRoot.get("status"), "Y"));
					predicates.add(cb.equal(wmRoot.get("typeId"), "D"));

					predicates.add(cb.not(cb.in(hpmRoot.get("quoteNo")).value(WquoteIn)));

					Predicate[] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);

					warrantyRes.addAll(em.createQuery(cq.where(predicatArray)).getResultList());

				} else {

					// TERMS AND CONDITION WARRANTY
					Root<TermsAndCondition> tacRoot = cq.from(TermsAndCondition.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot.get("sectionId"), "99999")));
						
						predicates.add(cb.equal(tacRoot.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

						predicates.add(cb.equal(tacRoot.get("sectionId"), sddRoot.get("sectionId")));
					}

					cq.multiselect(tacRoot.get("subIdDesc").alias("warrantyTerms"),
							tacRoot.get("sectionId").alias("sectionId"), tacRoot.get("sno").alias("warrantyId"),
							cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot.get("companyId"), hpmRoot.get("companyId")));

					predicates.add(cb.equal(tacRoot.get("productId").as(String.class),
							hpmRoot.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot.get("quoteNo")).value(tacRoot.get("quoteNo")));

					predicates.add(cb.equal(tacRoot.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot.get("branchCode"), hpmRoot.get("branchCode")),
							cb.equal(tacRoot.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot.get("id"), "4"));

					Predicate[] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);

					warrantyRes.addAll(em.createQuery(cq.where(predicatArray)).getResultList());
				}
			}

			warrantyList = warrantyRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Wmap = new LinkedHashMap<>();

				Wmap.put("conditionTerms",
						c.get("warrantyTerms") == null ? ""
								: c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Wmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Wmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Wmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Wmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getWarrantyDescriptionSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return warrantyList;
	}
	
	public List<Map<String, Object>> getExclusionListSectionCover(String policyNo,String quoteNo, Set<String> sectionIds) {

		List<Map<String, Object>> exclusionList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			for (int i = 1; i <= 2; i++) {

				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<>();

				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot3.get("quoteNo"), quoteNo));
				}

				// EXCLUSION MASTER
				if (i == 1) {

					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);

					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);

					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), quoteNo),
							cb.equal(SEtacRoot.get("id"), "7"));

					Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(emRoot3.get("sectionId").in(sectionIds),
//								cb.equal(emRoot3.get("sectionId"), "99999")));
						predicates.add(cb.equal(emRoot3.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));

						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId")),
								cb.equal(emRoot3.get("sectionId"), "99999")));
					}

					cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"),

							emRoot3.get("sectionId").alias("sectionId"),

							emRoot3.get("exclusionId").alias("exclusionId"),

							emRoot3.get("pdfLocation").alias("pdfLocation"),

							emRoot3.get("pdfName").alias("pdfName"));

					predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));

					predicates.add(cb.equal(emRoot3.get("productId").as(String.class),
							hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.or(cb.equal(emRoot3.get("branchCode"), hpmRoot3.get("branchCode")),
							cb.equal(emRoot3.get("branchCode"), "99999")));

					predicates.add(cb.between(cb.literal(new Date()), emRoot3.get("effectiveDateStart"),
							emRoot3.get("effectiveDateEnd")));

					predicates.add(cb.equal(emRoot3.get("status"), "Y"));
					predicates.add(cb.equal(emRoot3.get("typeId"), "D"));

					predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));

					Predicate[] predicatArray = new Predicate[predicates.size()];

					predicates.toArray(predicatArray);

					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());

				} else {

					// TERMS AND CONDITION EXCLUSION
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot3.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot3.get("sectionId"), "99999")));
						
						predicates.add(cb.equal(tacRoot3.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));

						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
					}

					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),

							tacRoot3.get("sectionId").alias("sectionId"),

							tacRoot3.get("sno").alias("exclusionId"),

							cb.nullLiteral(String.class).alias("conditionTitle"),

							cb.nullLiteral(String.class).alias("pdfLocation"),

							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));

					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class),
							hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));

					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")),
							cb.equal(tacRoot3.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot3.get("id"), "7"));

					Predicate[] predicatArray = new Predicate[predicates.size()];

					predicates.toArray(predicatArray);

					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}

			exclusionList = exclusionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Emap = new LinkedHashMap<>();

				Emap.put("conditionTerms",
						c.get("exclusionTerms") == null ? ""
								: c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Emap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Emap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Emap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Emap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getExclusionListSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return exclusionList;
	}
	
	public List<Map<String, Object>> getConditionListSectionCoverTermsOnly(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> conditionList = new ArrayList<Map<String, Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> conditionRes = new ArrayList<>();
			CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
			List<Predicate> predicates = new ArrayList<>();
				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot2.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot2.get("quoteNo"), quoteNo));
				}
				Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
					if (sectionIds != null && !sectionIds.isEmpty()) {

					//	predicates.add(cb.or(tacRoot2.get("sectionId").in(sectionIds),cb.equal(tacRoot2.get("sectionId"), "99999")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), sddRoot2.get("sectionId")));
					}

					cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"),
							tacRoot2.get("sectionId").alias("sectionId"), tacRoot2.get("sno").alias("clausesId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),
							cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));

					predicates.add(cb.equal(tacRoot2.get("productId").as(String.class),
							hpmRoot2.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot2.get("quoteNo")).value(tacRoot2.get("quoteNo")));

					predicates.add(cb.equal(tacRoot2.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")),
							cb.equal(tacRoot2.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot2.get("id"), "6"));
				
				Predicate[] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);

				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
			

			conditionList = conditionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Cmap = new LinkedHashMap<String, Object>();

				Cmap.put("conditionTerms",
						c.get("conditionTerms") == null ? ""
								: c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Cmap.put("title",
						c.get("conditionTitle") == null ? ""
								: c.get("conditionTitle").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Cmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Cmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {
			log.info("Error in getConditionListSectionCover ==> " + e.getMessage());
			e.printStackTrace();
		}

		return conditionList;
	}

	public List<Map<String, Object>> getWarrantyDescriptionSectionCoverTermsOnly(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> warrantyList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);

			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.isNotBlank(policyNo)) {
				predicates.add(cb.equal(hpmRoot.get("policyNo"), policyNo));
			} else {
				predicates.add(cb.equal(hpmRoot.get("quoteNo"), quoteNo));
			}

			Root<TermsAndCondition> tacRoot = cq.from(TermsAndCondition.class);

			// SECTION ID IN (...)
			if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot.get("sectionId"), "99999")));

				predicates.add(cb.equal(tacRoot.get("sectionId"), "99999"));

			} else {

				Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);

				predicates.add(cb.equal(sddRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

				predicates.add(cb.equal(tacRoot.get("sectionId"), sddRoot.get("sectionId")));
			}

			cq.multiselect(tacRoot.get("subIdDesc").alias("warrantyTerms"), tacRoot.get("sectionId").alias("sectionId"),
					tacRoot.get("sno").alias("warrantyId"), cb.nullLiteral(String.class).alias("pdfLocation"),
					cb.nullLiteral(String.class).alias("pdfName"));

			predicates.add(cb.equal(tacRoot.get("companyId"), hpmRoot.get("companyId")));

			predicates.add(
					cb.equal(tacRoot.get("productId").as(String.class), hpmRoot.get("productId").as(String.class)));

			predicates.add(cb.in(hpmRoot.get("quoteNo")).value(tacRoot.get("quoteNo")));

			predicates.add(cb.equal(tacRoot.get("status"), "Y"));

			predicates.add(cb.or(cb.equal(tacRoot.get("branchCode"), hpmRoot.get("branchCode")),
					cb.equal(tacRoot.get("branchCode"), "99999")));

			predicates.add(cb.equal(tacRoot.get("id"), "4"));

			Predicate[] predicatArray = new Predicate[predicates.size()];
			predicates.toArray(predicatArray);

			warrantyRes.addAll(em.createQuery(cq.where(predicatArray)).getResultList());

			warrantyList = warrantyRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Wmap = new LinkedHashMap<>();

				Wmap.put("conditionTerms",
						c.get("warrantyTerms") == null ? ""
								: c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Wmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Wmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Wmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Wmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getWarrantyDescriptionSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return warrantyList;
	}
	
	public List<Map<String, Object>> getExclusionListSectionCoverTermsOnly(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> exclusionList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.isNotBlank(policyNo)) {
				predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
			} else {
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), quoteNo));
			}
			Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
			if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot3.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot3.get("sectionId"), "99999")));

				predicates.add(cb.equal(tacRoot3.get("sectionId"), "99999"));

			} else {

				Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);

				predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));

				predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
			}

			cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),

					tacRoot3.get("sectionId").alias("sectionId"),

					tacRoot3.get("sno").alias("exclusionId"),

					cb.nullLiteral(String.class).alias("conditionTitle"),

					cb.nullLiteral(String.class).alias("pdfLocation"),

					cb.nullLiteral(String.class).alias("pdfName"));

			predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));

			predicates.add(
					cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));

			predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));

			predicates.add(cb.equal(tacRoot3.get("status"), "Y"));

			predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")),
					cb.equal(tacRoot3.get("branchCode"), "99999")));

			predicates.add(cb.equal(tacRoot3.get("id"), "7"));

			Predicate[] predicatArray = new Predicate[predicates.size()];

			predicates.toArray(predicatArray);

			exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());

			exclusionList = exclusionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Emap = new LinkedHashMap<>();

				Emap.put("conditionTerms",
						c.get("exclusionTerms") == null ? ""
								: c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Emap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Emap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Emap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Emap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getExclusionListSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return exclusionList;
	}
	public List<Map<String,Object>> getConditionListCover(String policyNo,String QuoteNo, String sectionId ,String coverId,Integer loc){
		List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
		try {
			List<String> sect= new ArrayList<>();
			sect.add(sectionId);
			List<String> cover= new ArrayList<>();
			cover.add(coverId);
			List<TermsAndCondition> tClist = termsConRepo.findByQuoteNoAndSectionIdInAndCoverIdInAndIdAndLocationId(QuoteNo,sect,cover,6,loc.toString());
			if(tClist != null && !tClist.isEmpty())
			{
				 conditionList = tClist.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.getSubIdDesc());
					Cmap.put("SectionId", c.getSectionId());
					Cmap.put("title", "Clauses");
					return Cmap;
				}).collect(Collectors.toList());
				 return conditionList;
			}
			else
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();		
				List<Tuple> conditionRes = new ArrayList<>();
				CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();

				predicates.add(cb.equal(hpmRoot2.get("quoteNo"), QuoteNo));
				
				Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);
				if(StringUtils.isNotBlank(sectionId)) {
					predicates.add(cb.equal(cmRoot2.get("sectionId"), Integer.valueOf(sectionId)));
					predicates.add(cb.equal(cmRoot2.get("coverId"), coverId));
				//	predicates.add(cb.equal(cmRoot2.get("coverId"), Integer.valueOf(coverId)));
				//	predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sectionId), cb.equal(cmRoot2.get("sectionId"), "99999")));
					
					predicates.add(cb.or(cb.equal(cmRoot2.get("coverId"), Integer.valueOf(coverId)),cb.equal(cmRoot2.get("coverId"), Integer.valueOf("99999"))));
				}else {
					Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
					predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
					//predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")), cb.equal(cmRoot2.get("sectionId"), "99999")));
					//predicates.add(cb.or(cb.equal(cmRoot2.get("coverId"), sddRoot2.get("coverId")), cb.equal(cmRoot2.get("coverId"), Integer.valueOf("99999"))));
					predicates.add(cb.equal(cmRoot2.get("coverId"), sddRoot2.get("coverId")));
					predicates.add(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")));
				}
				cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"),cmRoot2.get("sectionId").alias("sectionId"),cmRoot2.get("clausesId").alias("clausesId"),
						cmRoot2.get("clausesShortDesc").alias("conditionTitle"),cmRoot2.get("pdfLocation").alias("pdfLocation"),
						cmRoot2.get("pdfName").alias("pdfName"));
				predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
				predicates.add(cb.equal(cmRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
				predicates.add(cb.or(cb.equal(cmRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(cmRoot2.get("branchCode"), "99999")));
				predicates.add(cb.between(cb.literal(new Date()), cmRoot2.get("effectiveDateStart"), cmRoot2.get("effectiveDateEnd")));
				predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
				predicates.add(cb.equal(cmRoot2.get("typeId"), "D"));
				Predicate [] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);
				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
				conditionList = conditionRes.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.get("conditionTerms")==null?"":c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
					Cmap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
					Cmap.put("title", c.get("conditionTitle")==null?"":c.get("conditionTitle").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
					Cmap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
					Cmap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
					return Cmap;
				}).collect(Collectors.toList());
			}
			
		}catch(Exception e) {
			log.info("Error in getConditionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return conditionList;
	}

	public List<Map<String,Object>> getWarrantyDescriptionCover(String policyNo,String QuoteNo, String sectionId,String coverId,Integer loc){
		List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
		try {
			List<String> sect= new ArrayList<>();
			sect.add(sectionId);
			List<String> cover= new ArrayList<>();
			cover.add(coverId);
			List<TermsAndCondition> tClist = termsConRepo.findByQuoteNoAndSectionIdInAndCoverIdInAndIdAndLocationId(QuoteNo,sect,cover,4,loc.toString());
			if(tClist != null && !tClist.isEmpty())
			{
				warrantyList = tClist.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.getSubIdDesc());
					Cmap.put("SectionId", c.getSectionId());
					Cmap.put("title", "Clauses");
					return Cmap;
				}).collect(Collectors.toList());
				 return warrantyList;
			}else
			{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();

			
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				
			
					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "4"));
					Root<WarrantyMaster> wmRoot3 = cq3.from(WarrantyMaster.class);
					cq3.multiselect(wmRoot3.get("warrantyDescription").alias("warrantyTerms"),wmRoot3.get("sectionId").alias("sectionId"),wmRoot3.get("warrantyId").alias("warrantyId"),
							wmRoot3.get("pdfLocation").alias("pdfLocation"),wmRoot3.get("pdfName").alias("pdfName"));
					predicates.add(cb.equal(wmRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(wmRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
//					predicates.add(cb.or(cb.equal(wmRoot3.get("sectionId"), sectionId), cb.equal(wmRoot3.get("sectionId"), "99999")));
//					predicates.add(cb.or(cb.equal(wmRoot3.get("coverId"), coverId), cb.equal(wmRoot3.get("coverId"), "99999")));
					predicates.add(cb.equal(wmRoot3.get("sectionId"), Integer.valueOf(sectionId)));
					predicates.add(cb.equal(wmRoot3.get("coverId"), Integer.valueOf(coverId)));
					predicates.add(cb.or(cb.equal(wmRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(wmRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), wmRoot3.get("effectiveDateStart"), wmRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(wmRoot3.get("status"), "Y"));
					predicates.add(cb.equal(wmRoot3.get("typeId"), "D"));
			//		predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
		warrantyList = warrantyRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("Sno", c.get("warrantyId")==null?"":c.get("warrantyId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}
		}
		catch(Exception e) {
			log.info("Error in getWarrantyDescription ==> "+e.getMessage());
			e.printStackTrace();
		}
		return warrantyList;

	}

	public List<Map<String,Object>> getExclusionListCover(String policyNo,String QuoteNo,String sectionId,String coverId,Integer loc){
		List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
		try {
			List<String> sect= new ArrayList<>();
			sect.add(sectionId);
			List<String> cover= new ArrayList<>();
			cover.add(coverId);
			List<TermsAndCondition> tClist = termsConRepo.findByQuoteNoAndSectionIdInAndCoverIdInAndIdAndLocationId(QuoteNo,sect,cover,7,loc.toString());
			if(tClist != null && !tClist.isEmpty())
			{
				exclusionList = tClist.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.getSubIdDesc());
					Cmap.put("SectionId", c.getSectionId());
					Cmap.put("title", "Clauses");
					return Cmap;
				}).collect(Collectors.toList());
				 return exclusionList;
			}
			else {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				
				if(i == 1) {

					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "7"));

					Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.equal(emRoot3.get("sectionId"), Integer.valueOf(sectionId)));
						predicates.add(cb.equal(emRoot3.get("coverId"), Integer.valueOf(coverId)));
//						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sectionId), cb.equal(emRoot3.get("sectionId"), "99999")));
//						predicates.add(cb.or(cb.equal(emRoot3.get("coverId"), coverId), cb.equal(emRoot3.get("coverId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId"))
								, cb.equal(emRoot3.get("sectionId"), "99999")));
						predicates.add(cb.equal(emRoot3.get("sectionId"), Integer.valueOf(sectionId)));
						//predicates.add(cb.or(cb.equal(emRoot3.get("coverId"), sddRoot3.get("coverId")), cb.equal(emRoot3.get("coverId"), "99999")));
						predicates.add(cb.equal(emRoot3.get("coverId"), sddRoot3.get("coverId")));
					}
					cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"),emRoot3.get("sectionId").alias("sectionId"),emRoot3.get("exclusionId").alias("exclusionId"),
							emRoot3.get("pdfLocation").alias("pdfLocation"),emRoot3.get("pdfName").alias("pdfName"));
					predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(emRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(emRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(emRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), emRoot3.get("effectiveDateStart"), emRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(emRoot3.get("status"), "Y"));
					predicates.add(cb.equal(emRoot3.get("typeId"), "D"));
				//	predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
				
			}
			exclusionList = exclusionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return exclusionList;
	}
	public List<Map<String, Object>> getExcessListCover(HomePositionMaster home, String secId, String coverId,Integer locId) {
		List<Map<String,Object>> excessList = new ArrayList<Map<String,Object>>();
		try {
			List<ExcessTransactionDetails> etrList =extransactionRepo.findByRequestReferenceNoAndProductIdAndSectionIdAndCoverId(home.getRequestReferenceNo(),home.getProductId().toString(),secId,coverId);
			if (etrList != null && !etrList.isEmpty()) {
				etrList.forEach(e_l -> {
					Map<String, Object> e = new HashMap<>();
					e.put("excessName", e_l.getCoverName() == null ? "" : e_l.getCoverName());
					e.put("ExcessDescription",
							e_l.getExcessDescription() == null ? "" : e_l.getExcessDescription());
					e.put("ExcessPercentage",
							e_l.getExcessPercentage() == null ? 0 : e_l.getExcessPercentage());
					e.put("ExcessAmount",
							e_l.getExcessAmount() == null ? 0.00 : e_l.getExcessAmount());
					excessList.add(e);
				});
		}
			else {
				List<ExcessMaster> excess = excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
						home.getCompanyId(), home.getProductId().toString(),
						secId.toString(), coverId.toString());
				if (excess != null && !excess.isEmpty()) {
					excess.forEach(e_l -> {
						Map<String, Object> e = new HashMap<>();
						e.put("excessName", e_l.getCoverName() == null ? "" : e_l.getCoverName());
						e.put("ExcessDescription",
								e_l.getExcessDescription() == null ? "" : e_l.getExcessDescription());
						e.put("ExcessPercentage",
								e_l.getExcessPercentage() == null ? 0 : e_l.getExcessPercentage());
						e.put("ExcessAmount",
								e_l.getExcessAmount() == null ? 0.00 : e_l.getExcessAmount());
						excessList.add(e);
					});
			}
			}
				
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return excessList;
	}
	
	public List<Map<String, Object>> getConditionListCoverTermsOnly(String policyNo,String QuoteNo, String sectionId ,String coverId,Integer loc) {
		List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
		try {
			List<String> sect= new ArrayList<>();
			sect.add(sectionId);
			List<String> cover= new ArrayList<>();
			cover.add(coverId);
			List<TermsAndCondition> tClist = termsConRepo.findByQuoteNoAndSectionIdInAndCoverIdInAndIdAndLocationId(QuoteNo,sect,cover,6,loc.toString());
			if(tClist != null && !tClist.isEmpty())
			{
				 conditionList = tClist.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.getSubIdDesc());
					Cmap.put("SectionId", c.getSectionId());
					Cmap.put("title", "Clauses");
					return Cmap;
				}).collect(Collectors.toList());
				 return conditionList;
			}
		
		}catch(Exception e) {
			log.info("Error in getConditionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return conditionList;
	}
	
	public List<Map<String, Object>> getWarrantyDescriptionCoverTermsOnly(String policyNo,String QuoteNo, String sectionId,String coverId,Integer loc) {
		List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();

			
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
				cq3.multiselect(tacRoot3.get("subIdDesc").alias("warrantyTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("warrantyId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.equal(tacRoot3.get("sectionId"), sectionId));
					predicates.add(cb.equal(tacRoot3.get("coverId"),coverId));
					predicates.add(cb.equal(tacRoot3.get("locationId"),loc.toString()));
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "4"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
					warrantyList = warrantyRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("Sno", c.get("warrantyId")==null?"":c.get("warrantyId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getWarrantyDescription ==> "+e.getMessage());
			e.printStackTrace();
		}
		return warrantyList;

	}

	public List<Map<String, Object>> getExclusionListCoverTermsOnly(String policyNo,String QuoteNo,String sectionId,String coverId,Integer loc) {
		List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				
				
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					if(StringUtils.isNotBlank(sectionId)) {
//						predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
//						predicates.add(cb.or(cb.equal(tacRoot3.get("coverId"), coverId), cb.equal(tacRoot3.get("coverId"), "99999")));
						
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sectionId));
						predicates.add(cb.equal(tacRoot3.get("coverId"), coverId));
						predicates.add(cb.equal(tacRoot3.get("locationId"), loc.toString()));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
						predicates.add(cb.equal(tacRoot3.get("coverId"), sddRoot3.get("coverId")));
					}

					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("exclusionId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "7"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				
			
			exclusionList = exclusionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return exclusionList;
	}

	public List<AttachMentRes> getAttachMentList(String companyId,String productId,String itemType,String motorusage){
	    List<AttachMentRes> attachments = new ArrayList<>();
	    try {
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
	        Root<ListItemValue> dudRoot = cq.from(ListItemValue.class);

	        Subquery<Integer> amdMax = cq.subquery(Integer.class);
	        Root<ListItemValue> aSub = amdMax.from(ListItemValue.class);

	        amdMax.select(aSub.get("amendId"))
	        .where(cb.equal(aSub.get("itemType"), dudRoot.get("itemType")),
	                cb.equal(aSub.get("companyId"), dudRoot.get("companyId")),
	                cb.equal(aSub.get("param1"), dudRoot.get("param1")),
	                StringUtils.isNotBlank(motorusage)?cb.like(aSub.get("param2"), "%"+motorusage+"%"):cb.conjunction());

	        cq.multiselect(dudRoot.get("itemCode").alias("docRefNo"),dudRoot.get("itemValue").alias("filePathOrginal"))
	        .where(cb.equal(dudRoot.get("itemType"), itemType),
	                cb.equal(dudRoot.get("companyId"), companyId),
	                cb.equal(dudRoot.get("param1"), productId),
	                cb.equal(dudRoot.get("amendId"), amdMax),
	                StringUtils.isNotBlank(motorusage)?cb.like(dudRoot.get("param2"), "%"+motorusage+"%"):cb.conjunction())
	        .distinct(true);  

	        List<Tuple> docList = em.createQuery(cq).getResultList();
	        if(!docList.isEmpty()) {
	            docList.forEach(
	            		e -> {
	                AttachMentRes m = AttachMentRes.builder()
	                        .docRefNo(e.get("docRefNo")==null?"":e.get("docRefNo").toString())
	                        .docloction(e.get("filePathOrginal")==null?"":e.get("filePathOrginal").toString())
	                        .build();
	                attachments.add(m);
	            });
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return attachments;
	}
	
	
	private void insertOmrSymbol(WordprocessingMLPackage wordMLPackage, byte[] omrImageBytes) throws Exception {

		MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, omrImageBytes);

		List<Object> paragraphs = getAllParagraphs(mainDocumentPart.getContent());

		System.out.println("Total paragraphs: " + paragraphs.size());

		for (Object paragraphObj : paragraphs) {

			Object unwrapped = XmlUtils.unwrap(paragraphObj);

			if (!(unwrapped instanceof P)) {
				continue;
			}

			P paragraph = (P) unwrapped;

			String paragraphText = getParagraphText(paragraph);

			if (paragraphText == null || !paragraphText.contains("OMR")) {
				continue;
			}

			System.out.println("OMR found in paragraph: " + paragraphText);

			/*
			 * Process each run separately.
			 */
			List<Object> content = paragraph.getContent();

			for (int i = 0; i < content.size(); i++) {

				Object obj = XmlUtils.unwrap(content.get(i));

				if (!(obj instanceof R)) {
					continue;
				}

				R run = (R) obj;

				List<Object> runContent = run.getContent();

				for (int j = 0; j < runContent.size(); j++) {

					Object runObj = XmlUtils.unwrap(runContent.get(j));

					if (!(runObj instanceof Text)) {
						continue;
					}

					Text text = (Text) runObj;

					String value = text.getValue();

					if (value == null || !value.contains("OMR")) {
						continue;
					}

					System.out.println("OMR found in run: " + value);

					/*
					 * Split:
					 *
					 * OMR 2,000/-
					 *
					 * into:
					 *
					 * before = "" after = " 2,000/-"
					 */
					String before = value.substring(0, value.indexOf("OMR"));

					String after = value.substring(value.indexOf("OMR") + 3);

					/*
					 * Create image inline.
					 */
					Inline inline = imagePart.createImageInline("OMR.png", "Omani Rial", 1, 2, false);

					/*
					 * OMR symbol size.
					 *
					 * Start with 12 x 12. We can adjust later.
					 */
					long width = 10L * 9525L;
					long height = 10L * 9525L;

					inline.getExtent().setCx(width);
					inline.getExtent().setCy(height);

					/*
					 * Create image run.
					 */
					org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();

					R imageRun = factory.createR();

					org.docx4j.wml.Drawing drawing = factory.createDrawing();

					drawing.getAnchorOrInline().add(inline);

					imageRun.getContent().add(drawing);

					/*
					 * Create text before OMR, if any.
					 */
					if (!before.isEmpty()) {

						Text beforeText = factory.createText();
						beforeText.setValue(before);

						R beforeRun = factory.createR();
						beforeRun.getContent().add(beforeText);

						content.add(i, beforeRun);
						i++;
					}

					/*
					 * Add image run.
					 */
					content.add(i, imageRun);
					i++;

					/*
					 * Add remaining text:
					 *
					 * " 2,000/-"
					 */
					if (!after.isEmpty()) {

						Text afterText = factory.createText();
						afterText.setValue("  " + after);

						R afterRun = factory.createR();
						afterRun.getContent().add(afterText);

						content.add(i, afterRun);
						i++;
					}

					/*
					 * Remove original OMR text run.
					 */
					content.remove(i);

					break;
				}
			}
		}
	}
	
	private boolean replaceQrPlaceholder(
	        List<Object> content,
	        R qrRun) {

	    for (int i = 0; i < content.size(); i++) {

	        Object obj = XmlUtils.unwrap(content.get(i));

	        if (obj instanceof P) {

	            P paragraph = (P) obj;

	            String paragraphText = getParagraphText(paragraph);

	            if (paragraphText != null &&
	                    paragraphText.contains("QR_CODE_PLACEHOLDER")) {

	                paragraph.getContent().clear();
	                paragraph.getContent().add(qrRun);

	                return true;
	            }
	        }

	        /*
	         * Recursively search tables, cells, etc.
	         */
	        if (obj instanceof ContentAccessor) {

	            ContentAccessor contentAccessor =
	                    (ContentAccessor) obj;

	            if (replaceQrPlaceholder(
	                    contentAccessor.getContent(),
	                    qrRun)) {

	                return true;
	            }
	        }
	    }

	    return false;
	}
	
	private List<Object> getAllParagraphs(List<Object> content) {

	    List<Object> paragraphs = new ArrayList<>();

	    for (Object obj : content) {

	        Object unwrapped = XmlUtils.unwrap(obj);

	        if (unwrapped instanceof P) {

	            paragraphs.add(unwrapped);
	        }

	        if (unwrapped instanceof ContentAccessor) {

	            ContentAccessor accessor =
	                    (ContentAccessor) unwrapped;

	            paragraphs.addAll(
	                    getAllParagraphs(accessor.getContent()));
	        }
	    }

	    return paragraphs;
	}


}
