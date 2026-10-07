package com.maan.eway.endorsment.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
//import com.maan.eway.repository.MotorVehicleInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;

@Component
public class QuoteInfoUtil {

	@Autowired
	private HomePositionMasterRepository hpmRepo;
	
	public HomePositionMaster getFromHomePositionMaster(String quoteNo) {
		try {
			HomePositionMaster h = hpmRepo.findByQuoteNo(quoteNo);
			return h;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@Autowired
	private PolicyCoverDataRepository pcdRepo;
	
	public List<PolicyCoverData> getFromPolicyCoverData(String quoteNo){
		try {
			List<PolicyCoverData> qs=pcdRepo.findByQuoteNo(quoteNo);
			return qs;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	@Autowired
	private LoginMasterRepository logRepo;
	
	public LoginMaster getLogInDetails(String loginId,String companyId) {

		try {
			LoginMaster qs=logRepo.findByCompanyIdAndLoginId(companyId,loginId);
			return qs;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	
	}
	
	
	@Autowired
	private LoginUserInfoRepository logUserRepo;
	
	public LoginUserInfo getLogInUserDetails(String loginId,String companyId) {

		try {
			LoginUserInfo qs=logUserRepo.findByLoginId(loginId);
			return qs;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;  
	}
	
	
	@Autowired
	private CompanyProductMasterRepository cpmRepo;
	public CompanyProductMaster getFromCompanyProductMaster(String companyId,String productId ) {

		try {
				List<CompanyProductMaster> qs=cpmRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(companyId,Integer.parseInt(productId));
				if(!qs.isEmpty())
			return qs.get(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;  
	}
	
	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;
	
	public InsuranceCompanyMaster getFromInsuranceCompanyMaster(String companyId) {
		
		try {
			List<InsuranceCompanyMaster> ls = insuranceRepo.findByCompanyIdOrderByAmendIdDesc(companyId);
			if(!ls.isEmpty()) {
				return ls.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
		}
		
	@Autowired
	private ListItemValueRepository listvRepo;
	
	public ListItemValue getFromListItemValue(String itemtype,String itemvalue) {
		try {
			ListItemValue l=listvRepo.findByItemTypeAndItemCodeAndStatus(itemtype,itemvalue,"Y");			 
			return l;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//ltPayment

	@Autowired
	private PersonalInfoRepository personlRepo;
	public PersonalInfo getFromPresonalInfo(String companyId,String customerId) {
		try {
			List<PersonalInfo> p = personlRepo.findByCompanyIdAndCustomerId(companyId,customerId);
			if(!p.isEmpty()) {
				return p.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*@Autowired
	private MotorVehicleInfoRepository motorvehicleRepo;
	@Autowired
	private MotorDataDetailsRepository mddRepo;
	public MotorVehicleInfo getFromMotorVehicleInfo(String quoteNo,String vehicleId) {
		try {
			MotorDataDetails mdd = mddRepo.findByQuoteNoAndVehicleId(quoteNo,vehicleId);
			MotorVehicleInfo m = motorvehicleRepo.findByResChassisNumber(mdd.getChassisNumber());
			return m;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
 		return null;
	}*/
	@Autowired
	private ProductSectionMasterRepository sectionRepo;
	public Object getFromSectionMaster(String companyId, Integer productId, Integer sectionId) {
		 try {
			 List<ProductSectionMaster> p= sectionRepo.findByProductIdAndCompanyIdAndSectionIdAndStatusOrderByAmendIdDesc(productId, companyId,sectionId,"Y");
			 return p.get(0);
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
}
