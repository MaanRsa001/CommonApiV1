package com.maan.eway.admin.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.req.AdditionalInfoReq;
import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.AttachEndtIdsReq;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.AttachIssuerProductRequest;
import com.maan.eway.admin.req.AttachIssuerReferalReq;
import com.maan.eway.admin.req.AttachReferalReq;
import com.maan.eway.admin.req.AttacheIssuerBranchReq;
import com.maan.eway.admin.req.AttachedBranchesReq;
import com.maan.eway.admin.req.BrokerBranchesReq;
import com.maan.eway.admin.req.BrokerCreationReq;
import com.maan.eway.admin.req.CommonLoginCreationReq;
import com.maan.eway.admin.req.IssuerCraeationReq;
import com.maan.eway.admin.req.IssuerLoginReq;
import com.maan.eway.admin.req.IssuerPersonalInfoReq;
import com.maan.eway.admin.req.LoginBranchesSaveReq;
import com.maan.eway.admin.req.UserCreationReq;
import com.maan.eway.admin.service.LoginValidationService;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class LoginValidationServiceImpl implements LoginValidationService  {

	@Autowired
	private BasicLoginValidationService basicValidation;
	
	private Logger log=LogManager.getLogger(LoginValidationService.class);
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private LoginBranchMasterRepository  loginBranchRepo ;
	
	
	
	@PersistenceContext
	private EntityManager em;
	
//*************************************** Login Creation Apis Validations**********************************************************//
	
	@Override
	public List<String> validateBrokerCreation(BrokerCreationReq req) {
		List<String> errors = new ArrayList<String>();
		 DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			
			// Common Errors
			
			CommonLoginCreationReq commonReq = new CommonLoginCreationReq();
			dozerMapper.map(req, commonReq);
			String companyId = commonReq.getLoginInformation().getCompanyId();
			List<String>  commonErrors = basicValidation.commonLoginCreationValidation(commonReq) ;
			if(commonErrors.size()>0  ) {
				errors.addAll(commonErrors);
			}
			
			// Additional Broker Validataion
			AdditionalInfoReq  brokerReq = new AdditionalInfoReq();
			dozerMapper.map(req.getPersonalInformation() , brokerReq); 
			List<String>  brokerErrors = null;
			if("100055".equalsIgnoreCase(companyId))
			{
				 brokerErrors = commonBrokerPersonalValidationForOman(brokerReq) ;
			}else
			{
				  brokerErrors = basicValidation.commonBrokerPersonalValidation(brokerReq) ;
			}
			
			if(brokerErrors.size()>0  ) {
				errors.addAll(brokerErrors);
			}
			
			if(StringUtils.isBlank(commonReq.getLoginInformation().getBrokerCompanyYn()))  {
			//	errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
				errors.add("1761");
				
			} else if( ! (commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("Y") || commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("N")) )  {
			//	errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
				errors.add("1761");
			}
			
//			if(StringUtils.isBlank(brokerReq.getTaxExemptedYn())  ) {
//				errors.add(new Error("22", "TaxExempted", "Please Select Tax Exempted Yes/No" ));
//			}
			
			if(StringUtils.isNotBlank(brokerReq.getCreditLimit())  ) {
				if(! brokerReq.getCreditLimit().matches("[0-9.]+")  ) {
			//		errors.add(new Error("22", "CreditLimit", "Please Enter Valid Number in Credit Limit" ));
					errors.add("1762");
				}
			} 

			if(StringUtils.isBlank(brokerReq.getCustomerCode())  ) {
			//	errors.add(new Error("22", "CustomerCode", "Please Select Customer Code" ));
				errors.add("1763");
				
			} 
			if(StringUtils.isBlank(brokerReq.getRegulatoryCode())  ) {
			//	errors.add(new Error("22", "RegulatoryCode", "Please Enter Regulatory Code" ));
				errors.add("1764");
				
			} else if (brokerReq.getRegulatoryCode().length() > 20  ) {
			//	errors.add(new Error("22", "RegulatoryCode", "Regulatory Code Max 20 Characters only allowed" ));
				errors.add("1765");
				
			}
		  
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("07", "Common Error", e.getMessage() ));
			return errors;
		}
		return errors;
	}
	
	public List<String> commonBrokerPersonalValidationForOman(AdditionalInfoReq brokerReq) {

	    List<String> errorList = new ArrayList<>();

	    try {

	        // Insured Name (Company Name)
	        if (StringUtils.isBlank(brokerReq.getCompanyName())) {
	        	//Please enter the Insured name
	            errorList.add("5027");
	        } else if (brokerReq.getCompanyName().length() > 100) {
	        	//Insured name should be 100 characters
	            errorList.add("5028");
	        } else if (!brokerReq.getCompanyName().matches("^[A-Za-z0-9&.\\-'(), ]+$")) {
	        	//Special characters not allowed for insured name
	            errorList.add("5029");
	        }

	        // C.R No
	        if (StringUtils.isBlank(brokerReq.getCRNo())) {
	        	//Please enter the CRNO
	            errorList.add("5030");
	        } else if (!brokerReq.getCRNo().matches("^\\d{7}$")) {
	        	//CRNO must be numerical and length must be 7 digits
	            errorList.add("5031");
	        }

	        // C.R Expiry Date
	        if (brokerReq.getCrexpiryDate() == null) {
	        	//Please enter CR expiry date
	            errorList.add("5032");
	        } else if (!brokerReq.getCrexpiryDate().after(new Date())) {
	        	//CR expiry date must me future date
	            errorList.add("5033");
	        }
	        
	        if (StringUtils.isBlank(brokerReq.getVatRegNo())) {

	        	//Please enter the VAT Registration number
	            errorList.add("5036");

	        } 

	        if (brokerReq.getVatExpiryDate() == null) {
	        	//Please enter VAT expiry date
	            errorList.add("5033");
	        } else if (!brokerReq.getVatExpiryDate().after(new Date())) {
	        	//VAT expiry date must me future date
	            errorList.add("5035");
	        }

	        // Tax Card No - Optional

	        // Tax Card Expiry Date - Optional

	        // Mobile Number
	        if (StringUtils.isBlank(brokerReq.getUserMobile())) {
	        	//Please enter the mobile no
	            errorList.add("5039");
	        } else if (!brokerReq.getUserMobile().matches("^[79]\\d{7}$")) {
	        	//mobile no must start with 7,9
	            errorList.add("5040");
	        }

	        // P.O Box (Optional)
	        if (StringUtils.isNotBlank(brokerReq.getPobox())
	                && !brokerReq.getPobox().matches("^\\d{1,6}$")) {
	        	//PO BOX doesnt has Letters and Special characters
	            errorList.add("5041");
	        }

	        // Country
	        if (StringUtils.isBlank(brokerReq.getCountryCode())) {
	        	// please enter the country name
	            errorList.add("5042");
	        } else if (!"OMAN".equalsIgnoreCase(brokerReq.getCountryCode())
	                && !"OM".equalsIgnoreCase(brokerReq.getCountryCode())
	                && !"OMN".equalsIgnoreCase(brokerReq.getCountryCode())) {
	        	//Country name must be OMAN
	            errorList.add("5043");
	        }

	        // Email
	        if (StringUtils.isBlank(brokerReq.getUserMail())) {
	        	//Please enter the email id 
	            errorList.add("5044");
	        } else if (!isValidMail(brokerReq.getUserMail())) {
	        	//Please enter the valid mail id 
	            errorList.add("5045");
	        }

	        // Activity License No
	        if (StringUtils.isBlank(brokerReq.getActivityLicenseno())) {
	        	//Please enter Activity License No
	            errorList.add("5046");
	        }

	        // Activity License Expiry
	        if (brokerReq.getActivityExpiryDate() == null) {
	           //Please enter the Activity License no expiry date
	            errorList.add("5047");
	        } else if (!brokerReq.getActivityExpiryDate().after(new Date())) {
	        	//Activity License no expiry date must be future date
	            errorList.add("5048");
	        }
	        
	     // Tax Card No (Optional)
	        if (StringUtils.isNotBlank(brokerReq.getTaxCardNo())) {

	            if (!brokerReq.getTaxCardNo().matches("^\\d+$")) {
	            	errorList.add("5037"); // Tax Card No must be numeric
	            } else if (brokerReq.getTaxCardNo().length() > 20) {
	                errorList.add("5038"); // Tax card Maximum length is 20
	            }

	        }
 
	    } catch (Exception e) {
	        e.printStackTrace();
	        errorList.add("01");
	    }

	    return errorList;
	}
	
	public static boolean isValidMail(String mail) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.matches();

	}
	
	@Override
	public List<String> validateIssuerCreation(IssuerCraeationReq req) {
		List<String> errors = new ArrayList<String>();
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setAmbiguityIgnored(true);	
		try {
			
			// Common Errors
			CommonLoginCreationReq commonReq = new CommonLoginCreationReq();
			mapper.map(req, commonReq);
			List<String>  commonErrors = basicValidation.commonLoginCreationValidation(commonReq) ;
			if(commonErrors.size()>0  ) {
				errors.addAll(commonErrors);
			}
			
			IssuerPersonalInfoReq personalReq = req.getPersonalInformation() ;
			IssuerLoginReq loginReq = req.getLoginInformation();
			
			if(StringUtils.isNotBlank(req.getLoginInformation().getSubUserType()) && ! req.getLoginInformation().getSubUserType().equalsIgnoreCase("SuperAdmin") ) {
				
				if(StringUtils.isNotBlank(loginReq.getSubUserType()) && (loginReq.getSubUserType().equalsIgnoreCase("low") ) ) { 
					/*if( loginReq.getProductIds()==null || loginReq.getProductIds().size() == 0 ) {
						//	errors.add(new Error("06", "ProductIds", "Please Choose Atleast One Product"));
							errors.add("1766");
					}*/
				}
				
			/*	if( loginReq.getAttachedBranches()==null || loginReq.getAttachedBranches().size() == 0 ) {
				//	errors.add(new Error("06", "Attached Branch", "Please Choose Atleast One Branch"));
					errors.add("1767");
				} */
			
			}
			
			// Additional Errors
			if(StringUtils.isBlank(personalReq.getAddress1())  ) {
			//	errors.add(new Error("10", "Address1", "Please Enter Address1" ));
				errors.add("1768");
			} else if(personalReq.getAddress1().length()>100 ) {
			//	errors.add(new Error("10", "Address1", "Address1 Must Be Under 100 Character Only Allowed" ));
				errors.add("1769");
			}
			
			if(StringUtils.isBlank(personalReq.getAddress2())  ) {
			//	errors.add(new Error("11", "Address2", "Please Enter Address2" ));
				errors.add("1770");
			} else if(personalReq.getAddress2().length()>100 ) {
			//	errors.add(new Error("11", "Address2", "Address2 Must Be Under 100 Character Only Allowed" ));
				errors.add("1771");
			}
			
			if(StringUtils.isBlank(personalReq.getCountryCode())  ) {
			//	errors.add(new Error("18", "Country", "Please Select Country" ));
				errors.add("1772");
			}
		/*		
			if(StringUtils.isBlank(personalReq.getCityCode())  ) {
				errors.add(new Error("15", "City", "Please Select City" ));
			} else if(! personalReq.getCityCode().matches("[0-9]+")  ) {

			}
			*/
			if(StringUtils.isBlank(personalReq.getMobileCode())  ) {
			//	errors.add(new Error("29", "MobileCode", "Please Select MobileCode" ));
				errors.add("1773");
			} 
			
			if(StringUtils.isBlank(personalReq.getWhatsappCode())  ) {
			//	errors.add(new Error("29", "WhatsappCode", "Please Select WhatsappCode" ));
				errors.add("1774");
			} 
			
			if(StringUtils.isBlank(personalReq.getWhatsappNo())  ) {
			//	errors.add(new Error("29", "WhatsappNo", "Please Enter WhatsappNo" ));
				errors.add("1775");
			} else if(! personalReq.getWhatsappNo().matches("[0-9]+")  ) {
			//	errors.add(new Error("29", "WhatsappNo", "Please Enter Valid Number in WhatsappNo" ));
				errors.add("1776");
			} else if(personalReq.getWhatsappNo().length()>20  ) {
			//	errors.add(new Error("29", "WhatsappNo", "WhatsappNo Must Be Under 20 Characters Only Allowed" ));
				errors.add("1777");
			}
			

			if(StringUtils.isBlank(personalReq.getRemarks())  ) {
			//	errors.add(new Error("30", "Remarks", "Please Enter Remarks" ));
				errors.add("1778");
			} 
			else if(personalReq.getRemarks().length()>100  ) {
			//	errors.add(new Error("30", "Remarks", "Remarks Must Be Under 100 Characters Only Allowed" ));
				errors.add("1779");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
	//		errors.add(new Error("07", "Common Error", e.getMessage() ));
			return errors;
		}
		return errors;
	}
	
	
	@Override
	public List<String> validateUserCreation(UserCreationReq req) {
		List<String> errors = new ArrayList<String>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			
			// Common Errors
			CommonLoginCreationReq commonReq = new CommonLoginCreationReq();
			dozerMapper.map(req, commonReq);
			List<String>  commonErrors = basicValidation.commonLoginCreationValidation(commonReq) ;
			if(commonErrors.size()>0  ) {
				errors.addAll(commonErrors);
			}
			
			// Additional Broker Validataion
			AdditionalInfoReq  brokerReq = new AdditionalInfoReq();
			dozerMapper.map(req.getPersonalInformation() , brokerReq); 
			List<String>  brokerErrors = basicValidation.commonBrokerPersonalValidation(brokerReq) ;
			if(brokerErrors.size()>0  ) {
				errors.addAll(brokerErrors);
			}
			
			if(StringUtils.isBlank(req.getLoginInformation().getOaCode())  ) {
				//errors.add(new Error("06","Broker","Please Select Broker OaCode"));
				errors.add("1780");
			}
			if(StringUtils.isNotBlank(commonReq.getLoginInformation().getUserType())  && commonReq.getLoginInformation().getUserType().equalsIgnoreCase("Broker")  ) {
				if(StringUtils.isBlank(commonReq.getLoginInformation().getBrokerCompanyYn()))  {
				//	errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
					errors.add("1781");
				} else if( ! (commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("Y") || commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("N")) )  {
				//	errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
					errors.add("1781");
				}
			}
			
//			if(StringUtils.isBlank(brokerReq.getTaxExemptedYn())  ) {
//				errors.add(new Error("22", "TaxExempted", "Please Select Tax Exempted Yes/No" ));
//			}
			
			if(StringUtils.isNotBlank(brokerReq.getCreditLimit())  ) {
				if(! brokerReq.getCreditLimit().matches("[0-9.]+")  ) {
				//	errors.add(new Error("22", "CreditLimit", "Please Enter Valid Number in Credit Limit" ));
					errors.add("1782");
				}
			} 
			
			if(StringUtils.isBlank(brokerReq.getCustomerCode())  ) {
			//	errors.add(new Error("1783", "CustomerCode", "Please Select Customer Code" ));
				errors.add("1783");
				
			} 
//			if(StringUtils.isBlank(brokerReq.getRegulatoryCode())  ) {
//				errors.add(new Error("22", "RegulatoryCode", "Please Enter Regulatory Code" ));
//				
//			} else if (brokerReq.getRegulatoryCode().length() > 20  ) {
//				errors.add(new Error("22", "RegulatoryCode", "Regulatory Code Max 20 Characters only allowed" ));
//				
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
	//		errors.add(new Error("07", "Common Error", e.getMessage() ));
			return errors;
		}
		return errors;
	}
	
//***************************************Add Branch Apis Validations **********************************************************//	
	
	@Override
	public List<String> validateBrokerBranchReq(AttachCompaniesReq req) {
		List<String> errors = new ArrayList<String>();
		try {
			// Branch Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
		//		errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
				errors.add("1786");
			}
			if (StringUtils.isBlank(req.getBrokerCompanyYn())) {
			//	errors.add(new Error("02", "BrokerCompanyYn", "Please Enter BrokerCompanyYn"));
				errors.add("1787");
			}  
				
			if(req.getAttachedCompanies()==null || req.getAttachedCompanies().size()== 0 ) {
			//	errors.add(new Error("03", "Attached Companies", "Please select Atleast One  Company" ));
				errors.add("1788");
			} else {
				Long rowNo = 0L ;
				for(AttachedBranchesReq  data : req.getAttachedCompanies() ) {
					rowNo = rowNo + 1L ;
					if(StringUtils.isBlank(data.getInsuranceId()) ) {
				//		errors.add(new Error("03", "Insurance Id", "Please Select Atleast One Company" ));
						errors.add("1788" + "," + rowNo);
					}
					
					if(data.getAttachedBranches()==null || data.getAttachedBranches().size()== 0 ) {
					//	errors.add(new Error("03", "Attached Companies", "Please select Atleast One  Branch in Company Row No : " +  rowNo  ));
						errors.add("1789" + "," + rowNo);
					}
					for (BrokerBranchesReq data2 : data.getAttachedBranches()) {
						if (req.getBrokerCompanyYn().equals("N")) {
							if (StringUtils.isBlank(data2.getBranchCode())) {
						//		errors.add(new Error("1790", "Branch Code", "Please Enter Branch Code"));
								errors.add("1790" + "," + rowNo);
							} else if (req.getBrokerCompanyYn().equals("Y")) {
								if (data2.getBrokerBranchCode() == null || data2.getBrokerBranchCode().size() == 0) {
									//errors.add(new Error("04", "Broker Branch Code", "Please select Atleast One  Broker Branch Code in Row: "+rowNo));
									errors.add("1791" + "," + rowNo);
								}
							}
						}
					}
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}


	@Override
	public List<String> validateIssuerBranchReq(AttachIssuerBrannchReq req) {
		List<String> errors = new ArrayList<String>();
		try {
			// Branch Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
			//	errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
				errors.add("1786");
			}
			
			if(req.getAttachedCompanies()==null || req.getAttachedCompanies().size()== 0 ) {
			//	errors.add(new Error("02", "Attached Companies", "Please select Atleast One  Company" ));
				errors.add("1788");
			} else {
				Long rowNo = 0L ;
				for(AttacheIssuerBranchReq  data : req.getAttachedCompanies() ) {
					rowNo = rowNo + 1L ;
					if(StringUtils.isBlank(data.getInsuranceId()) ) {
					//	errors.add(new Error("01", "Insurance Id", "Please Select Atleast One Company" ));
						errors.add("1788" + "," + rowNo);
					}
					
					if(data.getAttachedBranches()==null || data.getAttachedBranches().size()== 0 ) {
					//	errors.add(new Error("02", "Attached Companies", "Please select Atleast One  Branch in Company Row No : " +  rowNo  ));
						errors.add("1789" + "," + rowNo);
					}
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}
	
//***************************************Add Broker Products Apis Validations **********************************************************//	

	@Override
	public List<String> validateBrokerProductReq(AttachCompnayProductRequest req) {
		List<String> errors = new ArrayList<String>();
		try {
			//Product Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
			//	errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
				errors.add("1786");
			}
			
			if(StringUtils.isBlank(req.getInsuranceId()) ) {
			//	errors.add(new Error("01", "InsuranceId", "Please Enter InsuranceId" ));
				errors.add("1255");
			}
			
			if(req.getProductIds()==null || req.getProductIds().size()== 0 ) {
			//	errors.add(new Error("02", "Product Ids", "Please select Atleast One Product" ));
				errors.add("1792");
			} 
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}
	
//***************************************Add Issuer Referal Apis Validations **********************************************************//	
	
	@Override
	public List<String> validateIssuerReferalReq(AttachIssuerReferalReq req) {
		List<String> errors = new ArrayList<String>();
		try {
			//Referal Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
			//	errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
				errors.add("1786");
			}
			
			if(StringUtils.isBlank(req.getInsuranceId()) ) {
			//	errors.add(new Error("02", "InsuranceId", "Please Enter InsuranceId" ));
				errors.add("1255");
				
			}
			if(StringUtils.isBlank(req.getBranchCode()) ) {
			//	errors.add(new Error("03", "BrnchCode", "Please Enter BranchCode" ));
				errors.add("1256");
			}
			
			if(req.getAttachedReferals()==null || req.getAttachedReferals().size()== 0 ) {
			//	errors.add(new Error("02", "Attached Referals", "Please select Atleast One  Referal" ));
				errors.add("1793");
			} else {
				Long referalRow  = 0L;
				boolean status = false ;
				for (AttachReferalReq referal :  req.getAttachedReferals() ) {
					referalRow = referalRow + 1L ;
					if(StringUtils.isBlank(referal.getReferalId())) {
				//		errors.add(new Error("02", "ReferalId", "Please Enter ReferalId in  Referal Row No : " +  referalRow  ));
						errors.add("1794" + "," + referalRow);
					}
					
					if(StringUtils.isBlank(referal.getReferalName())) {
					//	errors.add(new Error("02", "Referal Name", "Please Enter Referal Name in  Referal Row No : " +  referalRow  ));
						errors.add("1795" + "," + referalRow);
					}
					
					if(referal.getEffectiveDate()==null ) {
					//	errors.add(new Error("02", "Effective Date ", "Please select Effective Date  in  Referal Row No : " +  referalRow  ));
						errors.add("1796" + "," + referalRow);
					} else {
						Calendar cal = new GregorianCalendar();  
						Date today =  new Date(); 
						cal.setTime(today); cal.add(Calendar.DAY_OF_MONTH, -1); cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 50);
						today = cal.getTime();
						if(referal.getEffectiveDate().before(today) ) {
					//		errors.add(new Error("02", "Effective Date ", "Please Enter Future Date as Effective Date  in  Referal Row No : " +  referalRow ));
							errors.add("1797" + "," + referalRow);
						}
					}
					if(StringUtils.isBlank(referal.getStatus())) {
					//	errors.add(new Error("02", "Status", "Please Select Status in  Referal Row No : " +  referal ));
						errors.add("1798" + "," + referalRow);
						
					} else if (referal.getStatus().equalsIgnoreCase("Y") ) {
						status = true ;
					}
					if(StringUtils.isBlank(referal.getSumInsuredStart())) {
					//	errors.add(new Error("02", "Start Limit", "Please Enter Sum Insured Start in  Referal Row No : " +  referalRow ));
						errors.add("1799" + "," + referalRow);
					} else if (! referal.getSumInsuredStart().matches("[0-9]+") ) {
					//	errors.add(new Error("02", "Start Limit", "Please Enter Valid Number Sum Insured Start  in  Referal Row No : " +  referalRow ));
						errors.add("1800" + "," + referalRow);
					}
					if(StringUtils.isBlank(referal.getSumInsuredEnd())) {
					//	errors.add(new Error("02", "End Limit", "Please Enter Sum Insured End in  Referal Row No : " +  referalRow ));
						errors.add("1801" + "," + referalRow);
					} else if (! referal.getSumInsuredEnd().matches("[0-9]+") ) {
					//	errors.add(new Error("02", "End Limit", "Please Enter Valid Number Sum Insured End in  Referal Row No : " +  referalRow ));
						errors.add("1802" + "," + referalRow);
					} else if (StringUtils.isNotBlank(referal.getSumInsuredEnd()) && StringUtils.isBlank(referal.getSumInsuredEnd())  ) {
						if (Long.valueOf(referal.getSumInsuredEnd()) > Long.valueOf(referal.getSumInsuredStart()) ) {
					//		errors.add(new Error("02", "End Limit", "Sum Insured Start Greater Than Sum Insured End in  Referal Row No : " +  referalRow ));
							errors.add("1803" + "," + referalRow);
						}
					}
				}	
				
				if( status == false   ) {
				//	errors.add(new Error("02", "Status", "Please Select Active Status for Alteast One Referal " ));
					errors.add("1804" + "," + referalRow);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}


@Override
public List<String> validateBrokerCompanyBranchReq(AttachBrokerBranchReq req) {
	List<String> errors = new ArrayList<String>();
	try {
		
		if(StringUtils.isBlank(req.getLoginId()) ) {
//				errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
			errors.add("1786");
		}
		

		if(StringUtils.isBlank(req.getStatus()) && req.getStatus().equalsIgnoreCase("Y"))
		{
		if(checkBranch(req.getBranchCode(),req.getLoginId(),req.getCompanyId(),req.getBrokerBranchName()))
		{
		errors.add("2241");
		}}
			
		
		//Login  Data
//		LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
//		if (loginData.getBrokerCompanyYn() != null && !loginData.getBrokerCompanyYn().equals("N")) {
//			if (loginData.getBrokerCompanyYn().equals("Y") && loginData.getUserType().equalsIgnoreCase("BROKER")) {
//				if (StringUtils.isBlank(req.getBranchCode())) {
//					errors.add(new Error("03", "BranchCode", "Please Enter BranchCode"));
//				}
//				if (StringUtils.isBlank(req.getAttachedBranch())) {
//					errors.add(new Error("03", "AttachedBranchCode", "Please Enter AttachedBranchCode"));
//				}
//			}
//		}
		
		if (StringUtils.isBlank(req.getBranchCode())) {
		//	errors.add(new Error("03", "BranchCode", "Please Enter BranchCode"));
			errors.add("1256");
			
		}  else if ( StringUtils.isBlank(req.getBrokerBranchCode()) && StringUtils.isNotBlank(req.getSalePointCode()) && StringUtils.isNotBlank(req.getLoginId()) ) {
			List<LoginBranchMaster> list = getSalePointExistDetails(req.getSalePointCode() , req.getLoginId());
			if (list.size()>0 ) {
		//		errors.add(new Error("01", "SalePointCode", "This Company Sale Point Code Exist "));
				errors.add("1805");
			}
		} else if(  StringUtils.isNotBlank(req.getBrokerBranchCode())&& StringUtils.isNotBlank(req.getSalePointCode()) && StringUtils.isNotBlank(req.getLoginId()) ) {
			List<LoginBranchMaster> list =  getSalePointExistDetails(req.getSalePointCode() ,req.getLoginId() );
			if (list.size()>0 &&  (! req.getBrokerBranchCode().equalsIgnoreCase(list.get(0).getBrokerBranchCode().toString())) ) {
			//	errors.add(new Error("01", "SalePointCode", "This Sale Point Code  Already Exist "));
				errors.add("1806");
			}

		}
		
		if (StringUtils.isBlank(req.getBranchType())) {
	//		errors.add(new Error("03", "BranchType", "Please Enter BranchType"));
			errors.add("1807");
		}
		if(StringUtils.isBlank(req.getCompanyId()) ) {
		//	errors.add(new Error("02", "InsuranceId", "Please Enter InsuranceId" ));
			errors.add("1255");
		}
		if(req.getCompanyId().equals("100002")) {
			if (StringUtils.isBlank(req.getSalePointCode())) {
				//		errors.add(new Error("03", "SalePointCode", "Please Enter Sale Point Code"));
						errors.add("1808");
					} else if (req.getSalePointCode().length() > 100 ) {
					//	errors.add(new Error("03", "SalePointCode", "Sale Point Code Must be under 100 charecter only allowed"));
						errors.add("1809");
		  }
		}
		
		
//		if(StringUtils.isBlank(req.getCustomerCode()) ) {
//			errors.add(new Error("02", "CustomerCode", "Please Select CustomerCode" ));
//		}
		
//		if (StringUtils.isBlank(req.getBrokerBranchCode())) {
//			errors.add(new Error("03", "BrokerBranchCode", "Please Enter BrokerBranchCode"));
//		}
		
		if (StringUtils.isBlank(req.getBrokerBranchName())) {
//			errors.add(new Error("03", "BranchName", "Please Select Branch Name"));
			errors.add("1810");
	
		} else if (StringUtils.isBlank(req.getBrokerBranchCode()) &&  StringUtils.isNotBlank(req.getLoginId()) ) {
			List<LoginBranchMaster> list = getBrokerBranchNameExistDetails(req.getBrokerBranchName() , req.getLoginId());
			if (list.size()>0 ) {
			//	errors.add(new Error("01", "BranchName", "This Branch Name Already Exist "));
				errors.add("1811");
			}
		} else if(  StringUtils.isNotBlank(req.getLoginId())) {
			List<LoginBranchMaster> list =  getBrokerBranchNameExistDetails(req.getBrokerBranchName() ,req.getLoginId() );
			if (list.size()>0 &&  (! req.getBrokerBranchCode().equalsIgnoreCase(list.get(0).getBrokerBranchCode().toString())) ) {
			//	errors.add(new Error("01", "BranchName", "This Branch Name Already Exist "));
				errors.add("1811");
			}

		}
		
		
//		if(StringUtils.isBlank(req.getAttachedCompany()) ) {
//			errors.add(new Error("03", "AttachedComapany", "Please Enter AttachedComapany" ));
//		}

		//Status Validation
		if (StringUtils.isBlank(req.getStatus())) {
		//	errors.add(new Error("05", "Status", "Please Select Status  "));
			errors.add("1263");
		} else if (req.getStatus().length() > 1) {
		//	errors.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
			errors.add("1264");
		}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
		//	errors.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
			errors.add("1265");
		}
		
//		if (StringUtils.isBlank(req.getRemarks())) {
//			errors.add(new Error("03", "Remarks", "Please Enter Remarks"));
//		}
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 50);
		today = cal.getTime();
		if (req.getEffectiveDateStart() == null) {
		//	errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
			errors.add("1261");

		} else if (req.getEffectiveDateStart().before(today)) {
		//	errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			errors.add("1262");
		} 
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
	//	errors.add(new Error("09", "Common Error", e.getMessage() ));
	}
	return errors;
}
public boolean checkBranch(String branchcode,String login_id,String company_id,String brokerbranchname)
{
	List<LoginBranchMaster> findBranch=null;
if(StringUtils.isBlank(branchcode)||StringUtils.isBlank(login_id)||StringUtils.isBlank(brokerbranchname)||StringUtils.isBlank(company_id) )return false;

findBranch = loginBranchRepo. findByLoginIdAndCompanyIdAndBranchCodeAndBranchNameNotAndStatus(login_id,company_id,branchcode,brokerbranchname,"Y");
if(!findBranch.isEmpty()) return true;
return false;

}
public List<LoginBranchMaster> getBrokerBranchNameExistDetails(String brokerBranchName , String loginId ) {
	List<LoginBranchMaster> list = new ArrayList<LoginBranchMaster>();
	try {
		Date today = new Date();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LoginBranchMaster> query = cb.createQuery(LoginBranchMaster.class);

		// Find All
		Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);

		// Select
		query.select(b);

//		// Effective Date Max Filter
//		Subquery<Long> amendId = query.subquery(Long.class);
//		Root<LoginBranchMaster> ocpm1 = amendId.from(LoginBranchMaster.class);
//		amendId.select(cb.max(ocpm1.get("amendId")));
//		Predicate a1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
//		Predicate a2 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
//		Predicate a3 = cb.equal(ocpm1.get("brokerBranchCode"), b.get("brokerBranchCode"));
//		Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//		Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
//		amendId.where(a1,a2,a3,a4,a5);

	//	Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(cb.lower( b.get("brokerBranchName")), brokerBranchName.toLowerCase());
		Predicate n3 = cb.equal(cb.lower( b.get("loginId")), loginId);
		query.where(n2,n3);
		
		// Get Result
		TypedQuery<LoginBranchMaster> result = em.createQuery(query);
		list = result.getResultList();		
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());

	}
	return list;
}


public List<LoginBranchMaster> getSalePointExistDetails(String spCode , String loginId ) {
	List<LoginBranchMaster> list = new ArrayList<LoginBranchMaster>();
	try {
		Date today = new Date();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LoginBranchMaster> query = cb.createQuery(LoginBranchMaster.class);

		// Find All
		Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);

		// Select
		query.select(b);

//		// Effective Date Max Filter
//		Subquery<Long> amendId = query.subquery(Long.class);
//		Root<LoginBranchMaster> ocpm1 = amendId.from(LoginBranchMaster.class);
//		amendId.select(cb.max(ocpm1.get("amendId")));
//		Predicate a1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
//		Predicate a2 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
//		Predicate a3 = cb.equal(ocpm1.get("brokerBranchCode"), b.get("brokerBranchCode"));
//		Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//		Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
//		amendId.where(a1,a2,a3,a4,a5);

	//	Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n3 = cb.equal(b.get("salePointCode"),spCode );
		Predicate n4 = cb.equal(cb.lower( b.get("loginId")), loginId);
		query.where(n3,n4);
		
		// Get Result
		TypedQuery<LoginBranchMaster> result = em.createQuery(query);
		list = result.getResultList();		
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());

	}
	return list;
}

@Override
public List<String> validateLoginBranches(LoginBranchesSaveReq req) {
	List<String> errors = new ArrayList<String>();
	try {
		//Product Validation
		if(StringUtils.isBlank(req.getLoginId()) ) {
		//	errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
			errors.add("1786");
		}
		
		if(StringUtils.isBlank(req.getInsuranceId()) ) {
		//	errors.add(new Error("02", "InsuranceId", "Please Select InsuranceId" ));
			errors.add("1255");
		}
		
		if(req.getBrokerBranchIds()==null || req.getBrokerBranchIds().size()== 0 ) {
		//	errors.add(new Error("03", "Branch Ids", "Please select Atleast One  Branch " ));
			errors.add("1812");
		} 
		
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
	//	errors.add(new Error("09", "Common Error", e.getMessage() ));
	}
	return errors;
}


@Override
public List<String> validateIssuerProductReq(AttachIssuerProductRequest req) {
	// TODO Auto-generated method stub
	List<String> errors = new ArrayList<String>();
	try {
		//Product Validation
		if(StringUtils.isBlank(req.getLoginId()) ) {
		//	errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
			errors.add("1786");
		}
		
		if(StringUtils.isBlank(req.getInsuranceId()) ) {
		//	errors.add(new Error("01", "InsuranceId", "Please Enter InsuranceId" ));
			errors.add("1255");
		}
			
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
	//	errors.add(new Error("09", "Common Error", e.getMessage() ));
	}
	return errors;
}


@Override
public List<String> validateProductsEndtIds(AttachEndtIdsReq req) {
	// TODO Auto-generated method stub
		List<String> errors = new ArrayList<String>();
		try {
			//Product Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
			//	errors.add(new Error("01", "LoginId", "Please Enter LoginId" ));
				errors.add("1786");
			}
			
			if(StringUtils.isBlank(req.getInsuranceId()) ) {
			//	errors.add(new Error("01", "InsuranceId", "Please Enter InsuranceId" ));
				errors.add("1255");
			}
				
			if(StringUtils.isBlank(req.getProductId()) ) {
		//		errors.add(new Error("1313", "ProductId", "Please Enter ProductId" ));
				errors.add("00");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}


	


}
