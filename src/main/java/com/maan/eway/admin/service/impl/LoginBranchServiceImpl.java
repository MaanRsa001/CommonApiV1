package com.maan.eway.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.AttacheIssuerBranchReq;
import com.maan.eway.admin.req.AttachedBranchesReq;
import com.maan.eway.admin.req.BrokerBranchGetReq;
import com.maan.eway.admin.req.BrokerBranchesReq;
import com.maan.eway.admin.req.GetAllBrokerBranchReq;
import com.maan.eway.admin.req.GetBrokerBranchReq;
import com.maan.eway.admin.req.GetallBrokerBranchesReq;
import com.maan.eway.admin.req.IssuerBranchGetReq;
import com.maan.eway.admin.req.LoginBranchReq;
import com.maan.eway.admin.req.LoginBranchesSaveReq;
import com.maan.eway.admin.req.UserCompanyProductGetReq;
import com.maan.eway.admin.res.BranchCriteriaRes;
import com.maan.eway.admin.res.BrokerBranchGetRes;
import com.maan.eway.admin.res.BrokerCompanyGetRes;
import com.maan.eway.admin.res.GetBrokerBranchRes;
import com.maan.eway.admin.res.GetallBrokerBranchesRes;
import com.maan.eway.admin.res.IssuerBranchGetRes;
import com.maan.eway.admin.res.IssuerCompanyGetRes;
import com.maan.eway.admin.res.LoginBranchRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginBranchService;
import com.maan.eway.auth.dto.LoginBranchCriteriaRes;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginBranchMasterArch;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginMasterArch;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.claim.PortalCreateBranchRequest;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.LoginBranchMasterArchRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterArchRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.PremiaCustomerDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class LoginBranchServiceImpl implements LoginBranchService {

	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo; 

	@Autowired
	private LoginMasterArchRepository loginArchRepo;

	@Autowired
	private LoginBranchMasterRepository loginBrokerRepo;

	@Autowired
	private LoginBranchMasterArchRepository loginBrokerArchRepo;
	
	@Autowired
	private LoginProductMasterRepository loginProductRepo;

	@Autowired
	private BranchMasterRepository branchRepo;
	
	@Autowired
	private PremiaCustomerDetailsRepository premiaCustRepo ; 

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MarineLoginApi marineLogin;
	
	@Value(value = "${claimUserSaveBranch}")
	private String claimUserSaveBranch;
	
	Gson json = new Gson();

	private Logger log = LogManager.getLogger(LoginBranchServiceImpl.class);

//*************************************** Add Branch Methods **********************************************************//	

	@Override
	public LoginCreationRes attachBrokerBranches(AttachCompaniesReq req) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss");
		try {
			// Find Data
			LoginMaster findLogin = loginRepo.findByLoginId(req.getLoginId());

			// Save in Arch tables
			String archId = "AI-" + idf.format(new Date());
			LoginMasterArch loginArch = dozerMapper.map(findLogin, LoginMasterArch.class);
			loginArch.setArchId(archId);
			loginArchRepo.saveAndFlush(loginArch);

			// Branch Setup
			String totalBranches = "";
			String companies = "";
			List<String> branchIds = new ArrayList<String>();

			for (AttachedBranchesReq data : req.getAttachedCompanies()) {
				String branches = "";
				for (BrokerBranchesReq data2 : data.getAttachedBranches()) {
					branches = StringUtils.isBlank(branches) ? data2.getBranchCode()
							: branches + "," + data2.getBranchCode();
					branchIds.add(data2.getBranchCode());
				}

				companies = StringUtils.isBlank(companies) ? data.getInsuranceId() : "," + data.getInsuranceId();
				totalBranches = StringUtils.isBlank(branches) ? totalBranches : totalBranches + "," + branches;
			}
			List<BranchMaster> branchList = getBranchList(branchIds);
			List<String> rigionList = branchList.stream().map(o -> o.getRegionCode()).collect(Collectors.toList());

			// Remove Duplicate
			rigionList = rigionList.stream().distinct().collect(Collectors.toList());

			String regions = rigionList == null || rigionList.size() == 0 ? "" : String.join(",", rigionList);

			// Update Login Master
			findLogin.setAttachedBranches(totalBranches);
			findLogin.setAttachedRegions(regions);
			findLogin.setAttachedCompanies(companies);

			loginRepo.saveAndFlush(findLogin);

			log.info("Login Master Updated Details ---> " + json.toJson(findLogin));

			res.setResponse("Updated Successfully");

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public LoginCreationRes attachIssuerBranches(AttachIssuerBrannchReq req) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss");
		try {
			// Find Data
			LoginMaster findLogin = loginRepo.findByLoginId(req.getLoginId());

			// Save in Arch tables
			String archId = "AI-" + idf.format(new Date());
			LoginMasterArch loginArch = dozerMapper.map(findLogin, LoginMasterArch.class);
			loginArch.setArchId(archId);
			loginArchRepo.saveAndFlush(loginArch);

			/*
			 * //Login Broker Branch Master LoginBrokerBranchMaster loginBroker =new
			 * LoginBrokerBranchMaster();
			 * loginBroker=loginBrokerRepo.findByLoginId(req.getLoginId()); // Save in Arch
			 * tables String brokerArchId = "AI-" + idf.format(new Date());
			 * LoginBrokerBranchMasterArch loginBrokerArch = dozerMapper.map(loginBroker,
			 * LoginBrokerBranchMasterArch.class); loginBrokerArch.setArchId(brokerArchId);
			 * loginBrokerArchRepo.saveAndFlush(loginBrokerArch);
			 */

			// Branch Setup
			String totalBranches = "";
			String companies = "";
			List<String> branchIds = new ArrayList<String>();

			for (AttacheIssuerBranchReq data : req.getAttachedCompanies()) {

				String branches = "";
				for (String data2 : data.getAttachedBranches()) {
					branches = StringUtils.isBlank(branches) ? data2 : branches + "," + data2;
					branchIds.add(data2);
				}
				companies = StringUtils.isBlank(companies) ? data.getInsuranceId() : "," + data.getInsuranceId();
				totalBranches = StringUtils.isBlank(branches) ? totalBranches : totalBranches + "," + branches;
			}
			List<BranchMaster> branchList = getBranchList(branchIds);
			List<String> rigionList = branchList.stream().map(o -> o.getRegionCode()).collect(Collectors.toList());

			// Remove Duplicate
			rigionList = rigionList.stream().distinct().collect(Collectors.toList());

			String regions = rigionList == null || rigionList.size() == 0 ? "" : String.join(",", rigionList);

			// Update Login Master
			findLogin.setAttachedBranches(totalBranches);
			findLogin.setAttachedRegions(regions);
			findLogin.setAttachedCompanies(companies);
			loginRepo.saveAndFlush(findLogin);

			log.info("Login Master Updated Details ---> " + json.toJson(findLogin));

			res.setResponse("Updated Successfully");

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	public List<BranchMaster> getBranchList(List<String> branchIds) {
		List<BranchMaster> branchList = new ArrayList<BranchMaster>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BranchMaster> query = cb.createQuery(BranchMaster.class);

			// Find All
			Root<BranchMaster> b = query.from(BranchMaster.class);

			// Branch Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);
			// Select
			query.select(b);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("entryDate")));

			// In
			Expression<String> e0 = b.get("branchCode");

			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(branchIds);

			query.where(n1, n2, n3).orderBy(orderList);
			// Get Result
			TypedQuery<BranchMaster> result = em.createQuery(query);
			branchList = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return branchList;
	}

	@Override
	public List<BrokerCompanyGetRes> getBrokerBranches(BrokerBranchGetReq req) {
		List<BrokerCompanyGetRes> companyList = new ArrayList<BrokerCompanyGetRes>();
		try {
			// Find Data
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());

			List<String> branchIds = new ArrayList<>(Arrays.asList(loginData.getAttachedBranches().split(",")));
			// Criteria Query
			List<BranchCriteriaRes> list = getCompanyAndBranchDetails(branchIds);

			Map<String, List<BranchCriteriaRes>> groupByCompany = list.stream()
					.collect(Collectors.groupingBy(BranchCriteriaRes::getCompanyId));

			for (String companyId : groupByCompany.keySet()) {
				BrokerCompanyGetRes companyRes = new BrokerCompanyGetRes();

				List<BranchCriteriaRes> getDatas = groupByCompany.get(companyId);
				List<BrokerBranchGetRes> attachedBranches = new ArrayList<BrokerBranchGetRes>();

				for (BranchCriteriaRes data : getDatas) {
					BrokerBranchGetRes branchRes = new BrokerBranchGetRes();

					// Branch Res
					branchRes.setBranchCode(data.getBranchCode());
					branchRes.setBranchName(data.getBranchName());
					branchRes.setRegionCode(data.getRegionCode());
					branchRes.setRegionName(data.getRegionName());
					attachedBranches.add(branchRes);
				}

				// Company Res
				companyRes.setInsuranceId(getDatas.get(0).getCompanyId());
				companyRes.setCompanyName(getDatas.get(0).getCompanyName());
				companyRes.setAttachedBranches(attachedBranches);
				companyList.add(companyRes);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return companyList;
	}

	@Override
	public List<IssuerCompanyGetRes> getIssuerBranches(IssuerBranchGetReq req) {
		List<IssuerCompanyGetRes> companyList = new ArrayList<IssuerCompanyGetRes>();
		try {
			// Find Data
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());

			List<String> branchIds = new ArrayList<>(Arrays.asList(loginData.getAttachedBranches().split(",")));
			// Criteria Query
			List<BranchCriteriaRes> list = getCompanyAndBranchDetails(branchIds);

			Map<String, List<BranchCriteriaRes>> groupByCompany = list.stream()
					.collect(Collectors.groupingBy(BranchCriteriaRes::getCompanyId));

			for (String companyId : groupByCompany.keySet()) {
				IssuerCompanyGetRes companyRes = new IssuerCompanyGetRes();

				List<BranchCriteriaRes> getDatas = groupByCompany.get(companyId);
				List<IssuerBranchGetRes> attachedBranches = new ArrayList<IssuerBranchGetRes>();

				for (BranchCriteriaRes data : getDatas) {
					IssuerBranchGetRes branchRes = new IssuerBranchGetRes();

					// Branch Res
					branchRes.setBranchCode(data.getBranchCode());
					branchRes.setBranchName(data.getBranchName());
					branchRes.setRegionCode(data.getRegionCode());
					branchRes.setRegionName(data.getRegionName());
					attachedBranches.add(branchRes);
				}

				// Company Res
				companyRes.setInsuranceId(getDatas.get(0).getCompanyId());
				companyRes.setCompanyName(getDatas.get(0).getCompanyName());
				companyRes.setAttachedBranches(attachedBranches);
				companyList.add(companyRes);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return companyList;
	}

	public List<BranchCriteriaRes> getCompanyAndBranchDetails(List<String> branchIds) {
		List<BranchCriteriaRes> list = new ArrayList<BranchCriteriaRes>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BranchCriteriaRes> query = cb.createQuery(BranchCriteriaRes.class);

			// Find All
			Root<BranchMaster> b = query.from(BranchMaster.class);

			// Select Region Name SubQuery for Effective Date Max Filter
//			Subquery<Long> regEff = query.subquery(Long.class);
//			Root<RegionMaster> r = regEff.from(RegionMaster.class);
//			Subquery<Long> region = query.subquery(Long.class);
//			Root<RegionMaster> rn = region.from(RegionMaster.class);
//
//			regEff.select(cb.greatest(r.get("effectiveDateStart")));
//			Predicate e1 = cb.equal(rn.get("regionCode"), r.get("regionCode"));
//			Predicate e2 = cb.lessThanOrEqualTo(r.get("effectiveDateStart"), today);
//			regEff.where(e1, e2);
//
//			region.select(rn.get("regionName"));
//			Predicate r1 = cb.equal(rn.get("regionCode"), b.get("regionCode"));
//			Predicate r2 = cb.equal(rn.get("effectiveDateStart"), regEff);
//			region.where(r1, r2);

			// Select Company Name SubQuery for Effective Date Max Filter
			Subquery<Date> insEff = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> i = insEff.from(InsuranceCompanyMaster.class);
			Subquery<Long> company = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ins = company.from(InsuranceCompanyMaster.class);

			insEff.select(cb.greatest(i.get("effectiveDateStart").as(Date.class)));
			Predicate i1 = cb.equal(ins.get("companyId"), i.get("companyId"));
			Predicate i2 = cb.lessThanOrEqualTo(i.get("effectiveDateStart"), today);
			insEff.where(i1, i2);

			company.select(ins.get("companyName"));
			Predicate ins1 = cb.equal(ins.get("companyId"), b.get("companyId"));
			Predicate ins2 = cb.equal(ins.get("effectiveDateStart"), insEff);
			company.where(ins1, ins2);

			// Select
			query.multiselect(b.get("branchCode").alias("branchCode"), b.get("branchName").alias("branchName"),
					b.get("regionCode").alias("regionCode"), b.get("regionCode").alias("regionName"),
					b.get("companyId").alias("companyId"), company.alias("companyName"));

			// Branch Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("entryDate")));

			// In
			Expression<String> e0 = b.get("branchCode");

			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(branchIds);

			query.where(n1, n2, n3).orderBy(orderList);

			// Get Result
			TypedQuery<BranchCriteriaRes> result = em.createQuery(query);
			// System.out.println(result.unwrap(org.hibernate.query.Query.class).getQueryString()
			// );
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public LoginCreationRes attachBrokerCompanyBranch(AttachBrokerBranchReq req, String token) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss");
		try {
			// Login Data
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			LoginUserInfo loginUserInfo = loginUserRepo.findByLoginId(req.getLoginId());

			// Find Data
			String brokerBranchCode = "None";

			if (StringUtils.isBlank(req.getBrokerBranchCode()) && (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User"))) {
				long count = loginBrokerRepo.countByLoginId(req.getLoginId());
				brokerBranchCode = String.valueOf(count + 1);
			} else if (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User")) {
				brokerBranchCode = req.getBrokerBranchCode();
			}

//			LoginBranchMaster findBranch = loginBrokerRepo.findByBrokerBranchCodeAndLoginIdAndBranchCodeAndCompanyId(
//					brokerBranchCode, req.getLoginId(), req.getBranchCode(), req.getCompanyId());
			
//			LoginBranchMaster findBranch = loginBrokerRepo.findByBrokerBranchCodeAndLoginIdAndCompanyId(
//					brokerBranchCode, req.getLoginId(),  req.getCompanyId());
			
			LoginBranchMaster findBranch = loginBrokerRepo.findByBranchCodeAndLoginIdAndCompanyId(
					req.getBranchCode(), req.getLoginId(),  req.getCompanyId());
			
			LoginBranchMaster save = dozerMapper.map(req, LoginBranchMaster.class);
			if (findBranch != null) {
				// Delete Old Record
				loginBrokerRepo.delete(findBranch);
				// Save in Arch tables
				String archId = "AI-" + idf.format(new Date());
				LoginBranchMasterArch loginArch = dozerMapper.map(findBranch, LoginBranchMasterArch.class);
				loginArch.setArchId(archId);
				loginBrokerArchRepo.saveAndFlush(loginArch);

				save.setEntryDate(findBranch.getEntryDate());
				save.setCreatedBy(findBranch.getCreatedBy());
				save.setUpdatedBy(req.getCreatedBy());
				save.setUpdatedDate(new Date());
			} else {
				save.setEntryDate(new Date());
				save.setCreatedBy(req.getCreatedBy());
				save.setUpdatedBy(req.getCreatedBy());
				save.setUpdatedDate(new Date());
			}

			save.setOaCode(Integer.valueOf(loginData.getOaCode()));
			save.setAgencyCode(Integer.valueOf(loginData.getAgencyCode()));
			save.setAttachedBranch(
					StringUtils.isBlank(req.getAttachedBranch()) ? req.getBranchCode() : req.getAttachedBranch());
			save.setAttachedCompany(
					StringUtils.isBlank(req.getAttachedCompany()) ? req.getCompanyId() : req.getAttachedCompany());
			save.setBrokerBranchCode(brokerBranchCode);
			
			save.setUserType(loginData.getUserType());
			save.setSubUserType(loginData.getSubUserType());
			List<BranchMaster> branchname = branchRepo.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(req.getCompanyId(),req.getBranchCode());
			save.setBranchName(branchname.get(0).getBranchName());
			save.setBrokerBranchName(req.getBrokerBranchName());
			save.setCustomerCode(loginUserInfo.getCustomerCode());
			save.setCustomerName(loginUserInfo.getCustomerName());
		
			loginBrokerRepo.save(save);
			marineLogin.createBranch(save);

				saveClaimBranch(req, token, branchname);
			log.info("Login Master Updated Details ---> " + json.toJson(save));
			res.setResponse("Branch Added Successfully");

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public void saveClaimBranch(AttachBrokerBranchReq req, String token, List<BranchMaster> branchname) {
		if (req.getCompanyId().equalsIgnoreCase("100046") || req.getCompanyId().equalsIgnoreCase("100047")
				|| req.getCompanyId().equalsIgnoreCase("100048") || req.getCompanyId().equalsIgnoreCase("100049")
				|| req.getCompanyId().equalsIgnoreCase("100050")) {
			List<PortalCreateBranchRequest> requestList = new ArrayList<>();
			PortalCreateBranchRequest branchReq = new PortalCreateBranchRequest();
			branchReq.setBranchCode(branchname.get(0).getBranchCode());
			branchReq.setBranchName(branchname.get(0).getBranchName());
			branchReq.setCompanyId(req.getCompanyId());
			branchReq.setCreatedBy(req.getCreatedBy());
			branchReq.setLoginId(req.getLoginId());
			branchReq.setStatus(req.getStatus());
			requestList.add(branchReq);

			String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
			actualToken = actualToken.split(",")[0];
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.set("X-AUTH-TOKEN", actualToken);
//			headers.setBearerAuth(actualToken);
			HttpEntity<List<PortalCreateBranchRequest>> requestEntity = new HttpEntity<>(requestList, headers);
			ResponseEntity<String> response = restTemplate.exchange(claimUserSaveBranch, HttpMethod.POST, requestEntity,
					String.class);
			System.out.println("Response: " + response.getBody());
		}
	}
	
	

	@Override
	public GetBrokerBranchRes getBrokerCompanyBranch(GetBrokerBranchReq req) {
		GetBrokerBranchRes res = new GetBrokerBranchRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss");
		try {
			// Find Data
			LoginBranchMaster findBranch = loginBrokerRepo.findByBrokerBranchCodeAndLoginIdAndCompanyId(
					req.getBrokerBranchCode(), req.getLoginId(), req.getInsuranceId());
			if(findBranch!=null) {
			res = dozerMapper.map(findBranch, GetBrokerBranchRes.class);
			res.setCustomerCode(findBranch.getCustomerCode() );
			}
			else {
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<GetBrokerBranchRes> getallBrokerCompanyBranch(GetAllBrokerBranchReq req) {
		List<GetBrokerBranchRes> resList = new ArrayList<GetBrokerBranchRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss");
		try {
			// Find Data
			List<LoginBranchMaster> findBranches = loginBrokerRepo.findByLoginIdOrderByUpdatedDateDesc(req.getLoginId());
			List<String> branchCode =findBranches.stream().map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
			List<String> attachedBranchCode = findBranches.stream().map(LoginBranchMaster ::getAttachedBranch ).collect(Collectors.toList()) ;
			List<String> totalList = new ArrayList<>();
			totalList.addAll(branchCode);
			totalList.addAll(attachedBranchCode);
			Set<String> removeDuplicateBranch = new HashSet<>(totalList);
			
			List<LoginBranchCriteriaRes> loginCriteriaRes = getBranchDetails(removeDuplicateBranch);
			
			for (LoginBranchMaster brokerBranch : findBranches) {
				List<LoginBranchCriteriaRes>  filterBranch = loginCriteriaRes.stream().filter( o -> o.getBranchCode().equalsIgnoreCase(brokerBranch.getBranchCode()) ).collect(Collectors.toList());
				
				// Get Active Branches
				if( filterBranch.size()>0 ) {
					GetBrokerBranchRes res = new GetBrokerBranchRes();
					mapper.map(brokerBranch, res);
					res.setCustomerCode(brokerBranch.getCustomerCode());
					resList.add(res);
				}
				
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}
	
	
	private List<LoginBranchCriteriaRes> getBranchDetails(Set<String> removeDuplicateBranch) {
		List<LoginBranchCriteriaRes> list = new ArrayList<LoginBranchCriteriaRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			today = cal.getTime();

			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginBranchCriteriaRes> query = cb.createQuery(LoginBranchCriteriaRes.class);
			


			// Find All
			Root<BranchMaster> b = query.from(BranchMaster.class);

			// Company Effective Date Max Filter
			Subquery<Long> company = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ins = company.from(InsuranceCompanyMaster.class);
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateStart").as(Date.class)));
			Predicate ceff1 = cb.equal(ocpm2.get("companyId"), ins.get("companyId"));
			Predicate ceff2 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"), today);
			effectiveDate2.where(ceff1,ceff2);
			
			// Company Name
			company.select(ins.get("companyName"));
			Predicate ins1 = cb.equal(ins.get("companyId"), b.get("companyId"));
			Predicate ins2 = cb.equal(ins.get("effectiveDateStart"), effectiveDate2);
			company.where(ins1,ins2);
			
			// Company Currency Effective Date Max Filter
			Subquery<Long> currency = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> currencyId = currency.from(InsuranceCompanyMaster.class);
			Subquery<Date> effectiveDate6 = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm6 = effectiveDate6.from(InsuranceCompanyMaster.class);
			effectiveDate6.select(cb.greatest(ocpm6.get("effectiveDateStart").as(Date.class)));
			Predicate iceff3 = cb.equal(ocpm6.get("companyId"), currencyId.get("companyId"));
			Predicate iceff4 = cb.lessThanOrEqualTo(ocpm6.get("effectiveDateStart"), today);
			effectiveDate6.where(iceff3,iceff4);
			
			// Currency Id
			currency.select(currencyId.get("currencyId"));
			Predicate in3 = cb.equal(currencyId.get("companyId"), b.get("companyId"));
			Predicate in4 = cb.equal(currencyId.get("effectiveDateStart"), effectiveDate6);
			currency.where(in3,in4);
			
		
			// Select
			query.multiselect(b.get("branchCode").alias("branchCode") , b.get("regionCode").alias("regionCode") ,
					b.get("companyId").alias("companyId") , b.get("branchName").alias("branchName") ,
					company.alias("companyName") ,
					//region.alias("regionName") , 
				//	companyLogo.alias("companyLogo") ,
					currency.alias("currencyId") );

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate eff1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
		//	Predicate eff2 = cb.equal(ocpm1.get("regionCode"), b.get("regionCode"));
			Predicate eff3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate eff4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(eff1, eff3, eff4 );
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			//In 
			Expression<String>e0=b.get("branchCode");
			
			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(removeDuplicateBranch) ;

			query.where(n1, n2, n3).orderBy(orderList);

			// Get Result
			TypedQuery<LoginBranchCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return list;
		
	}

	
	@Override
	public List<GetallBrokerBranchesRes> getallBrokerBranches(GetallBrokerBranchesReq req) {
	List<GetallBrokerBranchesRes> resList = new ArrayList<GetallBrokerBranchesRes>();
	try {

		List<LoginBranchMaster> branches = loginBrokerRepo.findByLoginIdAndCompanyIdAndBranchCode(req.getLoginId(),req.getCompanyId(),req.getBranchCode());
		for(LoginBranchMaster data : branches) {
		GetallBrokerBranchesRes res = new GetallBrokerBranchesRes();
		res.setBrokerBranchCode(data.getBrokerBranchCode());
		res.setBrokerBranchName(data.getBrokerBranchName());
		resList.add(res);
		}
	}
	catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is -->" + e.getMessage());
		return null;
	}
	return resList;
}

	@Override
	public List<GetBrokerBranchRes> getallNonSelectedUserCompanyBranches(UserCompanyProductGetReq req) {
		List<GetBrokerBranchRes> resList = new ArrayList<GetBrokerBranchRes>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			LoginMaster brokerData = loginRepo.findByAgencyCodeAndOaCodeAndCompanyId(req.getOaCode(),Integer.valueOf(req.getOaCode()),req.getInsuranceId());
			
			List<LoginBranchMaster> branchList = loginBrokerRepo.findByLoginIdAndStatus(brokerData.getLoginId(),"Y");
			List<LoginBranchMaster> userbranchList = loginBrokerRepo.findByLoginIdAndStatus(req.getLoginId(),"Y");
			
			// Map
			for (LoginBranchMaster data : branchList ) {
				
				List<LoginBranchMaster> filterUser = userbranchList.stream().filter( o ->  o.getBrokerBranchCode().equalsIgnoreCase(data.getBrokerBranchCode())).collect(Collectors.toList());				
				
				if( filterUser.size()<=0	) {
					GetBrokerBranchRes res = new GetBrokerBranchRes();
					
					res = dozerMapper.map(data, GetBrokerBranchRes.class);
					res.setCustomerCode(data.getCustomerCode());
					resList.add(res);
			
				}
		}
				
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return resList;
	}

	@Override
	public LoginCreationRes saveLoginBranches(LoginBranchesSaveReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		try { 
			
			LoginMaster agencyCode = loginRepo.findByLoginId(req.getLoginId());
			LoginMaster brokerData = loginRepo.findByAgencyCodeAndCompanyId(req.getOaCode(),req.getInsuranceId());
			
			List<LoginBranchMaster> list =   loginBrokerRepo.findByLoginId(brokerData.getLoginId());
			
		
		for (String id : req.getBrokerBranchIds()) {
			List<LoginBranchMaster>  brokerDatas = list.stream().filter(o -> o.getBrokerBranchCode().equals(id) ).collect(Collectors.toList());
			if(brokerDatas.size()>0) {
				LoginBranchMaster brokerBranchData = brokerDatas.get(0);
				LoginBranchMaster save = new LoginBranchMaster();
				dozerMapper.map(brokerBranchData, save);
				save.setCompanyId(req.getInsuranceId());
				save.setCreatedBy(req.getCreatedBy());
				save.setEntryDate(new Date());
				save.setLoginId(req.getLoginId());
				save.setOaCode(Integer.valueOf(req.getOaCode()));
				save.setAgencyCode(Integer.valueOf(agencyCode.getAgencyCode()));
				//save.setBackDays(0);
				
				loginBrokerRepo.saveAndFlush(save);
				log.info("Saved Details is ---> " + json.toJson(save));
			}
				
				
			}		
			
			res.setResponse("Branches Added Successfully");
			res.setAgencyCode(agencyCode.getAgencyCode());
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<LoginBranchRes> getLoginbranches(LoginBranchReq req) {
		// TODO Auto-generated method stub
		List<LoginBranchRes> resList = new ArrayList<LoginBranchRes>();
		try {
			List<LoginBranchMaster> datas = new ArrayList<LoginBranchMaster>();
			
		if(StringUtils.isNotBlank(req.getBranchCode()) ) {
			 datas = loginBrokerRepo.findByLoginIdAndBranchCodeOrderByBranchCodeAsc(req.getLoginId() , req.getBranchCode() );	
		} else {
			 datas = loginBrokerRepo.findByLoginIdOrderByBranchCodeAsc(req.getLoginId());
		}
		
			
		if(datas.size()>0 && datas!=null) {
		for(LoginBranchMaster data : datas) {
			LoginBranchRes res = new LoginBranchRes();
			res.setBranchCode(data.getBranchCode()==null?"":data.getBranchCode());
			res.setBranchName(data.getBranchName()==null?"":data.getBranchName());
			res.setBrokerBranchCode(data.getBrokerBranchCode()==null?"":data.getBrokerBranchCode());
			res.setBrokerBranchName(data.getBrokerBranchName()==null?"":data.getBrokerBranchName());
			resList.add(res);
			}
		}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --->"+ e.getMessage());
			return null;
		}
		return resList;
		
	}
	
	
	
}