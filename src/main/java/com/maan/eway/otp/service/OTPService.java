package com.maan.eway.otp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.LoginBranchesSaveReq;
import com.maan.eway.admin.req.UserCompanyProductGetReq;
import com.maan.eway.admin.req.UserCreationReq;
import com.maan.eway.admin.req.UserLoginReq;
import com.maan.eway.admin.req.UserPersonalInfoReq;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginBranchService;
import com.maan.eway.admin.service.LoginDetailsService;
import com.maan.eway.admin.service.LoginProductService;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.OtpDataDetail;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.otp.dto.MPesaNotifDto;
import com.maan.eway.otp.dto.OtpConfirm;
import com.maan.eway.otp.dto.UserOtp;
import com.maan.eway.otp.dto.ValidateOtp;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.OtpDataDetailRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OTPService {

	@Autowired
	private OtpDataDetailRepository otpDataRepo;
	@Autowired
	private NotificationService notiService;

	@Autowired
	private LoginUserInfoRepository loginUserRepo;

	@Autowired
	private LoginMasterRepository loginMasterRepo;

	@Autowired
	private AuthendicationService authservice;

	public OtpConfirm generate(UserOtp otp) {
		try {
			int newOtp = generateOTP();
			long otpId = Instant.now().getEpochSecond();
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.MINUTE, 2);

			OtpDataDetail odd = OtpDataDetail.builder().companyId(otp.getCompanyId())
					.emailId(StringUtils.isBlank(otp.getUser().getMailId()) ? null : otp.getUser().getMailId())
					.mobileCode(otp.getUser().getMobileCode()).mobileNo(otp.getUser().getMobileNo())
					.whatsappCode(otp.getUser().getWhatsappCode()).whatsappNo(otp.getUser().getWhatsappNo())
					.otp(String.valueOf(newOtp)).otpId(new BigDecimal(otpId)).loginId(otp.getLoginId())
					.expiryDate(instance.getTime()).sNo(new BigDecimal(otpId))
					.productId(new BigDecimal(otp.getProductId())).build();
			OtpDataDetail save = otpDataRepo.save(odd);
			new Thread(new Runnable() {

				@Override
				public void run() {
					motorPushNotification(save, "B2C OTP", "");

				}
			}).start();
			OtpConfirm c = OtpConfirm.builder().isError(false).otpToken(otpId).otp(String.valueOf(newOtp)).build();
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResponseEntity<?> sendNotification(MPesaNotifDto mPesaNotifDto) {
		Map<String, String> result = new HashMap<>();
		try {
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.MINUTE, 2);

			OtpDataDetail odd = OtpDataDetail.builder().companyId(mPesaNotifDto.getCompanyId())
					.emailId(StringUtils.isBlank(mPesaNotifDto.getEmailId()) ? null : mPesaNotifDto.getEmailId())
					.mobileCode(mPesaNotifDto.getPhoneCode()).mobileNo(mPesaNotifDto.getPhoneNumber()).whatsappCode("")
					.whatsappNo("").otp("").otpId(BigDecimal.valueOf(0)).loginId("").expiryDate(instance.getTime())
					.sNo(new BigDecimal(0)).productId(new BigDecimal(mPesaNotifDto.getProductId())).build();

			new Thread(new Runnable() {

				@Override
				public void run() {
					motorPushNotification(odd, mPesaNotifDto.getTemplateName(), mPesaNotifDto.getDescription());
				}
			}).start();
			result.put("Result", "Notification added to the queue");
			return new ResponseEntity<>(result, HttpStatusCode.valueOf(200));
		} catch (Exception e) {
			result.put("Result", "Error in sending notification, " + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(result, HttpStatusCode.valueOf(500));
		}
	}

	public int generateOTP() {
		int otp = 0;
		try {
			final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
			Date now = new Date();
			otp = totp.generateOneTimePassword(totp.TOTP_ALGORITHM_HMAC_SHA1, now);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return otp;

	}

	@Autowired
	private ProductSectionMasterRepository productSectionRepo;
	@Autowired
	private EServiceMotorDetailsRepository eserviceMotorRepo;
	@Autowired
	private EserviceTravelDetailsRepository eserviceTravelRepo;
	@Autowired
	private EserviceBuildingDetailsRepository eservicebuildRepo;
	@Autowired
	private EserviceCommonDetailsRepository eservicecommonRepo;

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public OtpConfirm validate(ValidateOtp otp,HttpServletRequest http) {
		try {
			List<Error> errorlist = new ArrayList<Error>();

			if (otp == null || otp.getUserOtp().toString().length() > 6
					|| !otp.getUserOtp().toString().matches("[0-9]+")) {
				errorlist.add(new Error("09", "OTP", "Not a Valid OTP"));
			} else {
				Integer count = 0;
				if (otp.getCompanyId().equalsIgnoreCase("100055")) {
					count = otpDataRepo.countByCompanyIdAndOtpIdAndOtp(otp.getCompanyId(),
							new BigDecimal(otp.getOtpToken()), otp.getUserOtp());
				} else {
					count = otpDataRepo.countByCompanyIdAndProductIdAndOtpIdAndOtp(otp.getCompanyId(),
							new BigDecimal(otp.getProductId()), new BigDecimal(otp.getOtpToken()), otp.getUserOtp());
				}

				if (count == 0)
					errorlist.add(new Error("09", "OTP", "Not a Valid OTP"));
				else {
					int i = 0;
					if (otp.getCompanyId().equalsIgnoreCase("100055")) {
						i = otpDataRepo.countByCompanyIdAndOtpIdAndOtpAndExpiryDateIsBefore(otp.getCompanyId(),
								new BigDecimal(otp.getOtpToken()), otp.getUserOtp(), new Date());
					} else {
						i = otpDataRepo.countByCompanyIdAndProductIdAndOtpIdAndOtpAndExpiryDateIsBefore(
								otp.getCompanyId(), new BigDecimal(otp.getProductId()),
								new BigDecimal(otp.getOtpToken()), otp.getUserOtp(), new Date());
					}
					if (i > 0)
						errorlist.add(new Error("10", "OTP", "Otp is Expired"));
				}
			}
			OtpDataDetail otpData = null;
			if (errorlist.size() == 0) {
				if (otp.getCompanyId().equalsIgnoreCase("100055")) {
					otpData = otpDataRepo.findByCompanyIdAndOtpId(otp.getCompanyId(), new BigDecimal(otp.getOtpToken()));
				} else {
				otpData = otpDataRepo.findByCompanyIdAndProductIdAndOtpId(otp.getCompanyId(),
						new BigDecimal(otp.getProductId()), new BigDecimal(otp.getOtpToken()));
				}
			}
			if (otp.getCreateUser() && errorlist.size() == 0) {

				errorlist = createUserLogin(otpData, otp);
				if ((errorlist == null || errorlist.size() == 0) && StringUtils.isNotBlank(otp.getCustomerId())
						&& StringUtils.isNotBlank(otp.getReferenceNo())) {
					List<ProductSectionMaster> prodctSects = productSectionRepo
							.findByProductIdAndCompanyId(Integer.parseInt(otp.getProductId()), otp.getCompanyId());
					List<ProductSectionMaster> collect = prodctSects.stream()
							.filter(distinctByKey(ProductSectionMaster::getMotorYn)).collect(Collectors.toList());
					String loginId = otpData.getMobileCode().concat(otpData.getMobileNo());
					for (ProductSectionMaster productSectionMaster : collect) {
						String motorYn = productSectionMaster.getMotorYn();
						if (motorYn.equals("M")) {
							List<EserviceMotorDetails> referenceNos = eserviceMotorRepo
									.findByRequestReferenceNo(otp.getReferenceNo());

							eserviceMotorRepo.deleteAll(referenceNos);
							referenceNos.forEach(m -> m.setCustomerReferenceNo(otp.getCustomerId()));
							referenceNos.forEach(m -> m.setLoginId(loginId));
							referenceNos.forEach(m -> m.setAgencyCode(otp.getCreatedAgencyCode()));
							eserviceMotorRepo.saveAll(referenceNos);
						} else if (motorYn.equals("H") && "4".equalsIgnoreCase(otp.getProductId())) {
							EserviceTravelDetails referenceNos = eserviceTravelRepo
									.findByRequestReferenceNo(otp.getReferenceNo());
							eserviceTravelRepo.delete(referenceNos);
							referenceNos.setCustomerReferenceNo(otp.getCustomerId());
							referenceNos.setLoginId(loginId);
							referenceNos.setAgencyCode(otp.getCreatedAgencyCode());
							eserviceTravelRepo.save(referenceNos);
						} else if (motorYn.equals("A")) {
							List<EserviceBuildingDetails> referenceNos = eservicebuildRepo
									.findByRequestReferenceNo(otp.getReferenceNo());
							eservicebuildRepo.deleteAll(referenceNos);
							referenceNos.forEach(m -> m.setCustomerReferenceNo(otp.getCustomerId()));
							referenceNos.forEach(m -> m.setLoginId(loginId));
							referenceNos.forEach(m -> m.setAgencyCode(otp.getCreatedAgencyCode()));
							eservicebuildRepo.saveAll(referenceNos);
						} else {
							List<EserviceCommonDetails> referenceNos = eservicecommonRepo
									.findByRequestReferenceNo(otp.getReferenceNo());
							eservicecommonRepo.deleteAll(referenceNos);
							referenceNos.forEach(m -> m.setCustomerReferenceNo(otp.getCustomerId()));
							referenceNos.forEach(m -> m.setLoginId(loginId));
							referenceNos.forEach(m -> m.setAgencyCode(otp.getCreatedAgencyCode()));
							eservicecommonRepo.saveAll(referenceNos);
						}
					}

				}

			} else if (errorlist.size() == 0) {
				Integer count = 0;
				String mobileNo = otpData.getMobileCode().concat(otpData.getMobileNo());
				if (otp.getCompanyId().equalsIgnoreCase("100055")) {
				 count = loginMasterRepo.countByCompanyIdAndLoginId(otp.getCompanyId(), otp.getLoginId());
				}else {
					count = loginMasterRepo.countByCompanyIdAndLoginId(otp.getCompanyId(), mobileNo);
				}
				if (count == 0) {
					errorlist.add(new Error("10", "OTP", "User is Not Valid"));
				}
			}
			CommonLoginRes res = null;
			if (errorlist == null || errorlist.size() == 0) {
				LoginRequest mslogin = new LoginRequest();
				mslogin.setReLoginKey("Y");
				if (otp.getCompanyId().equalsIgnoreCase("100055")) {
					mslogin.setLoginId(otp.getLoginId());
					res = authservice.checkUserOtpLogin(mslogin, http);
				} else {
					mslogin.setLoginId(otpData.getMobileCode().concat(otpData.getMobileNo()));
					mslogin.setPassword("Admin@01");
					res = authservice.checkUserLogin(mslogin, http);
				}

			}
			OtpConfirm c = OtpConfirm.builder().isError((errorlist != null && errorlist.size() > 0) ? true : false)
					.errorlist(errorlist).otpToken(otp.getOtpToken()).res(res).build();
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Autowired
	private LoginDetailsService entityService;

	@Autowired
	private LoginBranchService loginBranchService;

	@Autowired
	private LoginProductService loginProductService;
	@Autowired
	private LoginBranchMasterRepository loginBrokerRepo;

	@Autowired
	private EserviceCustomerDetailsRepository eserviceCustomer;

	private List<Error> createUserLogin(OtpDataDetail otpData, ValidateOtp otp) {
		List<Error> validation = null;
		try {
			String mobileNo = otpData.getMobileCode().concat(otpData.getMobileNo());

			UserCreationReq userCreation = new UserCreationReq();
			UserLoginReq loginInformation = new UserLoginReq();
			loginInformation.setAgencyCode(null);
			loginInformation.setBankCode(null);
			loginInformation.setBrokerCompanyYn("N");
			loginInformation.setCompanyId(otpData.getCompanyId());
			loginInformation.setCreatedBy(otpData.getLoginId());
			loginInformation.setEffectiveDateStart(new Date());
			loginInformation.setLoginId(mobileNo);
			loginInformation.setOaCode(otp.getAgencyCode());
			loginInformation.setPassword("Admin@01");
			loginInformation.setStatus("Y");
			loginInformation.setSubUserType("b2c");
			loginInformation.setUserType("User");
			UserPersonalInfoReq personalInformation = new UserPersonalInfoReq();
			EserviceCustomerDetails custotmer = eserviceCustomer.findByCustomerReferenceNo(otp.getCustomerId());
			personalInformation.setAcExecutiveId("5");
			personalInformation.setAddress1(custotmer.getAddress1());
			personalInformation.setAddress2(custotmer.getAddress2());
			personalInformation.setAddress3(null);
			personalInformation.setApprovedPreparedBy(otpData.getLoginId());
			personalInformation.setCityCode("1");
			personalInformation.setCityName(custotmer.getCityName());
			personalInformation.setCompanyName(custotmer.getCompanyId());
			personalInformation.setContactPersonName(custotmer.getClientName());
			personalInformation.setCoreAppBrokerCode("NA");
			personalInformation.setCountryCode(custotmer.getNationality());
			personalInformation.setDesignation(custotmer.getOccupationDesc());
			personalInformation.setFax(custotmer.getFax());
			personalInformation.setMissippiId(null);
			personalInformation.setMobileCode(custotmer.getMobileCode1());
			personalInformation.setPobox(custotmer.getPinCode());
			personalInformation.setRemarks("B2C Customer");
			personalInformation.setStateCode("");
			personalInformation.setUserMail(custotmer.getEmail1());
			personalInformation.setUserMobile(custotmer.getMobileNo1());
			personalInformation.setUserName(custotmer.getClientName());
			personalInformation.setWhatsappCode(custotmer.getWhatsappCode());
			personalInformation.setWhatsappNo(custotmer.getWhatsappNo());
			personalInformation.setRegulatoryCode("ICC110");
			userCreation.setLoginInformation(loginInformation);
			userCreation.setPersonalInformation(personalInformation);

			/*
			 * validation = validationService.validateUserCreation(userCreation);
			 * if(validation.size()==0)
			 */
			Integer couts = loginMasterRepo.countByCompanyIdAndLoginId(otp.getCompanyId(), mobileNo);
			if (couts == 0) {
				LoginCreationRes createUserLogin = entityService.createUserLogin(userCreation);
				otp.setCreatedAgencyCode(createUserLogin.getAgencyCode());
			} else {
				LoginMaster loginMaster = loginMasterRepo.findByCompanyIdAndLoginId(otp.getCompanyId(), mobileNo);
				otp.setCreatedAgencyCode(loginMaster.getAgencyCode());
			}
			LoginBranchesSaveReq branch = new LoginBranchesSaveReq();
			branch.setLoginId(mobileNo);
			branch.setOaCode(otp.getAgencyCode());
			branch.setInsuranceId(otpData.getCompanyId());
			branch.setCreatedBy(otpData.getLoginId());
			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> brs = loginBrokerRepo.findByLoginIdAndStatus(otpData.getLoginId(), "Y");
			for (LoginBranchMaster loginBranchMaster : brs) {
				branches.add(loginBranchMaster.getBrokerBranchCode());
			}

			branch.setBrokerBranchIds(branches);
			loginBranchService.saveLoginBranches(branch);

			AttachCompnayProductRequest comProduct = new AttachCompnayProductRequest();
			comProduct.setCreatedBy(otpData.getLoginId());
			comProduct.setInsuranceId(otpData.getCompanyId());
			comProduct.setLoginId(mobileNo);

			UserCompanyProductGetReq re = new UserCompanyProductGetReq();
			re.setInsuranceId(otpData.getCompanyId());
			re.setLoginId(mobileNo);
			re.setOaCode(otp.getAgencyCode());
			List<String> productss = new ArrayList<String>();
			List<CompanyProductMasterRes> products = loginProductService.getallNonSelectedUserCompanyProducts(re);
			for (CompanyProductMasterRes companyProductMasterRes : products) {
				productss.add(companyProductMasterRes.getProductId());
			}
			comProduct.setProductIds(productss);

			loginProductService.saveBrokerProductDetails(comProduct);

			return validation;
		} catch (Exception e) {
			e.printStackTrace();
		}
		validation = new ArrayList<Error>();
		validation.add(new Error("10", "unExpected", "Some error while creating user"));
		return validation;
	}

	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;

	@Autowired
	private CompanyProductMasterRepository cpmRepo;

	private QuoteUpdateRes motorPushNotification(OtpDataDetail data, String notificationTemplateName,
			String description) {

		try {
			Notification n = new Notification();
			Broker brokerReq = new Broker();
			if (!data.getLoginId().isEmpty()) {
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(data.getLoginId());
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName() == null ? null : loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail() == null ? "" : loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(
						loginInfo.getWhatsappCodeDesc() == null && loginInfo.getWhatsappCodeDesc().isEmpty() ? null
								: Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(
						loginInfo.getWhatsappNo() == null && loginInfo.getWhatsappNo().isEmpty() ? BigDecimal.ZERO
								: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(
						loginInfo.getMobileCodeDesc() == null && loginInfo.getMobileCodeDesc().isEmpty() ? null
								: Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(
						loginInfo.getUserMobile() == null && loginInfo.getUserMobile().isEmpty() ? BigDecimal.ZERO
								: new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
			}
			Customer cusReq = new Customer();

			cusReq.setCustomerMailid(
					data.getEmailId() != null && !data.getEmailId().isEmpty() ? data.getEmailId() : null);
			cusReq.setCustomerName("Customer");
			cusReq.setCustomerPhoneCode(
					data.getMobileNo() != null && !data.getMobileNo().isEmpty() ? Integer.valueOf(data.getMobileCode())
							: null);
			cusReq.setCustomerPhoneNo(
					data.getMobileNo() != null && !data.getMobileNo().isEmpty() ? new BigDecimal(data.getMobileNo())
							: null);
			cusReq.setCustomerMessengerCode(data.getWhatsappCode() != null && !data.getWhatsappCode().isEmpty()
					? Integer.valueOf(data.getWhatsappCode())
					: null);
			cusReq.setCustomerMessengerPhone(data.getWhatsappNo() != null && !data.getWhatsappNo().isEmpty()
					? new BigDecimal(data.getWhatsappNo())
					: null);
			cusReq.setCustomerRefno(
					data.getCustomerId() != null && !data.getCustomerId().isEmpty() ? data.getCustomerId() : null);

			List<InsuranceCompanyMaster> company = insuranceRepo.findByCompanyIdOrderByAmendIdDesc(data.getCompanyId());

			n.setUnderwriters(null);
			// Company Info
			n.setCompanyid(data.getCompanyId());
			n.setCompanyName(company.get(0).getCompanyName());

			n.setNotifTemplatename(notificationTemplateName);
			n.setStatusMessage(null);

			// Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription(description);
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setPolicyNo(null);
			List<CompanyProductMaster> products = cpmRepo
					.findByCompanyIdAndProductIdOrderByAmendIdDesc(data.getCompanyId(), data.getProductId().intValue());
			n.setProductid(data.getProductId().intValue());
			n.setProductName(products.isEmpty() ? "" : products.get(0).getProductName());
			n.setQuoteNo(null);
			n.setSectionName(null);
			if (!data.getOtp().isEmpty()) {
				n.setOtp(Integer.parseInt(data.getOtp()));
			}
			n.setRefNo(data.getReqNo());
			n.setBranchCode(null);

			// Calling pushNotification
			CommonRes res = notiService.pushNotification(n);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public OtpConfirm createUser(ValidateOtp otp) {

		List<Error> errorlist = new ArrayList<Error>();
		OtpDataDetail otpData = new OtpDataDetail();
		otpData.setMobileCode(otp.getMobileCode());
		otpData.setMobileNo(otp.getMobileNo());
		otpData.setCompanyId(otp.getCompanyId());
		otpData.setCustomerId(otp.getCustomerId());
		otpData.setLoginId("guest");

		errorlist = createUserLogin(otpData, otp);
		if ((errorlist == null || errorlist.size() == 0) && StringUtils.isNotBlank(otp.getCustomerId())
				&& StringUtils.isNotBlank(otp.getReferenceNo())) {
			List<ProductSectionMaster> prodctSects = productSectionRepo
					.findByProductIdAndCompanyId(Integer.parseInt(otp.getProductId()), otp.getCompanyId());
			List<ProductSectionMaster> collect = prodctSects.stream()
					.filter(distinctByKey(ProductSectionMaster::getMotorYn)).collect(Collectors.toList());
			String loginId = otpData.getMobileCode().concat(otpData.getMobileNo());
			for (ProductSectionMaster productSectionMaster : collect) {
				String motorYn = productSectionMaster.getMotorYn();
				if (motorYn.equals("M")) {
					List<EserviceMotorDetails> referenceNos = eserviceMotorRepo
							.findByRequestReferenceNo(otp.getReferenceNo());

					eserviceMotorRepo.deleteAll(referenceNos);
					referenceNos.forEach(m -> m.setCustomerReferenceNo(otp.getCustomerId()));
					referenceNos.forEach(m -> m.setLoginId(loginId));
					referenceNos.forEach(m -> m.setAgencyCode(otp.getCreatedAgencyCode()));
					eserviceMotorRepo.saveAll(referenceNos);
				} else if (motorYn.equals("H") && "4".equalsIgnoreCase(otp.getProductId())) {
					EserviceTravelDetails referenceNos = eserviceTravelRepo
							.findByRequestReferenceNo(otp.getReferenceNo());
					eserviceTravelRepo.delete(referenceNos);
					referenceNos.setCustomerReferenceNo(otp.getCustomerId());
					referenceNos.setLoginId(loginId);
					referenceNos.setAgencyCode(otp.getCreatedAgencyCode());
					eserviceTravelRepo.save(referenceNos);
				} else if (motorYn.equals("A")) {
					List<EserviceBuildingDetails> referenceNos = eservicebuildRepo
							.findByRequestReferenceNo(otp.getReferenceNo());
					eservicebuildRepo.deleteAll(referenceNos);
					referenceNos.forEach(m -> m.setCustomerReferenceNo(otp.getCustomerId()));
					referenceNos.forEach(m -> m.setLoginId(loginId));
					referenceNos.forEach(m -> m.setAgencyCode(otp.getCreatedAgencyCode()));
					eservicebuildRepo.saveAll(referenceNos);
				} else {
					List<EserviceCommonDetails> referenceNos = eservicecommonRepo
							.findByRequestReferenceNo(otp.getReferenceNo());
					eservicecommonRepo.deleteAll(referenceNos);
					referenceNos.forEach(m -> m.setCustomerReferenceNo(otp.getCustomerId()));
					referenceNos.forEach(m -> m.setLoginId(loginId));
					referenceNos.forEach(m -> m.setAgencyCode(otp.getCreatedAgencyCode()));
					eservicecommonRepo.saveAll(referenceNos);
				}
			}

		}
		OtpConfirm c = OtpConfirm.builder().isError((errorlist != null && errorlist.size() > 0) ? true : false)
				.errorlist(errorlist).otpToken(otp.getOtpToken()).build();
		return c;

	}

	@Autowired
	private RestTemplate restTemplate;

	@Value("${oman.sms.url}")
	private String smsUrl;

	@Value("${oman.sms.userid}")
	private String userId;

	@Value("${oman.sms.password}")
	private String password;

	public OtpConfirm omanSMSOTP(UserOtp otp) {

		LoginMaster loginId = loginMasterRepo.findByLoginIdAndStatus(otp.getLoginId(), "Y");
		LoginUserInfo loginUserInfo = loginUserRepo.findByLoginId(otp.getLoginId());

		if (loginId != null) {
			int newOtp = generateOTP();
			long otpId = Instant.now().getEpochSecond();
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.MINUTE, 2);

			OtpDataDetail odd = OtpDataDetail.builder().companyId(loginId.getCompanyId())
					.emailId(loginUserInfo == null ? null : loginUserInfo.getUserMail())
					.mobileCode(loginUserInfo.getMobileCode()).mobileNo(loginUserInfo.getUserMobile()).whatsappCode("")
					.whatsappNo("").otp(String.valueOf(newOtp)).otpId(new BigDecimal(otpId)).loginId(otp.getLoginId())
					.expiryDate(instance.getTime()).sNo(new BigDecimal(otpId)).build();
			otpDataRepo.save(odd);

			new Thread(new Runnable() {
				@Override
				public void run() {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
					MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
					body.add("UserId", userId);
					body.add("Password", password);
					body.add("MobileNo", loginUserInfo.getMobileCode() + loginUserInfo.getUserMobile());
					body.add("Message", "OTP " + newOtp
							+ "is for you to log into our portal for authentication. This is to ensure your data is safe and secure and accessible to only you.OTP (One Time Password) to be input on the portal to log in. Welcome to the online revolution");
					body.add("PushDateTime",
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
					body.add("Lang", "0");
					body.add("Header", "ismartsms");
					body.add("referenceIds", "101");
					HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
					ResponseEntity<String> response = restTemplate.exchange(smsUrl, HttpMethod.POST, request,
							String.class);
					if (response.getBody() != null) {
						System.out.println("SMS Sent Successfully");
					} else {
						System.out.println("SMS Failed. Response Code: " + response.getBody());
					}
				}
			}).start();
			OtpConfirm c = OtpConfirm.builder().isError(false).otpToken(otpId).companyId(loginId.getCompanyId())
					.otp(String.valueOf(newOtp)).build();
			return c;
		} else {
			List<Error> errorlist = new ArrayList<Error>();
			errorlist.add(new Error("10", "Contact Admin", "Kindly contact Admin to Approver you Registration"));
			OtpConfirm c = OtpConfirm.builder().isError(true).errorlist(errorlist).build();
			return c;
		}
	}

}
