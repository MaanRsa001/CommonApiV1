package com.maan.eway.auth.service.impl;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.auth.dto.AuthToken2;
import com.maan.eway.auth.dto.BrokerProductCompaniesRes;
import com.maan.eway.auth.dto.BrokerProductsGetRes;
import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.ClaimLoginResponse;
import com.maan.eway.auth.dto.ClaimLogoutResponse;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.ForgetPasswordReq;
import com.maan.eway.auth.dto.GetEncryptionkeyReq;
import com.maan.eway.auth.dto.LobProductRes;
import com.maan.eway.auth.dto.LoginBranchCriteriaRes;
import com.maan.eway.auth.dto.LoginBranchDetailsRes;
import com.maan.eway.auth.dto.LoginProductCriteriaRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.LogoutRequest;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.auth.token.JwtTokenUtil;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EwayLobMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginMasterId;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.SessionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.crm.service.CrmService;
import com.maan.eway.error.Error;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.MailDataDetailsRepository;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.EwayLobMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MailMasterRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.SessionMasterRepository;
import com.maan.eway.repository.SmsConfigMasterRepository;
import com.maan.eway.repository.SmsDataDetailsRepository;
import com.maan.eway.res.SuccessRes;

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
import jakarta.servlet.http.HttpServletRequest;


@Lazy
@Service 
public class AuthendicationServiceImpl implements AuthendicationService, UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private LoginMasterRepository loginRepo;
	@Autowired
	private SessionMasterRepository sessionRep;
	@Autowired
	private EncryDecryService endecryService;
	@Autowired
	private BranchMasterRepository branchRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo ;

	@Autowired
	private InsuranceCompanyMasterRepository companyRepo;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;
	
	@Autowired
	private LoginProductMasterRepository loginProductRepo;
	
	@Autowired
	private ProductMasterRepository productRepo;
	
	@Autowired
	private ProductMasterRepository companyProductRepo;
	
	@Autowired
	private MailMasterRepository mailRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private LoginUserInfoRepository userInfoRepo ;
	
	@Autowired
	private MailDataDetailsRepository mailDataRepo ;
	
	@Autowired
	private  SmsConfigMasterRepository smsRepo ;
	
	@Autowired
	private  SmsDataDetailsRepository smsDataRepo ;
	
	@Autowired
	private BCryptPasswordEncoder cryptoService;
	
	@Autowired
	private EwayLobMasterRepository ewayLineOfBusinessRepository;

	@Value(value = "${crm.changePassword}")
	private String changePassword;
	
	@Autowired
	private CrmService crmService;
	
	private Logger log = LogManager.getLogger(AuthendicationServiceImpl.class);
	
	@Override
	public CommonLoginRes checkUserLogin(LoginRequest mslogin, HttpServletRequest http) {
		CommonLoginRes res = new CommonLoginRes();
		try {
			LoginMaster login = new LoginMaster();
			
			if(mslogin.getLoginId().equalsIgnoreCase("guest") ) {
				login =loginRepo.findByLoginId(mslogin.getLoginId());
			} else {
				passwordEnc passEnc = new passwordEnc();
				String epass = passEnc.crypt(mslogin.getPassword().trim());
				log.info("Encrpted password "+epass);
				 login =loginRepo.findByLoginIdAndPassword(mslogin.getLoginId(),epass);
					
				 if(login==null) {
					 login =loginRepo.findByLoginIdAndPassword(mslogin.getLoginId(),mslogin.getPassword().trim());
				 }
				 if(login.getPwdCount() !=null && Integer.parseInt(login.getPwdCount())>0) {
					 login.setPwdCount("0");
					 loginRepo.save(login);
				 }
			}
			
			if (login != null ) {
				if(http!=null)
					http.getSession().removeAttribute(mslogin.getLoginId());
				String token = jwtTokenUtil.doGenerateToken(mslogin.getLoginId());
				log.info("-----token------" + token);
				SessionMaster session = new SessionMaster();
				session.setLoginId(mslogin.getLoginId());
				session.setTokenId(token);
				session.setStatus("ACTIVE");
//				String temptoken = bCryptPasswordEncoder.encode("CommercialClaim");
//				session.setTempTokenid(temptoken);
				session.setTempTokenid(token);
				session.setUserType(login.getUserType());
				session.setSubUserType(login.getSubUserType());
				Date today = new Date(); 
				session.setEntryDate(today);
				session.setStartTime(today);
				session.setIpAddress(mslogin.getIpAddress());
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, 50);
				Date endTime = cal.getTime();
				session.setEndTime(endTime );
				session.setIpAddress(http.getLocalAddr() +":" +http.getLocalPort());
				session =sessionRep.save(session);
				ClaimLoginResponse loginRes = new ClaimLoginResponse(); 
				
				/*if (login.getLoginId().equalsIgnoreCase("guest") ) {
					loginRes.setToken(session.getTempTokenid());
					loginRes.setLoginId(login.getLoginId());
					loginRes.setUserName("guest");
					loginRes.setUserMail("");
					loginRes.setUserMobile("");
					loginRes.setUserType(login.getUserType());
					loginRes.setSubUserType(login.getSubUserType());
					loginRes.setOaCode(login.getOaCode().toString());
					loginRes.setBankCode(login.getBankCode());
					
					
					
				} else */ {
					loginRes = setTokenResponse(session,login,mslogin);
				}
				
				
				//Response 
				res.setCommonResponse(loginRes);
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
				res.setMessage("Success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public CommonLoginRes checkUserOtpLogin(LoginRequest mslogin, HttpServletRequest http) {
		CommonLoginRes res = new CommonLoginRes();
		try {
			LoginMaster login = new LoginMaster();
			
			if(mslogin.getLoginId().equalsIgnoreCase("guest") ) {
				login =loginRepo.findByLoginId(mslogin.getLoginId());
			} else {
				if(mslogin.getPassword() != null) {
				passwordEnc passEnc = new passwordEnc();
				String epass = passEnc.crypt(mslogin.getPassword().trim());
				log.info("Encrpted password "+epass);
				
				 login =loginRepo.findByLoginIdAndPassword(mslogin.getLoginId(),epass);
					
				 if(login==null) {
					 login =loginRepo.findByLoginIdAndPassword(mslogin.getLoginId(),mslogin.getPassword().trim());
				 }
				 if(login.getPwdCount() !=null && Integer.parseInt(login.getPwdCount())>0) {
					 login.setPwdCount("0");
					 loginRepo.save(login);
				 }
				}else {
					 login =loginRepo.findByLoginId(mslogin.getLoginId());
				 }
			}
			
			if (login != null ) {
				if(http!=null)
					http.getSession().removeAttribute(mslogin.getLoginId());
				String token = jwtTokenUtil.doGenerateToken(mslogin.getLoginId());
				log.info("-----token------" + token);
				SessionMaster session = new SessionMaster();
				session.setLoginId(mslogin.getLoginId());
				session.setTokenId(token);
				session.setStatus("ACTIVE");
//				String temptoken = bCryptPasswordEncoder.encode("CommercialClaim");
//				session.setTempTokenid(temptoken);
				session.setTempTokenid(token);
				session.setUserType(login.getUserType());
				session.setSubUserType(login.getSubUserType());
				Date today = new Date(); 
				session.setEntryDate(today);
				session.setStartTime(today);
				session.setIpAddress(mslogin.getIpAddress());
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, 50);
				Date endTime = cal.getTime();
				session.setEndTime(endTime );
				session.setIpAddress(http.getLocalAddr() +":" +http.getLocalPort());
				session =sessionRep.save(session);
				ClaimLoginResponse loginRes = new ClaimLoginResponse(); 
				
				/*if (login.getLoginId().equalsIgnoreCase("guest") ) {
					loginRes.setToken(session.getTempTokenid());
					loginRes.setLoginId(login.getLoginId());
					loginRes.setUserName("guest");
					loginRes.setUserMail("");
					loginRes.setUserMobile("");
					loginRes.setUserType(login.getUserType());
					loginRes.setSubUserType(login.getSubUserType());
					loginRes.setOaCode(login.getOaCode().toString());
					loginRes.setBankCode(login.getBankCode());
					
					
					
				} else */ {
					loginRes = setTokenResponse(session,login,mslogin);
				}
				
				
				//Response 
				res.setCommonResponse(loginRes);
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
				res.setMessage("Success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private ClaimLoginResponse setTokenResponse(SessionMaster session, LoginMaster login, LoginRequest mslogin) {
		ClaimLoginResponse r = new ClaimLoginResponse();
		try {
			LoginUserInfo userInfo = loginUserRepo.findByLoginId(login.getLoginId());
			
			r.setToken(session.getTempTokenid());
			r.setLoginId(login.getLoginId());
			r.setUserName(userInfo.getUserName());
			r.setUserMail(StringUtils.isBlank(userInfo.getUserMail())?"":userInfo.getUserMail());
			r.setUserMobile(StringUtils.isBlank(userInfo.getUserMobile())?"":userInfo.getUserMobile());
			r.setUserType(login.getUserType());
			r.setSubUserType(login.getSubUserType());
			r.setOaCode(login.getOaCode().toString());
			r.setBankCode(login.getBankCode());
			r.setCountryId(userInfo.getCountryCode() );
			r.setCustomerCode(userInfo.getCustomerCode());
			r.setCustomerName(userInfo.getCustomerName());
			r.setSourceCode(userInfo.getCoreAppBrokerCode());
			r.setLoginCreationAccess(userInfo.getLoginAccess());
			r.setReferralManageAccess(userInfo.getReferralAccess());
			r.setClaimCustomerAccess(userInfo.getClaimCustomerAccess());
			r.setModuleIds(convertModuleIds(login.getModuleIds()));
			if(login!=null && StringUtils.isNotBlank(login.getAttachedCompanies()) ) {
				String[] array = login.getAttachedCompanies().split(",");
				List<String> comapanyIds = 	new ArrayList<String>(Arrays.asList(array)) ;
				comapanyIds = comapanyIds.stream().filter( o -> StringUtils.isNotBlank(o)  ).collect(Collectors.toList());			
				r.setGetCompanies(comapanyIds);
			}
			List<InsuranceCompanyMaster> companyList = companyRepo.findByCompanyIdAndStatusOrderByEffectiveDateEndDesc(login.getCompanyId(),"Y");
			r.setCurrencyId(companyList.size() > 0 && StringUtils.isNotBlank(companyList.get(0).getCurrencyId()) ? companyList.get(0).getCurrencyId() : "TZS");			
			// Branch Res	
			List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginIdAndStatus(login.getLoginId() , "Y");
		
			List<String> branchCode =loginBranch.stream().map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
			List<String> attachedBranchCode = loginBranch.stream().map(LoginBranchMaster ::getAttachedBranch ).collect(Collectors.toList()) ;
			List<String> totalList = new ArrayList<>();
			totalList.addAll(branchCode);
			totalList.addAll(attachedBranchCode);
			List<String> companies = new ArrayList<String>(); //loginBranch.stream().map(LoginBranchMaster ::getCompanyId ).collect(Collectors.toList()) ;
			companies.add(login.getCompanyId());
			Set<String> removeDuplicateCompany = new HashSet<>(companies);
			Set<String> removeDuplicateBranch = new HashSet<>(totalList);
			List<LoginBranchCriteriaRes> loginCriteriaRes = getBranchDetails(removeDuplicateBranch ,login.getCompanyId() );
			
			List<LoginBranchDetailsRes> loginBranchRes = new ArrayList<LoginBranchDetailsRes>();
			
			for ( LoginBranchMaster data :  loginBranch  ) {
				LoginBranchDetailsRes branchRes = new LoginBranchDetailsRes();
				
				List<LoginBranchCriteriaRes>  filterBranchCriteria = loginCriteriaRes.stream().filter( o ->  o.getBranchCode().equalsIgnoreCase(data.getBranchCode()) ).collect(Collectors.toList());
				branchRes.setBranchCode( data.getBranchCode() );
				branchRes.setBranchName(data.getBranchName());
				// Normal Branch
				if(filterBranchCriteria.size()>0 ) {
					LoginBranchCriteriaRes getBranch = filterBranchCriteria.get(0);
					branchRes.setBranchName(getBranch.getBranchName());
					branchRes.setBrokerBranchCode(data.getBrokerBranchCode());
					branchRes.setDivisionCode(getBranch.getDivisionCode());
					branchRes.setBrokerBranchName(data.getBrokerBranchName());
					branchRes.setRegionCode(getBranch.getRegionCode() );
				//	branchRes.setRegionName(getBranch.getRegionName() );
					branchRes.setInsuranceId(getBranch.getCompanyId() );
					branchRes.setCompanyName(getBranch.getCompanyName() );
					branchRes.setCompanyLogo(getBranch.getCompanyLogo() );
					branchRes.setCurrencyId(getBranch.getCurrencyId() );;
					branchRes.setSourceType(data.getSourceType());
					branchRes.setDepartmentCode(data.getDepartmentCode());
//					branchRes.setCustomerCode(data.getCustomerCode());
//					branchRes.setCustomerName(data.getCustomerName());
					branchRes.setBrokerBranchNameLocal(data.getBrokerBranchNameLocal());
					branchRes.setBelongingBranch(getBranch.getBelongingBranch());
					branchRes.setOriginationCountryId(getBranch.getOriginationCountryId()==null?"":getBranch.getOriginationCountryId().toString());
					branchRes.setDestinationCountryId(getBranch.getDestinationCountryId()==null?"":getBranch.getDestinationCountryId().toString());
					
				}
				
				// Attached Branch
				if(! data.getBranchCode().equalsIgnoreCase(data.getAttachedBranch())  ) {
					List<LoginBranchCriteriaRes>  filterAttachedBranch = loginCriteriaRes.stream().filter( o ->  o.getBranchCode().equalsIgnoreCase(data.getBranchCode()) ).collect(Collectors.toList());
					branchRes.setAttachedBranchCode(data.getAttachedBranch());
					if(filterAttachedBranch.size()>0 ) {
						LoginBranchCriteriaRes getAttachedBranch = filterAttachedBranch.get(0);
						branchRes.setAttachedBranchName(getAttachedBranch.getBranchName()  );
						branchRes.setAttachedRegionCode(getAttachedBranch.getRegionCode() );
						branchRes.setDivisionCode(getAttachedBranch.getDivisionCode());
				//		branchRes.setAttachedRegionName(getAttachedBranch.getRegionName() );
						branchRes.setAttachedCompanyId(getAttachedBranch.getCompanyId() );
						branchRes.setAttachedCompanyName(getAttachedBranch.getCompanyName() );
				//		branchRes.setAttachedCompanyLogo(getAttachedBranch.getCompanyLogo() );
						branchRes.setCurrencyId(getAttachedBranch.getCurrencyId() );
						branchRes.setSourceType(data.getSourceType());
						branchRes.setDepartmentCode(data.getDepartmentCode());
//						branchRes.setCustomerCode(data.getCustomerCode());
//						branchRes.setCustomerName(data.getCustomerName());
						branchRes.setBelongingBranch(getAttachedBranch.getBelongingBranch());
						branchRes.setOriginationCountryId(getAttachedBranch.getOriginationCountryId()==null?"":getAttachedBranch.getOriginationCountryId().toString());
						branchRes.setDestinationCountryId(getAttachedBranch.getDestinationCountryId()==null?"":getAttachedBranch.getDestinationCountryId().toString());
					}
				}
				loginBranchRes.add(branchRes);
			}
			
			
			r.setLoginBranchDetails(loginBranchRes);

			List<String> companyIds = new ArrayList<>(removeDuplicateCompany);
			// Products

//			List<LoginProductMaster> loginproduct = new ArrayList<LoginProductMaster>();//loginProductRepo.findByLoginId(login.getLoginId());
//			{
//				Date today  = new Date();
//				Calendar cal = new GregorianCalendar(); 
//				cal.setTime(today);
//				cal.set(Calendar.HOUR_OF_DAY, 23);
//				cal.set(Calendar.MINUTE, 1);
//				today   = cal.getTime();
//				cal.setTime(today);
//				cal.set(Calendar.HOUR_OF_DAY, 1);
//				cal.set(Calendar.MINUTE, 1);
//				Date todayEnd   = cal.getTime();
//				
//				// Criteria
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
//			
//				// Find All
//				Root<LoginProductMaster>    c = query.from(LoginProductMaster.class);		
//				
//				// Select
//				query.select(c );
//				
//			
//				// Order By
//				List<Order> orderList = new ArrayList<Order>();
//				orderList.add(cb.asc(c.get("productName")));
//				
//				
//				
//				// Effective Date Max Filter
//				Subquery<Date> effectiveDate = query.subquery(Date.class);
//				Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
//				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//				Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId") );
//				Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
//				Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//				Predicate a4 = cb.equal(c.get("loginId"),ocpm1.get("loginId") );
//				effectiveDate.where(a1,a2,a3,a4);
//				
//				// Effective Date Max Filter
//				Subquery<Date> effectiveDate2 = query.subquery(Date.class);
//				Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
//				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
//				Predicate a5 = cb.equal(c.get("productId"),ocpm2.get("productId") );
//				Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
//				Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//				Predicate a8 = cb.equal(c.get("loginId"),ocpm2.get("loginId") );
//				effectiveDate2.where(a5,a6,a7,a8);
//				
//				// Filer Product IDs
//				Subquery<Long> productIds = query.subquery(Long.class);
//				Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);
//				
//				
//				Subquery<Date> effectiveDate3 = query.subquery(Date.class);
//				Root<CompanyProductMaster> ocpm4 = effectiveDate3.from(CompanyProductMaster.class);
//				effectiveDate3.select(cb.greatest(ocpm4.get("effectiveDateStart").as(Date.class)));
//				Predicate a9 = cb.equal(cm.get("productId"),ocpm4.get("productId") );
//				Predicate a10 = cb.equal(cm.get("companyId"),ocpm4.get("companyId") );
//				Predicate a11 = cb.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today);
//				effectiveDate3.where(a9,a10,a11);
//				
//				Subquery<Date> effectiveDate4 = query.subquery(Date.class);
//				Root<CompanyProductMaster> ocpm5 = effectiveDate4.from(CompanyProductMaster.class);
//				effectiveDate4.select(cb.greatest(ocpm5.get("effectiveDateEnd").as(Date.class)));
//				Predicate a12 = cb.equal(cm.get("productId"),ocpm5.get("productId") );
//				Predicate a13 = cb.equal(cm.get("companyId"),ocpm5.get("companyId") );
//				Predicate a14 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
//				effectiveDate4.where(a12,a13,a14);
//				
//				
//				productIds.select(cm.get("productId"));
//				Predicate a15 = cb.equal(cm.get("companyId"),companyIds.get(0));
//				Predicate a16 = cb.equal(cm.get("status"),"Y" );
//				Predicate a17 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
//				Predicate a18 = cb.equal(cm.get("effectiveDateEnd"), effectiveDate4);
//				productIds.where(a15,a16,a17,a18);
//				
//				//In 
//				Expression<String>e0=c.get("productId");
//				
//			    // Where	
//				Predicate n1 = cb.equal(c.get("status"), "Y");
//				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
//				Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
//				Predicate n4 = cb.equal(c.get("companyId"), companyIds.get(0));
//				Predicate n5 = cb.equal(c.get("loginId"), login.getLoginId());
//				Predicate n6 = e0.in(productIds);
//				query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
//				
//				// Get Result
//				TypedQuery<LoginProductMaster> result = em.createQuery(query);			
//				loginproduct =  result.getResultList(); 
//			}
			
			List<LoginProductMaster> loginproduct = new ArrayList<LoginProductMaster>();
			{
			    Date today = new Date();
			    Calendar cal = new GregorianCalendar();
			    cal.setTime(today);
			    cal.set(Calendar.HOUR_OF_DAY, 0);
			    cal.set(Calendar.MINUTE, 0);
			    cal.set(Calendar.SECOND, 0);
			    cal.set(Calendar.MILLISECOND, 0);
			    Date todayStart = cal.getTime(); // Start of today (for >= end date check)

			    // CriteriaBuilder setup
			    CriteriaBuilder cb = em.getCriteriaBuilder();
			    CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
			    Root<LoginProductMaster> c = query.from(LoginProductMaster.class);
			    query.select(c);

			    // Order By
			    List<Order> orderList = new ArrayList<Order>();
			    orderList.add(cb.asc(c.get("productName")));

			    // -------------------------------------------------------
			    // FIX: Single subquery - MAX(effectiveDateStart)
			    // WHERE start <= NOW AND end >= NOW (both conditions together)
			    // -------------------------------------------------------
			    Subquery<Date> effectiveDateSub = query.subquery(Date.class);
			    Root<LoginProductMaster> subRoot = effectiveDateSub.from(LoginProductMaster.class);
			    effectiveDateSub.select(cb.greatest(subRoot.get("effectiveDateStart").as(Date.class)));

			    Predicate s1 = cb.equal(c.get("productId"),  subRoot.get("productId"));
			    Predicate s2 = cb.equal(c.get("companyId"),  subRoot.get("companyId"));
			    Predicate s3 = cb.equal(c.get("loginId"),    subRoot.get("loginId"));
			    Predicate s4 = cb.lessThanOrEqualTo(subRoot.get("effectiveDateStart"), new Date()); // start <= NOW
			    Predicate s5 = cb.greaterThanOrEqualTo(subRoot.get("effectiveDateEnd"), todayStart); // end >= TODAY
			    effectiveDateSub.where(s1, s2, s3, s4, s5);

			    
			    Subquery<Long> productIds = query.subquery(Long.class);
			    Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);

			    // MAX effectiveDateStart for CompanyProductMaster (start <= NOW AND end >= TODAY)
			    Subquery<Date> compEffStart = query.subquery(Date.class);
			    Root<CompanyProductMaster> cpmSub1 = compEffStart.from(CompanyProductMaster.class);
			    compEffStart.select(cb.greatest(cpmSub1.get("effectiveDateStart").as(Date.class)));
			    Predicate c1 = cb.equal(cm.get("productId"), cpmSub1.get("productId"));
			    Predicate c2 = cb.equal(cm.get("companyId"), cpmSub1.get("companyId"));
			    Predicate c3 = cb.lessThanOrEqualTo(cpmSub1.get("effectiveDateStart"), new Date());
			    Predicate c4 = cb.greaterThanOrEqualTo(cpmSub1.get("effectiveDateEnd"), todayStart);
			    compEffStart.where(c1, c2, c3, c4);

			    productIds.select(cm.get("productId"));
			    Predicate p1 = cb.equal(cm.get("companyId"), companyIds.get(0));
			    Predicate p2 = cb.equal(cm.get("status"), "Y");
			    Predicate p3 = cb.equal(cm.get("effectiveDateStart"), compEffStart);
			    Predicate p4 = cb.greaterThanOrEqualTo(cm.<Date>get("effectiveDateEnd"), todayStart); // end >= TODAY
			    productIds.where(p1, p2, p3, p4);

			    // -------------------------------------------------------
			    // Main WHERE clause
			    // -------------------------------------------------------
			    Expression<String> e0 = c.get("productId");

			    Predicate n1 = cb.equal(c.get("status"), "Y");
			    Predicate n2 = cb.equal(c.get("companyId"), companyIds.get(0));
			    Predicate n3 = cb.equal(c.get("loginId"), login.getLoginId());
			    Predicate n4 = cb.lessThanOrEqualTo(c.<Date>get("effectiveDateStart"), new Date());     // start <= NOW
			    Predicate n5 = cb.greaterThanOrEqualTo(c.<Date>get("effectiveDateEnd"), todayStart);    // end >= TODAY
			    Predicate n6 = cb.equal(c.get("effectiveDateStart"), effectiveDateSub);                 // = MAX start
			    Predicate n7 = e0.in(productIds);

			    query.where(n1, n2, n3, n4, n5, n6, n7).orderBy(orderList);

			    // Execute
			    TypedQuery<LoginProductMaster> result = em.createQuery(query);
			    loginproduct = result.getResultList();
			}
			
			Integer productId;
			List<ProductDropDownRes> resList = new ArrayList<ProductDropDownRes>();
			
			for (LoginProductMaster products : loginproduct) {
				productId = products.getProductId();

				List<CompanyProductMaster> product = getCompanyProductMaster(products.getCompanyId()   , productId  );
				ProductDropDownRes res = new ProductDropDownRes();
				res.setOldProductName(products.getProductName());
				res.setNewProductName(product.get(0).getProductName());
				res.setProductIconId(product.get(0).getProductIconId().toString());
				res.setProductIconName(product.get(0).getProductIconName());
				res.setProductId(productId.toString());
				res.setPackageYn(product.get(0).getPackageYn());
				res.setDisplayOrder(product.get(0).getDisplayOrder()==null?999:product.get(0).getDisplayOrder());
				res.setNewProductNameLocal((product != null && product.size() > 0 ) ? product.get(0).getProductNameLocal() : "");
				res.setClaimExperienceYn(product.get(0).getClaimExperienceYn());
				res.setCoInsuranceYn(product.get(0).getCoInsuranceYn());
				res.setDynamicYN(StringUtils.isNotBlank(product.get(0).getDynamicYN()) ? product.get(0).getDynamicYN() : "N" );
				resList.add(res);
			}
			resList.sort(Comparator.comparing(ProductDropDownRes::getDisplayOrder));

			if (companyIds.get(0).equals("100002") || companyIds.get(0).equals("100019")) {

			    List<LobProductRes> lobProductResList = new ArrayList<>();
			    try {
			        List<EwayLobMaster> lobMappings = ewayLineOfBusinessRepository.findByCompnayId(companyIds.get(0));

			        for (EwayLobMaster lob : lobMappings) {
			            if (lob.getProductId() == null || lob.getProductId().trim().isEmpty()) {
			                continue;
			            }

			            String[] lobProductIds = lob.getProductId().split(",");
			            Set<String> lobProductIdSet = new HashSet<>();
			            for (String pid : lobProductIds) {
			                lobProductIdSet.add(pid.trim());
			            }

			            List<ProductDropDownRes> matchedProducts = resList.stream()
			                .filter(p -> lobProductIdSet.contains(p.getProductId()))
			                .collect(Collectors.toList());

			            if (!matchedProducts.isEmpty()) {
			                LobProductRes lobRes = new LobProductRes();
			                lobRes.setLobId(lob.getLobId());
			                lobRes.setLobCode(lob.getLobCode());
			                lobRes.setLobName(lob.getLobName());
			                lobRes.setLobImage(lob.getLobName().replaceAll("\\s+", "") + ".png");
			                lobRes.setProductList(matchedProducts);
			                lobProductResList.add(lobRes);
			            }
			        }
			    } catch (Exception e) {
			        log.info("Exception in LOB grouping ---> " + e.getMessage());
			        e.printStackTrace();
			    }

			    r.setLobCompanyProducts(lobProductResList); 

			} else {
			    r.setCompanyProducts(resList); 
			}

			

			// Menu Ids
		  if(login.getMenuIds()!=null && login.getMenuIds().indexOf(",")!=-1) {
			  String[] split = login.getMenuIds().split(",");
			  List<String> asList = Arrays.asList(split);
			//  r.setMenuList(getMenuList( asList));
		  }				
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return r;
		
	}
	
	public List<String> convertModuleIds(String moduleIds) {
		try {
	    if (moduleIds == null || moduleIds.trim().isEmpty()) {
	        return Collections.emptyList();
	    }

	    return Arrays.stream(moduleIds.split(","))
	                 .map(String::trim)
	                 .collect(Collectors.toList());
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is convertModuleIds()---> " + e.getMessage());
			 return Collections.emptyList();
		}
	}
	
	public List<CompanyProductMaster> getCompanyProductMaster(String companyId , Integer productId ) {
		List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query=  cb.createQuery(CompanyProductMaster.class);
		
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4,a5,a6);
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),companyId);
			Predicate n7 = cb.equal(c.get("productId"),productId);
			Predicate n5 = cb.equal(c.get("status"),"R");
			Predicate n6 = cb.or(n1,n5);
			query.where(n6,n2,n3,n4,n7).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();			
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return list;
		}

	public List<BrokerProductCompaniesRes> getBrokerProducts(String loginId , List<String> companyIds  ) {
		List<BrokerProductCompaniesRes> companyList = new ArrayList<BrokerProductCompaniesRes>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime() ;
			
			List<LoginProductCriteriaRes> loginProducts = null ;// loginProductsService.getBrokerProductDetails(loginId , companyIds , today ) ;
				
			// Grouping
			Map<String ,List<LoginProductCriteriaRes>> groupByCompany = loginProducts.stream().collect(Collectors.groupingBy(LoginProductCriteriaRes :: getCompanyId )) ;
			for (String company : groupByCompany.keySet()) { 
				BrokerProductCompaniesRes companyRes = new BrokerProductCompaniesRes();
				List<BrokerProductsGetRes> attachedProducts = new ArrayList<BrokerProductsGetRes>();
				
				List<LoginProductCriteriaRes> filterProduct = groupByCompany.get(company);
				
				for(LoginProductCriteriaRes data :  filterProduct) {
					BrokerProductsGetRes productRes = new BrokerProductsGetRes();
					
					if(StringUtils.isNotBlank(data.getStatus()) && data.getStatus().equalsIgnoreCase("Y")  ) {
						String pattern = "#####0.00";
						DecimalFormat df = new DecimalFormat(pattern);
						productRes.setOldProductName(data.getOldProductName() );
						productRes.setSumInsuredStart(data.getSumInsuredStart()==null?"" : df.format(data.getSumInsuredStart()) );
						productRes.setSumInsuredEnd(data.getSumInsuredEnd()==null?"" :df.format(data.getSumInsuredEnd()) );
						productRes.setStatus(data.getStatus());
						productRes.setRemarks(data.getRemarks());

						productRes.setProductId(data.getProductId().toString());
						productRes.setProductName(data.getProductName());
						attachedProducts.add(productRes);
					}
				}
				
				// Response 
				companyRes.setInsuranceId(filterProduct.get(0).getCompanyId() );
				companyRes.setCompanyName(filterProduct.get(0).getCompanyName() );
				companyRes.setAttachedProducts(attachedProducts);
				companyList.add(companyRes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return companyList;
	}
	
	private List<LoginBranchCriteriaRes> getBranchDetails(Set<String> removeDuplicateBranch , String companyId) {
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
//			
//			// Company Logo Effective Date Max Filter
			Subquery<Long> companyLogo = query.subquery(Long.class);
		Root<InsuranceCompanyMaster> logo = companyLogo.from(InsuranceCompanyMaster.class);
			Subquery<Date> effectiveDate5 = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm5 = effectiveDate5.from(InsuranceCompanyMaster.class);
			effectiveDate5.select(cb.greatest(ocpm5.<Date>get("effectiveDateStart")));
			Predicate iceff1 = cb.equal(ocpm5.get("companyId"), logo.get("companyId"));
			Predicate iceff2 = cb.lessThanOrEqualTo(ocpm5.get("effectiveDateStart"), today);
			effectiveDate5.where(iceff1,iceff2);
			
			// Company Logo
			companyLogo.select(logo.get("companyLogo"));
			Predicate in1 = cb.equal(logo.get("companyId"), b.get("companyId"));
			Predicate in2 = cb.equal(logo.get("effectiveDateStart"), effectiveDate5);
			companyLogo.where(in1,in2);
			
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
			
						
			
//			// Region Effective Date Filter
//			Subquery<Long> region = query.subquery(Long.class);
//			Root<RegionMaster> rm = region.from(RegionMaster.class);
//			Subquery<Long> effectiveDate3 = query.subquery(Long.class);
//			Root<RegionMaster> ocpm3 = effectiveDate3.from(RegionMaster.class);
//			effectiveDate3.select(cb.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
//			Predicate reff2 = cb.equal(ocpm3.get("regionCode"), rm.get("regionCode"));
//			Predicate reff3 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
//			effectiveDate3.where(reff2,reff3);
//			
//			//Region Name
//			region.select(rm.get("regionName"));
//			Predicate rm2 = cb.equal(rm.get("regionCode"),  b.get("regionCode") );
//			Predicate rm3 = cb.equal(rm.get("effectiveDateStart"), effectiveDate3);
//			region.where(rm2,rm3);
//			
			// Select
			query.multiselect(b.get("branchCode").alias("branchCode"),b.get("belongingBranch").alias("belongingBranch") , b.get("regionCode").alias("regionCode") ,
					b.get("companyId").alias("companyId") , b.get("branchName").alias("branchName") ,b.get("coreAppCode").alias("divisionCode"),
					company.alias("companyName") ,b.get("originationCountryId").alias("originationCountryId"),b.get("destinationCountryId").alias("destinationCountryId"),
					//region.alias("regionName") , 
					companyLogo.alias("companyLogo") ,
					currency.alias("currencyId"));
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate eff1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
		//	Predicate eff2 = cb.equal(ocpm1.get("regionCode"), b.get("regionCode"));
			Predicate eff3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate eff4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(eff1,  eff3, eff4 );
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			//In 
			Expression<String>e0=b.get("branchCode");
			
			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(removeDuplicateBranch) ;
			Predicate n4 =cb.equal(b.get("companyId"), companyId);
			query.where(n1, n2, n3,n4).orderBy(orderList);

			// Get Result
			TypedQuery<LoginBranchCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return list;
		
	}
	
	@SuppressWarnings("static-access")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LoginMaster userList = new LoginMaster ();
		try {
			log.info("loadUserByUsername==>" + username);
			
			String[] split = username.split(":");
			
			LoginMasterId id = new LoginMasterId();
			id.setLoginId(split[0]);
			
			LoginMaster  userListopt = loginRepo.findByLoginId(split[0]);
			 if(userListopt!=null) {
				 userList = userListopt;
			 }
			if (userList!=null) {
				//user = userList.get(0);
				String pass = bCryptPasswordEncoder.encode(endecryService.decrypt("zQYgCo7GMZeX1tBQyzAi8Q=="));
				userList.setPassword(pass);
				Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
				grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
				log.info("loadUserByTokenEncrypt==>" + userList.getPassword());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new org.springframework.security.core.userdetails.User(userList.getLoginId(), userList.getPassword(),getAuthority());
	}
	
	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
	
	
	// Change Passowrd
	@Override
	public String LoginChangePassword(ChangePasswordReq req) {
		String res = new String();
		try {
		passwordEnc passEnc = new passwordEnc();
		String epass = passEnc.crypt(req.getOldpassword().trim());
		String newpass = passEnc.crypt(req.getNewPassword().trim());
		LoginMaster master = new LoginMaster();  
		log.info("EncryptPassword-->" + epass);
		LoginMaster model = loginRepo.findByLoginId(req.getLoginId());
		if(model !=null ) {
			master = model ;
			
			String pass1 = master.getPassword();
			String pass2 = master.getLpass1();
			String pass3 = master.getLpass2();
			String pass4 = master.getLpass4();
			String pass5 = master.getLpass5();
			
			master.setLpass1(pass1);
			master.setLpass2(pass2);
			master.setLpass3(pass3);
			master.setLpass4(pass4);
			master.setLpass5(pass5);
			master.setPassword(newpass);
			Integer pwdCount =  Integer.valueOf(master.getPwdCount())+1 ;
			master.setPwdCount(String.valueOf(pwdCount) );
			
			Instant now = Instant.now();
			Instant after = now.plus(Duration.ofDays(45));
			Date dateAfter = Date.from(after);
			master.setLpassDate(dateAfter);
			LoginMaster table = loginRepo.save(master);
			
			if(table!=null) {
				res  = "Password Changed Successfully";
				crmService.updatePassword(req, changePassword);
			}
			else {
				res  = "FAILED" ;
				
			}
		}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error-->" + e.getMessage());
		}
		return res;

	}

	@Override
	public CommonLoginRes logout(LogoutRequest mslogin) {
		CommonLoginRes res = new CommonLoginRes();
		ClaimLogoutResponse r = new ClaimLogoutResponse();
		try {
		
			LoginMaster login = loginRepo.findByLoginId(mslogin.getUserId());
			if (login!=null) {

				SessionMaster session = sessionRep.findByTempTokenid(mslogin.getToken());
				session.setLogoutDate(new Date());
				session.setStatus("DE-ACTIVE");
				session = sessionRep.save(session);
				r.setStatus("Log Out Sucessfully");
			}else {
				r.setStatus("Log Out Failed");
			}
		} catch (Exception e) {
			r.setStatus("Log Out Failed");
			e.printStackTrace();
		}
		res.setCommonResponse(r);
		res.setErrorMessage(Collections.emptyList());
		res.setIsError(false);
		res.setMessage("Success");
		return res;
	}

	@Override
	public SuccessRes LoginForgetPassword(ForgetPasswordReq req) {

	    SuccessRes res = new SuccessRes();

	    try {

	        sendUserPwd(req, "tempPwd");

	    } catch (Exception e) {
	        log.error("Forgot Password Error", e);
	    }

	    res.setResponse("If the account exists, a temporary password has been sent.");

	    return res;
	}

	
	public String sendUserPwd( ForgetPasswordReq req, String temp) {
		String msg = "Temporary Password Notification Sent " ;
		try {
			String tempPassword = getTempPassword(req.getLoginId() );
			
			LoginMaster ld = loginRepo.findByLoginId(req.getLoginId()  );
			
			InsuranceCompanyMaster cm = getInscompanyMaster(ld.getCompanyId());
			
			LoginUserInfo lu = userInfoRepo.findByLoginId(req.getLoginId()  );
			
			// Send Mail &^ sms
			sentDirectMail(req.getLoginId()  , tempPassword ,  ld ,  lu , cm  ) ;
			// Send Sms
			//sentDirectSms(req.getLoginId()  , tempPassword ,  ld ,  lu , cm  ) ;
				 
				 
			
		} catch (Exception e) {
			log.info(e);
			return null ;	
		}
		return msg;
	}
	
	@Autowired
	private NotificationService notificationService;
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	
	
	public CommonRes sentDirectMail(String loginId , String tempPassword , LoginMaster ld , LoginUserInfo lu , InsuranceCompanyMaster cm  ) {
		CommonRes res = new CommonRes();
		SuccessRes response = new SuccessRes();
		try {
			
			
			/*List<NotifTemplateMaster> templat = masterRepo.findByCompanyIdAndProductIdAndStatusAndNotifTemplatenameIgnoreCaseOrderByAmendIdDesc(
					ld.getCompanyId(),99999L,"Y","TEMP_PASSWORD");
			List<MailMaster> mailc = mailRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(ld.getCompanyId(),"99999","Y");													
			List<SmsConfigMaster> smsc = smsRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(ld.getCompanyId(),"99999","Y");													
 			PushedStateChange p=new PushedStateChange(templat.get(0),mailc.get(0),smsc.get(0));*/
 			Calendar calend = Calendar.getInstance();
			calend.setTime(new Date()); 
			calend.add(Calendar.DATE, 1); 
			/*String name = lu.getUserName();
			String companylogo= cm.getCompanyLogo() ;
			String companyAddress= cm.getCompanyAddress() ;
			String mailBody= "Your Temporary Password Is : <p style=color:red;> " + tempPassword + " </p>";
			String mailSubject= "Temporary Password";
			String mailRegards= cm.getRegards() ;
			*/
			
 			String tinyGroupId=String.valueOf(Instant.now().getEpochSecond());
			NotifTransactionDetails nt = NotifTransactionDetails.builder()
					.brokerCompanyName(lu.getUserName())
					.brokerMailId(lu.getUserMail())					
					.companyName(cm.getCompanyName())
					.customerMailid(lu.getUserMail())					
					.customerName(lu.getUserName())
					.entryDate(new Date())
					.notifcationPushDate(new Date())
					.notifcationEndDate(calend.getTime())
					.notifDescription(tempPassword)
					.notifNo(Instant.now().toEpochMilli())
					//.notifNo(null)
					.notifPriority(1)
					.notifPushedStatus("P")
					.notifTemplatename("TEMP_PASSWORD")											
					.productName("Common")					
					//.tinyUrl(n.getTinyUrl())
					.companyid(ld.getCompanyId())
					.productid(99999)
					.companyLogo(cm.getCompanyLogo())
					.companyAddress(cm.getCompanyAddress())											
					.tinyUrlActive("N")
					.tinyGroupId(tinyGroupId)
					.build();
			NotifTransactionDetails sv = notifTrans.save(nt);
			List<NotifTransactionDetails> text=new LinkedList<NotifTransactionDetails>();
			text.add(sv);
			notificationService.jobProcess(text);
			
		 	response.setResponse("Temporary Password Sent Successfully");	
			response.setSuccessId(lu.getUserMail());
			res.setCommonResponse(response);
			res.setIsError(false);
		
			 
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			return res ;
		}
		return res;
	}
	/*
	public CommonRes sentDirectSms(String loginId , String tempPassword , LoginMaster ld , LoginUserInfo lu , InsuranceCompanyMaster cm  ) {
		CommonRes res = new CommonRes();
		SuccessRes response = new SuccessRes();
		try {
			String smsBody= "Your Temporary Password Is : " + tempPassword ;
			String smsSubject= "Temporary Password";
			String smsRegards= cm.getRegards() ;
			
			SmsConfigMaster smsc = smsRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(ld.getCompanyId(),"99999","Y").get(0);													
			
			Sms m=Sms.builder()
					.smsBody(smsBody)
					.smsRegards(smsRegards)
					.smsSubject(smsSubject)
					.smsTo(lu.getUserMobile() )	
					.smsFrom(smsc.getSenderId())
					.credential(JobCredentials.builder().host(smsc.getSmsPartyUrl()).isSSL(true).password(smsc.getSmsUserPass()).username(smsc.getSmsUserName()).build())
					.smsToCode(lu.getMobileCodeDesc())
					.notifNo(0)
					.build();
			
			// Save Sms Data Details
			SmsDataDetails savedata = new SmsDataDetails();

			Long sno = smsDataRepo.count();
			sno=sno+1;
			savedata.setMobileNo(m.getSmsTo());
			savedata.setSmsFrom(m.getSmsFrom());		
			savedata.setSmsType(smsSubject);
			savedata.setSmsContent(smsBody);
			savedata.setEntryDate(new Date());
			savedata.setSNo(sno.toString());
			savedata.setResMessage("Pending");
			savedata.setResStatus("P");
			savedata.setReqTime(new Date());
			savedata.setResTime(new Date());
			savedata.setNotifNo(m.getNotifNo());
			savedata.setPushedBy("None");
			savedata.setSmsRegards(smsRegards);
			smsDataRepo.saveAndFlush(savedata);
			
			ExecutorService service = Executors.newFixedThreadPool(4);
		    service.submit(new Runnable() {
		        public void run() {
		        	pushSms(m , savedata);
		        }
		    });
			
			
			response.setResponse("Sms Sent Successfully");	
			response.setSuccessId(sno.toString());
			res.setCommonResponse(response);
			res.setIsError(false);
			
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			return res ;
		}
		return res;
	}*/
	/*
	public String pushMail(Mail m , MailDataDetails mdd) {
		   
		String statusResponse=null;
		try {
			Properties prop = new Properties();
			prop.put("mail.smtp.host", m.getCredential().getHost());
			prop.put("mail.smtp.port", m.getCredential().getPort());
			if(m.getCredential().getIsSSL()) {
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.starttls.enable", "true"); // TLS
			}else {
				prop.put("mail.smtp.auth", "false");
				prop.put("mail.smtp.starttls.enable", "false"); // TLS
			}
			
			Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(m.getCredential().getUsername(), m.getCredential().getPassword());
				}
			});
			MimeMessage mimeMessage = new MimeMessage(session);

			mimeMessage.setFrom(new InternetAddress(m.getCredential().getUsername()));
			
			InternetAddress	to = new InternetAddress(m.getMailTo());
			mimeMessage.addRecipient(Message.RecipientType.TO, to);
			// Mail Cc
			InternetAddress[] addressCc=null;
			if (m.getMailcc() != null && m.getMailcc().size()>0 ) {
				 addressCc = new InternetAddress[m.getMailcc().size()];
				for (int i = 0; i < m.getMailcc().size(); i++) {
					if (StringUtils.isNotBlank( m.getMailcc().get(i))) {
						addressCc[i] = new InternetAddress( m.getMailcc().get(i)); 
						mimeMessage.addRecipient(Message.RecipientType.CC, addressCc[i]); 
					}
				} 
			}
			 
			mimeMessage.setSubject(m.getMailSubject());
			mimeMessage.setContent(m.getMailBody(), "text/html");
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setSubject(m.getMailSubject());
			helper.setText(m.getMailBody(), true);
//			if(m.getAttachments()!=null && StringUtils.isNotBlank(m.getAttachments())) {
//				for (String attachPath : m.getAttachments().split(";")) {
//					File file=loadFilesFromPath(attachPath);
//					if (file != null && file.exists())
//						helper.addAttachment(file.getName(), file);
//				}
//			}
			
			Transport.send(mimeMessage);
			
		}catch (Exception e) {
			e.printStackTrace();
			statusResponse=e.getLocalizedMessage();
			return statusResponse ;
		}
		
		statusResponse = "Success" ;
		mdd.setMailResponse("Success");
		mdd.setStatus("C");
		mailDataRepo.save(mdd);
		return statusResponse ;
		 
	}*/
	/*
	public String pushSms(Sms m , SmsDataDetails savedata) {

		String statusResponse = null;
		String type="0";	
		String dlr="1";
		String statuscode="";
		Integer statusvalue =0;
		try {
				
			String mobileCode="";
			RestTemplate restTemplate = new RestTemplate();
			String fooResourceUrl = m.getCredential().getHost();
			if(StringUtils.isNotBlank( m.getSmsToCode())) {
			mobileCode = m.getSmsToCode().replace("+", "");
			}
			String content="username="
					+ URLEncoder.encode(m.getCredential().getUsername(), "UTF-8") + "&password="
					+ m.getCredential().getPassword() + "&type="
					+ URLEncoder.encode(type, "UTF-8") + "&dlr="
					+ URLEncoder.encode(dlr, "UTF-8") + "&destination="
					 + URLEncoder.encode(m.getSmsBody(), "UTF-8") + "&source="
							+ URLEncoder.encode(mobileCode+m.getSmsFrom(), "UTF-8") + "&message="
							+m.getSmsBody()+m.getSmsRegards()==null?"":m.getSmsRegards();
			System.out.println("SMS request  ---> "+fooResourceUrl + "?"+content);
			
			ResponseEntity<String> response	  = restTemplate.getForEntity(fooResourceUrl + "?"+content, String.class);
			
			System.out.println("SMS Response"+response.getBody());
			statuscode =response.getStatusCode()!=null? response.getStatusCode().toString() : "";
			statusvalue = response.getStatusCodeValue() ;		
		} catch (Exception e) {
			e.printStackTrace();
			statusResponse = e.getLocalizedMessage();
			return statusResponse ;
		}

		if(statuscode.equalsIgnoreCase("200OK")) {
		savedata.setResStatus("OK");
		savedata.setResMessage("SMS Sent Successful");		
		}
		else {
			savedata.setResStatus("Not OK");
			savedata.setResMessage("SMS Sent Failed");					
		}
		savedata.setResTime(new Date());
		smsDataRepo.save(savedata);
		statusResponse = "Success" ;
		return statusResponse ;

	}
	*/
	
	private String getTempPassword(String loginId) {
		final String alphabet = "Aa2Bb@3Cc#4Dd$5Ee%6Ff7Gg&8Hh9Jj2Kk=3L4Mm5Nn@6Pp7Qq#8Rr$9Ss%2Tt3Uu&4Vv5Ww+6Xx=7Yy8Zz9";
		final int N = alphabet.length();
		String temppwd = "";
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			temppwd += alphabet.charAt(r.nextInt(N));
		}
		try {
			passwordEnc passEnc = new passwordEnc();
			String password = passEnc.crypt(temppwd.trim());
			log.info("newpwd ==>" + password + ":userId ==>" + loginId + ":Temppassword==>" + temppwd);
			//Integer count = Integer.valueOf(loginRepo.findByLoginId(loginId).getPwdCount()) + 1   ;
			LoginMaster loginData = loginRepo.findByLoginId(loginId);
			
			if (loginData !=null) {
				LoginMaster model = loginData ;
				model.setLpass5(loginData.getLpass4());
				model.setLpass4(loginData.getLpass3());
				model.setLpass3(loginData.getLpass2());
				model.setLpass2(loginData.getLpass1());
				model.setLpass1(loginData.getPassword()); 
				model.setStatus("Y");
				model.setPwdCount("0");
				Instant now = Instant.now();
				Instant after = now.plus(Duration.ofDays(1));
				Date dateAfter = Date.from(after);
				model.setLpassDate(dateAfter);
			//	String encpass = endecryService.encrypt(temppwd);
				model.setPassword(password);
				loginRepo.saveAndFlush(model);
				
			}
			
		} catch (Exception e) {
			log.info(e);
		}
		return temppwd;
	}
	
	
	private String getTemplateFrame(String name ,String companylogo ,String companyAddress, String mailBody , 	String mailSubject,	String mailRegards) {
		try {
			 String baseTemplate="<div style=\"margin: 0px auto;width: 700px;max-width: 90%;padding-top: 20px;background-color: rgb(255,255,255);\">\r\n"
			 		+ "        <div style=\"text-align: center; margin-bottom: 20px;\"> <img height=\"20px\"> </div>\r\n"
			 		+ "<div class=\"adM\" style=\"text-align: center;\"><img src=\"{xCompanyLogox}\">\r\n"
			 		+ "        \r\n"
			 		+ "        </div>"
			 		+ "        <div style=\"margin: 0px auto; width: 100%; line-height: 1.3;\"> <img width=\"100%\">\r\n"
			 		+ "          <div style=\"padding: 20px 30px;\">\r\n"
			 		+ "            <p style=\"text-transform: capitalize;\">Hi {xCustomerx},</p>\r\n"
			 		+ "            <p style=\"\r\n"
			 		+ "    font-size: 17px;\r\n"
			 		+ "\">{xsubjectx}</p>\r\n"
			 		+ "            <div style=\"border: 1px solid rgb(221, 221, 221); padding: 20px;\">\r\n"
			 		+ "              {xmailBodyx}"
			 		+ "                \r\n"
			 		+ "            </div>\r\n"
			 		+ "            <div>\r\n"
			 		+ "              <p style=\"font-weight: bold;\">Why {companyName}?</p>\r\n"
			 		+ "              <ul>\r\n"
			 		+ "                <li>Sample Text -1.</li>\r\n"
			 		+ "                <li>Sample Text -2.</li>\r\n"
			 		+ "                <li>Sample Text -3.</li>\r\n"
			 		+ "              </ul>\r\n"
			 		+ "              <p style=\"font-weight: bold;\">What’s next?</p>\r\n"
			 		+ "              <p>Paragraph Text</p>\r\n"
			 		+ "              <ul>\r\n"
			 		+ "                <li>Sample Text</li>\r\n"
			 		+ "                <li>Sample Text</li>\r\n"
			 		+ "              </ul>\r\n"
			 		+ "              <p style=\"font-size: 1.3em; text-align: center; font-weight: bold;\">That's all, it’s that simple.</p>\r\n"
			 		+ "            </div>\r\n"
			 		+ "             \r\n"
			 		+ "             \r\n"
			 		+ "            <p style=\"margin-top: 30px;\">We care,</p>\r\n"
			 		+ "            <p>{xregardsx} Team</p>\r\n"
			 		+ "            <div style=\"font-size: 0.8em; color: rgba(0, 0, 0, 0.4); margin-top: 30px;\">\r\n"
			 		+ "              <p>Your premium may need to be adjusted if the provided\r\n"
			 		+ "                information is incorrect.</p>\r\n"
			 		+ "            </div>\r\n"
			 		+ "            <div style=\"color: rgba(0, 0, 0, 0.4); font-size: 0.8em; border-top: 1px solid; margin-top: 30px; line-height: 0.9em; text-align: center; padding: 30px 0px;\">\r\n"
			 		+ "              <p><a href='#'/>\r\n"
			 		+ "               {xCompanyAddressx}</p>\r\n"
			 		+ "              <div>\r\n"
			 		+ "                <p style=\"font-weight: bold; color: rgb(0, 0, 0); margin-top: 20px;\">Connect with us</p>\r\n"
			 		+ "                </div>\r\n"
			 		+ "            </div>\r\n"
			 		+ "          </div>\r\n"
			 		+ "        </div>\r\n"
			 		+ "      </div>";
				String xCustomerx=name ;
				
				
				Map<String,String> hmap=new HashMap<String,String>();
				hmap.put("xregardsx", mailRegards);
				hmap.put("xmailBodyx", mailBody);
				hmap.put("xCustomerx", xCustomerx);
				hmap.put("xsubjectx", mailSubject);
				hmap.put("xCompanyLogox", companylogo );
				hmap.put("xCompanyAddressx", companyAddress);
				
			StringBuffer b=new StringBuffer(baseTemplate);
			while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
				 String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
				 b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(hmap.get(tx)!=null?hmap.get(tx) : ""));
			} 
			return b.toString(); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public InsuranceCompanyMaster getInscompanyMaster(String companyId) {
		InsuranceCompanyMaster data = new InsuranceCompanyMaster();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InsuranceCompanyMaster> query = cb.createQuery(InsuranceCompanyMaster.class);
			
			// Find All
			Root<InsuranceCompanyMaster>    c = query.from(InsuranceCompanyMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("companyName")));
			
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			
			// Effective Date End
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			
		    // Where	
			Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n3 = cb.equal(c.get("companyId"), companyId);
	
			query.where(n1,n2,n3).orderBy(orderList);

			// Get Result
			TypedQuery<InsuranceCompanyMaster> result = em.createQuery(query);
			List<InsuranceCompanyMaster> list  = result.getResultList();
					
			data = list.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return data;
	}

	@Override
	public AuthToken2 loginTokenRegenerate(LoginRequest req, HttpServletRequest http) {
		AuthToken2 res = new AuthToken2();
		try { 
			LoginMaster user = loginRepo.findByLoginId(req.getLoginId() );
			// Deactivate Old Session
		List<SessionMaster>	sessionlist = sessionRep.findByLoginIdOrderByEntryDateDesc(req.getLoginId());
		if(  sessionlist.size()>0 ) {
			SessionMaster updatelogout = sessionlist.get(0);
				updatelogout.setLogoutDate(new Date());
				updatelogout.setStatus("DE-ACTIVE");
				sessionRep.save(updatelogout);
		}
			
		http.getSession().removeAttribute(user.getLoginId());
		String token = jwtTokenUtil.doGenerateToken(user.getLoginId());
		log.info("-----token------" + token);
		SessionMaster session = new SessionMaster();
		session.setLoginId(user.getLoginId());
		session.setTokenId(token);
		session.setStatus("ACTIVE");
		String temptoken = bCryptPasswordEncoder.encode("CommercialClaim");
		session.setTempTokenid(temptoken);
		session.setUserType(user.getUserType());
		session.setSubUserType(user.getSubUserType());
		Date today = new Date(); 
		session.setEntryDate(today);
		session.setStartTime(today);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 20);
		Date endTime = cal.getTime();
		session.setEndTime(endTime );
		session.setIpAddress(http.getLocalAddr() +":" +http.getLocalPort());
		session =sessionRep.save(session);
		
		res.setToken(temptoken);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public String getEncryptionkey(GetEncryptionkeyReq req) {
		try {
			LoginMaster data = loginRepo.findByLoginId(req.getLoginId());
			if(data!=null) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("PageType", "B2C");
				map.put("BranchCode", StringUtils.isBlank(req.getBranchCode())?"01":req.getBranchCode());
				map.put("ProductId", req.getBranchCode());
				map.put("InsuranceId", req.getInsuranceId());
				map.put("RouterLink", "/customerProducts");
				map.put("SubUserType", data.getSubUserType());
				map.put("UserType", data.getUserType());
				map.put("LoginId", data.getLoginId());
				map.put("TinyUrlId",  StringUtils.isBlank(req.getTinyUrlId())?"169467719656897":req.getTinyUrlId());
				map.put("TinyGroupId", StringUtils.isBlank(req.getTinyGroupId())?"169467719656897":req.getTinyGroupId());
				return EncryDecryService.encrypt(new Gson().toJson(map));
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<ProductDropDownRes> getProductDetailByLoginId(String loginId, String companyId) {
	    List<LoginProductMaster> loginproduct = new ArrayList<>();

	    Date today = new Date();
	    Calendar cal = new GregorianCalendar();
	    cal.setTime(today);
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 1);
	    today = cal.getTime();

	    cal.set(Calendar.HOUR_OF_DAY, 1);
	    cal.set(Calendar.MINUTE, 1);
	    Date todayEnd = cal.getTime();

	    CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
	    Root<LoginProductMaster> c = query.from(LoginProductMaster.class);
	    query.select(c);

	    List<Order> orderList = new ArrayList<>();
	    orderList.add(cb.asc(c.get("productName")));

	    // Fix: Use Date instead of Long
	    Subquery<Date> effectiveDate = query.subquery(Date.class);
	    Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
	    effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
	    effectiveDate.where(
	            cb.equal(c.get("productId"), ocpm1.get("productId")),
	            cb.equal(c.get("companyId"), ocpm1.get("companyId")),
	            cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today),
	            cb.equal(c.get("loginId"), ocpm1.get("loginId"))
	    );

	    Subquery<Date> effectiveDate2 = query.subquery(Date.class);
	    Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
	    effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
	    effectiveDate2.where(
	            cb.equal(c.get("productId"), ocpm2.get("productId")),
	            cb.equal(c.get("companyId"), ocpm2.get("companyId")),
	            cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd),
	            cb.equal(c.get("loginId"), ocpm2.get("loginId"))
	    );

	    // Product IDs
	    Subquery<Long> productIds = query.subquery(Long.class);
	    Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);

	    Subquery<Date> effectiveDate3 = query.subquery(Date.class);
	    Root<CompanyProductMaster> ocpm4 = effectiveDate3.from(CompanyProductMaster.class);
	    effectiveDate3.select(cb.greatest(ocpm4.get("effectiveDateStart").as(Date.class)));
	    effectiveDate3.where(
	            cb.equal(cm.get("productId"), ocpm4.get("productId")),
	            cb.equal(cm.get("companyId"), ocpm4.get("companyId")),
	            cb.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today)
	    );

	    Subquery<Date> effectiveDate4 = query.subquery(Date.class);
	    Root<CompanyProductMaster> ocpm5 = effectiveDate4.from(CompanyProductMaster.class);
	    effectiveDate4.select(cb.greatest(ocpm5.get("effectiveDateEnd").as(Date.class)));
	    effectiveDate4.where(
	            cb.equal(cm.get("productId"), ocpm5.get("productId")),
	            cb.equal(cm.get("companyId"), ocpm5.get("companyId")),
	            cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd)
	    );

	    productIds.select(cm.get("productId"));
	    productIds.where(
	            cb.equal(cm.get("companyId"), companyId),
	            cb.equal(cm.get("status"), "Y"),
	            cb.equal(cm.get("effectiveDateStart"), effectiveDate3),
	            cb.equal(cm.get("effectiveDateEnd"), effectiveDate4)
	    );

	    Predicate n1 = cb.equal(c.get("status"), "Y");
	    Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
	    Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
	    Predicate n4 = cb.equal(c.get("companyId"), companyId);
	    Predicate n5 = cb.equal(c.get("loginId"), loginId);
	    Predicate n6 = c.get("productId").in(productIds);

	    query.where(cb.and(n1, n2, n3, n4, n5, n6)).orderBy(orderList);

	    TypedQuery<LoginProductMaster> result = em.createQuery(query);
	    loginproduct = result.getResultList();

	    // Final Result Mapping
	    List<ProductDropDownRes> resList = new ArrayList<>();
	    for (LoginProductMaster products : loginproduct) {
	        Integer productId = products.getProductId();
	        List<CompanyProductMaster> product = getCompanyProductMaster(products.getCompanyId(), productId);

	        if (!product.isEmpty()) {
	            CompanyProductMaster prod = product.get(0);
	            ProductDropDownRes res = new ProductDropDownRes();
	            res.setOldProductName(products.getProductName());
	            res.setNewProductName(prod.getProductName());
	            res.setProductIconId(prod.getProductIconId() != null ? prod.getProductIconId().toString() : null);
	            res.setProductIconName(prod.getProductIconName());
	            res.setProductId(productId.toString());
	            res.setPackageYn(prod.getPackageYn());
	            res.setDisplayOrder(prod.getDisplayOrder() == null ? 999 : prod.getDisplayOrder());
	            resList.add(res);
	        }
	    }

	    resList.sort(Comparator.comparing(ProductDropDownRes::getDisplayOrder));
	    return resList;
	}

}


