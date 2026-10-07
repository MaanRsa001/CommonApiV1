package com.maan.eway.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.error.Error;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.service.impl.JasperCustomServiceImple;
import com.maan.eway.jasper.service.impl.JasperServiceImpl;
import com.maan.eway.master.req.CustomerSaveReq;
import com.maan.eway.master.req.ProductSectionsSaveReq;
import com.maan.eway.master.req.ProductsRiskSaveReq;
import com.maan.eway.master.req.SectionListReq;
import com.maan.eway.repository.ApiIntegMasterRepository;
import com.maan.eway.repository.CustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.req.AccidentDetailsReq;
import com.maan.eway.req.EserviceAllRisksListReq;
import com.maan.eway.req.EserviceAllRisksSaveReq;
import com.maan.eway.req.EserviceBuildingsDetailsSaveReq;
import com.maan.eway.req.EserviceContentsDetailsSaveReq;
import com.maan.eway.req.EserviceContentsItemListReq;
import com.maan.eway.req.EservicePersonalAccidentSaveReq;
import com.maan.eway.service.ValidationService;

@Service
public class ValidationServiceImpl implements ValidationService {

	private Logger log=LogManager.getLogger(ValidationServiceImpl.class);
	
	@Autowired
	private CustomerDetailsRepository custRepo ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepo;
	
	@Autowired
	private JasperCustomServiceImple jasperCustServiceImple;
	
	@Autowired
	private ApiIntegMasterRepository apiIntegMasterRepo;
	
	@Autowired
	private JasperServiceImpl jasper;
	
	@Override
	public List<Error> validateCustomerSave(CustomerSaveReq req) {
		List<Error>  errors = new ArrayList<Error>();
		try {
			List<Error>  commonErrors = commonCustomerErrors(req);
			if(commonErrors.size()>0 ) {
				errors.addAll(commonErrors);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(" Exception is ---> " + e.getMessage());
			errors.add(new Error("01","Common Error" , e.getMessage() ));
		}
		return errors;
	}
	
	@Override
	public List<Error> validateCustomerUpdate(CustomerSaveReq req) {
		List<Error>  errors = new ArrayList<Error>();
		try {
			List<Error>  commonErrors = commonCustomerErrors(req);
			if(commonErrors.size()>0 ) {
				errors.addAll(commonErrors);
			}
			if(StringUtils.isBlank(req.getCustomerId() ) ) {
				errors.add(new Error("35","CustomerId" , "Please Enter CustomerId" ));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(" Exception is ---> " + e.getMessage());
			errors.add(new Error("01","Common Error" , e.getMessage() ));
		}
		return errors;
	}
	
	//____________________________________________COMMON CUSTOMER ERRORS_____________________________________________\\
	public List<Error> commonCustomerErrors(CustomerSaveReq req) {
		List<Error>  errors = new ArrayList<Error>();
		try {
			// PRimary Ids
			if(StringUtils.isBlank(req.getCompanyId() ) ) {
				errors.add(new Error("01","CompanyId" , "Please Select CompanyId" ));
			}
			if(StringUtils.isBlank(req.getCreatedBy() ) ) {
				errors.add(new Error("01","CreatedBy" , "Please Select CreatedBy" ));
			}
			if(StringUtils.isBlank(req.getBranchCode() ) ) {
				errors.add(new Error("01","BranchCode" , "Please Select BranchCode" ));
			}
			
			if(StringUtils.isBlank(req.getTitleId() ) ) {
				errors.add(new Error("01","TitleId" , "Please Select TitleId" ));
			}else if(StringUtils.isBlank(req.getTitleDesc() ) ) {
				errors.add(new Error("01","TitleDesc" , "Please Select TitleDesc" ));
			}
			
			
			if(StringUtils.isBlank(req.getClientName() ) ) {
				errors.add(new Error("02","ClientName" , "Please Enter ClientName" ));
			} else if (isNotValidName(req.getClientName())  ) {
				errors.add(new Error("02","ClientName" , "Please Enter Valid ClientName" ));
			} else if (isNotValidName(req.getClientName())  ) {
				errors.add(new Error("02","ClientName" , "ClientName must be Under 1000" ));
			}
			
			if(req.getDateOfBirth() == null  ) {
				errors.add(new Error("03","DateOfBirth" , "Please Select DateOfBirth" ));
			}  else {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today); cal.add(Calendar.YEAR, -18); 
				cal.set(Calendar.HOUR , 23); cal.set(Calendar.MINUTE , 59);
				Date years18 = cal.getTime();
				if(  req.getDateOfBirth().after(years18) ) {
					errors.add(new Error("03","DateOfBirth" , "DateOfBirth Before 18 YEars Must Not Be Allowed" ));
				}
				
			}
			
			if(StringUtils.isBlank(req.getGstNo() ) ) {
				errors.add(new Error("04","GstNo" , "Please Enter GstNo" ));
			} else if (req.getGstNo().length() > 20 ) {
				errors.add(new Error("04","GstNo" , "Please Enter Valid GstNo" ));
			} else if(StringUtils.isBlank(req.getCustomerId())  ){
			  long count = custRepo.countByGstNo(req.getGstNo() );
			  if(count >0 ) {
				  errors.add(new Error("04","GstNo" , "GstNo Already Exist" ));
			  }
 			}
			
			if(StringUtils.isBlank(req.getNationalityId() ) ) {
				errors.add(new Error("05","NationalityId" , "Please Select NationalityId" ));
			} else if(StringUtils.isBlank(req.getNationalityDesc() ) ) {
				errors.add(new Error("05","NationalityDesc" , "Please Select NationalityDesc" ));
			}
			
			if(StringUtils.isBlank(req.getPlaceOfBirth() ) ) {
				errors.add(new Error("06","PlaceOfBirth" , "Please Enter PlaceOfBirth" ));
			} else if (req.getPlaceOfBirth().length()>100 ) {
				errors.add(new Error("06","PlaceOfBirth" , "PlaceOfBirth must be under 100 letters only allowed" ));
			}
			
			if(StringUtils.isBlank(req.getGenderId() ) ) {
				errors.add(new Error("07","GenderId" , "Please Select GenderId" ));
			}else if(StringUtils.isBlank(req.getGenderDesc() ) ) {
				errors.add(new Error("07","GenderDesc" , "Please Select GenderDesc" ));
			}
			
			if(StringUtils.isBlank(req.getOccupationId() ) ) {
				errors.add(new Error("08","OccupationId" , "Please Select OccupationId" ));
			}else if(StringUtils.isBlank(req.getOccupationDesc() ) ) {
				errors.add(new Error("08","OccupationDesc" , "Please Select OccupationDesc" ));
			}
			
			if(StringUtils.isBlank(req.getAddress1() ) ) {
				errors.add(new Error("09","Address1" , "Please Enter Address1" ));
			} else if(req.getAddress1().length()>100  ) {
				errors.add(new Error("09","Address1" , "Address1 Must be under 100 Characters Only Allowed" ));
			}
			if(StringUtils.isBlank(req.getAddress2() ) ) {
				errors.add(new Error("10","Address2" , "Please Enter Address2" ));
			} else if(req.getAddress2().length()>100  ) {
				errors.add(new Error("10","Address2" , "Address2 Must be under 100 Characters Only Allowed" ));
			}
			
			if(StringUtils.isBlank(req.getInsurer() ) ) {
				errors.add(new Error("11","Insurer" , "Please Enter Insurer" ));
			} else if(req.getInsurer().length()>100  ) {
				errors.add(new Error("11","Insurer" , "Isurer Must be under 100 Characters Only Allowed" ));
			}
			
			if(StringUtils.isBlank(req.getCurrencyId() ) ) {
				errors.add(new Error("12","CurrenyId" , "Please Select CurrenyId" ));
			} else if(StringUtils.isBlank(req.getCurrencyName() ) ) {
				errors.add(new Error("12","CurrenyName" , "Please Select CurrenyName" ));
			}
			
			if(StringUtils.isBlank(req.getExchangeRate())) {
				errors.add(new Error("13","ExchangeRate" , "Please Enter ExchangeRate" ));
			} else if(! req.getExchangeRate().matches("[0-9.]+")) {
				errors.add(new Error("13","ExchangeRate" , "Please Enter Valid ExchangeRate" ));
			}
			
			if(StringUtils.isBlank(req.getInsurerExchangeRate())) {
				errors.add(new Error("14","InsurerExchangeRate" , "Please Enter InsurerExchangeRate" ));
			} else if(! req.getInsurerExchangeRate().matches("[0-9.]+")) {
				errors.add(new Error("14","InsurerExchangeRate" , "Please Enter Valid InsurerExchangeRate" ));
			}
		
			// Policy Details Validation 
			if( req.getExpiryDate()==null ) {
				errors.add(new Error("15","Expiry Date" , "Please Select Expiry Date" ));
			}
			
			if(StringUtils.isBlank(req.getNoOfDaysPolicy()) ) {
				errors.add(new Error("16","NoOfDaysPolicy" , "Please Enter NoOfDaysPolicy" ));
			} else if(! req.getNoOfDaysPolicy().matches("[0-9.]+") ) {
				errors.add(new Error("16","NoOfDaysPolicy" , "Please Enter Valid Numbe In NoOfDaysPolicy" ));
			}
			
			if(StringUtils.isBlank(req.getUniquePropertyIdentification()) ) {
				errors.add(new Error("17","UniquePropertyIdentification" , "Please Enter UniquePropertyIdentification" ));
			} else if(req.getUniquePropertyIdentification().length()>100  ) {
				errors.add(new Error("17","UniquePropertyIdentification" , "UniquePropertyIdentification Must be under 100 Characters Only Allowed" ));
			}
			
			if(StringUtils.isBlank(req.getContactPerson()) ) {
				errors.add(new Error("18","ContactPerson" , "Please Enter ContactPerson" ));
			}  else if(req.getContactPerson().length()>100  ) {
				errors.add(new Error("18","ContactPerson" , "ContactPerson Must be under 100 Characters Only Allowed" ));
			} 
			
			if(StringUtils.isBlank(req.getContactPersonMobile()) ) {
				errors.add(new Error("19","ContactPersonMobile" , "Please Enter ContactPersonMobile" ));
			} else if(req.getContactPersonMobile().length()>100  ) {
				errors.add(new Error("19","ContactPersonMobile" , "ContactPersonMobile Must be under 100 Characters Only Allowed" ));
			} 
			
			if(StringUtils.isBlank(req.getInsuredName()) ) {
				errors.add(new Error("20","InsuredName" , "Please Enter InsuredName" ));
			} else if(req.getInsuredName().length()>100  ) {
				errors.add(new Error("20","InsuredName" , "InsuredName Must be under 100 Characters Only Allowed" ));
			}
			
			if(StringUtils.isBlank(req.getBusinessTypeId()) ) {
				errors.add(new Error("21","BusinessTypeId" , "Please Select BusinessTypeId" ));
			} else if(StringUtils.isBlank(req.getBusinessTypeIdDesc()) ) {
				errors.add(new Error("21","BusinessTypeIdDesc" , "Please Enter BusinessTypeIdDesc" ));
			}
			
			if(StringUtils.isBlank(req.getCountryOfRegistrationId()) ) {
				errors.add(new Error("22","CountryOfRegistrationId" , "Please Select CountryOfRegistrationId" ));
			} else if(StringUtils.isBlank(req.getCountryOfRegistrationDesc()) ) {
				errors.add(new Error("22","CountryOfRegistrationDesc" , "Please Enter CountryOfRegistrationDesc" ));
			}
			
			if(req.getRegistrationDate()==null ) {
				errors.add(new Error("23","RegistrationDate" , "Please Enter RegistrationDate" ));
			} else {
				Date today = new Date();
				if( req.getRegistrationDate().after(today) ) {
					errors.add(new Error("23","RegistrationDate" , "Future Date Not Allowed As RegistrationDate" ));
				}
			}
			
			if(StringUtils.isBlank(req.getRegistrationNumber()) ) {
				errors.add(new Error("24","RegistrationNumber" , "Please Enter RegistrationNumber" ));
			} else if(req.getRegistrationNumber().length()>20 ) {
				errors.add(new Error("24","RegistrationNumber" , "RegistrationNumber must br Under 20 Charecters only allowed" ));
			}
			if(StringUtils.isBlank(req.getDistrictId()) ) {
				errors.add(new Error("25","District ID" , "Please Enter District ID" ));
			} else if(StringUtils.isBlank(req.getDistrictDesc()) ) {
				errors.add(new Error("25","District Desc" , "Please Enter District Desc" ));
			}
			
			if(StringUtils.isBlank(req.getStreet()) ) {
				errors.add(new Error("25","Street" , "Please Enter Street" ));
			} else if(req.getStreet().length()>100 ) {
				errors.add(new Error("25","Street" , "Street Must be under 100 Charecters only allowed" ));
			} 
				
			if(StringUtils.isBlank(req.getFax()) ) {
				errors.add(new Error("26","Fax" , "Please Enter Fax" ));
			} else if( req.getFax().length()>20 ) {
				errors.add(new Error("26","Fax" , "Fax Must be under 100 Charectes only allowed" ));
			}
			
			if(StringUtils.isBlank(req.getProfileId()) ) {
				errors.add(new Error("27","ProfileId" , "Please Enter ProfileId" ));
			}
			
			if(StringUtils.isBlank(req.getProfileCategoryId()) ) {
				errors.add(new Error("28","ProfileCategoryId" , "Please Enter ProfileCategoryId" ));
			} else if(StringUtils.isBlank(req.getProfileCategoryDesc()) ) {
				errors.add(new Error("28","ProfileCategoryDesc" , "Please Enter ProfileCategoryDesc" ));
			}
			
			if(StringUtils.isBlank(req.getMobileNo1()) ) {
				errors.add(new Error("29","MobileNo1" , "Please Enter MobileNo1" ));
			} else if(req.getMobileNo1().length()>20 ) {
				errors.add(new Error("29","MobileNo1" , "MobileNo1 must be under 20 number only allowed" ));
			} 
			
			if(StringUtils.isBlank(req.getEmail1()) ) {
				errors.add(new Error("30","Email1" , "Please Enter Email1" ));
			} else if(req.getEmail1().length()>100 ) {
				errors.add(new Error("30","Email1" , "Email1 must be under 100 number only allowed" ));
			} else if (isNotValidMail(req.getEmail1()) ) {
				errors.add(new Error("30","Email1" , "Please Enter Valid Email1" ));
			}
			
			if(StringUtils.isBlank(req.getPreferredSystemNotificationId()) ) {
				errors.add(new Error("31","PreferredSystemNotificationId" , "Please Enter PreferredSystemNotificationId" ));
			} else if(StringUtils.isBlank(req.getPreferredSystemNotification()) ) {
				errors.add(new Error("31","PreferredSystemNotification" , "Please Enter PreferredSystemNotification" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info(" Exception is ---> " + e.getMessage());
			errors.add(new Error("32","Common Error" , e.getMessage() ));
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

	
	
	//____________________________________________PRODUCT SECTION VALIDATION_____________________________________________\\
	@Override
	public List<Error> validateProductSections(ProductsRiskSaveReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if (StringUtils.isBlank(req.getBranchCode()) ) {
				errors.add(new Error("01","Branch Code", "Please Enter BranchCode"));
			}
			if (StringUtils.isBlank(req.getCreatedBy()) ) {
				errors.add(new Error("02","Created By", "Please Enter CreatedBy"));
			}
			if (StringUtils.isBlank(req.getCustomerId()) ) {
				errors.add(new Error("03","Customer Id", "Please Enter Customer Id"));
			}
			if (StringUtils.isBlank(req.getInsuranceId()) ) {
				errors.add(new Error("04","Insurance Id", "Please Enter Insurance Id"));
			}
			if (StringUtils.isBlank(req.getProductId()) ) {
				errors.add(new Error("05","Product Id", "Please Enter Product Id"));
			}
			if (StringUtils.isBlank(req.getProductName()) ) {
				errors.add(new Error("06","Product Name", "Please Enter Product Name"));
			}
			
			if (StringUtils.isBlank(req.getRequestReferenceNo()) ) {
				errors.add(new Error("06","Request Reference No", "Please Enter Request Reference No"));
			}
			
			if (req.getProductRiskList()==null || req.getProductRiskList().size() <= 0 ) {
				errors.add(new Error("08","Risk List", "Please Select atleast One Risk"));
			} else {
				Long riskRowNo = 0L ;
				for (ProductSectionsSaveReq data :  req.getProductRiskList()) {
					riskRowNo = riskRowNo + 1 ;
					if (StringUtils.isBlank(data.getLocationId())  ) {
						errors.add(new Error("09","Location Id", "Please Select atleast One Location in Location Row No :" + riskRowNo));
					}
					
				    if( data.getSectionList() ==null || data.getSectionList().size()<=0  ) {
				    	errors.add(new Error("09","Location Id", "Please Select atleast One Section in Location Row No :" + riskRowNo));
				    } else {
				    	Long sectionsRowNo = 0L ;
				    	for (  SectionListReq data2 : data.getSectionList()) {
				    		sectionsRowNo = sectionsRowNo + 1 ;
				    		if (StringUtils.isBlank(data2.getSectionId())  ) {
								errors.add(new Error("09","Section Id",  "Please Select Section Id in Risk "+  riskRowNo + "& Section Row No :" + sectionsRowNo));
							}
				    		if (StringUtils.isBlank(data2.getSectionName()) ) {
								errors.add(new Error("09","Risk Id", "Please Select Section Name in Risk "+  riskRowNo + "& Section Row No :" + sectionsRowNo));
							}
				    	}
				    	
				    }
				}
			}
			
			
		} catch (Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("01","Common Error", e.getMessage()));
		}
		return errors;
	}


	//____________________________________________PERSONAL ACCIDENT VALIDATION_____________________________________________\\
	
	@Override
	public List<Error> validatePersonalAccident(EservicePersonalAccidentSaveReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if (StringUtils.isBlank(req.getBranchCode()) ) {
				errors.add(new Error("01","Branch Code", "Please Enter BranchCode"));
			}
			if (StringUtils.isBlank(req.getCompanyId()) ) {
				errors.add(new Error("02","Company Id", "Please Enter Company Id"));
			}
			if (StringUtils.isBlank(req.getCustomerId().toString()) ) {
				errors.add(new Error("03","Customer Id", "Please Enter Customer Id"));
			}
			if (StringUtils.isBlank(req.getLocationId().toString()) ) {
				errors.add(new Error("04","Location Id", "Please Enter Location Id"));
			}
			if (StringUtils.isBlank(req.getSectionId().toString())) {
				errors.add(new Error("05","Section Id", "Please Enter Section Id"));
			}
			for(AccidentDetailsReq ac : req.getAccidentDetails()) {
				if (StringUtils.isBlank(ac.getNameOfPerson())) {
					errors.add(new Error("06","Name of Person", "Please Enter Name of Person"));
				}
				if (ac.getNameOfPerson().length()>100) {
					errors.add(new Error("06","Name Of Person", "Please Enter Name Of Person within 100 Characters"));
				}
				if (StringUtils.isBlank(ac.getDescription())) {
					errors.add(new Error("07","Description", "Please Enter Description"));
				}
				if (ac.getDescription().length()>100) {
					errors.add(new Error("07","Description", "Please Enter Description within 100 Characters"));
				}
				if (StringUtils.isBlank(ac.getAge().toString())) {
					errors.add(new Error("08","Age", "Please Enter Age"));
				}
				if (StringUtils.isBlank(ac.getDateOfBirth().toString())) {
					errors.add(new Error("09","Date Of Birth", "Please Enter Date Of Birth"));
				}
				if (StringUtils.isBlank(ac.getHeight().toString())) {
					errors.add(new Error("10","Height", "Please Enter Height"));
				}
				if (StringUtils.isBlank(ac.getSumInsured().toString())) {
					errors.add(new Error("11","Sum Insured", "Please Enter Sum Insured"));
				}
				if (StringUtils.isBlank(ac.getWeight().toString())) {
					errors.add(new Error("12","Weight", "Please Enter Weight"));
				}
			}
		} catch (Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("01","Common Error", e.getMessage()));
		}
		return errors;
	}


//____________________________________________RAW BUILDING DETAILS VALIDATION_____________________________________________\\

@Override
public List<Error> validateEserviceBuildingDetails(EserviceBuildingsDetailsSaveReq req) {
	List<Error> errors =new ArrayList<Error>();
	try {
		if (StringUtils.isBlank(req.getBuildingAddress().toString())) {
			errors.add(new Error("01", "BuildingAddress", "Please Enter BuildingAddress"));
		} else if (req.getBuildingAddress().length()>200) {
			errors.add(new Error("01", "BuildingAddress", "Please Enter BuildingAddress Character within 200"));
		}
		if (StringUtils.isBlank(req.getConstMaterialId().toString())) {
			errors.add(new Error("02", "ConstMaterialId", "Please Select ConstMaterialId"));
		} else if (StringUtils.isBlank(req.getConstMaterialDesc())) {
			errors.add(new Error("02", "ConstMaterialDesc", "Please Select ConstMaterialDesc"));
		} 
		if(!("01".equals(req.getConstMaterialId())||"02".equals(req.getConstMaterialId()) )){
			errors.add(new Error("02","ConstMaterialDesc()","Select any one ConstMaterialDesc"));
		}else if(!("Walls".equals(req.getConstMaterialDesc())||"Roof".equals(req.getConstMaterialDesc()) )){
				errors.add(new Error("02","ConstMaterialDesc()","Select any one ConstMaterialDesc"));
		}
		if (!StringUtils.isNumeric(req.getStoreysHeight().toString())) {
			errors.add(new Error("03", "StoreysHeight", "Please Enter StoreysHeight Integer"));
		}
		if (StringUtils.isBlank(req.getOutbuildingConstId().toString())) {
			errors.add(new Error("04", "OutbuildingConstId", "Please Select OutbuildingConstId"));
		} else if (StringUtils.isBlank(req.getOutbuildingConstDesc())) {
			errors.add(new Error("04", "OutbuildingConstDesc", "Please Select OutbuildingConstDesc"));
		}else if(!("01".equals(req.getOutbuildingConstId())||"02".equals(req.getOutbuildingConstId()) )){
			errors.add(new Error("04","OutbuildingConstId()","Select any one OutbuildingConstId"));
		}else if(!("Walls".equals(req.getOutbuildingConstDesc())||"Roof".equals(req.getOutbuildingConstDesc()) )){
				errors.add(new Error("04","OutbuildingConstDesc()","Select any one OutbuildingConstDesc"));
		}
		if (StringUtils.isBlank(req.getBusinessPortionDetails().toString())) {
			errors.add(new Error("05", "BusinessPortionDetails", "Please Enter BusinessPortionDetails"));
		} else if (req.getBusinessPortionDetails().length()>20) {
			errors.add(new Error("05", "BusinessPortionDetails", "Please Enter BusinessPortionDetails  within 20 Character"));
		}
		if (StringUtils.isBlank(req.getAboutBuildingId().toString())) {
			errors.add(new Error("06", "AboutBuildingId", "Please Select AboutBuildingId"));
		} else if (StringUtils.isBlank(req.getAboutBuildingDesc())) {
			errors.add(new Error("06", "AboutBuildingDesc", "Please Select AboutBuildingDesc"));
		}
		if(!("01".equals(req.getAboutBuildingId())||"02".equals(req.getAboutBuildingId())|| "03".equals(req.getAboutBuildingId()) )){
			errors.add(new Error("06","AboutBuildingId()","Select any one AboutBuildingId"));
		}else if(!("Private".equals(req.getAboutBuildingDesc())||" SelfContained".equals(req.getAboutBuildingDesc())||"NotSelfContained".equals(req.getAboutBuildingDesc()))){
			errors.add(new Error("06","AboutBuildingDesc()","Select any one AboutBuildingDesc"));
		}
		
		
		if (StringUtils.isBlank(req.getYouOccupyTheBuilding().toString())) {
			errors.add(new Error("07", "YouOccupyTheBuilding", "Please Enter YouOccupyTheBuilding"));
		} else if (req.getYouOccupyTheBuilding().length()>20) {
			errors.add(new Error("07", "YouOccupyTheBuilding", "Please Enter YouOccupyTheBuilding within 20 Character "));
		}
		
		if (StringUtils.isBlank(req.getStateExtentId().toString())) {
			errors.add(new Error("09", "StateExtentId", "Please Select StateExtentId"));
		} else if (StringUtils.isBlank(req.getStateExtentDesc())) {
			errors.add(new Error("09", "StateExtentDesc", "Please Select StateExtentDesc"));
		}else if(!("01".equals(req.getStateExtentId())||"02".equals(req.getStateExtentId()) )){
			errors.add(new Error("09","StateExtentId()","Select any one StateExtentId"));
		}
		if(!("More than 7 Days".equals(req.getStateExtentDesc())||" More than 30 Days".equals(req.getStateExtentDesc()))){
			errors.add(new Error("09","StateExtentDesc()","Select any one StateExtentDesc"));
		}
		
		if (StringUtils.isBlank(req.getMaintanenceDesc().toString())) {
			errors.add(new Error("10", "MaintanenceDesc", "Please Enter MaintanenceDesc"));
		} else if (req.getMaintanenceDesc().length()>20) {
			errors.add(new Error("10", "MaintanenceDesc", "Please Enter MaintanenceDesc within 20 Character "));
		}
		if (StringUtils.isBlank(req.getSectionId().toString())) {
			errors.add(new Error("11", "SectionId", "Please Enter SectionId"));
		}
		if(StringUtils.isBlank(req.getCustomerId())){
			errors.add(new Error ("12","CustomerId","Please Enter CustomerId"));
		}
		if(StringUtils.isBlank(req.getCompanyId())) {
			errors.add(new Error("13","CompanyId","Please Enter CompanyId"));
		}
		if(StringUtils.isBlank(req.getLocationId())) {
			errors.add(new Error("14","LocationId","Please Enter Location Id"));
		}
		if(StringUtils.isBlank(req.getBranchCode())) {
			errors.add(new Error("15","BranchCode","please Enter BranchCode"));
		}
	}catch(Exception e) {		
		e.printStackTrace();
		log.info("Exception is --->", e.getMessage());
		errors.add(new Error("16","Common Error",e.getMessage()));
	}
	return errors;
}
//____________________________________________RAW CONTENT DETAILS VALIDATION_____________________________________________\\
@Override
public List<Error> validateEserviceContentDetails(EserviceContentsDetailsSaveReq req) {
	List<Error> errors=new ArrayList<Error>();
	try {
	if (StringUtils.isBlank(req.getSectionId().toString())) {
		errors.add(new Error("02", "SectionId", "Please Enter SectionId"));
	}
	if(StringUtils.isBlank(req.getCustomerId().toString())){
		errors.add(new Error ("03","CustomerId","Please Enter CustomerId"));
	}
	if(StringUtils.isBlank(req.getCompanyId())) {
		errors.add(new Error("04","CompanyId","Please Enter CompanyId"));
	}
	if(StringUtils.isBlank(req.getLocationId().toString())) {
		errors.add(new Error("05","Location ID","Please Enter Location Id"));
	}
	if(StringUtils.isBlank(req.getBranchCode())) {
		errors.add(new Error("06","BranchCode","please Enter BranchCode"));
	}else {
	
		for (EserviceContentsItemListReq data :  req.getItemList()) {
			
			if (StringUtils.isBlank(data.getItemId())  ) {
				errors.add(new Error("09","ItemId", "Please Select Item Id"));
			}
			if (StringUtils.isBlank(data.getItemName())  ) {
				errors.add(new Error("10","ItemName", "Please Select ItemName : " ));
			}
			if (StringUtils.isBlank(data.getItemDesc())  ) {
				errors.add(new Error("11","ItemDesc", "Please Enter ItemDesc"));
			}
			if (StringUtils.isBlank(data.getItemValue())  ) {
				errors.add(new Error("12","Value", "Please Enter Value : " ));
			}
		
		}
	}
	}catch(Exception e) {
		e.printStackTrace();
		log.info("Exception is -->", e.getMessage());
		errors.add(new Error("01","CommonError",e.getMessage()));
	}
	return null;
}
//____________________________________________ALL RISK VALIDATION_____________________________________________\\
@Override
public List<Error> validateEserviceAllRisk(EserviceAllRisksSaveReq req) {

List<Error> errors =new ArrayList<Error>();
try {
	if (StringUtils.isBlank(req.getSectionId().toString())) {
		errors.add(new Error("02", "SectionId", "Please Enter SectionId"));
	}
	if(StringUtils.isBlank(req.getCustomerId().toString())){
		errors.add(new Error ("03","CustomerId","Please Enter CustomerId"));
	}
	if(StringUtils.isBlank(req.getCompanyId())) {
		errors.add(new Error("04","CompanyId","Please Enter CompanyId"));
	}
	if(StringUtils.isBlank(req.getLocationId().toString())) {
		errors.add(new Error("05","Location Id","Please Enter Location Id"));
	}
	if(StringUtils.isBlank(req.getBranchCode())) {
		errors.add(new Error("06","BranchCode","please Enter BranchCode"));
	}
	if (req.getEserviceAllRisksList()==null || req.getEserviceAllRisksList().size() <= 0 ) {
		errors.add(new Error("08","Risk List", "Please Select atleast One Risk"));
	} else {
	
		for (EserviceAllRisksListReq data :  req.getEserviceAllRisksList()) {
			
			if (StringUtils.isBlank(data.getItemId())  ) {
				errors.add(new Error("09","ItemId", "Please Select Item Id"));
			}
			if (StringUtils.isBlank(data.getItemName())  ) {
				errors.add(new Error("10","ItemName", "Please Select ItemName : " ));
			}
			if (StringUtils.isBlank(data.getMake())  ) {
				errors.add(new Error("11","Make", "Please Enter Make"));
			}
			if (StringUtils.isBlank(data.getModel())  ) {
				errors.add(new Error("12","Model", "Please Enter Model : " ));
			}
			if (StringUtils.isBlank(data.getSerialNo())  ) {
				errors.add(new Error("13","SerialNo", "Please Enter SerialNo : " ));
			}
			if (StringUtils.isBlank(data.getValue().toString())  ) {
				errors.add(new Error("14","Value", "Please Enter Value : " ));
			} 	/*else if (data.getValue()>80000)   {
				errors.add(new Error("14","Value", "Please Enter Value Greater than 80000: " ));
			}*/
		}
	}
}catch(Exception e) {
	e.printStackTrace();
	log.info("Exception is-->", e.getMessage());
	errors.add(new Error("01","CommonError",e.getMessage()));
}
	return null;
}

@Override
public List<Error> validateMotorSchedule(JasperDocumentReq req) {
	List<Error> errors = new ArrayList<>();
	try {
		HomePositionMaster hpmData = homeRepo.findByQuoteNo(req.getQuoteNo());
		if(hpmData!=null && Arrays.asList(5,46).contains(hpmData.getProductId()) && hpmData.getCompanyId().equalsIgnoreCase("100002") && hpmData.getStatus().equalsIgnoreCase("P")) {
			List<MotorDataDetails> m = motorDataDetailsRepo.findByQuoteNo(req.getQuoteNo());
			IntStream.range(0,m.size()).forEach(i -> {
				MotorDataDetails k = m.get(i);
				SectionDataDetails details = jasperCustServiceImple
				        .getStickerDetails(k.getQuoteNo(), k.getVehicleId());

				String stickerNo = (details != null) ? details.getStickerNumber() : null;
				String responseDesc = (details != null) ? details.getResponseStatusDesc() : null;

				if (StringUtils.isBlank(stickerNo)) {

				    String errorMsg;

				    if (StringUtils.isNotBlank(responseDesc)) {
				    	errorMsg = responseDesc.trim() + " So Vehicle is not pushed in TIRA";
				    } else {
				        
				        errorMsg = "Report cannot be generated. Please wait—TIRA processing is still in progress for Quote No: "
				                + k.getQuoteNo() + " and Vehicle ID: " + k.getVehicleId();
				    }

				    errors.add(new Error(String.valueOf(i), "StickerNumber", errorMsg));
				}
			});
		}
	}catch(Exception e) {
		e.printStackTrace();
	}
	return errors;
}

@Override
public List<Error> validateMotorSchedule1(JasperDocumentReq req) {
    List<Error> errors = new ArrayList<>();
    try {
        // RequestReferenceNo should already be validated in controller, but double-check
        if (StringUtils.isBlank(req.getRequestReferenceNo())) {
             return errors; // Skip validation
        }
        
        // Get data by RequestReferenceNo
        List<HomePositionMaster> hpmDataList = homeRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
        
        // Check if list is not empty
        if (hpmDataList == null || hpmDataList.isEmpty()) {
            errors.add(new Error("", "NOT_FOUND", "No record found for RequestReferenceNo: " + req.getRequestReferenceNo()));
            return errors;
        }
        
        // Get the first record (since findByRequestReferenceNo returns list)
        HomePositionMaster hpmData = hpmDataList.get(0);
        
        if(hpmData != null && Arrays.asList(5,46).contains(hpmData.getProductId()) && 
           hpmData.getCompanyId().equalsIgnoreCase("100002") && 
           hpmData.getStatus().equalsIgnoreCase("P")) {
            
            // Use quoteNo from the found HomePositionMaster to get motor details
            String quoteNo = hpmData.getQuoteNo();
            if (StringUtils.isNotBlank(quoteNo)) {
                List<MotorDataDetails> m = motorDataDetailsRepo.findByQuoteNo(quoteNo);
                if (m != null && !m.isEmpty()) {
                    IntStream.range(0, m.size()).forEach(i -> {
                        MotorDataDetails k = m.get(i);
                        SectionDataDetails details = jasperCustServiceImple
        				        .getStickerDetails(k.getQuoteNo(), k.getVehicleId());

        				String stickerNo = (details != null) ? details.getStickerNumber() : null;
        				String responseDesc = (details != null) ? details.getResponseStatusDesc() : null;

        				if (StringUtils.isBlank(stickerNo)) {

        				    String errorMsg;

        				    if (StringUtils.isNotBlank(responseDesc)) {
        				    	errorMsg = responseDesc.trim() + " So Vehicle is not pushed in TIRA";
        				    } else {
        				        
        				        errorMsg = "Report cannot be generated. Please wait—TIRA processing is still in progress for Quote No: "
        				                + k.getQuoteNo() + " and Vehicle ID: " + k.getVehicleId();
        				    }

        				    errors.add(new Error(String.valueOf(i), "StickerNumber", errorMsg));
        				}
                    });
                }
            }
        }
        
    } catch(Exception e) {
        e.printStackTrace();
        errors.add(new Error("", "VALIDATION_EXCEPTION", "Error during validation: " + e.getMessage()));
    }
    return errors;
}

@Override
public List<Error> validateTaxInvoice(String quoteNo) {
    List<Error> errors = new ArrayList<>();
    try {
        HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);

        System.out.println("Entering into Validation block");
        if (homeData != null && homeData.getCompanyId().equalsIgnoreCase("100002")) {

        	System.out.println("Entering into If block");
            if (StringUtils.isBlank(homeData.getOrangeCardurl())) {

                List<ApiIntegMaster> apiIntegList = apiIntegMasterRepo
                        .findAllByCompanyIdAndProductId(homeData.getCompanyId(), 99999);

                if (apiIntegList == null || apiIntegList.isEmpty()) {
                    errors.add(new Error("0", "ApiIntegMaster",
                            "No API integration config found for companyId: " + homeData.getCompanyId()));
                    return errors;
                }

                Map<String, String> apiUrlMap = apiIntegList.stream()
                        .collect(Collectors.toMap(
                                a -> a.getApiType().toUpperCase(),
                                ApiIntegMaster::getApiUrl
                        ));

                String tokenUrl   = apiUrlMap.get("TIRA_TOKEN");    
                String invoiceUrl = apiUrlMap.get("TIRA_STATUS");  
                
                System.out.println("tokenUrl" + tokenUrl);

                System.out.println("invoiceUrl" + invoiceUrl);
                
                if (StringUtils.isBlank(tokenUrl)) {
                    errors.add(new Error("0", "ApiIntegMaster",
                            "API config not found for type: TIRA_TOKEN"));
                    return errors;
                }

                if (StringUtils.isBlank(invoiceUrl)) {
                    errors.add(new Error("0", "ApiIntegMaster",
                            "API config not found for type: TIRA_STATUS"));
                    return errors;
                }

                String responseUrl = jasper.callThirdPartyApi(
                        tokenUrl,
                        invoiceUrl,
                        "admin",
                        "admin",
                        homeData.getPolicyNo()
                );

                if (StringUtils.isNotBlank(responseUrl)) {
                    if (responseUrl.trim().equalsIgnoreCase("https://verify.tra.go.tz")) {
                        errors.add(new Error("0", "TRA Certificate search API",
                                "It is not integrated in TIRA please wait."));
                    } else {
                        homeData.setOrangeCardurl(responseUrl);
                        homeRepo.saveAndFlush(homeData);
                    }
                } else {
                    errors.add(new Error("0", "TRA Certificate search API",
                            "Failed to retrieve TRA Certificate search API URL from Premia API."));
                }
            }
            
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return errors;
}
}
	

