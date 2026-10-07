package com.maan.eway.reinsurance.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EwayDivisionDepartment;
import com.maan.eway.bean.EwayLobMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.ReInsuranceCoverDetailsId;
import com.maan.eway.bean.ReInsuranceRiskDetails;
import com.maan.eway.bean.ReInsuranceRiskDetailsId;
import com.maan.eway.bean.ReinsuranceCoverDetails;
import com.maan.eway.bean.ReinsuranceDiscLoadDetails;
import com.maan.eway.bean.ReinsuranceDiscLoadDetailsId;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.reinsurance.req.ReInsuranceApprovedReq;
import com.maan.eway.reinsurance.req.ReInsuranceCoversDetail;
import com.maan.eway.reinsurance.req.ReInsuranceDisLoadDetails;
import com.maan.eway.reinsurance.req.ReInsuranceFreezeReq;
import com.maan.eway.reinsurance.req.ReInsurancePushReq;
import com.maan.eway.reinsurance.req.ReInsuranceQuoteReq;
import com.maan.eway.reinsurance.req.ReInsuranceRisksDetails;
import com.maan.eway.reinsurance.req.ReInsuranceSectionDetails;
import com.maan.eway.reinsurance.res.ReInsuranceCommonRes;
import com.maan.eway.reinsurance.res.ReInsuranceCoverDetail;
import com.maan.eway.reinsurance.res.ReInsuranceRiskDetail;
import com.maan.eway.reinsurance.res.ReInsuranceUnfreezeRes;
import com.maan.eway.reinsurance.res.SectionCoverRes;
import com.maan.eway.reinsurance.res.ViewReInsuranceDetails;
import com.maan.eway.reinsurance.service.ReinsuranceService;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EwayDivisionDepartmentRepository;
import com.maan.eway.repository.EwayLobMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.ReInsuranceRiskDetailsRepositiory;
import com.maan.eway.repository.ReinsuranceCoverDetailsRepository;
import com.maan.eway.repository.ReinsuranceDiscLoadDetailsRepository;
import com.maan.eway.repository.SectionCoverMasterRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@Transactional
public class ReinsuranceServiceImpl implements ReinsuranceService {

	private Logger log = LogManager.getLogger(ReinsuranceServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ReInsuranceRiskDetailsRepositiory reInsuranceRiskDetailsRepo;

	@Autowired
	private ReinsuranceCoverDetailsRepository reinsuranceCoverDetailsRepo;

	@Autowired
	private PolicyCoverDataRepository policyCoverDataRepo;

	@Autowired
	private HomePositionMasterRepository homePositionMasterRepo;

	@Autowired
	private SectionDataDetailsRepository sectionDataDetailsRepo;

	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingDetailsRepo;

	@Autowired
	private EserviceCommonDetailsRepository eserviceCommonDetailsRepo;

	@Autowired
	private CompanyProductMasterRepository companyProductMasterRepo;

	@Autowired
	private ProductSectionMasterRepository productSectionMasterRepo;

	@Autowired
	private SectionCoverMasterRepository sectionCoverMasterRepo;

	@Autowired
	private BranchMasterRepository branchMasterRepos;

	@Autowired
	private EndtTypeMasterRepository endtTypeMasterRepos;

	@Autowired
	private EwayLobMasterRepository ewayLobMasterRepos;

	@Autowired
	private EwayDivisionDepartmentRepository ewayDivisionDepartmentRepos;

	@Autowired
	private ReinsuranceDiscLoadDetailsRepository reinsuranceDiscLoadDetailsRepos;

	@Autowired
	private PersonalInfoRepository personalInfoRepository;

	@Autowired
	private LoginUserInfoRepository loginUserInfoRepository;

	@Autowired
	private EserviceCustomerDetailsRepository customerDetailsRepository;

	Gson json = new Gson();

	@Value(value = "${reinsurance.api}")
	private String reInsuranceApi;

	@Value(value = "${zambia.reinsurance.api}")
	private String zambiaReInsuranceApi;

	@Value(value = "${botswana.reinsurance.api}")
	private String botswanaReInsuranceApi;

	@Value(value = "${mozambique.reinsurance.api}")
	private String mozambiqueReInsuranceApi;

	@Value(value = "${swaziland.reinsurance.api}")
	private String swazilandReInsuranceApi;

	@Value(value = "${namibia.reinsurance.api}")
	private String namibiaReInsuranceApi;

	@Override
	public CommonRes insertReinsurance(ReInsuranceQuoteReq req) {
		CommonRes res = new CommonRes();
		try {
			List<ReInsuranceRiskDetails> reInsuranceRiskDetailsList = new ArrayList<>();
			List<ReinsuranceCoverDetails> reinsuranceCoverDetailList = new ArrayList<>();
			List<ReInsuranceRiskDetails> reInsuranceRiskListBK = new ArrayList<>();
			List<ReinsuranceCoverDetails> reinsuranceCoverListBK = new ArrayList<>();
			Date today = new Date();
			String address = null;

			List<ReinsuranceDiscLoadDetails> saveDisLoadDetails = new ArrayList<>();
			List<ReinsuranceCoverDetails> saveCoverDetails = new ArrayList<>();
			HomePositionMaster homePositionMaster = homePositionMasterRepo.findByQuoteNo(req.getQuoteNo());
			if (homePositionMaster == null) {
				homePositionMaster = homePositionMasterRepo
						.findByRequestReferenceNoOrderByNoOfVehiclesAsc(req.getRequestReferenceNo());
			}
			CompanyProductMaster companyProductMaster = companyProductMasterRepo.findByProductIdAndCompanyIdAndStatus(
					homePositionMaster.getProductId(), homePositionMaster.getCompanyId(), "Y");
			List<EndtTypeMaster> endtTypeMasterList = endtTypeMasterRepos.findByCompanyIdAndProductIdAndStatus(
					homePositionMaster.getCompanyId(), homePositionMaster.getProductId(), "Y");
			BranchMaster branchMaster = branchMasterRepos.findByBranchCodeAndCompanyIdAndStatus(
					homePositionMaster.getBranchCode(), homePositionMaster.getCompanyId(), "Y");
			List<SectionDataDetails> sectionDataDetailsList = sectionDataDetailsRepo.findByQuoteNo(req.getQuoteNo());

			EwayLobMaster ewayLobMaster = ewayLobMasterRepos.findByCompnayIdAndProductId(
					homePositionMaster.getCompanyId(), homePositionMaster.getProductId().toString());

			EwayDivisionDepartment ewayDivisionDepartment = ewayDivisionDepartmentRepos.findByCompnayIdAndProductId(
					homePositionMaster.getCompanyId(), homePositionMaster.getProductId().toString());

			List<ReInsuranceRiskDetails> reInsuranceRiskDetailsListBK = reInsuranceRiskDetailsRepo
					.findByQuoteno(req.getQuoteNo());
			List<ReinsuranceCoverDetails> reinsuranceCoverDetailsListBK = reinsuranceCoverDetailsRepo
					.findByQuoteno(req.getQuoteNo());

			List<ProductSectionMaster> productSectionMasterList = productSectionMasterRepo
					.findByProductIdAndCompanyId(homePositionMaster.getProductId(), homePositionMaster.getCompanyId());

			Map<Integer, ProductSectionMaster> reSectionMap = productSectionMasterList.stream()
					.collect(Collectors.groupingBy(ProductSectionMaster::getSectionId,
							Collectors.collectingAndThen(
									Collectors.maxBy(Comparator.comparingInt(ProductSectionMaster::getAmendId)),
									Optional::get)));

			reInsuranceRiskListBK = reInsuranceRiskDetailsListBK;
			reinsuranceCoverListBK = reinsuranceCoverDetailsListBK;

			reInsuranceRiskDetailsRepo.deleteAll(reInsuranceRiskDetailsListBK);
			reinsuranceCoverDetailsRepo.deleteAll(reinsuranceCoverDetailsListBK);

			List<EserviceBuildingDetails> eserviceBuildingDetailsList = eserviceBuildingDetailsRepo
					.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			List<EserviceCommonDetails> eserviceCommonDetailsList = eserviceCommonDetailsRepo
					.findByQuoteNo(req.getQuoteNo());
			if (eserviceBuildingDetailsList != null && !eserviceBuildingDetailsList.isEmpty()) {
				address = eserviceBuildingDetailsList.get(0).getAddress();
			} else {
				if (eserviceCommonDetailsList != null && !eserviceCommonDetailsList.isEmpty()) {
					address = eserviceCommonDetailsList.get(0).getAddress();
				}
			}

			List<ReInsuranceRiskDetails> saveRiskDetails = saveRiskDetails(req, reInsuranceRiskDetailsList,
					reInsuranceRiskListBK, today, address, homePositionMaster, companyProductMaster, endtTypeMasterList,
					branchMaster, sectionDataDetailsList, reSectionMap, ewayLobMaster, ewayDivisionDepartment);

			List<SectionCoverMaster> sectionCoverMasterList = sectionCoverMasterRepo
					.findByCompanyIdAndProductId(homePositionMaster.getCompanyId(), homePositionMaster.getProductId());

			List<PolicyCoverData> policyCoverDataList = policyCoverDataRepo.findByQuoteNo(req.getQuoteNo());
			if (homePositionMaster.getEndtTypeId() != null && !homePositionMaster.getEndtTypeId().isEmpty()) {

				List<PolicyCoverData> enPpolicyCoverList = policyCoverDataList.stream()
						.filter(o -> (o.getCoverageType().equalsIgnoreCase("E")
								|| o.getCoverageType().equalsIgnoreCase("B")
								|| o.getCoverageType().equalsIgnoreCase("O")) && o.getVehicleId() != 99999)
						.collect(Collectors.toList());
				String endtPrevQuoteNo = homePositionMaster.getEndtPrevQuoteNo();

				List<ReinsuranceCoverDetails> reinsuranceCoverDetailsListEDBK = reinsuranceCoverDetailsRepo
						.findByQuoteno(endtPrevQuoteNo);

				saveCoverDetails = saveEndCoverDetails(req, reinsuranceCoverDetailList, reInsuranceRiskListBK,
						reinsuranceCoverDetailsListEDBK, today, homePositionMaster, companyProductMaster, reSectionMap,
						sectionCoverMasterList, enPpolicyCoverList);

			} else {

				List<PolicyCoverData> policyCoverList = policyCoverDataList.stream().filter(
						o -> (o.getCoverageType().equalsIgnoreCase("B") || o.getCoverageType().equalsIgnoreCase("O")
								|| o.getCoverageType().equalsIgnoreCase(
										"A"))
								&& o.getVehicleId() != 99999)
						.collect(Collectors.toList());
				saveCoverDetails = saveCoverDetails(req, reinsuranceCoverDetailList, reInsuranceRiskListBK,
						reinsuranceCoverListBK, today, homePositionMaster, companyProductMaster, reSectionMap,
						sectionCoverMasterList, policyCoverList);
				List<PolicyCoverData> policyDiscLoadCoverList = policyCoverDataList.stream().filter(
						o -> o.getCoverageType().equalsIgnoreCase("D") || o.getCoverageType().equalsIgnoreCase("L"))
						.collect(Collectors.toList());

				saveDisLoadDetails = saveDisLoadDetails(req, today, homePositionMaster, companyProductMaster,
						reSectionMap, sectionCoverMasterList, policyDiscLoadCoverList);

			}

			if (req.getUserType() != null && !req.getUserType().equalsIgnoreCase("Issuer")) {
				premiaPush(res, homePositionMaster, saveRiskDetails, saveCoverDetails, saveDisLoadDetails);
			}
			res.setMessage("Saved Successfully");
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErroCode(0);

			return res;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
			res.setMessage("ReInsurance Failed");
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(0);
			return res;
		} finally {
		}
	}

	private List<ReinsuranceDiscLoadDetails> saveDisLoadDetails(ReInsuranceQuoteReq req, Date today,
			HomePositionMaster homePositionMaster, CompanyProductMaster companyProductMaster,
			Map<Integer, ProductSectionMaster> reSectionMap, List<SectionCoverMaster> sectionCoverMasterList,
			List<PolicyCoverData> policyDiscLoadCoverList) {
		List<ReinsuranceDiscLoadDetails> saveAll = new ArrayList<>();
		try {
			List<ReinsuranceDiscLoadDetails> reinsuranceDiscLoadDetailsList = new ArrayList<>();

			List<ReinsuranceDiscLoadDetails> exitingDiscloadingdeatils = reinsuranceDiscLoadDetailsRepos
					.findByQuoteno(req.getQuoteNo());
			if (exitingDiscloadingdeatils != null && exitingDiscloadingdeatils.isEmpty()) {
				reinsuranceDiscLoadDetailsRepos.deleteAll(exitingDiscloadingdeatils);
			}
			for (PolicyCoverData policyCoverData : policyDiscLoadCoverList) {
				ReinsuranceDiscLoadDetails reinsuranceDiscLoadDetails = new ReinsuranceDiscLoadDetails();
				ReinsuranceDiscLoadDetailsId reinsuranceDiscLoadDetailsId = new ReinsuranceDiscLoadDetailsId();
				reinsuranceDiscLoadDetailsId.setCompanyId(homePositionMaster.getCompanyId());
				reinsuranceDiscLoadDetailsId.setDiscLoadId(policyCoverData.getDiscLoadId().toString());
				reinsuranceDiscLoadDetailsId.setProductId(String.valueOf(policyCoverData.getProductId()));
				reinsuranceDiscLoadDetailsId.setQuoteno(req.getQuoteNo());
				if (policyCoverData.getVehicleId() != null && policyCoverData.getVehicleId() != 99999) {
					Optional<SectionCoverMaster> sectionCoverMaster = sectionCoverMasterList.stream()
							.filter(c -> c.getSectionId().equals(policyCoverData.getSectionId())
									&& c.getCoverId().equals(policyCoverData.getCoverId()))
							.findFirst();
					ProductSectionMaster productSectionMaster = reSectionMap
							.get(Integer.valueOf(policyCoverData.getSectionId()));
					reinsuranceDiscLoadDetails.setCoverCode(
							sectionCoverMaster.isPresent() ? sectionCoverMaster.get().getCoreAppCode() : "");
					reinsuranceDiscLoadDetails.setRiskId(policyCoverData.getVehicleId().toString());
					reinsuranceDiscLoadDetails.setSectionCode(productSectionMaster.getCoreAppCode());
					reinsuranceDiscLoadDetails.setCoverId(policyCoverData.getCoverId().toString());
					reinsuranceDiscLoadDetails.setSectionId(policyCoverData.getSectionId().toString());
					reinsuranceDiscLoadDetails.setDiscLoadCode(
							sectionCoverMaster.isPresent() ? sectionCoverMaster.get().getCoreAppCode() : "");
				} else {
					Optional<SectionCoverMaster> sectionCoverMaster = sectionCoverMasterList.stream()
							.filter(c -> c.getSectionId().equals(policyCoverData.getSectionId())
									&& c.getCoverId().equals(policyCoverData.getCoverId()))
							.findFirst();
					reinsuranceDiscLoadDetails.setDiscLoadCode(
							sectionCoverMaster.isPresent() ? sectionCoverMaster.get().getCoreAppCode() : "");
					reinsuranceDiscLoadDetails.setCoverCode(null);
					reinsuranceDiscLoadDetails.setRiskId(null);
					reinsuranceDiscLoadDetails.setSectionCode(null);
					reinsuranceDiscLoadDetails.setCoverId(null);
					reinsuranceDiscLoadDetails.setSectionId(null);
				}

				reinsuranceDiscLoadDetails.setDiscLoadId(reinsuranceDiscLoadDetailsId.getDiscLoadId());
				reinsuranceDiscLoadDetails.setDiscLoadFc(policyCoverData.getPremiumExcludedTaxFc());
				reinsuranceDiscLoadDetails.setDiscLoadLc(
						policyCoverData.getPremiumExcludedTaxLc() != null ? policyCoverData.getPremiumExcludedTaxLc()
								: policyCoverData.getPremiumExcludedTaxFc());

				reinsuranceDiscLoadDetails.setDiscLoadName(policyCoverData.getCoverName());

				reinsuranceDiscLoadDetails.setCompanyId(reinsuranceDiscLoadDetailsId.getCompanyId());
				reinsuranceDiscLoadDetails.setCoverRecType("I");
				reinsuranceDiscLoadDetails.setDiscLoadType(policyCoverData.getCoverageType());
				reinsuranceDiscLoadDetails.setPolIdx(Integer.valueOf(homePositionMaster.getAhpolIdx().toString()));
				reinsuranceDiscLoadDetails.setProductCode(companyProductMaster.getCoreAppCode());
				reinsuranceDiscLoadDetails.setProductId(reinsuranceDiscLoadDetailsId.getProductId());
				reinsuranceDiscLoadDetails.setQuoteno(reinsuranceDiscLoadDetailsId.getQuoteno());
				reinsuranceDiscLoadDetails.setCreatedBy(req.getCreatedBy());
				reinsuranceDiscLoadDetails.setCreatedDate(today);
				reinsuranceDiscLoadDetails.setStatus("Y");
				reinsuranceDiscLoadDetails.setUwSysId(Integer.valueOf(homePositionMaster.getUwsysId().toString()));
				reinsuranceDiscLoadDetailsList.add(reinsuranceDiscLoadDetails);
			}
			saveAll = reinsuranceDiscLoadDetailsRepos.saveAll(reinsuranceDiscLoadDetailsList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
		return saveAll;
	}

	private List<ReinsuranceCoverDetails> saveEndCoverDetails(ReInsuranceQuoteReq req,
			List<ReinsuranceCoverDetails> reinsuranceCoverDetailList,
			List<ReInsuranceRiskDetails> reInsuranceRiskListBK, List<ReinsuranceCoverDetails> reinsuranceCoverListBK,
			Date today, HomePositionMaster homePositionMaster, CompanyProductMaster companyProductMaster,
			Map<Integer, ProductSectionMaster> reSectionMap, List<SectionCoverMaster> sectionCoverMasterList,
			List<PolicyCoverData> policyCoverList) {
		List<ReinsuranceCoverDetails> saveAll = new ArrayList<>();
		try {
			for (PolicyCoverData policyCoverData : policyCoverList) {
				ReinsuranceCoverDetails reinsuranceCoverDetails = new ReinsuranceCoverDetails();
				ReinsuranceCoverDetails coverBackup = null;
				ReInsuranceRiskDetails riskBackup = null;
				String pmlPrec = null;
				String dyf = null;
				ReInsuranceCoverDetailsId reInsuranceCoverDetailsId = new ReInsuranceCoverDetailsId();
				reInsuranceCoverDetailsId.setCompanyId(homePositionMaster.getCompanyId());
				reInsuranceCoverDetailsId.setCoverId(String.valueOf(policyCoverData.getCoverId()));
				reInsuranceCoverDetailsId.setProductId(String.valueOf(policyCoverData.getProductId()));
				reInsuranceCoverDetailsId.setQuoteno(req.getQuoteNo());
				reInsuranceCoverDetailsId.setSectionId(String.valueOf(policyCoverData.getSectionId()));

				List<ReinsuranceCoverDetails> coverBkList = reinsuranceCoverListBK.stream()
						.filter(c -> c.getCoverId().equalsIgnoreCase(policyCoverData.getCoverId().toString())
								&& c.getSectionId().equalsIgnoreCase(policyCoverData.getSectionId().toString()))
						.collect(Collectors.toList());
				if (coverBkList != null && !coverBkList.isEmpty()) {
					Optional<ReinsuranceCoverDetails> first = coverBkList.stream()
							.filter(o -> o.getDafPerc() != null && !o.getDafPerc().isEmpty()).findFirst();
					dyf = first.isPresent() ? first.get().getDafPerc() : null;
					coverBackup = coverBkList.get(0);
				}
				List<ReInsuranceRiskDetails> riskBKList = reInsuranceRiskListBK.stream()
						.filter(o -> o.getSectionId().equalsIgnoreCase(policyCoverData.getSectionId().toString()))
						.collect(Collectors.toList());
				if (riskBKList != null && !riskBKList.isEmpty()) {
					riskBackup = riskBKList.get(0);
					pmlPrec = riskBKList.get(0).getPmlPerc();
				}
				Optional<SectionCoverMaster> sectionCoverMaster = sectionCoverMasterList.stream()
						.filter(c -> c.getSectionId().equals(policyCoverData.getSectionId())
								&& c.getCoverId().equals(policyCoverData.getCoverId()))
						.findFirst();
				ProductSectionMaster productSectionMaster = reSectionMap
						.get(Integer.valueOf(policyCoverData.getSectionId()));

				reinsuranceCoverDetails.setCompanyId(reInsuranceCoverDetailsId.getCompanyId());
				reinsuranceCoverDetails.setUwSysId(homePositionMaster.getUwsysId() != null
						? Integer.valueOf(homePositionMaster.getUwsysId().toString())
						: null);
				reinsuranceCoverDetails.setPolIdx(homePositionMaster.getAhpolIdx() != null
						? Integer.valueOf(homePositionMaster.getAhpolIdx().toString())
						: null);
				reinsuranceCoverDetails.setCoverId(reInsuranceCoverDetailsId.getCoverId());
				reinsuranceCoverDetails.setCoverName(policyCoverData.getCoverName());
				reinsuranceCoverDetails.setEndoEndDate(null);
				reinsuranceCoverDetails.setEndoStartDate(null);
				reinsuranceCoverDetails.setEntryDate(today);
				reinsuranceCoverDetails.setExpiryDate(homePositionMaster.getExpiryDate());
				reinsuranceCoverDetails.setInceptionDate(homePositionMaster.getInceptionDate());
				reinsuranceCoverDetails.setCreatedBy(req.getCreatedBy());

				reinsuranceCoverDetails.setProductId(reInsuranceCoverDetailsId.getProductId());
				reinsuranceCoverDetails.setProductName(homePositionMaster.getProductName());
				reinsuranceCoverDetails.setQuoteno(reInsuranceCoverDetailsId.getQuoteno());

				reinsuranceCoverDetails.setSectionId(reInsuranceCoverDetailsId.getSectionId());
				reinsuranceCoverDetails
						.setSectionCode(productSectionMaster != null ? productSectionMaster.getCoreAppCode() : null);
				reinsuranceCoverDetails
						.setSectionName(productSectionMaster != null ? productSectionMaster.getSectionName() : null);
				reInsuranceCoverDetailsId.setRiskId(policyCoverData.getLocationId());
				reinsuranceCoverDetails.setRiskId(reInsuranceCoverDetailsId.getRiskId());
				reinsuranceCoverDetails.setStatus("Y");

				reinsuranceCoverDetails.setWaryn("N");
				reinsuranceCoverDetails.setProductCode(companyProductMaster.getCoreAppCode());

				reinsuranceCoverDetails.setCoverCode(
						sectionCoverMaster.isPresent() ? sectionCoverMaster.get().getCoreAppCode() : null);
				reinsuranceCoverDetails.setCoverRecType("I");

				// saveCoverCalc(policyCoverData, reinsuranceCoverDetails, coverBackup,
				// riskBackup, pmlPrec, dyf);

				endrosementForCover(homePositionMaster, policyCoverData, reinsuranceCoverDetails, coverBackup,
						riskBackup, pmlPrec, dyf);

				reinsuranceCoverDetailList.add(reinsuranceCoverDetails);
			}
			saveAll = reinsuranceCoverDetailsRepo.saveAll(reinsuranceCoverDetailList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
		return saveAll;
	}

	private List<ReinsuranceCoverDetails> saveCoverDetails(ReInsuranceQuoteReq req,
			List<ReinsuranceCoverDetails> reinsuranceCoverDetailList,
			List<ReInsuranceRiskDetails> reInsuranceRiskListBK, List<ReinsuranceCoverDetails> reinsuranceCoverListBK,
			Date today, HomePositionMaster homePositionMaster, CompanyProductMaster companyProductMaster,
			Map<Integer, ProductSectionMaster> reSectionMap, List<SectionCoverMaster> sectionCoverMasterList,
			List<PolicyCoverData> policyCoverList) {
		List<ReinsuranceCoverDetails> saveAll = new ArrayList<>();
		try {
			for (PolicyCoverData policyCoverData : policyCoverList) {
				ReinsuranceCoverDetails reinsuranceCoverDetails = new ReinsuranceCoverDetails();
				ReinsuranceCoverDetails coverBackup = null;
				ReInsuranceRiskDetails riskBackup = null;
				String pmlPrec = null;
				String dyf = null;
				ReInsuranceCoverDetailsId reInsuranceCoverDetailsId = new ReInsuranceCoverDetailsId();
				reInsuranceCoverDetailsId.setCompanyId(homePositionMaster.getCompanyId());
				reInsuranceCoverDetailsId.setCoverId(String.valueOf(policyCoverData.getCoverId()));
				reInsuranceCoverDetailsId.setProductId(String.valueOf(policyCoverData.getProductId()));
				reInsuranceCoverDetailsId.setQuoteno(req.getQuoteNo());

				reInsuranceCoverDetailsId.setSectionId(String.valueOf(policyCoverData.getSectionId()));

				List<ReinsuranceCoverDetails> coverBkList = reinsuranceCoverListBK.stream()
						.filter(c -> c.getCoverId().equalsIgnoreCase(policyCoverData.getCoverId().toString())
								&& c.getSectionId().equalsIgnoreCase(policyCoverData.getSectionId().toString()))
						.collect(Collectors.toList());
				if (coverBkList != null && !coverBkList.isEmpty()) {
					Optional<ReinsuranceCoverDetails> first = coverBkList.stream()
							.filter(o -> o.getDafPerc() != null && !o.getDafPerc().isEmpty()).findFirst();
					dyf = first.isPresent() ? first.get().getDafPerc() : null;
					coverBackup = coverBkList.get(0);
				}
				List<ReInsuranceRiskDetails> riskBKList = reInsuranceRiskListBK.stream()
						.filter(o -> o.getSectionId().equalsIgnoreCase(policyCoverData.getSectionId().toString()))
						.collect(Collectors.toList());
				if (riskBKList != null && !riskBKList.isEmpty()) {
					riskBackup = riskBKList.get(0);
					pmlPrec = riskBKList.get(0).getPmlPerc();
				}
				Optional<SectionCoverMaster> sectionCoverMaster = sectionCoverMasterList.stream()
						.filter(c -> c.getSectionId().equals(policyCoverData.getSectionId())
								&& c.getCoverId().equals(policyCoverData.getCoverId()))
						.findFirst();
				ProductSectionMaster productSectionMaster = reSectionMap
						.get(Integer.valueOf(policyCoverData.getSectionId()));

				reinsuranceCoverDetails.setCompanyId(reInsuranceCoverDetailsId.getCompanyId());
				reinsuranceCoverDetails.setUwSysId(Integer.valueOf(homePositionMaster.getUwsysId().toString()));
				reinsuranceCoverDetails.setPolIdx(Integer.valueOf(homePositionMaster.getAhpolIdx().toString()));
				reinsuranceCoverDetails.setCoverId(reInsuranceCoverDetailsId.getCoverId());
				reinsuranceCoverDetails.setCoverName(policyCoverData.getCoverName());
				reinsuranceCoverDetails.setEndoEndDate(null);
				reinsuranceCoverDetails.setEndoStartDate(null);
				reinsuranceCoverDetails.setEntryDate(today);
				reinsuranceCoverDetails.setExpiryDate(homePositionMaster.getExpiryDate());
				reinsuranceCoverDetails.setInceptionDate(homePositionMaster.getInceptionDate());
				reinsuranceCoverDetails.setCreatedBy(req.getCreatedBy());
				reinsuranceCoverDetails.setPremiumfc(policyCoverData.getPremiumExcludedTaxFc());
				reinsuranceCoverDetails.setPremiumlc(policyCoverData.getPremiumExcludedTaxLc());
				reinsuranceCoverDetails.setProductId(reInsuranceCoverDetailsId.getProductId());
				reinsuranceCoverDetails.setProductName(homePositionMaster.getProductName());
				reinsuranceCoverDetails.setQuoteno(reInsuranceCoverDetailsId.getQuoteno());

				reinsuranceCoverDetails.setSectionId(reInsuranceCoverDetailsId.getSectionId());
				reinsuranceCoverDetails
						.setSectionCode(productSectionMaster != null ? productSectionMaster.getCoreAppCode() : null);
				reinsuranceCoverDetails
						.setSectionName(productSectionMaster != null ? productSectionMaster.getSectionName() : null);
				reInsuranceCoverDetailsId.setRiskId(policyCoverData.getLocationId());
				reinsuranceCoverDetails.setRiskId(reInsuranceCoverDetailsId.getRiskId());
				reinsuranceCoverDetails.setStatus("Y");
				reinsuranceCoverDetails.setSumInsuredfc(
						policyCoverData.getCalcType().equalsIgnoreCase("X") ? policyCoverData.getCoverageLimit()
								: policyCoverData.getSumInsuredLc());
				reinsuranceCoverDetails.setSuminsuredlc(
						policyCoverData.getCalcType().equalsIgnoreCase("X") ? policyCoverData.getCoverageLimit()
								: policyCoverData.getSumInsuredLc());
				reinsuranceCoverDetails.setWaryn("N");
				reinsuranceCoverDetails.setProductCode(companyProductMaster.getCoreAppCode());

				reinsuranceCoverDetails.setCoverCode(
						sectionCoverMaster.isPresent() ? sectionCoverMaster.get().getCoreAppCode() : null);
				reinsuranceCoverDetails.setCoverRecType("I");

				saveCoverCalc(policyCoverData, reinsuranceCoverDetails, coverBackup, riskBackup, pmlPrec, dyf);
				reinsuranceCoverDetails.setEndoEndDate(null);
				reinsuranceCoverDetails.setEndoStartDate(null);

				reinsuranceCoverDetailList.add(reinsuranceCoverDetails);
			}
			saveAll = reinsuranceCoverDetailsRepo.saveAll(reinsuranceCoverDetailList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
		return saveAll;
	}

	private void saveCoverCalc(PolicyCoverData policyCoverData, ReinsuranceCoverDetails reinsuranceCoverDetails,
			ReinsuranceCoverDetails coverBackup, ReInsuranceRiskDetails riskBackup, String pmlPrec, String dyf) {
		try {
			reinsuranceCoverDetails.setDafPerc(coverBackup != null ? coverBackup.getDafPerc() : dyf);
			reinsuranceCoverDetails.setFacPercantage(coverBackup != null ? coverBackup.getFacPercantage()
					: riskBackup != null ? riskBackup.getCoverFAC() : null);
			reinsuranceCoverDetails.setFacpmlSuminsuredlc(facpmlCalc(
					coverBackup != null ? coverBackup.getFacPercantage()
							: riskBackup != null ? riskBackup.getCoverFAC() : null,
					pmlPrec, policyCoverData.getSumInsuredLc()));
			reinsuranceCoverDetails.setFacpmlSuminsuredfc(facpmlCalc(
					coverBackup != null ? coverBackup.getFacPercantage()
							: riskBackup != null ? riskBackup.getCoverFAC() : null,
					pmlPrec, policyCoverData.getSumInsured()));
			reinsuranceCoverDetails.setFacPremiumlc(facPremiumCalc(
					coverBackup != null ? coverBackup.getFacPercantage()
							: riskBackup != null ? riskBackup.getCoverFAC() : null,
					policyCoverData.getPremiumExcludedTaxLc()));
			reinsuranceCoverDetails.setFacPremiumfc(facPremiumCalc(
					coverBackup != null ? coverBackup.getFacPercantage()
							: riskBackup != null ? riskBackup.getCoverFAC() : null,
					policyCoverData.getPremiumExcludedTaxFc()));
			reinsuranceCoverDetails
					.setFacSuminsuredlc(facSuminuredCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getSumInsuredLc()));
			reinsuranceCoverDetails
					.setFacSuminsuredfc(facSuminuredCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getSumInsured()));
			reinsuranceCoverDetails.setPmlSuminsuredlc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsuredLc()));
			reinsuranceCoverDetails.setPmlSuminsuredfc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsured()));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
	}

	private List<ReInsuranceRiskDetails> saveRiskDetails(ReInsuranceQuoteReq req,
			List<ReInsuranceRiskDetails> reInsuranceRiskDetailsList, List<ReInsuranceRiskDetails> reInsuranceRiskListBK,
			Date today, String address, HomePositionMaster homePositionMaster,
			CompanyProductMaster companyProductMaster, List<EndtTypeMaster> endtTypeMasterList,
			BranchMaster branchMaster, List<SectionDataDetails> sectionDataDetailsList,
			Map<Integer, ProductSectionMaster> reSectionMap, EwayLobMaster ewayLobMaster,
			EwayDivisionDepartment ewayDivisionDepartment) {
		List<ReInsuranceRiskDetails> saveAll = new ArrayList<>();
		try {
			for (SectionDataDetails sectionDataDetails : sectionDataDetailsList) {
				ReInsuranceRiskDetails riskBackup = null;
				ReInsuranceRiskDetails reInsuranceRiskDetails = new ReInsuranceRiskDetails();
				List<ReInsuranceRiskDetails> riskBKList = reInsuranceRiskListBK.stream()
						.filter(o -> o.getSectionId().equals(sectionDataDetails.getSectionId()))
						.collect(Collectors.toList());
				if (riskBKList != null && !riskBKList.isEmpty()) {
					riskBackup = riskBKList.get(0);
				}
				ProductSectionMaster productSectionMaster = reSectionMap
						.get(Integer.valueOf(sectionDataDetails.getSectionId()));

				ReInsuranceRiskDetailsId reInsuranceRiskDetailsId = new ReInsuranceRiskDetailsId();
				reInsuranceRiskDetailsId.setCompanyId(homePositionMaster.getCompanyId());
				reInsuranceRiskDetailsId.setProductId(sectionDataDetails.getProductId());
				reInsuranceRiskDetailsId.setQuoteno(req.getQuoteNo());
				reInsuranceRiskDetailsId.setRiskId(sectionDataDetails.getLocationId());
				reInsuranceRiskDetailsId.setSectionId(sectionDataDetails.getSectionId());

				saveLocationAndProductDetailsForRisk(req, today, address, homePositionMaster, companyProductMaster,
						sectionDataDetails, riskBackup, reInsuranceRiskDetails, productSectionMaster,
						reInsuranceRiskDetailsId);

				endrosementForRisk(req, homePositionMaster, endtTypeMasterList, sectionDataDetails,
						reInsuranceRiskDetails);
				PersonalInfo personalInfo = personalInfoRepository.findByCustomerId(homePositionMaster.getCustomerId());
				Optional<EserviceCustomerDetails> customerDetails = customerDetailsRepository
						.findByCustomerReferenceNoAndCompanyId(personalInfo.getCustomerReferenceNo(),
								personalInfo.getCompanyId());
				LoginUserInfo loginUserInfo = loginUserInfoRepository.findByLoginId(homePositionMaster.getLoginId());
				reInsuranceRiskDetails.setInsuredName(
						customerDetails.get().getClientName() != null ? customerDetails.get().getClientName() : null);
				reInsuranceRiskDetails.setInsuredCode(
						customerDetails.get().getPolCustCode() != null ? customerDetails.get().getPolCustCode()
								: customerDetails.get().getCustomerReferenceNo());
				reInsuranceRiskDetails.setAgBrkCode(loginUserInfo.getLoginId());
				reInsuranceRiskDetails.setAgBrkName(loginUserInfo.getUserName());
				reInsuranceRiskDetails.setPmlPerc(riskBackup != null ? riskBackup.getPmlPerc() : null);
				reInsuranceRiskDetails.setCoverFAC(riskBackup != null ? riskBackup.getCoverFAC() : null);
				reInsuranceRiskDetails.setUwPremCur(homePositionMaster.getCurrency());
				reInsuranceRiskDetails.setUwDivnId(homePositionMaster.getBranchCode());
				reInsuranceRiskDetails.setUwDivnCode(branchMaster.getCoreAppCode());
				reInsuranceRiskDetails.setUwType("Q");
				reInsuranceRiskDetails.setPolIdx(homePositionMaster.getAhpolIdx() != null
						? Integer.valueOf(homePositionMaster.getAhpolIdx().toString())
						: null);
				reInsuranceRiskDetails.setUwSysId(homePositionMaster.getUwsysId() != null
						? Integer.valueOf(homePositionMaster.getUwsysId().toString())
						: null);
				reInsuranceRiskDetails.setUwBusType("1");
				reInsuranceRiskDetails.setRiskRecType("I");
				reInsuranceRiskDetails.setUwLob(ewayLobMaster != null ? ewayLobMaster.getLobCode() : null);
				reInsuranceRiskDetails
						.setUwDeptId(ewayDivisionDepartment != null ? ewayDivisionDepartment.getDepartmentId() : null);
				reInsuranceRiskDetails.setUwDeptCode(
						ewayDivisionDepartment != null ? ewayDivisionDepartment.getDepartmentCode() : null);
				reInsuranceRiskDetails.setApprovalDate(today);
				reInsuranceRiskDetails.setApprovalStatus("I");
				reInsuranceRiskDetails.setApprovalUserId(req.getCreatedBy());
				reInsuranceRiskDetailsList.add(reInsuranceRiskDetails);
			}
			saveAll = reInsuranceRiskDetailsRepo.saveAll(reInsuranceRiskDetailsList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
		return saveAll;
	}

	private void saveLocationAndProductDetailsForRisk(ReInsuranceQuoteReq req, Date today, String address,
			HomePositionMaster homePositionMaster, CompanyProductMaster companyProductMaster,
			SectionDataDetails sectionDataDetails, ReInsuranceRiskDetails riskBackup,
			ReInsuranceRiskDetails reInsuranceRiskDetails, ProductSectionMaster productSectionMaster,
			ReInsuranceRiskDetailsId reInsuranceRiskDetailsId) {
		reInsuranceRiskDetails.setCompanyId(reInsuranceRiskDetailsId.getCompanyId());
		reInsuranceRiskDetails.setCreatedBy(req.getCreatedBy());
		reInsuranceRiskDetails.setProductId(homePositionMaster.getProductName());
		reInsuranceRiskDetails.setCompanyCode(companyProductMaster.getCoreAppCode());
		reInsuranceRiskDetails.setProductCode(companyProductMaster.getCoreAppCode());
		reInsuranceRiskDetails.setSectionCode(productSectionMaster.getCoreAppCode());
		reInsuranceRiskDetails.setSectionId(reInsuranceRiskDetailsId.getSectionId());
		reInsuranceRiskDetails.setProductId(reInsuranceRiskDetailsId.getProductId());
		reInsuranceRiskDetails.setSectionName(sectionDataDetails.getSectionDesc());
		reInsuranceRiskDetails.setRiskaddress(address);
		reInsuranceRiskDetails.setBranchName(homePositionMaster.getBranchName());
		reInsuranceRiskDetails.setRiskId(reInsuranceRiskDetailsId.getRiskId());
		reInsuranceRiskDetails.setRiskCategory(riskBackup != null ? riskBackup.getRiskCategory() : null);
		reInsuranceRiskDetails.setProductName(homePositionMaster.getProductName());
		reInsuranceRiskDetails.setLocationName(sectionDataDetails.getLocationName());
		reInsuranceRiskDetails.setEntryDate(today);
		reInsuranceRiskDetails.setExpireDate(homePositionMaster.getExpiryDate());
		reInsuranceRiskDetails.setInceptionDate(homePositionMaster.getInceptionDate());
		reInsuranceRiskDetails.setQuoteno(sectionDataDetails.getQuoteNo());
		reInsuranceRiskDetails.setStatus("Y");
		reInsuranceRiskDetails.setRiskSiCurr(homePositionMaster.getCurrency());
		reInsuranceRiskDetails.setPolicyNo(
				homePositionMaster.getOriginalPolicyNo() != null ? homePositionMaster.getOriginalPolicyNo() : null);
		reInsuranceRiskDetails
				.setRiskrefno(riskBackup != null ? riskBackup.getRiskrefno() : reInsuranceRiskDetailsId.getRiskId());
	}

	private void endrosementForRisk(ReInsuranceQuoteReq req, HomePositionMaster homePositionMaster,
			List<EndtTypeMaster> endtTypeMasterList, SectionDataDetails sectionDataDetails,
			ReInsuranceRiskDetails reInsuranceRiskDetails) {
		if (homePositionMaster.getEndtTypeId() != null && !homePositionMaster.getEndtTypeId().isEmpty()) {
			reInsuranceRiskDetails.setEndtNo(homePositionMaster.getPolicyNo());
			List<EndtTypeMaster> endtTypeMaster = endtTypeMasterList.stream()
					.filter(e -> e.getEndtTypeId().equals(Integer.valueOf(homePositionMaster.getEndtTypeId())))
					.collect(Collectors.toList());
			reInsuranceRiskDetails.setEndtTypeCode(endtTypeMaster.get(0).getCoreAppCode());
			reInsuranceRiskDetails.setEndoEndDate(homePositionMaster.getExpiryDate());
			reInsuranceRiskDetails.setEndoStartDate(homePositionMaster.getEndorsementEffdate());
			reInsuranceRiskDetails.setEndtTypeId(
					sectionDataDetails.getEndorsementType() != null ? sectionDataDetails.getEndorsementType().toString()
							: null);
			reInsuranceRiskDetails.setEndtTypeCategory(endtTypeMaster.get(0).getEndtTypeCategory());
			reInsuranceRiskDetails.setEndtTypeCategoryId(endtTypeMaster.get(0).getEndtTypeCategoryId());
			reInsuranceRiskDetails.setEndtTypeName(sectionDataDetails.getEndorsementTypeDesc());
		} else {
			reInsuranceRiskDetails.setEndtNo(null);
			reInsuranceRiskDetails.setEndtTypeCode(null);
			reInsuranceRiskDetails.setEndoEndDate(null);
			reInsuranceRiskDetails.setEndoStartDate(null);
			reInsuranceRiskDetails.setEndtTypeId(null);
			reInsuranceRiskDetails.setEndtTypeCategory(null);
			reInsuranceRiskDetails.setEndtTypeCategoryId(null);
			reInsuranceRiskDetails.setEndtTypeName(null);
		}
	}

	private void endrosementForCover(HomePositionMaster homePositionMaster, PolicyCoverData policyCoverData,
			ReinsuranceCoverDetails reinsuranceCoverDetails, ReinsuranceCoverDetails coverBackup,
			ReInsuranceRiskDetails riskBackup, String pmlPrec, String dyf) {
		if (homePositionMaster.getEndtTypeId() != null && !homePositionMaster.getEndtTypeId().isEmpty()) {
			reinsuranceCoverDetails.setEndoEndDate(homePositionMaster.getExpiryDate());
			reinsuranceCoverDetails.setEndoStartDate(homePositionMaster.getEndorsementEffdate());
			if ((policyCoverData.getDiscLoadId().compareTo(852) == 0
					|| policyCoverData.getDiscLoadId().compareTo(842) == 0)
					&& policyCoverData.getIsSelected().equalsIgnoreCase("D")) {
				reinsuranceCoverDetails.setDafPerc(coverBackup != null ? coverBackup.getDafPerc() : dyf);
				reinsuranceCoverDetails.setFacPercantage(coverBackup != null ? coverBackup.getFacPercantage()
						: riskBackup != null ? riskBackup.getCoverFAC() : null);
				BigDecimal facPremiumCalc = facPremiumCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						policyCoverData.getPremiumExcludedTaxLc());
				BigDecimal facPremiumCalc2 = facPremiumCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						policyCoverData.getPremiumExcludedTaxFc());
				reinsuranceCoverDetails.setFacPremiumlc(facPremiumCalc);
				reinsuranceCoverDetails.setFacPremiumfc(facPremiumCalc2);
				reinsuranceCoverDetails.setPremiumfc(
						policyCoverData.getPremiumExcludedTaxFc() != null ? policyCoverData.getPremiumExcludedTaxFc()
								: null);
				reinsuranceCoverDetails.setPremiumlc(
						policyCoverData.getPremiumExcludedTaxLc() != null ? policyCoverData.getPremiumExcludedTaxLc()
								: null);
				reinsuranceCoverDetails.setFacSuminsuredlc(facSuminuredCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						policyCoverData.getSumInsuredLc().multiply(new BigDecimal("-1"))));
				reinsuranceCoverDetails
						.setFacSuminsuredfc(facSuminuredCalc(
								coverBackup != null ? coverBackup.getFacPercantage()
										: riskBackup != null ? riskBackup.getCoverFAC() : null,
								policyCoverData.getSumInsured()).multiply(new BigDecimal("-1")));
				reinsuranceCoverDetails
						.setPmlSuminsuredlc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsuredLc()) != null
								? pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsuredLc()).multiply(
										new BigDecimal("-1"))
								: null);
				reinsuranceCoverDetails
						.setPmlSuminsuredfc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsured()) != null
								? pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsured()).multiply(
										new BigDecimal("-1"))
								: null);
				if (policyCoverData.getDiscLoadId().compareTo(842) == 0) {
					reinsuranceCoverDetails.setSumInsuredfc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setSuminsuredlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacSuminsuredlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacSuminsuredfc(BigDecimal.ZERO);
				} else {
					reinsuranceCoverDetails.setFacSuminsuredlc(facSuminuredCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getSumInsuredLc().multiply(new BigDecimal("-1"))));
					reinsuranceCoverDetails.setFacSuminsuredfc(facSuminuredCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getSumInsured()).multiply(new BigDecimal("-1")));
					reinsuranceCoverDetails.setSumInsuredfc(policyCoverData.getCalcType().equalsIgnoreCase("X")
							? policyCoverData.getCoverageLimit().multiply(new BigDecimal("-1"))
							: policyCoverData.getSumInsuredLc().multiply(new BigDecimal("-1")));
					reinsuranceCoverDetails.setSuminsuredlc(policyCoverData.getCalcType().equalsIgnoreCase("X")
							? policyCoverData.getCoverageLimit().multiply(new BigDecimal("-1"))
							: policyCoverData.getSumInsuredLc().multiply(new BigDecimal("-1")));
				}
				reinsuranceCoverDetails.setFacpmlSuminsuredlc(facpmlCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						pmlPrec, policyCoverData.getSumInsuredLc()) != null
								? facpmlCalc(
										coverBackup != null ? coverBackup.getFacPercantage()
												: riskBackup != null ? riskBackup.getCoverFAC() : null,
										pmlPrec, policyCoverData.getSumInsuredLc()).multiply(new BigDecimal("-1"))
								: null);
				reinsuranceCoverDetails.setFacpmlSuminsuredfc(facpmlCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						pmlPrec, policyCoverData.getSumInsuredLc()) != null
								? facpmlCalc(
										coverBackup != null ? coverBackup.getFacPercantage()
												: riskBackup != null ? riskBackup.getCoverFAC() : null,
										pmlPrec, policyCoverData.getSumInsuredLc()).multiply(new BigDecimal("-1"))
								: null);
			} else if (policyCoverData.getDiscLoadId().compareTo(850) == 0) {
				reinsuranceCoverDetails.setDafPerc(coverBackup != null ? coverBackup.getDafPerc() : dyf);
				reinsuranceCoverDetails.setFacPercantage(coverBackup != null ? coverBackup.getFacPercantage()
						: riskBackup != null ? riskBackup.getCoverFAC() : null);
				reinsuranceCoverDetails.setSumInsuredfc(
						policyCoverData.getCalcType().equalsIgnoreCase("X") ? policyCoverData.getCoverageLimit()
								: policyCoverData.getSumInsuredLc());
				reinsuranceCoverDetails.setSuminsuredlc(
						policyCoverData.getCalcType().equalsIgnoreCase("X") ? policyCoverData.getCoverageLimit()
								: policyCoverData.getSumInsuredLc());
				if (policyCoverData.getDiscLoadId().compareTo(850) == 0
						&& policyCoverData.getSumInsured().compareTo(BigDecimal.ZERO) < 0) {
					reinsuranceCoverDetails.setPremiumfc(policyCoverData.getPremiumExcludedTaxFc() != null
							? policyCoverData.getPremiumExcludedTaxFc().multiply(new BigDecimal("-1"))
							: null);
					reinsuranceCoverDetails.setPremiumlc(policyCoverData.getPremiumExcludedTaxLc() != null
							? policyCoverData.getPremiumExcludedTaxLc().multiply(new BigDecimal("-1"))
							: null);
					BigDecimal facPremiumCalc = facPremiumCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getPremiumExcludedTaxLc());
					BigDecimal facPremiumCalc2 = facPremiumCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getPremiumExcludedTaxFc());
					reinsuranceCoverDetails.setFacPremiumlc(
							facPremiumCalc != null ? facPremiumCalc.multiply(new BigDecimal("-1")) : null);
					reinsuranceCoverDetails.setFacPremiumfc(
							facPremiumCalc2 != null ? facPremiumCalc2.multiply(new BigDecimal("-1")) : null);
				} else {
					reinsuranceCoverDetails.setPremiumfc(policyCoverData.getPremiumExcludedTaxFc());
					reinsuranceCoverDetails.setPremiumlc(policyCoverData.getPremiumExcludedTaxLc());
					reinsuranceCoverDetails.setFacPremiumlc(facPremiumCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getPremiumExcludedTaxLc()));
					reinsuranceCoverDetails.setFacPremiumfc(facPremiumCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getPremiumExcludedTaxFc()));
				}

				reinsuranceCoverDetails.setFacpmlSuminsuredlc(facpmlCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						pmlPrec, policyCoverData.getSumInsuredLc()));
				reinsuranceCoverDetails.setFacpmlSuminsuredfc(facpmlCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						pmlPrec, policyCoverData.getSumInsured()));

				reinsuranceCoverDetails.setFacSuminsuredlc(facSuminuredCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						policyCoverData.getSumInsuredLc()));
				reinsuranceCoverDetails.setFacSuminsuredfc(facSuminuredCalc(
						coverBackup != null ? coverBackup.getFacPercantage()
								: riskBackup != null ? riskBackup.getCoverFAC() : null,
						policyCoverData.getSumInsured()));
				reinsuranceCoverDetails
						.setPmlSuminsuredlc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsuredLc()));
				reinsuranceCoverDetails.setPmlSuminsuredfc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsured()));

			} else {
				if ((homePositionMaster.getEndtTypeId().equalsIgnoreCase("851")
						|| homePositionMaster.getEndtTypeId().equalsIgnoreCase("854"))
						&& policyCoverData.getCoverageType().equalsIgnoreCase("E")
						&& policyCoverData.getPremiumExcludedTaxFc().compareTo(BigDecimal.ZERO) != 0) {

					reinsuranceCoverDetails.setDafPerc(coverBackup != null ? coverBackup.getDafPerc() : dyf);
					reinsuranceCoverDetails.setFacPercantage(coverBackup != null ? coverBackup.getFacPercantage()
							: riskBackup != null ? riskBackup.getCoverFAC() : null);
					reinsuranceCoverDetails.setSumInsuredfc(
							policyCoverData.getCalcType().equalsIgnoreCase("X") ? policyCoverData.getCoverageLimit()
									: policyCoverData.getSumInsuredLc());
					reinsuranceCoverDetails.setSuminsuredlc(
							policyCoverData.getCalcType().equalsIgnoreCase("X") ? policyCoverData.getCoverageLimit()
									: policyCoverData.getSumInsuredLc());

					reinsuranceCoverDetails.setPremiumfc(policyCoverData.getPremiumExcludedTaxFc());
					reinsuranceCoverDetails.setPremiumlc(policyCoverData.getPremiumExcludedTaxLc());
					reinsuranceCoverDetails.setFacPremiumlc(facPremiumCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getPremiumExcludedTaxLc()));
					reinsuranceCoverDetails.setFacPremiumfc(facPremiumCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getPremiumExcludedTaxFc()));
					reinsuranceCoverDetails.setFacpmlSuminsuredlc(facpmlCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							pmlPrec, policyCoverData.getSumInsuredLc()));
					reinsuranceCoverDetails.setFacpmlSuminsuredfc(facpmlCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							pmlPrec, policyCoverData.getSumInsured()));

					reinsuranceCoverDetails.setFacSuminsuredlc(facSuminuredCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getSumInsuredLc()));
					reinsuranceCoverDetails.setFacSuminsuredfc(facSuminuredCalc(
							coverBackup != null ? coverBackup.getFacPercantage()
									: riskBackup != null ? riskBackup.getCoverFAC() : null,
							policyCoverData.getSumInsured()));
					reinsuranceCoverDetails
							.setPmlSuminsuredlc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsuredLc()));
					reinsuranceCoverDetails
							.setPmlSuminsuredfc(pmlSuminuredCalc(pmlPrec, policyCoverData.getSumInsured()));
				} else {
					reinsuranceCoverDetails.setDafPerc(coverBackup != null ? coverBackup.getDafPerc() : dyf);
					reinsuranceCoverDetails.setFacPercantage(coverBackup != null ? coverBackup.getFacPercantage()
							: riskBackup != null ? riskBackup.getCoverFAC() : null);
					reinsuranceCoverDetails.setSumInsuredfc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setSuminsuredlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setPremiumfc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setPremiumlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacpmlSuminsuredlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacpmlSuminsuredfc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacPremiumlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacPremiumfc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacSuminsuredlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setFacSuminsuredfc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setPmlSuminsuredlc(BigDecimal.ZERO);
					reinsuranceCoverDetails.setPmlSuminsuredfc(BigDecimal.ZERO);
				}
			}

		} else {
			reinsuranceCoverDetails.setEndoEndDate(null);
			reinsuranceCoverDetails.setEndoStartDate(null);
		}
	}

	private BigDecimal facpmlCalc(String facPrec, String pmlPrec, BigDecimal sumInsured) {
		BigDecimal facpml = null;
		if (facPrec != null && sumInsured != null && pmlPrec != null) {
			facpml = sumInsured.multiply(new BigDecimal(facPrec)).divide(new BigDecimal("100"))
					.multiply(new BigDecimal(pmlPrec)).divide(new BigDecimal("100"));
		}
		return facpml;
	}

	private BigDecimal facPremiumCalc(String facPrec, BigDecimal premium) {
		BigDecimal facPremium = null;
		if (facPrec != null && premium != null) {
			facPremium = premium.multiply(new BigDecimal(facPrec)).divide(new BigDecimal("100"));
		}
		return facPremium;
	}

	private BigDecimal facSuminuredCalc(String facPrec, BigDecimal sumInsured) {
		BigDecimal facSumInsured = null;
		if (facPrec != null && sumInsured != null) {
			facSumInsured = sumInsured.multiply(new BigDecimal(facPrec)).divide(new BigDecimal("100"));
		}
		return facSumInsured;

	}

	private BigDecimal pmlSuminuredCalc(String pmlPrec, BigDecimal sumInsured) {
		BigDecimal pmlSumInsured = null;
		if (pmlPrec != null && sumInsured != null) {
			pmlSumInsured = sumInsured.multiply(new BigDecimal(pmlPrec)).divide(new BigDecimal("100"));
		}
		return pmlSumInsured;
	}

	@Override
	public ViewReInsuranceDetails viewReinsuranceDetials(String quoteNo) {
		try {

			List<ReInsuranceRiskDetail> reInsuranceRiskDetaiList = new ArrayList<>();
			if (quoteNo != null && !quoteNo.isEmpty()) {
				ViewReInsuranceDetails viewReInsuranceDetails = new ViewReInsuranceDetails();
				HomePositionMaster homePositionMaster = homePositionMasterRepo.findByQuoteNo(quoteNo);
				viewReInsuranceDetails.setCurrency(homePositionMaster.getCurrency());
				viewReInsuranceDetails.setEndtFromDate(null);
				viewReInsuranceDetails.setEndtToDate(null);
				viewReInsuranceDetails.setFacPerc(homePositionMaster.getFacPrec());
				viewReInsuranceDetails.setPolicyFrom(homePositionMaster.getInceptionDate());
				viewReInsuranceDetails.setPolicyTo(homePositionMaster.getExpiryDate());
				viewReInsuranceDetails.setProductId(homePositionMaster.getProductId().toString());
				viewReInsuranceDetails.setProductName(homePositionMaster.getProductName());
				viewReInsuranceDetails.setRiStatus(homePositionMaster.getRiStatus());
				viewReInsuranceDetails.setQuoteNo(quoteNo);

				viewReInsuranceDetails.setRiBasisId(homePositionMaster.getRiBasisId());
				viewReInsuranceDetails.setRiBasisName(homePositionMaster.getRiBasisName());
				List<ReInsuranceRiskDetails> reInsuranceRiskDetailsList = reInsuranceRiskDetailsRepo
						.findByQuoteno(quoteNo);
				for (ReInsuranceRiskDetails reInsuranceRiskDetails : reInsuranceRiskDetailsList) {
					ReInsuranceRiskDetail reInsuranceRiskDetail = new ReInsuranceRiskDetail();
					reInsuranceRiskDetail.setCoverFACPerc(reInsuranceRiskDetails.getCoverFAC());
					reInsuranceRiskDetail.setCoverDAFPerc(reInsuranceRiskDetails.getCoverDAF());
					reInsuranceRiskDetail.setLocationName(reInsuranceRiskDetails.getLocationName());
					reInsuranceRiskDetail.setPmlPerc(
							reInsuranceRiskDetails.getPmlPerc() != null ? reInsuranceRiskDetails.getPmlPerc() : "0");
					reInsuranceRiskDetail.setRiskCategory(reInsuranceRiskDetails.getRiskCategory());
					reInsuranceRiskDetail.setRiskId(reInsuranceRiskDetails.getRiskId().toString());
					reInsuranceRiskDetail.setRiskRefNo(reInsuranceRiskDetails.getRiskId().toString());
					reInsuranceRiskDetail.setSectionId(reInsuranceRiskDetails.getSectionId().toString());
					reInsuranceRiskDetail.setSectionName(reInsuranceRiskDetails.getSectionName());

					List<ReinsuranceCoverDetails> reinsuranceCoverDetailsList = reinsuranceCoverDetailsRepo
							.findByQuotenoAndSectionIdAndRiskIdAndProductId(quoteNo,
									reInsuranceRiskDetails.getSectionId(), reInsuranceRiskDetails.getRiskId(),
									reInsuranceRiskDetails.getProductId());
					List<ReInsuranceCoverDetail> reInsuranceCoverDetailsList = new ArrayList<>();
					for (ReinsuranceCoverDetails reinsuranceCoverDetails : reinsuranceCoverDetailsList) {
						ReInsuranceCoverDetail reInsuranceCoverDetail = new ReInsuranceCoverDetail();
						reInsuranceCoverDetail.setCoverId(reinsuranceCoverDetails.getCoverId().toString());
						reInsuranceCoverDetail.setCoverName(reinsuranceCoverDetails.getCoverName());
						reInsuranceCoverDetail.setFacPerc(reinsuranceCoverDetails.getFacPercantage() != null
								? reinsuranceCoverDetails.getFacPercantage()
								: "0");
						reInsuranceCoverDetail.setFacPermium(reinsuranceCoverDetails.getFacPremiumlc() != null
								? reinsuranceCoverDetails.getFacPremiumlc().toPlainString()
								: "0");
						reInsuranceCoverDetail.setDafTreartyPerc(
								reinsuranceCoverDetails.getDafPerc() != null ? reinsuranceCoverDetails.getDafPerc()
										: "0");
						reInsuranceCoverDetail.setFacSumInsured(reinsuranceCoverDetails.getFacSuminsuredlc() != null
								? reinsuranceCoverDetails.getFacSuminsuredlc().toPlainString()
								: "0");
						reInsuranceCoverDetail.setPmlSumInsured(reinsuranceCoverDetails.getPmlSuminsuredlc() != null
								? reinsuranceCoverDetails.getPmlSuminsuredlc().toPlainString()
								: "0");
						reInsuranceCoverDetail.setPremium(reinsuranceCoverDetails.getPremiumlc() != null
								? reinsuranceCoverDetails.getPremiumlc().toPlainString()
								: "0");
						reInsuranceCoverDetail.setSumInsured(reinsuranceCoverDetails.getSuminsuredlc() != null
								? reinsuranceCoverDetails.getSuminsuredlc().toPlainString()
								: "0");
						reInsuranceCoverDetail.setFacpml(reinsuranceCoverDetails.getFacpmlSuminsuredlc() != null
								? reinsuranceCoverDetails.getFacpmlSuminsuredlc().toPlainString()
								: "0");
						reInsuranceCoverDetailsList.add(reInsuranceCoverDetail);
					}
					reInsuranceRiskDetail.setReInsuranceCoverDetails(reInsuranceCoverDetailsList);
					reInsuranceRiskDetaiList.add(reInsuranceRiskDetail);
				}

				viewReInsuranceDetails.setReInsuranceRiskDetails(reInsuranceRiskDetaiList);
				return viewReInsuranceDetails;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
		return null;
	}

	public CommonRes updateReInsurance(ViewReInsuranceDetails req) {
		CommonRes res = new CommonRes();
		try {
			Date today = new Date();

			List<ReInsuranceRiskDetails> reRiskList = reInsuranceRiskDetailsRepo.findByQuoteno(req.getQuoteNo());
			Map<String, ReInsuranceRiskDetails> reRiskMap = reRiskList.stream()
					.collect(Collectors.toMap(r -> r.getRiskId() + "-" + r.getSectionId(), r -> r));

			List<ReinsuranceCoverDetails> reCoverList = reinsuranceCoverDetailsRepo.findByQuoteno(req.getQuoteNo());
			Map<String, ReinsuranceCoverDetails> reCoverMap = reCoverList.stream().collect(
					Collectors.toMap(c -> c.getRiskId() + "-" + c.getSectionId() + "-" + c.getCoverId(), c -> c));

			List<ReinsuranceDiscLoadDetails> reDisLoadDetails = reinsuranceDiscLoadDetailsRepos
					.findByQuoteno(req.getQuoteNo());

			List<ReInsuranceRiskDetails> toSaveRisk = new ArrayList<>();
			List<ReinsuranceCoverDetails> toSaveCover = new ArrayList<>();

			for (ReInsuranceRiskDetail reIn : req.getReInsuranceRiskDetails()) {
				ReInsuranceRiskDetails reInsuranceRiskDetails = reRiskMap
						.get(reIn.getRiskId() + "-" + reIn.getSectionId());

				reInsuranceRiskDetails
						.setRiskrefno(reIn.getRiskRefNo() != null ? Integer.valueOf(reIn.getRiskRefNo()) : null);
				reInsuranceRiskDetails.setPmlPerc(reIn.getPmlPerc());
				reInsuranceRiskDetails.setRiskCategory(reIn.getRiskCategory());
				reInsuranceRiskDetails.setCoverFAC(reIn.getCoverFACPerc());
				reInsuranceRiskDetails.setUpdateDate(today);
				reInsuranceRiskDetails.setUpdatedBy(req.getCreatedBy());
				reInsuranceRiskDetails.setUwRiBasis(req.getRiBasisName());
				reInsuranceRiskDetails.setCoverDAF(reIn.getCoverDAFPerc());
				toSaveRisk.add(reInsuranceRiskDetails);

				for (ReInsuranceCoverDetail reInCoverDetail : reIn.getReInsuranceCoverDetails()) {

					ReinsuranceCoverDetails recover = reCoverMap
							.get(reIn.getRiskId() + "-" + reIn.getSectionId() + "-" + reInCoverDetail.getCoverId());

					recover.setDafPerc(reInCoverDetail.getDafTreartyPerc());
					recover.setFacPercantage(
							reInCoverDetail.getFacPerc() != null ? reInCoverDetail.getFacPerc() : null);
					recover.setFacpmlSuminsuredlc(
							reInCoverDetail.getFacpml() != null ? new BigDecimal(reInCoverDetail.getFacpml()) : null);
//					recover.setFacpmlSuminsuredfc(
//							reInCoverDetail.getFacpml() != null ? new BigDecimal(reInCoverDetail.getFacpml()) : null);
					recover.setFacPremiumlc(
							reInCoverDetail.getFacPermium() != null ? new BigDecimal(reInCoverDetail.getFacPermium())
									: null);
//					recover.setFacPremiumfc(
//							reInCoverDetail.getFacPermium() != null ? new BigDecimal(reInCoverDetail.getFacPermium())
//									: null);
					recover.setFacSuminsuredlc(reInCoverDetail.getFacSumInsured() != null
							? new BigDecimal(reInCoverDetail.getFacSumInsured())
							: null);
//					recover.setFacSuminsuredfc(reInCoverDetail.getFacSumInsured() != null
//							? new BigDecimal(reInCoverDetail.getFacSumInsured())
//							: null);
					recover.setPmlSuminsuredlc(reInCoverDetail.getPmlSumInsured() != null
							? new BigDecimal(reInCoverDetail.getPmlSumInsured())
							: null);
//					recover.setPmlSuminsuredfc(reInCoverDetail.getPmlSumInsured() != null
//							? new BigDecimal(reInCoverDetail.getPmlSumInsured())
//							: null);
					recover.setUpdatedDate(today);
					recover.setUpdatedBy(req.getCreatedBy());

					toSaveCover.add(recover);
				}
			}

			reDisLoadDetails.stream().forEach(d -> {
				d.setUpdatedBy(req.getCreatedBy());
				d.setUpdatedDate(today);
			});

			List<ReInsuranceRiskDetails> saveReinsuranceRiskDetails = reInsuranceRiskDetailsRepo.saveAll(toSaveRisk);
			List<ReinsuranceCoverDetails> saveCoverDetails = reinsuranceCoverDetailsRepo.saveAll(toSaveCover);
			List<ReinsuranceDiscLoadDetails> saveDisLoadDetails = reinsuranceDiscLoadDetailsRepos
					.saveAll(reDisLoadDetails);
			HomePositionMaster homePositionMaster = homePositionMasterRepo.findByQuoteNo(req.getQuoteNo());
			premiaPush(res, homePositionMaster, saveReinsuranceRiskDetails, saveCoverDetails, saveDisLoadDetails);
			homePositionMaster.setRiBasisId(req.getRiBasisId());
			homePositionMaster.setFacPrec(req.getFacPerc());

			if (res == null || res.getMessage() == null || res.getIsError()) {
				homePositionMaster.setRiStatus("RP");
			} else {
				homePositionMaster.setRiStatus(null);
			}
			homePositionMasterRepo.save(homePositionMaster);

			return res;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
			res.setMessage("ReInsurance Failed");
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(0);
			return res;
		}
	}

	private void premiaPush(CommonRes res, HomePositionMaster homePositionMaster,
			List<ReInsuranceRiskDetails> saveReinsuranceRiskDetails, List<ReinsuranceCoverDetails> saveCoverDetails,
			List<ReinsuranceDiscLoadDetails> saveDisLoadDetails) throws JsonProcessingException, JsonMappingException {
		ReInsurancePushReq pushReq = new ReInsurancePushReq();
		List<ReInsuranceCoversDetail> premiacovList = new ArrayList<>();
		List<ReInsuranceRisksDetails> premiariskDetail = new ArrayList<>();
		ReInsuranceSectionDetails premiaReinsuranceRisk = new ReInsuranceSectionDetails();
		List<ReInsuranceSectionDetails> premiaReInsuranceSectionDetails = new ArrayList<>();
		premiaRiskDetails(saveReinsuranceRiskDetails, pushReq, premiariskDetail, homePositionMaster);
		premiaCoverDetails(saveCoverDetails, pushReq, premiacovList, premiaReinsuranceRisk,
				premiaReInsuranceSectionDetails);
		premiaDiscLoadDetails(pushReq, homePositionMaster, saveDisLoadDetails);

		ResponseEntity<String> response = premiaCall(pushReq);
		if (response != null) {
			saveReinsuranceRiskDetails.stream().forEach(r -> {
				r.setPremiaResponse(response.getBody());
				r.setPremiaStatus("Success");
			});
			saveCoverDetails.stream().forEach(r -> {
				r.setPremiaResponse(response.getBody());
				r.setPremiaStatus("Success");
			});

			saveDisLoadDetails.stream().forEach(r -> {
				r.setPremiaResponse(response.getBody());
				r.setPremiaStatus("Success");
			});

			reInsuranceRiskDetailsRepo.saveAllAndFlush(saveReinsuranceRiskDetails);
			reinsuranceCoverDetailsRepo.saveAllAndFlush(saveCoverDetails);
			reinsuranceDiscLoadDetailsRepos.saveAllAndFlush(saveDisLoadDetails);
		}
		ObjectMapper mapper = new ObjectMapper();
		if (response != null && response.getBody() != null) {
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode path = root.path("data").path("Result").path("Response");
			String pSts = path.path("P_STS").asText();
			String pMsg = path.path("P_MSG").asText();
			res.setCommonResponse(pMsg);
			res.setMessage(pMsg);
			res.setIsError(pSts.equalsIgnoreCase("S") ? false : true);
			res.setErroCode(0);

		} else {
			res.setMessage("ReInsurance Procedure Failed");
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(0);
		}
	}

	@Override
	public CommonRes pushReInsuranceDetails(ReInsuranceQuoteReq req) {
		CommonRes res = new CommonRes();
		List<ReInsuranceRiskDetails> riskList = null;
		List<ReinsuranceCoverDetails> coverList = null;
		List<ReinsuranceDiscLoadDetails> discloadList = null;
		try {
			riskList = reInsuranceRiskDetailsRepo.findByQuoteno(req.getQuoteNo());
			coverList = reinsuranceCoverDetailsRepo.findByQuoteno(req.getQuoteNo());
			discloadList = reinsuranceDiscLoadDetailsRepos.findByQuoteno(req.getQuoteNo());
			HomePositionMaster homePositionMaster = homePositionMasterRepo.findByQuoteNo(req.getQuoteNo());
			if (!riskList.isEmpty() && !coverList.isEmpty()) {
				return premiaPush(res, riskList, coverList, homePositionMaster, discloadList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setMessage("Failed");
			res.setErrorMessage(null);
			riskList.stream().forEach(r -> {
				r.setPremiaResponse(null);
				r.setPremiaStatus("Failed");
			});
			coverList.stream().forEach(r -> {
				r.setPremiaResponse(null);
				r.setPremiaStatus("Failed");
			});
			discloadList.stream().forEach(r -> {
				r.setPremiaResponse(null);
				r.setPremiaStatus("Failed");
			});
			reInsuranceRiskDetailsRepo.saveAllAndFlush(riskList);
			reinsuranceCoverDetailsRepo.saveAllAndFlush(coverList);
			reinsuranceDiscLoadDetailsRepos.saveAll(discloadList);
			return null;
		}
		return null;
	}

	private CommonRes premiaPush(CommonRes res, List<ReInsuranceRiskDetails> riskList,
			List<ReinsuranceCoverDetails> coverList, HomePositionMaster homePositionMaster,
			List<ReinsuranceDiscLoadDetails> discloadList) throws JsonProcessingException {
		ReInsurancePushReq pushReq = new ReInsurancePushReq();
		List<ReInsuranceRisksDetails> listDetail = new ArrayList<>();
		List<ReInsuranceCoversDetail> covList = new ArrayList<>();
		ReInsuranceSectionDetails sec = new ReInsuranceSectionDetails();
		List<ReInsuranceSectionDetails> secList = new ArrayList<>();

		// RiskDetails
		premiaRiskDetails(riskList, pushReq, listDetail, homePositionMaster);

		// Section and Cover List

		premiaCoverDetails(coverList, pushReq, covList, sec, secList);

		premiaDiscLoadDetails(pushReq, homePositionMaster, discloadList);

		ResponseEntity<String> response = premiaReinsuranaceCall(riskList, coverList, pushReq, discloadList);

		res.setCommonResponse(response.getBody());
		res.setMessage("Success");
		res.setErrorMessage(null);
		res.setErroCode(0);
		return res;
	}

	private ResponseEntity<String> premiaReinsuranaceCall(List<ReInsuranceRiskDetails> riskList,
			List<ReinsuranceCoverDetails> coverList, ReInsurancePushReq pushReq,
			List<ReinsuranceDiscLoadDetails> discloadList) throws JsonProcessingException {
		ResponseEntity<String> response = premiaCall(pushReq);

		riskList.stream().forEach(r -> {
			r.setPremiaResponse(response.getBody());
			r.setPremiaStatus("Success");
		});
		coverList.stream().forEach(r -> {
			r.setPremiaResponse(response.getBody());
			r.setPremiaStatus("Success");
		});
		discloadList.stream().forEach(r -> {
			r.setPremiaResponse(response.getBody());
			r.setPremiaStatus("Success");
		});

		reInsuranceRiskDetailsRepo.saveAll(riskList);
		reinsuranceCoverDetailsRepo.saveAll(coverList);
		reinsuranceDiscLoadDetailsRepos.saveAll(discloadList);
		return response;
	}

//    private ResponseEntity<String> premiaCall(ReInsurancePushReq pushReq) throws JsonProcessingException {
//        ResponseEntity<String> response = null;
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            String pushRequest = mapper.writeValueAsString(pushReq);
//            System.out.println("Push Premia Request: " + pushRequest);
//            RestTemplate restTemp = new RestTemplate();
//            HttpHeaders header = new HttpHeaders();
//            header.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<?> requestent = new HttpEntity<>(pushRequest, header);
//
//            if(pushReq.getCompanyId().equalsIgnoreCase("100046")) {
//            	response = restTemp.exchange(zambiaReInsuranceApi, HttpMethod.POST, requestent, String.class);
//            } else if(pushReq.getCompanyId().equalsIgnoreCase("100047")) {
//            	response = restTemp.exchange(botswanaReInsuranceApi, HttpMethod.POST, requestent, String.class);
//            } else if(pushReq.getCompanyId().equalsIgnoreCase("100048")) {
//            	response = restTemp.exchange(mozambiqueReInsuranceApi, HttpMethod.POST, requestent, String.class);
//            } else if(pushReq.getCompanyId().equalsIgnoreCase("100049")) {
//            	response = restTemp.exchange(swazilandReInsuranceApi, HttpMethod.POST, requestent, String.class);
//            } else if(pushReq.getCompanyId().equalsIgnoreCase("100050")) {
//            	response = restTemp.exchange(namibiaReInsuranceApi, HttpMethod.POST, requestent, String.class);
//            } 
//            
//
//            System.out.println("response Api:  " + response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("REInsurance Details" + e.getMessage());
//        }
//        return response;
//    }

	private ResponseEntity<String> premiaCall(ReInsurancePushReq pushReq) throws JsonProcessingException {
		ResponseEntity<String> response = null;

		String targetUrl = resolveTargetUrl(pushReq != null ? pushReq.getCompanyId() : null);

		if (targetUrl == null) {
			log.error("Invalid or unsupported Company ID: {}", pushReq != null ? pushReq.getCompanyId() : "NULL");
			return ResponseEntity.badRequest().body("Unsupported Company ID");
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			String pushRequest = mapper.writeValueAsString(pushReq);
			log.info("Push Premia Request to [{}]: {}", targetUrl, pushRequest);

			// 2. Configure RestTemplate with strict timeouts
			SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			factory.setConnectTimeout(3000); // 3 seconds connect timeout
			factory.setReadTimeout(5000);
			RestTemplate restTemp = new RestTemplate(factory);

			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestent = new HttpEntity<>(pushRequest, header);

			// 3. Make the API call
			response = restTemp.exchange(targetUrl, HttpMethod.POST, requestent, String.class);

			if (response != null && response.getBody() != null) {
				log.info("Response API: {}", response.getBody());
			}

		} catch (org.springframework.web.client.ResourceAccessException e) {
			// Triggered when API is DOWN, connection timed out, or DNS failed
			log.error("External API is DOWN or Unreachable at URL [{}]. Error: {}", targetUrl, e.getMessage());
			return null;

		} catch (org.springframework.web.client.HttpStatusCodeException e) {
			// Triggered when API is UP, but responded with HTTP 4xx or 5xx error
			log.error("External API returned error code [{}] for URL [{}]. Response: {}", e.getStatusCode(), targetUrl,
					e.getResponseBodyAsString());
			return null;

		} catch (Exception e) {
			log.error("Unexpected error during ReInsurance push call: ", e);
			return null;
		}

		return response;
	}

	private String resolveTargetUrl(String companyId) {
		if (companyId == null)
			return null;

		switch (companyId) {
		case "100046":
			return zambiaReInsuranceApi;
		case "100047":
			return botswanaReInsuranceApi;
		case "100048":
			return mozambiqueReInsuranceApi;
		case "100049":
			return swazilandReInsuranceApi;
		case "100050":
			return namibiaReInsuranceApi;
		default:
			return null;
		}
	}

	private void premiaCoverDetails(List<ReinsuranceCoverDetails> coverList, ReInsurancePushReq pushReq,
			List<ReInsuranceCoversDetail> covList, ReInsuranceSectionDetails sec,
			List<ReInsuranceSectionDetails> secList) {
		try {

			// Group covers by sectionId + riskId
			Map<String, List<ReinsuranceCoverDetails>> groupedCovers = coverList.stream()
					.collect(Collectors.groupingBy(c -> c.getSectionId() + "-" + c.getRiskId()));

			for (Map.Entry<String, List<ReinsuranceCoverDetails>> entry : groupedCovers.entrySet()) {
				List<ReinsuranceCoverDetails> groupedCoverList = entry.getValue();
				ReinsuranceCoverDetails firstCover = groupedCoverList.get(0);

				ReInsuranceSectionDetails sectionDetail = new ReInsuranceSectionDetails();
				sectionDetail
						.setSectionId(firstCover.getSectionId() == null ? null : firstCover.getSectionId().toString());
				sectionDetail.setSectionName(firstCover.getSectionName() == null ? null : firstCover.getSectionName());
				sectionDetail.setRiskId(firstCover.getRiskId() == null ? null : firstCover.getRiskId().toString());

				List<ReInsuranceCoversDetail> coverDetailList = new ArrayList<>();

				for (ReinsuranceCoverDetails covDetail : groupedCoverList) {
					ReInsuranceCoversDetail cov = new ReInsuranceCoversDetail();
					cov.setCoverId(covDetail.getCoverId() == null ? null : covDetail.getCoverId().toString());
					cov.setCoverName(covDetail.getCoverName() == null ? null : covDetail.getCoverName());
					cov.setSectionId(covDetail.getSectionId() == null ? null : covDetail.getSectionId());
					cov.setSectionName(covDetail.getSectionName() == null ? null : covDetail.getSectionName());
					cov.setInceptionDate(covDetail.getInceptionDate() == null ? null : covDetail.getInceptionDate());
					cov.setExpiryDate(covDetail.getExpiryDate() == null ? null : covDetail.getExpiryDate());
					cov.setEndStartDate(covDetail.getEndoStartDate() == null ? null : covDetail.getEndoStartDate());
					cov.setEndEndDate(covDetail.getEndoEndDate() == null ? null : covDetail.getEndoEndDate());
					cov.setWarYn(covDetail.getWaryn() == null ? null : covDetail.getWaryn());
					cov.setSumInsuredFc(
							covDetail.getSumInsuredfc() == null ? null : covDetail.getSumInsuredfc().toString());
					cov.setSumInsuredLc(
							covDetail.getSuminsuredlc() == null ? null : covDetail.getSuminsuredlc().toString());
					cov.setPremiumLc(covDetail.getPremiumlc() == null ? null : covDetail.getPremiumlc().toString());
					cov.setPremiumFc(covDetail.getPremiumfc() == null ? null : covDetail.getPremiumfc().toString());
					cov.setDafPercentage(covDetail.getDafPerc() == null ? null : covDetail.getDafPerc().toString());
					cov.setFacPercentage(
							covDetail.getFacPercantage() == null ? null : covDetail.getFacPercantage().toString());
					cov.setPmlSumInsuredLc(
							covDetail.getPmlSuminsuredlc() == null ? null : covDetail.getPmlSuminsuredlc().toString());
					cov.setPmlSumInsuredFc(
							covDetail.getPmlSuminsuredfc() == null ? null : covDetail.getPmlSuminsuredfc().toString());
					cov.setFacSumInsuredLc(
							covDetail.getFacSuminsuredlc() == null ? null : covDetail.getFacSuminsuredlc().toString());
					cov.setFacSumInsuredFc(
							covDetail.getFacSuminsuredfc() == null ? null : covDetail.getFacSuminsuredfc().toString());
					cov.setFacPmlSumInsuredLc(covDetail.getFacpmlSuminsuredlc() == null ? null
							: covDetail.getFacpmlSuminsuredlc().toString());
					cov.setFacPmlSumInsuredFc(covDetail.getFacpmlSuminsuredfc() == null ? null
							: covDetail.getFacpmlSuminsuredfc().toString());
					cov.setFacPremiumLc(
							covDetail.getFacPremiumlc() == null ? null : covDetail.getFacPremiumlc().toString());
					cov.setFacPremiumFc(
							covDetail.getFacPremiumfc() == null ? null : covDetail.getFacPremiumfc().toString());
					cov.setStatus(covDetail.getStatus() == null ? null : covDetail.getStatus());
					cov.setEntryDate(covDetail.getEntryDate() == null ? null : covDetail.getEntryDate());
					cov.setCreatedBy(covDetail.getCreatedBy() == null ? null : covDetail.getCreatedBy());
					cov.setPremiaStatus(covDetail.getPremiaStatus() == null ? null : covDetail.getPremiaStatus());
					cov.setPremiaResponse(covDetail.getPremiaResponse() == null ? null : covDetail.getPremiaResponse());
					cov.setUpdateDate(covDetail.getUpdatedDate() == null ? null : covDetail.getUpdatedDate());
					cov.setUpdatedBy(covDetail.getUpdatedBy() == null ? null : covDetail.getUpdatedBy());
					cov.setCoverCode(covDetail.getCoverCode() == null ? null : covDetail.getCoverCode());
					cov.setSectionCode(covDetail.getSectionCode() == null ? null : covDetail.getSectionCode());
					cov.setCoverRecType(covDetail.getCoverRecType() == null ? null : covDetail.getCoverRecType());
					coverDetailList.add(cov);
				}

				sectionDetail.setCoverList(coverDetailList);
				secList.add(sectionDetail);
			}

			pushReq.setSectionList(secList);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("ReInsurance Details" + e.getMessage());
		}
	}

	private void premiaRiskDetails(List<ReInsuranceRiskDetails> riskList, ReInsurancePushReq pushReq,
			List<ReInsuranceRisksDetails> listDetail, HomePositionMaster homePositionMaster) {
		try {
			pushReq.setQuoteNo(riskList.get(0).getQuoteno());
			pushReq.setUwSysId(homePositionMaster.getUwsysId().toString());
			pushReq.setAhPolIdx(homePositionMaster.getAhpolIdx().toString());
			pushReq.setCompanyId(riskList.get(0).getCompanyId() == null ? null : riskList.get(0).getCompanyId());
			pushReq.setCompanyCode(riskList.get(0).getCompanyCode() == null ? null : riskList.get(0).getCompanyCode());
			pushReq.setProductId(
					riskList.get(0).getProductId() == null ? null : riskList.get(0).getProductId().toString());
			pushReq.setProductName(riskList.get(0).getProductName() == null ? null : riskList.get(0).getProductName());
			pushReq.setProductCode(riskList != null && !riskList.isEmpty() ? riskList.get(0).getProductCode() : null);
			pushReq.setPolicyNo(riskList != null && !riskList.isEmpty() ? riskList.get(0).getPolicyNo() : null);
			for (ReInsuranceRiskDetails riskDetails : riskList) {
				ReInsuranceRisksDetails risk = new ReInsuranceRisksDetails();
				risk.setSectionId(riskDetails.getSectionId() == null ? null : riskDetails.getSectionId().toString());
				risk.setSectionName(riskDetails.getSectionName() == null ? null : riskDetails.getSectionName());
				risk.setRiskId(riskDetails.getRiskId() == null ? null : riskDetails.getRiskId().toString());
				risk.setLocationName(riskDetails.getLocationName() == null ? null : riskDetails.getLocationName());
				risk.setRiskAddress(riskDetails.getRiskaddress() == null ? null : riskDetails.getRiskaddress());
				risk.setRiskCategory(riskDetails.getRiskCategory() == null ? null : riskDetails.getRiskCategory());
				risk.setRiskRefNo(riskDetails.getRiskrefno() == null ? null : riskDetails.getRiskrefno().toString());
				risk.setCoverFac(riskDetails.getCoverFAC() == null ? null : riskDetails.getCoverFAC());
				risk.setPmlPercentage(riskDetails.getPmlPerc() == null ? null : riskDetails.getPmlPerc());
				risk.setInceptionDate(riskDetails.getInceptionDate() == null ? null : riskDetails.getInceptionDate());
				risk.setExpiryDate(riskDetails.getExpireDate() == null ? null : riskDetails.getExpireDate());
				risk.setEndStartDate(riskDetails.getEndoStartDate() == null ? null : riskDetails.getEndoStartDate());
				risk.setEndEndDate(riskDetails.getEndoEndDate() == null ? null : riskDetails.getEndoEndDate());
				risk.setStatus(riskDetails.getStatus() == null ? null : riskDetails.getStatus());
				risk.setEntryDate(riskDetails.getEntryDate() == null ? null : riskDetails.getEntryDate());
				risk.setCreatedBy(riskDetails.getCreatedBy() == null ? null : riskDetails.getCreatedBy());
				risk.setPremiaStatus(riskDetails.getPremiaStatus() == null ? null : riskDetails.getPremiaStatus());
				risk.setEndtTypeId(riskDetails.getEndtTypeId() == null ? null : riskDetails.getEndtTypeId());
				risk.setEndtTypeName(riskDetails.getEndtTypeName() == null ? null : riskDetails.getEndtTypeName());
				risk.setPremiaResponse(
						riskDetails.getPremiaResponse() == null ? null : riskDetails.getPremiaResponse());
				risk.setUpdateDate(riskDetails.getUpdateDate() == null ? null : riskDetails.getUpdateDate());
				risk.setUpdatedBy(riskDetails.getUpdatedBy() == null ? null : riskDetails.getUpdatedBy());
				risk.setSectionCode(riskDetails.getSectionCode() == null ? null : riskDetails.getSectionCode());
				risk.setUwBusType(riskDetails.getUwBusType() == null ? null : riskDetails.getUwBusType());
				risk.setUwDeptId(riskDetails.getUwDeptId() == null ? null : riskDetails.getUwDeptId());
				risk.setUwDivnId(riskDetails.getUwDivnId() == null ? null : riskDetails.getUwDivnId());
				risk.setUwLob(riskDetails.getUwLob() == null ? null : riskDetails.getUwLob());
				risk.setUwRiBasis(riskDetails.getUwRiBasis() == null ? null : riskDetails.getUwRiBasis());
				risk.setUwType(riskDetails.getUwType() == null ? null : riskDetails.getUwType());
				risk.setPolicyNo(riskDetails.getPolicyNo() == null ? null : riskDetails.getPolicyNo());
				risk.setRiskSiCurr(riskDetails.getRiskSiCurr() == null ? null : riskDetails.getRiskSiCurr());
				risk.setRiskRecType(riskDetails.getRiskRecType() == null ? null : riskDetails.getRiskRecType());
				risk.setBranchName(riskDetails.getBranchName() == null ? null : riskDetails.getBranchName());
				risk.setUwDivnCode(riskDetails.getUwDivnCode() == null ? null : riskDetails.getUwDivnCode());
				risk.setUwDeptCode(riskDetails.getUwDeptCode() == null ? null : riskDetails.getUwDeptCode());
				risk.setApprovalStatus(
						riskDetails.getApprovalStatus() == null ? null : riskDetails.getApprovalStatus());
				risk.setApprovalDate(riskDetails.getApprovalDate() == null ? null : riskDetails.getApprovalDate());
				risk.setApprovalUserId(
						riskDetails.getApprovalUserId() == null ? null : riskDetails.getApprovalUserId());
				risk.setEndtTypeCategory(
						riskDetails.getEndtTypeCategory() == null ? null : riskDetails.getEndtTypeCategory());
				risk.setEndtTypeCategoryId(riskDetails.getEndtTypeCategoryId() == null ? null
						: riskDetails.getEndtTypeCategoryId().toString());
				risk.setEndtNo(riskDetails.getEndtNo() == null ? null : riskDetails.getEndtNo());
				risk.setInsuredName(riskDetails.getInsuredName() == null ? null : riskDetails.getInsuredName());
				risk.setInsuredCode(riskDetails.getInsuredCode() == null ? null : riskDetails.getInsuredCode());
				risk.setAgBrkCode(riskDetails.getAgBrkCode() == null ? null : riskDetails.getAgBrkCode());
				risk.setAgBrkName(riskDetails.getAgBrkName() == null ? null : riskDetails.getAgBrkName());
				risk.setEndtTypeCode(riskDetails.getEndtTypeCode() == null ? null : riskDetails.getEndtTypeCode());
				risk.setUwPremCur(riskDetails.getUwPremCur() == null ? null : riskDetails.getUwPremCur());
				listDetail.add(risk);
			}
			pushReq.setRiskList(listDetail);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
	}

	private void premiaDiscLoadDetails(ReInsurancePushReq pushReq, HomePositionMaster homePositionMaster,
			List<ReinsuranceDiscLoadDetails> saveDisLoadDetails) {
		try {
			List<ReInsuranceDisLoadDetails> discLoadList = new ArrayList<>();
			for (ReinsuranceDiscLoadDetails reinsuranceDiscLoadDetails : saveDisLoadDetails) {
				ReInsuranceDisLoadDetails reInsuranceDisLoadDetails = new ReInsuranceDisLoadDetails();
				reInsuranceDisLoadDetails.setCoverCode(reinsuranceDiscLoadDetails.getCoverCode() == null ? null
						: reinsuranceDiscLoadDetails.getCoverCode());
				reInsuranceDisLoadDetails.setCoverId(reinsuranceDiscLoadDetails.getCoverId() == null ? null
						: reinsuranceDiscLoadDetails.getCoverId());
				reInsuranceDisLoadDetails.setCoverRecType(reinsuranceDiscLoadDetails.getCoverRecType() == null ? null
						: reinsuranceDiscLoadDetails.getCoverRecType());
				reInsuranceDisLoadDetails.setCreatedBy(reinsuranceDiscLoadDetails.getCreatedBy() == null ? null
						: reinsuranceDiscLoadDetails.getCreatedBy());
				reInsuranceDisLoadDetails.setCreatedDate(reinsuranceDiscLoadDetails.getCreatedDate() == null ? null
						: reinsuranceDiscLoadDetails.getCreatedDate());
				reInsuranceDisLoadDetails.setDiscLoadCode(reinsuranceDiscLoadDetails.getDiscLoadCode() == null ? null
						: reinsuranceDiscLoadDetails.getDiscLoadCode());
				reInsuranceDisLoadDetails.setDiscLoadFc(reinsuranceDiscLoadDetails.getDiscLoadFc() == null ? null
						: reinsuranceDiscLoadDetails.getDiscLoadFc().toString());
				reInsuranceDisLoadDetails.setDiscLoadLc(reinsuranceDiscLoadDetails.getDiscLoadLc() == null ? null
						: reinsuranceDiscLoadDetails.getDiscLoadLc().toString());
				reInsuranceDisLoadDetails.setDiscLoadId(reinsuranceDiscLoadDetails.getDiscLoadId() == null ? null
						: reinsuranceDiscLoadDetails.getDiscLoadId());
				reInsuranceDisLoadDetails.setDiscLoadType(reinsuranceDiscLoadDetails.getDiscLoadType() == null ? null
						: reinsuranceDiscLoadDetails.getDiscLoadType());
				reInsuranceDisLoadDetails
						.setPremiaResponse(reinsuranceDiscLoadDetails.getPremiaResponse() == null ? null
								: reinsuranceDiscLoadDetails.getPremiaResponse());
				reInsuranceDisLoadDetails.setPremiaStatus(reinsuranceDiscLoadDetails.getPremiaStatus() == null ? null
						: reinsuranceDiscLoadDetails.getPremiaStatus());
				reInsuranceDisLoadDetails.setRiskId(
						reinsuranceDiscLoadDetails.getRiskId() == null ? null : reinsuranceDiscLoadDetails.getRiskId());
				reInsuranceDisLoadDetails.setSectionCode(reinsuranceDiscLoadDetails.getSectionCode() == null ? null
						: reinsuranceDiscLoadDetails.getSectionCode());
				reInsuranceDisLoadDetails.setSectionId(reinsuranceDiscLoadDetails.getSectionId() == null ? null
						: reinsuranceDiscLoadDetails.getSectionId());
				reInsuranceDisLoadDetails.setStatus(
						reinsuranceDiscLoadDetails.getStatus() == null ? null : reinsuranceDiscLoadDetails.getStatus());
				reInsuranceDisLoadDetails.setUpdateDate(reinsuranceDiscLoadDetails.getUpdatedDate() == null ? null
						: reinsuranceDiscLoadDetails.getUpdatedDate());
				reInsuranceDisLoadDetails.setUpdatedBy(reinsuranceDiscLoadDetails.getUpdatedBy() == null ? null
						: reinsuranceDiscLoadDetails.getUpdatedBy());
				discLoadList.add(reInsuranceDisLoadDetails);
			}

			pushReq.setDiscLoadList(discLoadList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}

	}

	@Override
	public CommonRes updatePolicyNo(ReInsuranceQuoteReq req) {
		CommonRes res = new CommonRes();
		List<ReInsuranceRiskDetails> riskList = null;
		List<ReinsuranceCoverDetails> coverList = null;
		List<ReinsuranceDiscLoadDetails> discloadList = null;
		try {
			Date today = new Date();
			HomePositionMaster homePositionMaster = homePositionMasterRepo.findByQuoteNo(req.getQuoteNo());
			riskList = reInsuranceRiskDetailsRepo.findByQuoteno(req.getQuoteNo());
			coverList = reinsuranceCoverDetailsRepo.findByQuoteno(req.getQuoteNo());
			discloadList = reinsuranceDiscLoadDetailsRepos.findByQuoteno(req.getQuoteNo());
			riskList.stream().forEach(r -> {
				r.setPolicyNo(homePositionMaster.getOriginalPolicyNo());
				r.setApprovalDate(today);
				// r.setApprovalUserId(req.getCreatedBy());
				r.setApprovalStatus("A");
				r.setRiskRecType("N");
			});
			coverList.stream().forEach(r -> {
				r.setCoverRecType("N");
			});
			discloadList.stream().forEach(r -> {
				r.setCoverRecType("N");
			});

			if (!riskList.isEmpty() && !coverList.isEmpty()) {
				homePositionMaster.setRiStatus("A");
				homePositionMasterRepo.save(homePositionMaster);
				return premiaPush(res, riskList, coverList, homePositionMaster, discloadList);

			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setMessage("ReInsurance Failed");
			res.setErrorMessage(null);
			riskList.stream().forEach(r -> {
				r.setPremiaResponse(null);
				r.setPremiaStatus("Failed");
			});
			coverList.stream().forEach(r -> {
				r.setPremiaResponse(null);
				r.setPremiaStatus("Failed");
			});
			discloadList.stream().forEach(r -> {
				r.setPremiaResponse(null);
				r.setPremiaStatus("Failed");
			});

			reInsuranceRiskDetailsRepo.saveAllAndFlush(riskList);
			reinsuranceCoverDetailsRepo.saveAllAndFlush(coverList);
			reinsuranceDiscLoadDetailsRepos.saveAllAndFlush(discloadList);
			return null;
		}
		return null;
	}

	@Override
	public ReInsuranceCommonRes updateReInsuranceApprovedStatus(ReInsuranceApprovedReq req) {
		ReInsuranceCommonRes res = new ReInsuranceCommonRes();
		try {
			HomePositionMaster homePositionMaster = homePositionMasterRepo
					.findByAhpolIdxAndUwsysId(Long.valueOf(req.getPolidx()), Long.valueOf(req.getUwsysId()));
			if (homePositionMaster != null) {
				if (req.getFreezeYN().equalsIgnoreCase("Y")) {
					homePositionMaster.setRiStatus("RA");
					homePositionMasterRepo.save(homePositionMaster);
				} else {
					homePositionMaster.setRiStatus("RP");
					homePositionMasterRepo.save(homePositionMaster);
				}
			}
			res.setMessage("Reinsurance successfully approved");
			res.setIsError(false);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
			res.setMessage("Reinsurance approval failed");
			res.setIsError(true);
		}
		return res;
	}

	@Override
	public ReInsuranceUnfreezeRes updateReInsuranceUnfreezeStatus(ReInsuranceFreezeReq req) {
		ReInsuranceUnfreezeRes res = new ReInsuranceUnfreezeRes();
		try {
			HomePositionMaster homePositionMaster = homePositionMasterRepo.findByQuoteNo(req.getQuoteNo());
			if (homePositionMaster != null) {
				res.setFreezeYN("N");
				res.setPolidx(
						homePositionMaster.getAhpolIdx() != null ? homePositionMaster.getAhpolIdx().toString() : null);
				res.setUwsysId(
						homePositionMaster.getUwsysId() != null ? homePositionMaster.getUwsysId().toString() : null);
				res.setQuoteNo(req.getQuoteNo());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("REInsurance Details" + e.getMessage());
		}
		return res;
	}

	public List<SectionCoverRes> getSectionCovers(String companyId) {

		String sql = """
				SELECT distinct
				            a.product_id,
				            c.product_name,
				            a.section_id,
				            a.section_name,
				            e.cover_id,
				            e.cover_name,a.effective_date_start , a.effective_date_end
				        FROM product_section_master a,
				             company_product_master c,
				            section_cover_master e
				        WHERE a.company_id = c.company_id
				          AND a.product_id = c.product_id
				          AND a.company_id = e.company_id
				          AND a.product_id = e.product_id
				          AND a.section_id = e.section_id
				          AND a.company_id=:companyId
				          AND c.amend_id = (
				              SELECT MAX(d.amend_id)
				              FROM company_product_master d
				              WHERE d.company_id = c.company_id
				                AND d.product_id = c.product_id
				          )
				          AND e.amend_id = (
				              SELECT MAX(f.amend_id)
				              FROM section_cover_master f
				              WHERE f.company_id = c.company_id
				                AND f.product_id = c.product_id
				                AND f.section_id = a.section_id
				                AND f.cover_id = e.cover_id
				          )
				          AND SYSDATE() BETWEEN a.effective_date_start AND a.effective_date_end
				          AND a.amend_id = (
				              SELECT MAX(b.amend_id)
				              FROM product_section_master b
				              WHERE b.company_id = a.company_id
				                AND b.product_id = a.product_id
				                AND b.section_id = a.section_id
				          )
				        ORDER BY a.product_id, a.section_id, e.cover_id ASC;""";

		Query query = em.createNativeQuery(sql);
		query.setParameter("companyId", companyId);

		List<Object[]> rows = query.getResultList();
		List<SectionCoverRes> result = new ArrayList<>();

		for (Object[] row : rows) {
			SectionCoverRes dto = new SectionCoverRes();

			dto.setProductId(((Number) row[0]).longValue());
			dto.setProductName((String) row[1]);
			dto.setSectionId(((Number) row[2]).longValue());
			dto.setSectionName((String) row[3]);
			dto.setCoverId(((Number) row[4]).longValue());
			dto.setCoverName((String) row[5]);
			dto.setEffectiveDateStart((java.util.Date) row[6]);
			dto.setEffectiveDateEnd((java.util.Date) row[7]);

			result.add(dto);
		}
		return result;
	}

}