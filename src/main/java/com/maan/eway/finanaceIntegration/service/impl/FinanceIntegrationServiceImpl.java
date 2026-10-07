package com.maan.eway.finanaceIntegration.service.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.maan.eway.finanaceIntegration.service.FinanceIntegerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.WntCustDtl;
import com.maan.eway.bean.YiPolicyDetail;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.EserviceCustomerDetailsServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.finanaceIntegration.req.CustomerCreationReq;
import com.maan.eway.finanaceIntegration.req.FinanceIntegerationBean;
import com.maan.eway.finanaceIntegration.req.FinanceReq;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.WntCustDtlRepository;
import com.maan.eway.repository.YiPolicyDetailRepository;

@Service
public class FinanceIntegrationServiceImpl implements FinanceIntegerationService {
	@Value(value = "${BasicAuthPass}")
	private String BasicAuthPass;

	@Value(value = "${BasicAuthName}")
	private String BasicAuthName;

	@Value(value = "${finace.CreateFreshpolicy}")
	private String freshPolicy;

	@Value(value = "${finace.generateToken}")
	private String generateToken;

	@Autowired
	private YiPolicyDetailRepository yiPolicyDetailRepository;

	@Autowired
	private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepository;

	@Autowired
	private WntCustDtlRepository wntCustDtlRepository;

	@Autowired
	private EserviceCustomerDetailsServiceImpl eserviceCustomerDetailsServiceImpl;	
	
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository repository;
	
	@Override
	public PremiaResponse customerEntityCreation(String sysId, FinanceReq req) {
		PremiaResponse res1 = new PremiaResponse();

		try {
			List<YiPolicyDetail> yiDetailList = yiPolicyDetailRepository.findByPolAssrCode(sysId);
			if (yiDetailList == null || yiDetailList.isEmpty()) {
				res1.setResponse("Policy not found for sysId: " + sysId);
				return res1;
			}

			YiPolicyDetail yiPolicyDetail = yiDetailList.get(0);
			String polCustCode = yiPolicyDetail.getPolAssrCode();

			Optional<EserviceCustomerDetails> customerDetail = eserviceCustomerDetailsRepository
					.findByPolCustCode(polCustCode);
			EserviceCustomerDetails eserviceCustomerDetails = customerDetail.get();
			eserviceCustomerDetails.setPolCustCode(polCustCode);
			eserviceCustomerDetailsRepository.save(eserviceCustomerDetails);

			Optional<WntCustDtl> custDtl = wntCustDtlRepository
					.findByCode(eserviceCustomerDetails.getCustomerReferenceNo());

			if (custDtl.isEmpty()) {
				WntCustDtl wntCustDtl = new WntCustDtl();

				wntCustDtl.setCode(eserviceCustomerDetails.getCustomerReferenceNo());
				wntCustDtl.setCorporate(null);
				wntCustDtl.setCredit(null);
				wntCustDtl.setGridGlLedgerStatus(null);
				wntCustDtl.setSubGlobalLedgerStatus(null);
				wntCustDtl.setGroupLimit(null);
				wntCustDtl.setGroupId(null);
				wntCustDtl.setNameAr(null);
				wntCustDtl.setNameEn(null);
				wntCustDtl.setPartyType(null);
				wntCustDtl.setGlGroupLabel(null);
				wntCustDtl.setGlGroupValue(null);
				wntCustDtl.setGlLabel(null);
				wntCustDtl.setGlValue(null);
				wntCustDtl.setParentPartyLabel(null);
				wntCustDtl.setParentPartyValue(null);
				wntCustDtl.setGlCompLabel(null);
				wntCustDtl.setGlCompValue(null);
				wntCustDtl.setGlCurrLabel(null);
				wntCustDtl.setGlCurrValue(null);
				wntCustDtl.setCustReqRespStatus(null);
				wntCustDtl.setCustReqSentDt(null);
				wntCustDtl.setCustReqMessage(null);
				wntCustDtl.setCustResRecdDt(null);
				wntCustDtl.setCustRespMessage(null);
				wntCustDtl.setCustFinIntgStatus(null);
				wntCustDtl.setCustFinIntgRefNo(null);
				wntCustDtl.setLedgerType(null);
				wntCustDtlRepository.save(wntCustDtl);
			} else {
				WntCustDtl wntCustDtl = custDtl.get();
				wntCustDtl.setCorporate(null);
				wntCustDtl.setCredit(null);
				wntCustDtl.setGridGlLedgerStatus(null);
				wntCustDtl.setSubGlobalLedgerStatus(null);
				wntCustDtl.setGroupLimit(null);
				wntCustDtl.setGroupId(null);
				wntCustDtl.setNameAr(null);
				wntCustDtl.setNameEn(null);
				wntCustDtl.setPartyType(null);
				wntCustDtl.setGlGroupLabel(null);
				wntCustDtl.setGlGroupValue(null);
				wntCustDtl.setGlLabel(null);
				wntCustDtl.setGlValue(null);
				wntCustDtl.setParentPartyLabel(null);
				wntCustDtl.setParentPartyValue(null);
				wntCustDtl.setGlCompLabel(null);
				wntCustDtl.setGlCompValue(null);
				wntCustDtl.setGlCurrLabel(null);
				wntCustDtl.setGlCurrValue(null);
				wntCustDtl.setCustReqRespStatus(null);
				wntCustDtl.setCustReqSentDt(null);
				wntCustDtl.setCustReqMessage(null);
				wntCustDtl.setCustResRecdDt(null);
				wntCustDtl.setCustRespMessage(null);
				wntCustDtl.setCustFinIntgStatus(null);
				wntCustDtl.setCustFinIntgRefNo(null);
				wntCustDtl.setLedgerType(null);
				wntCustDtlRepository.save(wntCustDtl);
//				wntCustDtlRepository.updateWntCustDtl(wntCustDtl.getCode(), wntCustDtl.getCorporate(),
//						wntCustDtl.getCredit(), wntCustDtl.getGridGlLedgerStatus(),
//						wntCustDtl.getSubGlobalLedgerStatus(), wntCustDtl.getGroupLimit(), wntCustDtl.getGroupId(),
//						wntCustDtl.getNameAr(), wntCustDtl.getNameEn(), wntCustDtl.getPartyType(),
//						wntCustDtl.getGlGroupLabel(), wntCustDtl.getGlGroupValue(), wntCustDtl.getGlLabel(),
//						wntCustDtl.getGlValue(), wntCustDtl.getParentPartyLabel(), wntCustDtl.getParentPartyValue(),
//						wntCustDtl.getGlCompLabel(), wntCustDtl.getGlCompValue(), wntCustDtl.getGlCurrLabel(),
//						wntCustDtl.getGlCurrValue(), wntCustDtl.getCustReqRespStatus(), wntCustDtl.getCustReqSentDt(),
//						wntCustDtl.getCustReqMessage(), wntCustDtl.getCustResRecdDt(), wntCustDtl.getCustRespMessage(),
//						wntCustDtl.getCustFinIntgStatus(), wntCustDtl.getCustFinIntgRefNo(),
//						wntCustDtl.getLedgerType());
			}

			String auth = BasicAuthName + ":" + BasicAuthPass;
			byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodedAuth);

//			RestTemplate restTemplate = new RestTemplate();
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.set("Authorization", authHeader);
//			HttpEntity<wntCustDtlReq> entityReq = new HttpEntity<>(wntCustDtl, headers);

//			/** FinanceIntegeration Call **/
//			String url = eserviceCustomerDetail.isPresent()
//					? "http://102.222.132.213:5053/api/corefinance/updateCustomerEntity"
//					: "http://102.222.132.213:5053/api/corefinance/customerEntityCreation";
//			ResponseEntity<PremiaResponse> response = restTemplate.postForEntity(url, entityReq, PremiaResponse.class);
//			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//				res1.setResponse(response.getBody().getResponse());
//				res1.setPremiaPolicyNo(response.getBody().getPremiaPolicyNo());
//			} else {
//				res1.setResponse("API Error");
//				res1.setPremiaPolicyNo("N/A");
//			}
		} catch (Exception e) {
			e.printStackTrace();
			res1.setResponse("Exception occurred");
			res1.setPremiaPolicyNo(e.getMessage());
		}

		return res1;
	}

	@Override
	public ResponseEntity<?> createFreshPolicy(FinanceIntegerationBean integrationBean) {
		List<Error> errors = null;
		CommonRes data = new CommonRes();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String loginPayload = "{\"username\": \"admin\", \"password\": \"admin\"}";
			HttpHeaders loginHeaders = new HttpHeaders();
			loginHeaders.setContentType(MediaType.APPLICATION_JSON);
			loginHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<String> loginRequest = new HttpEntity<>(loginPayload, loginHeaders);
			ResponseEntity<String> loginResponse = restTemplate.postForEntity(generateToken, loginRequest,
					String.class);
			String token = loginResponse.getBody();
			System.out.println("Token: " + token);
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
			requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			requestHeaders.setBearerAuth(token);
			HttpEntity<FinanceIntegerationBean> request = new HttpEntity<>(integrationBean, requestHeaders);
			ResponseEntity<String> response = restTemplate.postForEntity(freshPolicy, request, String.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
			data.setIsError(true);
			data.setMessage("Failed");
			Error err = new Error();
			err.setCode("9999");
			err.setField("createFreshPolicy");
			err.setMessage("Fianance createFreshPolicy");
			errors = new ArrayList<Error>();
			errors.add(err);
			data.setErroCode(500);
			return ResponseEntity.ok(data);
		}
	}

	@Override
	public ResponseEntity<?> financeIntegrationCustomerCreation(CustomerCreationReq customerCreationReq) {
		CommonRes data = new CommonRes();
		List<Error> errors = null;
		try {
			CustomerCreationReq cust = eserviceCustomerDetailsServiceImpl
					.financeIntegrationCustomerCreation(customerCreationReq);
			/*
			 * if (cust.getPolCustCode() != null) {
			 * eserviceCustomerDetailsServiceImpl.cutomerCreationFinanceThirdPartyApiCall(
			 * cust.getPolCustCode(), cust.getCustomerReferenceNo(),
			 * cust.getCompanyId(),cust.getPolicyNo()); }
			 */
			CustomerCreationReq finCustCheckExist = eserviceCustomerDetailsServiceImpl.financeCustomerCheckExist(cust);
			if (!finCustCheckExist.isThirdpartyStatus()) {
				eserviceCustomerDetailsServiceImpl.customerCreationFinanceThirdPartyApiCall(cust.getPolCustCode(),
						cust.getCustomerReferenceNo(), cust.getCompanyId(), cust.getPolicyNo());
			}
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return ResponseEntity.ok(data);
		} catch (Exception e) {
			e.printStackTrace();
			data.setIsError(true);
			data.setMessage("Failed");
			Error err = new Error();
			err.setCode("9999");
			err.setField("CustomerCreation");
			err.setMessage("Fianance CustomerCreation");
			errors = new ArrayList<Error>();
			errors.add(err);
			data.setErroCode(500);
			return ResponseEntity.ok(data);
		}
	}

	@Override
	public ResponseEntity<?> financeIntegrationUpdateCreation(CustomerCreationReq customerCreationReq) {
		CommonRes data = new CommonRes();
		List<Error> errors = null;
		try {
			eserviceCustomerDetailsServiceImpl.financeIntegrationCustomerDetailUpdate(customerCreationReq);

			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return ResponseEntity.ok(data);
		} catch (Exception e) {
			e.printStackTrace();
			data.setIsError(true);
			data.setMessage("Failed");
			Error err = new Error();
			err.setCode("9999");
			err.setField("UpdateCreation");
			err.setMessage("Fianance UpdateCreation");
			errors = new ArrayList<Error>();
			errors.add(err);
			data.setErroCode(500);
			return ResponseEntity.ok(data);
		}
	}

	public FinanceIntegerationBean draftAccountEntryRequest(FinanceIntegerationBean req) {
		String quoteNo = req.getQuoteNo();
		HomePositionMaster home = homePosistionRepo.findByQuoteNo(quoteNo);
		req.setLoginUserId(home.getLoginId());
		req.setPolicyNo(home.getPolicyNo());
		req.setAccountType("UW-001");
		req.setProductId(home.getProductId().toString());
		req.setCompanyId(home.getCompanyId());
		return req;

	}

}
