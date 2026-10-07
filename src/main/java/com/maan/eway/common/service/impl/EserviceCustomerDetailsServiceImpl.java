package com.maan.eway.common.service.impl;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.maan.eway.bean.*;
import com.maan.eway.common.res.*;
import com.maan.eway.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.claim.controller.ClaimCustomerDetailsController;
import com.maan.eway.claim.req.ClaimCustomerSyncReq;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.req.CustomerChangesSaveReq;
import com.maan.eway.common.req.CustomerDetailsReq;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetByCustomerRefNoReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.service.EserviceCustomerDetailsService;
import com.maan.eway.error.Error;
import com.maan.eway.finanaceIntegration.req.CustomerCreationReq;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.impl.FactorRateRequestDetailsServiceImpl;

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
@Transactional
public class EserviceCustomerDetailsServiceImpl implements EserviceCustomerDetailsService {

	private Logger log = LogManager.getLogger(EserviceCustomerDetailsServiceImpl.class);

	@Autowired
	private EserviceCustomerDetailsRepository repository;

	@Autowired
	private EserviceInsuredDetailsRepository insuredRepository;

	@Autowired
	private ListItemValueRepository listRepo;

	@Autowired
	private LoginMasterRepository loginRepo;

	@Autowired
	private SeqCustrefnoRepository custRefRepo  ;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;

	@Autowired
	private PersonalInfoRepository personalInforepo;

	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ;

	@Autowired
	private RegionMasterRepository regionMasterRepo;

	@Autowired
	private StateMasterRepository stateMasterRepo;

	private PaymentDetailRepository paymentDetailsRepo;


	@Autowired
	private CompanyProductMasterRepository companyProductRepo;

	@Autowired
	private EServiceMotorDetailsRepository  eserviceMotorRepo;

	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingRepo;

	@Autowired
	private EserviceTravelDetailsRepository eserviceTravelRepo;

	@Autowired
	private EserviceCommonDetailsRepository eserviceCommonRepo;

	@Autowired private PolicyCoverDataRepository policyCoverDataRepo;

	@Autowired FactorRateRequestDetailsServiceImpl factorRateRequestDetailsServiceImpl;

	@Autowired
	private SanlamEserviceCustomerDetails sanlamEcustdetails;

	@Autowired
	private TanzaniaEserviceCustomerDetails tanzaniaEcustdetails;

	@Autowired
	private MadisonEserviceCustomerDetails madisonEcustdetails;

	@Autowired
	private OromiaEserviceCustomerDetails oromiaEcustdetails;

	@Autowired
	private UgandaEserviceCustomerDetails ugandaEcustdetails;

	@Autowired
	PolicyCoverDataRepository policyCoverDataRepository;

	@Autowired
	private KenyaEserviceCustomerDetails kenyaEcustdetails;

	@Autowired
	private EagalEserviceCustomerDetails eagalEcustdetails;

	@Autowired
	private BurkinoEserviceCustomerDetails burkinoEcustdetails;

	@Autowired
	private AngolaEserviceCustomerDetails angolaEcustdetails;

	@Autowired
	private PhoenixEserviceCustomerDetails phoenixEcustdetails;
	
	@Autowired
	private PhoenixEserviceNamibiaCustomerDetails namibiaEcustdetails;

	@Autowired
	private PhoenixMozambiqueEserviceCustomerDetails mozambiqueEcustdetails;


	@Autowired
	private EserviceCustomerLocationRepository locationRepo;

	@Autowired
	private CustomerSequenceRepository customerSequenceRepository;

	@Autowired
	private WnmCustCategoryRepository wnmCustCategoryRepository;

	@Autowired
	private WntCustDtlRepository wntCustDtlRepo;
	@Autowired
	private ClaimCustomerDetailsController claimCustomerDetails;


	@Autowired
	private ClaimIntimationRepository claimIntimationRepository;


	@Autowired
	private ClaimReserveDetailsRepository claimReserveDetailsRepository;
	
	@Autowired
	private  OmanEserviceCustomerDetails oman;

	@Autowired
	private DozerBeanMapper dozerBeanMapper;

	@Value(value = "${finace.generateToken}")
	private String financeTokenApi;

	@Value(value = "${finace.CustomerCreation}")
	private String finaceCustomerCreation;

	@Value(value = "${finace.updateCustomerDetail}")
	private String updateCustomerDetail;


	@PersistenceContext
	private EntityManager em;


	public boolean containsOnlyNumbers(String input) {
	    if (input == null || input.isEmpty()) {
	        return false; // Return false for null or empty strings
	    }

	    for (int i = 0; i < input.length(); i++) {
	        if (!Character.isDigit(input.charAt(i))) {
	            return false; // Return false if any character is not a digit
	        }
	    }
		/*
		 * if(company_id.equals("100004") && idType.contains("NRC")) {
		 * if(input.length()!=9) { return false; } }
		 */
	    return true; // Return true if all characters are digits
	}
	@Override
	public List<String> validateCustomerDetails(EserviceCustomerSaveReq req) {
		List<String> errorList = new ArrayList<String>();
		if ("100004".equalsIgnoreCase(req.getCompanyId())) {
			errorList = madisonEcustdetails.validateCustomerDetails(req);
		} else if ("100018".equalsIgnoreCase(req.getCompanyId())) {
			errorList = oromiaEcustdetails.validateCustomerDetails(req);
		} else if ("100019".equalsIgnoreCase(req.getCompanyId())) {
			errorList = ugandaEcustdetails.validateCustomerDetails(req);
		} else if ("100020".equalsIgnoreCase(req.getCompanyId())) {
			errorList = kenyaEcustdetails.validateCustomerDetails(req);
		} else if ("100027".equalsIgnoreCase(req.getCompanyId())) {
			errorList = angolaEcustdetails.validateCustomerDetails(req);
		} else if ("100028".equalsIgnoreCase(req.getCompanyId())) {
			errorList = eagalEcustdetails.validateCustomerDetails(req);
		} else if ("100040".equalsIgnoreCase(req.getCompanyId())) {
			errorList = sanlamEcustdetails.validateCustomerDetails(req);
		} else if ("100042".equalsIgnoreCase(req.getCompanyId())) {
			errorList = burkinoEcustdetails.validateCustomerDetails(req);
		} else if ("100046".equalsIgnoreCase(req.getCompanyId())) {
			errorList = phoenixEcustdetails.validateCustomerDetails(req);
		} else if ("100047".equalsIgnoreCase(req.getCompanyId())) {
			errorList = phoenixEcustdetails.validateCustomerDetails(req);
		} else if ("100049".equalsIgnoreCase(req.getCompanyId())) {
			errorList = phoenixEcustdetails.validateCustomerDetails(req);
		} else if ("100050".equalsIgnoreCase(req.getCompanyId())) {
			errorList = namibiaEcustdetails.validateCustomerDetails(req);
		} else if ("100048".equalsIgnoreCase(req.getCompanyId())) {
			errorList = mozambiqueEcustdetails.validateCustomerDetails(req);
		} 
		else if ("100055".equalsIgnoreCase(req.getCompanyId())) {
			errorList = oman.validateCustomerDetails(req);
		} else {
			errorList = tanzaniaEcustdetails.validateCustomerDetails(req);
		}
		return errorList;

	}
	public static boolean isValidMail(String mail) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.matches();

	}

	public static boolean checkIsValidMail(String mail) {
		String regex = "^[a-zA-Z0-9._%+-àâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]+@[a-zA-Z0-9.-àâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]+\\.[a-zA-ZàâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]{2,}$";
	    Pattern p = Pattern.compile(regex);
	    Matcher m = p.matcher(mail);
	    return m.matches();
	}
	@Override
	public List<String> validateInsuredDetails(EserviceCustomerSaveReq req) {
		List<String> errorList = new ArrayList<String>();
	try {
		if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {

			if (StringUtils.isBlank(req.getTitle()))  {
				errorList.add("1047");
			}
			if("2".equalsIgnoreCase(req.getPolicyHolderType()))
			{
				if (StringUtils.isBlank(req.getClientName()) ) {
					errorList.add("1100");
				} else if (req.getClientName().length() > 100) {
				   errorList.add("1101");
				}
				else if (StringUtils.isNotBlank(req.getClientName()) && (req.getClientName().matches("^[0-9].*") || !req.getClientName().matches("[a-zA-ZÀ-ÿ0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?\\s'-]+$"))){
					errorList.add("1102");
				}
			}
			else
			{
				if (StringUtils.isBlank(req.getClientName()) ) {
					errorList.add("1001");
				} else if (req.getClientName().length() > 100) {
				   errorList.add("1002");
				}
				else if (StringUtils.isNotBlank(req.getClientName()) && (req.getClientName().matches("^[0-9].*") || !req.getClientName().matches("[a-zA-ZÀ-ÿ0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?\\s'-]+$"))){
					errorList.add("1003");
				}
				if (StringUtils.isBlank(req.getLastName()) ) {
					errorList.add("3307");
				} else if (req.getLastName().length() > 100) {
				   errorList.add("3308");
				}
				else if (StringUtils.isNotBlank(req.getLastName()) && (req.getLastName().matches("^[0-9].*") || !req.getLastName().matches("[a-zA-ZÀ-ÿ0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?\\s'-]+$"))){
					errorList.add("3309");
				}
			}
				// Date Validation
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if ("1".equalsIgnoreCase(req.getPolicyHolderType())) {
					if (req.getDobOrRegDate() == null) {
						errorList.add("1065");
					}else if (req.getDobOrRegDate() != null) {
						if (req.getDobOrRegDate().after(today)) {
							errorList.add("1088");
						}
						LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDate();
						LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						Integer years = Period.between(localDate1, localDate2).getYears();
						if (years > 100) {
							errorList.add("1089");

						}
					}
				}else if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
					if (req.getDobOrRegDate() == null) {
						errorList.add("1065");
					}else if (req.getDobOrRegDate() != null) {
						if (req.getDobOrRegDate().after(today)) {
								errorList.add("1090");
						}
						LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						Integer years = Period.between(localDate1, localDate2).getYears();
						if (years > 100) {
							errorList.add("1091");

						}
					}
				}
			/*if (StringUtils.isBlank(req.getOccupation()) ) {
				errorList.add("1022");
			} else if(req.getOccupation().equalsIgnoreCase("99999")){
				if (StringUtils.isBlank(req.getOtherOccupation()) ) {
					errorList.add("1023");
				}else if (req.getOtherOccupation().length() > 100){
					errorList.add("1024");
				}else if(!req.getOtherOccupation().matches("[a-zA-Z\\s]+")){
					errorList.add("1025");
				}
			}*/
			if (StringUtils.isBlank(req.getMobileCode1())) {
				errorList.add("1062");
			}
			if (StringUtils.isBlank(req.getMobileNo1())) {
				errorList.add("1026");
			} else if (req.getMobileNo1().length() > 10||req.getMobileNo1().length() < 8) {
				errorList.add("1027");
			} else if (!req.getMobileNo1().matches("[0-9]+") ) {
				errorList.add("1028");
			} else if (req.getMobileNo1().matches("[0-9]+") && Double.valueOf(req.getMobileNo1()) <=0 ) {
				errorList.add("1029");
			}
			if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
				if (StringUtils.isBlank(req.getIdType())) {
					errorList.add("1011");
				}
				if (StringUtils.isBlank(req.getPolicyHolderTypeid())) {
					errorList.add("1012");
				}

				if (StringUtils.isBlank(req.getIdNumber())) {
					errorList.add("1013");
				} else if (req.getIdNumber().length() > 15) {
					errorList.add("1014");
				}  else if (req.getIdNumber().matches("[0-9]+") && Double.valueOf(req.getIdNumber()) <=0 ) {
					errorList.add("1015");
				} else if(!req.getIdNumber().matches("[a-zA-Z0-9-]+")) {
					errorList.add("1015");
				}

				if(StringUtils.isBlank(req.getStreet()) ) {
					errorList.add("3310");
				}else if (req.getStreet().length() > 100) {
					errorList.add("3311");
				}

				if (StringUtils.isBlank(req.getCityName())) {
					errorList.add("1082");
				} else if (req.getCityName().length() > 100) {
					errorList.add("1083");
				}
			}
			if (StringUtils.isBlank(req.getRegionCode())) {
				errorList.add("1053");
			} else if (req.getRegionCode().length() > 20) {
				errorList.add("1054");
			}
			if (StringUtils.isBlank(req.getStateCode())) {
				errorList.add("1061");
			}
			if (StringUtils.isBlank(req.getCountry())) {
				//errorList.add("1048");
			}

			if (StringUtils.isBlank(req.getClientStatus())) {
				errorList.add("1010");
			}

			if (StringUtils.isNotBlank(req.getPinCode())) {
				 if (! (req.getPinCode().matches("[0-9a-zA-Z]+") ||  req.getPinCode().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")) ) {
					 errorList.add("3000");
				 }
				if (req.getPinCode().length() > 10) {
					errorList.add("1016");
				}
			}
			if (StringUtils.isNotBlank(req.getFax()) && req.getFax().length() > 20) {
				errorList.add("1017");
			}

			if (StringUtils.isNotBlank(req.getTelephoneNo2()) && req.getTelephoneNo2().length() > 20) {
				errorList.add("1018");
			} else if (StringUtils.isNotBlank(req.getTelephoneNo2()) && !req.getTelephoneNo2().matches("\\d+")) {
				errorList.add("1019");
			}

			if (StringUtils.isNotBlank(req.getTelephoneNo3()) && req.getTelephoneNo3().length() > 20) {
				errorList.add("1020");
			} else if (StringUtils.isNotBlank(req.getTelephoneNo3()) && !req.getTelephoneNo3().matches("\\d+")) {
				errorList.add("1021");
			}

			if (StringUtils.isNotBlank(req.getMobileNo3()) &&( req.getMobileNo3().length() > 10||req.getMobileNo3().length() < 10)) {
				errorList.add("1030");
			} else if (StringUtils.isNotBlank(req.getMobileNo3()) && !req.getMobileNo3().matches("\\d+")) {
				errorList.add("1031");
			}

			if("2".equalsIgnoreCase(req.getPolicyHolderType()))
			{
				if ( StringUtils.isNotBlank(req.getEmail1()) ) {
					if( req.getEmail1().length() > 50 ) {
						errorList.add("1032");
					} else if(StringUtils.isNotBlank(req.getEmail1())) {
						boolean bValue = checkIsValidMail(req.getEmail1());

						if(req.getEmail1().matches("^[0-9].*") || !req.getEmail1().matches(".*@.*\\..*") )
						{
							errorList.add("1033");
						}
						else if (!bValue) {
							errorList.add("1033");
						}
					}
				}
				else {
					errorList.add("3001");
				}

			}
			else
			{
				if ( StringUtils.isNotBlank(req.getEmail1()) ) {
					if( req.getEmail1().length() > 50 ) {
						errorList.add("1032");
					} else if(StringUtils.isNotBlank(req.getEmail1())) {
						boolean bValue = checkIsValidMail(req.getEmail1());

						if(req.getEmail1().matches("^[0-9].*") || !req.getEmail1().matches(".*@.*\\..*") )
						{
							errorList.add("1033");
						}
						else if (!bValue) {
							errorList.add("1033");
						}
					}
				}
			}

			if (StringUtils.isNotBlank(req.getEmail2()) && req.getEmail2().length() > 20) {
				errorList.add("1034");
			} else if (StringUtils.isNotBlank(req.getEmail2())) {
				boolean b = isValidMail(req.getEmail2());

				if (b == false) {
					errorList.add("1035");
				}
			}
			if (StringUtils.isNotBlank(req.getEmail3()) && req.getEmail3().length() > 20) {
				errorList.add("1036");
			} else if (StringUtils.isNotBlank(req.getEmail3())) {
				boolean b = isValidMail(req.getEmail3());
				if (b == false) {
					errorList.add("1037");
				}
			}
			if (StringUtils.isBlank(req.getLanguage())) {
				errorList.add("1038");
			}

			if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail2()) && req.getEmail1().equalsIgnoreCase(req.getEmail2())) {
				errorList.add("1039");
			}
			if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail3()) && req.getEmail1().equalsIgnoreCase(req.getEmail3())) {
				errorList.add("1040");
			}
			if (StringUtils.isNotBlank(req.getEmail2()) && StringUtils.isNotBlank(req.getEmail3()) && req.getEmail2().equalsIgnoreCase(req.getEmail3())) {
				errorList.add("1041");
			}

			if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo2()) && req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo2())) {
				errorList.add("1042");
			}
			if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo3()) && req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo3())) {
				errorList.add("1042");
			}
			if (StringUtils.isNotBlank(req.getTelephoneNo2()) && StringUtils.isNotBlank(req.getTelephoneNo3())&& req.getTelephoneNo2().equalsIgnoreCase(req.getTelephoneNo3())) {
				errorList.add("1043");
			}

			if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo2()) && req.getMobileNo1().equalsIgnoreCase(req.getMobileNo2())) {
				errorList.add("1044");
			}
			if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo3())&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo3())) {
				errorList.add("1045");
			}
			if (StringUtils.isNotBlank(req.getMobileNo2()) && StringUtils.isNotBlank(req.getMobileNo3())&& req.getMobileNo2().equalsIgnoreCase(req.getMobileNo3())) {
				errorList.add("1046");
			}

			if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

				if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
					if (StringUtils.isBlank(req.getBusinessType())) {
						//errorList.add("1050");
					}
				}
			}

			/*if (StringUtils.isBlank(req.getIsTaxExempted())) {
				errorList.add("1055");

			}else if (req.getIsTaxExempted().equals("Y")) {
				if (StringUtils.isBlank(req.getTaxExemptedId())) {
					errorList.add("1056");
				} else if (req.getTaxExemptedId().length() > 20) {
					errorList.add("1057");
				}

			}*/

			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add("1058");
			} else if (req.getStatus().length() > 1) {
				errorList.add("1059");
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())|| "P".equals(req.getStatus()))) {
				errorList.add("1060");
			}


			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add("1063");
			} else if (req.getCreatedBy().length() > 100) {
				errorList.add("1064");
			}


			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add("1074");
			} else if (req.getBranchCode().length() > 20) {
				errorList.add("1075");
			}

			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add("1076");
			} else if (req.getProductId().length() > 20) {
				errorList.add("1077");
			}
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add("1078");
			} else if (req.getCompanyId().length() > 20) {
				errorList.add("1079");
			}





			List<EserviceCustomerDetails> list = new ArrayList<EserviceCustomerDetails>();
			if ((StringUtils.isNotBlank(req.getAddress1()))
				//	&& (StringUtils.isNotBlank(req.getAddress2()))
					&& (StringUtils.isNotBlank(req.getBranchCode()))
				//	&& (StringUtils.isNotBlank(req.getBusinessType()))
					&& (StringUtils.isNotBlank(req.getCityCode())) && (StringUtils.isNotBlank(req.getCityName()))
					&& (StringUtils.isNotBlank(req.getClientName()))
					&& (StringUtils.isNotBlank(req.getClientStatus()))
					&& (StringUtils.isNotBlank(req.getCompanyId())) && (StringUtils.isNotBlank(req.getCreatedBy()))
					// && (StringUtils.isNotBlank(req.getCustomerReferenceNo()))
					&& (StringUtils.isNotBlank(req.getEmail1()))
				//	&& (StringUtils.isNotBlank(req.getEmail2()))
				//	&& (StringUtils.isNotBlank(req.getEmail3())) && (StringUtils.isNotBlank(req.getFax()))
					&& (StringUtils.isNotBlank(req.getGender())) && (StringUtils.isNotBlank(req.getIdNumber()))
					&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
					&& (StringUtils.isNotBlank(req.getLanguage()))
				//	&& (StringUtils.isNotBlank(req.getLanguageDesc()))
					&& (StringUtils.isNotBlank(req.getMobileNo1()))
				//	&& (StringUtils.isNotBlank(req.getMobileNo2()))
				//	&& (StringUtils.isNotBlank(req.getMobileNo3()))
					&& (StringUtils.isNotBlank(req.getNationality()))
					&& (StringUtils.isNotBlank(req.getOccupation()))
					&& (StringUtils.isNotBlank(req.getPlaceOfBirth()))
					&& (StringUtils.isNotBlank(req.getPolicyHolderType()))
					&& (StringUtils.isNotBlank(req.getPolicyHolderTypeid()))
					&& (StringUtils.isNotBlank(req.getProductId())) && (StringUtils.isNotBlank(req.getRegionCode()))
					&& (StringUtils.isNotBlank(req.getStateCode())) && (StringUtils.isNotBlank(req.getStateName()))
					&& (StringUtils.isNotBlank(req.getStatus()))
				//	&& (StringUtils.isNotBlank(req.getStreet()))
				//	&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
				//	&& (StringUtils.isNotBlank(req.getTelephoneNo1()))
				//	&& (StringUtils.isNotBlank(req.getTelephoneNo2()))
				//  && (StringUtils.isNotBlank(req.getTelephoneNo3())) && (StringUtils.isNotBlank(req.getTitle()))
					&& (req.getDobOrRegDate()!=null)
					&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
				//	&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
					&& (StringUtils.isNotBlank(req.getPreferredNotification()))
				//	&& (req.getAppointmentDate()!=null)

					){

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);
				// Find all
				Root<EserviceCustomerDetails> b = query.from(EserviceCustomerDetails.class);
				// Select
				query.select(b);
				// Where

				Predicate n1 = (cb.like(cb.lower(b.get("address1")), req.getAddress1().toLowerCase()));
			//	Predicate n2 = (cb.like(cb.lower(b.get("address2")), req.getAddress2().toLowerCase()));
				Predicate n3 = (cb.like(cb.lower(b.get("branchCode")), req.getBranchCode().toLowerCase()));
			//	Predicate n4 = (cb.like(cb.lower(b.get("businessType")), req.getBusinessType().toLowerCase()));
			//	Predicate n5 = (cb.like(cb.lower(b.get("cityCode")), req.getCityCode().toLowerCase()));
				Predicate n5 =	(cb.equal(b.get("cityCode") ,  null != req.getCityCode() &&
						req.getCityCode().matches("[0-9]+") ? Integer.valueOf(req.getCityCode()) : 0 ));
				Predicate n6 = (cb.like(cb.lower(b.get("cityName")), req.getCityName().toLowerCase()));
				Predicate n7 = (cb.like(cb.lower(b.get("clientName")), req.getClientName().toLowerCase()));
				Predicate n8 = (cb.like(cb.lower(b.get("clientStatus")), req.getClientStatus().toLowerCase()));
				Predicate n9 = (cb.like(cb.lower(b.get("companyId")), req.getCompanyId().toLowerCase()));
				Predicate n10 = (cb.like(cb.lower(b.get("createdBy")), req.getCreatedBy().toLowerCase()));
				// Predicate n11 =
				// (cb.like(cb.lower(b.get("customerReferenceNo")),req.getCustomerReferenceNo().toLowerCase()));
				Predicate n12 = (cb.equal(b.get("dobOrRegDate"), req.getDobOrRegDate()));
				Predicate n13 = (cb.like(cb.lower(b.get("email1")), req.getEmail1().toLowerCase()));
			//	Predicate n14 = (cb.like(cb.lower(b.get("email2")), req.getEmail2().toLowerCase()));
			//	Predicate n15 = (cb.like(cb.lower(b.get("email3")), req.getEmail3().toLowerCase()));
			//	Predicate n16 = (cb.equal(b.get("fax"), req.getFax().toLowerCase()));
				Predicate n17 = (cb.like(cb.lower(b.get("gender")), req.getGender().toLowerCase()));
				Predicate n18 = (cb.like(cb.lower(b.get("idNumber")), req.getIdNumber().toLowerCase()));
				Predicate n19 = (cb.like(cb.lower(b.get("isTaxExempted")), req.getIsTaxExempted().toLowerCase()));
				Predicate n20 = (cb.like(cb.lower(b.get("language")), req.getLanguage().toLowerCase()));
			//	Predicate n21 = (cb.like(cb.lower(b.get("languageDesc")), req.getLanguageDesc().toLowerCase()));
				Predicate n22 = (cb.equal(b.get("mobileNo1"), req.getMobileNo1()));
			//	Predicate n23 = (cb.equal(b.get("mobileNo2"), req.getMobileNo2()));
			//	Predicate n24 = (cb.equal(b.get("mobileNo3"), req.getMobileNo3()));
				Predicate n25 = (cb.like(cb.lower(b.get("nationality")), req.getNationality().toLowerCase()));
				Predicate n26 = (cb.like(cb.lower(b.get("occupation")), req.getOccupation().toLowerCase()));
				Predicate n27 = (cb.like(cb.lower(b.get("placeOfBirth")), req.getPlaceOfBirth().toLowerCase()));
				Predicate n28 = (cb.like(cb.lower(b.get("policyHolderType")),
						req.getPolicyHolderType().toLowerCase()));
			//	Predicate n29 = (cb.like(cb.lower(b.get("policyHolderTypeId")),
			//			req.getPolicyHolderTypeid().toLowerCase()));
			//	Predicate n30 = (cb.like(cb.lower(b.get("productId")), req.getProductId()));

				Predicate n30 = (cb.equal(b.get("productId") ,  null != req.getProductId() &&
						req.getProductId().matches("[0-9]+") ? Integer.valueOf(req.getProductId()) : 0 ));
				Predicate n31 = (cb.like(cb.lower(b.get("regionCode")), req.getRegionCode().toLowerCase()));
			//	Predicate n32 = (cb.like(cb.lower(b.get("stateCode")), req.getStateCode().toLowerCase()));
				Predicate n33 = (cb.like(cb.lower(b.get("stateName")), req.getStateName().toLowerCase()));
				Predicate n34 = (cb.like(cb.lower(b.get("status")), req.getStatus().toLowerCase()));
			//	Predicate n35 = (cb.like(cb.lower(b.get("street")), req.getStreet().toLowerCase()));
			//	Predicate n36 = (cb.like(cb.lower(b.get("taxExemptedId")), req.getTaxExemptedId().toLowerCase()));
			//	Predicate n37 = (cb.equal(b.get("telephoneNo1"), req.getTelephoneNo1()));
			//	Predicate n38 = (cb.equal(b.get("telephoneNo2"), req.getTelephoneNo2()));
			//	Predicate n39 = (cb.equal(b.get("telephoneNo3"), req.getTelephoneNo3()));
			//	Predicate n40 = (cb.like(cb.lower(b.get("title")), req.getTitle().toLowerCase()));
			//	Predicate n41 = (cb.equal(b.get("appointmentDate"), req.getAppointmentDate()));
				Predicate n42 = (cb.like(cb.lower(b.get("preferredNotification")), req.getPreferredNotification().toLowerCase()));

				query.where(n1,  n3,  n6, n7, n8, n9, n10,
						// n11,
						n12, n13,  n17, n18, n19, n20,  n22, n25, n26, n27, n28,
						n30, n31,  n33, n34, /*n35,*/  n42);
				// Get Result
				TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
				list = result.getResultList();
				if (list.size() > 0 && req.getCustomerReferenceNo()==null) {
					errorList.add("1511");

				}
			}
		}

		else if (req.getSaveOrSubmit().equalsIgnoreCase("Save")) {
			if (StringUtils.isBlank(req.getClientName())) {
				errorList.add("1084");
			} else if (req.getClientName().length() > 100) {
				errorList.add("1085");
			}
			if (StringUtils.isBlank(req.getPolicyHolderType())) {
				errorList.add("1086");
			}


		}
	}catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		errorList.add("01");
	}
		return errorList;


	}
	public List<String> validateInsuredDetails1(EserviceCustomerSaveReq req) {
		List<String> errorList = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {


			if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {

				if("100040".equalsIgnoreCase(req.getCompanyId())) {

					if("2".equalsIgnoreCase(req.getPolicyHolderType()))
					{
						if (StringUtils.isBlank(req.getClientName()) ) {
							//errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
							errorList.add("1100");
						} else if (req.getClientName().length() > 100) {
						   errorList.add("1101");
							//errorList.add(new Error("01", "ClientName", "Please Enter ClientName with in 250 Character "));
						}
						else if (StringUtils.isNotBlank(req.getClientName()) &&
						         (req.getClientName().matches("^[0-9].*") ||
						                 !req.getClientName().matches("[a-zA-ZÀ-ÿ0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?\\s'-]+$"))){
							errorList.add("1102");
							//errorList.add(new Error("01", "ClientName", "Please Enter Valid ClientName "));
						}
					}
					else
					{
						if (StringUtils.isBlank(req.getClientName()) ) {
							//errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
							errorList.add("1001");
						} else if (req.getClientName().length() > 100) {
						   errorList.add("1002");
							//errorList.add(new Error("01", "ClientName", "Please Enter ClientName with in 250 Character "));
						}
						else if (StringUtils.isNotBlank(req.getClientName()) &&
						         (req.getClientName().matches("^[0-9].*") ||
						                 !req.getClientName().matches("[a-zA-ZÀ-ÿ0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?\\s'-]+$"))){
							errorList.add("1003");
							//errorList.add(new Error("01", "ClientName", "Please Enter Valid ClientName "));
						}
					}

				}
				else {
					if (StringUtils.isBlank(req.getClientName()) ) {
						//errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
						errorList.add("1001");
					} else if (req.getClientName().length() > 250) {
					   errorList.add("1002");
						//errorList.add(new Error("01", "ClientName", "Please Enter ClientName with in 250 Character "));
					}
					else if (StringUtils.isNotBlank(req.getClientName())&& !req.getClientName().matches("[a-zA-Z.&() ]+") && !req.getClientName().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")){
						errorList.add("1003");
						//errorList.add(new Error("01", "ClientName", "Please Enter Valid ClientName "));
					}
				}






//				if("100040".equalsIgnoreCase(req.getCompanyId()))
//				{
//					if (StringUtils.isBlank(req.getAddress1())) {
//						errorList.add("1004");
//						//errorList.add(new Error("02", "Address1", "Please Enter Address "));
//					} else if (req.getAddress1().length() > 50) {
//						errorList.add("1009");
//						//errorList.add(new Error("02", "Address1", "Please Enter Address within 100 Characters"));
//					}
//				}
//				else {
//					if (StringUtils.isBlank(req.getAddress1())) {
//						errorList.add("1004");
//						//errorList.add(new Error("02", "Address1", "Please Enter Address "));
//					} else if (req.getAddress1().length() > 100) {
//						errorList.add("1009");
//						//errorList.add(new Error("02", "Address1", "Please Enter Address within 100 Characters"));
//					}
//				}
//
				if("100040".equalsIgnoreCase(req.getCompanyId()))
				{
					if (req.getAddress2().length() > 50) {
						errorList.add("1000");
					}
				}


//				if (StringUtils.isBlank(req.getStreet())) {
//					errorList.add(new Error("03", "Street", "Please Enter Street"));
//				} else if (req.getAddress1().length() > 100) {
//					errorList.add(new Error("03", "Street", "Please Enter Street within 100 Characters"));
//				}

				/*if (StringUtils.isBlank(req.getAddress2())) {
					errorList.add(new Error("02", "Address2", "Please Enter Address2 "));
				} else if (req.getAddress2().length() > 100) {
					errorList.add(new Error("03", "Address2", "Please Enter Address2 within 100 Characters"));
				}*/

				if (StringUtils.isBlank(req.getClientStatus())) {
					//errorList.add(new Error("05", "Client Status", "Please Select Client Status"));
					errorList.add("1010");
				}
				if (StringUtils.isBlank(req.getIdType())) {
					errorList.add("1011");
					//errorList.add(new Error("09", "IdType", "Please Select Personal/Corporate"));
				}

				if (StringUtils.isBlank(req.getPolicyHolderTypeid())) {
					errorList.add("1012");
					//errorList.add(new Error("09", " Identity Type", "Please Select Identity Type"));
				}


				if("100040".equalsIgnoreCase(req.getCompanyId()))	{
					if (StringUtils.isBlank(req.getIdNumber())) {
						errorList.add("1013");
						//errorList.add(new Error("11", "IdNumber", "Please Enter Id Number"));
					} else if (req.getIdNumber().length() > 15) {
						errorList.add("1014");
						//errorList.add(new Error("11", "IdNumber", "Please Enter Id Number within 100 Characters"));
					}  else if (req.getIdNumber().matches("[0-9]+") && Double.valueOf(req.getIdNumber()) <=0 ) {
						errorList.add("1015");
						//errorList.add(new Error("11", "IdNumber", "Please Enter Valid Id Number "));
					} else if(!req.getIdNumber().matches("[a-zA-Z0-9-]+")) {

						errorList.add("1015");
					}
				}
				else
				{
					if (StringUtils.isBlank(req.getIdNumber())) {
						errorList.add("1013");
						//errorList.add(new Error("11", "IdNumber", "Please Enter Id Number"));
					} else if (req.getIdNumber().length() > 100) {
						errorList.add("1014");
						//errorList.add(new Error("11", "IdNumber", "Please Enter Id Number within 100 Characters"));
					}  else if (req.getIdNumber().matches("[0-9]+") && Double.valueOf(req.getIdNumber()) <=0 ) {
						errorList.add("1015");
						//errorList.add(new Error("11", "IdNumber", "Please Enter Valid Id Number "));
					} else if(!req.getIdNumber().matches("[a-zA-Z0-9-]+")) {
						errorList.add("1015");
					}
				}



//				else if(!containsOnlyNumbers(req.getIdNumber()))
//				{
//					errorList.add("1015");
//				}


//				else if (! req.getIdNumber().matches("[A-Za-z0-9]+") ) {
//					errorList.add(new Error("11", "IdNumber", "Please Enter Valid IdNumber "));
//				}


//				if (StringUtils.isBlank(req.getPreferredNotification())) {
//					errorList.add(new Error("12", "PreferredNotification", "Please select Preferred Notification"));
//				}

//				if(StringUtils.isNotBlank(req.getAppointmentDate().toString())) {
//				cal.add(Calendar.DATE, -1);
//				Date yesterday = cal.getTime();
//				String a1 = sdf.format(req.getAppointmentDate());
//				Date a = sdf.parse(a1);
//
//				if (a.before(yesterday)) {
//					errorList.add(new Error("07", "Appointment Date", "Please Enter Appointment Date as Future Date"));
//					}
//				}
				//				Date today2 = new Date();
//				cal.setTime(today2);
//				cal.set(Calendar.HOUR_OF_DAY, 1);
//				cal.set(Calendar.MINUTE, 1);
//				today2 =  cal.getTime()	;
//				if (req.getAppointmentDate() == null ) {
//					errorList.add(new Error("12", "AppointmentDate", "Please select AppointmentDate"));
//				} else {
//					cal.setTime(req.getAppointmentDate());
//					cal.set(Calendar.HOUR_OF_DAY, 5);
//					cal.set(Calendar.MINUTE, 5);
//					Date appDate =  cal.getTime()	;
//					if (appDate.before(today2) ) {
//						errorList.add(new Error("12", "AppointmentDate", "Please Enter AppointmentDate As FuturDate"));
//					}
//				}
				/*
				 * if (StringUtils.isBlank(req.getPlaceOfBirth())) { errorList.add(new
				 * Error("13", "PlaceOfBirth", "Please Enter PlaceOfBirth ")); } else if
				 * (req.getPlaceOfBirth().length() > 100) { errorList.add(new Error("13",
				 * "PlaceOfBirth", "Please Enter PlaceOfBirth within 100 Characters")); } if
				 * (StringUtils.isBlank(req.getGender())) { errorList.add(new Error("14",
				 * "Gender", "Please Select Gender")); }
				 *
				 * if (StringUtils.isBlank(req.getOccupation())) { errorList.add(new Error("15",
				 * "Occupation", "Please Select Occupation")); }
				 */

//
//			if (req.getVrnGst().length() > 20) {
//				errorList.add(new Error("17", "VrnGst", "Please Enter VrnGst within 20 Characters"));
//			}

				if("100040".equalsIgnoreCase(req.getCompanyId() ))
				{
					if (StringUtils.isNotBlank(req.getPinCode())) {
						 if (! (req.getPinCode().matches("[0-9a-zA-Z]+") ||  req.getPinCode().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")) ) {
							 errorList.add("3000");

//							 new Error("18", "PinCode", "Please Enter Valid Number In Po Box")

						 }
						if (req.getPinCode().length() > 10) {
							errorList.add("1016");
							//errorList.add(new Error("18", "PinCode", "Please Enter Po Box within 20 Characters"));
						}
					}
				}
				else
				{
					if (StringUtils.isNotBlank(req.getPinCode())) {
//						 if (! req.getPinCode().matches("[0-9a-bA-Z]+") ) {
//							 errorList.add(new Error("18", "PinCode", "Please Enter Valid Number In Po Box"));
//
//						 } else
						if (req.getPinCode().length() > 20) {
							errorList.add("1016");
							//errorList.add(new Error("18", "PinCode", "Please Enter Po Box within 20 Characters"));
						}
					}
				}



				/*if (StringUtils.isBlank(req.getStreet())) {
					errorList.add(new Error("19", "Street", "Please Enter Street"));
				}
				else if (StringUtils.isNotBlank(req.getStreet()) && req.getStreet().length() > 100) {
					errorList.add(new Error("19", "Street", "Please Enter Street within 100 Characters"));
				}*/
				if (StringUtils.isNotBlank(req.getFax()) && req.getFax().length() > 20) {
					errorList.add("1017");
					//errorList.add(new Error("20", "Fax", "Please Enter Fax within 20 Characters"));
				}
				/*if (StringUtils.isBlank(req.getTelephoneNo1())) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo1"));
				}
				else if (StringUtils.isNotBlank(req.getTelephoneNo1()) && req.getTelephoneNo1().length() > 20) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo within 20 Characters"));
				} else if (!req.getTelephoneNo1().matches("\\d+")) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo only in numbers"));
				} */
				if (StringUtils.isNotBlank(req.getTelephoneNo2()) && req.getTelephoneNo2().length() > 20) {
					errorList.add("1018");
					//errorList.add(new Error("22", "TelephoneNo2", "Please Enter TelephoneNo2 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getTelephoneNo2()) && !req.getTelephoneNo2().matches("\\d+")) {
					errorList.add("1019");
					//errorList.add(new Error("22", "TelephoneNo2", "Please Enter TelephoneNo2 only in numbers"));
				}

				if (StringUtils.isNotBlank(req.getTelephoneNo3()) && req.getTelephoneNo3().length() > 20) {
					errorList.add("1020");
					//errorList.add(new Error("23", "TelephoneNo3", "Please Enter TelephoneNo3 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getTelephoneNo3()) && !req.getTelephoneNo3().matches("\\d+")) {
					errorList.add("1021");
					//errorList.add(new Error("23", "TelephoneNo3", "Please Enter TelephoneNo3 only in numbers"));
				}

//				if (StringUtils.isBlank(req.getOccupation()) ) {
//					errorList.add("1022");
//					//errorList.add(new Error("23", "Occupation", "Please Select Occupation"));
//				} else if(req.getOccupation().equalsIgnoreCase("99999")){
//					if (StringUtils.isBlank(req.getOtherOccupation()) ) {
//						errorList.add("1023");
//						//errorList.add(new Error("47", "Other Occupation", "Please Enter Other Occupation"));
//					}else if (req.getOtherOccupation().length() > 100){
//						errorList.add("1024");
//						//errorList.add(new Error("47","Other Occupation", "Please Enter Other Occupation within 100 Characters"));
//					}else if(!req.getOtherOccupation().matches("[a-zA-Z\\s]+")){
//						errorList.add("1025");
//						//errorList.add(new Error("47","Other Occupation", "Please Enter Valid Other Occupation"));
//					}
//					}

				if (StringUtils.isBlank(req.getMobileNo1())) {
					errorList.add("1026");
					//errorList.add(new Error("24", "MobileNo", "Please Enter MobileNo"));
				} else if (req.getMobileNo1().length() > 10||req.getMobileNo1().length() < 8) {
					errorList.add("1027");
					//errorList.add(new Error("24", "MobileNo", "Please Enter Valid Mobile No"));
				} else if (!req.getMobileNo1().matches("[0-9]+") ) {
					errorList.add("1028");
					//errorList.add(new Error("24", "MobileNo", "Please Enter Mobile No only in numbers"));
				} else if (req.getMobileNo1().matches("[0-9]+") && Double.valueOf(req.getMobileNo1()) <=0 ) {
					errorList.add("1029");
					//errorList.add(new Error("11", "MobileNo", "Please Enter Valid Mobile No "));
				}



				if (StringUtils.isNotBlank(req.getMobileNo3()) &&( req.getMobileNo3().length() > 10||req.getMobileNo3().length() < 10)) {
					errorList.add("1030");
					//errorList.add(new Error("26", "MobileNo3", "Please Enter MobileNo3 must be 10 digts"));
				} else if (StringUtils.isNotBlank(req.getMobileNo3()) && !req.getMobileNo3().matches("\\d+")) {
					errorList.add("1031");
					//errorList.add(new Error("26", "MobileNo3", "Please Enter MobileNo3 only in numbers"));
				}
//				if (StringUtils.isBlank(req.getEmail1())) {
//					errorList.add(new Error("27", "Email1", "Please Enter Email"));
//				} else

				if("100040".equalsIgnoreCase(req.getCompanyId()))
				{

					if("2".equalsIgnoreCase(req.getPolicyHolderType()))
					{
						if ( StringUtils.isNotBlank(req.getEmail1()) ) {
							if( req.getEmail1().length() > 50 ) {
								errorList.add("1032");
								//errorList.add(new Error("27", "Email1", "Please Enter Email within 100 Characters"));
							} else if(StringUtils.isNotBlank(req.getEmail1())) {
								boolean bValue = checkIsValidMail(req.getEmail1());

								if(req.getEmail1().matches("^[0-9].*") || !req.getEmail1().matches(".*@.*\\..*") )
								{
									errorList.add("1033");
								}
								else if (!bValue) {
									errorList.add("1033");
								}
							}
						}
						else {
							errorList.add("3001");
						}

					}
					else
					{
						if ( StringUtils.isNotBlank(req.getEmail1()) ) {
							if( req.getEmail1().length() > 50 ) {
								errorList.add("1032");
								//errorList.add(new Error("27", "Email1", "Please Enter Email within 100 Characters"));
							} else if(StringUtils.isNotBlank(req.getEmail1())) {
								boolean bValue = checkIsValidMail(req.getEmail1());

								if(req.getEmail1().matches("^[0-9].*") || !req.getEmail1().matches(".*@.*\\..*") )
								{
									errorList.add("1033");
								}
								else if (!bValue) {
									errorList.add("1033");
								}
							}
						}
					}

				}
				else {
					if ( StringUtils.isNotBlank(req.getEmail1()) ) {
						if( req.getEmail1().length() > 100 ) {
							errorList.add("1032");
							//errorList.add(new Error("27", "Email1", "Please Enter Email within 100 Characters"));
						} else if(StringUtils.isNotBlank(req.getEmail1())) {
							boolean b = isValidMail(req.getEmail1());
							if (b == false && (!req.getEmail1().matches("^[a-zA-ZÀ-ÿ\\s'-]+$") || !req.getEmail1().matches("^[.@]+$"))) {
								errorList.add("1033");
							}
						}
					}
				}






				if (StringUtils.isNotBlank(req.getEmail2()) && req.getEmail2().length() > 20) {
					errorList.add("1034");
					//errorList.add(new Error("28", "Email2", "Please Enter Email2 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getEmail2())) {
					boolean b = isValidMail(req.getEmail2());

					if (b == false) {
						errorList.add("1035");
						//errorList.add(new Error("28", "Email2", "Please Enter Email2 in correct format"));
					}
				}
				if (StringUtils.isNotBlank(req.getEmail3()) && req.getEmail3().length() > 20) {
					errorList.add("1036");
					//errorList.add(new Error("29", "Email3", "Please Enter Email3 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getEmail3())) {
					boolean b = isValidMail(req.getEmail3());
					if (b == false) {
						errorList.add("1037");
						//errorList.add(new Error("29", "Email3", "Please Enter Email3 in correct format"));
					}
				}
				if (StringUtils.isBlank(req.getLanguage())) {
					errorList.add("1038");
					//errorList.add(new Error("30", "Language", "Please Select Language"));
				}

				if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail2())
						&& req.getEmail1().equalsIgnoreCase(req.getEmail2())) {
					errorList.add("1039");
					//errorList.add(new Error("28", "Email2", "Email2 Is Already Available In Email"));
				}
				if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail1().equalsIgnoreCase(req.getEmail3())) {
					errorList.add("1040");
					//errorList.add(new Error("28", "Email2", "Email3 Is Already Available In Email"));
				}
				if (StringUtils.isNotBlank(req.getEmail2()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail2().equalsIgnoreCase(req.getEmail3())) {
					errorList.add("1041");
				}

				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo2())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo2())) {
					errorList.add("1042");
					//errorList.add(new Error("28", "TelephoneNo2", "TelephoneNo2 Is Already Available In TelephoneNo"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add("1042");
					//errorList.add(new Error("28", "TelephoneNo2", "TelephoneNo2 Is Already Available In TelephoneNo"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo2()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo2().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add("1043");
					//errorList.add(new Error("28", "TelephoneNo3", "TelephoneNo3 Is Already Available In TelephoneNo2"));
				}

				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo2())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo2())) {
					errorList.add("1044");
					//errorList.add(new Error("28", "MobileNo2", "MobileNo2 Is Already Available In MobileNo"));
				}
				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add("1045");
					//errorList.add(new Error("28", "MobileNo2", "MobileNo3 Is Already Available In MobileNo"));
				}
				if (StringUtils.isNotBlank(req.getMobileNo2()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo2().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add("1046");
					//errorList.add(new Error("28", "MobileNo3", "MobileNo3 Is Already Available In MobileNo2"));
				}
//
//				if (StringUtils.isNotBlank(req.getAddress1()) && StringUtils.isNotBlank(req.getAddress2())
//						&& req.getAddress1().equalsIgnoreCase(req.getAddress2())) {
//					errorList.add(new Error("28", "Address2", "Address2 Is Already Available In Address"));
//				}




				// Status Validation
//				if(StringUtils.isNotBlank(req.getCompanyId()) && "100004".equalsIgnoreCase(req.getCompanyId())) {
//					//
//					if(req.getPolicyHolderType().equalsIgnoreCase("2")) {
//					if (StringUtils.isNotBlank(req.getMobileNo2()) && req.getMobileNo2().length() > 20) {
//						errorList.add(new Error("25", "MobileNo2", "Please Enter MobileNo2 within 20 Characters"));
//					} else if (StringUtils.isNotBlank(req.getMobileNo2()) && !req.getMobileNo2().matches("\\d+")) {
//						errorList.add(new Error("25", "MobileNo2", "Please Enter MobileNo2 only in numbers"));
//					}
//					}
//
//				} else {
//					if (StringUtils.isBlank(req.getTitle()))  {
//						errorList.add(new Error("04", "Title", "Please Select Title"));
//					}
//					if (StringUtils.isBlank(req.getNationality())) {
//						errorList.add(new Error("12", "Country", "Please select Country"));
//					}
//					if (StringUtils.isBlank(req.getPreferredNotification())) {
//						errorList.add(new Error("09", "Preferred Notification", "Please Select Preferred Notification"));
//					}
//
//					if (StringUtils.isNotBlank(req.getPolicyHolderType())) {
//
//						if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
//							if (StringUtils.isBlank(req.getBusinessType())) {
//								errorList.add(new Error("16", "BusinessType", "Please Select BusinessType"));
//							}
//						}
//					}
//					if( StringUtils.isNotBlank(req.getPolicyHolderType()) && req.getPolicyHolderType().equalsIgnoreCase("2") ) {
//						if (StringUtils.isBlank(req.getVrTinNo())) {
//							errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number"));
//						} else if (req.getVrTinNo().length() > 20) {
//							errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number within 20 Characters"));
//						}
//
//					}
//					if (StringUtils.isBlank(req.getRegionCode())) {
//						errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode"));
//					} else if (req.getRegionCode().length() > 20) {
//						errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode within 20 Characters"));
//					}
//
//					if (StringUtils.isBlank(req.getIsTaxExempted())) {
//						errorList.add(new Error("31", "IsTaxExempted", "Please Select IsTaxExempted"));
//
//					}else if (req.getIsTaxExempted().equals("Y")) {
//						if (StringUtils.isBlank(req.getTaxExemptedId())) {
//							errorList.add(new Error("32", "TaxExemptedId", "Please Enter TaxExemptedId"));
//						} else if (req.getTaxExemptedId().length() > 20) {
//							errorList.add(
//									new Error("33", "TaxExemptedId", "Please Enter TaxExemptedId within 20 Characters"));
//						}
//
//					}
//					if (StringUtils.isBlank(req.getStatus())) {
//						errorList.add(new Error("34", "Status", "Please Enter Status"));
//					} else if (req.getStatus().length() > 1) {
//						errorList.add(new Error("34", "Status", "Enter Status in 1 Character Only"));
//					} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())
//							|| "P".equals(req.getStatus()))) {
//						errorList.add(new Error("34", "Status", "Plese Enter Status"));
//					}
//					if (StringUtils.isBlank(req.getStateCode())) {
//						errorList.add(new Error("45", "RegionCode", "Please Enter RegionCode "));
//					}
//
//					if (StringUtils.isBlank(req.getMobileCode1())) {
//						errorList.add(new Error("46", "MobileCode", "Please Select MobileCode "));
//					}
//				}


				if (StringUtils.isBlank(req.getTitle()))  {
					errorList.add("1047");
					//errorList.add(new Error("04", "Title", "Please Select Title"));
				}
				if (StringUtils.isBlank(req.getNationality())) {
					errorList.add("1048");
					//errorList.add(new Error("12", "Country", "Please select Country"));
				}
//				if (StringUtils.isBlank(req.getPreferredNotification())) {
//					errorList.add("1049");
//					//errorList.add(new Error("09", "Preferred Notification", "Please Select Preferred Notification"));
//				}

				if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
//						if (StringUtils.isBlank(req.getBusinessType())) {
//							errorList.add("1050");
//							//errorList.add(new Error("16", "BusinessType", "Please Select BusinessType"));
//						}
					}
				}
				if( StringUtils.isNotBlank(req.getPolicyHolderType()) && req.getPolicyHolderType().equalsIgnoreCase("2") ) {
//					if (StringUtils.isBlank(req.getVrTinNo())) {
//						errorList.add("1051");
//						//errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number"));
//					} else if (req.getVrTinNo().length() > 20) {
//						errorList.add("1052");
//						//errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number within 20 Characters"));
//					}

				}
				if (StringUtils.isBlank(req.getRegionCode())) {
					errorList.add("1053");
					//errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode"));
				} else if (req.getRegionCode().length() > 20) {
					errorList.add("1054");
					//errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode within 20 Characters"));
				}

//				if (StringUtils.isBlank(req.getIsTaxExempted())) {
//					errorList.add("1055");
//					//errorList.add(new Error("31", "IsTaxExempted", "Please Select IsTaxExempted"));
//
//				}else if (req.getIsTaxExempted().equals("Y")) {
//					if (StringUtils.isBlank(req.getTaxExemptedId())) {
//						errorList.add("1056");
//						//errorList.add(new Error("32", "TaxExemptedId", "Please Enter TaxExemptedId"));
//					} else if (req.getTaxExemptedId().length() > 20) {
//						errorList.add("1057");
//						//errorList.add(new Error("33", "TaxExemptedId", "Please Enter TaxExemptedId within 20 Characters"));
//					}
//
//				}
				if (StringUtils.isBlank(req.getStatus())) {
					errorList.add("1058");
					//errorList.add(new Error("34", "Status", "Please Enter Status"));
				} else if (req.getStatus().length() > 1) {
					errorList.add("1059");
					//errorList.add(new Error("34", "Status", "Enter Status in 1 Character Only"));
				} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())
						|| "P".equals(req.getStatus()))) {
					errorList.add("1060");
					//errorList.add(new Error("34", "Status", "Plese Enter Status"));
				}
				if (StringUtils.isBlank(req.getStateCode())) {
					errorList.add("1061");
					//errorList.add(new Error("45", "RegionCode", "Please Enter RegionCode "));
				}

				if (StringUtils.isBlank(req.getMobileCode1())) {
					errorList.add("1062");
					//errorList.add(new Error("46", "MobileCode", "Please Select MobileCode "));
				}

				if (StringUtils.isBlank(req.getCreatedBy())) {
					errorList.add("1063");
					//errorList.add(new Error("35", "CreatedBy", "Please Enter CreatedBy "));
				} else if (req.getCreatedBy().length() > 100) {
					errorList.add("1064");
					//errorList.add(new Error("35", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
				}


				// Date Validation
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
//				if(StringUtils.isNotBlank(req.getCompanyId()) && ! (req.getCompanyId().equalsIgnoreCase("100019")|| req.getCompanyId().equalsIgnoreCase("100027"))) {
//
//					// DOB Validation
//					if (StringUtils.isNotBlank(req.getPolicyHolderType()) && req.getPolicyHolderType().equalsIgnoreCase("1")) {
//						if( StringUtils.isNotBlank(req.getIdType()) && req.getIdType().equalsIgnoreCase("1")) {
//							if (req.getDobOrRegDate() == null) {
//								errorList.add("1065");
//								//errorList.add(new Error("38", "DobOrRegDate", "Please Select Dob "));
//							}
//						}
//
//						try {
//						if (req.getDobOrRegDate() != null) {
//							if (req.getDobOrRegDate().after(today)) {
//								errorList.add("1066");
//								//errorList.add(new Error("38", "DobOrRegDate", "Please Enter Dob as Past Date"));
//
//							} else {
//								LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
//										.toLocalDate();
//								LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//								Integer years = Period.between(localDate1, localDate2).getYears();
//								if (years > 100) {
//									errorList.add("1067");
//									//errorList.add(new Error("38", "DobOrRegDate", "Dob Not Accepted More than 100 Years"));
//
//								} else if (years < 18) {
//									errorList.add("1068");
//									//errorList.add(new Error("38", "DobOrRegDate", "Dob Not Accepted Less than 18 Years For Induvidual"));
//
//								}
//
//							}
//
//						}else {
//							errorList.add("1069");
//							//errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
//						}
//						}catch (Exception e) {
//							errorList.add("1070");
//							//errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
//						}
//					}
//					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
//						try {
//						if (req.getDobOrRegDate() != null) {
//							cal.setTime(today);
//							cal.add(Calendar.DAY_OF_MONTH, +1);
//							cal.set(Calendar.HOUR_OF_DAY, 23);
//							cal.set(Calendar.MINUTE, 50);
//							Date tomorrow = cal.getTime();
//							if (req.getDobOrRegDate().after(tomorrow)) {
//								errorList.add("1071");
//								//errorList.add(new Error("38", "DobOrRegDate", "Please Enter RegDate as Past Date"));
//
//							} else if(req.getDobOrRegDate()!=null ) {
//								LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
//										.toLocalDate();
//								LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//								Integer years = Period.between(localDate1, localDate2).getYears();
//								if (years > 100) {
//									errorList.add("1072");
//									//errorList.add(new Error("38", "DobOrRegDate", "RegDate Not Accepted More than 100 Years"));
//								}
//							}
//
//						}else {
//							errorList.add("1073");
//							//errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
//						}
//						}catch (Exception e) {
//							errorList.add("1073");
//							//errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
//						}
//					}
//				}


				if (StringUtils.isBlank(req.getBranchCode())) {
					errorList.add("1074");
					//errorList.add(new Error("39", "BranchCode", "Please Enter BranchCode "));
				} else if (req.getBranchCode().length() > 20) {
					errorList.add("1075");
				}

//				if (StringUtils.isBlank(req.getBranchCode())) {
//					errorList.add(new Error("39", "BranchCode", "Please Enter BranchCode "));
//				}
				if (StringUtils.isBlank(req.getProductId())) {
					errorList.add("1076");
				} else if (req.getProductId().length() > 20) {
					errorList.add("1077");
				}
				if (StringUtils.isBlank(req.getCompanyId())) {
					errorList.add("1078");
				} else if (req.getCompanyId().length() > 20) {
					errorList.add("1079");
				}




//			if (StringUtils.isBlank(req.getStateName())) {
//				errorList.add(new Error("43", "StateName", "Please Select StateName"));
//			}
//				if (req.getCompanyId().equalsIgnoreCase("100004") ) {
//					if(   StringUtils.isBlank(req.getCityName())) {
//						errorList.add("1080");
//						//errorList.add(new Error("11", "CityName", "Please Enter District"));
//					} else if (req.getCityName().length() > 100) {
//						errorList.add("1081");
//						///errorList.add(new Error("11", "CityName", "Please Enter District within 100 Characters"));
//					}
//				} else {
//					if (StringUtils.isBlank(req.getCityName())) {
//						errorList.add("1082");
//						//errorList.add(new Error("43", "District", "Please Select District "));
//					} else if (req.getCityName().length() > 100) {
//						errorList.add("1083");
//						//errorList.add(new Error("43", "District", "Please Enter District within 100 Characters"));
//					}
//				}



				/*if (StringUtils.isBlank(req.getStreet())) {
					errorList.add(new Error("44", "Street", "Please Enter Street "));
				} else if (req.getStreet().length() > 100) {
					errorList.add(new Error("44", "Street", "Please Enter Street within 100 Characters"));
				}*/



//				if (StringUtils.isBlank(req.getWhatsappCode())) {
//					errorList.add(new Error("47", "WhatsappCode", "Please Select WhatsappCode "));
//				}

				List<EserviceInsuredDetails> list = new ArrayList<EserviceInsuredDetails>();
				if ((StringUtils.isNotBlank(req.getAddress1()))
					//	&& (StringUtils.isNotBlank(req.getAddress2()))
						&& (StringUtils.isNotBlank(req.getBranchCode()))
					//	&& (StringUtils.isNotBlank(req.getBusinessType()))
						&& (StringUtils.isNotBlank(req.getCityCode())) && (StringUtils.isNotBlank(req.getCityName()))
						&& (StringUtils.isNotBlank(req.getClientName()))
						&& (StringUtils.isNotBlank(req.getClientStatus()))
						&& (StringUtils.isNotBlank(req.getCompanyId())) && (StringUtils.isNotBlank(req.getCreatedBy()))
						// && (StringUtils.isNotBlank(req.getCustomerReferenceNo()))
						&& (StringUtils.isNotBlank(req.getEmail1()))
					//	&& (StringUtils.isNotBlank(req.getEmail2()))
					//	&& (StringUtils.isNotBlank(req.getEmail3())) && (StringUtils.isNotBlank(req.getFax()))
						&& (StringUtils.isNotBlank(req.getGender())) && (StringUtils.isNotBlank(req.getIdNumber()))
						&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
						&& (StringUtils.isNotBlank(req.getLanguage()))
					//	&& (StringUtils.isNotBlank(req.getLanguageDesc()))
						&& (StringUtils.isNotBlank(req.getMobileNo1()))
					//	&& (StringUtils.isNotBlank(req.getMobileNo2()))
					//	&& (StringUtils.isNotBlank(req.getMobileNo3()))
						&& (StringUtils.isNotBlank(req.getNationality()))
						&& (StringUtils.isNotBlank(req.getOccupation()))
						&& (StringUtils.isNotBlank(req.getPlaceOfBirth()))
						&& (StringUtils.isNotBlank(req.getPolicyHolderType()))
						&& (StringUtils.isNotBlank(req.getPolicyHolderTypeid()))
						&& (StringUtils.isNotBlank(req.getProductId())) && (StringUtils.isNotBlank(req.getRegionCode()))
						&& (StringUtils.isNotBlank(req.getStateCode())) && (StringUtils.isNotBlank(req.getStateName()))
						&& (StringUtils.isNotBlank(req.getStatus()))
					//	&& (StringUtils.isNotBlank(req.getStreet()))
					//	&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
					//	&& (StringUtils.isNotBlank(req.getTelephoneNo1()))
					//	&& (StringUtils.isNotBlank(req.getTelephoneNo2()))
					//  && (StringUtils.isNotBlank(req.getTelephoneNo3())) && (StringUtils.isNotBlank(req.getTitle()))
						&& (req.getDobOrRegDate()!=null)
						&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
					//	&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
						&& (StringUtils.isNotBlank(req.getPreferredNotification()))
					//	&& (req.getAppointmentDate()!=null)

						){

					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<EserviceInsuredDetails> query = cb.createQuery(EserviceInsuredDetails.class);
					// Find all
					Root<EserviceInsuredDetails> b = query.from(EserviceInsuredDetails.class);
					// Select
					query.select(b);
					// Where

					Predicate n1 = (cb.like(cb.lower(b.get("address1")), req.getAddress1().toLowerCase()));
				//	Predicate n2 = (cb.like(cb.lower(b.get("address2")), req.getAddress2().toLowerCase()));
					Predicate n3 = (cb.like(cb.lower(b.get("branchCode")), req.getBranchCode().toLowerCase()));
				//	Predicate n4 = (cb.like(cb.lower(b.get("businessType")), req.getBusinessType().toLowerCase()));
				//	Predicate n5 = (cb.like(cb.lower(b.get("cityCode")), req.getCityCode().toLowerCase()));
					Predicate n5 =	(cb.equal(b.get("cityCode") ,  null != req.getCityCode() &&
							req.getCityCode().matches("[0-9]+") ? Integer.valueOf(req.getCityCode()) : 0 ));
					Predicate n6 = (cb.like(cb.lower(b.get("cityName")), req.getCityName().toLowerCase()));
					Predicate n7 = (cb.like(cb.lower(b.get("clientName")), req.getClientName().toLowerCase()));
					Predicate n8 = (cb.like(cb.lower(b.get("clientStatus")), req.getClientStatus().toLowerCase()));
					Predicate n9 = (cb.like(cb.lower(b.get("companyId")), req.getCompanyId().toLowerCase()));
					Predicate n10 = (cb.like(cb.lower(b.get("createdBy")), req.getCreatedBy().toLowerCase()));
					// Predicate n11 =
					// (cb.like(cb.lower(b.get("customerReferenceNo")),req.getCustomerReferenceNo().toLowerCase()));
					Predicate n12 = (cb.equal(b.get("dobOrRegDate"), req.getDobOrRegDate()));
					Predicate n13 = (cb.like(cb.lower(b.get("email1")), req.getEmail1().toLowerCase()));
				//	Predicate n14 = (cb.like(cb.lower(b.get("email2")), req.getEmail2().toLowerCase()));
				//	Predicate n15 = (cb.like(cb.lower(b.get("email3")), req.getEmail3().toLowerCase()));
				//	Predicate n16 = (cb.equal(b.get("fax"), req.getFax().toLowerCase()));
					Predicate n17 = (cb.like(cb.lower(b.get("gender")), req.getGender().toLowerCase()));
					Predicate n18 = (cb.like(cb.lower(b.get("idNumber")), req.getIdNumber().toLowerCase()));
					Predicate n19 = (cb.like(cb.lower(b.get("isTaxExempted")), req.getIsTaxExempted().toLowerCase()));
					Predicate n20 = (cb.like(cb.lower(b.get("language")), req.getLanguage().toLowerCase()));
				//	Predicate n21 = (cb.like(cb.lower(b.get("languageDesc")), req.getLanguageDesc().toLowerCase()));
					Predicate n22 = (cb.equal(b.get("mobileNo1"), req.getMobileNo1()));
				//	Predicate n23 = (cb.equal(b.get("mobileNo2"), req.getMobileNo2()));
				//	Predicate n24 = (cb.equal(b.get("mobileNo3"), req.getMobileNo3()));
					Predicate n25 = (cb.like(cb.lower(b.get("nationality")), req.getNationality().toLowerCase()));
					Predicate n26 = (cb.like(cb.lower(b.get("occupation")), req.getOccupation().toLowerCase()));
					Predicate n27 = (cb.like(cb.lower(b.get("placeOfBirth")), req.getPlaceOfBirth().toLowerCase()));
					Predicate n28 = (cb.like(cb.lower(b.get("policyHolderType")),
							req.getPolicyHolderType().toLowerCase()));
				//	Predicate n29 = (cb.like(cb.lower(b.get("policyHolderTypeId")),
				//			req.getPolicyHolderTypeid().toLowerCase()));
				//	Predicate n30 = (cb.like(cb.lower(b.get("productId")), req.getProductId()));

					Predicate n30 = (cb.equal(b.get("productId") ,  null != req.getProductId() &&
							req.getProductId().matches("[0-9]+") ? Integer.valueOf(req.getProductId()) : 0 ));
					Predicate n31 = (cb.like(cb.lower(b.get("regionCode")), req.getRegionCode().toLowerCase()));
				//	Predicate n32 = (cb.like(cb.lower(b.get("stateCode")), req.getStateCode().toLowerCase()));
					Predicate n33 = (cb.like(cb.lower(b.get("stateName")), req.getStateName().toLowerCase()));
					Predicate n34 = (cb.like(cb.lower(b.get("status")), req.getStatus().toLowerCase()));
				//	Predicate n35 = (cb.like(cb.lower(b.get("street")), req.getStreet().toLowerCase()));
				//	Predicate n36 = (cb.like(cb.lower(b.get("taxExemptedId")), req.getTaxExemptedId().toLowerCase()));
				//	Predicate n37 = (cb.equal(b.get("telephoneNo1"), req.getTelephoneNo1()));
				//	Predicate n38 = (cb.equal(b.get("telephoneNo2"), req.getTelephoneNo2()));
				//	Predicate n39 = (cb.equal(b.get("telephoneNo3"), req.getTelephoneNo3()));
				//	Predicate n40 = (cb.like(cb.lower(b.get("title")), req.getTitle().toLowerCase()));
				//	Predicate n41 = (cb.equal(b.get("appointmentDate"), req.getAppointmentDate()));
					Predicate n42 = (cb.like(cb.lower(b.get("preferredNotification")), req.getPreferredNotification().toLowerCase()));

					query.where(n1,  n3,  n6, n7, n8, n9, n10,
							// n11,
							n12, n13,  n17, n18, n19, n20,  n22, n25, n26, n27, n28,
							n30, n31,  n33, n34, /*n35,*/  n42);
					// Get Result
					TypedQuery<EserviceInsuredDetails> result = em.createQuery(query);
					list = result.getResultList();
					if (list.size() > 0 && req.getCustomerReferenceNo()==null) {
						errorList.add("1511");

					}
				}
			}

			else if (req.getSaveOrSubmit().equalsIgnoreCase("Save")) {
				if (StringUtils.isBlank(req.getClientName())) {
					errorList.add("1084");

					//errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
				} else if (req.getClientName().length() > 100) {
					errorList.add("1085");
					//errorList.add(new Error("01", "ClientName", "Please Enter ClientName within 100 Characters"));
				}
				if (StringUtils.isBlank(req.getPolicyHolderType())) {
				 errorList.add("1086");
					//errorList.add(new Error("02", "PolicyHolderType", "Please Select PolicyHolderType "));
				}
/*				if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
						if (StringUtils.isBlank(req.getBusinessType())) {
							errorList.add(new Error("16", "BusinessType", "Please Select BusinessType"));
						}
					}
				}
*/
				if("100040".equalsIgnoreCase(req.getCompanyId()))	{
				// Date Validation
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if (req.getPolicyHolderType().equalsIgnoreCase("1")) {
					if (req.getDobOrRegDate() == null) {
						errorList.add("1065");
					}else if (req.getDobOrRegDate() != null) {
						/*if (StringUtils.isBlank(req.getGender()) ) {
							errorList.add("1087");
							//errorList.add(new Error("23", "Gender", "Please Select Gender"));
						}*/
						if (req.getDobOrRegDate().after(today)) {
							errorList.add("1088");
						}
						LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDate();
						LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						Integer years = Period.between(localDate1, localDate2).getYears();
						if (years > 100) {
							errorList.add("1089");

						}

					}



				}else if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
					if (req.getDobOrRegDate() == null) {
						errorList.add("1065");
					}else if (req.getDobOrRegDate() != null) {
						if (req.getDobOrRegDate().after(today)) {
								errorList.add("1090");
						}
						LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
						LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						Integer years = Period.between(localDate1, localDate2).getYears();
						if (years > 100) {
							errorList.add("1091");

						}
					}


				}
			}

			}
			/*
			if (StringUtils.isBlank(req.getMobileNo1())) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1"));
			} else if (req.getMobileNo1().length() > 20) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1 within 20 Characters"));
			} else if (!req.getMobileNo1().matches("\\d+")) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1 only in numbers"));
			}
			if (StringUtils.isBlank(req.getEmail1())) {
				errorList.add(new Error("27", "Email1", "Please Enter Email1"));
			} else if (req.getEmail1().length() > 20) {
				errorList.add(new Error("27", "Email1", "Please Enter Email1 within 20 Characters"));
			} else {
				boolean b = isValidMail(req.getEmail1());
				if (b == false) {
					errorList.add(new Error("37", "Email1", "Please Enter Email in correct format"));
				}
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			errorList.add("01");
		}
		return errorList;

	}

	public static boolean datevalid(String date) {
		String regex = "(([0-9]{2})/([0-9]{2})/([0-9]{4}))";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(date);
		return m.matches();

	}

	@Override
	@Transactional
	public SuccessRes saveCustomerDetails(EserviceCustomerSaveReq req) {
		SuccessRes res = new SuccessRes();
		if("100004".equalsIgnoreCase(req.getCompanyId()))	{
			res=madisonEcustdetails.saveCustomerDetails(req);
		}else if("100018".equalsIgnoreCase(req.getCompanyId()))	{
			res=oromiaEcustdetails.saveCustomerDetails(req);
		}else if("100019".equalsIgnoreCase(req.getCompanyId()))	{
			res=ugandaEcustdetails.saveCustomerDetails(req);
		}else if("100020".equalsIgnoreCase(req.getCompanyId()))	{
			res=kenyaEcustdetails.saveCustomerDetails(req);
		}else if("100027".equalsIgnoreCase(req.getCompanyId()))	{
			res=angolaEcustdetails.saveCustomerDetails(req);
		}else if("100028".equalsIgnoreCase(req.getCompanyId()))	{
			res=eagalEcustdetails.saveCustomerDetails(req);
		}else if("100040".equalsIgnoreCase(req.getCompanyId())){
			res=sanlamEcustdetails.saveCustomerDetails(req);
		}else if("100042".equalsIgnoreCase(req.getCompanyId())){
			res=burkinoEcustdetails.saveCustomerDetails(req);
		}else if("100046".equalsIgnoreCase(req.getCompanyId()))	{
			res=phoenixEcustdetails.saveCustomerDetails(req);
		}else if("100047".equalsIgnoreCase(req.getCompanyId()))	{
			res=phoenixEcustdetails.saveCustomerDetails(req);
		}else if("100049".equalsIgnoreCase(req.getCompanyId()))	{
			res=phoenixEcustdetails.saveCustomerDetails(req);
		}else if("100050".equalsIgnoreCase(req.getCompanyId()))	{
			res=namibiaEcustdetails.saveCustomerDetails(req);
		}else if("100048".equalsIgnoreCase(req.getCompanyId()))	{
			res=mozambiqueEcustdetails.saveCustomerDetails(req);
		}else if("100055".equalsIgnoreCase(req.getCompanyId()))	{
			res=oman.saveCustomerDetails(req);
		}
		else {
			res=tanzaniaEcustdetails.saveCustomerDetails(req);
		}
		return res;
	}


	@Override
	@Transactional
	public SuccessRes saveInsuredDetails(EserviceCustomerSaveReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
	//	SimpleDateFormat sdf = new SimpleDateFormat("yyMMddmmssSSS");
		try {
			EserviceInsuredDetails saveInsured = new EserviceInsuredDetails();
			Date entryDate = null;
			String createdBy = "";
			String custRefNo = req.getCustomerReferenceNo();
			String insRefNo = "";
			Integer productId=0;
        /*if (StringUtils.isBlank(req.getCustomerReferenceNo())) {
				// Save
				entryDate = new Date();
				createdBy = req.getCreatedBy();
			//	Random rand = new Random();
			//	int random = rand.nextInt(90) + 10;
				productId=Integer.valueOf(req.getProductId());
			//	custRefNo = "Cust-" +   generateCustRefNo() ; // idf.format(new Date()) + random ;
				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(req.getCompanyId());
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("1");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
	 		 	custRefNo =  genSeqNoService.generateSeqCall(generateSeqReq);
				res.setResponse("Saved Successfully");
				res.setSuccessId(custRefNo);
			} else {
				// Update
				custRefNo = req.getCustomerReferenceNo();
				EserviceInsuredDetails findData = insuredRepository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
				entryDate = findData.getEntryDate();
				createdBy = findData.getCreatedBy();
				productId=findData.getProductId();
				res.setResponse("Updated Successfully");
				res.setSuccessId(custRefNo);
			}*/
			 if (!StringUtils.isBlank(req.getInsuredReferenceNo())) {
				// Update
				insRefNo = req.getInsuredReferenceNo();
				EserviceInsuredDetails findData = insuredRepository.findByInsuredReferenceNo(req.getInsuredReferenceNo());
				entryDate = findData.getEntryDate();
				createdBy = findData.getCreatedBy();
				productId=findData.getProductId();
			}
			if (!StringUtils.isBlank(req.getCustomerReferenceNo())) {
				List<EserviceInsuredDetails> data = insuredRepository.findAllByCustomerReferenceNo(req.getCustomerReferenceNo());
				if(data!=null && !data.isEmpty()){
				req.setInsuredReferenceNo(data.get(0).getInsuredReferenceNo());
				insRefNo=data.get(0).getInsuredReferenceNo();
				insuredRepository.deleteAll(data);
				}
			}
        if (StringUtils.isBlank(req.getInsuredReferenceNo())) {
			// Save
			entryDate = new Date();
			createdBy = req.getCreatedBy();
			productId=Integer.valueOf(req.getProductId());

 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
 		 	generateSeqReq.setInsuranceId(req.getCompanyId());
 		 	generateSeqReq.setProductId(req.getProductId());
 		 	generateSeqReq.setType("10");
 		 	generateSeqReq.setTypeDesc("INSURED_REFERENCE_NO");
 		 	insRefNo =  genSeqNoService.generateSeqCall(generateSeqReq);
			res.setResponse("Saved Successfully");
			res.setSuccessId(insRefNo);
		}else {
			res.setResponse("Updated Successfully");
			res.setSuccessId(insRefNo);
		}

        	// Dob Condition
	        if(req.getDobOrRegDate() ==null  ) {
				Date   dobOrReg = new Date() ;
				if( req.getPolicyHolderType().equalsIgnoreCase("1") ) {
					// Dob
					Calendar cal = new GregorianCalendar();
					cal.setTime(dobOrReg);
					cal.add(Calendar.YEAR, -18);
					dobOrReg = cal.getTime();
				}
				req.setDobOrRegDate(dobOrReg);

			}

			dozerMapper.map(req, saveInsured);
			saveInsured.setProductId(productId);
			saveInsured.setEntryDate(entryDate);
			saveInsured.setCreatedBy(createdBy);
			saveInsured.setUpdatedDate(new Date());
			saveInsured.setUpdatedBy(req.getCreatedBy());
			saveInsured.setCustomerReferenceNo(custRefNo);
			saveInsured.setStatus(req.getStatus());

			saveInsured.setZone(StringUtils.isBlank(req.getZone())? 0:Integer.valueOf(req.getZone()));
			saveInsured.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
			saveInsured.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
			saveInsured.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
			saveInsured.setBrokerBranchCode(req.getBrokerBranchCode());

			// Age Calculation
			int age = 0 ;
			Date dob = null;
			if (req.getDobOrRegDate() !=null) {
				dob = req.getDobOrRegDate();
				Date today = new Date();
				age = today.getYear() - dob.getYear();
			}

			// From List Item Value

			Map<String,String> title = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"NAME_TITLE",req.getTitle());//listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
			Map<String,String> gender = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"GENDER",req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
			Map<String,String> language = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"LANGUAGE",req.getLanguage());//listRepo.findByItemTypeAndItemCode("LANGUAGE", req.getLanguage());
			Map<String,String> policyHolderType = getListItemLocal ("99999" , req.getBranchCode() ,"POLICY_HOLDER_TYPE",req.getPolicyHolderType());//listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",	req.getPolicyHolderType());
			Map<String,String> policyHolderTypeId = getListItemLocal (req.getCompanyId(), req.getBranchCode() ,"POLICY_HOLDER_ID_TYPE",req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());

			String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
			String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
			String languageDesc = Optional.ofNullable(language).map(map -> map.get("itemDesc")).orElse("");
			String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc")).orElse("");
			String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc")).orElse("");

			// From List Item Value (Local)
			String genderLocal = Optional.ofNullable(gender).map(map -> map.get("itemDescLocal")).orElse("");
			String titleLocal = Optional.ofNullable(title).map(map -> map.get("itemDescLocal")).orElse("");
			String languageLocal = Optional.ofNullable(language).map(map -> map.get("itemDescLocal")).orElse("");
			String PolicyHolderTypeLocal = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDescLocal")).orElse("");
			String policyHolderTypeIdLocal = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDescLocal")).orElse("");

			// From Region_mater for state name local
			String stateNameLocal = "";
			List<RegionMaster> rgMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getNationality(),req.getStateCode());
			if(rgMaster!= null  && rgMaster.size()>0) {
				stateNameLocal = rgMaster.get(0).getRegionNameLocal();
			}
			// From State_master for city name local
			String cityNameLocal = "";
			List<StateMaster> stMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(Integer.valueOf(req.getCityCode()),req.getNationality(),req.getStateCode());
			if(stMaster!= null && stMaster.size()>0) {
				cityNameLocal = stMaster.get(0).getStateNameLocal();
			}

			if(StringUtils.isNotBlank(req.getMobileCode1())){
				Map<String,String> mobileCode1Desc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode1());
				String mobileCode1 = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDesc")).orElse("");
				String mobileCode1Local = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDescLocal")).orElse("");
				saveInsured.setMobileCodeDesc1(mobileCode1);

			}
	        if(StringUtils.isNotBlank(req.getMobileCode2())){
	        	Map<String,String> mobileCode2Desc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode2());
	        	String mobileCode2 = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDesc")).orElse("");
				String mobileCode2Local = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDescLocal")).orElse("");
				saveInsured.setMobileCodeDesc2(mobileCode2);

	        }


	        if(StringUtils.isNotBlank(req.getMobileCode3())){
	        	Map<String,String> mobileCode3Desc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode3());
	        	String mobileCode3 = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDesc")).orElse("");
				String mobileCode3Local = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDescLocal")).orElse("");
				saveInsured.setMobileCodeDesc3(mobileCode3);

	        }
	        if(StringUtils.isNotBlank(req.getWhatsappCode())){
	        	Map<String,String> whatsappCodeDesc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getWhatsappCode());
	        	String whatsappCode = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDesc")).orElse("");
				String whatsappCodeLocal = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDescLocal")).orElse("");
				saveInsured.setWhatsappCodeDesc(whatsappCode);

	        }
	        String businessTypeLocal = "";
			if (StringUtils.isNotBlank(req.getBusinessType())) {
				Map<String,String> businessTypeDesc =  getListItemLocal ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
				String businessType = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDesc")).orElse("");
				businessTypeLocal = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDescLocal")).orElse("");
				saveInsured.setBusinessTypeDesc(businessType);
			}
 			Map<String,String> occupation = getByOccupationIdDesc(req.getOccupation(), req.getCompanyId(),req.getProductId() , req.getBranchCode());
			String occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
			String occupationDescLocal = Optional.ofNullable(occupation).map(map -> map.get("occupationNameLocal")).orElse("");
//			if(StringUtils.isNotBlank(req.getCompanyId()) && "100004".equalsIgnoreCase(req.getCompanyId()) ) {
//				saveData.setTitleDesc(null);
//				saveData.setPreferredNotification("Sms");
//				saveData.setIsTaxExempted("N");
//				saveData.setRegionCode(null);
//				saveData.setStatus("Y");
//				saveData.setBusinessType(null);
//				saveData.setVrTinNo(null);
//				saveData.setVrnGst(null);
//			    String mobileCode2 = getListItem1(req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE");
//		        saveData.setMobileCode1(mobileCode2);
//				saveData.setMobileCode2(mobileCode2);
//		     	String country = getByCountry(req.getCompanyId());
//				saveData.setNationality(country);
//
//			}else {
//				saveData.setTitleDesc(title);
//				saveData.setPreferredNotification(req.getPreferredNotification());
//				saveData.setIsTaxExempted(req.getIsTaxExempted());
//				saveData.setRegionCode(req.getRegionCode());
//				saveData.setStatus(req.getStatus());
//				saveData.setBusinessType(req.getBusinessType());
//				saveData.setVrTinNo(req.getVrTinNo());
//				saveData.setVrnGst(req.getVrTinNo());
//				saveData.setMobileCode1(req.getMobileCode1());
//				saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
//			}
			saveInsured.setTitleDesc(titleDesc);
			saveInsured.setMiddleName(req.getMiddleName());
			saveInsured.setLastName(req.getLastName());
			saveInsured.setPreferredNotification(req.getPreferredNotification());
			saveInsured.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted())?"0":req.getIsTaxExempted());
			saveInsured.setRegionCode(req.getRegionCode());
			saveInsured.setStatus(req.getStatus());
			saveInsured.setBusinessType(req.getBusinessType());
			saveInsured.setVrTinNo(req.getVrTinNo());
			saveInsured.setVrnGst(req.getVrTinNo());
			saveInsured.setMobileCode1(req.getMobileCode1());
			saveInsured.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
			saveInsured.setGenderDesc(genderDesc);
			saveInsured.setLanguageDesc(languageDesc);
			saveInsured.setOccupationDesc(occupationDesc);
			saveInsured.setOtherOccupation(req.getOtherOccupation());
			saveInsured.setPolicyHolderTypeDesc(policyHolderTypeDesc);
			saveInsured.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
			saveInsured.setIdType(req.getPolicyHolderTypeid());
			saveInsured.setIdTypeDesc(policyHolderTypeIdDesc);
			saveInsured.setVrTinNo(req.getVrTinNo());
			saveInsured.setVrnGst(req.getVrTinNo());
			saveInsured.setAge(age);
			saveInsured.setMobileCode1(req.getMobileCode1());
			//saveData.setStreet(req.getStreet());
			saveInsured.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
			saveInsured.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
			saveInsured.setWhatsappCode(req.getWhatsappCode());
			saveInsured.setRegionCode(req.getStateCode());
			saveInsured.setStateCode(StringUtils.isBlank(req.getStateCode()) ?null :Integer.valueOf(req.getStateCode()));
			saveInsured.setStateName(req.getStateName());
			saveInsured.setCityCode(StringUtils.isBlank(req.getCityCode())?null :Integer.valueOf(req.getCityCode()));
			saveInsured.setCityName(req.getCityName());
			saveInsured.setRegionCode(req.getRegionCode());

			//local desc feilds
			saveInsured.setGenderDescLocal(genderLocal);
			saveInsured.setTitleDescLocal(titleLocal);
			saveInsured.setLanguageDescLocal(languageLocal);
			saveInsured.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
			saveInsured.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
			saveInsured.setOccupationDescLocal(occupationDescLocal);
			//saveData.setMaritalStatusDescLocal(maritalStatusDescLocal);
			saveInsured.setStateNameLocal(stateNameLocal);
			saveInsured.setCityNameLocal(cityNameLocal);
			saveInsured.setMobileCodeDesc1Local(req.getMobileCode1());
			saveInsured.setMobileCodeDesc2Local(req.getMobileCode2());
			saveInsured.setMobileCodeDesc3Local(req.getMobileCode3());
			saveInsured.setWhatsappCodeDescLocal(req.getWhatsappCode());
			saveInsured.setIdTypeDescLocal(policyHolderTypeIdLocal);
			saveInsured.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
			saveInsured.setActivities(req.getActivities());
			saveInsured.setInsuredReferenceNo(insRefNo);
			saveInsured.setCustomerReferenceNo(custRefNo);

			// Kenya Rating Fields
			saveInsured.setMaritalStatus(StringUtils.isBlank(req.getMaritalStatus()) ?"Single" : req.getMaritalStatus() );
			if (req.getLicenseIssuedDate()!=null ) {
				saveInsured.setLicenseIssuedDate(req.getLicenseIssuedDate());
				Date licenceIssued = req.getDobOrRegDate();
				Date today = new Date();
				int licenseDuration = today.getYear() - licenceIssued.getYear();
				saveInsured.setLicenseDuration(licenseDuration);

			} else {
				saveInsured.setLicenseIssuedDate(new Date());
				saveInsured.setLicenseDuration(20);
			}


//			if((StringUtils.isNotBlank(req.getNationality()))&&(StringUtils.isNotBlank(req.getStateCode()))){
//			List<StateMaster> stateCityNames = getStateAndCityName(req.getNationality(), req.getStateCode());
//			saveData.setStateName(stateCityNames.get(0).getStateName() == null ? "" : stateCityNames.get(0).getStateName().toString());
//			saveData.setCityName(req.getCityName());
//			}
			saveInsured.setCountry(req.getCountry());
			insuredRepository.save(saveInsured);

			//Personal Info Update

			//Endorsement flow and B2C Flow
			//Type=B2C
			if(StringUtils.isNotBlank(req.getEndtCategDesc())) {
			if("Non Financial".equalsIgnoreCase(req.getEndtCategDesc().toString())) {
				PersonalInfo savePersonalInfo=new PersonalInfo();
				HomePositionMaster homedata=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			//	PersonalInfo personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
				dozerMapper.map(req, saveInsured);
				savePersonalInfo.setPinCode(req.getPinCode());
				savePersonalInfo.setCustomerId(homedata.getCustomerId());
				savePersonalInfo.setIdNumber(req.getIdNumber());
				savePersonalInfo.setCreatedBy(createdBy);
				savePersonalInfo.setUpdatedDate(new Date());
				savePersonalInfo.setUpdatedBy(req.getCreatedBy());
				savePersonalInfo.setCustomerReferenceNo(custRefNo);
				savePersonalInfo.setAddress1(req.getAddress1());
				savePersonalInfo.setAddress2(req.getAddress2());
				savePersonalInfo.setAge(age);
				savePersonalInfo.setBranchCode(req.getBranchCode());
				savePersonalInfo.setBusinessType(req.getBusinessType());
				savePersonalInfo.setOtherOccupation(req.getOtherOccupation());
				if (StringUtils.isNotBlank(req.getBusinessType())) {
					String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
					savePersonalInfo.setBusinessTypeDesc(businessType);

				}


				savePersonalInfo.setRegionCode(req.getRegionCode());
				savePersonalInfo.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted())?"0":req.getIsTaxExempted());
				savePersonalInfo.setCityCode(req.getCityCode());
				savePersonalInfo.setCityName(req.getCityName());
				savePersonalInfo.setClientName(req.getClientName());
				savePersonalInfo.setClientStatus(req.getClientStatus());
				savePersonalInfo.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
				savePersonalInfo.setCompanyId(req.getCompanyId());
				savePersonalInfo.setCreatedBy(req.getCreatedBy());
				savePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
 				savePersonalInfo.setDobOrRegDate(dob);
  				savePersonalInfo.setEmail1(req.getEmail1());
				savePersonalInfo.setEmail2(req.getEmail2());
				savePersonalInfo.setEmail3(req.getEmail3());
				savePersonalInfo.setEndorsementDate(req.getEndorsementDate());
				savePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
				savePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
				savePersonalInfo.setEndorsementType(req.getEndorsementType());
				savePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
				savePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
				savePersonalInfo.setEndtCount(req.getEndtCount());
				savePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
				savePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
				savePersonalInfo.setEndtStatus(req.getEndtStatus());
				savePersonalInfo.setEntryDate(new Date());
				savePersonalInfo.setFax(req.getFax());
				savePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
				savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
				savePersonalInfo.setGenderDesc(genderDesc);
				savePersonalInfo.setTitleDesc(titleDesc);
				savePersonalInfo.setLanguageDesc(languageDesc);
				savePersonalInfo.setOccupationDesc(occupationDesc);


				// Induvidual / Corporate
				savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
				savePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);

				// Possport or etc
				savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
				savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
				savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
				savePersonalInfo.setIdTypeDesc(policyHolderTypeIdDesc);

				savePersonalInfo.setMobileCode1(req.getMobileCode1());
				savePersonalInfo.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
				savePersonalInfo.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
				savePersonalInfo.setMobileNo1(req.getMobileNo1());
				savePersonalInfo.setMobileNo2(req.getMobileNo2());
				savePersonalInfo.setMobileNo3(req.getMobileNo3());
				savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
				if (StringUtils.isNotBlank(req.getMobileCode1())) {
 					ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode1(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getMobileCode2())) {
					ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode2(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getMobileCode3())) {
					ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode3(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getWhatsappCode())) {
					ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
							req.getWhatsappCode(),req.getCompanyId());
					savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

				}
				savePersonalInfo.setRegionCode(req.getRegionCode());
				savePersonalInfo.setStateCode(req.getStateCode());
				savePersonalInfo.setStateName(req.getStateName());
				savePersonalInfo.setStatus(req.getStatus());
				savePersonalInfo.setNationality(req.getNationality());
				savePersonalInfo.setVrTinNo(req.getVrTinNo());
				savePersonalInfo.setVrnGst(req.getVrTinNo());


				// local desc
				savePersonalInfo.setTitleDescLocal(titleLocal);
				savePersonalInfo.setGenderDescLocal(genderLocal);
				savePersonalInfo.setOccupationDescLocal(occupationDescLocal);
				savePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
				savePersonalInfo.setStateNameLocal(stateNameLocal);
				savePersonalInfo.setCityNameLocal(cityNameLocal);
				savePersonalInfo.setIdTypeDescLocal(policyHolderTypeIdLocal);
				savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
				savePersonalInfo.setLanguageDescLocal(languageLocal);
				savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
				savePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
				savePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
				savePersonalInfo.setActivities(req.getActivities());


				personalInforepo.save(savePersonalInfo);
			}
			}else if(StringUtils.isNotBlank(req.getType())) {
				HomePositionMaster homedata=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				if("b2c".equalsIgnoreCase(req.getType().toString()) && homedata !=null ) {
					PersonalInfo savePersonalInfo=new PersonalInfo();

				//	PersonalInfo personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
					dozerMapper.map(req, saveInsured);
					savePersonalInfo.setPinCode(req.getPinCode());
					savePersonalInfo.setCustomerId(homedata.getCustomerId());
					savePersonalInfo.setIdNumber(req.getIdNumber());
					savePersonalInfo.setCreatedBy(createdBy);
					savePersonalInfo.setUpdatedDate(new Date());
					savePersonalInfo.setUpdatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(custRefNo);
					savePersonalInfo.setAddress1(req.getAddress1());
					savePersonalInfo.setAddress2(req.getAddress2());
					savePersonalInfo.setAge(age);
					savePersonalInfo.setBranchCode(req.getBranchCode());
					savePersonalInfo.setBusinessType(req.getBusinessType());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
						savePersonalInfo.setBusinessTypeDesc(businessType);
					}

					savePersonalInfo.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted())?"0":req.getIsTaxExempted());
					savePersonalInfo.setCityCode(req.getCityCode());
					savePersonalInfo.setCityName(req.getCityName());
					savePersonalInfo.setClientName(req.getClientName());
					savePersonalInfo.setClientStatus(req.getClientStatus());
					savePersonalInfo.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
					savePersonalInfo.setCompanyId(req.getCompanyId());
					savePersonalInfo.setCreatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
					savePersonalInfo.setDobOrRegDate(req.getDobOrRegDate());
					savePersonalInfo.setEmail1(req.getEmail1());
					savePersonalInfo.setEmail2(req.getEmail2());
					savePersonalInfo.setEmail3(req.getEmail3());
					savePersonalInfo.setEndorsementDate(req.getEndorsementDate());
					savePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
					savePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
					savePersonalInfo.setEndorsementType(req.getEndorsementType());
					savePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
					savePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
					savePersonalInfo.setEndtCount(req.getEndtCount());
					savePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					savePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					savePersonalInfo.setEndtStatus(req.getEndtStatus());
					savePersonalInfo.setEntryDate(new Date());
					savePersonalInfo.setFax(req.getFax());
					savePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
					savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
					savePersonalInfo.setGenderDesc(genderDesc);
					savePersonalInfo.setTitle(req.getTitle());
					savePersonalInfo.setTitleDesc(titleDesc);
					savePersonalInfo.setLanguageDesc(languageDesc);
					savePersonalInfo.setOccupationDesc(occupationDesc);
					savePersonalInfo.setIdType(req.getIdType());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);

					// Induvidual / Corporate
					savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
					savePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);

					// Possport or etc
					savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
					savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
					savePersonalInfo.setIdTypeDesc(policyHolderTypeDesc);

					savePersonalInfo.setMobileCode1(req.getMobileCode1());
					savePersonalInfo.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
					savePersonalInfo.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
					savePersonalInfo.setMobileNo1(req.getMobileNo1());
					savePersonalInfo.setMobileNo2(req.getMobileNo2());
					savePersonalInfo.setMobileNo3(req.getMobileNo3());
					savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode1(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode2(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode3(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(),req.getCompanyId());
						savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					savePersonalInfo.setRegionCode(req.getRegionCode());
					savePersonalInfo.setStateCode(req.getStateCode());
					savePersonalInfo.setStateName(req.getStateName());
					savePersonalInfo.setStatus(req.getStatus());
					savePersonalInfo.setNationality(req.getNationality());
					savePersonalInfo.setVrTinNo(req.getVrTinNo());
					savePersonalInfo.setVrnGst(req.getVrTinNo());

					// local desc
					savePersonalInfo.setTitleDescLocal(titleLocal);
					savePersonalInfo.setGenderDescLocal(genderLocal);
					savePersonalInfo.setOccupationDescLocal(occupationDescLocal);
					savePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
					savePersonalInfo.setStateNameLocal(stateNameLocal);
					savePersonalInfo.setCityNameLocal(cityNameLocal);
					savePersonalInfo.setIdTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setLanguageDescLocal(languageLocal);
					savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
					savePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
					savePersonalInfo.setActivities(req.getActivities());

					personalInforepo.save(savePersonalInfo);
				}
			}
			// Response

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

	}



	public synchronized String generateCustRefNo() {
	       try {
	    	   SeqCustrefno entity;
	            entity = custRefRepo.save(new SeqCustrefno());
	            return String.format("%05d",entity.getCustReferenceNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }

	 }

	public synchronized String getListItem(String insuranceId , String branchCode, String itemType, String itemCode) {
		String itemDesc = "" ;
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));


			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			//Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n1,n2,n3,n4,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "" ;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc ;
	}

	public synchronized Map<String,String> getListItemLocal(String insuranceId , String branchCode, String itemType, String itemCode) {
		Map<String,String> itemDesc = new HashMap<String,String>() ;
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));


			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			//Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n1,n2,n3,n4,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemDesc.put("itemDesc",list.size() > 0 ? list.get(0).getItemValue() : "" );
			itemDesc.put("itemDescLocal",list.size() > 0 ? list.get(0).getItemValueLocal() : "" );
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc ;
	}


	public synchronized String getListItem1(String insuranceId , String branchCode, String itemType) {
		String countryCode = "" ;
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));


			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			//Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			query.where(n1,n2,n3,n4,n9,n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			countryCode = list.size() > 0 ? list.get(0).getItemValue() : "" ;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return countryCode ;
	}

	public String getByOccupationId(String occupationId, String insuranceId, String productId , String branchCode) {
		String occupationDesc = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OccupationMaster> query=  cb.createQuery(OccupationMaster.class);
			List<OccupationMaster> list = new ArrayList<OccupationMaster>();

			// Find All
			Root<OccupationMaster> c = query.from(OccupationMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<OccupationMaster> ocpm1 = effectiveDate.from(OccupationMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("occupationId"),ocpm1.get("occupationId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a9 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a5,a6,a9);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<OccupationMaster> ocpm2 = effectiveDate2.from(OccupationMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("occupationId"),ocpm2.get("occupationId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate a10 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			effectiveDate2.where(a3,a4,a7,a8,a10);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			Predicate n5 = cb.equal(c.get("branchCode"),branchCode);
			Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n8 = cb.equal(c.get("occupationId"),occupationId);
			Predicate n9 = cb.equal(c.get("productId"),productId );
			Predicate n10 = cb.equal(c.get("productId"),"99999" );
			Predicate n11 =  cb.or(n9, n10);
			query.where(n1,n2,n3,n4,n7,n8,n11).orderBy(orderList);
			TypedQuery<OccupationMaster> result = em.createQuery(query);
			list = result.getResultList();

			if(list.size()>0) {
				list = result.getResultList();
				list.sort(Comparator.comparing(OccupationMaster::getOccupationName));
				occupationDesc = list.size() > 0 ? list.get(0).getOccupationName() : "";
			}
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
		}
			return occupationDesc;
		}
	public Map<String,String> getByOccupationIdDesc(String occupationId, String insuranceId, String productId , String branchCode) {
		Map<String,String> occupationDesc = new HashMap<String,String>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OccupationMaster> query=  cb.createQuery(OccupationMaster.class);
			List<OccupationMaster> list = new ArrayList<OccupationMaster>();

			// Find All
			Root<OccupationMaster> c = query.from(OccupationMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<OccupationMaster> ocpm1 = effectiveDate.from(OccupationMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("occupationId"),ocpm1.get("occupationId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a9 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a5,a6,a9);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<OccupationMaster> ocpm2 = effectiveDate2.from(OccupationMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("occupationId"),ocpm2.get("occupationId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate a10 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			effectiveDate2.where(a3,a4,a7,a8,a10);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			Predicate n5 = cb.equal(c.get("branchCode"),branchCode);
			Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n8 = cb.equal(c.get("occupationId"),occupationId);
			Predicate n9 = cb.equal(c.get("productId"),productId );
			Predicate n10 = cb.equal(c.get("productId"),"99999" );
			Predicate n11 =  cb.or(n9, n10);
			query.where(n1,n2,n3,n4,n7,n8,n11).orderBy(orderList);
			TypedQuery<OccupationMaster> result = em.createQuery(query);
			list = result.getResultList();

			if(list.size()>0) {
				list = result.getResultList();
				list.sort(Comparator.comparing(OccupationMaster::getOccupationName));
				occupationDesc.put("occupationName" , list.size() > 0 ? list.get(0).getOccupationName() : "");
				occupationDesc.put("occupationNameLocal" , list.size() > 0 ? list.get(0).getOccupationNameLocal() : "");
			}
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
		}
			return occupationDesc;
		}

	public String getByCountry(String insuranceId) {
		String country = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InsuranceCompanyMaster> query=  cb.createQuery(InsuranceCompanyMaster.class);
			List<InsuranceCompanyMaster> list = new ArrayList<InsuranceCompanyMaster>();

			// Find All
			Root<InsuranceCompanyMaster> c = query.from(InsuranceCompanyMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("amendId")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("amendId"),ocpm1.get("amendId"));
			//Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			//Predicate a9 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a5);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("amendId"),ocpm2.get("amendId"));
			//Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			//Predicate a10 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			effectiveDate2.where(a3,a4,a7);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			//Predicate n5 = cb.equal(c.get("branchCode"),branchCode);
			//Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			//Predicate n7 = cb.or(n5,n6);
			query.where(n1,n2,n3,n4).orderBy(orderList);
			TypedQuery<InsuranceCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();

			if(list.size()>0) {
				list = result.getResultList();
				country = list.size() > 0 ? list.get(0).getCountryId() : "";
			}
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
		}
			return country;
		}

	public List<StateMaster> getStateAndCityName(String countryId, String stateCode) {
		List<StateMaster> list = new ArrayList<StateMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);

			// State Effective Date Max Filter
			Root<StateMaster> s = query.from(StateMaster.class);

			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<StateMaster> ocpm2 = effectiveDate2.from(StateMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateStart").as(Date.class)));
			Predicate seff1 = cb.equal(ocpm2.get("stateId"), stateCode);
			Predicate seff2 = cb.equal(ocpm2.get("countryId"), countryId);
			Predicate seff3 = cb.equal(ocpm2.get("status"), s.get("status"));
			Predicate seff4 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"), today);
			effectiveDate2.where(seff1, seff2, seff3, seff4);

			// State Name Max Filter
			query.select(s.get("stateName"));
			Predicate s1 = cb.equal(s.get("stateId"), stateCode);
			Predicate s2 = cb.equal(s.get("countryId"), countryId);
			Predicate s3 = cb.equal(s.get("status"), s.get("status"));
			Predicate s4 = cb.equal(s.get("effectiveDateStart"), effectiveDate2);
			query.where(s1, s2, s3, s4);

			// Country Effective Date Max Filter
			Subquery<Long> country = query.subquery(Long.class);
			Root<CountryMaster> cm = country.from(CountryMaster.class);

			Subquery<Date> effectiveDate3 = query.subquery(Date.class);
			Root<CountryMaster> ocpm3 = effectiveDate3.from(CountryMaster.class);
			effectiveDate3.select(cb.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
			Predicate ceff2 = cb.equal(ocpm3.get("countryId"), cm.get("countryId"));
			Predicate ceff3 = cb.equal(ocpm3.get("status"), cm.get("status"));
			Predicate ceff4 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
			effectiveDate3.where(ceff2, ceff3, ceff4);

			// Country Name Max Filter
			country.select(cm.get("countryName"));
			Predicate cm2 = cb.equal(cm.get("countryId"), s.get("countryId"));
			Predicate cm3 = cb.equal(cm.get("status"), s.get("status"));
			Predicate cm4 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
			country.where(cm2, cm3, cm4);

			// Select
			query.select(s.alias("stateName"));

			query.where(s1, s2, s3, s4);
			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public CustomerDetailsGetRes getCustomerDetails(GetCustomerDetailsReq req) {
		CustomerDetailsGetRes res = new CustomerDetailsGetRes();
		if("100004".equalsIgnoreCase(req.getCompanyId()))	{
			res=madisonEcustdetails.getCustomerDetails(req);
		}else if("100018".equalsIgnoreCase(req.getCompanyId()))	{
			res=oromiaEcustdetails.getCustomerDetails(req);
		}else if("100019".equalsIgnoreCase(req.getCompanyId()))	{
			res=ugandaEcustdetails.getCustomerDetails(req);
		}else if("100020".equalsIgnoreCase(req.getCompanyId()))	{
			res=kenyaEcustdetails.getCustomerDetails(req);
		}else if("100027".equalsIgnoreCase(req.getCompanyId()))	{
			res=angolaEcustdetails.getCustomerDetails(req);
		}else if("100028".equalsIgnoreCase(req.getCompanyId()))	{
			res=eagalEcustdetails.getCustomerDetails(req);
		}else if("100040".equalsIgnoreCase(req.getCompanyId()))	{
			res=sanlamEcustdetails.getCustomerDetails(req);
		}else if("100042".equalsIgnoreCase(req.getCompanyId()))	{
			res=burkinoEcustdetails.getCustomerDetails(req);
		}else if("100046".equalsIgnoreCase(req.getCompanyId()))	{
			res=phoenixEcustdetails.getCustomerDetails(req);
		}else if("100055".equalsIgnoreCase(req.getCompanyId())) {
			res=oman.getCustomerDetails(req);
		}
		else {
			res=tanzaniaEcustdetails.getCustomerDetails(req);
		}
		return res;
	}

	@Override
	public InsuredDetailsGetRes getInsuredDetails(GetCustomerDetailsReq req) {
		InsuredDetailsGetRes res = new InsuredDetailsGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			List<EserviceInsuredDetails> data = insuredRepository.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo());

			if(data != null && !data.isEmpty() && StringUtils.isNotBlank(data.get(0).getInsuredReferenceNo()))	{
				res = dozerMapper.map(data.get(0), InsuredDetailsGetRes.class);

				res.setMobileCodeDesc1(data.get(0).getMobileCodeDesc1()==null?"":data.get(0).getMobileCodeDesc1());
				res.setMobileCodeDesc2(data.get(0).getMobileCodeDesc2()==null?"":data.get(0).getMobileCodeDesc2());
				res.setMobileCodeDesc3(data.get(0).getMobileCodeDesc3()==null?"":data.get(0).getMobileCodeDesc3());
				res.setMobileCode1(data.get(0).getMobileCode1()==null?"":data.get(0).getMobileCode1());
				res.setMobileCode2(data.get(0).getMobileCode2()==null?"":data.get(0).getMobileCode2());
				res.setMobileCode3(data.get(0).getMobileCode3()==null?"":data.get(0).getMobileCode3());
				res.setSocioProfessionalCategory(data.get(0).getSocioProfessionalCategory());
				res.setActivities(data.get(0).getActivities());
				res.setAddress2(data.get(0).getAddress2()==null?"":data.get(0).getAddress2());
				res.setInsuredReferenceNo(data.get(0).getInsuredReferenceNo()==null?"":data.get(0).getInsuredReferenceNo());
				res.setIsTaxExempted(data.get(0).getIsTaxExempted()==null?"":data.get(0).getIsTaxExempted());
				res.setCountry(data.get(0).getCountry()==null?"":data.get(0).getCountry());
			}
			else
			{
				return null;
			}



		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}


	@Override
	public List<CustomerDetailsGetRes> getallCustomerDetails(GetAllCustomerDetailsReq req) {
		List<CustomerDetailsGetRes> resList = new ArrayList<CustomerDetailsGetRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			// Limit , Offset
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			Pageable paging = PageRequest.of(limit, offset, Sort.by("updatedDate").descending());

			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());

			LoginMaster Brokerlogin = loginRepo.findByAgencyCodeAndCompanyId(loginData.getOaCode().toString(),req.getComapanyId());
			List<EserviceCustomerDetails> custList = new ArrayList<EserviceCustomerDetails>();
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			Root<PersonalInfo> p = query.from(PersonalInfo.class);

			// Select
			query.select(c  ).distinct(true);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(p.get("customerId"), h.get("customerId"));
			Predicate n2 = cb.equal(h.get("companyId"), req.getComapanyId());
			Predicate n3 = cb.equal(c.get("customerReferenceNo"), p.get("customerReferenceNo"));
		//	Predicate n3 = cb.equal(h.get("productId"), req.getProductId());
			Predicate n7 = cb.equal(c.get("companyId"), req.getComapanyId());
			Predicate n4 = null ;
			Predicate n5 = null ;

			if (loginData.getUserType().equalsIgnoreCase("Broker") || loginData.getUserType().equalsIgnoreCase("User")) {

				n4 = cb.equal(  h.get("brokerBranchCode"), req.getBrokerBranchCode());
				if ("Broker".equalsIgnoreCase(loginData.getUserType())) {
					Subquery<String> loginId = query.subquery(String.class);
					Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
					loginId.select(ocpm1.get("loginId"));
					Predicate a1 = cb.equal(ocpm1.get("agencyCode"), loginData.getOaCode());
					Predicate a2 = cb.equal(ocpm1.get("companyId"),req.getComapanyId());

					loginId.where(a1,a2);
					n5 = cb.equal(c.get("createdBy"), loginId.as(String.class));
				}else 	if ("User".equalsIgnoreCase(loginData.getUserType())&& !"Direct".equalsIgnoreCase(Brokerlogin.getSubUserType())) {
					Subquery<Long> loginId = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
					loginId.select(ocpm1.get("loginId"));
					Predicate a1 = cb.equal(ocpm1.get("agencyCode"), loginData.getOaCode());
					Predicate a2 = cb.equal(ocpm1.get("companyId"),req.getComapanyId());
					loginId.where(a1,a2);
					n5 = cb.equal(c.get("createdBy"), loginId.as(String.class));
				}else 	if ("User".equalsIgnoreCase(loginData.getUserType())&& "Direct".equalsIgnoreCase(Brokerlogin.getSubUserType())) {
					n5 = cb.equal(c.get("createdBy"), req.getCreatedBy());
				}
			//	n5 = cb.equal(  h.get("loginId"), req.getCreatedBy());
			} else {

				n4 = cb.equal(  h.get("branchCode"),  req.getBranchCode());
				n5 = cb.equal(  h.get("applicationId"), req.getCreatedBy());
			}

			query.where(n1, n2,n3, n4, n5,n7).orderBy(orderList);

			// Get Result
			TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			custList = result.getResultList();



			List<EserviceCustomerDetails> totalCustList = new ArrayList<EserviceCustomerDetails>();
			Page<EserviceCustomerDetails> datas = null ;
			if (loginData.getUserType().equalsIgnoreCase("Broker")|| (loginData.getUserType().equalsIgnoreCase("User")&& !"Direct".equalsIgnoreCase(Brokerlogin.getSubUserType()))) {
//				datas = repository.findByCompanyIdAndBrokerBranchCodeAndCreatedBy(paging,
//						req.getComapanyId(), req.getBrokerBranchCode(),
//						req.getCreatedBy());
				List<LoginMaster> loginlist = loginRepo.findByOaCodeAndCompanyId(loginData.getOaCode(),req.getComapanyId());
				List<String> loginIds=loginlist.stream().map(LoginMaster :: getLoginId ).collect(Collectors.toList())  ;
				datas = repository.findByCompanyIdAndBrokerBranchCodeAndCreatedByIn(paging,
						req.getComapanyId(), req.getBrokerBranchCode(),loginIds);
			} else {
//				datas = repository.findByCompanyIdAndBranchCodeAndCreatedBy(paging, req.getComapanyId(),
//						req.getBranchCode(), req.getCreatedBy());
				datas  = repository.findByCompanyIdAndBranchCodeAndCreatedBy(paging,
						req.getComapanyId(), req.getBranchCode(),req.getCreatedBy());
			}

			totalCustList.addAll(datas.getContent());
			totalCustList.addAll(custList);

			totalCustList = totalCustList.stream().filter(distinctByKey(o -> Arrays.asList(o.getCustomerReferenceNo()))).collect(Collectors.toList());

			for (EserviceCustomerDetails data : totalCustList) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				res = dozerMapper.map(data, CustomerDetailsGetRes.class);
				res.setMobileCodeDesc1(data.getMobileCodeDesc1()==null?"":data.getMobileCodeDesc1());
				res.setMobileCodeDesc2(data.getMobileCodeDesc2()==null?"":data.getMobileCodeDesc2());
				res.setMobileCodeDesc3(data.getMobileCodeDesc3()==null?"":data.getMobileCodeDesc3());
				res.setMobileCode1(data.getMobileCode1()==null?"":data.getMobileCode1());
				res.setMobileCode2(data.getMobileCode2()==null?"":data.getMobileCode2());
				res.setMobileCode3(data.getMobileCode3()==null?"":data.getMobileCode3());
				res.setWhatsappCode(data.getWhatsappCode()==null?"":data.getWhatsappCode());
				res.setWhatsappDesc(data.getWhatsappCodeDesc()==null?"":data.getWhatsappCodeDesc());
				res.setWhatsappNo(data.getWhatsappNo()==null?"":data.getWhatsappNo());
				res.setVrTinNo( data.getIdType().equalsIgnoreCase("6") ? data.getIdNumber() : data.getVrTinNo()  );
				res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
					    (data.getFirstName() == null ? "" : data.getFirstName()) + " " +
					    (data.getLastName() == null ? "" : data.getLastName()));
				res.setSocioProfessionalCategory(data.getSocioProfessionalCategory());
				res.setActivities(data.getActivities());
				res.setAddress2(data.getAddress2()==null?"":data.getAddress2());
				res.setCustomerAsInsurer(data.getCustomerAsInsurer()==null?"":data.getCustomerAsInsurer());
				res.setIsTaxExempted(data.getIsTaxExempted()==null?"":data.getIsTaxExempted());
				res.setKraPin(data.getKraPin()==null?"":data.getKraPin());
				res.setFirstName(data.getFirstName()==null?"":data.getFirstName());
				res.setMiddleName(data.getMiddleName()==null?"":data.getMiddleName());
				res.setLastName(data.getLastName()==null?"":data.getLastName());
				res.setTitleDesc(data.getTitleDesc()==null?"":data.getTitleDesc());
				res.setTitle(data.getTitle()==null?"":data.getTitle());
				res.setCustomerType(data.getCustomerType() == null ? "" : data.getCustomerType());
				res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +

						(data.getFirstName() == null ? "" : data.getFirstName()) + " " +

						(data.getLastName() == null ? "" : data.getLastName()));
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<CustomerDetailsGetRes> getbyvrtinno(EserviceCustomerSearchVrtinReq req) {
		List<CustomerDetailsGetRes> reslist = new ArrayList<CustomerDetailsGetRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();

			// Search By Tin No
			String searchKey = "TinNumber";
			List<EserviceCustomerDetails> list = searchCustomerData(searchKey, searchValue, companyId);
			if (list.size() <= 0) {
				searchKey = "IdNumber";
				list = searchCustomerData(searchKey, searchValue, companyId);
			}
			if (list.size() <= 0) {
				searchKey = "ClientName";
				list = searchCustomerData(searchKey, searchValue, companyId);
			}

			for (EserviceCustomerDetails data : list) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				dozermapper.map(data, res);
				reslist.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			;
			return null;
		}
		return reslist;
	}

	public List<EserviceCustomerDetails> searchCustomerData(String searchKey, String searchValue, String companyId) {
		List<EserviceCustomerDetails> list = new ArrayList<EserviceCustomerDetails>();
		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);
			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("updatedDate")));

			Predicate n1 = null;
			// Where
			if (searchKey.equalsIgnoreCase("TinNumber")) {
				n1 = cb.like(cb.lower(c.get("vrTinNo")), "%" + searchValue + "%");
			} else if (searchKey.equalsIgnoreCase("IdNumber")) {
				n1 = cb.like(cb.lower(c.get("idNumber")), "%" + searchValue + "%");
			} else if (searchKey.equalsIgnoreCase("ClientName")) {
				n1 = cb.like(cb.lower(c.get("clientName")), "%" + searchValue + "%");
			}
			Predicate n2 = cb.equal(c.get("companyId"), companyId);
			Predicate n3 = cb.equal(c.get("status"), "Y");

			query.where(n1, n2,n3).orderBy(orderList);
			// Get Result
			TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			;
			return null;
		}
		return list;
	}

	@Override
	public List<CustomerDetailsGetRes> getActiveCustomerDetails(GetAllCustomerDetailsReq req) {
		List<CustomerDetailsGetRes> resList = new ArrayList<CustomerDetailsGetRes>();
	//	DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {

			// Limit , Offset
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			Pageable paging = PageRequest.of(limit, offset, Sort.by("updatedDate").descending());

			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
//			LoginMaster Brokerlogin = loginRepo.findByAgencyCode(loginData.getOaCode().toString());
			LoginMaster Brokerlogin = loginRepo.findByAgencyCodeAndCompanyId(loginData.getOaCode().toString(),req.getComapanyId());
			List<EserviceCustomerDetails> custList = new ArrayList<EserviceCustomerDetails>();
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			Root<PersonalInfo> p = query.from(PersonalInfo.class);

			// Select
			query.select(c  ).distinct(true);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(p.get("customerId"), h.get("customerId"));
			Predicate n2 = cb.equal(h.get("companyId"), req.getComapanyId());
			Predicate n3 = cb.equal(c.get("customerReferenceNo"), p.get("customerReferenceNo"));
			Predicate n6 = cb.equal(c.get("status"), "Y");
			Predicate n7 = cb.equal(c.get("companyId"), req.getComapanyId());
			Predicate n8 = cb.equal(c.get("customerType"), req.getCustomerType());

		//	Predicate n3 = cb.equal(h.get("productId"), req.getProductId());
			Predicate n4 = null ;
			Predicate n5 = null ;

			if (loginData.getUserType().equalsIgnoreCase("Broker") || loginData.getUserType().equalsIgnoreCase("User")) {

				n4 = cb.equal(  h.get("brokerBranchCode"), req.getBrokerBranchCode());
				if ("Broker".equalsIgnoreCase(loginData.getUserType())) {
					Subquery<String> loginId = query.subquery(String.class);
					Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
					loginId.select(ocpm1.get("loginId"));
					Predicate a1 = cb.equal(ocpm1.get("agencyCode"), loginData.getOaCode());
					Predicate a2 = cb.equal(ocpm1.get("companyId"), req.getComapanyId());
					loginId.where(a1,a2);
					n5 = cb.equal(c.get("createdBy"), loginId.as(String.class));
				}else 	if ("User".equalsIgnoreCase(loginData.getUserType())&& !"Direct".equalsIgnoreCase(Brokerlogin.getSubUserType())) {
					Subquery<Long> loginId = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
					loginId.select(ocpm1.get("loginId"));
					Predicate a1 = cb.equal(ocpm1.get("agencyCode"), loginData.getOaCode());
					Predicate a2 = cb.equal(ocpm1.get("companyId"), req.getComapanyId());
					loginId.where(a1,a2);
					n5 = cb.equal(c.get("createdBy"), loginId.as(String.class));
				}else 	if ("User".equalsIgnoreCase(loginData.getUserType())&& "Direct".equalsIgnoreCase(Brokerlogin.getSubUserType())) {
					n5 = cb.equal(c.get("createdBy"), req.getCreatedBy());
				}
			//	n5 = cb.equal(  h.get("loginId"), req.getCreatedBy());
			} else {

				n4 = cb.equal(  h.get("branchCode"),  req.getBranchCode());
				n5 = cb.equal(  h.get("applicationId"), req.getCreatedBy());
			}

			query.where(n1, n2,n3, n4,n5,n6,n7,n8).orderBy(orderList);

			// Get Result
			TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			custList = result.getResultList();

			Page<EserviceCustomerDetails> datas = null;

			if (loginData.getUserType().equalsIgnoreCase("Broker")|| (loginData.getUserType().equalsIgnoreCase("User")&& !"Direct".equalsIgnoreCase(Brokerlogin.getSubUserType()))) {
//				List<LoginMaster> loginlist = loginRepo.findByOaCode(loginData.getOaCode());
				List<LoginMaster> loginlist = loginRepo.findByOaCodeAndCompanyId(loginData.getOaCode(),req.getComapanyId());
				List<String> loginIds=loginlist.stream().map(LoginMaster :: getLoginId ).collect(Collectors.toList())  ;
				datas = repository.findByCompanyIdAndBrokerBranchCodeAndStatusAndCreatedByIn(paging,req.getComapanyId(), req.getBrokerBranchCode(),"Y",loginIds);
			} else {
				datas = repository.findByCompanyIdAndBranchCodeAndCreatedByAndStatus(paging,
						req.getComapanyId(), req.getBranchCode(),
						req.getCreatedBy(), "Y");
			}

			List<EserviceCustomerDetails> totalCustList = new ArrayList<EserviceCustomerDetails>();
			totalCustList.addAll(datas.getContent());
			totalCustList.addAll(custList);

			totalCustList = totalCustList.stream().filter(distinctByKey(o -> Arrays.asList(o.getCustomerReferenceNo()))).collect(Collectors.toList());

			if(req.getProductId().equalsIgnoreCase("93")) {
				totalCustList.stream().filter(o -> o.getCustomerType() == null || o.getCustomerType().equalsIgnoreCase("DOMESTIC")).collect(Collectors.toList());
				} else {
					totalCustList.stream().filter(o -> o.getCustomerType() == null || !o.getCustomerType().equalsIgnoreCase("DOMESTIC")).collect(Collectors.toList());
				}
			for (EserviceCustomerDetails data : totalCustList) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				res = dozerBeanMapper.map(data, CustomerDetailsGetRes.class);
				res.setMobileCodeDesc1(data.getMobileCodeDesc1()==null?"":data.getMobileCodeDesc1());
				res.setMobileCodeDesc2(data.getMobileCodeDesc2()==null?"":data.getMobileCodeDesc2());
				res.setMobileCodeDesc3(data.getMobileCodeDesc3()==null?"":data.getMobileCodeDesc3());
				res.setMobileCode1(data.getMobileCode1()==null?"":data.getMobileCode1());
				res.setMobileCode2(data.getMobileCode2()==null?"":data.getMobileCode2());
				res.setMobileCode3(data.getMobileCode3()==null?"":data.getMobileCode3());
				res.setWhatsappCode(data.getWhatsappCode()==null?"":data.getWhatsappCode());
				res.setWhatsappDesc(data.getWhatsappCodeDesc()==null?"":data.getWhatsappCodeDesc());
				res.setWhatsappNo(data.getWhatsappNo()==null?"":data.getWhatsappNo());
				res.setVrTinNo( data.getIdType().equalsIgnoreCase("6") ? data.getIdNumber() : data.getVrTinNo()  );
				res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
					    (data.getFirstName() == null ? "" : data.getFirstName()) + " " +
					    (data.getLastName() == null ? "" : data.getLastName()));
				res.setSocioProfessionalCategory(data.getSocioProfessionalCategory());
				res.setActivities(data.getActivities());
				res.setAddress2(data.getAddress2()==null?"":data.getAddress2());
				res.setCustomerAsInsurer(data.getCustomerAsInsurer()==null?"":data.getCustomerAsInsurer());
				res.setIsTaxExempted(data.getIsTaxExempted()==null?"":data.getIsTaxExempted());
				res.setFirstName(data.getFirstName()==null?"":data.getFirstName());
				/**res.setMiddleName(data.getMiddleName()==null?"":data.getMiddleName());**/
				/**res.setLastName(data.getLastName()==null?"":data.getLastName());**/
				res.setTitle(data.getTitle()==null?"":data.getTitle());
				res.setTitleDesc(data.getTitleDesc()==null?"":data.getTitleDesc());
				res.setLeadSeqNo(data.getLeadSeqNo());
				res.setComplianceStatus(data.getComplianceStatus() == null ? "" : data.getComplianceStatus());
				res.setComplianceStatusId(data.getComplianceStatusId() == null ? "" : data.getComplianceStatusId());
				res.setIndustryType(data.getIndustryType() == null ? "" : data.getIndustryType());
				res.setIndustryTypeId(data.getIndustryTypeId() == null ? "" : data.getIndustryTypeId());
				res.setWealthSource(data.getWealthSource() == null ? "" : data.getWealthSource());
				res.setWealthSourceId(data.getWealthSourceId() == null ? "" : data.getWealthSourceId());
				res.setOwners(data.getOwners() == null ? "" : data.getOwners());
				res.setOwnersId(data.getOwnersId() == null ? "" : data.getOwnersId());
				res.setLegalStructure(data.getLegalStructure() == null ? "" : data.getLegalStructure());
				res.setLegalStructureId(data.getLegalStructureId() == null ? "" : data.getLegalStructureId());
				res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +

						(data.getFirstName() == null ? "" : data.getFirstName()) + " " +

						(data.getLastName() == null ? "" : data.getLastName()) + " "
						+ (data.getMiddleName() == null ? "" : data.getMiddleName()));
				res.setCustomerType(data.getCustomerType() == null ? "" : data.getCustomerType());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

//	@Override
//	public List<CustomerDetailsGetRes> getActiveCustomerDetails(GetAllCustomerDetailsReq req) {
//	    List<CustomerDetailsGetRes> resList = new ArrayList<>();
//	    try {
//	        // --- Auth lookups ---
//	        LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
//	        LoginMaster brokerLogin = loginRepo.findByAgencyCodeAndCompanyId(loginData.getOaCode().toString(), req.getComapanyId());
//
//	        // --- Single criteria query (removed duplicate repo query) ---
//	        CriteriaBuilder cb = em.getCriteriaBuilder();
//	        CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);
//
//	        Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
//	        Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
//	        Root<PersonalInfo> p = query.from(PersonalInfo.class);
//
//	        query.select(c).distinct(true);
//
//	        // --- Base predicates ---
//	        List<Predicate> predicates = new ArrayList<>();
//	        predicates.add(cb.equal(p.get("customerId"), h.get("customerId")));
//	        predicates.add(cb.equal(h.get("companyId"), req.getComapanyId()));
//	        predicates.add(cb.equal(c.get("customerReferenceNo"), p.get("customerReferenceNo")));
//	        predicates.add(cb.equal(c.get("status"), "Y"));
//	        predicates.add(cb.equal(c.get("companyId"), req.getComapanyId()));
//	        if (StringUtils.isNotBlank(req.getCustomerType())) {
//	            predicates.add(cb.equal(c.get("customerType"), req.getCustomerType()));
//	        }
//
//	        String userType = loginData.getUserType();
//
//	        // --- Role-based predicates ---
//	        if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//	            predicates.add(cb.equal(h.get("brokerBranchCode"), req.getBrokerBranchCode()));
//
//	            boolean isDirect = "Direct".equalsIgnoreCase(brokerLogin.getSubUserType());
//
//	            if ("User".equalsIgnoreCase(userType) && isDirect) {
//	                predicates.add(cb.equal(c.get("createdBy"), req.getCreatedBy()));
//	            } else {
//	                // Broker or non-direct User: filter by all logins under same agency
//	                Subquery<String> loginIdSub = query.subquery(String.class);
//	                Root<LoginMaster> lm = loginIdSub.from(LoginMaster.class);
//	                loginIdSub.select(lm.get("loginId"));
//	                loginIdSub.where(
//	                    cb.equal(lm.get("agencyCode"), loginData.getOaCode()),
//	                    cb.equal(lm.get("companyId"), req.getComapanyId())
//	                );
//	                predicates.add(c.get("createdBy").in(loginIdSub));
//	            }
//	        } else {
//	            predicates.add(cb.equal(h.get("branchCode"), req.getBranchCode()));
//	            predicates.add(cb.equal(h.get("applicationId"), req.getCreatedBy()));
//	        }
//
//	        query.where(predicates.toArray(new Predicate[0]))
//	             .orderBy(cb.desc(c.get("updatedDate")));
//
//	        // --- Paginated query (single DB hit) ---
//	        int limit  = StringUtils.isBlank(req.getLimit())  ? 0   : Integer.parseInt(req.getLimit());
//	        int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.parseInt(req.getOffset());
//
//	        TypedQuery<EserviceCustomerDetails> typedQuery = em.createQuery(query);
//	        typedQuery.setFirstResult(limit * offset);
//	        typedQuery.setMaxResults(offset);
//	        List<EserviceCustomerDetails> custList = typedQuery.getResultList();
//
//	        // --- Fix: filter is now actually assigned (was silent no-op before) ---
//	        boolean isDomestic = "93".equalsIgnoreCase(req.getProductId());
//	        custList = custList.stream()
//	            .filter(o -> o.getCustomerType() == null ||
//	                         isDomestic == o.getCustomerType().equalsIgnoreCase("DOMESTIC"))
//	            .collect(Collectors.toList());
//
//	        // --- Map results (direct field sets — no reflection overhead) ---
//	        for (EserviceCustomerDetails data : custList) {
//	        	resList.add(mapToRes(data));
//	        }
//
//	    } catch (Exception e) {
//	        log.error("getActiveCustomerDetails failed: {}", e.getMessage(), e);
//	        return null;
//	    }
//	    return resList;
//	}
//
//

	private static String nullToEmpty(String s) { return s == null ? "" : s; }

	private CustomerDetailsGetRes mapToRes(EserviceCustomerDetails data) {
	    CustomerDetailsGetRes res = new CustomerDetailsGetRes();

	    // --- Identity / Reference ---
	    res.setCustomerReferenceNo(nullToEmpty(data.getCustomerReferenceNo()));
	    res.setCompanyId(data.getCompanyId());
	    res.setCustomerType(nullToEmpty(data.getCustomerType()));
	    res.setCustomerCode(nullToEmpty(data.getCustomerCode()));

	    // --- Name fields ---
	    res.setFirstName(nullToEmpty(data.getFirstName()));
	    res.setMiddleName(nullToEmpty(data.getMiddleName()));
	    res.setLastName(nullToEmpty(data.getLastName()));
	    res.setTitle(nullToEmpty(data.getTitle()));
	    res.setTitleDesc(nullToEmpty(data.getTitleDesc()));

	    // Single correct clientName (title + first + last + middle)
	    res.setClientName(
	        nullToEmpty(data.getTitleDesc())  + " " +
	        nullToEmpty(data.getFirstName())  + " " +
	        nullToEmpty(data.getLastName())   + " " +
	        nullToEmpty(data.getMiddleName())
	    );

	    // --- ID / Policy holder ---
	    res.setIdType(nullToEmpty(data.getIdType()));
	    res.setIdTypeDesc(nullToEmpty(data.getIdTypeDesc()));
	    res.setIdNumber(nullToEmpty(data.getIdNumber()));
	    res.setPolicyHolderType(nullToEmpty(data.getPolicyHolderType()));
	    res.setPolicyHolderTypeid(nullToEmpty(data.getPolicyHolderTypeid()));
	    res.setPolicyHolderTypeDesc(nullToEmpty(data.getPolicyHolderTypeDesc()));

	    // VrTin: use IdNumber when IdType == "6", else use vrTinNo
	    res.setVrTinNo("6".equalsIgnoreCase(data.getIdType())
	            ? data.getIdNumber() : data.getVrTinNo());
	    res.setVrnGst(nullToEmpty(data.getVrnGst()));
	    res.setKraPin(nullToEmpty(data.getKraPin()));

	    // --- Demographics ---
	    res.setAge(data.getAge() == null ? "" : String.valueOf(data.getAge()));
	    res.setGender(nullToEmpty(data.getGender()));
	    res.setGenderDesc(nullToEmpty(data.getGenderDesc()));
	    res.setNationality(nullToEmpty(data.getNationality()));
	    res.setNationalityName(nullToEmpty(data.getNationalityName()));
	    res.setPlaceOfBirth(nullToEmpty(data.getPlaceOfBirth()));
	    res.setMaritalStatus(nullToEmpty(data.getMaritalStatus()));
	    res.setDobOrRegDate(data.getDobOrRegDate());
	    res.setAppointmentDate(data.getAppointmentDate());
	    res.setExpiryDate(data.getExpiryDate());
	    res.setRiskAssessmentDate(data.getRiskAssessmentDate());

	    // --- Occupation / Business ---
	    res.setOccupation(nullToEmpty(data.getOccupation()));
	    res.setOccupationDesc(nullToEmpty(data.getOccupationDesc()));
	    res.setOtherOccupation(nullToEmpty(data.getOtherOccupation()));
	    res.setBusinessType(nullToEmpty(data.getBusinessType()));
	    res.setBusinessTypeDesc(nullToEmpty(data.getBusinessTypeDesc()));
	    res.setSocioProfessionalCategory(data.getSocioProfessionalCategory());
	    res.setActivities(data.getActivities());

	    // --- Address ---
	    res.setAddress1(nullToEmpty(data.getAddress1()));
	    res.setAddress2(nullToEmpty(data.getAddress2()));
	    res.setAddress3(nullToEmpty(data.getAddress3()));
	    res.setStreet(nullToEmpty(data.getStreet()));
	    res.setPinCode(nullToEmpty(data.getPinCode()));
	    res.setRegionCode(nullToEmpty(data.getRegionCode()));
	    res.setStateCode(data.getStateCode() == null ? "" : String.valueOf(data.getStateCode()));
	    res.setStateName(nullToEmpty(data.getStateName()));
	    res.setCityCode(data.getCityCode() == null ? "" : String.valueOf(data.getCityCode()));
	    res.setCityName(nullToEmpty(data.getCityName()));
	    res.setCountry(nullToEmpty(data.getCountry()));
	    res.setCountryName(nullToEmpty(data.getCountryName()));
	    res.setZone(data.getZone() == null ? "" : String.valueOf(data.getZone()));

	    // --- Contact: Mobile ---
	    res.setMobileNo1(nullToEmpty(data.getMobileNo1()));
	    res.setMobileNo2(nullToEmpty(data.getMobileNo2()));
	    res.setMobileNo3(nullToEmpty(data.getMobileNo3()));
	    res.setMobileCode1(nullToEmpty(data.getMobileCode1()));
	    res.setMobileCode2(nullToEmpty(data.getMobileCode2()));
	    res.setMobileCode3(nullToEmpty(data.getMobileCode3()));
	    res.setMobileCodeDesc1(nullToEmpty(data.getMobileCodeDesc1()));
	    res.setMobileCodeDesc2(nullToEmpty(data.getMobileCodeDesc2()));
	    res.setMobileCodeDesc3(nullToEmpty(data.getMobileCodeDesc3()));

	    // --- Contact: WhatsApp ---
	    res.setWhatsappCode(nullToEmpty(data.getWhatsappCode()));
	    res.setWhatsappDesc(nullToEmpty(data.getWhatsappCodeDesc())); // note: entity field is whatsappCodeDesc
	    res.setWhatsappNo(nullToEmpty(data.getWhatsappNo()));

	    // --- Contact: Telephone / Fax / Email ---
	    res.setTelephoneNo1(nullToEmpty(data.getTelephoneNo1()));
	    res.setTelephoneNo2(nullToEmpty(data.getTelephoneNo2()));
	    res.setTelephoneNo3(nullToEmpty(data.getTelephoneNo3()));
	    res.setFax(nullToEmpty(data.getFax()));
	    res.setEmail1(nullToEmpty(data.getEmail1()));
	    res.setEmail2(nullToEmpty(data.getEmail2()));
	    res.setEmail3(nullToEmpty(data.getEmail3()));
	    res.setPhoneNoCode(nullToEmpty(data.getPhoneNoCode()));
	    res.setPreferredNotification(nullToEmpty(data.getPreferredNotification()));

	    // --- Language ---
	    res.setLanguage(nullToEmpty(data.getLanguage()));
	    res.setLanguageDesc(nullToEmpty(data.getLanguageDesc()));

	    // --- Tax / Compliance ---
	    res.setIsTaxExempted(nullToEmpty(data.getIsTaxExempted()));
	    res.setTaxExemptedId(nullToEmpty(data.getTaxExemptedId()));
	    res.setComplianceStatus(nullToEmpty(data.getComplianceStatus()));
	    res.setComplianceStatusId(nullToEmpty(data.getComplianceStatusId()));

	    // --- KYC / AML ---
	    res.setIndustryType(nullToEmpty(data.getIndustryType()));
	    res.setIndustryTypeId(nullToEmpty(data.getIndustryTypeId()));
	    res.setWealthSource(nullToEmpty(data.getWealthSource()));
	    res.setWealthSourceId(nullToEmpty(data.getWealthSourceId()));
	    res.setOwners(nullToEmpty(data.getOwners()));
	    res.setOwnersId(nullToEmpty(data.getOwnersId()));
	    res.setLegalStructure(nullToEmpty(data.getLegalStructure()));
	    res.setLegalStructureId(nullToEmpty(data.getLegalStructureId()));

	    // --- Status / Audit ---
	    res.setStatus(nullToEmpty(data.getStatus()));
	    res.setClientStatus(nullToEmpty(data.getClientStatus()));
	    res.setClientStatusDesc(nullToEmpty(data.getClientStatusDesc()));
	    res.setCreatedBy(nullToEmpty(data.getCreatedBy()));
	    res.setUpdatedBy(nullToEmpty(data.getUpdatedBy()));
	    res.setUpdatedDate(data.getUpdatedDate());
	    res.setEntryDate(data.getEntryDate());

	    // --- Branch / Product ---
	    res.setBranchCode(nullToEmpty(data.getBranchCode()));
	    res.setProductId(data.getProductId() == null ? "" : String.valueOf(data.getProductId()));

	    // --- Misc ---
	    res.setCustomerAsInsurer(nullToEmpty(data.getCustomerAsInsurer()));
	    res.setVipFlag(nullToEmpty(data.getVipFlag()));
	    res.setLeadSeqNo(data.getLeadSeqNo());
	    res.setFather_name(nullToEmpty(data.getFather_name()));
	    res.setMother_name(nullToEmpty(data.getMother_name()));

	    // --- Fields only in DTO (not in entity — leave as null/default) ---
	    // endorsementDate, endorsementRemarks, endorsementEffdate,
	    // originalPolicyNo, endtPrevPolicyNo, endtPrevQuoteNo,
	    // endtCount, endtStatus, isFinaceYn, endtCategDesc,
	    // endorsementType, endorsementTypeDesc
	    // → These are commented out in the entity. Populate from a separate
	    //   endorsement source if needed.

	    return res;
	}
	@Override
	public SuccessRes updatebycustrefno(GetByCustomerRefNoReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceCustomerDetails> save = new ArrayList<EserviceCustomerDetails>();
			List<EserviceCustomerDetails> list = repository.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo());
			// Update E service Customer Details
			if (list.size() > 0 && list != null) {
				for (EserviceCustomerDetails data : list) {
					EserviceCustomerDetails saveData = new EserviceCustomerDetails();
					saveData = dozerMapper.map(data, EserviceCustomerDetails.class);
					saveData.setPolCustCode(req.getPolCustCode());
					save.add(saveData);
				}
				repository.saveAllAndFlush(save);
				System.out.println("**********Eservice Customer Details Updated Successfully for Customer Reference No : "+req.getCustomerReferenceNo());
			}
			// Update Personal Info
			List<PersonalInfo> savePersonalInfo = new ArrayList<PersonalInfo>();
			List<PersonalInfo> personalInfoList = personalInforepo.findByCustomerReferenceNo(req.getCustomerReferenceNo());
			if (list.size() > 0 && list != null) {
				for (PersonalInfo data : personalInfoList) {
					PersonalInfo saveData = new PersonalInfo();
					saveData = dozerMapper.map(data, PersonalInfo.class);
					saveData.setPolCustCode(req.getPolCustCode());
					savePersonalInfo.add(saveData);
				}
				personalInforepo.saveAllAndFlush(savePersonalInfo);
				System.out.println("**********PersonalInfo Updated Successfully for Customer Reference No : "+req.getCustomerReferenceNo());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public CommonRes validateCustomerId(String accountType, String identifyType, String companyId, String saveOrSubmit,
			String customerId) {

		CommonRes res = new CommonRes();
		List<String> errorCodes = new ArrayList<>();
		List<Error> fetchErrorDetails = new ArrayList<>();

		if (null != companyId && !companyId.isEmpty() && null != accountType && !accountType.isEmpty()
				&& null != identifyType && !identifyType.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {

			if ("Submit".equalsIgnoreCase(saveOrSubmit)) {

				if (null != customerId && !customerId.isEmpty()) {

					if ("100028".equals(companyId)) {

						if ("individual".equalsIgnoreCase(accountType.trim())) {

							if ("NIC".equalsIgnoreCase(identifyType.trim())) {

								if (customerId.length() != 14) {
									errorCodes.add("2181");
								} else if (!Character.isLetter(customerId.charAt(0))) {

									errorCodes.add("2182");
								} else if (!customerId.matches("^[a-zA-Z0-9]*$")) {

									errorCodes.add("2183");
								}

							} else if ("passport".equalsIgnoreCase(identifyType.trim())) {

								if (!customerId.matches("^[a-zA-Z0-9]*$")) {

									errorCodes.add("2183");
								}

							}

						} else if ("corporate".equalsIgnoreCase(accountType.trim())) {

							if ("BRNNUMBER".equalsIgnoreCase(identifyType.trim())) {

								if (!Character.isLetter(customerId.charAt(0))) {
									errorCodes.add("2182");
								} else if (!customerId.matches("^[a-zA-Z0-9]*$")) {

									errorCodes.add("2183");
								}

							}

							else if ("NGOID".equalsIgnoreCase(identifyType.trim())) {

								if ((customerId.charAt(0) != 'f' && customerId.charAt(0) != 'F')) {

									errorCodes.add("2184");
								} else if (!customerId.matches("^[a-zA-Z0-9]*$")) {
									errorCodes.add("2183");

								}

							}

						}
					} else { // other companies

						if (customerId.length() > 100) {

							errorCodes.add("1014");
						} else if (customerId.matches("[0-9]+") && Double.valueOf(customerId) <= 0) {
							errorCodes.add("1015");
						}

					}
				} else { // common to all companies

					errorCodes.add("1013");

				}
			}

		} else {
			res.setMessage("Failed - BadRequest");
			res.setIsError(false);
			res.setErrorMessage(null);
			res.setCommonResponse(null);
			res.setErroCode(0);
			return res;
		}

		if (null != errorCodes && !errorCodes.isEmpty()) {

			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			// comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(companyId);
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("1");
			comErrDescReq.setModuleName("CUSTOMER CREATION");

			fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

			if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

				res.setMessage("Failed");
				res.setIsError(true);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;

			}
		} else {
			res.setMessage("Success");
			res.setIsError(false);
			res.setErrorMessage(fetchErrorDetails);
			res.setCommonResponse(null);
			res.setErroCode(0);
			return res;
		}
		return res;

	}

		@Override
		public CommonRes validateCustomerName(String customerName, String companyId, String saveOrSubmit) {

			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();
			CommonRes res = new CommonRes();


			if(null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty() ) {

			if ("Submit".equalsIgnoreCase(saveOrSubmit)) {

				if (null != customerName && !customerName.isEmpty()) { // common to all companies

					if (customerName.length() > 250) {
						errorCodes.add("1002");
					} else if (!customerName.matches("[a-zA-Z.&() ]+")) {
						errorCodes.add("1003");
					}

				} else {
					errorCodes.add("1001");
				}

			} else if ("Save".equalsIgnoreCase(saveOrSubmit)) {

				if (null != customerName && !customerName.isEmpty()) {
					if (customerName.length() > 100) {
						errorCodes.add("1085");
					}
				} else {
					errorCodes.add("1084");
				}

			}
		} else {
			res.setMessage("Failed - BadRequest");
			res.setIsError(false);
			res.setErrorMessage(null);
			res.setCommonResponse(null);
			res.setErroCode(0);
			return res;
		}
			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;
		}

		@Override
		public CommonRes validateOccupationAndOtherOccupation(String occupation, String otherOccupation,
				String companyId, String saveOrSubmit) {

			CommonRes res = new CommonRes();
			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {

				if ((null == occupation || occupation.isEmpty()) && null != otherOccupation
						&& !otherOccupation.isEmpty()) {

					res.setMessage("Failed - BadRequest");
					res.setIsError(false);
					res.setErrorMessage(null);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;
				}
				if ("Submit".equalsIgnoreCase(saveOrSubmit)) {
				if (null != occupation && !occupation.isEmpty()) {

					if ("99999".equals(occupation)) {

						if (null != otherOccupation && !otherOccupation.isEmpty()) {

							if (otherOccupation.length() > 100) {
								errorCodes.add("1024");
							} else if (!otherOccupation.matches("[a-zA-Z\\s]+")) {
								errorCodes.add("1025");
							}

						} else {
							errorCodes.add("1023");
						}

					}
				} else {
					errorCodes.add("1022");
				}

			}
		} else {
				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;

		}

		@Override
		public CommonRes validateAddress(String address, String companyId, String saveOrSubmit) {

			CommonRes res = new CommonRes();
			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if(null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {

				if("Submit".equalsIgnoreCase(saveOrSubmit)) {

					if(null != address && !address.isEmpty()) {

						if(address.length() > 100) {
							errorCodes.add("1009");
						}

					}else {
						errorCodes.add("1004");
					}

				}


			}else {

				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;

			}

			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;

		}

		@Override
		public CommonRes validateCityName(String cityName, String companyId, String saveOrSubmit) {

			CommonRes res = new CommonRes();
			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {

				if ("Submit".equalsIgnoreCase(saveOrSubmit)) {

					if ("100004".equals(companyId)) { // specific company

						if (null != cityName && !cityName.isEmpty()) {

							if (cityName.length() > 100) {
								errorCodes.add("1081");
							}

						} else {
							errorCodes.add("1080");
						}

					} else { // other company

						if (null != cityName && !cityName.isEmpty()) {

							if (cityName.length() > 100) {
								errorCodes.add("1083");
							}

						} else {
							errorCodes.add("1082");
						}

					}

				}

			} else {

				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;

			}
			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;

		}

		@Override
		public CommonRes validateStatus(String status, String companyId, String saveOrSubmit) {

			CommonRes res = new CommonRes();
			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {

				if ("Submit".equalsIgnoreCase(saveOrSubmit)) { // common to all companies

					if (null != status && !status.isEmpty()) {

						if (status.length() > 1) {
							errorCodes.add("1059");
						} else if (!("Y".equals(status) || "N".equals(status) || "P".equals(status))) {
							errorCodes.add("1060");
						}

					} else {
						errorCodes.add("1058");

					}

				}

			} else {
				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;

		}

		@Override
		public CommonRes validateMobileNumber(String mobileNumber, String mobileCode, String companyId,
				String saveOrSubmit) {

			CommonRes res = new CommonRes();
			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {

				if ("Submit".equalsIgnoreCase(saveOrSubmit)) { // common to all companies

					if (null != mobileCode && !mobileCode.isEmpty()) {
						if (null != mobileNumber && !mobileNumber.isEmpty()) {

							if (mobileNumber.length() > 10 || mobileNumber.length() < 8) {
								errorCodes.add("1027");
							} else if (!mobileNumber.matches("[0-9]+")) {
								errorCodes.add("1028");
							} else if (mobileNumber.matches("[0-9]+") && Double.valueOf(mobileNumber) <= 0) {
								errorCodes.add("1029");
							}
						} else {
							errorCodes.add("1026");

						}
					} else {
						errorCodes.add("1062");

					}

				}

			} else {
				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}

			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;
		}

		@Override
		public CommonRes validateCustomerCreationFields(EserviceCustomerSaveReq req) {
			CommonRes res = new CommonRes();
			List<String> errorList = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != req) {

				if (null != req.getSaveOrSubmit() && !req.getSaveOrSubmit().isEmpty()
						&& "Submit".equalsIgnoreCase(req.getSaveOrSubmit())) {

					if (StringUtils.isBlank(req.getClientName())) {

						errorList.add("1001");
					}
					if (StringUtils.isBlank(req.getAddress1())) {
						errorList.add("1004");
					}
					if (StringUtils.isBlank(req.getClientStatus())) {
						errorList.add("1010");
					}
					if (StringUtils.isBlank(req.getIdType())) {
						errorList.add("1011");
					}

					if (StringUtils.isBlank(req.getPolicyHolderTypeid())) {
						errorList.add("1012");
					}

					if (StringUtils.isBlank(req.getIdNumber())) {
						errorList.add("1013");
					}
					if (StringUtils.isBlank(req.getOccupation())) {
						errorList.add("1022");
					}
					if (StringUtils.isBlank(req.getMobileNo1())) {
						errorList.add("1026");
					}
					if (StringUtils.isBlank(req.getLanguage())) {
						errorList.add("1038");
					}
					if (StringUtils.isBlank(req.getTitle())) {
						errorList.add("1047");
					}
					if (StringUtils.isBlank(req.getNationality())) {
						errorList.add("1048");
					}
					if (StringUtils.isBlank(req.getRegionCode())) {
						errorList.add("1053");
					}
					if (StringUtils.isBlank(req.getIsTaxExempted())) {
						errorList.add("1055");

					}
					if (StringUtils.isBlank(req.getStatus())) {
						errorList.add("1058");
					}
					if (StringUtils.isBlank(req.getStateCode())) {
						errorList.add("1061");
					}

					if (StringUtils.isBlank(req.getMobileCode1())) {
						errorList.add("1062");
					}

					if (StringUtils.isBlank(req.getCreatedBy())) {
						errorList.add("1063");
					}

					if (StringUtils.isNotBlank(req.getCompanyId()) && !req.getCompanyId().equalsIgnoreCase("100019")) {
						// DOB Validation
						if (StringUtils.isNotBlank(req.getPolicyHolderType())
								&& req.getPolicyHolderType().equalsIgnoreCase("1")) {
							if (StringUtils.isNotBlank(req.getIdType()) && req.getIdType().equalsIgnoreCase("1")) {
								if (req.getDobOrRegDate() == null) {
									errorList.add("1065");
								}
							} else {
								if (req.getDobOrRegDate() == null) {
									errorList.add("1069");
								}
							}

						} else if (StringUtils.isNotBlank(req.getPolicyHolderType())
								&& req.getPolicyHolderType().equalsIgnoreCase("2")) {

							if (req.getDobOrRegDate() == null) {
								errorList.add("1073");

							}
						}
					}

					if (StringUtils.isBlank(req.getBranchCode())) {
						errorList.add("1074");
					}

					if (StringUtils.isBlank(req.getProductId())) {
						errorList.add("1076");
					}

					if (StringUtils.isBlank(req.getCompanyId())) {
						errorList.add("1078");
					}
					if (null != req.getCompanyId() && !req.getCompanyId().isEmpty()
							&& req.getCompanyId().equalsIgnoreCase("100004")) {
						if (StringUtils.isBlank(req.getCityName())) {
							errorList.add("1080");
						}
					} else {
						if (StringUtils.isBlank(req.getCityName())) {
							errorList.add("1082");
						}
					}


					if (null != req.getPolicyHolderType() && !req.getPolicyHolderType().isEmpty()
							&& "2".equals(req.getPolicyHolderType())) {

						if (null == req.getBusinessType() || req.getBusinessType().trim().isEmpty()) {
							errorList.add("1050");
						}

						if (null == req.getVrTinNo() || req.getVrTinNo().trim().isEmpty()) {
							errorList.add("1051");
						}
					}

					// length and empty validation



					if (StringUtils.isNotBlank(req.getIsTaxExempted()) && req.getIsTaxExempted().equals("Y")) {
						if (StringUtils.isBlank(req.getTaxExemptedId())) {
							errorList.add("1056");
						} else if (req.getTaxExemptedId().length() > 20) {
							errorList.add("1057");
						}

					}


					             // length validations

					if (StringUtils.isNotBlank(req.getFax()) && req.getFax().length() > 20) {
						errorList.add("1017");

					}
					if (StringUtils.isNotBlank(req.getRegionCode()) && req.getRegionCode().length() > 20) {
						errorList.add("1054");
					}

					if (StringUtils.isNotBlank(req.getCreatedBy()) && req.getCreatedBy().length() > 100) {
						errorList.add("1064");
					}
					if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().length() > 20) {
						errorList.add("1075");
					}
					if (StringUtils.isNotBlank(req.getProductId()) && req.getProductId().length() > 20) {
						errorList.add("1077");
					}
					 if ( StringUtils.isNotBlank(req.getCompanyId()) && req.getCompanyId().length() > 20) {
							errorList.add("1079");
						}
					if(StringUtils.isBlank(req.getCustomerReferenceNo()) && StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getCreatedBy())){
						if( StringUtils.isNotBlank(req.getMobileCode1()) && StringUtils.isNotBlank(req.getMobileNo1())) {
							if(isMoblieNumberDublicate(req.getCompanyId(),req.getCreatedBy(),req.getMobileCode1(),req.getMobileNo1())) {
								errorList.add("2224");
							}
					    }
						if(StringUtils.isNotBlank(req.getIdType()) && StringUtils.isNotBlank(req.getIdNumber())) {
							if(isUniqueIdDublicate(req.getCompanyId(),req.getCreatedBy(),req.getPolicyHolderTypeid(),req.getIdNumber())) {
								errorList.add("2212");
							}
						}
					}else if(StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getCreatedBy())) {
						if(isMoblieNumberNotSame(req.getCustomerReferenceNo(),req.getMobileNo1())){
							if(isMoblieNumberDublicate(req.getCompanyId(),req.getCreatedBy(),req.getMobileCode1(),req.getMobileNo1())) {
								errorList.add("2224");
							}
						}
						if(isIdNumberNotSame(req.getCustomerReferenceNo(),req.getIdNumber(),req.getPolicyHolderTypeid())) {
							if(isUniqueIdDublicate(req.getCompanyId(),req.getCreatedBy(),req.getPolicyHolderTypeid(),req.getIdNumber())) {
								errorList.add("2212");
							}
						}
					}

		} else if (null != req.getSaveOrSubmit() && !req.getSaveOrSubmit().isEmpty()
				&& "Save".equalsIgnoreCase(req.getSaveOrSubmit())) {

					if (StringUtils.isBlank(req.getClientName())) {
						errorList.add("1084");

					}
					if (StringUtils.isBlank(req.getPolicyHolderType())) {
						errorList.add("1086");
					}

					if (null != req.getPolicyHolderType() && !req.getPolicyHolderType().isEmpty()
							&& req.getPolicyHolderType().equalsIgnoreCase("1")) {

						if (req.getDobOrRegDate() != null) {
							if (StringUtils.isBlank(req.getGender())) {
								errorList.add("1087");
							}

						}
					}
					// else
					if (null != req.getPolicyHolderType() && !req.getPolicyHolderType().isEmpty()
							&& req.getPolicyHolderType().equalsIgnoreCase("2")) {

						if (req.getDobOrRegDate() != null) {
							if (StringUtils.isBlank(req.getGender())) {
								errorList.add("1087");
							}

						}
					}

				}
			} else {

				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}

			if (null != errorList && !errorList.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(null != req.getCompanyId() ? req.getCompanyId() : "99999");
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorList, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;
		}

		@Override
		public CommonRes validatePincode(String pinCode, String companyId, String saveOrSubmit) {
			CommonRes res = new CommonRes();
			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()
					&& null != pinCode && !pinCode.isEmpty()) {

				if ("Submit".equalsIgnoreCase(saveOrSubmit)) {

					if (pinCode.length() > 20) {

						errorCodes.add("1016");
					}
				}

			} else {

				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}

			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;

		}

		@Override
		public CommonRes validateEmail(String email, String companyId, String saveOrSubmit) {

			CommonRes res = new CommonRes();
			List<String> errorCodes = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {
				if("Submit".equalsIgnoreCase(saveOrSubmit)) {
				if (null != email && !email.isEmpty()) {

					if (email.length() > 100) {
						errorCodes.add("1032");
					} else {
						boolean b = isValidMail(email);
						if (b == false) {
							errorCodes.add("1033");
						}
					}

				}

			}

			}	else {

				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			if (null != errorCodes && !errorCodes.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorCodes, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;

		}

		@Override
		public CommonRes validateDate(Date date, String policyHolderType, String idType, String companyId,
				String saveOrSubmit , String gender) {

			CommonRes res = new CommonRes();
			List<String> errorList = new ArrayList<>();
			List<Error> fetchErrorDetails = new ArrayList<>();

			if (null != companyId && !companyId.isEmpty() && null != saveOrSubmit && !saveOrSubmit.isEmpty()) {

				if("Submit".equalsIgnoreCase(saveOrSubmit)) {
				// Date Validation
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();

				if (StringUtils.isNotBlank(companyId) && !companyId.equalsIgnoreCase("100019")  ) {
					// DOB Validation
					if (StringUtils.isNotBlank(policyHolderType) && policyHolderType.equalsIgnoreCase("1")) {
						if (StringUtils.isNotBlank(idType) && idType.equalsIgnoreCase("1")) {
							if (date == null) {
								errorList.add("1065");
							}
						}

						try {
							if (date != null) {
								if (date.after(today)) {
									errorList.add("1066");

								} else {
									LocalDate localDate1 = date.toInstant().atZone(ZoneId.systemDefault())
											.toLocalDate();
									LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault())
											.toLocalDate();

									Integer years = Period.between(localDate1, localDate2).getYears();
									if (years > 100) {
										errorList.add("1067");

									} else if (years < 18) {
										errorList.add("1068");

									}

								}

							} else {
								errorList.add("1069");
							}
						} catch (Exception e) {
							errorList.add("1070");
						}
					}
					if (StringUtils.isNotBlank(policyHolderType) && policyHolderType.equalsIgnoreCase("2")) {
						try {
							if (date != null) {
								cal.setTime(today);
								cal.add(Calendar.DAY_OF_MONTH, +1);
								cal.set(Calendar.HOUR_OF_DAY, 23);
								cal.set(Calendar.MINUTE, 50);
								Date tomorrow = cal.getTime();
								if (date.after(tomorrow)) {
									errorList.add("1071");

								} else if (date != null) {
									LocalDate localDate1 = date.toInstant().atZone(ZoneId.systemDefault())
											.toLocalDate();
									LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault())
											.toLocalDate();

									Integer years = Period.between(localDate1, localDate2).getYears();
									if (years > 100) {
										errorList.add("1072");
									}
								}

							} else {
								errorList.add("1073");
							}
						} catch (Exception e) {
							errorList.add("1073");
						}
					}
				}

				}
				else if("Save".equalsIgnoreCase(saveOrSubmit)) {

					// Date Validation
					Calendar cal = new GregorianCalendar();
					Date today = new Date();
					cal.setTime(today);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 50);
					today = cal.getTime();
					if (StringUtils.isNotBlank(policyHolderType) &&  policyHolderType.equalsIgnoreCase("1")) {

						if (date != null) {
							if (StringUtils.isBlank(gender) ) {
								errorList.add("1087");
							}
							if (date.after(today)) {
								errorList.add("1088");

							}

							LocalDate localDate1 = date.toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
							LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

							Integer years = Period.between(localDate1, localDate2).getYears();
							if (years > 100) {
								errorList.add("1089");

							}

						}



					}

					if (StringUtils.isNotBlank(policyHolderType) && policyHolderType.equalsIgnoreCase("2")) {

						if (date != null) {
							 if (date.after(today)) {
									errorList.add("1090");

							}
							 LocalDate localDate1 = date.toInstant().atZone(ZoneId.systemDefault())
										.toLocalDate();
							LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

							Integer years = Period.between(localDate1, localDate2).getYears();
							if (years > 100) {
								errorList.add("1091");

							}
						}


					}
				}

			} else {

				res.setMessage("Failed - BadRequest");
				res.setIsError(false);
				res.setErrorMessage(null);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}

			if (null != errorList && !errorList.isEmpty()) {

				CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
				// comErrDescReq.setBranchCode(req.getBranchCode());
				comErrDescReq.setInsuranceId(companyId);
				comErrDescReq.setProductId("99999");
				comErrDescReq.setModuleId("1");
				comErrDescReq.setModuleName("CUSTOMER CREATION");

				fetchErrorDetails = errorDescService.getErrorDesc(errorList, comErrDescReq);

				if (null != fetchErrorDetails && !fetchErrorDetails.isEmpty()) {

					res.setMessage("Failed");
					res.setIsError(true);
					res.setErrorMessage(fetchErrorDetails);
					res.setCommonResponse(null);
					res.setErroCode(0);
					return res;

				}
			} else {
				res.setMessage("Success");
				res.setIsError(false);
				res.setErrorMessage(fetchErrorDetails);
				res.setCommonResponse(null);
				res.setErroCode(0);
				return res;
			}
			return res;
		}

		private boolean isMoblieNumberDublicate(String companyId, String createdBy, String mobileCode1, String mobileNo1) {
			int count = repository.countByCompanyIdAndCreatedByAndMobileCode1AndMobileNo1(companyId,createdBy,mobileCode1,mobileNo1);
			if(count > 0) {
				return true;
			}
			return false;
		}
		private boolean isUniqueIdDublicate(String companyId, String createdBy, String idType, String idNumber) {
			int count = repository.countByCompanyIdAndCreatedByAndIdTypeAndIdNumber(companyId,createdBy,idType,idNumber);
			if(count > 0) {
				return true;
			}
			return false;
		}
		private boolean isMoblieNumberNotSame(String referanceNumber, String mobileNumber) {
			EserviceCustomerDetails list = repository.findByCustomerReferenceNo(referanceNumber);
			if(list.getMobileNo1().equalsIgnoreCase(mobileNumber)) {
				return false;
			}
			return true;
		}
		private boolean isIdNumberNotSame(String referanceNumber, String IdNumber, String idType) {
			EserviceCustomerDetails list = repository.findByCustomerReferenceNo(referanceNumber);
			if(list.getIdNumber().equalsIgnoreCase(IdNumber) && list.getIdType().equalsIgnoreCase(idType)) {
				return false;
			}
			return true;
		}


		@Override
		public List<Error> validate(CustomerChangesSaveReq req) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SuccessRes customerChanges(CustomerChangesSaveReq req) {

			SuccessRes res = new SuccessRes();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {

				if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
					List<HomePositionMaster> homeData = homePosistionRepo
							.findByRequestReferenceNo(req.getRequestReferenceNo());

					if (homeData != null && homeData.size() > 0) {
						String companyId = homeData.get(0).getCompanyId();
						String productId = homeData.get(0).getProductId().toString();
						String customerId = homeData.get(0).getCustomerId();

						// From List Item Value
						String gender = getListItem (req.getCompanyId() , req.getBranchCode() ,"GENDER",req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
						String title = getListItem (req.getCompanyId() , req.getBranchCode() ,"NAME_TITLE",req.getTitle());//listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
						String language = getListItem (req.getCompanyId() , req.getBranchCode() ,"LANGUAGE",req.getLanguage());//listRepo.findByItemTypeAndItemCode("LANGUAGE", req.getLanguage());
						String policyHolderType = getListItem ("99999" , req.getBranchCode() ,"POLICY_HOLDER_TYPE",req.getPolicyHolderType());//listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",	req.getPolicyHolderType());
						String policyHolderTypeId = getListItem (req.getCompanyId(), req.getBranchCode() ,"POLICY_HOLDER_ID_TYPE",req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());

						if(StringUtils.isNotBlank(req.getMobileCode1())){
							String mobileCode1 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode1());
//						saveData.setMobileCodeDesc1(mobileCode1);

						}
				        if(StringUtils.isNotBlank(req.getMobileCode2())){
				        	String mobileCode2 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode2());
//				        	saveData.setMobileCodeDesc2(mobileCode2);

				        }


				        if(StringUtils.isNotBlank(req.getMobileCode3())){
				        	String mobileCode3 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode3());
//						saveData.setMobileCodeDesc3(mobileCode3);

				        }
				        if(StringUtils.isNotBlank(req.getWhatsappCode())){
				        	String whatsappCode = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getWhatsappCode());
//						saveData.setWhatsappCodeDesc(whatsappCode);

				        }

						if (StringUtils.isNotBlank(req.getBusinessType())) {
							String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
//							saveData.setBusinessTypeDesc(businessType);
						}
			 			String occupationDesc = getByOccupationId(req.getOccupation(), req.getCompanyId(),req.getProductId() , req.getBranchCode());
			 		// Age Calculation
						int age = 0 ;
						Date dob = null;
						if (req.getDobOrRegDate() !=null) {
							dob = req.getDobOrRegDate();
							Date today = new Date();
							age = today.getYear() - dob.getYear();
						}
//						CompanyProductMaster product = getCompanyProductMasterDropdown(companyId, productId);
//
//						HomePositionMaster savehomeData = new HomePositionMaster();
//						dozerMapper.map(homeData, savehomeData);
//						savehomeData.setCustomerName(null);
//						if (product.getMotorYn().equalsIgnoreCase("M")) {
//							List<EserviceMotorDetails> motorData=motorRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
//							if(motorData!=null && motorData.size()>0) {
//								EserviceMotorDetails savemotor=new EserviceMotorDetails();
//								dozerMapper.map(motorData,savemotor);
//							}
//
//						} else if (product.getMotorYn().equalsIgnoreCase("H")
//								&& req.getProductId().equalsIgnoreCase(travelProductId)) {
//
//						} else if (product.getMotorYn().equalsIgnoreCase("A")) {
//
//						} else if (product.getMotorYn().equalsIgnoreCase("L")) {
//
//						}
						PersonalInfo perData=personalInforepo.findByCustomerId(customerId);
						if(perData!=null) {
							PersonalInfo savePersonalInfo=new PersonalInfo();
							dozerMapper.map(perData, savePersonalInfo);
							savePersonalInfo.setPinCode(req.getPinCode());
							savePersonalInfo.setIdNumber(req.getIdNumber());
							savePersonalInfo.setUpdatedDate(new Date());
							savePersonalInfo.setUpdatedBy(req.getCreatedBy());
							savePersonalInfo.setAddress1(req.getAddress1());
							savePersonalInfo.setAddress2(req.getAddress2());
							savePersonalInfo.setAge(age);
							savePersonalInfo.setBranchCode(req.getBranchCode());
							savePersonalInfo.setBusinessType(req.getBusinessType());
							savePersonalInfo.setOtherOccupation(req.getOtherOccupation());
							if (StringUtils.isNotBlank(req.getBusinessType())) {
								String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
								savePersonalInfo.setBusinessTypeDesc(businessType);

							}


							savePersonalInfo.setRegionCode(req.getRegionCode());
							savePersonalInfo.setIsTaxExempted(req.getIsTaxExempted());
							savePersonalInfo.setCityCode(req.getCityCode());
							savePersonalInfo.setCityName(req.getCityName());
							savePersonalInfo.setClientName(req.getClientName());
							savePersonalInfo.setClientStatus(req.getClientStatus());
							savePersonalInfo.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
							savePersonalInfo.setCompanyId(req.getCompanyId());
							savePersonalInfo.setCreatedBy(req.getCreatedBy());
							savePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
			 				savePersonalInfo.setDobOrRegDate(dob);
			  				savePersonalInfo.setEmail1(req.getEmail1());
							savePersonalInfo.setEmail2(req.getEmail2());
							savePersonalInfo.setEmail3(req.getEmail3());
							savePersonalInfo.setEntryDate(new Date());
							savePersonalInfo.setFax(req.getFax());
							savePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
							savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
							savePersonalInfo.setGenderDesc(gender);
							savePersonalInfo.setTitleDesc(title);
							savePersonalInfo.setLanguageDesc(language);
							savePersonalInfo.setOccupationDesc(occupationDesc);


							// Induvidual / Corporate
							savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
							savePersonalInfo.setPolicyHolderTypeDesc(policyHolderType);

							// Possport or etc
							savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
							savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeId);
							savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
							savePersonalInfo.setIdTypeDesc(policyHolderTypeId);

							savePersonalInfo.setMobileCode1(req.getMobileCode1());
							savePersonalInfo.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
							savePersonalInfo.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
							savePersonalInfo.setMobileNo1(req.getMobileNo1());
							savePersonalInfo.setMobileNo2(req.getMobileNo2());
							savePersonalInfo.setMobileNo3(req.getMobileNo3());
							savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
							if (StringUtils.isNotBlank(req.getMobileCode1())) {
			 					ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode1(),req.getCompanyId());
								savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

							}
							if (StringUtils.isNotBlank(req.getMobileCode2())) {
								ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode2(),req.getCompanyId());
								savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

							}
							if (StringUtils.isNotBlank(req.getMobileCode3())) {
								ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode3(),req.getCompanyId());
								savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

							}
							if (StringUtils.isNotBlank(req.getWhatsappCode())) {
								ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
										req.getWhatsappCode(),req.getCompanyId());
								savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

							}
							savePersonalInfo.setRegionCode(req.getRegionCode());
							savePersonalInfo.setStateCode(req.getStateCode());
							savePersonalInfo.setStateName(req.getStateName());
							savePersonalInfo.setStatus(req.getStatus());
							savePersonalInfo.setNationality(req.getNationality());
							savePersonalInfo.setVrTinNo(req.getVrTinNo());
							savePersonalInfo.setVrnGst(req.getVrTinNo());
							personalInforepo.save(savePersonalInfo);
						}

						EserviceCustomerDetails escustDetails=repository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
						if(escustDetails!=null) {
							EserviceCustomerDetails saveData=new EserviceCustomerDetails();
							dozerMapper.map(escustDetails, saveData);
							saveData.setClientName(req.getClientName());
							saveData.setUpdatedDate(new Date());
							saveData.setUpdatedBy(req.getCreatedBy());
							saveData.setStatus(req.getStatus());
							saveData.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
							saveData.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
							saveData.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
							saveData.setBrokerBranchCode(req.getBrokerBranchCode());


							if(StringUtils.isNotBlank(req.getMobileCode1())){
								String mobileCode1 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode1());
							saveData.setMobileCodeDesc1(mobileCode1);

							}
					        if(StringUtils.isNotBlank(req.getMobileCode2())){
					        	String mobileCode2 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode2());
							saveData.setMobileCodeDesc2(mobileCode2);

					        }


					        if(StringUtils.isNotBlank(req.getMobileCode3())){
					        	String mobileCode3 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode3());
							saveData.setMobileCodeDesc3(mobileCode3);

					        }
					        if(StringUtils.isNotBlank(req.getWhatsappCode())){
					        	String whatsappCode = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getWhatsappCode());
							saveData.setWhatsappCodeDesc(whatsappCode);

					        }

							if (StringUtils.isNotBlank(req.getBusinessType())) {
								String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
								saveData.setBusinessTypeDesc(businessType);
							}

//
							saveData.setTitleDesc(title);
							saveData.setPreferredNotification(req.getPreferredNotification());
							saveData.setIsTaxExempted(req.getIsTaxExempted());
							saveData.setRegionCode(req.getRegionCode());
							saveData.setStatus(req.getStatus());
							saveData.setBusinessType(req.getBusinessType());
							saveData.setVrTinNo(req.getVrTinNo());
							saveData.setVrnGst(req.getVrTinNo());
							saveData.setMobileCode1(req.getMobileCode1());
							saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
							saveData.setGenderDesc(gender);
							saveData.setTitleDesc(title);
							saveData.setLanguageDesc(language);
							saveData.setOccupationDesc(occupationDesc);
							saveData.setOtherOccupation(req.getOtherOccupation());
							saveData.setPolicyHolderTypeDesc(policyHolderType);
							saveData.setPolicyHolderTypeIdDesc(policyHolderTypeId);
							saveData.setIdType(req.getPolicyHolderTypeid());
							saveData.setIdTypeDesc(policyHolderTypeId);
							saveData.setVrTinNo(req.getVrTinNo());
							saveData.setVrnGst(req.getVrTinNo());
							saveData.setAge(age);
							saveData.setMobileCode1(req.getMobileCode1());
							saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
							saveData.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
							saveData.setWhatsappCode(req.getWhatsappCode());
							saveData.setRegionCode(req.getStateCode());
							saveData.setStateCode(StringUtils.isBlank(req.getStateCode()) ?null :Integer.valueOf(req.getStateCode()));
							saveData.setStateName(req.getStateName());
							saveData.setCityCode(StringUtils.isBlank(req.getCityCode())?null :Integer.valueOf(req.getCityCode()));
							saveData.setCityName(req.getCityName());
							saveData.setRegionCode(req.getRegionCode());

							// Kenya Rating Fields
							saveData.setMaritalStatus(StringUtils.isBlank(req.getMaritalStatus()) ?"Single" : req.getMaritalStatus() );
							if (req.getLicenseIssuedDate()!=null ) {
								saveData.setLicenseIssuedDate(req.getLicenseIssuedDate());
								Date licenceIssued = req.getDobOrRegDate();
								Date today = new Date();
								int licenseDuration = today.getYear() - licenceIssued.getYear();
								saveData.setLicenseDuration(licenseDuration);

							} else {
								saveData.setLicenseIssuedDate(new Date());
								saveData.setLicenseDuration(20);
							}
							repository.save(saveData);
						}
						res.setSuccessId(req.getCustomerReferenceNo());
						res.setResponse("Updated");
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
			}
			return res;

		}
		public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
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


		@Override
		public CommonRes fetchPolicyData(String policyNumber) {

			CommonRes commonResponse = new CommonRes();

			if (StringUtils.isNotBlank(policyNumber)) {

				HomePositionMaster homeData = homePosistionRepo.findTop1ByPolicyNo(policyNumber);

				if (null != homeData &&  homeData.getProductId() != null && StringUtils.isNotBlank( homeData.getRequestReferenceNo()) )  {


					com.maan.eway.req.FactorRateDetailsGetReq req = new com.maan.eway.req.FactorRateDetailsGetReq();
					req.setRequestReferenceNo( homeData.getRequestReferenceNo());
					req.setProductId( homeData.getProductId().toString());

						Map<List<EservieMotorDetailsViewRes>, List<com.maan.eway.res.calc.Cover>> res = factorRateRequestDetailsServiceImpl.getFactorRateRequestDetails(req, "");

					if(res != null && !res.isEmpty() )
					{
						Entry<List<EservieMotorDetailsViewRes>, List<com.maan.eway.res.calc.Cover>> entry =res.entrySet().iterator().next();

						for (EservieMotorDetailsViewRes eservieMotorDetailsViewRes : entry.getKey())
								{

							if(null != eservieMotorDetailsViewRes &&  eservieMotorDetailsViewRes.getCoverList() == null) {

								continue;
							}else {

								List<com.maan.eway.res.calc.Cover> covers =	eservieMotorDetailsViewRes.getCoverList();

								if(covers == null) {

									continue;
								}else {


									for (com.maan.eway.res.calc.Cover cover : covers) {

										if(null != cover) {

											cover.setTaxes(null);
										}

									}

								}

							}

						}

					}

					commonResponse.setCommonResponse(res);

				return commonResponse;
			}

		} else {

			// bad request
		}

		 boolean isNeed = false;

  //  ------------------------------------Proper----------------------------------


			if(isNeed) {

			PolicyDataRes res = new PolicyDataRes();

			CommonRes commonRes = new CommonRes();

			try {

				if (StringUtils.isNotBlank(policyNumber)) {


				HomePositionMaster homeData = homePosistionRepo.findByPolicyNo(policyNumber);

				if(null != homeData) {


					if (StringUtils.isNotBlank(homeData.getQuoteNo())) {

						List<PaymentDetail> paymentDataList = paymentDetailsRepo.findByQuoteNo(homeData.getQuoteNo());

						List<PolicyCoverData> coverDataList = policyCoverDataRepo.findByQuoteNo(homeData.getQuoteNo());

						if (null != paymentDataList && !paymentDataList.isEmpty() && null != paymentDataList.get(0)) {

							PaymentDetail paymentData =  paymentDataList.get(0);

							String transactionDate = null;
							String inceptionDate = null;
							String expiryDate = null;

							try {

								SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");


								if(paymentData.getEntryDate() != null ) {

								  String s =	format.format(paymentData.getEntryDate());

								  transactionDate =  s;
								}

								if(homeData.getInceptionDate() != null ) {

								  String s = format.format(homeData.getInceptionDate());

								  inceptionDate = s;

								}

								if(homeData.getExpiryDate() != null ) {

									  String s = format.format(homeData.getExpiryDate());

									  expiryDate = s;

									}


							}catch (Exception e) {

								log.error("Exception Occurs When Format The Date *****  "  +  e.getMessage());
								e.printStackTrace();
						//		throw new DateParseException("Date Format Convert Exception ");
							}

							res.setTypeOfTransaction( StringUtils.isNotBlank(paymentData.getPaymentTypedesc() ) ? paymentData.getPaymentTypedesc() : "" );
							res.setTransactionDate(transactionDate);
                            res.setInceptionDate(inceptionDate);
						    res.setExpiryDate(expiryDate);
						    res.setGrossPremium(paymentData.getPremium() != null ? paymentData.getPremium() : null );
						    res.setProductId(homeData.getProductId() != null ? homeData.getProductId() : 0  );
		//				    res.setSectionId(homeData.getSectionId() != null  ? homeData.getSectionId() : 0 );;


						    List<Cover> coverList = new ArrayList<>();


						    if(null != coverDataList  && !coverDataList.isEmpty()) {

						    Set<Integer>  sectionIdList = coverDataList.stream().map(a ->a.getSectionId()).collect(Collectors.toSet() );

						    for (Integer sectionId : sectionIdList) {

							    Cover cover = new Cover();

							    cover.setSectionId(sectionId != null  ? sectionId.toString() : "" );

							    List<Covers> coversList = new ArrayList<>();

							    for (PolicyCoverData policyCoverData : coverDataList) {

						    		Covers covers = new Covers();


						    		covers.setCoverId(policyCoverData.getCoverId() != null ? policyCoverData.getCoverId().toString(): null   );
									covers.setPremium(policyCoverData.getPremiumAfterDiscountFc() != null ? policyCoverData.getPremiumAfterDiscountFc().toString(): null   );
									covers.setSumInsured(policyCoverData.getSumInsured() != null ? policyCoverData.getSumInsured().toPlainString(): null   );
						    	    covers.setIsSubCover( StringUtils.isNotBlank(policyCoverData.getSubCoverYn()) ? policyCoverData.getSubCoverYn().toString(): null   );

									coversList.add(covers);

								}

						    	cover.setCovers(coversList);
							}
						}


						    res.setCoversList(coverList);


						    Map<Integer, String> siMap = new HashMap<>();


						    if(StringUtils.isNotBlank(paymentData.getCompanyId()) && homeData.getProductId() != null ) {
						  List<CompanyProductMaster> companyProductDataList =  companyProductRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(paymentData.getCompanyId(), homeData.getProductId());


						  if(null != companyProductDataList && !companyProductDataList.isEmpty() && null != companyProductDataList.get(0) &&null  != companyProductDataList.get(0) ){

							  CompanyProductMaster  product =	  companyProductDataList.get(0);



							  if(product.getMotorYn().equalsIgnoreCase("H") &&  homeData.getProductId().equals(Integer.valueOf(4))) {

								  // travel

								  List<EserviceTravelDetails> travelList  =  eserviceTravelRepo.findByPolicyNo(policyNumber);

								 // No sum Insured

							 } else if(product.getMotorYn().equalsIgnoreCase("M") ) {
								// Motor Product Details
									List<EserviceMotorDetails> motorList =  eserviceMotorRepo.findByOriginalPolicyNo(policyNumber);

									 if(null != motorList && !motorList.isEmpty()) {

										  for (EserviceMotorDetails motor : motorList) {

											  if(null != motor && StringUtils.isNotBlank(motor.getSectionId()) && motor.getSumInsured() != null ) {
											  siMap.put( Integer.valueOf(motor.getSectionId()) , motor.getSumInsured() != null ?  motor.getSumInsured().toPlainString() : "" );
										}

										  }
									  }

							} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
								// Asset Product Details
								List<EserviceBuildingDetails> buildingList =  eserviceBuildingRepo.findByPolicyNo(policyNumber);

								if(null != buildingList && !buildingList.isEmpty()) {

									  for (EserviceBuildingDetails building : buildingList) {

										  if(null != building && StringUtils.isNotBlank(building.getSectionId())) {

											  BigDecimal sumInsured = null;
											  if( "1".equals( building.getSectionId()) ) {

												  sumInsured = building.getSumInsured();
											  }
											  else  if( "47".equals( building.getSectionId()) ) {

												  sumInsured = building.getSumInsured();
											  }

											  else if( "3".equals( building.getSectionId()) ) {

												  sumInsured = building.getSumInsured();
                                                  }
//
//											  else  if( "36".equals( building.getSectionId()) ) {
//
//                                                 humanrepo.
//
//	                                               sumInsured = building.get
//                                                }
//											  else if( "35".equals( building.getSectionId()) ) {
//
//                                            		  sumInsured = building.get
//                                            	 }
//

										  siMap.put( Integer.valueOf(building.getSectionId()) ,  sumInsured != null ? sumInsured.toPlainString() : "" );
									}

									  }
								  }

							} else {
								// Human Product Details
								List<EserviceCommonDetails> humanList =  eserviceCommonRepo.findByPolicyNo(policyNumber);


								if(null != humanList && !humanList.isEmpty()) {

									  for (EserviceCommonDetails human : humanList) {

											if (null != human && StringUtils.isNotBlank(human.getSectionId())
													&& human.getSumInsured() != null) {
												siMap.put(Integer.valueOf(human.getSectionId()),human.getSumInsured() != null ? human.getSumInsured().toPlainString() : "" );
											}

										}
									}
								}

							}

						}

					//	res.setSumInsuredWithSectionId(siMap);

					}

				}

			}

			res.setPolicyNo(policyNumber);

			commonRes.setMessage("success");
			commonRes.setIsError(false);
			commonRes.setCommonResponse(res);
			commonRes.setErroCode(0);


			return commonRes;

			} else {

					commonRes.setMessage("Failed-Check Request Data");
					commonRes.setIsError(true);
					commonRes.setCommonResponse(null);
					commonRes.setErroCode(0);

				}

				return commonRes;

			} catch (Exception e) {

				log.error("Exception occurs When Fetching The Policy Data Based On Policy Number  ****** "+ e.getMessage());
				e.printStackTrace();
				// throw new DataBindingException( "Check Data Fetching and Binding", e);

			}


			}
			return null;

		}

		public void deleteAllByCustomerReferenceNo(String customerReferenceNo) {
		    insuredRepository.deleteByCustomerReferenceNo(customerReferenceNo);
		}

		@Override
		@Transactional
		public SuccessRes updateCustomerDetails(EserviceCustomerSaveReq req) {
			SuccessRes res = new SuccessRes();
			if ("100046".equalsIgnoreCase(req.getCompanyId())) {
				res = phoenixEcustdetails.updateZambiaCustomerDetail(req);
			} else if ("100047".equalsIgnoreCase(req.getCompanyId())) {
				res = phoenixEcustdetails.updateZambiaCustomerDetail(req);
			} else if ("100048".equalsIgnoreCase(req.getCompanyId())) {
				res = phoenixEcustdetails.updateZambiaCustomerDetail(req);
			} else if ("100049".equalsIgnoreCase(req.getCompanyId())) {
				res = phoenixEcustdetails.updateZambiaCustomerDetail(req);
			} else if ("100050".equalsIgnoreCase(req.getCompanyId())) {
				res = phoenixEcustdetails.updateZambiaCustomerDetail(req);
			} else {
				res = tanzaniaEcustdetails.updateCustomerDetail(req);
			}
			return res;
		}

		@Override
		public ResponseEntity<CommonRes> getExistingCustomerDetails(EserviceCustomerSaveReq req,
				List<String> validationCodes) {
			List<Error> errorList = new ArrayList<>();
			List<String> conflictFields = new ArrayList<>();
			CommonRes data = new CommonRes();

			if (validationCodes.contains("ID_NUMBER_EXISTS") || validationCodes.contains("EMAIL_EXISTS")
					|| validationCodes.contains("MOBILE_EXISTS")) {

				List<EserviceCustomerDetails> existingList = repository.findCustomerDetails(req.getCompanyId(),
						req.getBranchCode(), req.getIdNumber(), req.getPolicyHolderTypeid(), req.getEmail1(),
						req.getMobileNo1());

				if (existingList.isEmpty()) {
					data.setIsError(true);
					data.setMessage("Validation failed but no existing customer details found");
					data.setErroCode(1511);
					return ResponseEntity.ok(data);
				}

				EserviceCustomerDetails existing = existingList.get(0);
				String existingCustomerRef = existing.getCustomerReferenceNo();
				String existingCustomerName = existing.getClientName();

				if (validationCodes.contains("ID_NUMBER_EXISTS")) {
					addError(errorList, conflictFields, "ID_NUMBER_EXISTS", "ID Number", existingCustomerRef,
							existingCustomerName);
				}
				if (validationCodes.contains("EMAIL_EXISTS")) {
					addError(errorList, conflictFields, "EMAIL_EXISTS", "Email", existingCustomerRef,
							existingCustomerName);
				}
				if (validationCodes.contains("MOBILE_EXISTS")) {
					addError(errorList, conflictFields, "MOBILE_EXISTS", "Mobile Number", existingCustomerRef,
							existingCustomerName);
				}

				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(errorList);
				data.setMessage("Customer already exists with " + String.join(", ", conflictFields) + " [Ref: "
						+ existingCustomerRef + "]");
				data.setErroCode(1511);

				return ResponseEntity.ok(data);
			}

			return ResponseEntity.ok(new CommonRes());
		}

		private void addError(List<Error> errorList, List<String> conflictFields, String code, String field,
				String existingRef, String existingName) {
			Error err = new Error();
			err.setCode(code);
			err.setMessage("Customer with this " + field + " already exists (Ref: " + existingRef + ", Name: "
					+ existingName + ")");
			errorList.add(err);
			conflictFields.add(field);
		}


		@Override
		public CommonRes saveMultipleLocations(List<EserviceCustomerLocation> locations) {

			 CommonRes response = new CommonRes();
				List<Error> errorList = new ArrayList<Error>();

		        if (locations == null || locations.isEmpty()) {
		            response.setCommonResponse(null);
		            response.setErroCode(400);
		            response.setIsError(true);
		            response.setErrorMessage(null);
		            response.setMessage("Location list is empty");
		            return response;
		        }

		        List<EserviceCustomerLocation> savedList = new ArrayList<>();

		        for (EserviceCustomerLocation location : locations) {
		            if (location.getLocationName() == null ||
		                location.getActive() == null ||
		                location.getCompanyId() == null ||
		                location.getCustomerReferenceNo() == null) {
		                response.setCommonResponse(null);
		                response.setErroCode(404);
		                response.setErrorMessage(null);
		                response.setMessage("Null values not allowed");
		                return response;
		            }

		            Optional<EserviceCustomerLocation> existingLocationOpt =
		                    locationRepo.findByLocationName(location.getLocationName());

		            if (existingLocationOpt.isPresent()) {
		                EserviceCustomerLocation existingLocation = existingLocationOpt.get();

		                if (existingLocation.getCustomerReferenceNo()
		                        .equals(location.getCustomerReferenceNo())) {

		                    existingLocation.setAddress(location.getAddress());
		                    existingLocation.setActive(location.getActive());
		                    existingLocation.setCompanyId(location.getCompanyId());
		                    savedList.add(locationRepo.save(existingLocation));

		                } else {

		                	errorList.add(new Error("Location name '" , location.getLocationName() ,
		                            "' already exists for another customer"));
		                    response.setCommonResponse(errorList);
		                    response.setErroCode(409);
		                    response.setIsError(true);
		                    response.setErrorMessage(errorList);
		                    response.setMessage(null);
		                    return response;
		                }

		            } else {
		                savedList.add(locationRepo.save(location));
		            }
		        }

		        List<LocationRes> locationResponses = savedList.stream().map(loc -> {
		            LocationRes dto = new LocationRes();
		            dto.setLocationName(loc.getLocationName());
		            dto.setAddress(loc.getAddress());
		            dto.setActive(loc.getActive());
		            dto.setCompanyId(loc.getCompanyId());
		            dto.setCustomerReferenceNo(loc.getCustomerReferenceNo());
		            return dto;
		        }).toList();

		        response.setCommonResponse(locationResponses);
		        response.setErroCode(200);
		        response.setErrorMessage(null);
		        response.setMessage("Locations saved/updated successfully");
		        return response;
		}


		@Override
		public List<EserviceCustomerLocation> getLocationsByCustomerRefNo(String customerRefNo) {
			List<EserviceCustomerLocation> locations = locationRepo.findByCustomerReferenceNo(customerRefNo);
			return locations;
		}

		@Override
//		public CommonRes fetchCustomerQuoteAndPolicyDetails(CustomerDetailsReq customerDetailsReq) {
//			CommonRes response = new CommonRes();
//			if (customerDetailsReq.getCompanyId() == null && customerDetailsReq.getRequestReferenceNo() == null) {
//				response.setCommonResponse(null);
//				response.setMessage(null);
//				response.setIsError(true);
//				response.setErroCode(500);
//				return response;
//			}
//			String requestReferenceNo = customerDetailsReq.getRequestReferenceNo();
//			List<PersonalInfo> personalInfoList = personalInforepo.findByCustomerReferenceNo(requestReferenceNo);
//			if (personalInfoList.isEmpty()) {
//				response.setCommonResponse(null);
//				response.setMessage("requestNo has no personal Informations");
//				response.setIsError(true);
//				response.setErroCode(500);
//				return response;
//			}
//			List<String> customerIds = personalInfoList.stream().map(PersonalInfo::getCustomerId)
//					.collect(Collectors.toList());
//
//			List<HomePositionMaster> homePositionMasters = homePosistionRepo.findByCustomerIdIn(customerIds);
//
//			List<CustomerDetailsResponse> basedOnQuoteNo = new ArrayList<>();
//			List<CustomerDetailsResponse> basedOnPolicyNo = new ArrayList<>();
//			List<ClaimDetailResponse> claimDetailResponses=new ArrayList<>();
//
//			CustomerQuoteAndPolicyDetail result = new CustomerQuoteAndPolicyDetail();
//
//			List<String> policyNos = homePositionMasters.stream().map(HomePositionMaster::getPolicyNo)
//					.collect(Collectors.toList());
//
//			List<ClaimIntimation> claimIntimations=claimIntimationRepository.findByPolicyNoIn(policyNos);
//
//
//
//			homePositionMasters.stream().map(master -> {
//				CustomerDetailsResponse dto = new CustomerDetailsResponse();
//
//				List<PolicyCoverData> policyCoverData=null;
//				if(master.getPolicyNo()!=null){
//					policyCoverData=policyCoverDataRepository.findByPolicyNoAndCoverageTypeNot(master.getPolicyNo(),"T");
//				}
//				else{
//					policyCoverData=policyCoverDataRepository.findByQuoteNoAndCoverageTypeNot(master.getQuoteNo(),"T");
//				}
//				BigDecimal overAllSumInsured = policyCoverData.stream()
//						.map(data -> data.getSumInsured() != null
//								? data.getSumInsured()
//								: BigDecimal.ZERO)
//						.reduce(BigDecimal.ZERO, BigDecimal::add);
//
//				dto.setSumInsured(overAllSumInsured);
//				dto.setQuoteNo(master.getQuoteNo());
//				dto.setRequestReferenceNo(master.getRequestReferenceNo());
//				dto.setCustomerName(master.getCustomerName());
//				dto.setPolicyNo(master.getPolicyNo());
//				dto.setEffectiveDate(master.getEffectiveDate());
//				dto.setExpiryDate(master.getExpiryDate());
//				dto.setCurrency(master.getCurrency());
//				dto.setPremiumLc(master.getPremiumLc());
//				if (dto.getPolicyNo() != null) {
//					basedOnPolicyNo.add(dto);
//				} else {
//					basedOnQuoteNo.add(dto);
//				}
//				return dto;
//			}).collect(Collectors.toList());
//
//			claimIntimations.stream().map(claim -> {
//
//				ClaimDetailResponse dto = new ClaimDetailResponse();
//
//				List<ClaimReserveDetails> reserveDetails=claimReserveDetailsRepository.findByClaimNoAndPolicyNo(claim.getClaimNo(), claim.getPolicyNo());
//
//						dto.setClaimReferenceNo(
//						claim.getClaimRefNo() != null
//								? claim.getClaimRefNo()
//								: null);
//
//				dto.setClaimNo(claim.getClaimNo() != null
//						? claim.getClaimNo()
//						: null);
//
//				dto.setPolicyNo(claim.getPolicyNo()!=null?claim.getPolicyNo():null);
//
//				dto.setIntimationDate(
//						claim.getClaimIntimationDate() != null
//								? claim.getClaimIntimationDate()
//								: null);
//
//				dto.setLossDate(
//						claim.getLossDate() != null
//								? claim.getLossDate()
//								: null);
//
//				dto.setCurrency(claim.getCurrencyCode()!=null?claim.getCurrencyCode():null);
//
//
//				// Total Reserve Amount
//				BigDecimal totalReserveAmount = reserveDetails.stream()
//						.map(r -> r.getReserveAmount() != null
//								? new BigDecimal(r.getReserveAmount())
//								: BigDecimal.ZERO)
//						.reduce(BigDecimal.ZERO, BigDecimal::add);
//// Total Settlement Amount
//				BigDecimal totalSettlementAmount = reserveDetails.stream()
//						.map(r -> r.getPaymentAmount() != null
//								? r.getPaymentAmount()
//								: BigDecimal.ZERO)
//						.reduce(BigDecimal.ZERO, BigDecimal::add);
//
//				BigDecimal outstandingAmount =
//						totalReserveAmount.subtract(totalSettlementAmount);
//
//				dto.setReserveAmount(totalReserveAmount);
//
//				dto.setSettlementAmount(totalSettlementAmount);
//
//				dto.setOutstandingAmount(outstandingAmount);
//
//
//				claimDetailResponses.add(dto);
//
//				return dto;
//
//			}).collect(Collectors.toList());
//			result.setPolicyNos(basedOnPolicyNo);
//			result.setQuoteNos(basedOnQuoteNo);
//			result.setClaimNos(claimDetailResponses);
//			response.setCommonResponse(result);
//			response.setMessage("Success");
//			response.setIsError(false);
//			response.setErroCode(200);
//
//			return response;
//		}
		public CommonRes fetchCustomerQuoteAndPolicyDetails(CustomerDetailsReq customerDetailsReq) {
			CommonRes response = new CommonRes();
			if (customerDetailsReq.getCompanyId() == null && customerDetailsReq.getRequestReferenceNo() == null) {
				response.setIsError(true); response.setErroCode(500); return response;
			}

			List<PersonalInfo> personalInfoList = personalInforepo
					.findByCustomerReferenceNo(customerDetailsReq.getRequestReferenceNo());
			if (personalInfoList.isEmpty()) {
				response.setMessage("requestNo has no personal Informations");
				response.setIsError(true); response.setErroCode(500); return response;
			}

			List<String> customerIds = personalInfoList.stream()
					.map(PersonalInfo::getCustomerId).collect(Collectors.toList());

			List<HomePositionMaster> homePositionMasters = homePosistionRepo.findByCustomerIdIn(customerIds);

			// ── Separate into policyNos and quoteNos upfront ──────────────────────
			List<String> policyNos = homePositionMasters.stream()
					.map(HomePositionMaster::getPolicyNo)
					.filter(Objects::nonNull).collect(Collectors.toList());

			List<String> quoteNos = homePositionMasters.stream()
					.filter(m -> m.getPolicyNo() == null)
					.map(HomePositionMaster::getQuoteNo)
					.filter(Objects::nonNull).collect(Collectors.toList());

			// ── Single bulk fetch for cover data, then group by key ───────────────
			Map<String, List<PolicyCoverData>> coverByPolicy = policyCoverDataRepository
					.findByPolicyNoInAndCoverageTypeNot(policyNos, "T")
					.stream().collect(Collectors.groupingBy(PolicyCoverData::getPolicyNo));

			Map<String, List<PolicyCoverData>> coverByQuote = policyCoverDataRepository
					.findByQuoteNoInAndCoverageTypeNot(quoteNos, "T")
					.stream().collect(Collectors.groupingBy(PolicyCoverData::getQuoteNo));

			// ── Single bulk fetch for claims, then group by claimNo ───────────────
			List<ClaimIntimation> claimIntimations = claimIntimationRepository.findByPolicyNoIn(policyNos);

			List<String> claimNos = claimIntimations.stream()
					.map(ClaimIntimation::getClaimNo).collect(Collectors.toList());

			// Single bulk fetch for reserve details, group by claimNo
			Map<String, List<ClaimReserveDetails>> reserveByClaimNo = claimReserveDetailsRepository
					.findByClaimNoIn(claimNos)
					.stream().collect(Collectors.groupingBy(ClaimReserveDetails::getClaimNo));

			// ── Build quote/policy DTOs (no DB calls inside loop now) ─────────────
			List<CustomerDetailsResponse> basedOnQuoteNo = new ArrayList<>();
			List<CustomerDetailsResponse> basedOnPolicyNo = new ArrayList<>();

			for (HomePositionMaster master : homePositionMasters) {
				List<PolicyCoverData> coverData = master.getPolicyNo() != null
						? coverByPolicy.getOrDefault(master.getPolicyNo(), Collections.emptyList())
						: coverByQuote.getOrDefault(master.getQuoteNo(), Collections.emptyList());

				BigDecimal sumInsured = coverData.stream()
						.map(d -> d.getSumInsured() != null ? d.getSumInsured() : BigDecimal.ZERO)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				CustomerDetailsResponse dto = new CustomerDetailsResponse();
				dto.setSumInsured(sumInsured);
				dto.setQuoteNo(master.getQuoteNo());
				dto.setRequestReferenceNo(master.getRequestReferenceNo());
				dto.setCustomerName(master.getCustomerName());
				dto.setPolicyNo(master.getPolicyNo());
				dto.setEffectiveDate(master.getEffectiveDate());
				dto.setExpiryDate(master.getExpiryDate());
				dto.setCurrency(master.getCurrency());
				dto.setPremiumLc(master.getPremiumLc());

				if (master.getPolicyNo() != null) basedOnPolicyNo.add(dto);
				else basedOnQuoteNo.add(dto);
			}

			// ── Build claim DTOs (no DB calls inside loop now) ────────────────────
			List<ClaimDetailResponse> claimDetailResponses = claimIntimations.stream().map(claim -> {
				List<ClaimReserveDetails> reserves = reserveByClaimNo
						.getOrDefault(claim.getClaimNo(), Collections.emptyList());

				BigDecimal totalReserve = reserves.stream()
						.map(r -> r.getReserveAmount() != null ? new BigDecimal(r.getReserveAmount()) : BigDecimal.ZERO)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				BigDecimal totalSettlement = reserves.stream()
						.map(r -> r.getPaymentAmount() != null ? r.getPaymentAmount() : BigDecimal.ZERO)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				ClaimDetailResponse dto = new ClaimDetailResponse();
				dto.setClaimReferenceNo(claim.getClaimRefNo());
				dto.setClaimNo(claim.getClaimNo());
				dto.setPolicyNo(claim.getPolicyNo());
				dto.setIntimationDate(claim.getClaimIntimationDate());
				dto.setLossDate(claim.getLossDate());
				dto.setCurrency(claim.getCurrencyCode());
				dto.setReserveAmount(totalReserve);
				dto.setSettlementAmount(totalSettlement);
				dto.setOutstandingAmount(totalReserve.subtract(totalSettlement));
				return dto;
			}).collect(Collectors.toList());

			CustomerQuoteAndPolicyDetail result = new CustomerQuoteAndPolicyDetail();
			result.setPolicyNos(basedOnPolicyNo);
			result.setQuoteNos(basedOnQuoteNo);
			result.setClaimNos(claimDetailResponses);

			response.setCommonResponse(result);
			response.setMessage("Success");
			response.setIsError(false);
			response.setErroCode(200);
			return response;
		}
			
		@Override
		public CommonRes deleteLocation(Long locationId, String customerReferenceNo) {
			CommonRes response = new CommonRes();
			List<Error> errorList = new ArrayList<Error>();

			Optional<EserviceCustomerLocation> existingLocationOpt = locationRepo.findById(locationId);

			if (existingLocationOpt.isEmpty()) {
				response.setCommonResponse(null);
				response.setErroCode(404);
				response.setIsError(true);
				errorList.add(new Error("Errors", "Location not found", ""));
				response.setErrorMessage(errorList);
				response.setMessage(null);
				return response;
			}

			EserviceCustomerLocation existingLocation = existingLocationOpt.get();

			if (!existingLocation.getCustomerReferenceNo().equals(customerReferenceNo)) {
				response.setCommonResponse(null);
				response.setErroCode(403);
				response.setIsError(true);
				errorList.add(new Error("Errors", "You are not allowed to delete this location", ""));
				response.setErrorMessage(errorList);
				response.setMessage(null);
				return response;
			}

			locationRepo.delete(existingLocation);

			response.setCommonResponse(null);
			response.setErroCode(200);
			response.setIsError(false);
			response.setErrorMessage(null);
			response.setMessage("Location deleted successfully");
			return response;
		}

		@Override
		public CustomerCreationReq financeIntegrationCustomerCreation(CustomerCreationReq req) {
			try {
				String quoteNo = req.getQuoteNo();
				HomePositionMaster home = homePosistionRepo.findByQuoteNo(quoteNo);
				if (home == null)
					return null;

				String customerId = home.getCustomerId();
				PersonalInfo personal = personalInforepo.findByCustomerId(customerId);
				if (personal == null)
					return null;

				String customerReferenceNo = personal.getCustomerReferenceNo();
				EserviceCustomerDetails customer = repository.findByCustomerReferenceNo(customerReferenceNo);
				if (customer == null)
					return null;

				if (customer.getPolCustCode() != null && !customer.getPolCustCode().isEmpty()) {
					CustomerCreationReq reqCust = new CustomerCreationReq();
					reqCust.setPolCustCode(customer.getPolCustCode());
					reqCust.setPolicyNo(home.getPolicyNo());
					reqCust.setCompanyId(home.getCompanyId());
					reqCust.setCustomerReferenceNo(customerReferenceNo);
					return reqCust;
				}

			//	String latestSeq = customerSequenceRepository.findLatestSequence();
//				String latestSeq = customerSequenceRepository.findLatestSequenceByCompanyId(home.getCompanyId());
//				String nextSeq = (latestSeq == null || latestSeq.isEmpty() || latestSeq.equals("000")) ? "001"
//						: String.format("%03d", Integer.parseInt(latestSeq) + 1);
				
				String latestSeq = customerSequenceRepository
				        .findLatestSequenceByCompanyId(home.getCompanyId());

				String nextSeq = (latestSeq == null || latestSeq.isEmpty() || latestSeq.equals("000"))
				        ? "001"
				        : String.format("%03d", Integer.parseInt(latestSeq) + 1);

				customerSequenceRepository.deleteByCompanyId(home.getCompanyId());

				CustomerSequence customerSequence = new CustomerSequence();
				customerSequence.setCompanyid(home.getCompanyId());
				customerSequence.setSequenceNo(nextSeq);

				customerSequenceRepository.save(customerSequence);

				String custCatgeoryCode = "35";
				/**Integer productId = home.getProductId();
				String sourceType = home.getSourceType();
				if (sourceType != null) {
					switch (sourceType.toLowerCase()) {
					case "broker":
						custCatgeoryCode = "20";
						break;
					case "agent":
						custCatgeoryCode = "25";
						break;
					case "direct":
					case "b2c":
						custCatgeoryCode = "35";
						break;
					default:
						custCatgeoryCode = "35";
					}
				} else {
					custCatgeoryCode = "35";
				}**/
					/**if (productId != null && productId.equals(5)) {
						EserviceMotorDetails motorDetail = eserviceMotorRepo.findByPolicyNo(home.getPolicyNo());
						if (motorDetail != null) {
							String sourceType = motorDetail.getSourceType();
							if (sourceType != null) {
								switch (sourceType.toLowerCase()) {
								case "broker":
									custCatgeoryCode = "20";
									break;
								case "agent":
									custCatgeoryCode = "25";
									break;
								case "direct":
								case "b2c":
									custCatgeoryCode = "35";
									break;
								default:
									custCatgeoryCode = "35";
								}
							} else {
								custCatgeoryCode = "35";
							}
						} else {
							custCatgeoryCode = "35";
						}
					} else {
						custCatgeoryCode = "35";
					}**/
				

				String financeCustomerCode = custCatgeoryCode + "P" + nextSeq;
				
				  customer.setPolCustCode(financeCustomerCode);
				 customer.setCcCustCatgCode(custCatgeoryCode);
				 repository.save(customer);

				ClaimCustomerSyncReq syncReq=new ClaimCustomerSyncReq();
				syncReq.setCustomerReferenceNo(customer.getCustomerReferenceNo());
				claimCustomerDetails.syncCustomerToClaim(syncReq);


					/*
					 * home.setPolCustCode(financeCustomerCode); homePosistionRepo.save(home);
					 */
				Optional<WnmCustCategory> custMaster = wnmCustCategoryRepository.findByCcCustCatgCode(custCatgeoryCode);
				custMaster.ifPresent(c -> customer.setCcCustType(c.getCcCustCatgCodeDesc()));
				CustomerCreationReq reqCust = new CustomerCreationReq();
				reqCust.setPolCustCode(financeCustomerCode);
				reqCust.setCompanyId(home.getCompanyId());
				reqCust.setPolicyNo(home.getPolicyNo());
				reqCust.setCustomerReferenceNo(customerReferenceNo);
				return reqCust;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void financeIntegrationCustomerDetailUpdate(CustomerCreationReq req) {
			if ("100046".equalsIgnoreCase(req.getCompanyId()) || "100047".equalsIgnoreCase(req.getCompanyId())
					|| "100048".equalsIgnoreCase(req.getCompanyId()) || "100049".equalsIgnoreCase(req.getCompanyId())
					|| "100050".equalsIgnoreCase(req.getCompanyId())) {

				String successId = req.getCustomerReferenceNo();
				EserviceCustomerDetails customer = repository.findByCustomerReferenceNo(successId);

				Map<String, String> authRequest = new HashMap<>();
				authRequest.put("username", "admin");
				authRequest.put("password", "admin");

				HttpHeaders authHeaders = new HttpHeaders();
				authHeaders.setContentType(MediaType.APPLICATION_JSON);

				HttpEntity<Map<String, String>> authEntity = new HttpEntity<>(authRequest, authHeaders);
				RestTemplate restTemplate = new RestTemplate();

				ResponseEntity<String> authResponse = restTemplate.postForEntity(financeTokenApi, authEntity,
						String.class);

				String token = authResponse.getBody();

				// String accountingUrl = updateCustomerDetail;

				Map<String, Object> accountingRequest = new HashMap<>();

				accountingRequest.put("CustCode", customer.getPolCustCode());
				accountingRequest.put("CustomerName", customer.getClientName());

				HttpHeaders accountCreation = new HttpHeaders();
				accountCreation.setContentType(MediaType.APPLICATION_JSON);
				accountCreation.setBearerAuth(token);

				HttpEntity<Map<String, Object>> customerCreationEntity = new HttpEntity<>(accountingRequest,
						accountCreation);
				ResponseEntity<String> accountingResponse = restTemplate.postForEntity(updateCustomerDetail,
						customerCreationEntity, String.class);
				System.out.println("Customer Update API Response: " + accountingResponse.getBody());

			}

		}

		@Override
		public void customerCreationFinanceThirdPartyApiCall(String polCustCode, String customerReferenceNo,
				String companyId, String policyNo) {
			try {
				System.out.println("cutomerCreationFinanceThirdPartyApiCall ------> " + polCustCode + " "
						+ customerReferenceNo + " " + companyId);
				Map<String, String> authRequest = new HashMap<>();
				authRequest.put("username", "admin");
				authRequest.put("password", "admin");

				HttpHeaders authHeaders = new HttpHeaders();
				authHeaders.setContentType(MediaType.APPLICATION_JSON);

				HttpEntity<Map<String, String>> authEntity = new HttpEntity<>(authRequest, authHeaders);
				RestTemplate restTemplate = new RestTemplate();

				ResponseEntity<String> authResponse = restTemplate.postForEntity(financeTokenApi, authEntity,
						String.class);

				String token = authResponse.getBody();

				Map<String, Object> accountingRequest = new HashMap<>();

				accountingRequest.put("sysId", polCustCode);
				accountingRequest.put("InsuranceId", companyId);
				accountingRequest.put("CustomerReferenceNo", customerReferenceNo);
				accountingRequest.put("PolicyNo", policyNo);
				HttpHeaders accountCreation = new HttpHeaders();
				accountCreation.setContentType(MediaType.APPLICATION_JSON);
				accountCreation.setBearerAuth(token);

				HttpEntity<Map<String, Object>> customerCreationEntity = new HttpEntity<>(accountingRequest,
						accountCreation);
				System.out.println(
						"PAyload: " + "url " + finaceCustomerCreation + " -->payload" + customerCreationEntity);
				ResponseEntity<String> accountingResponse = restTemplate.postForEntity(finaceCustomerCreation,
						customerCreationEntity, String.class);
				System.out.println("Customer Creation API Response: " + accountingResponse.getBody());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public CustomerCreationReq financeCustomerCheckExist(CustomerCreationReq req) {
			try {
				boolean status = wntCustDtlRepo.findByCodeAndCompanyId(req.getPolCustCode(),req.getCompanyId()).map(WntCustDtl::getCustFinIntgStatus)
						.map(s -> "Success".equalsIgnoreCase(s)).orElse(false);

				req.setThirdpartyStatus(status);

			} catch (Exception e) {
				System.err.println("Error in financeCustomerCheckExist: " + e.getMessage());
				req.setThirdpartyStatus(false);
			}

			return req;
		}

	}