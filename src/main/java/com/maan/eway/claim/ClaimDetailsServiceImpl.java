package com.maan.eway.claim;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EwayDivisionDepartment;
import com.maan.eway.bean.EwayLobMaster;
import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.claimintimation.dto.ClaimCoInsurancedetailsData;
import com.maan.eway.claimintimation.dto.ClaimRequest;
import com.maan.eway.coinsurance.Repo.CoinsuranceHeaderRepo;
import com.maan.eway.coinsurance.bean.CoInsuranceHeader;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchPremiumCoverDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.SearchServiceImpl;
import com.maan.eway.excelupload.bean.AdditionalInformation;
import com.maan.eway.excelupload.bean.TemplateEntity;
import com.maan.eway.excelupload.repository.AdditionalInformationRepository;
import com.maan.eway.excelupload.repository.TemplateRepository;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EwayDivisionDepartmentRepository;
import com.maan.eway.repository.EwayLobMasterRepository;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.SectionCoverMasterRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
@Service
public class ClaimDetailsServiceImpl implements ClaimDetailsService {

	@Autowired
	private SearchServiceImpl searchServiceImpl;

	@Autowired
	private HomePositionMasterRepository homePositionMasterRepo;

	@Autowired
	private QuoteService quoteService;

	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepo;

	@Autowired
	private CoverDetailsRepository coverRepo;

	@Autowired
	private SectionDataDetailsRepository sectionDataDetailsRepo;

	@Autowired
	private PolicyCoverDataRepository policyRepo;

	@Autowired
	private CoinsuranceHeaderRepo coinsuranceHeaderRepo;

	@Autowired
	private PersonalInfoRepository personalInfoRepo;

	@Autowired
	private FactorRateRequestDetailsRepository factorRateRequestDetailsRepo;

	@Autowired
	private EwayLobMasterRepository ewayLobMasterRepos;

	@Autowired
	private CompanyProductMasterRepository companyProductMasterRepo;

	@Autowired
	private ProductSectionMasterRepository productSectionMasterRepo;

	@Autowired
	private SectionCoverMasterRepository coverMasterRepo;

	@Autowired
	private EwayDivisionDepartmentRepository ewayDivisionDepartmentRepos;

	@Autowired
	private InsuranceCompanyMasterRepository insuranceCompanyMasterRepos;

	@Autowired
	private BranchMasterRepository branchMasterRepos;

	@Autowired
	private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepos;

	@Autowired
	private ExcessMasterRepository excessMasterRepos;

	@Autowired
	private TemplateRepository templateRepos;

	@Autowired
	private AdditionalInformationRepository additionalInformationRepo;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public List<PolicyDetailsResponseDto> policydetailsbyregno(PolicyDetailsReq req) {

		List<PolicyDetailsResponseDto> responseList = new ArrayList<PolicyDetailsResponseDto>();

		try {

			HomePositionMaster home = homePositionMasterRepo
					.findByPolicyNoAndStatusAndCompanyId(req.getQuotationPolicyNo(), "P", req.getCompanyId());

			if (home != null) {
				PersonalInfo personalInfo = personalInfoRepo.findByCustomerId(home.getCustomerId());
				ViewQuoteDetailsReq quoteReq = new ViewQuoteDetailsReq();
				quoteReq.setInsuranceId(home.getCompanyId());
				quoteReq.setProductId(home.getProductId().toString());
				quoteReq.setQuoteNo(home.getQuoteNo());
				quoteReq.setRequestReferenceNo(home.getRequestReferenceNo());
				ViewQuoteDetailsRes viewQuoteDetails = searchServiceImpl.viewQuoteDetails(quoteReq);

				SearchReq customerDetailsReq = new SearchReq();
				customerDetailsReq.setApplicationId(home.getApplicationId());
				customerDetailsReq.setBranchCode(home.getBranchCode());
				customerDetailsReq.setInsuranceId(home.getCompanyId());
				customerDetailsReq.setLoginId(home.getLoginId());
				customerDetailsReq.setProductId(home.getProductId().toString());
				customerDetailsReq.setQuoteNo(home.getQuoteNo());
				customerDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
				customerDetailsReq.setSearchValue(home.getQuoteNo());
				List<SearchCustomerDetailsRes> adminCustomerSearch = searchServiceImpl
						.adminCustomerSearch(customerDetailsReq);

				SearchReq vehicleDetailsReq = new SearchReq();
				vehicleDetailsReq.setInsuranceId(home.getCompanyId());
				vehicleDetailsReq.setProductId(home.getProductId().toString());
				vehicleDetailsReq.setQuoteNo(home.getQuoteNo());
				vehicleDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
				SearchROPVehicleDetailsRes adminROPVehicleSearch = new SearchROPVehicleDetailsRes();

				adminROPVehicleSearch = searchServiceImpl.adminROPVehicleSearch(vehicleDetailsReq);

				responseList = claimResponseMapper(viewQuoteDetails, adminCustomerSearch.get(0),
						adminROPVehicleSearch.getVehDetails(), home, personalInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseList;
	}

	private List<PolicyDetailsResponseDto> claimResponseMapper(ViewQuoteDetailsRes viewQuoteDetails,
			SearchCustomerDetailsRes adminCustomerSearch, List<SearchROPVehicleRes> vehDetails, HomePositionMaster home,
			PersonalInfo personalInfo) {
		List<PolicyDetailsResponseDto> resList = new ArrayList<>();

		PolicyDetailsResponseDto response = new PolicyDetailsResponseDto();

		PolicyInfoDetailsDto policyInfo = new PolicyInfoDetailsDto();

		policyInfo.setPolicyNo(viewQuoteDetails.getPolicyNo());
		policyInfo.setPolicyFrom(viewQuoteDetails.getInceptionDate());
		policyInfo.setPolicyTo(viewQuoteDetails.getExpiryDate());
		policyInfo.setProductDesc(home.getProductName());
		policyInfo.setProductcode(home.getProductId().toString());
		policyInfo.setCustomerCode(viewQuoteDetails.getCustomerCode());
		policyInfo.setContactPerName(viewQuoteDetails.getCustomerName());
		policyInfo.setCivilId(adminCustomerSearch.getIdNumber());
		policyInfo.setSectionCode(home.getSectionId() != null ? home.getSectionId().toString() : null);
		policyInfo.setCurrencyCode(viewQuoteDetails.getCurrency());
		policyInfo.setAddress(adminCustomerSearch.getAddress1());
		policyInfo.setOccupation(adminCustomerSearch.getOccupationDesc());
		policyInfo.setEmail(adminCustomerSearch.getEmail1());
		policyInfo.setMobileNumber(adminCustomerSearch.getMobileNo1());
		policyInfo.setCompanyId(adminCustomerSearch.getCompanyId());
		policyInfo.setCompanyName(adminCustomerSearch.getCompanyName());
		policyInfo.setProductType(adminCustomerSearch.getProductType());
		policyInfo.setCustomer(adminCustomerSearch.getClientName());
		policyInfo.setBranchCode(home.getBranchCode());
		policyInfo.setProduct(home.getProductName());
		policyInfo.setBrokerCode(home.getBrokerCode());
		policyInfo.setBranch(home.getBranchName());
		policyInfo.setRegionCode(personalInfo.getRegionCode());
		policyInfo.setCityName(personalInfo.getCityName());
		policyInfo.setMobileCode(personalInfo.getMobileCode1());
		policyInfo.setNationality(personalInfo.getNationality());
		policyInfo.setSourceType(home.getSourceType());
		policyInfo.setSourceTypeId(home.getSourceTypeId());
		for (SearchROPVehicleRes veh : vehDetails) {

			MotorRes vehicleInfo = new MotorRes();

			vehicleInfo.setSeats(veh.getSeatingCapacity() != null ? veh.getSeatingCapacity().toString() : null);
			vehicleInfo.setVehicleMakeModel(veh.getResMake() + " / " + veh.getResModel());
			vehicleInfo.setPlateNoCharacter(veh.getResRegNumber());
			vehicleInfo.setChassisNo(veh.getResRegNumber());
			vehicleInfo.setVehicletypedesc(veh.getResBodyType());
			vehicleInfo.setVehtype(veh.getMotorDesc());
			vehicleInfo.setVehmake(veh.getResMake());
			vehicleInfo.setVehbodytype(veh.getResBodyType());
			vehicleInfo.setManufactureyear(
					veh.getResYearOfManufacture() != null ? veh.getResYearOfManufacture().toString() : null);
			vehicleInfo.setVehiclecc(null);
			vehicleInfo.setSeating(veh.getSeatingCapacity() != null ? veh.getSeatingCapacity().toString() : null);
			vehicleInfo.setTonnage(veh.getTareWeight() != null ? veh.getTareWeight().toString()
					: veh.getGrossWeight() != null ? veh.getGrossWeight().toString() : null);
			vehicleInfo.setVechregno(veh.getResRegNumber());
			vehicleInfo.setVehmodel(veh.getResModel());
			response.setVehicleInfo(vehicleInfo);

		}
		response.setPolicyInfo(policyInfo);
		resList.add(response);
		return resList;
	}

	private NonMotorRes getNonMotorDetails(HomePositionMaster home) {
		NonMotorRes nonMotorRes = new NonMotorRes();
		try {
			List<NonMotorLocRes> nonMotorLocResList = new ArrayList<>();
			List<SectionDataDetails> sectiondatadetails = sectionDataDetailsRepo.findByQuoteNo(home.getQuoteNo());

			Set<Integer> locset = new HashSet<>();

			List<SectionDataDetails> locationList = sectiondatadetails.stream()
					.filter(s -> locset.add(s.getLocationId())).collect(Collectors.toList());

			for (SectionDataDetails Loc : locationList) {
				NonMotorLocRes nonMotorLocRes = new NonMotorLocRes();
				nonMotorLocRes.setLocationId(Loc.getLocationId().toString());
				nonMotorLocRes.setLocationName(Loc.getLocationName());
				nonMotorLocRes.setAddress(Loc.getLocationName());
				nonMotorLocRes.setRiskId(Loc.getRiskId().toString());
				nonMotorLocRes.setCoversRequiredYn(null);
				nonMotorLocRes.setBuildingOwnerYn(null);
				Set<String> secSet = new HashSet<>();
				List<SectionDataDetails> sectionLocList = sectionDataDetailsRepo
						.findByPolicyNoAndLocationId(home.getPolicyNo(), Loc.getLocationId());
				List<SectionDataDetails> sectionList = sectionLocList.stream().filter(s -> secSet.add(s.getSectionId()))
						.collect(Collectors.toList());
				List<NonMotorSectionRes> nonMotorSectionResList = new ArrayList<>();
				for (SectionDataDetails sec : sectionList) {
					List<ProductSectionMaster> productSectionMasterList = productSectionMasterRepo
							.findByProductIdAndCompanyIdAndSectionIdAndStatusOrderByAmendIdDesc(home.getProductId(),
									home.getCompanyId(), Integer.valueOf(sec.getSectionId()), "Y");
					NonMotorSectionRes nonMotorSectionRes = new NonMotorSectionRes();
					nonMotorSectionRes.setSectionId(sec.getSectionId());
					nonMotorSectionRes.setSectionCode(productSectionMasterList.get(0).getCoreAppCode());
					nonMotorSectionRes.setSectionDesc(sec.getSectionDesc());
					nonMotorSectionRes.setSectionType(sec.getProductType());
					List<PolicyCoverData> coverList = coverRepo.findByQuoteNoAndSectionIdAndLocationId(
							home.getQuoteNo(), Integer.valueOf(sec.getSectionId()), sec.getLocationId());
					List<PolicyCoverData> collect;
					if ("852".equals(home.getEndtTypeId())) {
						Set<String> excludedTypes = Set.of("T", "L", "D", "E");
						collect = coverList
								.stream().filter(a -> !excludedTypes.contains(a.getCoverageType().toUpperCase())

										// NOT EXISTS condition
										&& coverList.stream()
												.noneMatch(b -> a.getQuoteNo().equals(b.getQuoteNo())
														&& a.getProductId().equals(b.getProductId())
														&& a.getSectionId().equals(b.getSectionId())
														&& a.getCoverId().equals(b.getCoverId())
														&& a.getLocationId().equals(b.getLocationId())
														&& Integer.valueOf(852).equals(b.getDiscLoadId())
														&& b.getSumInsured() != null
														&& b.getSumInsured().compareTo(BigDecimal.ZERO) != 0))
								.collect(Collectors.toList());
					} else {
						collect = coverList.stream()
								.filter(c -> !c.getCoverageType().equalsIgnoreCase("T")
										&& !c.getCoverageType().equalsIgnoreCase("L")
										&& !c.getCoverageType().equalsIgnoreCase("D")
										&& !c.getCoverageType().equalsIgnoreCase("E"))
								.collect(Collectors.toList());
					}
					List<NonMotorCoverRes> nonMotorCoverResList = new ArrayList<>();
					for (PolicyCoverData policyCoverData : collect) {
						List<SectionCoverMaster> sectionCoverMasterList = coverMasterRepo
								.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
										home.getCompanyId(), home.getProductId(), Integer.valueOf(sec.getSectionId()),
										policyCoverData.getCoverId());
						NonMotorCoverRes nonMotorCoverRes = new NonMotorCoverRes();
						nonMotorCoverRes.setCoverageLimit(policyCoverData.getCoverageLimit().toPlainString());
						nonMotorCoverRes.setCoverageType(policyCoverData.getCoverageType());
						nonMotorCoverRes.setCoverId(policyCoverData.getCoverId().toString());
						nonMotorCoverRes.setSuminsured(policyCoverData.getSumInsured().toPlainString());
						nonMotorCoverRes.setPremium(policyCoverData.getPremiumIncludedTaxLc().toPlainString());
						nonMotorCoverRes.setCoverName(policyCoverData.getCoverName());
						nonMotorCoverRes.setCoverCode(sectionCoverMasterList.get(0).getCoreAppCode());

						fetchExcessDetails(home, sec, policyCoverData, nonMotorCoverRes);
						List<AdditionalInformationRes> additionalInformationList = additionalInformation(home,
								policyCoverData);
						nonMotorCoverRes.setAdditionalInformationList(additionalInformationList);
						nonMotorCoverResList.add(nonMotorCoverRes);
					}
					nonMotorSectionRes.setCoverList(nonMotorCoverResList);
					nonMotorSectionResList.add(nonMotorSectionRes);

				}
				nonMotorLocRes.setSectionList(nonMotorSectionResList);
				nonMotorLocResList.add(nonMotorLocRes);
			}
			nonMotorRes.setLocationList(nonMotorLocResList);

		} catch (Exception e) {
			System.out.println("***********The Exception Occured in GetMotorDetails  Api ***********");
			e.printStackTrace();
			return null;
		}

		return nonMotorRes;
	}

	@Override
	public PolicyDetailsResponseDto policydetails(PolicyDetailsReq req) {

		PolicyDetailsResponseDto policyDetailsResponseDto = new PolicyDetailsResponseDto();
		try {
			HomePositionMaster home = homePositionMasterRepo
					.findByPolicyNoAndStatusAndCompanyId(req.getQuotationPolicyNo(), "P", req.getCompanyId());

			if (home != null) {
				// View Quote details
				ViewQuoteDetailsReq quoteReq = new ViewQuoteDetailsReq();
				quoteReq.setInsuranceId(home.getCompanyId());
				quoteReq.setProductId(home.getProductId().toString());
				quoteReq.setQuoteNo(home.getQuoteNo());
				quoteReq.setRequestReferenceNo(home.getRequestReferenceNo());
				ViewQuoteDetailsRes viewQuoteDetails = searchServiceImpl.viewQuoteDetails(quoteReq);

				// Customer Details
				SearchReq customerDetailsReq = new SearchReq();
				customerDetailsReq.setApplicationId(home.getApplicationId());
				customerDetailsReq.setBranchCode(home.getBranchCode());
				customerDetailsReq.setInsuranceId(home.getCompanyId());
				customerDetailsReq.setLoginId(home.getLoginId());
				customerDetailsReq.setProductId(home.getProductId().toString());
				customerDetailsReq.setQuoteNo(home.getQuoteNo());
				customerDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
				customerDetailsReq.setSearchValue(home.getQuoteNo());
				List<SearchCustomerDetailsRes> adminCustomerSearch = searchServiceImpl
						.adminCustomerSearch(customerDetailsReq);

				// vehicle Details
				SearchReq vehicleDetailsReq = new SearchReq();
				vehicleDetailsReq.setInsuranceId(home.getCompanyId());
				vehicleDetailsReq.setProductId(home.getProductId().toString());
				vehicleDetailsReq.setQuoteNo(home.getQuoteNo());
				vehicleDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
				SearchROPVehicleDetailsRes adminROPVehicleSearch = searchServiceImpl
						.adminROPVehicleSearch(vehicleDetailsReq);

				// Mapping eway response to claim response
				policyDetailsResponseDto = policyDetailsResponseDtomapper(viewQuoteDetails, adminCustomerSearch.get(0),
						adminROPVehicleSearch.getVehDetails().get(0), home);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return policyDetailsResponseDto;
	}

	private PolicyDetailsResponseDto policyDetailsResponseDtomapper(ViewQuoteDetailsRes viewQuoteDetails,
			SearchCustomerDetailsRes adminCustomerSearch, SearchROPVehicleRes vehDetails, HomePositionMaster home) {

		PolicyDetailsResponseDto response = new PolicyDetailsResponseDto();

		PolicyInfoDetailsDto policyInfo = new PolicyInfoDetailsDto();
		policyInfo.setPolicyNo(viewQuoteDetails.getPolicyNo());
		policyInfo.setPolicyFrom(viewQuoteDetails.getInceptionDate());
		policyInfo.setPolicyTo(viewQuoteDetails.getExpiryDate());
		policyInfo.setProductDesc(home.getProductName());
		policyInfo.setProductcode(home.getProductId().toString());
		policyInfo.setCustomerCode(viewQuoteDetails.getCustomerCode());
		policyInfo.setContactPerName(viewQuoteDetails.getCustomerName());
		policyInfo.setCivilId(adminCustomerSearch.getIdNumber());
		policyInfo.setSectionCode(home.getSectionId() != null ? home.getSectionId().toString() : null);
		policyInfo.setCurrencyCode(viewQuoteDetails.getCurrency());
		policyInfo.setAddress(adminCustomerSearch.getAddress1());
		policyInfo.setOccupation(adminCustomerSearch.getOccupationDesc());
		policyInfo.setEmail(adminCustomerSearch.getEmail1());
		policyInfo.setMobileNumber(adminCustomerSearch.getMobileNo1());
		policyInfo.setSourceType(home.getSourceType());
		policyInfo.setSourceTypeId(home.getSourceTypeId());

		MotorRes vehicleInfo = new MotorRes();

		vehicleInfo
				.setSeats(vehDetails.getSeatingCapacity() != null ? vehDetails.getSeatingCapacity().toString() : null);
		vehicleInfo.setVehicleMakeModel(vehDetails.getResMake() + " / " + vehDetails.getResModel());
		vehicleInfo.setPlateNoCharacter(vehDetails.getResRegNumber());
		vehicleInfo.setChassisNo(vehDetails.getResRegNumber());
		vehicleInfo.setVehicletypedesc(vehDetails.getResBodyType());
		vehicleInfo.setVehtype(vehDetails.getMotorDesc());
		vehicleInfo.setVehmake(vehDetails.getResMake());
		vehicleInfo.setVehbodytype(vehDetails.getResBodyType());
		vehicleInfo.setManufactureyear(
				vehDetails.getResYearOfManufacture() != null ? vehDetails.getResYearOfManufacture().toString() : null);
		vehicleInfo.setVehiclecc(null);
		vehicleInfo.setSeating(
				vehDetails.getSeatingCapacity() != null ? vehDetails.getSeatingCapacity().toString() : null);
		vehicleInfo.setTonnage(vehDetails.getTareWeight() != null ? vehDetails.getTareWeight().toString()
				: vehDetails.getGrossWeight() != null ? vehDetails.getGrossWeight().toString() : null);
		vehicleInfo.setVechregno(vehDetails.getResRegNumber());
		vehicleInfo.setVehmodel(vehDetails.getResModel());
		response.setPolicyInfo(policyInfo);
		response.setVehicleInfo(vehicleInfo);
		return response;
	}

	@Override
	public ViewQuoteRes claimViewQuoteDetails(PolicyDetailsReq req) {

		String quoteNo = "";

		if (StringUtils.isNoneBlank(req.getQuotationPolicyNo())) {
			HomePositionMaster home = homePositionMasterRepo
					.findByPolicyNoAndStatusAndCompanyId(req.getQuotationPolicyNo(), "P", req.getCompanyId());
			System.out.println("Requet ==> " + req);
			quoteNo = home.getQuoteNo();
		}

		ViewQuoteReq request = new ViewQuoteReq();
		request.setQuoteNo(quoteNo);

		return quoteService.viewQuoteDetails(request);
	}

	@Override
	public List<ClaimCoverdetailsData> getCoverList(PolicyDetailsReq req) {
		List<ClaimCoverdetailsData> claimCoverdetailsDataList = new ArrayList<>();
		try {
			HomePositionMaster home = homePositionMasterRepo
					.findByPolicyNoAndStatusAndCompanyId(req.getQuotationPolicyNo(), "P", req.getCompanyId());

			if (home != null) {
				List<MotorDataDetails> motorid = motorDataDetailsRepo
						.findByQuoteNoOrderByVehicleIdAsc(home.getQuoteNo());
				List<PolicyCoverData> covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(home.getQuoteNo());
				for (MotorDataDetails mot : motorid) {
					// Cover Details
					List<PolicyCoverData> filterCovers = covers.stream()
							.filter(o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId())))
							.collect(Collectors.toList());

					Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
							.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

					List<SearchPremiumCoverDetailsRes> coverListRes = searchServiceImpl.getCoverDetails(groupByCover);

					for (SearchPremiumCoverDetailsRes coverDetailsRes : coverListRes) {
						List<SectionCoverMaster> sectionCoverMasterList = coverMasterRepo
								.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
										home.getCompanyId(), home.getProductId(),
										Integer.valueOf(coverDetailsRes.getSectionId()),
										Integer.valueOf(coverDetailsRes.getCoverId()));
						List<ProductSectionMaster> productSectionMasterList = productSectionMasterRepo
								.findByProductIdAndCompanyIdAndSectionIdAndStatusOrderByAmendIdDesc(home.getProductId(),
										home.getCompanyId(), Integer.valueOf(coverDetailsRes.getSectionId()), "Y");
						CompanyProductMaster companyProductMaster = companyProductMasterRepo
								.findByProductIdAndCompanyIdAndStatus(home.getProductId(), home.getCompanyId(), "Y");
						ClaimCoverdetailsData claimCoverdetailsData = new ClaimCoverdetailsData();
						claimCoverdetailsData.setSectionId(coverDetailsRes.getSectionId());
						claimCoverdetailsData.setSectionName(coverDetailsRes.getSectionName());
						claimCoverdetailsData.setProductId(home.getProductId().toString());
						claimCoverdetailsData.setProductName(home.getProductName());
						claimCoverdetailsData.setSectionCode(productSectionMasterList.get(0).getCoreAppCode());
						claimCoverdetailsData.setProductCode(companyProductMaster.getCoreAppCode());
						claimCoverdetailsData.setCoverId(coverDetailsRes.getCoverId());
						claimCoverdetailsData.setCoverCode(sectionCoverMasterList.get(0).getCoreAppCode());
						claimCoverdetailsData.setCoverName(coverDetailsRes.getCoverName());
						claimCoverdetailsData.setPremium(coverDetailsRes.getPremiumIncludedTaxLC().toPlainString());
						claimCoverdetailsData.setSuminsured(coverDetailsRes.getSumInsured().toPlainString());
						claimCoverdetailsData.setCoverageLimit(coverDetailsRes.getCoverageLimit());
						claimCoverdetailsData.setCoverageType(coverDetailsRes.getCoverageType());
						claimCoverdetailsData.setExcessAmount(coverDetailsRes.getExcessAmount());
						claimCoverdetailsData.setExcessPercent(coverDetailsRes.getExcessPercent());
						claimCoverdetailsDataList.add(claimCoverdetailsData);
					}
				}
			}
			return claimCoverdetailsDataList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<NetRes> getsumInsured(NetReq req) {
		List<NetRes> res = new ArrayList<>();
		try {
			List<PolicyCoverData> policy = policyRepo
					.findByPolicyNoAndSectionIdAndCoverIdAndVehicleIdAndCompanyIdOrderByCoverIdAsc(req.getPolicyNo(),
							Integer.valueOf(req.getSectionCode()), Integer.valueOf(req.getCoverId()),
							Integer.valueOf(req.getVechileId()), req.getCompanyId());
			if (policy != null && !policy.isEmpty()) {
				List<NetRes> net = policy.stream().map(en -> {
					NetRes resp = new NetRes();
					resp.setCompanyId(en.getCompanyId());
					resp.setCoverId(en.getCoverId().toString());
					resp.setPolicyNo(en.getPolicyNo());
					resp.setSumInsured(en.getSumInsured() == null ? "0.0" : en.getSumInsured().toPlainString());
					resp.setSectionCode(en.getSectionId().toString());
					resp.setError("0");
					res.add(resp);
					return resp;
				}).collect(Collectors.toList());

			}

		} catch (Exception e) {
			e.printStackTrace();
			return res;
		}
		return res;
	}

	@Override
	public ClaimCoInsurancedetailsData getCoinsurancedetailsist(ClaimRequest req) {
		ClaimCoInsurancedetailsData claimCoInsurancedetailsData = new ClaimCoInsurancedetailsData();
		try {
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			HomePositionMaster home = homePositionMasterRepo
					.findByPolicyNoAndStatusAndCompanyId(req.getQuotationPolicyNo(), "P", req.getInsuranceId());

			if (home != null) {
				CoInsuranceHeader coInsuranceHeader = coinsuranceHeaderRepo.findByQuoteNo(home.getQuoteNo());
				if (coInsuranceHeader != null) {
					dozerMapper.map(coInsuranceHeader, claimCoInsurancedetailsData);
				}
			}
			return claimCoInsurancedetailsData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PolicyInfoDetailsDto customerDetails(PolicyDetailsReq req) {
		try {
			HomePositionMaster home = homePositionMasterRepo.findByPolicyNoAndStatusInAndCompanyId(
					req.getQuotationPolicyNo(), Arrays.asList("P", "E"), req.getCompanyId());

			if (home != null) {
				EwayLobMaster ewayLobMaster = ewayLobMasterRepos.findByCompnayIdAndProductId(home.getCompanyId(),
						home.getProductId().toString());
				CompanyProductMaster companyProductMaster = companyProductMasterRepo
						.findByProductIdAndCompanyIdAndStatus(home.getProductId(), req.getCompanyId(), "Y");

				InsuranceCompanyMaster insuranceCompanyMaster = insuranceCompanyMasterRepos
						.findByCompanyId(req.getCompanyId());

				EwayDivisionDepartment ewayDivisionDepartment = ewayDivisionDepartmentRepos
						.findByCompnayIdAndProductId(home.getCompanyId(), home.getProductId().toString());

				BranchMaster branchMaster = branchMasterRepos
						.findByBranchCodeAndCompanyIdAndStatus(home.getBranchCode(), home.getCompanyId(), "Y");

				PersonalInfo personalInfo = personalInfoRepo.findByCustomerId(home.getCustomerId());

				EserviceCustomerDetails eserviceCustomerDetails = eserviceCustomerDetailsRepos
						.findByCustomerReferenceNo(personalInfo.getCustomerReferenceNo());

				PolicyInfoDetailsDto policyInfo = new PolicyInfoDetailsDto();

				SearchReq customerDetailsReq = new SearchReq();
				customerDetailsReq.setApplicationId(home.getApplicationId());
				customerDetailsReq.setBranchCode(home.getBranchCode());
				customerDetailsReq.setInsuranceId(home.getCompanyId());
				customerDetailsReq.setLoginId(home.getLoginId());
				customerDetailsReq.setProductId(home.getProductId().toString());
				customerDetailsReq.setQuoteNo(home.getQuoteNo());
				customerDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
				customerDetailsReq.setSearchValue(home.getQuoteNo());
				List<SearchCustomerDetailsRes> adminCustomerSearchList = searchServiceImpl
						.adminCustomerSearch(customerDetailsReq);
				SearchCustomerDetailsRes adminCustomerSearch = adminCustomerSearchList.get(0);
				ViewQuoteDetailsReq quoteReq = new ViewQuoteDetailsReq();
				quoteReq.setInsuranceId(home.getCompanyId());
				quoteReq.setProductId(home.getProductId().toString());
				quoteReq.setQuoteNo(home.getQuoteNo());
				quoteReq.setRequestReferenceNo(home.getRequestReferenceNo());
				ViewQuoteDetailsRes viewQuoteDetails = searchServiceImpl.viewQuoteDetails(quoteReq);

				policyInfo.setPolicyNo(viewQuoteDetails.getPolicyNo());
				policyInfo.setPolicyFrom(viewQuoteDetails.getInceptionDate());
				policyInfo.setPolicyTo(viewQuoteDetails.getExpiryDate());
				policyInfo.setProductDesc(home.getProductName());
				policyInfo.setProductcode(companyProductMaster.getCoreAppCode());
				policyInfo.setCustomerCode(viewQuoteDetails.getCustomerCode());
				policyInfo.setContactPerName(viewQuoteDetails.getCustomerName());
				policyInfo.setCivilId(adminCustomerSearch.getIdNumber());
				policyInfo.setSectionCode(home.getSectionId() != null ? home.getSectionId().toString() : null);
				policyInfo.setCurrencyCode(viewQuoteDetails.getCurrency());
				policyInfo.setAddress(adminCustomerSearch.getAddress1());
				policyInfo.setOccupation(adminCustomerSearch.getOccupationDesc());
				policyInfo.setEmail(adminCustomerSearch.getEmail1());
				policyInfo.setMobileNumber(adminCustomerSearch.getMobileNo1());
				policyInfo.setCompanyId(adminCustomerSearch.getCompanyId());
				policyInfo.setCompanyName(adminCustomerSearch.getCompanyName());
				policyInfo.setProductType(adminCustomerSearch.getProductType());
				policyInfo.setCustomer(adminCustomerSearch.getClientName());
				policyInfo.setBranchCode(home.getBranchCode());
				policyInfo.setProduct(home.getProductName());
				policyInfo.setBrokerCode(home.getBrokerCode());
				policyInfo.setBranch(home.getBranchName());
				policyInfo.setRegionCode(personalInfo.getRegionCode());
				policyInfo.setMobileCode(personalInfo.getMobileCode1());
				policyInfo.setNationality(personalInfo.getNationality());
				policyInfo.setUwlob(ewayLobMaster != null ? ewayLobMaster.getLobCode() : null);
				policyInfo.setUwSysId(home.getUwsysId() != null ? home.getUwsysId().toString() : null);
				policyInfo.setPolidx(home.getAhpolIdx() != null ? home.getAhpolIdx().toString() : null);
				policyInfo.setProductId(home.getProductId().toString());
				policyInfo.setCompanyCode(insuranceCompanyMaster.getCoreAppCode());
				policyInfo.setBrokerName(home.getBdmName());
				policyInfo.setCustCode(eserviceCustomerDetails.getPolCustCode());
				policyInfo.setCustName(eserviceCustomerDetails.getClientName());
				policyInfo.setDivisionCode(branchMaster.getCoreAppCode());
				policyInfo.setDeptCode(
						ewayDivisionDepartment != null ? ewayDivisionDepartment.getDepartmentCode() : null);
				policyInfo.setCityCode(personalInfo.getCityCode());
				policyInfo.setCityName(personalInfo.getCityName());
				policyInfo.setPolicyHolderTypeDesc(personalInfo.getPolicyHolderTypeDesc());
				policyInfo.setPolicyHolderTypeId(personalInfo.getPolicyHolderTypeid());
				policyInfo.setIdNumber(personalInfo.getIdNumber());
				policyInfo.setIdType(personalInfo.getIdType());
				policyInfo.setIdTypeDesc(personalInfo.getIdTypeDesc());
				policyInfo.setPhoneNo(personalInfo.getMobileNo1());
				policyInfo.setDistrict(null);
				policyInfo.setStateCode(personalInfo.getStateCode());
				policyInfo.setStateName(personalInfo.getStateName());
				policyInfo.setStreet(personalInfo.getStreet());
				policyInfo.setPostalNo(personalInfo.getPinCode());
				policyInfo.setGenderDesc(personalInfo.getGenderDesc());
				policyInfo.setAge(personalInfo.getAge() != null ? personalInfo.getAge().toString() : null);
				policyInfo.setDob(personalInfo.getDobOrRegDate());
				policyInfo.setSourceType(home.getSourceType());
				policyInfo.setSourceTypeId(home.getSourceTypeId());

				return policyInfo;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public List<NonMotorPolicyDetailsResponseDto> nonMotorpolicydetails(PolicyDetailsReq req) {

		List<NonMotorPolicyDetailsResponseDto> responseList = new ArrayList<NonMotorPolicyDetailsResponseDto>();

		try {

			HomePositionMaster home = homePositionMasterRepo
					.findByPolicyNoAndStatusAndCompanyId(req.getQuotationPolicyNo(), "P", req.getCompanyId());

			if (home != null) {
				if (home.getProductId().compareTo(5) != 0) {
					PersonalInfo personalInfo = personalInfoRepo.findByCustomerId(home.getCustomerId());
					ViewQuoteDetailsReq quoteReq = new ViewQuoteDetailsReq();
					quoteReq.setInsuranceId(home.getCompanyId());
					quoteReq.setProductId(home.getProductId().toString());
					quoteReq.setQuoteNo(home.getQuoteNo());
					quoteReq.setRequestReferenceNo(home.getRequestReferenceNo());
					ViewQuoteDetailsRes viewQuoteDetails = searchServiceImpl.viewQuoteDetails(quoteReq);

					SearchReq customerDetailsReq = new SearchReq();
					customerDetailsReq.setApplicationId(home.getApplicationId());
					customerDetailsReq.setBranchCode(home.getBranchCode());
					customerDetailsReq.setInsuranceId(home.getCompanyId());
					customerDetailsReq.setLoginId(home.getLoginId());
					customerDetailsReq.setProductId(home.getProductId().toString());
					customerDetailsReq.setQuoteNo(home.getQuoteNo());
					customerDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
					customerDetailsReq.setSearchValue(home.getQuoteNo());
					List<SearchCustomerDetailsRes> adminCustomerSearch = searchServiceImpl
							.adminCustomerSearch(customerDetailsReq);

					SearchReq vehicleDetailsReq = new SearchReq();
					vehicleDetailsReq.setInsuranceId(home.getCompanyId());
					vehicleDetailsReq.setProductId(home.getProductId().toString());
					vehicleDetailsReq.setQuoteNo(home.getQuoteNo());
					vehicleDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());

					responseList = claimNonMotorResponseMapper(viewQuoteDetails, adminCustomerSearch.get(0), home,
							personalInfo);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseList;
	}

	private List<NonMotorPolicyDetailsResponseDto> claimNonMotorResponseMapper(ViewQuoteDetailsRes viewQuoteDetails,
			SearchCustomerDetailsRes adminCustomerSearch, HomePositionMaster home, PersonalInfo personalInfo) {
		List<NonMotorPolicyDetailsResponseDto> resList = new ArrayList<>();

		NonMotorPolicyDetailsResponseDto response = new NonMotorPolicyDetailsResponseDto();

		CompanyProductMaster companyProductMaster = companyProductMasterRepo
				.findByProductIdAndCompanyIdAndStatus(home.getProductId(), adminCustomerSearch.getCompanyId(), "Y");

		EwayDivisionDepartment ewayDivisionDepartment = ewayDivisionDepartmentRepos
				.findByCompnayIdAndProductId(home.getCompanyId(), home.getProductId().toString());

		InsuranceCompanyMaster insuranceCompanyMaster = insuranceCompanyMasterRepos
				.findByCompanyId(adminCustomerSearch.getCompanyId());

		PolicyInfoDetailsDto policyInfo = new PolicyInfoDetailsDto();

		policyInfo.setPolicyNo(viewQuoteDetails.getPolicyNo());
		policyInfo.setPolicyFrom(viewQuoteDetails.getInceptionDate());
		policyInfo.setPolicyTo(viewQuoteDetails.getExpiryDate());
		policyInfo.setProductDesc(home.getProductName());
		policyInfo.setProductcode(companyProductMaster.getCoreAppCode());
		policyInfo.setCustomerCode(viewQuoteDetails.getCustomerCode());
		policyInfo.setContactPerName(viewQuoteDetails.getCustomerName());
		policyInfo.setCivilId(adminCustomerSearch.getIdNumber());
		policyInfo.setSectionCode(home.getSectionId() != null ? home.getSectionId().toString() : null);
		policyInfo.setCurrencyCode(viewQuoteDetails.getCurrency());
		policyInfo.setAddress(adminCustomerSearch.getAddress1());
		policyInfo.setOccupation(adminCustomerSearch.getOccupationDesc());
		policyInfo.setEmail(adminCustomerSearch.getEmail1());
		policyInfo.setMobileNumber(adminCustomerSearch.getMobileNo1());
		policyInfo.setCompanyId(adminCustomerSearch.getCompanyId());
		policyInfo.setCompanyName(adminCustomerSearch.getCompanyName());
		policyInfo.setProductType(adminCustomerSearch.getProductType());
		policyInfo.setCustomer(adminCustomerSearch.getClientName());
		policyInfo.setBranchCode(home.getBranchCode());
		policyInfo.setProduct(home.getProductName());
		policyInfo.setBrokerCode(home.getBrokerCode());
		policyInfo.setBranch(home.getBranchName());
		policyInfo.setRegionCode(personalInfo.getRegionCode());
		policyInfo.setCityName(personalInfo.getCityName());
		policyInfo.setMobileCode(personalInfo.getMobileCode1());
		policyInfo.setNationality(personalInfo.getNationality());
		policyInfo.setProductId(home.getProductId().toString());
		policyInfo.setCompanyCode(insuranceCompanyMaster.getCoreAppCode());
		policyInfo.setBrokerName(home.getBdmName());
		policyInfo.setCustCode(home.getCustomerCode());
		policyInfo.setCustName(home.getCustomerName());
		policyInfo.setDeptCode(ewayDivisionDepartment != null ? ewayDivisionDepartment.getDepartmentCode() : null);
		policyInfo.setCityCode(personalInfo.getCityCode());
		policyInfo.setCityName(personalInfo.getCityName());
		policyInfo.setPolicyHolderTypeDesc(personalInfo.getPolicyHolderTypeDesc());
		policyInfo.setPolicyHolderTypeId(personalInfo.getPolicyHolderTypeid());
		policyInfo.setIdNumber(personalInfo.getIdNumber());
		policyInfo.setIdType(personalInfo.getIdType());
		policyInfo.setIdTypeDesc(personalInfo.getIdTypeDesc());
		policyInfo.setPhoneNo(personalInfo.getMobileNo1());
		policyInfo.setDistrict(null);
		policyInfo.setStateCode(personalInfo.getStateCode());
		policyInfo.setStateName(personalInfo.getStateName());
		policyInfo.setStreet(personalInfo.getStreet());
		policyInfo.setPostalNo(personalInfo.getPinCode());
		policyInfo.setGenderDesc(personalInfo.getGenderDesc());
		policyInfo.setAge(personalInfo.getAge() != null ? personalInfo.getAge().toString() : null);
		policyInfo.setDob(personalInfo.getDobOrRegDate());
		policyInfo.setSourceType(home.getSourceType());
		policyInfo.setSourceTypeId(home.getSourceTypeId());

		NonMotorRes res = getNonMotorDetails(home);
		response.setNonmotoInfo(res);

		response.setPolicyInfo(policyInfo);
		resList.add(response);
		return resList;
	}

	@Override
	public NonMotorRes nonMotorDetails(PolicyDetailsReq req) {
		NonMotorRes nonMotorRes = new NonMotorRes();
		try {
			HomePositionMaster home = homePositionMasterRepo
					.findByPolicyNoAndStatusAndCompanyId(req.getQuotationPolicyNo(), "P", req.getCompanyId());
			if (home != null) {
				if (home.getProductId().compareTo(5) != 0) {
					List<NonMotorLocRes> nonMotorLocResList = new ArrayList<>();
					List<SectionDataDetails> sectiondatadetails = sectionDataDetailsRepo
							.findByQuoteNo(home.getQuoteNo());
					Set<Integer> locset = new HashSet<>();
					List<SectionDataDetails> locationList = sectiondatadetails.stream()
							.filter(s -> locset.add(s.getLocationId())).collect(Collectors.toList());

					for (SectionDataDetails Loc : locationList) {
						NonMotorLocRes nonMotorLocRes = new NonMotorLocRes();
						nonMotorLocRes.setLocationId(Loc.getLocationId().toString());
						nonMotorLocRes.setLocationName(Loc.getLocationName());
						nonMotorLocRes.setAddress(Loc.getLocationName());
						nonMotorLocRes.setRiskId(Loc.getRiskId().toString());
						nonMotorLocRes.setCoversRequiredYn(null);
						nonMotorLocRes.setBuildingOwnerYn(null);
						Set<String> secSet = new HashSet<>();
						List<SectionDataDetails> sectionLocList = sectionDataDetailsRepo
								.findByPolicyNoAndLocationId(home.getPolicyNo(), Loc.getLocationId());
						List<SectionDataDetails> sectionList = sectionLocList.stream()
								.filter(s -> secSet.add(s.getSectionId())).collect(Collectors.toList());
						List<NonMotorSectionRes> nonMotorSectionResList = new ArrayList<>();
						for (SectionDataDetails sec : sectionList) {
							List<ProductSectionMaster> productSectionMasterList = productSectionMasterRepo
									.findByProductIdAndCompanyIdAndSectionIdAndStatusOrderByAmendIdDesc(
											home.getProductId(), home.getCompanyId(),
											Integer.valueOf(sec.getSectionId()), "Y");
							NonMotorSectionRes nonMotorSectionRes = new NonMotorSectionRes();
							nonMotorSectionRes.setSectionId(sec.getSectionId());
							nonMotorSectionRes.setSectionDesc(sec.getSectionDesc());
							nonMotorSectionRes.setSectionType(sec.getProductType());
							nonMotorSectionRes.setSectionCode(productSectionMasterList.get(0).getCoreAppCode());
							List<PolicyCoverData> coverList = coverRepo.findByQuoteNoAndSectionIdAndLocationId(
									home.getQuoteNo(), Integer.valueOf(sec.getSectionId()), sec.getLocationId());
							List<PolicyCoverData> collect = coverList.stream()
									.filter(c -> !c.getCoverageType().equalsIgnoreCase("T")
											&& !c.getCoverageType().equalsIgnoreCase("L")
											&& !c.getCoverageType().equalsIgnoreCase("D"))
									.collect(Collectors.toList());
							List<NonMotorCoverRes> nonMotorCoverResList = new ArrayList<>();
							for (PolicyCoverData policyCoverData : collect) {
								List<SectionCoverMaster> sectionCoverMasterList = coverMasterRepo
										.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
												home.getCompanyId(), home.getProductId(),
												Integer.valueOf(sec.getSectionId()), policyCoverData.getCoverId());
								NonMotorCoverRes nonMotorCoverRes = new NonMotorCoverRes();
								nonMotorCoverRes.setCoverageLimit(policyCoverData.getCoverageLimit().toPlainString());
								nonMotorCoverRes.setCoverageType(policyCoverData.getCoverageType());
								nonMotorCoverRes.setCoverId(policyCoverData.getCoverId().toString());
								nonMotorCoverRes.setSuminsured(policyCoverData.getSumInsured().toPlainString());
								nonMotorCoverRes.setPremium(policyCoverData.getPremiumIncludedTaxLc().toPlainString());
								nonMotorCoverRes.setCoverName(policyCoverData.getCoverName());
								nonMotorCoverRes.setCoverCode(sectionCoverMasterList.get(0).getCoreAppCode());
								fetchExcessDetails(home, sec, policyCoverData, nonMotorCoverRes);
								List<AdditionalInformationRes> additionalInformationList = additionalInformation(home,
										policyCoverData);
								nonMotorCoverRes.setAdditionalInformationList(additionalInformationList);
								nonMotorCoverResList.add(nonMotorCoverRes);
							}
							List<FactorRateRequestDetails> factorRateRequestDetailsList = factorRateRequestDetailsRepo
									.findByRequestReferenceNoAndProductIdAndCompanyIdAndLocationIdAndSectionIdAndUserOpt(
											home.getRequestReferenceNo(), home.getProductId(), home.getCompanyId(),
											sec.getLocationId(), Integer.valueOf(sec.getSectionId()), "Y");
							List<FactorRateRequestDetails> collect2 = factorRateRequestDetailsList.stream()
									.filter(c -> !c.getCoverageType().equalsIgnoreCase("A"))
									.collect(Collectors.toList());
							for (FactorRateRequestDetails factorRateRequestDetails : collect2) {
								List<SectionCoverMaster> sectionCoverMasterList = coverMasterRepo
										.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
												home.getCompanyId(), home.getProductId(),
												Integer.valueOf(sec.getSectionId()),
												factorRateRequestDetails.getCoverId());
								NonMotorCoverRes nonMotorCoverRes = new NonMotorCoverRes();
								nonMotorCoverRes
										.setCoverageLimit(factorRateRequestDetails.getCoverageLimit().toPlainString());
								nonMotorCoverRes.setCoverageType(factorRateRequestDetails.getCoverageType());
								nonMotorCoverRes.setCoverId(factorRateRequestDetails.getCoverId().toString());
								nonMotorCoverRes
										.setSuminsured(factorRateRequestDetails.getSumInsured().toPlainString());
								nonMotorCoverRes
										.setPremium(factorRateRequestDetails.getPremiumIncludedTaxLc().toPlainString());
								nonMotorCoverRes.setCoverName(factorRateRequestDetails.getCoverName());
								nonMotorCoverRes.setCoverCode(sectionCoverMasterList.get(0).getCoreAppCode());
								nonMotorCoverResList.add(nonMotorCoverRes);
							}
							nonMotorSectionRes.setCoverList(nonMotorCoverResList);
							nonMotorSectionResList.add(nonMotorSectionRes);
						}
						nonMotorLocRes.setSectionList(nonMotorSectionResList);
						nonMotorLocResList.add(nonMotorLocRes);
					}
					nonMotorRes.setLocationList(nonMotorLocResList);
				}
			}
		} catch (Exception e) {
			System.out.println("***********The Exception Occured in GetMotorDetails  Api ***********");
			e.printStackTrace();
			return null;
		}
		return nonMotorRes;
	}

	private void fetchExcessDetails(HomePositionMaster home, SectionDataDetails sec, PolicyCoverData policyCoverData,
			NonMotorCoverRes nonMotorCoverRes) {
		List<NonMotorExcessRes> nonMotorExcessResList = new ArrayList<>();
		List<ExcessMaster> excessMasterList = excessMasterRepos
				.findByCompanyIdAndProductIdAndSectionIdAndCoverIdAndStatus(home.getCompanyId(),
						home.getProductId().toString(), sec.getSectionId(), policyCoverData.getCoverId().toString(),
						"Y");
		if (excessMasterList != null && !excessMasterList.isEmpty()) {
			for (ExcessMaster excessMaster : excessMasterList) {
				NonMotorExcessRes nonMotorExcessRes = new NonMotorExcessRes();
				nonMotorExcessRes.setCompanyId(excessMaster.getCompanyId());
				nonMotorExcessRes.setCoverId(excessMaster.getCoverId());
				nonMotorExcessRes.setCoverName(excessMaster.getCoverName());
				nonMotorExcessRes.setExcessAmount(
						excessMaster.getExcessAmount() != null ? excessMaster.getExcessAmount().toString() : null);
				nonMotorExcessRes.setExcessDesc(excessMaster.getExcessDescription());
				nonMotorExcessRes.setExcessCode(excessMaster.getCoreAppCode());
				nonMotorExcessRes
						.setExcessId(excessMaster.getExcessId() != null ? excessMaster.getExcessId().toString() : null);
				nonMotorExcessRes.setExcessPercent(
						excessMaster.getExcessPercentage() != null ? excessMaster.getExcessPercentage().toString()
								: null);
				nonMotorExcessRes.setProductId(excessMaster.getProductId());
				nonMotorExcessRes.setSectionId(excessMaster.getSectionId());
				nonMotorExcessResList.add(nonMotorExcessRes);
			}
			nonMotorCoverRes.setExcessList(nonMotorExcessResList);
		}
	}

	@Override
	public List<NonMotorPolicyDetailsListRes> nonMotorPolicyDetailsList(NonMotorPolicyDetailsListReq req) {
		List<NonMotorPolicyDetailsListRes> nonMotorPolicyDetailsListList = new ArrayList<>();
		try {
			String name = req.getName();
			String value = req.getValue();
			if (name.equalsIgnoreCase("CustomerName")) {
				List<PersonalInfo> personalInfoList = personalInfoRepo
						.findByCompanyIdAndClientNameContainingIgnoreCase(req.getCompanyId(), value);
				List<String> customerIdList = personalInfoList.stream().map(PersonalInfo::getCustomerId)
						.collect(Collectors.toList());
				List<HomePositionMaster> homePositionMasterList = homePositionMasterRepo
						.findByCustomerIdInAndCompanyIdAndStatusAndBranchCode(customerIdList, req.getCompanyId(), "P",
								req.getBranchCode());
				List<HomePositionMaster> collect = homePositionMasterList.stream()
						.filter(h -> h.getProductId().compareTo(5) != 0).filter(h -> {
							if (h.getEndtCount() != null && h.getEndtCount() > 0) {
								return "C".equalsIgnoreCase(h.getEndtStatus());
							}
							return true;
						}).collect(Collectors.toList());
				Map<String, HomePositionMaster> homePositionMasterMap = collect.stream()
						.collect(Collectors.toMap(HomePositionMaster::getCustomerId, Function.identity(), (a, b) -> a));
				if (personalInfoList != null && !personalInfoList.isEmpty()) {
					personalInfoList.stream().forEach(p -> {
						HomePositionMaster home = homePositionMasterMap.get(p.getCustomerId());
						if (home != null) {
							NonMotorPolicyDetailsListRes res = new NonMotorPolicyDetailsListRes();
							res.setCustomerName(p.getClientName());
							res.setEmail(p.getEmail1());
							res.setIdNumber(p.getIdNumber());
							res.setIdType(p.getIdType());
							res.setIdTypeName(p.getIdTypeDesc());
							res.setMobileNo(p.getMobileNo1());
							res.setOriginalPolicyNo(home.getOriginalPolicyNo());
							res.setEndtCount(home.getEndtCount());
							res.setPolicyEndDate(sdf.format(home.getExpiryDate()));
							res.setPolicyStartDate(sdf.format(home.getInceptionDate()));
							res.setPolicyNo(home.getPolicyNo());
							nonMotorPolicyDetailsListList.add(res);
						}
					});
				}

			} else if (name.equalsIgnoreCase("PolicyNo")) {
				List<HomePositionMaster> homePositionMasterList = homePositionMasterRepo
						.findByPolicyNoContainingIgnoreCaseAndCompanyIdAndBranchCode(value, req.getCompanyId(),
								req.getBranchCode());
				List<HomePositionMaster> collect = homePositionMasterList.stream()
						.filter(h -> h.getProductId().compareTo(5) != 0).collect(Collectors.toList());
				List<String> customerIdList = collect.stream().map(HomePositionMaster::getCustomerId)
						.collect(Collectors.toList());
				List<PersonalInfo> personalInfoList = personalInfoRepo.findByCustomerIdInAndCompanyId(customerIdList,
						req.getCompanyId());
				Map<String, PersonalInfo> personalInfoMap = personalInfoList.stream()
						.collect(Collectors.toMap(PersonalInfo::getCustomerId, Function.identity(), (a, b) -> a));

				if (personalInfoList != null && !personalInfoList.isEmpty()) {
					homePositionMasterList.stream().forEach(h -> {
						PersonalInfo personalInfo = personalInfoMap.get(h.getCustomerId());
						if (personalInfo != null) {
							NonMotorPolicyDetailsListRes res = new NonMotorPolicyDetailsListRes();
							res.setCustomerName(personalInfo.getClientName());
							res.setEmail(personalInfo.getEmail1());
							res.setIdNumber(personalInfo.getIdNumber());
							res.setIdType(personalInfo.getIdType());
							res.setIdTypeName(personalInfo.getIdTypeDesc());
							res.setMobileNo(personalInfo.getMobileNo1());
							res.setPolicyEndDate(sdf.format(h.getExpiryDate()));
							res.setPolicyStartDate(sdf.format(h.getInceptionDate()));
							res.setPolicyNo(h.getPolicyNo());
							res.setOriginalPolicyNo(h.getOriginalPolicyNo());
							res.setEndtCount(h.getEndtCount());
							nonMotorPolicyDetailsListList.add(res);
						}
					});
				}

			} else if (name.equalsIgnoreCase("EmailId")) {
				List<PersonalInfo> personalInfoList = personalInfoRepo
						.findByCompanyIdAndEmail1ContainingIgnoreCase(req.getCompanyId(), value);
				List<String> customerIdList = personalInfoList.stream().map(PersonalInfo::getCustomerId)
						.collect(Collectors.toList());
				List<HomePositionMaster> homePositionMasterList = homePositionMasterRepo
						.findByCustomerIdInAndCompanyIdAndStatusAndBranchCode(customerIdList, req.getCompanyId(), "P",
								req.getBranchCode());
				List<HomePositionMaster> collect = homePositionMasterList.stream()
						.filter(h -> h.getProductId().compareTo(5) != 0).collect(Collectors.toList());
				Map<String, HomePositionMaster> homePositionMasterMap = collect.stream()
						.collect(Collectors.toMap(HomePositionMaster::getCustomerId, Function.identity(), (a, b) -> a));
				if (personalInfoList != null && !personalInfoList.isEmpty()) {
					personalInfoList.stream().forEach(p -> {
						HomePositionMaster home = homePositionMasterMap.get(p.getCustomerId());
						if (home != null) {
							NonMotorPolicyDetailsListRes res = new NonMotorPolicyDetailsListRes();
							res.setCustomerName(p.getClientName());
							res.setEmail(p.getEmail1());
							res.setIdNumber(p.getIdNumber());
							res.setIdType(p.getIdType());
							res.setIdTypeName(p.getIdTypeDesc());
							res.setMobileNo(p.getMobileNo1());
							res.setPolicyEndDate(sdf.format(home.getExpiryDate()));
							res.setPolicyStartDate(sdf.format(home.getInceptionDate()));
							res.setPolicyNo(home.getPolicyNo());
							res.setOriginalPolicyNo(home.getOriginalPolicyNo());
							res.setEndtCount(home.getEndtCount());
							nonMotorPolicyDetailsListList.add(res);
						}
					});
				}

			} else if (name.equalsIgnoreCase("MobileNo")) {
				List<PersonalInfo> personalInfoList = personalInfoRepo
						.findByCompanyIdAndMobileNo1ContainingIgnoreCase(req.getCompanyId(), value);
				List<String> customerIdList = personalInfoList.stream().map(PersonalInfo::getCustomerId)
						.collect(Collectors.toList());
				List<HomePositionMaster> homePositionMasterList = homePositionMasterRepo
						.findByCustomerIdInAndCompanyIdAndStatusAndBranchCode(customerIdList, req.getCompanyId(), "P",
								req.getBranchCode());
				List<HomePositionMaster> collect = homePositionMasterList.stream()
						.filter(h -> h.getProductId().compareTo(5) != 0).collect(Collectors.toList());
				Map<String, HomePositionMaster> homePositionMasterMap = collect.stream()
						.collect(Collectors.toMap(HomePositionMaster::getCustomerId, Function.identity(), (a, b) -> a));
				if (personalInfoList != null && !personalInfoList.isEmpty()) {
					personalInfoList.stream().forEach(p -> {
						HomePositionMaster home = homePositionMasterMap.get(p.getCustomerId());
						if (home != null) {
							NonMotorPolicyDetailsListRes res = new NonMotorPolicyDetailsListRes();
							res.setCustomerName(p.getClientName());
							res.setEmail(p.getEmail1());
							res.setIdNumber(p.getIdNumber());
							res.setIdType(p.getIdType());
							res.setIdTypeName(p.getIdTypeDesc());
							res.setMobileNo(p.getMobileNo1());
							res.setPolicyEndDate(sdf.format(home.getExpiryDate()));
							res.setPolicyStartDate(sdf.format(home.getInceptionDate()));
							res.setPolicyNo(home.getPolicyNo());
							res.setOriginalPolicyNo(home.getOriginalPolicyNo());
							res.setEndtCount(home.getEndtCount());
							nonMotorPolicyDetailsListList.add(res);
						}
					});
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
		return nonMotorPolicyDetailsListList;
	}

	public List<AdditionalInformationRes> additionalInformation(HomePositionMaster home,
			PolicyCoverData policyCoverData) {
		List<AdditionalInformationRes> additionalInformationResList = new ArrayList<>();
		try {
			TemplateEntity templateEntity = templateRepos.findByCompanyIdAndProductIdAndSectionIdAndCoverIdAndStatusTf(
					home.getCompanyId(), policyCoverData.getProductId().toString(),
					policyCoverData.getSectionId().toString(), policyCoverData.getCoverId().toString(), "Y");

			List<AdditionalInformation> additionalInformationList = additionalInformationRepo
					.findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(home.getQuoteNo(), home.getCompanyId(),
							policyCoverData.getProductId().toString(), policyCoverData.getSectionId().toString(),
							policyCoverData.getCoverId().toString());

			for (AdditionalInformation addinformation : additionalInformationList) {
				AdditionalInformationRes addRes = new AdditionalInformationRes();
				addRes.setCompanyId(addinformation.getCompanyId());
				addRes.setCoverId(addinformation.getCoverId());
				addRes.setLocationId(addinformation.getLocationId());
				addRes.setProductId(addinformation.getProductId());
				addRes.setSectionId(addinformation.getSectionId());
				addRes.setValue(addinformation.getValue());
				addRes.setParam1(addinformation.getParam1());
				addRes.setParam3(addinformation.getParam3());
				addRes.setParam4(addinformation.getParam4());
				addRes.setParam5(addinformation.getParam5());
				addRes.setParam6(addinformation.getParam6());
				addRes.setParam7(addinformation.getParam7());
				addRes.setParam8(addinformation.getParam8());
				addRes.setParam9(addinformation.getParam9());
				addRes.setParam10(addinformation.getParam10());
				additionalInformationResList.add(addRes);
			}
			return additionalInformationResList;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public CaimPolicydetailsRes caimPolicydetailsRes(PolicyDetailsReq req) {
		try {
			CaimPolicydetailsRes res = new CaimPolicydetailsRes();
			String policyNo = req.getQuotationPolicyNo();
			if ((req.getQuotationPolicyNo() == null || req.getQuotationPolicyNo().isEmpty())
					&& (req.getChassisno() != null && !req.getChassisno().isEmpty())) {
				List<MotorDataDetails> motorDataDetailsList = motorDataDetailsRepo
						.findByRegistrationNumberAndStatus(req.getChassisno(), "P");
				if (!motorDataDetailsList.isEmpty()) {
					policyNo = motorDataDetailsList.get(0).getPolicyNo();
				}
			}

			HomePositionMaster home = homePositionMasterRepo.findByPolicyNo(policyNo);

			CompanyProductMaster companyProductMaster = companyProductMasterRepo
					.findByProductIdAndCompanyIdAndStatus(home.getProductId(), home.getCompanyId(), "Y");

			EwayDivisionDepartment ewayDivisionDepartment = ewayDivisionDepartmentRepos
					.findByCompnayIdAndProductId(home.getCompanyId(), home.getProductId().toString());

			InsuranceCompanyMaster insuranceCompanyMaster = insuranceCompanyMasterRepos
					.findByCompanyId(home.getCompanyId());
			PersonalInfo personalInfo = personalInfoRepo.findByCustomerId(home.getCustomerId());

			BranchMaster branchMaster = branchMasterRepos.findByBranchCodeAndCompanyIdAndStatus(home.getBranchCode(),
					home.getCompanyId(), "Y");

			EserviceCustomerDetails eserviceCustomerDetails = eserviceCustomerDetailsRepos
					.findByCustomerReferenceNo(personalInfo.getCustomerReferenceNo());

			EwayLobMaster ewayLobMaster = ewayLobMasterRepos.findByCompnayIdAndProductId(home.getCompanyId(),
					home.getProductId().toString());

			PolicyInfoDetailsDto policyInfo = new PolicyInfoDetailsDto();

			policyInfo.setPolicyNo(home.getPolicyNo());
			policyInfo.setPolicyFrom(home.getInceptionDate());
			policyInfo.setPolicyTo(home.getExpiryDate());
			policyInfo.setProductDesc(home.getProductName());
			policyInfo.setProductcode(companyProductMaster.getCoreAppCode());
			policyInfo.setCustomerCode(eserviceCustomerDetails.getPolCustCode());
			policyInfo.setContactPerName(policyInfo.getContactPerName());
			policyInfo.setCivilId(policyInfo.getIdNumber());
			policyInfo.setSectionCode(home.getSectionId() != null ? home.getSectionId().toString() : null);
			policyInfo.setCurrencyCode(home.getCurrency());
			policyInfo.setAddress(policyInfo.getAddress());
			policyInfo.setOccupation(policyInfo.getOccupation());
			policyInfo.setEmail(policyInfo.getEmail());
			policyInfo.setMobileNumber(policyInfo.getMobileNumber());
			policyInfo.setCompanyId(home.getCompanyId());
			policyInfo.setCompanyName(insuranceCompanyMaster.getCompanyName());
			policyInfo.setProductType(home.getProductName());
			policyInfo.setCustomer(policyInfo.getCustomer());
			policyInfo.setBranchCode(home.getBranchCode());
			policyInfo.setProduct(home.getProductName());
			policyInfo.setBrokerCode(home.getBrokerCode());
			policyInfo.setBranch(home.getBranchName());
			policyInfo.setRegionCode(personalInfo.getRegionCode());
			policyInfo.setCityName(personalInfo.getCityName());
			policyInfo.setMobileCode(personalInfo.getMobileCode1());
			policyInfo.setNationality(personalInfo.getNationality());
			policyInfo.setProductId(home.getProductId().toString());
			policyInfo.setCompanyCode(insuranceCompanyMaster.getCoreAppCode());
			policyInfo.setBrokerName(home.getBdmName());
			policyInfo.setCustCode(home.getCustomerCode());
			policyInfo.setCustName(home.getCustomerName());
			policyInfo.setDeptCode(ewayDivisionDepartment != null ? ewayDivisionDepartment.getDepartmentCode() : null);
			policyInfo.setCityCode(personalInfo.getCityCode());
			policyInfo.setCityName(personalInfo.getCityName());
			policyInfo.setPolicyHolderTypeDesc(personalInfo.getPolicyHolderTypeDesc());
			policyInfo.setPolicyHolderTypeId(personalInfo.getPolicyHolderTypeid());
			policyInfo.setIdNumber(personalInfo.getIdNumber());
			policyInfo.setIdType(personalInfo.getIdType());
			policyInfo.setIdTypeDesc(personalInfo.getIdTypeDesc());
			policyInfo.setPhoneNo(personalInfo.getMobileNo1());
			policyInfo.setDistrict(personalInfo.getStateName());
			policyInfo.setStateCode(personalInfo.getStateCode());
			policyInfo.setStateName(personalInfo.getStateName());
			policyInfo.setStreet(personalInfo.getStreet());
			policyInfo.setPostalNo(personalInfo.getPinCode());
			policyInfo.setGenderDesc(personalInfo.getGenderDesc());
			policyInfo.setAge(personalInfo.getAge() != null ? personalInfo.getAge().toString() : null);
			policyInfo.setDob(personalInfo.getDobOrRegDate());
			policyInfo.setSourceType(home.getSourceType());
			policyInfo.setSourceTypeId(home.getSourceTypeId());
			policyInfo.setDivisionCode(branchMaster.getCoreAppCode());
			policyInfo.setUwlob(ewayLobMaster != null ? ewayLobMaster.getLobCode() : null);
			policyInfo.setUwSysId(home.getUwsysId() != null ? home.getUwsysId().toString() : null);
			policyInfo.setPolidx(home.getAhpolIdx() != null ? home.getAhpolIdx().toString() : null);
			policyInfo.setEndtNo(home.getEndtCount() != null ? home.getEndtCount().toString() : null);
			if (home.getProductId().compareTo(5) == 0) {

				SearchReq vehicleDetailsReq = new SearchReq();
				vehicleDetailsReq.setInsuranceId(home.getCompanyId());
				vehicleDetailsReq.setProductId(home.getProductId().toString());
				vehicleDetailsReq.setQuoteNo(home.getQuoteNo());
				vehicleDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
				SearchROPVehicleDetailsRes adminROPVehicleSearch = searchServiceImpl
						.adminROPVehicleSearch(vehicleDetailsReq);
				@SuppressWarnings("unused")
				List<MotorRes> motor = new ArrayList<>();
				for (SearchROPVehicleRes veh : adminROPVehicleSearch.getVehDetails()) {
					MotorRes vehicleInfo = new MotorRes();
					vehicleInfo.setSeats(veh.getSeatingCapacity() != null ? veh.getSeatingCapacity().toString() : null);
					vehicleInfo.setVehicleMakeModel(veh.getResMake() + " / " + veh.getResModel());
					vehicleInfo.setPlateNoCharacter(veh.getResRegNumber());
					vehicleInfo.setChassisNo(veh.getResRegNumber());
					vehicleInfo.setVehicletypedesc(veh.getResBodyType());
					vehicleInfo.setVehtype(veh.getMotorDesc());
					vehicleInfo.setVehmake(veh.getResMake());
					vehicleInfo.setVehbodytype(veh.getResBodyType());
					vehicleInfo.setManufactureyear(
							veh.getResYearOfManufacture() != null ? veh.getResYearOfManufacture().toString() : null);
					vehicleInfo.setVehiclecc(null);
					vehicleInfo
							.setSeating(veh.getSeatingCapacity() != null ? veh.getSeatingCapacity().toString() : null);
					vehicleInfo.setTonnage(veh.getTareWeight() != null ? veh.getTareWeight().toString()
							: veh.getGrossWeight() != null ? veh.getGrossWeight().toString() : null);
					vehicleInfo.setVechregno(veh.getResRegNumber());
					vehicleInfo.setVehmodel(veh.getResModel());
					motor.add(vehicleInfo);
				}
				policyInfo.setMotorList(motor);
			}

			NonMotorRes nonMotorRes = new NonMotorRes();

			List<NonMotorLocRes> nonMotorLocResList = new ArrayList<>();
			List<SectionDataDetails> sectiondatadetails = sectionDataDetailsRepo.findByQuoteNo(home.getQuoteNo());
			Set<Integer> locset = new HashSet<>();
			List<SectionDataDetails> locationList = sectiondatadetails.stream()
					.filter(s -> locset.add(s.getLocationId())).collect(Collectors.toList());

			for (SectionDataDetails Loc : locationList) {
				NonMotorLocRes nonMotorLocRes = new NonMotorLocRes();
				nonMotorLocRes.setLocationId(Loc.getLocationId().toString());
				nonMotorLocRes.setLocationName(Loc.getLocationName());
				nonMotorLocRes.setAddress(Loc.getLocationName());
				nonMotorLocRes.setRiskId(Loc.getRiskId().toString());
				nonMotorLocRes.setCoversRequiredYn(null);
				nonMotorLocRes.setBuildingOwnerYn(null);
				Set<String> secSet = new HashSet<>();
				List<SectionDataDetails> sectionLocList = sectionDataDetailsRepo
						.findByPolicyNoAndLocationId(home.getPolicyNo(), Loc.getLocationId());
				List<SectionDataDetails> sectionList = sectionLocList.stream().filter(s -> secSet.add(s.getSectionId()))
						.collect(Collectors.toList());
				List<NonMotorSectionRes> nonMotorSectionResList = new ArrayList<>();
				for (SectionDataDetails sec : sectionList) {
					List<ProductSectionMaster> productSectionMasterList = productSectionMasterRepo
							.findByProductIdAndCompanyIdAndSectionIdAndStatusOrderByAmendIdDesc(home.getProductId(),
									home.getCompanyId(), Integer.valueOf(sec.getSectionId()), "Y");
					NonMotorSectionRes nonMotorSectionRes = new NonMotorSectionRes();
					nonMotorSectionRes.setSectionId(sec.getSectionId());
					nonMotorSectionRes.setSectionDesc(sec.getSectionDesc());
					nonMotorSectionRes.setSectionType(sec.getProductType());
					nonMotorSectionRes.setSectionCode(productSectionMasterList.get(0).getCoreAppCode());
					List<PolicyCoverData> coverList = coverRepo.findByQuoteNoAndSectionIdAndLocationId(
							home.getQuoteNo(), Integer.valueOf(sec.getSectionId()), sec.getLocationId());
					List<PolicyCoverData> collect = coverList.stream()
							.filter(c -> !c.getCoverageType().equalsIgnoreCase("T")
									&& !c.getCoverageType().equalsIgnoreCase("L")
									&& !c.getCoverageType().equalsIgnoreCase("D"))
							.collect(Collectors.toList());
					List<NonMotorCoverRes> nonMotorCoverResList = new ArrayList<>();
					for (PolicyCoverData policyCoverData : collect) {
						List<SectionCoverMaster> sectionCoverMasterList = coverMasterRepo
								.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
										home.getCompanyId(), home.getProductId(), Integer.valueOf(sec.getSectionId()),
										policyCoverData.getCoverId());
						NonMotorCoverRes nonMotorCoverRes = new NonMotorCoverRes();
						nonMotorCoverRes.setCoverageLimit(policyCoverData.getCoverageLimit().toPlainString());
						nonMotorCoverRes.setCoverageType(policyCoverData.getCoverageType());
						nonMotorCoverRes.setCoverId(policyCoverData.getCoverId().toString());
						nonMotorCoverRes.setSuminsured(policyCoverData.getSumInsured().toPlainString());
						nonMotorCoverRes.setPremium(policyCoverData.getPremiumIncludedTaxLc().toPlainString());
						nonMotorCoverRes.setCoverName(policyCoverData.getCoverName());
						nonMotorCoverRes.setCoverCode(sectionCoverMasterList.get(0).getCoreAppCode());
						fetchExcessDetails(home, sec, policyCoverData, nonMotorCoverRes);
						if (home.getProductId().compareTo(5) != 0) {
							List<AdditionalInformationRes> additionalInformationList = additionalInformation(home,
									policyCoverData);
							nonMotorCoverRes.setAdditionalInformationList(additionalInformationList);
						}
						nonMotorCoverResList.add(nonMotorCoverRes);
					}
//					List<FactorRateRequestDetails> factorRateRequestDetailsList = factorRateRequestDetailsRepo
//							.findByRequestReferenceNoAndProductIdAndCompanyIdAndLocationIdAndSectionIdAndUserOpt(
//									home.getRequestReferenceNo(), home.getProductId(), home.getCompanyId(),
//									sec.getLocationId(), Integer.valueOf(sec.getSectionId()), "Y");
//					List<FactorRateRequestDetails> collect2 = factorRateRequestDetailsList.stream()
//							.filter(c -> !c.getCoverageType().equalsIgnoreCase("A")).collect(Collectors.toList());
//					for (FactorRateRequestDetails factorRateRequestDetails : collect2) {
//						List<SectionCoverMaster> sectionCoverMasterList = coverMasterRepo
//								.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
//										home.getCompanyId(), home.getProductId(), Integer.valueOf(sec.getSectionId()),
//										factorRateRequestDetails.getCoverId());
//						NonMotorCoverRes nonMotorCoverRes = new NonMotorCoverRes();
//						nonMotorCoverRes.setCoverageLimit(factorRateRequestDetails.getCoverageLimit().toPlainString());
//						nonMotorCoverRes.setCoverageType(factorRateRequestDetails.getCoverageType());
//						nonMotorCoverRes.setCoverId(factorRateRequestDetails.getCoverId().toString());
//						nonMotorCoverRes.setSuminsured(factorRateRequestDetails.getSumInsured() != null
//								? factorRateRequestDetails.getSumInsured().toPlainString()
//								: null);
//						nonMotorCoverRes.setPremium(factorRateRequestDetails.getPremiumIncludedTaxLc() != null
//								? factorRateRequestDetails.getPremiumIncludedTaxLc().toPlainString()
//								: null);
//						nonMotorCoverRes.setCoverName(factorRateRequestDetails.getCoverName());
//						nonMotorCoverRes.setCoverCode(sectionCoverMasterList.get(0).getCoreAppCode());
//						nonMotorCoverResList.add(nonMotorCoverRes);
//					}
					nonMotorSectionRes.setCoverList(nonMotorCoverResList);
					nonMotorSectionResList.add(nonMotorSectionRes);
				}
				nonMotorLocRes.setSectionList(nonMotorSectionResList);
				nonMotorLocResList.add(nonMotorLocRes);
			}
			nonMotorRes.setLocationList(nonMotorLocResList);
			res.setNonmotoInfo(nonMotorRes);
			res.setPolicyInfo(policyInfo);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
