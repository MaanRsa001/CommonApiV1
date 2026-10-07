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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.req.AdditionalInfoReq;
import com.maan.eway.admin.req.CommonLoginCreationReq;
import com.maan.eway.admin.req.CommonLoginInformationReq;
import com.maan.eway.admin.req.CommonPersonalInforReq;
import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CityMasterRepository;
import com.maan.eway.repository.CountryMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.StateMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class BasicLoginValidationService {
	
	private Logger log=LogManager.getLogger(BasicLoginValidationService.class);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@Autowired
	private CityMasterRepository cityRepo;
	
	@Autowired
	private StateMasterRepository stateRepo;
	
	@Autowired
	private CountryMasterRepository countryRepo;
	
	@Autowired
	private LoginUserInfoRepository userinfoRepo;
	
	@Autowired
	private InsuranceCompanyMasterRepository companyMasterRepo;
	

	
	
	
	public List<String>  commonLoginCreationValidation(CommonLoginCreationReq req ) {
		List<String> errors = new ArrayList<String>();
		try {
			// Login Validation
			List<Error> list = new ArrayList<Error>();
			CommonLoginInformationReq loginReq = req.getLoginInformation() ;
			
			if(!StringUtils.isBlank(req.getLoginInformation().getPassword()))
			{
			if (!passwordvaildation(req.getLoginInformation().getPassword(),req.getLoginInformation().getCompanyId())) {
			
				String errormsg=geterrormsg(req.getLoginInformation().getCompanyId());
					
				//errors.add(new Error("","New Password",errormsg));
				//errors.add("2257");
				errors.add("555-"+errormsg);
	         }}
			
			
			if (StringUtils.isBlank(loginReq .getCreatedBy())) {
			//	errors.add(new Error("01", "Created By", "Please Enter Created By"));
				errors.add("1270");
			}
			
			
			if (StringUtils.isBlank(loginReq.getLoginId())) {
		//		errors.add(new Error("02", "Login Id", "Please Enter Login Id"));
				errors.add("1723");
				
			} 
			else if(StringUtils.isNotBlank(loginReq.getLoginId()) )
			{
			if(loginReq.getLoginId().contains(" "))
			{
				errors.add("2281");
			}
			}
			else if (loginReq.getLoginId().length() > 50 || loginReq.getLoginId().length() < 5  ) {
			//	errors.add(new Error("02", "Login Id", "Login Id Under 5 - 50 Characters Only Allowed"));
				errors.add("1724");
			} 
			if( StringUtils.isBlank(loginReq.getAgencyCode())) {
				if (StringUtils.isBlank(loginReq.getPassword())) {
		//			errors.add(new Error("04", "Password", "Please Enter Password"));
					errors.add("1725");
				} else if (loginReq.getPassword().length() > 50) {
		//			errors.add(new Error("03", "PassWord", "Password Must Be Under 50 Characters Only Allowed"));
					errors.add("1726");
				}
			}
			
			

			//Status Validation
			if (StringUtils.isBlank(loginReq.getStatus())) {
		//		errors.add(new Error("05", "Status", "Please Select Status  "));
				errors.add("1263");
			} else if (loginReq.getStatus().length() > 1) {
			//	errors.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allowed"));
				errors.add("1264");
			}else if(!("Y".equalsIgnoreCase(loginReq.getStatus())||"N".equalsIgnoreCase(loginReq.getStatus())||"R".equalsIgnoreCase(loginReq.getStatus())|| "P".equalsIgnoreCase(loginReq.getStatus()))) {
		//		errors.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errors.add("1265");
			}
			
			if(StringUtils.isNotBlank(loginReq.getSubUserType() )  && "SuperAdmin".equalsIgnoreCase(loginReq.getSubUserType())) {
				if (loginReq.getAttachedCompanies()== null || loginReq.getAttachedCompanies().size()<=0 ) {
			//		errors.add(new Error("05", "Companies", "Please Select Companies"));
					errors.add("1727");
				}
				
			} else 	if (StringUtils.isBlank(loginReq.getCompanyId())) {
			//	errors.add(new Error("05", "InsuranceId", "Please Select InsuranceId"));
				errors.add("1255");
			}
			
			if( StringUtils.isBlank(loginReq.getUserType()) ) {
			//	errors.add(new Error("05", "UserType", "Please Select UserType"));
				errors.add("1728");
			} else if (loginReq.getUserType().length() > 20 ) {
			//	errors.add(new Error("05", "UserType", "UserType Under 20 Characters Only Allowed"));
				errors.add("1729");
				
			} else if (StringUtils.isBlank(loginReq.getSubUserType())) {
			//	errors.add(new Error("05", "Sub UserType", "Please Select Sub UserType"));
				errors.add("1730");
			} else if (loginReq.getSubUserType().length() > 20 ) {
			//	errors.add(new Error("05", "Sub UserType", "Sub UserType Under 20 Characters Only Allowed"));
				errors.add("1731");
			} else if (loginReq.getSubUserType().equalsIgnoreCase("bank") && StringUtils.isBlank(loginReq.getBankCode()) ) {
			//	errors.add(new Error("05", "Bank Code", "Please Select Bank Code"));
				errors.add("1732");
				
			}else if ( loginReq.getUserType().equalsIgnoreCase("User") ) {
				LoginMaster  loginData = loginRepo.findByLoginId(loginReq.getLoginId());
				if(loginData!=null ) {
					if (StringUtils.isBlank( loginReq.getAgencyCode()) ) {
			//			errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
						errors.add("1733");
					} else if(! loginReq.getAgencyCode().equalsIgnoreCase(loginData.getAgencyCode() ) ) {
				//		errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
						errors.add("1733");
					}
				} 
			} else {
				
				LoginMaster  loginData = loginRepo.findByLoginId(loginReq.getLoginId());
				if(loginData!=null ) {
					if (StringUtils.isBlank( loginReq.getOaCode()) ) {
				//		errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
						errors.add("1733");
					} else if(! loginReq.getOaCode().equalsIgnoreCase(loginData.getOaCode().toString() ) ) {
				//		errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
						errors.add("1733");
					}
				} 
			}
			
			// Effective Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (loginReq.getEffectiveDateStart()==null) {
		//		errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
				errors.add("1261");

			} else if (loginReq.getEffectiveDateStart().before(today)) {
		//		errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date  "));
				errors.add("1262");
			}
			
			if(StringUtils.isNotBlank(loginReq.getSubUserType() ) && ! loginReq.getSubUserType().equalsIgnoreCase("bank") &&   StringUtils.isNotBlank(loginReq.getBankCode())   ) {
		//		errors.add(new Error("06","BankCode","You Can't Enter BankCode"));
				errors.add("1734");
			}
		/*	if( loginReq.getAttachedBranches()==null || loginReq.getAttachedBranches().size() == 0 ) {
				errors.add(new Error("06", "Attached Branch", "Please Choose Atleast One Branch"));
			} 
			if( loginReq.getAttachedRegions()==null || loginReq.getAttachedRegions().size() == 0 ) {
				errors.add(new Error("06", "Attached Region", "Please Choose Atleast One Region"));
			}
			if( loginReq.getAttachedCompanies()==null || loginReq.getAttachedCompanies().size() == 0 ) {
				errors.add(new Error("06", "Attached Branch", "Please Choose Atleast One Branch"));
			} */
			
			
			// Personal Info Validation
			CommonPersonalInforReq personalReq = req.getPersonalInformation() ; 
			
		
			
		/*	CityMaster countCity=cityRepo.findByCityIdAndStatus(personalReq.getCityCode(),"Y")
			if( StringUtils.isBlank(personalReq.getCityCode()) ) {
				errors.add(new Error("0", "CityCode", "Please Enter City Code"));
			}else if(countCity < 0) {
				errors.add(new Error("0", "CityCode", "Please Enter Valid City Code"));
			} */ 
			
			
			//if ( loginReq.getUserType().equalsIgnoreCase("Broker") || loginReq.getUserType().equalsIgnoreCase("User") ) {
			if ( loginReq.getUserType().equalsIgnoreCase("Broker") && isCustomerCodeValidationRequired(loginReq.getCompanyId())) {	
				if( StringUtils.isBlank(personalReq.getCustomerCode()) ) {
			//		errors.add(new Error("06", "Customer Code", "Please Enter Customer Code"));
					errors.add("1735");
				} else
				
				if (StringUtils.isNotBlank(loginReq.getLoginId()) && isCustomerCodeValidationRequired(loginReq.getCompanyId())) {
					List<InsuranceCompanyMaster> insuranceList = companyMasterRepo
							.findTopByCompanyIdOrderByAmendIdDesc(loginReq.getCompanyId());
					if (insuranceList != null && !insuranceList.isEmpty()) {
						String companyName = insuranceList.get(0).getCompanyName();
						List<LoginUserInfo> loginData = userinfoRepo
								.findByCustomerCodeAndCompanyName(personalReq.getCustomerCode(), companyName);

						if (loginData != null && loginData.size() > 0) {
							System.out.println("Customer Creation validate already Exist for This customerCode"
									+ personalReq.getCustomerCode());
							if (!loginData.get(0).getLoginId().equalsIgnoreCase(loginReq.getLoginId())) {
								// errors.add(new Error("06", "Customer Code", "Already One Login Id Exists For
								// This Customer Code"));
								errors.add("1736");
							}
						}
					}
				}
				
			}
			
			
			if( StringUtils.isBlank(personalReq.getUserMail()) ) {
			//	errors.add(new Error("06", "User Mail", "Please Select User Mail"));
				errors.add("1737");
			} else if (personalReq.getUserMail().length() > 50  ) {
			//	errors.add(new Error("06", "User Mail", "Mail Under Must Be 50 Characters Only Allowed"));
				errors.add("1738");
			}  else if( isNotValidMail(personalReq.getUserMail()) ){
		//		errors.add(new Error("08", "User Mail", "Please Enter Valid User Mail"));
				errors.add("1739");
			}  else if(StringUtils.isBlank( loginReq.getAgencyCode()) && loginReq.getUserType().equalsIgnoreCase("User") ) {
					if(existingMailCheck(personalReq.getUserMail(),req.getLoginInformation().getCompanyId())) {
						errors.add("2204");
					}
			} else if(StringUtils.isNotBlank( loginReq.getAgencyCode())  && loginReq.getUserType().equalsIgnoreCase("User") ){
				if(isEmailNotSame(loginReq.getLoginId(),personalReq.getUserMail())){
					if(existingMailCheck(personalReq.getUserMail(),req.getLoginInformation().getCompanyId())) {
						errors.add("2204");
					}
				}
			}
			
			
			if( StringUtils.isBlank(personalReq.getUserMobile()) ) {
			//	errors.add(new Error("07", "User Mobile", "Please Enter User Mobile"));
				errors.add("1740");
			} else if (personalReq.getUserMobile().length() > 20 ) {
			//	errors.add(new Error("07", "User Mobile", "User Mobile Must Be Under 20 Number Only Allowed"));
				errors.add("1741");
			} else if (! personalReq.getUserMobile().matches("[0-9]+") ) {
			//	errors.add(new Error("07", "User Mobile", "User Mobile Must Be Under 20 Number Only Allowed"));
				errors.add("1742");
			}
			
			if( StringUtils.isBlank(personalReq.getUserName()) ) {
		//		errors.add(new Error("08", "User Name", "Please Enter User Name"));
				errors.add("1743");
			} else if (personalReq.getUserName().length() > 500 ) {
		//		errors.add(new Error("08", "User Name ", "User Name Must Be Under 500 Characters Only Allowed"));
				errors.add("1744");
			}
//			else if (isNotValidName(personalReq.getUserName()) ) {
//				errors.add(new Error("08", "User Name ", "Please Enter Valid User Name"));
//			} 
			 
//			if( StringUtils.isBlank(personalReq.getCityName()) ) {
//				errors.add(new Error("09", "City Name", "Please Enter City Name"));
//			} else if (personalReq.getCityName().length() > 100 ) {
//				errors.add(new Error("09", "City Name ", "City Name Must Be Under 100 Characters Only Allowed"));
//			}
			if( StringUtils.isBlank(personalReq.getCityName()) ) {
			//	errors.add(new Error("09", "District", "Please Enter District"));
				errors.add("1745");
			} else if (personalReq.getCityName().length() > 100 ) {
			//	errors.add(new Error("09", "District ", "District Must Be Under 100 Characters Only Allowed"));
				errors.add("1746");
			}
			
			if ( loginReq.getUserType().equalsIgnoreCase("User") ) {
				if(StringUtils.isBlank(personalReq.getIdType())){
					errors.add("2210");
				}else if(StringUtils.isBlank(personalReq.getIdNumber())) {
					errors.add("2211");
				}else if(StringUtils.isBlank( loginReq.getAgencyCode()) ) {
					  if(checkIdNumberIsDuplicate(personalReq.getIdType(),personalReq.getIdNumber(),req.getLoginInformation().getCompanyId())) {
						  errors.add("2212");
					    }
				}else if(StringUtils.isNotBlank( loginReq.getAgencyCode())) {
					if(isIdNumberNotSame(loginReq.getLoginId(),personalReq.getIdNumber(),personalReq.getIdType())) {
						if(checkIdNumberIsDuplicate(personalReq.getIdType(),personalReq.getIdNumber(),req.getLoginInformation().getCompanyId())) {
							  errors.add("2212");
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
	
	private boolean isCustomerCodeValidationRequired(String companyId) {
	    return !"100055".equals(companyId);
	}
	
	public String geterrormsg(String Loginid)
	{
		String character=null,symbols=null,error="",max=null,min=null;
		int numbg=-1,numend=-1;
	try {
      if (Loginid != null) {
	
	  String company_id = Loginid;
	  List<InsuranceCompanyMaster> req1 = companyMasterRepo.findTopByCompanyIdOrderByAmendIdDesc(company_id);
	  if(req1!=null)
	  {
		  InsuranceCompanyMaster req = req1.stream()
  			    .filter(record -> "Y".equalsIgnoreCase(record.getPatternstatus() != null ? record.getPatternstatus().trim() : null))
  			    .findFirst()
  			    .orElse(null);

		 if(req!=null) {
		  character=req.getAlphabet();
		  numbg=req.getNumericDigitsStart()==null?numbg:Integer.valueOf(req.getNumericDigitsStart());
		  numend=req.getNumericDigitsEnd()==null?numbg:Integer.valueOf(req.getNumericDigitsEnd());
		  symbols=req.getSymbols();
		 max =req.getTotalmax();
		  min=req.getTotalmin();
		  if(character!=null && numbg!=-1 && numend!=-1 && symbols!=null && max!= null && min!=null)
		    error ="The password should contains character "+req.getAlphabet()+", numberic digit from "+numbg+" to "+numend+", symbols should contain "+ symbols +" , minimum password length is "+min +" and  maximum Password length is "+max +"...";
		
		  else   error ="The passwords should contain a combination of characters and password length between 5 to 20 characters  long.."; 
		   
		 }
		  else error ="The passwords should contain a combination of characters and password length between 5 to 20 characters  long.."; 
		   
	  }
	  else  error ="The passwords should contain a combination of characters and password length between 5 to 20 characters  long...";   
	  
			}
		} catch (Exception ex) {
			ex.getMessage();
			return "Please Enter Valid Password";
		}
	return error;
	}
	public boolean  passwordvaildation(String Password, String company_id)
	{
		try {
		String Pattern="";
		List<InsuranceCompanyMaster> req1= companyMasterRepo.findTopByCompanyIdOrderByAmendIdDesc(company_id);
		
		System.out.print("Req details========"+req1);
	     if(req1!=null && !req1.isEmpty())
	     {
	    	 InsuranceCompanyMaster req = req1.stream()
	    			    .filter(record -> "Y".equalsIgnoreCase(record.getPatternstatus() != null ? record.getPatternstatus().trim() : null))
	    			    .findFirst()
	    			    .orElse(null);; 

	    	 if(req!=null) {
	    			int lengthmin=5;
	    			int max=20;
	        if(!StringUtils.isBlank(req.getTotalmin()) &&!StringUtils.isBlank(req.getTotalmax()))
	        {
	        	lengthmin =Integer.valueOf(req.getTotalmin())-1;
	        	 max=Integer.valueOf(req.getTotalmax())-1;
	        }
	        
	        	
	    	String collect2=!StringUtils.isBlank(req.getAlphabet()) ?req.getAlphabet(): "";
	       String aplhabet= !StringUtils.isBlank(req.getAlphabet()) ? "(?=.*["+req.getAlphabet()+"])" : ""; //^(?=.*[a-zA-Z])or null
	       if(aplhabet.equals("(?=.*[A-Za-z])"))
	       {
	    	   aplhabet="(?=.*[A-Za-z])(?=.*[A-Z])(?=.*[a-z])" ;//(?=.*[A-Za-z])(?=.*[a-z])
	       }
	       String   number=!StringUtils.isBlank(req.getNumericDigitsStart()) && !StringUtils.isBlank(req.getNumericDigitsEnd())? req.getNumericDigitsStart()+"-"+req.getNumericDigitsEnd() : "";//0-9
		  	   String	symbols=!StringUtils.isBlank(req.getSymbols()) ? req.getSymbols() :"";//@#$%^&+=!
	  	   String length=!StringUtils.isBlank(req.getTotalmin())&&!StringUtils.isBlank(req.getTotalmax()) ? "{"+lengthmin+","+max+"}" :"";//{8,10}	
	       String collect="["+collect2+number+symbols+"^]";//
	      Pattern ="^"+(aplhabet)+("(?=.*["+number+"])")+("(?=.*["+symbols+"^])")+("["+collect2+"]")+(collect)+(length)+"$";//^(?=.*[A-Za-z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#^])[A-Za-z][A-Za-z0-9@#^]{7,19}$
				
	  	
	    	 }else Pattern=null;
	     }
		if(StringUtils.isBlank(Pattern)||company_id==null||Pattern.equals("^[]$"))
		 {
					 Pattern="^(?=\\S+$).{5,20}"; 
		 }
	     System.out.print("Generated Pattern is ----------->"+Pattern);
	    if(Password.matches(Pattern))
		   {
			   return true;
		   }
		}
		catch(Exception ss)
		{
			System.out.println("**************Exception in password Expression gneration*****************");
			ss.printStackTrace();
			System.out.print(ss.getMessage());
			
		}
       return false;
	}



	public Long getCountryCount(String countryId) {
		Long countryCount = 0L ;
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<CountryMaster> c = query.from(CountryMaster.class);
			
			// Country Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CountryMaster> ocpm1 = effectiveDate.from(CountryMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate c1 = cb.equal(ocpm1.get("countryId"), c.get("countryId"));
			Predicate c2 = cb.equal(ocpm1.get("status"),c.get("status"));
			Predicate c3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(c1,c2,c3);
			
			Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(c.get("countryId"), countryId);
			Predicate n3 = cb.equal(c.get("status"), "Y");
			
			// Select
			query.multiselect( cb.count(c) );
			
			query.where(n1,n2,n3);
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			if( list.size()>0) {
				countryCount = list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return countryCount;
	}
	
	public Long getStateCount(String countryId, String stateId ) {
		Long stateCount = 0L ;
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<StateMaster> s = query.from(StateMaster.class);
			
			// State Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<StateMaster> ocpm1 = effectiveDate.from(StateMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate c1 = cb.equal(ocpm1.get("countryId"), s.get("countryId"));
			Predicate c2 = cb.equal(ocpm1.get("status"),s.get("status"));
			Predicate c3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate c4 = cb.equal(ocpm1.get("stateId"), s.get("stateId"));
			effectiveDate.where(c1,c2,c3,c4);
			
			Predicate n1 = cb.equal(s.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(s.get("countryId"), countryId);
			Predicate n3 = cb.equal(s.get("stateId"), stateId);
			Predicate n4 = cb.equal(s.get("status"), "Y");
			
			// Select
			query.multiselect( cb.count(s) );
			
			query.where(n1,n2,n3,n4);
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			if( list.size()>0) {
				stateCount = list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return stateCount;
	}
	
	public Long getCityCount(String countryId,String cityId) {
		Long cityCount = 0L ;
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<CityMaster> c = query.from(CityMaster.class);
			
			// City Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CityMaster> ocpm1 = effectiveDate.from(CityMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate c1 = cb.equal(ocpm1.get("countryId"), c.get("countryId"));
			Predicate c2 = cb.equal(ocpm1.get("status"),c.get("status"));
			Predicate c3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate c4 = cb.equal(ocpm1.get("stateId"), c.get("stateId"));
			Predicate c5 = cb.equal(ocpm1.get("cityId"), c.get("cityId"));
			effectiveDate.where(c1,c2,c3,c4,c5);
			
			Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(c.get("countryId"), countryId);
			Predicate n4 = cb.equal(c.get("cityId"), cityId );
			Predicate n5 = cb.equal(c.get("status"), "Y");
			
			// Select
			query.multiselect( cb.count(c) );
			
			query.where(n1,n2,n4,n5);
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			if( list.size()>0) {
				cityCount = list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return cityCount;
	}
	
	
	public List<String>  commonBrokerPersonalValidation(AdditionalInfoReq brokerReq ) {
		List<String> errors = new ArrayList<String>();
		try {
			// Login Validation
			if(StringUtils.isBlank(brokerReq.getAcExecutiveId())  ) {
		//		errors.add(new Error("09", "Ac Excutive Id", "Please Enter AcExcutiveId" ));
				errors.add("1747");
			} else if(! brokerReq.getAcExecutiveId().matches("[0-9]+")  ) {
			//	errors.add(new Error("09", "Ac Excutive Id", "Please Enter Valid Number AcExcutiveId" ));
				errors.add("1748");
			}
			
//			if(StringUtils.isBlank(brokerReq.getAddress1())  ) {
//				errors.add(new Error("10", "Address1", "Please Enter Address1" ));
//			} else if(brokerReq.getAddress1().length()>100 ) {
//				errors.add(new Error("10", "Address1", "Address1 Must Be Under 100 Character Only Allowed" ));
//			}
//			
//			if(StringUtils.isBlank(brokerReq.getAddress2())  ) {
//				errors.add(new Error("11", "Address2", "Please Enter Address2" ));
//			} else if(brokerReq.getAddress2().length()>100 ) {
//				errors.add(new Error("11", "Address2", "Address2 Must Be Under 100 Character Only Allowed" ));
//			}
//			
//			if(StringUtils.isBlank(brokerReq.getAddress3())  ) {
//				errors.add(new Error("12", "Address3", "Please Enter Address3" ));
//			} else if(brokerReq.getAddress3().length()>100 ) {
//				errors.add(new Error("12", "Address3", "Address3 Must Be Under 100 Character Only Allowed" ));
//			}
			
			if(StringUtils.isBlank(brokerReq.getApprovedPreparedBy())  ) {
		//		errors.add(new Error("13", "ApprovedPreparedBy", "Please Enter Approved Prepared By" ));
				errors.add("1749");
			} else if(brokerReq.getApprovedPreparedBy().length()>30 ) {
		//		errors.add(new Error("13", "ApprovedPreparedBy", "ApprovedPreparedBy Must Be Under 30 Character Only Allowed" ));
				errors.add("1750");
			}
			
//			if(StringUtils.isBlank(brokerReq.getDesignation())  ) {
//				errors.add(new Error("17", "Designation", "Please Enter Designation" ));
//			}else if(brokerReq.getDesignation().length()>100 ) {
//				errors.add(new Error("17", "Designation", "Designation Must Be Under 100 Character Only Allowed" ));
//			} 
			
			
			if(StringUtils.isBlank(brokerReq.getFax())  ) {
		//		errors.add(new Error("22", "Fax", "Please Select Fax" ));
				errors.add("1751");
			} else if(brokerReq.getFax().length() > 50  ) {
		//		errors.add(new Error("22", "Fax", "Fax Must Be Under 50 Character Only Allowed" ));
				errors.add("1752");
			}
			
		
			
			// Yn Validation
//			if (StringUtils.isBlank(brokerReq.getMakerYn())) {
//				errors.add(new Error("23", "Maker", "Please Select Maker Y or N"));
//			} else if (!("Y".equals(brokerReq.getMakerYn()) || "N".equals(brokerReq.getMakerYn()))) {
//				errors.add(new Error("23", "Maker", "Please Select Maker Y or N"));
//			}
//			
//			if (StringUtils.isBlank(brokerReq.getCheckerYn())) {
//				errors.add(new Error("24", "CheckerYn", "Please Select Checker Y or N"));
//			} else if (!("Y".equals(brokerReq.getCheckerYn()) || "N".equals(brokerReq.getCheckerYn()))) {
//				errors.add(new Error("24", "CheckerYn", "Please Select Checker Y or N"));
//			}
			
//			if (StringUtils.isBlank(brokerReq.getCommissionVatYn())) {
//				errors.add(new Error("25", "CommissionVat", "Please Select CommissionVat Y or N"));
//			} else if (!("Y".equals(brokerReq.getCommissionVatYn()) || "N".equals(brokerReq.getCommissionVatYn()))) {
//				errors.add(new Error("25", "CommissionVat", "Please Select CommissionVat Y or N"));
//			} else if ("Y".equals(brokerReq.getCommissionVatYn())) {
//				if(StringUtils.isBlank(brokerReq.getVatRegNo())  ) {
//					errors.add(new Error("29", "VatRegNo", "Please Select VatRegNo" ));
//				} else if(brokerReq.getVatRegNo().length()>100  ) {
//					errors.add(new Error("29", "VatRegNo", "VatRegNo Must Be Under 100 Characters Only Allowed" ));
//				}
//			}
			
//			if (StringUtils.isBlank(brokerReq.getCustConfirmYn())) {
//				errors.add(new Error("26", "Customer Confirm ", "Please Select Customer Confirm Y or N"));
//			} else if (!("Y".equals(brokerReq.getCustConfirmYn()) || "N".equals(brokerReq.getCustConfirmYn()))) {
//				errors.add(new Error("26", "Customer Confirm ", "Please Select Customer Confirm  Y or N"));
//			}
			
			
//			if(StringUtils.isBlank(brokerReq.getPobox())  ) {
//				errors.add(new Error("25", "Post Box No", "Please Enter Post Box No" ));
//			} else if(! brokerReq.getPobox().matches("[0-9]+")  ) {
//				errors.add(new Error("25", "Post Box No", "Please Enter Valid Number In Post Box No" ));
//			}
			
//			if(StringUtils.isBlank(brokerReq.getRemarks())  ) {
//				errors.add(new Error("26", "Remarks", "Please Enter Remarks" ));
//			} else if(brokerReq.getRemarks().length()>100  ) {
//				errors.add(new Error("26", "Remarks", "Remarks Must Be Under 100 Characters Only Allowed" ));
//			}
			
			if(StringUtils.isBlank(brokerReq.getCoreAppBrokerCode())  ) {
		//		errors.add(new Error("27", "CoreAppBrokerCode", "Please Enter CoreAppBrokerCode" ));
				errors.add("1753");
			} else if(brokerReq.getCoreAppBrokerCode().length()>100  ) {
		//		errors.add(new Error("27", "CoreAppBrokerCode", "RsaBrokerCode Must Be Under 100 Characters Only Allowed" ));
				errors.add("1754");
			}
			Date today = new Date();
			if(StringUtils.isBlank(brokerReq.getCountryCode())  ) {
		//		errors.add(new Error("18", "Country", "Please Select Country" ));
				errors.add("1755");
			}
			/*else if(! brokerReq.getCountryCode().matches("[0-9]+")  ) {
				errors.add(new Error("1756", "Country", "Please Enter Valid Number In Country" ));
			} 
			*/else {
				Long countryCount  = getCountryCount(brokerReq.getCountryCode());// countryRepo.countByCountryIdAndStatusAndEffectiveDateStartLessThanEqual(Integer.valueOf(brokerReq.getCountryCode()),"Y", today );
				if(countryCount <=0 ) {
			//		errors.add(new Error("18", "Country", "Please Select Valid Country" ));
					errors.add("1757");
				}
			}
			
		/*	if(StringUtils.isBlank(brokerReq.getStateCode())  ) {
				errors.add(new Error("28", "State", "Please Select State" ));
			} else if(! brokerReq.getStateCode().matches("[0-9]+")  ) {
				errors.add(new Error("18", "Country", "Please Enter Valid Number In Country" ));
			}else if(StringUtils.isNotBlank(brokerReq.getCountryCode()) &&   brokerReq.getCountryCode().matches("[0-9]+")  ){
	
				Long stateCount  = getStateCount( brokerReq.getCountryCode(),brokerReq.getStateCode() );//stateRepo.countByStateIdAndCountryIdAndStatusAndEffectiveDateStartLessThanEqual(Integer.valueOf(brokerReq.getStateCode()) , Integer.valueOf(brokerReq.getCountryCode()),"Y", today );
				if(stateCount <=0 ) {
					errors.add(new Error("18", "State", "Please Select Valid State" ));
				}
			} */
			
//			if(StringUtils.isBlank(brokerReq.getUserName())  ) {
//				errors.add(new Error("29", "UserName", "Please Enter UserName" ));
//			} else if(brokerReq.getUserName().length()>100  ) {
//				errors.add(new Error("29", "UserName", "UserName Must Be Under 100 Characters Only Allowed" ));
//			}
//			
//			if(StringUtils.isBlank(brokerReq.getUserMail())  ) {
//				errors.add(new Error("29", "UserMail", "Please Enter UserName" ));
//			} else if(brokerReq.getUserMail().length()>100  ) {
//				errors.add(new Error("29", "UserMail", "UserMail Must Be Under 100 Characters Only Allowed" ));
//			} else if( isNotValidMail(brokerReq.getUserMail()) == true ) {
//				errors.add(new Error("29", "UserMail", "UserMail Is Not Valid Mail" ));
//			}
			/*
			if(StringUtils.isBlank(brokerReq.getCityCode())  ) {
				errors.add(new Error("15", "City", "Please Select City" ));
			} else if(! brokerReq.getCityCode().matches("[0-9]+")  ) {
			
			}else if(StringUtils.isNotBlank(brokerReq.getCountryCode()) &&   brokerReq.getCountryCode().matches("[0-9]+")  ){
	
				Long cityCount  = getCityCount(brokerReq.getCountryCode() , brokerReq.getCityCode());//cityRepo.countByCityIdAndStateIdAndCountryIdAndStatusAndEffectiveDateStartLessThanEqual(Integer.valueOf(brokerReq.getCityCode()) , Integer.valueOf(brokerReq.getStateCode()) , Integer.valueOf(brokerReq.getCountryCode()),"Y", today );
				if(cityCount  <=0 ) {
					errors.add(new Error("18", "City", "Please Select Valid City" ));
				}
			} 
			*/
			
			
			
						
//			if(StringUtils.isBlank(brokerReq.getContactPersonName())  ) {
//				errors.add(new Error("29", "ContactPersonName", "Please Enter ContactPersonName" ));
//			} else if(brokerReq.getContactPersonName().length()>100  ) {
//				errors.add(new Error("29", "ContactPersonName", "ContactPersonName Must Be Under 100 Characters Only Allowed" ));
//			}
			
			if(StringUtils.isBlank(brokerReq.getMobileCode())  ) {
		//		errors.add(new Error("29", "MobileCode", "Please Select MobileCode" ));
				errors.add("1758");
			} 
			
			
//			if(StringUtils.isBlank(brokerReq.getWhatsappCode())  ) {
//				errors.add(new Error("29", "WhatsappCode", "Please Select WhatsappCode" ));
//			} 
//			
//			if(StringUtils.isBlank(brokerReq.getWhatsappNo())  ) {
//				errors.add(new Error("29", "WhatsappNo", "Please Enter WhatsappNo" ));
//			} else if(! brokerReq.getWhatsappNo().matches("[0-9]+")  ) {
//				errors.add(new Error("29", "WhatsappNo", "Please Enter Valid Number in WhatsappNo" ));
//			} else if(brokerReq.getWhatsappNo().length()>20  ) {
//				errors.add(new Error("29", "WhatsappNo", "WhatsappNo Must Be Under 20 Characters Only Allowed" ));
//			}
			
			
//			if(StringUtils.isBlank(brokerReq.getCityName())  ) {
//				errors.add(new Error("30", "City Name", "Please Enter City Name" ));
//			}
			if(StringUtils.isBlank(brokerReq.getUserName())  ) {
		//		errors.add(new Error("31", "Broker Name", "Please Enter Broker Name" ));
				errors.add("1759");
			}
			else if(brokerReq.getUserName().length()>500)   {
		//		errors.add(new Error("31", "Broker Name", "Please Enter Broker Name within 50 Characters" ));
				errors.add("1760");
			}
//			if(StringUtils.isBlank(brokerReq.getUserMail())  ) {
//				errors.add(new Error("32", "User Mail", "Please Enter User Mail" ));
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}
	
	
	public boolean isNotValidName(String name) {
		String s = name;
		String regx = "^[\\p{L} .'-]+$";
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(s);
		try {
			if (m.matches()) {
				return false;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return true;
		}
		return true;
	}
	
	public boolean isNotValidMail(String mail) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(mail);
		try {
			if (m.matches()) {
				return false;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return true;
		}
		return true;
	}
	
	private boolean existingMailCheck(String userMail, String companyId) {
		String companyName = companyMasterRepo.findByCompanyIdOrderByAmendIdDesc(companyId).get(0).getCompanyName();
		int count = userinfoRepo.countByCompanyNameAndUserMail(companyName,userMail);
		if(count>=1) {
			return true;
		}
		return false;
	}
	
	private boolean checkIdNumberIsDuplicate(String idType, String idNumber, String companyId) {
		String companyName = companyMasterRepo.findByCompanyIdOrderByAmendIdDesc(companyId).get(0).getCompanyName();
		int count = userinfoRepo.countByCompanyNameAndIdTypeAndIdNumber(companyName,idType,idNumber);
		if(count>=1) {
			return true;
		}
		return false;
	}
	
	private boolean isEmailNotSame(String loginId, String userMail) {
		LoginUserInfo login = userinfoRepo.findByLoginId(loginId);
		if(login.getUserMail().equalsIgnoreCase(userMail)) {
			return false;
		}
		return true;
	}
	
	private boolean isIdNumberNotSame(String loginId, String idNumber, String idType) {
		LoginUserInfo login = userinfoRepo.findByLoginId(loginId);
		if(login.getIdNumber().equalsIgnoreCase(idNumber) && login.getIdType().equalsIgnoreCase(idType)){
			return false;
		}
		return true;
	}

}
