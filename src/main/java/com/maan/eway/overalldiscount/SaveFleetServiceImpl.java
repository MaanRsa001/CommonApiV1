package com.maan.eway.overalldiscount;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.MsPolicyDetails;
import com.maan.eway.bean.PdRefno;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.repository.EServiceDriverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.MotorColorMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MsPolicyDetailsRepository;
import com.maan.eway.repository.PdRefnoRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.service.impl.CalculatorEngineService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class SaveFleetServiceImpl implements EserviceSaveFleetService{
	
	@Autowired
	private CalculatorEngineService calc;
	
	@Autowired
	private EServiceMotorDetailsRepository repo;

	@Autowired
	private EServiceDriverDetailsRepository esDriverRepo;

	@Autowired
	private ListItemValueRepository listRepo;

	@Autowired
	private MotorDataDetailsRepository motordatarepo;

	@Autowired
	private EserviceCustomerDetailsRepository custRepo;

	@Autowired
	private MotorColorMasterRepository color;

	@Autowired
	private MsPolicyDetailsRepository msPolicyRepo;

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private PdRefnoRepository pdRefNoRepo;

	Gson json = new Gson();
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService;

	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo;

	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;

	
	@Autowired
	private EserviceTravelDetailsRepository eservicetravel;
	
	@Autowired
	private ProductSectionMasterRepository productSectionRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository sectionRepo;
	
	
	
	 public  static LocalDate convertToLocalDate(Date dateToConvert) {
		 if (dateToConvert == null) {
	            return null;
	        }
	        return dateToConvert.toInstant()
	                            .atZone(ZoneId.systemDefault())
	                            .toLocalDate();
	    }


	private Logger log = LogManager.getLogger(SaveFleetServiceImpl.class);

	
	@Override
	public EserviceMotorDetailsSaveRes updateFleetDetails(FleetDetailsSaveReq req,String tokens) {
		EserviceMotorDetailsSaveRes mot = new EserviceMotorDetailsSaveRes();
		
		FleetDetailsRes fleetRes=new FleetDetailsRes();
		fleetRes.setRequestReferenceNo(req.getRequestReferenceNo());
		fleetRes.setNoOfVehicles(0);
		fleetRes.setPdrefno("0");
	
		
		List<EserviceCommonDetails> humans =new ArrayList<>();
		List<EserviceTravelDetails> travel =new ArrayList<>();
		try {
			//CompanyProductMaster product = getCompanyProductMasterDropdown2(req.getInsuranceId(), req.getProductId()); 
			EserviceSectionDetails prodctsec=sectionRepo.findFirstByRequestReferenceNo(req.getRequestReferenceNo());
			
			List<ProductSectionMaster> product = productSectionRepo.
					findByCompanyIdAndProductIdAndSectionIdAndStatusOrderByAmendIdDesc(req.getInsuranceId(),Integer.valueOf(prodctsec.getProductId()), 
							Integer.valueOf(prodctsec.getSectionId()), "Y");
			
			String productType=product.get(0).getMotorYn();

			if ("M".equalsIgnoreCase(productType )) {
				List<String> statusesNot = new ArrayList<String>();
				statusesNot.add("D");

				List<EserviceMotorDetails> motors = repo
						.findByRequestReferenceNoAndStatusNotIn(req.getRequestReferenceNo(), statusesNot);
				Integer count = motors.size();
				EserviceMotorDetails motDetails = motors.size() > 0 ? motors.get(motors.size() - 1) : null;

				if (motDetails != null) {
					MsPolicySaveReq msPolicySaveReq = new MsPolicySaveReq();
					msPolicySaveReq.setCurrency(motDetails.getCurrency());
					msPolicySaveReq
							.setEndtCategoryId(motDetails.getIsFinaceYn() == null ? "N" : motDetails.getIsFinaceYn());
					msPolicySaveReq.setEndtTypeId(
							motDetails.getEndorsementType() == null ? 0 : motDetails.getEndorsementType());
					msPolicySaveReq.setExchangeRate(motDetails.getExchangeRate());
					msPolicySaveReq.setGroupCount(count);
					msPolicySaveReq.setHavepromocode(motDetails.getHavepromocode());
					msPolicySaveReq.setPromocode(motDetails.getPromocode());
					msPolicySaveReq.setNoOfVehicles(count);
					msPolicySaveReq.setRequestReferenceNo(req.getRequestReferenceNo());
					msPolicySaveReq.setPdRefno(null);
					msPolicySaveReq.setStatus(motDetails.getStatus());
					msPolicySaveReq.setPeriodOfInsurance(motDetails.getPeriodOfInsurance());
					msPolicySaveReq.setBuildingSumInsured(BigDecimal.ZERO);
					msPolicySaveReq
							.setCdRefno(motDetails.getCdRefno() == null ? null : Long.valueOf(motDetails.getCdRefno()));

					// Save Method Call
					String pdRefNo = saveMsPolicyDetails(msPolicySaveReq);
					fleetRes.setNoOfVehicles(count);
					fleetRes.setPdrefno(pdRefNo);
					fleetRes.setInsuranceId(motDetails.getCompanyId());
					fleetRes.setBranchCode(motDetails.getBranchCode());
					fleetRes.setAgencyCode(
							motDetails.getAgencyCode() == null ? "99999" : motDetails.getAgencyCode().toString());
					fleetRes.setSectionId("99999");
					fleetRes.setProductId(motDetails.getProductId());
					fleetRes.setMSRefNo("");
					fleetRes.setVehicleId("99999");
					fleetRes.setCdRefNo(motDetails.getCdRefno() == null ? "99999" : motDetails.getCdRefno().toString());
					fleetRes.setVdRefNo(pdRefNo);
					fleetRes.setCreatedBy(motDetails.getCreatedBy());
					fleetRes.setRequestReferenceNo(motDetails.getRequestReferenceNo());
					fleetRes.setPdrefno2(pdRefNo);
					Date policyStartDate = motDetails.getPolicyStartDate();
					Date policyEndDate = motDetails.getPolicyEndDate();

					String formattedStart = formatter.format(policyStartDate);
					String formattedEnd = formatter.format(policyEndDate);

					Date formattedStartDate = formatter.parse(formattedStart);
					Date formattedEndDate1 = formatter.parse(formattedEnd);

					fleetRes.setEffectiveStartDate(formattedStartDate);
					fleetRes.setEffectiveEndDate(formattedEndDate1);
					
					fleetRes.setEffectiveStartDate(policyStartDate);
					fleetRes.setEffectiveEndDate(formattedEndDate1);
					fleetRes.setUnderwriterAdjustments(req.getUnderwriterAdjustments());
					
				}

			} else if ("A".equalsIgnoreCase(productType)) {
				List<String> statusesNot = new ArrayList<String>();
				statusesNot.add("D");

				List<EserviceBuildingDetails> buildings = eserBuildRepo
						.findByRequestReferenceNoAndStatusNotIn(req.getRequestReferenceNo(), statusesNot);
				Double suminsured = 0D;
//				suminsured = buildings.stream()
//						.filter(o -> !"0".equalsIgnoreCase(o.getSectionId()) && o.getBuildingSuminsured() != null)
//						.mapToDouble(o -> o.getBuildingSuminsured().doubleValue()).sum();
				suminsured = buildings.stream()
						.filter(o -> !"0".equalsIgnoreCase(o.getSectionId()) && o.getSumInsured() != null)
						.mapToDouble(o -> o.getSumInsured().doubleValue()).sum();

				Integer count = 1;
				EserviceBuildingDetails buildDetails = buildings.size() > 0 ? buildings.get(buildings.size() - 1)
						: null;
				if (buildDetails != null) {
					MsPolicySaveReq msPolicySaveReq = new MsPolicySaveReq();
					msPolicySaveReq.setCurrency(buildDetails.getCurrency());
					msPolicySaveReq
							.setEndtCategoryId(buildDetails.getIsFinyn() == null ? "N" : buildDetails.getIsFinyn());
					msPolicySaveReq.setEndtTypeId(
							buildDetails.getEndorsementType() == null ? 0 : buildDetails.getEndorsementType());
					msPolicySaveReq.setExchangeRate(buildDetails.getExchangeRate());
					msPolicySaveReq.setGroupCount(count);
					msPolicySaveReq.setHavepromocode(buildDetails.getHavepromocode());
					msPolicySaveReq.setPromocode(buildDetails.getPromocode());
					msPolicySaveReq.setNoOfVehicles(count);
					msPolicySaveReq.setRequestReferenceNo(req.getRequestReferenceNo());
					msPolicySaveReq.setPdRefno(null);
					msPolicySaveReq.setStatus(buildDetails.getStatus());
					msPolicySaveReq.setPeriodOfInsurance(
							buildDetails.getPolicyPeriord() == null ? "0" : buildDetails.getPolicyPeriord().toString());
					msPolicySaveReq
							.setBuildingSumInsured(suminsured == null ? BigDecimal.ZERO : new BigDecimal(suminsured));
					msPolicySaveReq.setCdRefno(
							buildDetails.getCdRefno() == null ? null : Long.valueOf(buildDetails.getCdRefno()));
					msPolicySaveReq.setFactorUse(buildDetails.getWallType());
					
					// Save Method Call
					String pdRefNo = saveMsPolicyDetails(msPolicySaveReq);
					fleetRes.setNoOfVehicles(count);
					fleetRes.setPdrefno(pdRefNo);
					fleetRes.setInsuranceId(buildDetails.getCompanyId());
					fleetRes.setBranchCode(buildDetails.getBranchCode());
					fleetRes.setAgencyCode(
							buildDetails.getAgencyCode() == null ? "99999" : buildDetails.getAgencyCode().toString());
					fleetRes.setSectionId("99999");
					fleetRes.setProductId(buildDetails.getProductId());
					fleetRes.setMSRefNo("");
					fleetRes.setVehicleId("99999");
					fleetRes.setCdRefNo(
							buildDetails.getCdRefno() == null ? "99999" : buildDetails.getCdRefno().toString());
					fleetRes.setVdRefNo(pdRefNo);
					fleetRes.setCreatedBy(buildDetails.getCreatedBy());
					fleetRes.setRequestReferenceNo(buildDetails.getRequestReferenceNo());
					fleetRes.setPdrefno2(pdRefNo);
					Date policyStartDate =buildDetails.getPolicyStartDate();
					Date policyEndDate =buildDetails.getPolicyEndDate();
					// Format and parse back to remove time part
					String formattedStart = formatter.format(policyStartDate);
					String formattedEnd = formatter.format(policyEndDate);

					Date formattedStartDate = formatter.parse(formattedStart);
					Date formattedEndDate1 = formatter.parse(formattedEnd);

					// Now set as Date objects
					fleetRes.setEffectiveStartDate(formattedStartDate);
					fleetRes.setEffectiveEndDate(formattedEndDate1);
					
					fleetRes.setEffectiveStartDate(policyStartDate);
					fleetRes.setEffectiveEndDate(formattedEndDate1);
					fleetRes.setUnderwriterAdjustments(req.getUnderwriterAdjustments());
	
				}
			} else if ("H".equalsIgnoreCase(productType)) {
				List<String> statusesNot = new ArrayList<String>();
				statusesNot.add("D");

				if(req.getProductId().equals("4")) {
					travel=eservicetravel.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
					Integer count = travel.size();
					EserviceTravelDetails motDetails = travel.size() > 0 ? travel.get(travel.size() - 1) : null;

					if (motDetails != null) {
						MsPolicySaveReq msPolicySaveReq = new MsPolicySaveReq();
						msPolicySaveReq.setCurrency(motDetails.getCurrency());
//						msPolicySaveReq
//								.setEndtCategoryId(motDetails.getIsFinaceYn() == null ? "N" : motDetails.getIsFinaceYn());
						msPolicySaveReq.setEndtTypeId(
								motDetails.getEndorsementType() == null ? 0 : motDetails.getEndorsementType());
						msPolicySaveReq.setExchangeRate(motDetails.getExchangeRate());
						msPolicySaveReq.setGroupCount(count);
						msPolicySaveReq.setHavepromocode(motDetails.getHavepromocode());
						msPolicySaveReq.setPromocode(motDetails.getPromocode());
						msPolicySaveReq.setNoOfVehicles(count);
						msPolicySaveReq.setRequestReferenceNo(req.getRequestReferenceNo());
						msPolicySaveReq.setPdRefno(null);
						msPolicySaveReq.setStatus(motDetails.getStatus());
						
						Date startDate = motDetails.getTravelStartDate(); 
			            Date endDate = motDetails.getTravelEndDate();     

			            LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			            LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			            long days = ChronoUnit.DAYS.between(localStartDate, localEndDate) + 1;

			            System.out.println("Total travel days: " + days);
						
						
						msPolicySaveReq.setPeriodOfInsurance(String.valueOf(days));
						msPolicySaveReq.setBuildingSumInsured(BigDecimal.ZERO);
						msPolicySaveReq
								.setCdRefno(motDetails.getCdRefno() == null ? null : Long.valueOf(motDetails.getCdRefno()));

						// Save Method Call
						String pdRefNo = saveMsPolicyDetails(msPolicySaveReq);
						fleetRes.setNoOfVehicles(count);
						fleetRes.setPdrefno(pdRefNo);
						fleetRes.setInsuranceId(motDetails.getCompanyId());
						fleetRes.setBranchCode(motDetails.getBranchCode());
						fleetRes.setAgencyCode(
								motDetails.getAgencyCode() == null ? "99999" : motDetails.getAgencyCode().toString());
						fleetRes.setSectionId("99999");
						fleetRes.setProductId(String.valueOf(motDetails.getProductId()));
						fleetRes.setMSRefNo("");
						fleetRes.setVehicleId("99999");
						fleetRes.setCdRefNo(motDetails.getCdRefno() == null ? "99999" : motDetails.getCdRefno().toString());
						fleetRes.setVdRefNo(pdRefNo);
						fleetRes.setCreatedBy(motDetails.getCreatedBy());
						fleetRes.setRequestReferenceNo(motDetails.getRequestReferenceNo());
						fleetRes.setPdrefno2(pdRefNo);
						Date policyStartDate =motDetails.getEffectiveDate() ;
						Date policyEndDate =motDetails.getTravelEndDate();
						// Format and parse back to remove time part
						String formattedStart = formatter.format(policyStartDate);
						String formattedEnd = formatter.format(policyEndDate);

						Date formattedStartDate = formatter.parse(formattedStart);
						Date formattedEndDate1 = formatter.parse(formattedEnd);

						// Now set as Date objects
						fleetRes.setEffectiveStartDate(formattedStartDate);
						fleetRes.setEffectiveEndDate(formattedEndDate1);
						
						fleetRes.setEffectiveStartDate(policyStartDate);
						fleetRes.setEffectiveEndDate(formattedEndDate1);
						fleetRes.setUnderwriterAdjustments(req.getUnderwriterAdjustments());

					}
				}
				else {
				humans = eserCommonRepo
						.findByRequestReferenceNoAndStatusNotIn(req.getRequestReferenceNo(), statusesNot);
				Integer count = 0;
				Long totalcount = humans.stream().filter(o -> o.getCount() != null).mapToLong(o -> o.getCount()).sum();
				count = Integer.valueOf(totalcount.toString());

				EserviceCommonDetails human = humans.size() > 0 ? humans.get(humans.size() - 1) : null;
				if (human != null) {
					MsPolicySaveReq msPolicySaveReq = new MsPolicySaveReq();
					msPolicySaveReq.setCurrency(human.getCurrency());
					msPolicySaveReq.setEndtCategoryId(human.getIsFinaceYn() == null ? "N" : human.getIsFinaceYn());
					msPolicySaveReq.setEndtTypeId(human.getEndorsementType() == null ? 0 : human.getEndorsementType());
					msPolicySaveReq.setExchangeRate(human.getExchangeRate());
					msPolicySaveReq.setGroupCount(count);
					msPolicySaveReq.setHavepromocode(human.getHavepromocode());
					msPolicySaveReq.setPromocode(human.getPromocode());
					msPolicySaveReq.setNoOfVehicles(count);
					msPolicySaveReq.setRequestReferenceNo(req.getRequestReferenceNo());
					msPolicySaveReq.setPdRefno(null);
					msPolicySaveReq.setStatus(human.getStatus());
					msPolicySaveReq.setPeriodOfInsurance(
							human.getPolicyPeriod() == null ? "0" : human.getPolicyPeriod().toString());
					msPolicySaveReq.setBuildingSumInsured(BigDecimal.ZERO);
					msPolicySaveReq.setCdRefno(human.getCdRefno() == null ? null : Long.valueOf(human.getCdRefno()));

					// Save Method Call
					String pdRefNo = saveMsPolicyDetails(msPolicySaveReq);
					fleetRes.setNoOfVehicles(count);
					fleetRes.setPdrefno(pdRefNo);
					fleetRes.setInsuranceId(human.getCompanyId());
					fleetRes.setBranchCode(human.getBranchCode());
					fleetRes.setAgencyCode(human.getAgencyCode() == null ? "99999" : human.getAgencyCode().toString());
					fleetRes.setSectionId("99999");
					fleetRes.setProductId(human.getProductId());
					fleetRes.setMSRefNo("");
					fleetRes.setVehicleId("99999");
					fleetRes.setCdRefNo(human.getCdRefno() == null ? "99999" : human.getCdRefno().toString());
					fleetRes.setVdRefNo(pdRefNo);
					fleetRes.setCreatedBy(human.getCreatedBy());
					fleetRes.setRequestReferenceNo(human.getRequestReferenceNo());
					fleetRes.setPdrefno2(pdRefNo);
					Date policyStartDate = human.getPolicyStartDate();
					Date policyEndDate = human.getPolicyEndDate();

					// Format and parse back to remove time part
					String formattedStart = formatter.format(policyStartDate);
					String formattedEnd = formatter.format(policyEndDate);

					Date formattedStartDate = formatter.parse(formattedStart);
					Date formattedEndDate1 = formatter.parse(formattedEnd);

					// Now set as Date objects
					fleetRes.setEffectiveStartDate(formattedStartDate);
					fleetRes.setEffectiveEndDate(formattedEndDate1);
					
					fleetRes.setEffectiveStartDate(policyStartDate);
					fleetRes.setEffectiveEndDate(formattedEndDate1);
					fleetRes.setUnderwriterAdjustments(req.getUnderwriterAdjustments());
					
				}
				}
				
			}
			
			CalcEngine engine = new CalcEngine();
			engine.setLocationId(fleetRes.getLocationId() == null ? "99999" : fleetRes.getLocationId().toString());
			engine.setBranchCode(fleetRes.getBranchCode());
			engine.setInsuranceId(fleetRes.getInsuranceId());
			engine.setSectionId(fleetRes.getSectionId());
			engine.setProductId(fleetRes.getProductId());
			engine.setMsrefno(fleetRes.getMSRefNo().toString());
			engine.setCdRefNo(fleetRes.getCdRefNo().toString());
			engine.setVdRefNo(fleetRes.getVdRefNo().toString());
			engine.setPdrefno(fleetRes.getPdrefno().toString());
			engine.setCreatedBy(fleetRes.getCreatedBy());
			engine.setRequestReferenceNo(fleetRes.getRequestReferenceNo());
			engine.setEffectiveDate(fleetRes.getEffectiveStartDate() == null ? fleetRes.getEffectiveStartDate()
					: fleetRes.getEffectiveStartDate());
			engine.setPolicyEndDate(
					fleetRes.getEffectiveEndDate()== null ? fleetRes.getEffectiveEndDate() : fleetRes.getEffectiveEndDate());
			engine.setCoverModification(
					StringUtils.isBlank(fleetRes.getCoverModification()) ? "N" : fleetRes.getCoverModification());
			engine.setVehicleId("99999");
//			engine.setCoverId(fleetRes.getCoverId() == null ? "0" : data.getCoverId().toString());
			engine.setAgencyCode(fleetRes.getAgencyCode());
//			System.out.println((new StringBuilder("Json Req==>")).append((new Gson()).toJson(engine)).toString());
//			ObjectMapper objectMapper = new ObjectMapper();
//			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
//			System.out.println("Calculator Request -->Vehicle Id " + fleetRes.getRiskId() + " \nCover Id : "
//					+ fleetRes.getCoverId() + "\nRequest -->  " + objectMapper.writeValueAsString(engine));
			
			if (fleetRes.getUnderwriterAdjustments() != null && !fleetRes.getUnderwriterAdjustments().isEmpty()) {
			    engine.setUnderwriterAdjustments(fleetRes.getUnderwriterAdjustments());
			}

			mot =calc.policyCalculator(engine, tokens);

		} catch (Exception e) {

			log.error(e);
			e.printStackTrace();
			return null;
		}
		return mot;
	}
	
	
	public CompanyProductMaster getCompanyProductMasterDropdown2(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4, a5, a6);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			product = list.size() > 0 ? list.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}
	
	public synchronized String saveMsPolicyDetails(MsPolicySaveReq request) {
		String pdRefNo = "";
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
			List<MsPolicyDetails> list = new ArrayList<MsPolicyDetails>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MsPolicyDetails> query = cb.createQuery(MsPolicyDetails.class);
			// Find All
			Root<MsPolicyDetails> b = query.from(MsPolicyDetails.class);
			// Select
			query.select(b);

			Predicate n1 = cb.equal(b.get("currency"), request.getCurrency());
			Predicate n2 = cb.equal(b.get("endtCategoryId"), request.getEndtCategoryId());
			Predicate n3 = cb.equal(b.get("endtTypeId"), request.getEndtTypeId());
			Predicate n4 = cb.equal(b.get("exchangeRate"), request.getExchangeRate());
			Predicate n5 = cb.equal(b.get("groupCount"), request.getGroupCount());
			Predicate n6 = cb.equal(b.get("havepromocode"), request.getHavepromocode());
			Predicate n7 = cb.equal(b.get("promocode"), request.getPromocode());
			if (request.getPromocode() == null)
				n7 = cb.isNull(b.get("promocode"));

			Predicate n8 = cb.equal(b.get("noOfVehicles"), request.getNoOfVehicles());
			Predicate n9 = cb.equal(b.get("requestReferenceNo"), request.getRequestReferenceNo());
			// Predicate n1 = cb.equal( b.get("policyHolderTypeid")request.getPdRefno(null);
			Predicate n10 = cb.equal(b.get("status"), request.getStatus());
			Predicate n11 = cb.equal(b.get("periodOfInsurance"), request.getPeriodOfInsurance());
			Predicate n12 = cb.equal(b.get("buildingSuminsured"), request.getBuildingSumInsured());
			Predicate n13 = cb.equal(b.get("cdRefno"), request.getCdRefno());

			query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13);

			TypedQuery<MsPolicyDetails> result = em.createQuery(query);
			list = result.getResultList();
			if (list != null && list.size() > 0) {
				pdRefNo = String.format("%05d", list.get(0).getPdRefno());
			} else {
				// Random rand = new Random();
				// int random=rand.nextInt(90)+10;custData.getRegionCode()==null

				pdRefNo = genPdRefNo(); // sdf.format(new Date()) + random ;
				MsPolicyDetails saveNewEntry = new MsPolicyDetails();
				dozerMapper.map(request, saveNewEntry);
				saveNewEntry.setPdRefno(Long.valueOf(pdRefNo));
				saveNewEntry.setEntryDate(new Date());
				saveNewEntry.setBuildingSuminsured(request.getBuildingSumInsured());
				saveNewEntry.setClaimRatio(new BigDecimal(0)); 				
				msPolicyRepo.save(saveNewEntry);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null;
		}

		return pdRefNo;
	}
	
	public synchronized String genPdRefNo() {
		try {
			PdRefno entity;
			entity = pdRefNoRepo.save(new PdRefno());
			return String.format("%05d", entity.getPdRefno());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}

	}
}
