package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.RenewQuotePolicy;
import com.maan.eway.bean.RenewQuotePolicyId;
/**
 * <h2>BankMasterRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:26
 * <p>
 * Description: "BankMaster" Repository
 */
 
 
 
public interface RenewQuotePolicyRepository  extends JpaRepository<RenewQuotePolicy,RenewQuotePolicyId > , JpaSpecificationExecutor<RenewQuotePolicy> {


	List<RenewQuotePolicy> findByCurrentStatusCodeAndTranId(String string, String tranId);

	List<RenewQuotePolicy> findByCurrentStatusCodeInAndTranIdIn(List<String> status, List<String> tranId);

	List<RenewQuotePolicy> findByOldpolicyNo(String policyNo);
	
	List<RenewQuotePolicy> findByTranId(String tranId);

	List<RenewQuotePolicy> findByTranIdAndCurrentStatusCode(String tranId, String string);

	List<RenewQuotePolicy> findByTranIdAndCurrentStatusCodeNotIn(String tranId, List<String> asList);

	List<RenewQuotePolicy> findByTranIdAndCompanyIdAndBranchCodeAndCurrentStatusCode(String tranId, String insuranceId,
			String branchCode, String string);

	List<RenewQuotePolicy> findByTranIdAndCompanyIdAndBranchCodeAndCurrentStatusCodeNotIn(String tranId,
			String insuranceId, String branchCode, List<String> asList);

	List<RenewQuotePolicy> findByOldpolicyNoIn(List<String> policyNos);

	List<RenewQuotePolicy> findBySourceCode(String coreAppBrokerCode);
	
	List<RenewQuotePolicy> findByBranchCodeAndCompanyId(String branchCode, String companyId);

	List<RenewQuotePolicy> findByBranchCodeAndCompanyIdAndPolProductCode(String branchCode,String companyId, String polProductCode);

	//@Query(value = "SELECT DIVISION_CODE,DIVISION_NAME,POL_PROD_CODE,PROD_NAME,POL_TYPE,CUSTOMER_CODE,CUSTOMER_NAME,POL_ASSR_CODE,POL_ASSR_NAME,SOURCE_CODE,SOURCE_NAME,POL_NO,POL_SYS_ID,INDEX_NO,POL_FM_DT,POL_EXP_DT,NEW_START_DATE,POL_PREM,POL_SI_LC_1,NET_PREM,CHARGE_AMT,LOADING_PREMIUM,DISCOUNT_PREMIUM,INSURED_CIVIL_ID,INSURED_MOBILE,INSURED_EMAIL_ID,MAKE_ID,MAKE_ID_NAME,MODEL_ID,MODEL_ID_NAME,BODY_TYPE,BODY_TYPE_NAME,PLATE_NUMBER,CHASS_NO,ENGINE_NUMBER,MANUFACTURE_YEAR,TYPE_OF_COVER,TYPE_OF_COVER_NAME,USAGE_TYPE,USAGE_TYPE_NAME,VEHICLE_AGE,NO_OF_PASSENGER,SEATING,CC,CLAIM_FREE_YEARS,RENEWAL_COUNT,COLOR,COLOR_NAME,PLATE_COLOR,PLATE_COLOR_NAME,VEHICLE_VALUE,TONNAGE,REQUEST_TIME,RESPONSE_TIME,ROW_NUMBER() OVER(PARTITION BY PLATE_NUMBER ORDER BY NULL) RN FROM  RENEW_REMINDER_POL A WHERE PLATE_NUMBER IS NOT NULL AND NOT EXISTS (SELECT 1 FROM RENEW_PREMIA_POLICY_RAW B WHERE B.POL_NO=A.POL_NO AND B.PLATE_NUMBER=A.PLATE_NUMBER)) WHERE RN=1", nativeQuery = true)
	//List<Map<String, Object>> getRenewalViewList();

	


	

}
