package com.maan.eway.update;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorVehicleInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.RegionMaster;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.req.UpdateCustomerDetailsReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.service.impl.EserviceCustomerDetailsServiceImpl;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorVehicleInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.RegionMasterRepository;
import com.maan.eway.repository.StateMasterRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
@Transactional
public class UpdateCustomerServiceImpl implements UpdateCustomerService {

	private Logger log = LogManager.getLogger(UpdateCustomerServiceImpl.class);

	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService;

	@Autowired
	private EserviceCustomerDetailsRepository repository;

	@Autowired
	private RegionMasterRepository regionMasterRepo;

	@Autowired
	private StateMasterRepository stateMasterRepo;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;

	@Autowired
	private EserviceCustomerDetailsServiceImpl eCustDetailsServiceImpl;

	@Autowired
	private ListItemValueRepository listRepo;

	@Autowired
	private PersonalInfoRepository personalInforepo;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepo;
	
	@Autowired
	private MotorVehicleInfoRepository motorVehicleInfoRepo;

	public List<String> validateCustomerDetails(EserviceCustomerSaveReq req) {
		List<String> errorList = new ArrayList<String>();
		try {
			if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {

				if (StringUtils.isBlank(req.getTitle())) {
					errorList.add("1047");
				}
				if (StringUtils.isBlank(req.getClientName())) {
					errorList.add("1001");
				} else if (req.getClientName().length() > 250) {
					errorList.add("1002");
				} else if (StringUtils.isNotBlank(req.getClientName()) && !req.getClientName().matches("[a-zA-Z.&() ]+")
						&& !req.getClientName().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")) {
					errorList.add("1003");
				}
				if ("1".equalsIgnoreCase(req.getPolicyHolderType())) {
					if (StringUtils.isBlank(req.getGender())) {
						errorList.add("1087");
					}
				}
				if (StringUtils.isBlank(req.getOccupation())) {
					errorList.add("1022");
				} else if (req.getOccupation().equalsIgnoreCase("99999")) {
					if (StringUtils.isBlank(req.getOtherOccupation())) {
						errorList.add("1023");
					} else if (req.getOtherOccupation().length() > 100) {
						errorList.add("1024");
					} else if (!req.getOtherOccupation().matches("[a-zA-Z\\s]+")) {
						errorList.add("1025");
					}
				}
				if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
					if (StringUtils.isBlank(req.getEmail1())) {
						errorList.add("1440");
					} else if (StringUtils.isNotBlank(req.getEmail1())) {
						if (req.getEmail1().length() > 100) {
							errorList.add("1032");
						} else if (StringUtils.isNotBlank(req.getEmail1())) {
							boolean b = isValidMail(req.getEmail1());
							if (b == false && (!req.getEmail1().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")
									|| !req.getEmail1().matches("^[.@]+$"))) {
								errorList.add("1033");
							}
						}
					}
				}
				if (StringUtils.isBlank(req.getMobileCode1())) {
					errorList.add("1062");
				}
				if (StringUtils.isBlank(req.getMobileNo1())) {
					errorList.add("1026");
				} else if (req.getMobileNo1().length() > 10 || req.getMobileNo1().length() < 8) {
					errorList.add("1027");
				} else if (!req.getMobileNo1().matches("[0-9]+")) {
					errorList.add("1028");
				} else if (req.getMobileNo1().matches("[0-9]+") && Double.valueOf(req.getMobileNo1()) <= 0) {
					errorList.add("1029");
				}
				// if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
				if (StringUtils.isBlank(req.getIdType())) {
					errorList.add("1011");
				}
				if (StringUtils.isBlank(req.getPolicyHolderTypeid())) {
					errorList.add("1012");
				}

				if (StringUtils.isBlank(req.getIdNumber())) {
					errorList.add("1013");
				} else if (req.getIdNumber().length() > 100) {
					errorList.add("1014");
				} else if (req.getIdNumber().matches("[0-9]+") && Double.valueOf(req.getIdNumber()) <= 0) {
					errorList.add("1015");
				} else if (!req.getIdNumber().matches("[a-zA-Z0-9-]+")) {
					errorList.add("1015");
				}

				// }
				if (StringUtils.isBlank(req.getPreferredNotification())) {
					errorList.add("1049");
				}
				if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
					if (StringUtils.isBlank(req.getVrTinNo())) {
						errorList.add("1051");
					} else if (req.getVrTinNo().length() > 20) {
						errorList.add("1052");
					}
					if (StringUtils.isBlank(req.getStreet())) {
						errorList.add("3310");
					} else if (req.getStreet().length() > 100) {
						errorList.add("3311");
					}
					if (StringUtils.isBlank(req.getCountry())) {
						errorList.add("1048");
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

				if (StringUtils.isBlank(req.getClientStatus())) {
					errorList.add("1010");
				}
				if (StringUtils.isNotBlank(req.getPinCode())) {
					if (req.getPinCode().length() > 20) {
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

				if (StringUtils.isNotBlank(req.getMobileNo3())
						&& (req.getMobileNo3().length() > 10 || req.getMobileNo3().length() < 10)) {
					errorList.add("1030");
				} else if (StringUtils.isNotBlank(req.getMobileNo3()) && !req.getMobileNo3().matches("\\d+")) {
					errorList.add("1031");
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

				if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail2())
						&& req.getEmail1().equalsIgnoreCase(req.getEmail2())) {
					errorList.add("1039");
				}
				if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail1().equalsIgnoreCase(req.getEmail3())) {
					errorList.add("1040");
				}
				if (StringUtils.isNotBlank(req.getEmail2()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail2().equalsIgnoreCase(req.getEmail3())) {
					errorList.add("1041");
				}

				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo2())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo2())) {
					errorList.add("1042");
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add("1042");
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo2()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo2().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add("1043");
				}

				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo2())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo2())) {
					errorList.add("1044");
				}
				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add("1045");
				}
				if (StringUtils.isNotBlank(req.getMobileNo2()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo2().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add("1046");
				}

				if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
						if (StringUtils.isBlank(req.getBusinessType())) {
							// errorList.add("1050");
						}
					}
				}

				if (StringUtils.isBlank(req.getStatus())) {
					errorList.add("1058");
				} else if (req.getStatus().length() > 1) {
					errorList.add("1059");
				} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())
						|| "P".equals(req.getStatus()))) {
					errorList.add("1060");
				}
				if (StringUtils.isBlank(req.getStateCode())) {
					errorList.add("1061");
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
						// && (StringUtils.isNotBlank(req.getAddress2()))
						&& (StringUtils.isNotBlank(req.getBranchCode()))
						// && (StringUtils.isNotBlank(req.getBusinessType()))
						&& (StringUtils.isNotBlank(req.getCityCode())) && (StringUtils.isNotBlank(req.getCityName()))
						&& (StringUtils.isNotBlank(req.getClientName()))
						&& (StringUtils.isNotBlank(req.getClientStatus()))
						&& (StringUtils.isNotBlank(req.getCompanyId())) && (StringUtils.isNotBlank(req.getCreatedBy()))
						// && (StringUtils.isNotBlank(req.getCustomerReferenceNo()))
						&& (StringUtils.isNotBlank(req.getEmail1()))
						// && (StringUtils.isNotBlank(req.getEmail2()))
						// && (StringUtils.isNotBlank(req.getEmail3())) &&
						// (StringUtils.isNotBlank(req.getFax()))
						&& (StringUtils.isNotBlank(req.getGender())) && (StringUtils.isNotBlank(req.getIdNumber()))
						&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
						&& (StringUtils.isNotBlank(req.getLanguage()))
						// && (StringUtils.isNotBlank(req.getLanguageDesc()))
						&& (StringUtils.isNotBlank(req.getMobileNo1()))
						// && (StringUtils.isNotBlank(req.getMobileNo2()))
						// && (StringUtils.isNotBlank(req.getMobileNo3()))
						&& (StringUtils.isNotBlank(req.getNationality()))
						&& (StringUtils.isNotBlank(req.getOccupation()))
						&& (StringUtils.isNotBlank(req.getPlaceOfBirth()))
						&& (StringUtils.isNotBlank(req.getPolicyHolderType()))
						&& (StringUtils.isNotBlank(req.getPolicyHolderTypeid()))
						&& (StringUtils.isNotBlank(req.getProductId())) && (StringUtils.isNotBlank(req.getRegionCode()))
						&& (StringUtils.isNotBlank(req.getStateCode())) && (StringUtils.isNotBlank(req.getStateName()))
						&& (StringUtils.isNotBlank(req.getStatus()))
						// && (StringUtils.isNotBlank(req.getStreet()))
						// && (StringUtils.isNotBlank(req.getTaxExemptedId()))
						// && (StringUtils.isNotBlank(req.getTelephoneNo1()))
						// && (StringUtils.isNotBlank(req.getTelephoneNo2()))
						// && (StringUtils.isNotBlank(req.getTelephoneNo3())) &&
						// (StringUtils.isNotBlank(req.getTitle()))
						&& (req.getDobOrRegDate() != null) && (StringUtils.isNotBlank(req.getIsTaxExempted()))
						// && (StringUtils.isNotBlank(req.getTaxExemptedId()))
						&& (StringUtils.isNotBlank(req.getPreferredNotification()))
				// && (req.getAppointmentDate()!=null)

				) {

					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);
					// Find all
					Root<EserviceCustomerDetails> b = query.from(EserviceCustomerDetails.class);
					// Select
					query.select(b);
					// Where

					Predicate n1 = (cb.like(cb.lower(b.get("address1")), req.getAddress1().toLowerCase()));
					// Predicate n2 = (cb.like(cb.lower(b.get("address2")),
					// req.getAddress2().toLowerCase()));
					Predicate n3 = (cb.like(cb.lower(b.get("branchCode")), req.getBranchCode().toLowerCase()));
					// Predicate n4 = (cb.like(cb.lower(b.get("businessType")),
					// req.getBusinessType().toLowerCase()));
					// Predicate n5 = (cb.like(cb.lower(b.get("cityCode")),
					// req.getCityCode().toLowerCase()));
					Predicate n5 = (cb.equal(b.get("cityCode"),
							null != req.getCityCode() && req.getCityCode().matches("[0-9]+")
									? Integer.valueOf(req.getCityCode())
									: 0));
					Predicate n6 = (cb.like(cb.lower(b.get("cityName")), req.getCityName().toLowerCase()));
					Predicate n7 = (cb.like(cb.lower(b.get("clientName")), req.getClientName().toLowerCase()));
					Predicate n8 = (cb.like(cb.lower(b.get("clientStatus")), req.getClientStatus().toLowerCase()));
					Predicate n9 = (cb.like(cb.lower(b.get("companyId")), req.getCompanyId().toLowerCase()));
					Predicate n10 = (cb.like(cb.lower(b.get("createdBy")), req.getCreatedBy().toLowerCase()));
					// Predicate n11 =
					// (cb.like(cb.lower(b.get("customerReferenceNo")),req.getCustomerReferenceNo().toLowerCase()));
					Predicate n12 = (cb.equal(b.get("dobOrRegDate"), req.getDobOrRegDate()));
					Predicate n13 = (cb.like(cb.lower(b.get("email1")), req.getEmail1().toLowerCase()));
					// Predicate n14 = (cb.like(cb.lower(b.get("email2")),
					// req.getEmail2().toLowerCase()));
					// Predicate n15 = (cb.like(cb.lower(b.get("email3")),
					// req.getEmail3().toLowerCase()));
					// Predicate n16 = (cb.equal(b.get("fax"), req.getFax().toLowerCase()));
					Predicate n17 = (cb.like(cb.lower(b.get("gender")), req.getGender().toLowerCase()));
					Predicate n18 = (cb.like(cb.lower(b.get("idNumber")), req.getIdNumber().toLowerCase()));
					Predicate n19 = (cb.like(cb.lower(b.get("isTaxExempted")), req.getIsTaxExempted().toLowerCase()));
					Predicate n20 = (cb.like(cb.lower(b.get("language")), req.getLanguage().toLowerCase()));
					// Predicate n21 = (cb.like(cb.lower(b.get("languageDesc")),
					// req.getLanguageDesc().toLowerCase()));
					Predicate n22 = (cb.equal(b.get("mobileNo1"), req.getMobileNo1()));
					// Predicate n23 = (cb.equal(b.get("mobileNo2"), req.getMobileNo2()));
					// Predicate n24 = (cb.equal(b.get("mobileNo3"), req.getMobileNo3()));
					Predicate n25 = (cb.like(cb.lower(b.get("nationality")), req.getNationality().toLowerCase()));
					Predicate n26 = (cb.like(cb.lower(b.get("occupation")), req.getOccupation().toLowerCase()));
					Predicate n27 = (cb.like(cb.lower(b.get("placeOfBirth")), req.getPlaceOfBirth().toLowerCase()));
					Predicate n28 = (cb.like(cb.lower(b.get("policyHolderType")),
							req.getPolicyHolderType().toLowerCase()));
					// Predicate n29 = (cb.like(cb.lower(b.get("policyHolderTypeId")),
					// req.getPolicyHolderTypeid().toLowerCase()));
					// Predicate n30 = (cb.like(cb.lower(b.get("productId")), req.getProductId()));

					Predicate n30 = (cb.equal(b.get("productId"),
							null != req.getProductId() && req.getProductId().matches("[0-9]+")
									? Integer.valueOf(req.getProductId())
									: 0));
					Predicate n31 = (cb.like(cb.lower(b.get("regionCode")), req.getRegionCode().toLowerCase()));
					// Predicate n32 = (cb.like(cb.lower(b.get("stateCode")),
					// req.getStateCode().toLowerCase()));
					Predicate n33 = (cb.like(cb.lower(b.get("stateName")), req.getStateName().toLowerCase()));
					Predicate n34 = (cb.like(cb.lower(b.get("status")), req.getStatus().toLowerCase()));
					// Predicate n35 = (cb.like(cb.lower(b.get("street")),
					// req.getStreet().toLowerCase()));
					// Predicate n36 = (cb.like(cb.lower(b.get("taxExemptedId")),
					// req.getTaxExemptedId().toLowerCase()));
					// Predicate n37 = (cb.equal(b.get("telephoneNo1"), req.getTelephoneNo1()));
					// Predicate n38 = (cb.equal(b.get("telephoneNo2"), req.getTelephoneNo2()));
					// Predicate n39 = (cb.equal(b.get("telephoneNo3"), req.getTelephoneNo3()));
					// Predicate n40 = (cb.like(cb.lower(b.get("title")),
					// req.getTitle().toLowerCase()));
					// Predicate n41 = (cb.equal(b.get("appointmentDate"),
					// req.getAppointmentDate()));
					Predicate n42 = (cb.like(cb.lower(b.get("preferredNotification")),
							req.getPreferredNotification().toLowerCase()));

					query.where(n1, n3, n6, n7, n8, n9, n10,
							// n11,
							n12, n13, n17, n18, n19, n20, n22, n25, n26, n27, n28, n30, n31, n33, n34, /* n35, */ n42);
					// Get Result
					TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
					list = result.getResultList();
					if (list.size() > 0 && req.getCustomerReferenceNo() == null) {
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

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
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

	public static boolean checkIsValidMail(String mail) {
		String regex = "^[a-zA-Z0-9._%+-àâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]+@[a-zA-Z0-9.-àâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]+\\.[a-zA-ZàâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]{2,}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.matches();
	}

	@Override
	@Transactional
	public SuccessRes updateCustomerDetails(EserviceCustomerSaveReq req) {
		SuccessRes res = new SuccessRes();

		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {

			Date entryDate = null;
			String createdBy = "";
			String custRefNo = "";
			Integer productId;

			// Update
			custRefNo = req.getCustomerReferenceNo();
			EserviceCustomerDetails findData = repository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
			entryDate = findData.getEntryDate();
			createdBy = findData.getCreatedBy();
			productId = findData.getProductId();
			res.setResponse("Updated Successfully");
			res.setSuccessId(custRefNo);
			PersonalInfo pi = new PersonalInfo();
			List<PersonalInfo> piList = personalInforepo.findByCustomerReferenceNo(req.getCustomerReferenceNo());
			if (!piList.isEmpty()) {
				pi = piList.get(0);
			}

			// Age Calculation
			int age = 0;
			Date dob = null;
			if (req.getDobOrRegDate() != null) {
				dob = req.getDobOrRegDate();
				Date today = new Date();
				age = today.getYear() - dob.getYear();
			}
			// From List Item Value

			Map<String, String> title = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "NAME_TITLE", req.getTitle());// listRepo.findByItemTypeAndItemCode("NAME_TITLE",
																		// req.getTitle());
			Map<String, String> gender = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "GENDER", req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER",
																	// saveData.getGender());
			Map<String, String> language = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "LANGUAGE", req.getLanguage());// listRepo.findByItemTypeAndItemCode("LANGUAGE",
																		// req.getLanguage());
			Map<String, String> policyHolderType = eCustDetailsServiceImpl.getListItemLocal("99999",
					req.getBranchCode(), "POLICY_HOLDER_TYPE", req.getPolicyHolderType());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",
																							// req.getPolicyHolderType());
			Map<String, String> policyHolderTypeId = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE",
																								// req.getPolicyHolderTypeid());

			String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
			String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
			String languageDesc = Optional.ofNullable(language).map(map -> map.get("itemDesc")).orElse("");
			String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc"))
					.orElse("");
			String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc"))
					.orElse("");

			// From List Item Value (Local)
			String genderLocal = Optional.ofNullable(gender).map(map -> map.get("itemDescLocal")).orElse("");
			String titleLocal = Optional.ofNullable(title).map(map -> map.get("itemDescLocal")).orElse("");
			String languageLocal = Optional.ofNullable(language).map(map -> map.get("itemDescLocal")).orElse("");
			String PolicyHolderTypeLocal = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDescLocal"))
					.orElse("");
			String policyHolderTypeIdLocal = Optional.ofNullable(policyHolderTypeId)
					.map(map -> map.get("itemDescLocal")).orElse("");

			// From Region_mater for state name local
			String stateNameLocal = "";
			List<RegionMaster> rgMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getCountry(),
					req.getStateCode());
			if (rgMaster != null && rgMaster.size() > 0) {
				stateNameLocal = rgMaster.get(0).getRegionNameLocal();
			}
			// From State_master for city name local
			String cityNameLocal = "";
			List<StateMaster> stMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(
					Integer.valueOf(StringUtils.isNotBlank(req.getCityCode()) ? req.getCityCode() : "0"),
					req.getCountry(), req.getStateCode());
			if (stMaster != null && stMaster.size() > 0) {
				cityNameLocal = stMaster.get(0).getStateNameLocal();
			}

			if (StringUtils.isNotBlank(req.getMobileCode1())) {
				Map<String, String> mobileCode1Desc = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode1());
				String mobileCode1 = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode1Local = Optional.ofNullable(mobileCode1Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");

			}
			if (StringUtils.isNotBlank(req.getMobileCode2())) {
				Map<String, String> mobileCode2Desc = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode2());
				String mobileCode2 = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode2Local = Optional.ofNullable(mobileCode2Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");

			}

			if (StringUtils.isNotBlank(req.getMobileCode3())) {
				Map<String, String> mobileCode3Desc = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode3());
				String mobileCode3 = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode3Local = Optional.ofNullable(mobileCode3Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");

			}
			if (StringUtils.isNotBlank(req.getWhatsappCode())) {
				Map<String, String> whatsappCodeDesc = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getWhatsappCode());
				String whatsappCode = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDesc")).orElse("");
				// String whatsappCodeLocal = Optional.ofNullable(whatsappCodeDesc).map(map ->
				// map.get("itemDescLocal")).orElse("");

			}
			String businessTypeLocal = "";
			if (StringUtils.isNotBlank(req.getBusinessType())) {
				Map<String, String> businessTypeDesc = eCustDetailsServiceImpl.getListItemLocal("99999",
						req.getBranchCode(), "BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																						// req.getBusinessType());
				String businessType = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDesc")).orElse("");
				businessTypeLocal = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDescLocal"))
						.orElse("");

			}
			String occupationDesc = "", occupationDescLocal = "";
			if (StringUtils.isNotBlank(req.getOccupation())) {
				Map<String, String> occupation = eCustDetailsServiceImpl.getByOccupationIdDesc(req.getOccupation(),
						req.getCompanyId(), req.getProductId(), req.getBranchCode());
				occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
				occupationDescLocal = Optional.ofNullable(occupation).map(map -> map.get("occupationNameLocal"))
						.orElse("");
			}

			String idType = StringUtils.isBlank(req.getIdType()) && StringUtils.isNotBlank(req.getPolicyHolderType())
					? req.getPolicyHolderType()
					: req.getIdType();

			// Personal Info Update

			// Endorsement flow and B2C Flow
			// Type=B2C
			if (StringUtils.isNotBlank(req.getEndtCategDesc())) {
				if ("Non Financial".equalsIgnoreCase(req.getEndtCategDesc().toString())) {
					PersonalInfo savePersonalInfo = new PersonalInfo();
					HomePositionMaster homedata = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
					// PersonalInfo
					// personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());

					savePersonalInfo.setPinCode(req.getPinCode() != null ? req.getPinCode() : findData.getPinCode());
					savePersonalInfo.setCustomerId(homedata.getCustomerId()); // assuming this never null
					savePersonalInfo
							.setIdNumber(req.getIdNumber() != null ? req.getIdNumber() : findData.getIdNumber());
					savePersonalInfo.setCreatedBy(createdBy); // assuming this is set earlier
					savePersonalInfo.setUpdatedDate(new Date());
					savePersonalInfo
							.setUpdatedBy(req.getCreatedBy() != null ? req.getCreatedBy() : findData.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(custRefNo); // assuming this is set earlier
					savePersonalInfo
							.setAddress1(req.getAddress1() != null ? req.getAddress1() : findData.getAddress1());
					savePersonalInfo
							.setAddress2(req.getAddress2() != null ? req.getAddress2() : findData.getAddress2());
					savePersonalInfo.setAge(age); // assuming age is calculated earlier
					savePersonalInfo.setBranchCode(
							req.getBranchCode() != null ? req.getBranchCode() : findData.getBranchCode());
					savePersonalInfo.setBusinessType(
							req.getBusinessType() != null ? req.getBusinessType() : findData.getBusinessType());
					savePersonalInfo.setOtherOccupation(req.getOtherOccupation() != null ? req.getOtherOccupation()
							: findData.getOtherOccupation());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType = eCustDetailsServiceImpl.getListItem("99999", req.getBranchCode(),
								"BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																		// req.getBusinessType());
						savePersonalInfo.setBusinessTypeDesc(businessType);

					}

					savePersonalInfo.setRegionCode(
							req.getRegionCode() != null ? req.getRegionCode() : findData.getRegionCode());
					savePersonalInfo.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted())
							? (StringUtils.isBlank(findData.getIsTaxExempted()) ? "0" : findData.getIsTaxExempted())
							: req.getIsTaxExempted());
					savePersonalInfo.setCityCode(
							req.getCityCode() != null ? req.getCityCode() : String.valueOf(findData.getCityCode()));
					savePersonalInfo.setCityName(StringUtils.isBlank(determineCityName(req)) ? findData.getCityName()
							: determineCityName(req));
					savePersonalInfo.setClientName(
							StringUtils.isBlank(req.getClientName()) ? findData.getClientName() : req.getClientName());
					savePersonalInfo
							.setClientStatus(StringUtils.isBlank(req.getClientStatus()) ? findData.getClientStatus()
									: req.getClientStatus());

					String clientStatus = savePersonalInfo.getClientStatus();
					savePersonalInfo.setClientStatusDesc("N".equalsIgnoreCase(clientStatus) ? "DeActive" : "Active");

					savePersonalInfo
							.setCompanyId(req.getCompanyId() != null ? req.getCompanyId() : findData.getCompanyId());
					savePersonalInfo
							.setCreatedBy(req.getCreatedBy() != null ? req.getCreatedBy() : findData.getCreatedBy());
					savePersonalInfo
							.setCustomerReferenceNo(req.getCustomerReferenceNo() != null ? req.getCustomerReferenceNo()
									: findData.getCustomerReferenceNo());
					savePersonalInfo.setDobOrRegDate(dob); // assuming 'dob' is already calculated

					savePersonalInfo
							.setEmail1(StringUtils.isBlank(req.getEmail1()) ? findData.getEmail1() : req.getEmail1());
					savePersonalInfo
							.setEmail2(StringUtils.isBlank(req.getEmail2()) ? findData.getEmail2() : req.getEmail2());
					savePersonalInfo
							.setEmail3(StringUtils.isBlank(req.getEmail3()) ? findData.getEmail3() : req.getEmail3());

					savePersonalInfo.setEndorsementDate(
							req.getEndorsementDate() != null ? req.getEndorsementDate() : pi.getEndorsementDate());
					savePersonalInfo
							.setEndorsementEffdate(req.getEndorsementEffdate() != null ? req.getEndorsementEffdate()
									: pi.getEndorsementEffdate());
					savePersonalInfo.setEndorsementRemarks(
							StringUtils.isBlank(req.getEndorsementRemarks()) ? pi.getEndorsementRemarks()
									: req.getEndorsementRemarks());
					savePersonalInfo.setEndorsementType(
							StringUtils.isBlank(String.valueOf(req.getEndorsementType())) ? pi.getEndorsementType()
									: req.getEndorsementType());
					savePersonalInfo.setEndorsementTypeDesc(
							StringUtils.isBlank(req.getEndorsementTypeDesc()) ? pi.getEndorsementTypeDesc()
									: req.getEndorsementTypeDesc());
					savePersonalInfo
							.setEndtCategDesc(StringUtils.isBlank(req.getEndtCategDesc()) ? pi.getEndtCategDesc()
									: req.getEndtCategDesc());
					savePersonalInfo.setEndtCount(req.getEndtCount() != null ? req.getEndtCount() : pi.getEndtCount());
					savePersonalInfo.setEndtPrevPolicyNo(
							StringUtils.isBlank(req.getEndtPrevPolicyNo()) ? pi.getEndtPrevPolicyNo()
									: req.getEndtPrevPolicyNo());
					savePersonalInfo
							.setEndtPrevQuoteNo(StringUtils.isBlank(req.getEndtPrevQuoteNo()) ? pi.getEndtPrevQuoteNo()
									: req.getEndtPrevQuoteNo());
					savePersonalInfo.setEndtStatus(
							StringUtils.isBlank(req.getEndtStatus()) ? pi.getEndtStatus() : req.getEndtStatus());

					savePersonalInfo.setEntryDate(new Date());

					savePersonalInfo.setFax(StringUtils.isBlank(req.getFax()) ? findData.getFax() : req.getFax());
					savePersonalInfo.setGender(StringUtils.isBlank(req.getGender())
							? (StringUtils.isBlank(findData.getGender()) ? "M" : findData.getGender())
							: req.getGender());
					savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation())
							? (StringUtils.isBlank(findData.getOccupation()) ? "2" : findData.getOccupation())
							: req.getOccupation());

					savePersonalInfo.setGenderDesc(genderDesc);
					savePersonalInfo.setTitleDesc(titleDesc);
					savePersonalInfo.setLanguageDesc(languageDesc);
					savePersonalInfo.setOccupationDesc(occupationDesc);

					// Individual / Corporate
					savePersonalInfo.setPolicyHolderType(
							StringUtils.isBlank(req.getPolicyHolderType()) ? findData.getPolicyHolderType()
									: req.getPolicyHolderType());
					savePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc); // Assumes this is calculated based
																					// on selected type

					// Passport or similar document
					savePersonalInfo.setPolicyHolderTypeid(
							StringUtils.isBlank(req.getPolicyHolderTypeid()) ? findData.getPolicyHolderTypeid()
									: req.getPolicyHolderTypeid());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc); // Assumes this is derived based
																						// on the ID type

					// Mapping to idType and its description (same as PolicyHolderTypeid)
					savePersonalInfo.setIdType(
							StringUtils.isBlank(req.getPolicyHolderTypeid()) ? findData.getPolicyHolderTypeid()
									: req.getPolicyHolderTypeid());
					savePersonalInfo.setIdTypeDesc(policyHolderTypeIdDesc); // Same description used here too

					savePersonalInfo
							.setMobileCode1(StringUtils.isBlank(req.getMobileCode1()) ? findData.getMobileCode1()
									: req.getMobileCode1());
					savePersonalInfo.setMobileCode2(StringUtils.isBlank(req.getMobileCode2())
							? (StringUtils.isBlank(findData.getMobileCode2()) ? "" : findData.getMobileCode2())
							: req.getMobileCode2());
					savePersonalInfo.setMobileCode3(StringUtils.isBlank(req.getMobileCode3())
							? (StringUtils.isBlank(findData.getMobileCode3()) ? "" : findData.getMobileCode3())
							: req.getMobileCode3());

					savePersonalInfo.setMobileNo1(
							StringUtils.isBlank(req.getMobileNo1()) ? findData.getMobileNo1() : req.getMobileNo1());
					savePersonalInfo.setMobileNo2(
							StringUtils.isBlank(req.getMobileNo2()) ? findData.getMobileNo2() : req.getMobileNo2());
					savePersonalInfo.setMobileNo3(
							StringUtils.isBlank(req.getMobileNo3()) ? findData.getMobileNo3() : req.getMobileNo3());

					savePersonalInfo
							.setWhatsappCode(StringUtils.isBlank(req.getWhatsappCode()) ? findData.getWhatsappCode()
									: req.getWhatsappCode());

					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode1(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode2(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode3(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(), req.getCompanyId());
						savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					savePersonalInfo.setRegionCode(
							StringUtils.isBlank(req.getRegionCode()) ? findData.getRegionCode() : req.getRegionCode());

					savePersonalInfo.setStateCode(
							StringUtils.isBlank(req.getStateCode()) ? String.valueOf(findData.getStateCode())
									: req.getStateCode());

					String stateName = determineStateName(req);
					savePersonalInfo.setStateName(StringUtils.isBlank(stateName) ? findData.getStateName() : stateName);

					savePersonalInfo
							.setStatus(StringUtils.isBlank(req.getStatus()) ? findData.getStatus() : req.getStatus());

					savePersonalInfo
							.setNationality(StringUtils.isBlank(req.getNationality()) ? findData.getNationality()
									: req.getNationality());

					savePersonalInfo.setVrTinNo(
							StringUtils.isBlank(req.getVrTinNo()) ? findData.getVrTinNo() : req.getVrTinNo());

					// Assuming VrnGst is same as VrTinNo
					savePersonalInfo.setVrnGst(
							StringUtils.isBlank(req.getVrTinNo()) ? findData.getVrTinNo() : req.getVrTinNo());

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
					savePersonalInfo
							.setSocioProfessionalCategory(StringUtils.isBlank(req.getSocioProfessionalCategory())
									? findData.getSocioProfessionalCategory()
									: req.getSocioProfessionalCategory());

					savePersonalInfo.setActivities(
							StringUtils.isBlank(req.getActivities()) ? findData.getActivities() : req.getActivities());

					savePersonalInfo.setCustomerAsInsurer(
							StringUtils.isBlank(req.getCustomerAsInsurer()) ? findData.getCustomerAsInsurer()
									: req.getCustomerAsInsurer());

					personalInforepo.save(savePersonalInfo);
					homedata.setCustomerName(
							StringUtils.isBlank(req.getClientName()) ? findData.getClientName() : req.getClientName());
					homePosistionRepo.save(homedata);
					
				}
			} else {
				HomePositionMaster homedata = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
					PersonalInfo savePersonalInfo = new PersonalInfo();

					// PersonalInfo
					// personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
					savePersonalInfo.setPinCode(
							StringUtils.isBlank(req.getPinCode()) ? findData.getPinCode() : req.getPinCode());

					savePersonalInfo.setCustomerId(
							homedata.getCustomerId() != null ? homedata.getCustomerId() : pi.getCustomerId());

					savePersonalInfo.setIdNumber(
							StringUtils.isBlank(req.getIdNumber()) ? findData.getIdNumber() : req.getIdNumber());

					savePersonalInfo.setCreatedBy(createdBy); // assumed set earlier or system-generated

					savePersonalInfo.setUpdatedDate(new Date());

					savePersonalInfo.setUpdatedBy(
							StringUtils.isBlank(req.getCreatedBy()) ? findData.getCreatedBy() : req.getCreatedBy());

					savePersonalInfo.setCustomerReferenceNo(
							StringUtils.isBlank(custRefNo) ? findData.getCustomerReferenceNo() : custRefNo);

					savePersonalInfo.setAddress1(
							StringUtils.isBlank(req.getAddress1()) ? findData.getAddress1() : req.getAddress1());

					savePersonalInfo.setAddress2(
							StringUtils.isBlank(req.getAddress2()) ? findData.getAddress2() : req.getAddress2());

					savePersonalInfo.setAge(age); // assumed calculated already

					savePersonalInfo.setBranchCode(
							StringUtils.isBlank(req.getBranchCode()) ? findData.getBranchCode() : req.getBranchCode());

					savePersonalInfo
							.setBusinessType(StringUtils.isBlank(req.getBusinessType()) ? findData.getBusinessType()
									: req.getBusinessType());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType = eCustDetailsServiceImpl.getListItem("99999", req.getBranchCode(),
								"BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																		// req.getBusinessType());
						savePersonalInfo.setBusinessTypeDesc(businessType);
					}

					savePersonalInfo.setIsTaxExempted(
							StringUtils.isBlank(req.getIsTaxExempted()) ? "0" : req.getIsTaxExempted());

					savePersonalInfo
							.setCityCode(StringUtils.isBlank(req.getCityCode()) ? pi.getCityCode() : req.getCityCode());

					String cityName = determineCityName(req);
					savePersonalInfo.setCityName(StringUtils.isBlank(cityName) ? findData.getCityName() : cityName);

					savePersonalInfo.setClientName(
							StringUtils.isBlank(req.getClientName()) ? findData.getClientName() : req.getClientName());

					savePersonalInfo
							.setClientStatus(StringUtils.isBlank(req.getClientStatus()) ? findData.getClientStatus()
									: req.getClientStatus());

					String clientStatus = StringUtils.isBlank(req.getClientStatus()) ? findData.getClientStatus()
							: req.getClientStatus();
					savePersonalInfo.setClientStatusDesc("N".equalsIgnoreCase(clientStatus) ? "DeActive" : "Active");

					savePersonalInfo.setCompanyId(
							StringUtils.isBlank(req.getCompanyId()) ? findData.getCompanyId() : req.getCompanyId());

					savePersonalInfo.setCreatedBy(
							StringUtils.isBlank(req.getCreatedBy()) ? findData.getCreatedBy() : req.getCreatedBy());

					savePersonalInfo.setCustomerReferenceNo(
							StringUtils.isBlank(req.getCustomerReferenceNo()) ? findData.getCustomerReferenceNo()
									: req.getCustomerReferenceNo());

					savePersonalInfo.setDobOrRegDate(
							req.getDobOrRegDate() != null ? req.getDobOrRegDate() : findData.getDobOrRegDate());

					savePersonalInfo
							.setEmail1(StringUtils.isBlank(req.getEmail1()) ? findData.getEmail1() : req.getEmail1());

					savePersonalInfo
							.setEmail2(StringUtils.isBlank(req.getEmail2()) ? findData.getEmail2() : req.getEmail2());

					savePersonalInfo
							.setEmail3(StringUtils.isBlank(req.getEmail3()) ? findData.getEmail3() : req.getEmail3());

					savePersonalInfo.setEndorsementDate(
							req.getEndorsementDate() != null ? req.getEndorsementDate() : pi.getEndorsementDate());

					savePersonalInfo
							.setEndorsementEffdate(req.getEndorsementEffdate() != null ? req.getEndorsementEffdate()
									: pi.getEndorsementEffdate());

					savePersonalInfo.setEndorsementRemarks(
							StringUtils.isBlank(req.getEndorsementRemarks()) ? pi.getEndorsementRemarks()
									: req.getEndorsementRemarks());

					savePersonalInfo.setEndorsementType(
							StringUtils.isBlank(String.valueOf(req.getEndorsementType())) ? pi.getEndorsementType()
									: req.getEndorsementType());

					savePersonalInfo.setEndorsementTypeDesc(
							StringUtils.isBlank(req.getEndorsementTypeDesc()) ? pi.getEndorsementTypeDesc()
									: req.getEndorsementTypeDesc());

					savePersonalInfo
							.setEndtCategDesc(StringUtils.isBlank(req.getEndtCategDesc()) ? pi.getEndtCategDesc()
									: req.getEndtCategDesc());

					savePersonalInfo.setEndtCount(req.getEndtCount() != null ? req.getEndtCount() : pi.getEndtCount());

					savePersonalInfo.setEndtPrevPolicyNo(
							StringUtils.isBlank(req.getEndtPrevPolicyNo()) ? pi.getEndtPrevPolicyNo()
									: req.getEndtPrevPolicyNo());

					savePersonalInfo
							.setEndtPrevQuoteNo(StringUtils.isBlank(req.getEndtPrevQuoteNo()) ? pi.getEndtPrevQuoteNo()
									: req.getEndtPrevQuoteNo());

					savePersonalInfo.setEndtStatus(
							StringUtils.isBlank(req.getEndtStatus()) ? pi.getEndtStatus() : req.getEndtStatus());

					savePersonalInfo.setEntryDate(new Date());

					savePersonalInfo.setFax(StringUtils.isBlank(req.getFax()) ? findData.getFax() : req.getFax());

					savePersonalInfo.setGender(StringUtils.isBlank(req.getGender())
							? (StringUtils.isBlank(findData.getGender()) ? "M" : findData.getGender())
							: req.getGender());

					savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation())
							? (StringUtils.isBlank(findData.getOccupation()) ? "2" : findData.getOccupation())
							: req.getOccupation());

					savePersonalInfo.setGenderDesc(genderDesc); // Assumed derived already
					savePersonalInfo
							.setTitle(StringUtils.isBlank(req.getTitle()) ? findData.getTitle() : req.getTitle());
					savePersonalInfo.setTitleDesc(titleDesc); // Assumed derived already
					savePersonalInfo.setLanguageDesc(languageDesc);
					savePersonalInfo.setOccupationDesc(occupationDesc);

					savePersonalInfo
							.setIdType(StringUtils.isBlank(req.getIdType()) ? findData.getIdType() : req.getIdType());

					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc); // Assumed derived
					// Individual / Corporate
					savePersonalInfo.setPolicyHolderType(
							StringUtils.isBlank(req.getPolicyHolderType()) ? findData.getPolicyHolderType()
									: req.getPolicyHolderType());

					savePersonalInfo.setPolicyHolderTypeDesc(
							StringUtils.isBlank(policyHolderTypeDesc) ? findData.getPolicyHolderTypeDesc()
									: policyHolderTypeDesc);

					// Passport or etc
					savePersonalInfo.setPolicyHolderTypeid(
							StringUtils.isBlank(req.getPolicyHolderTypeid()) ? findData.getPolicyHolderTypeid()
									: req.getPolicyHolderTypeid());

					savePersonalInfo.setPolicyHolderTypeIdDesc(
							StringUtils.isBlank(policyHolderTypeIdDesc) ? findData.getPolicyHolderTypeIdDesc()
									: policyHolderTypeIdDesc);

					savePersonalInfo.setIdType(StringUtils.isBlank(req.getPolicyHolderTypeid()) ? findData.getIdType()
							: req.getPolicyHolderTypeid());

					savePersonalInfo.setIdTypeDesc(StringUtils.isBlank(policyHolderTypeDesc) ? findData.getIdTypeDesc()
							: policyHolderTypeDesc);

					// Mobile Information
					savePersonalInfo
							.setMobileCode1(StringUtils.isBlank(req.getMobileCode1()) ? findData.getMobileCode1()
									: req.getMobileCode1());

					savePersonalInfo
							.setMobileCode2(StringUtils.isBlank(req.getMobileCode2()) ? findData.getMobileCode2()
									: req.getMobileCode2());

					savePersonalInfo
							.setMobileCode3(StringUtils.isBlank(req.getMobileCode3()) ? findData.getMobileCode3()
									: req.getMobileCode3());

					savePersonalInfo.setMobileNo1(
							StringUtils.isBlank(req.getMobileNo1()) ? findData.getMobileNo1() : req.getMobileNo1());

					savePersonalInfo.setMobileNo2(
							StringUtils.isBlank(req.getMobileNo2()) ? findData.getMobileNo2() : req.getMobileNo2());

					savePersonalInfo.setMobileNo3(
							StringUtils.isBlank(req.getMobileNo3()) ? findData.getMobileNo3() : req.getMobileNo3());

					savePersonalInfo
							.setWhatsappCode(StringUtils.isBlank(req.getWhatsappCode()) ? findData.getWhatsappCode()
									: req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode1(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode2(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode3(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(), req.getCompanyId());
						savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					savePersonalInfo.setRegionCode(
							StringUtils.isBlank(req.getRegionCode()) ? findData.getRegionCode() : req.getRegionCode());

					savePersonalInfo.setStateCode(
							StringUtils.isBlank(req.getStateCode()) ? pi.getStateCode() : req.getStateCode());

					savePersonalInfo.setStateName(StringUtils.isBlank(determineStateName(req)) ? findData.getStateName()
							: determineStateName(req));

					savePersonalInfo
							.setStatus(StringUtils.isBlank(req.getStatus()) ? findData.getStatus() : req.getStatus());

					savePersonalInfo
							.setNationality(StringUtils.isBlank(req.getNationality()) ? findData.getNationality()
									: req.getNationality());

					savePersonalInfo.setVrTinNo(
							StringUtils.isBlank(req.getVrTinNo()) ? findData.getVrTinNo() : req.getVrTinNo());

					savePersonalInfo
							.setVrnGst(StringUtils.isBlank(req.getVrTinNo()) ? findData.getVrnGst() : req.getVrTinNo());

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
					savePersonalInfo
							.setSocioProfessionalCategory(StringUtils.isBlank(req.getSocioProfessionalCategory())
									? findData.getSocioProfessionalCategory()
									: req.getSocioProfessionalCategory());

					savePersonalInfo.setActivities(
							StringUtils.isBlank(req.getActivities()) ? findData.getActivities() : req.getActivities());

					savePersonalInfo.setCustomerAsInsurer(
							StringUtils.isBlank(req.getCustomerAsInsurer()) ? findData.getCustomerAsInsurer()
									: req.getCustomerAsInsurer());

					personalInforepo.save(savePersonalInfo);
					homedata.setCustomerName(
							StringUtils.isBlank(req.getClientName()) ? findData.getClientName() : req.getClientName());
					homePosistionRepo.save(homedata);
					if (homedata.getProductId() == 5) {
						Optional<MotorDataDetails> motorDataDetails = motorDataDetailsRepo
								.findFirstByQuoteNo(homedata.getQuoteNo());
						if (motorDataDetails.isPresent()) {
							MotorVehicleInfo motorVehicleInfo = motorVehicleInfoRepo
									.findTop1ByResChassisNumberAndCompanyIdOrderByEntryDateDesc(
											motorDataDetails.get().getChassisNumber(), homedata.getCompanyId());

							motorVehicleInfo
									.setResOwnerName(StringUtils.isBlank(req.getClientName()) ? findData.getClientName()
											: req.getClientName());
							motorVehicleInfoRepo.save(motorVehicleInfo);
						}
					}
					res.setSuccessId(custRefNo);
					res.setResponse("Updated");
	
				}
			// Response

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

	}

	public CustomerDetailsGetRes getCustomerDetails(GetCustomerDetailsReq req) {
		CustomerDetailsGetRes res = new CustomerDetailsGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceCustomerDetails> data = repository
					.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo());
			if (data != null && data.size() > 0) {
				EserviceCustomerDetails cdate = data.get(0);
				res = dozerMapper.map(cdate, CustomerDetailsGetRes.class);

				res.setTitle(cdate.getTitle() == null ? "" : cdate.getTitle());
				res.setFirstName(cdate.getClientName() == null ? "" : cdate.getClientName());
				res.setBusinessType(cdate.getBusinessType() == null ? "" : cdate.getBusinessType());
				res.setGender(cdate.getGender() == null ? "" : cdate.getGender());
				res.setOccupation(cdate.getOccupation() == null ? "" : cdate.getOccupation());
				res.setOtherOccupation(cdate.getOtherOccupation() == null ? "" : cdate.getOtherOccupation());
				res.setEmail1(cdate.getEmail1() == null ? "" : cdate.getEmail1());
				res.setWhatsappCode(cdate.getWhatsappCode() == null ? "" : cdate.getWhatsappCode());
				res.setMobileCode1(cdate.getMobileCode1() == null ? "" : cdate.getMobileCode1());
				res.setMobileCode2(cdate.getMobileCode2() == null ? "" : cdate.getMobileCode2());
				res.setMobileCode3(cdate.getMobileCode3() == null ? "" : cdate.getMobileCode3());
				res.setMobileCodeDesc1(cdate.getMobileCodeDesc1() == null ? "" : cdate.getMobileCodeDesc1());
				res.setMobileCodeDesc2(cdate.getMobileCodeDesc2() == null ? "" : cdate.getMobileCodeDesc2());
				res.setMobileCodeDesc3(cdate.getMobileCodeDesc3() == null ? "" : cdate.getMobileCodeDesc3());
				res.setMobileNo1(cdate.getMobileNo1() == null ? "" : cdate.getMobileNo1());
				res.setMobileNo2(cdate.getMobileNo2() == null ? "" : cdate.getMobileNo2());
				res.setMobileNo3(cdate.getMobileNo3() == null ? "" : cdate.getMobileNo3());
				res.setActivities(cdate.getActivities() == null ? "" : cdate.getActivities());
				res.setIdType(cdate.getIdType() == null ? "" : cdate.getIdType());
				res.setIdNumber(cdate.getIdNumber() == null ? "" : cdate.getIdNumber());
				res.setIsTaxExempted(cdate.getIsTaxExempted() == null ? "" : cdate.getIsTaxExempted());
				res.setPreferredNotification(
						cdate.getPreferredNotification() == null ? "" : cdate.getPreferredNotification());
				res.setStatus(cdate.getStatus() == null ? "" : cdate.getStatus());

				res.setCountry(cdate.getCountry() == null ? "" : cdate.getCountry());
				res.setCityCode(cdate.getCityCode() == null ? "" : cdate.getCityCode().toString());
				res.setCityName(cdate.getCityName() == null ? "" : cdate.getCityName());
				res.setStateName(cdate.getStateName() == null ? "" : cdate.getStateName());
				res.setPinCode(cdate.getPinCode() == null ? "" : cdate.getPinCode());
				res.setRegionCode(cdate.getRegionCode() == null ? "" : cdate.getRegionCode());
				res.setPolicyHolderTypeid(cdate.getPolicyHolderTypeid() == null ? "" : cdate.getPolicyHolderTypeid());
				res.setVrTinNo(cdate.getVrTinNo() == null ? "" : cdate.getVrTinNo());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	/**
	 * This method returns the state name if it's provided. If not, it tries to find
	 * the state name using the country, and region code.
	 */
	private String determineStateName(EserviceCustomerSaveReq req) {

		if (StringUtils.isNotBlank(req.getStateName())) {
			return req.getStateName();
		}

		else if (StringUtils.isBlank(req.getStateName()) && StringUtils.isNotBlank(req.getStateCode())) {
			List<RegionMaster> regionMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getCountry(),
					req.getRegionCode());
			if (regionMaster.size() == 1) {
				return regionMaster.get(0).getRegionName();
			}
		}

		return null;
	}

	/**
	 * This method returns the city name if it's provided. If not, it looks up the
	 * city name using the city code, country, and state code.
	 */
	private String determineCityName(EserviceCustomerSaveReq req) {
		if (StringUtils.isNotBlank(req.getCityName())) {
			return req.getCityName();
		}

		else if (StringUtils.isBlank(req.getCityName()) && StringUtils.isNotBlank(req.getCityCode())) {
			List<StateMaster> stateMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(
					Integer.valueOf(req.getCityCode()), req.getCountry(), req.getStateCode());

			if (stateMaster.size() == 1) {
				return stateMaster.get(0).getStateName();
			}
		}

		return null;
	}

	@Override
	public CustomerDetailsGetRes getCustomerReferenceNo(GetCustomerReq req) {
		CustomerDetailsGetRes res = new CustomerDetailsGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			HomePositionMaster h = homePosistionRepo.findByPolicyNo(req.getPolicyNo());
			if (h != null) {

				PersonalInfo personalDetails = personalInforepo.findByCustomerId(h.getCustomerId());
				if (personalDetails != null) {
					res = dozerMapper.map(personalDetails, CustomerDetailsGetRes.class);
				//	res.setSailPointCode(h.getSalePointCode()== null?null:h.getSalePointCode() );
				//	res.setPolicyStartDate(h.getInceptionDate());
				//	res.setPolicyEndDate(h.getExpiryDate());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public SuccessRes updateTiraCustomerDetails(UpdateCustomerDetailsReq req) {
		SuccessRes res = new SuccessRes();
		try {
			if((StringUtils.isNotBlank(req.getQuoteNo()))) {
				
				HomePositionMaster home = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				
				if(home != null) {
					
					Map<String, String> gender = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
							req.getBranchCode(), "GENDER", req.getGender());
					
					Map<String, String> policyHolderTypeId = eCustDetailsServiceImpl.getListItemLocal(req.getCompanyId(),
							req.getBranchCode(), "POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());
					
					String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
					
					String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc"))
							.orElse("");
					
					String customerId = home.getCustomerId();
					
					PersonalInfo custDetails = personalInforepo.findByCustomerId(customerId);
					String custRefNo = custDetails.getCustomerReferenceNo();
					
					//CustomerInformation Update
					custDetails.setAddress1(req.getAddress1());
					custDetails.setCityCode(req.getCityCode());
					custDetails.setCityName(req.getCityName());	
					custDetails.setClientName(req.getClientName());
					custDetails.setDobOrRegDate(req.getDobOrRegDate());
					custDetails.setEmail1(req.getEmail1());
					custDetails.setFax(req.getFax()==null?null:req.getFax());
					custDetails.setGender(req.getGender());
					custDetails.setGenderDesc(genderDesc);
					custDetails.setIdNumber(req.getIdNumber());
					custDetails.setIdType(req.getIdType());
					custDetails.setPolicyHolderTypeid(req.getIdType());
					custDetails.setIdTypeDesc(policyHolderTypeIdDesc);
					custDetails.setMobileCodeDesc1(req.getMobileCode1());	
					custDetails.setMobileNo1(req.getMobileNo1());
					custDetails.setPolicyHoderTypeIdDesc(policyHolderTypeIdDesc);
					custDetails.setNationality(req.getNationality());
					custDetails.setStateCode(req.getStateCode());
					custDetails.setStateName(req.getStateName());
					custDetails.setStreet(req.getStreet());
					
					personalInforepo.saveAndFlush(custDetails);
					log.info("Customer Information Updated");
					
					//Policy Details Update				 
				     Date StartDate = req.getPolicyStartDate();
				     Date EndDate = req.getPolicyEndDate();
				     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
				     StartDate.setHours(5);
				     StartDate.setMinutes(30);
				     StartDate.setSeconds(0);
				     
				     EndDate.setHours(23);
				     EndDate.setMinutes(59);
				     EndDate.setSeconds(59);
				     
					home.setSalePointCode(req.getSailPointCode());
					home.setInceptionDate(StartDate);
					home.setExpiryDate(EndDate);
					home.setCustomerName(req.getClientName());
					
					homePosistionRepo.saveAndFlush(home);
					log.info("Home Position Table Updated");
					
					//MotorVehicleInfo update
					
					if (home.getProductId() == 5) {
						Optional<MotorDataDetails> motorDataDetails = motorDataDetailsRepo
								.findFirstByQuoteNo(home.getQuoteNo());
						if (motorDataDetails.isPresent()) {
							MotorVehicleInfo motorVehicleInfo = motorVehicleInfoRepo
									.findTop1ByResChassisNumberAndCompanyIdOrderByEntryDateDesc(
											motorDataDetails.get().getChassisNumber(), home.getCompanyId());

							motorVehicleInfo
									.setResOwnerName(req.getClientName());
							motorVehicleInfoRepo.save(motorVehicleInfo);
						}
					}
				//	MotorVehicleInfo motVehInfo = motorVehicleInfoRepo.findByReqRegNumberAndReqChassisNumberAndCompanyId(
						//	req.getRegistrationNo(),req.getChassisNo(),req.getCompanyId());
					
				//	if(motVehInfo != null) {
						
					//	motVehInfo.setResOwnerName(req.getClientName());
					//	motorVehicleInfoRepo.saveAndFlush(motVehInfo);
					//	log.info("Motor Vehicle Info Table Updated");
						
					//}
					res.setSuccessId(custRefNo);
					res.setResponse("Updated Successfull");
					
				}else {
					res.setSuccessId(null);
					res.setResponse("Updated Falied");
				}
		
			}else {
				res.setSuccessId(null);
				res.setResponse("Updated Falied");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return res;
		}
		return res;
	}

	@Override
	public UpdateCustomerDetailsReq getTiraCustomerDetails(UpdateCustomerDetailsReq req) {
		UpdateCustomerDetailsReq res = new UpdateCustomerDetailsReq();
		try {
			HomePositionMaster h = homePosistionRepo.findByPolicyNo(req.getPolicyNo());
			if (h != null) {
				res.setQuoteNo(h.getQuoteNo() == null ? null : h.getQuoteNo());
				res.setPolicyStartDate(h.getInceptionDate() == null ? null : h.getInceptionDate());
				res.setPolicyEndDate(h.getExpiryDate() == null ? null : h.getExpiryDate());
				res.setSailPointCode(h.getSalePointCode() == null ? null : h.getSalePointCode());
				res.setBranchCode(h.getBranchCode() == null ? null : h.getBranchCode());
				res.setCompanyId(h.getCompanyId() == null ? null : h.getCompanyId());
				
				
				PersonalInfo personalDetails = personalInforepo.findByCustomerId(h.getCustomerId());
				if (personalDetails != null) {
					res.setAddress1(personalDetails.getAddress1() == null ? null : personalDetails.getAddress1());
					res.setCityCode(personalDetails.getCityCode()== null ? null : personalDetails.getCityCode());
					res.setCityName(personalDetails.getCityName()== null ? null : personalDetails.getCityName());	
					res.setClientName(personalDetails.getClientName()== null ? null : personalDetails.getClientName());
					res.setDobOrRegDate(personalDetails.getDobOrRegDate()== null ? null : personalDetails.getDobOrRegDate());
					res.setEmail1(personalDetails.getEmail1()== null ? null : personalDetails.getEmail1());
					res.setFax(personalDetails.getFax()==null?null:req.getFax());
					res.setGender(personalDetails.getGender()== null ? null : personalDetails.getGender());
					res.setGenderDesc(personalDetails.getGenderDesc()== null ? null : personalDetails.getGenderDesc());
					res.setIdNumber(personalDetails.getIdNumber()== null ? null : personalDetails.getIdNumber());
					res.setIdType(personalDetails.getIdType()== null ? null : personalDetails.getIdType());
					res.setPolicyHolderTypeid(personalDetails.getPolicyHolderTypeid()== null ? null : personalDetails.getPolicyHolderTypeid());
					res.setIdTypeDesc(personalDetails.getIdTypeDesc()== null ? null : personalDetails.getIdTypeDesc());
					res.setMobileCode1(personalDetails.getMobileCodeDesc1()== null ? null : personalDetails.getMobileCodeDesc1());	
					res.setMobileNo1(personalDetails.getMobileNo1()== null ? null : personalDetails.getMobileNo1());
					res.setPolicyHoderTypeIdDesc(personalDetails.getPolicyHolderTypeIdDesc()== null ? null : personalDetails.getPolicyHolderTypeIdDesc());
					res.setNationality(personalDetails.getNationality()== null ? null : personalDetails.getNationality());
					res.setStateCode(personalDetails.getStateCode()== null ? null : personalDetails.getStateCode());
					res.setStateName(personalDetails.getStateName()== null ? null : personalDetails.getStateName());
					res.setStreet(personalDetails.getStreet()== null ? null : personalDetails.getStreet());
					res.setCustomerReferenceNo(personalDetails.getCustomerReferenceNo()==null?null:personalDetails.getCustomerReferenceNo());
					res.setPolicyHolderType(personalDetails.getPolicyHolderType()==null?null:personalDetails.getPolicyHolderType());
					res.setPolicyHolderTypeDesc(personalDetails.getPolicyHolderTypeDesc()==null?null:personalDetails.getPolicyHolderTypeDesc());	
				}
				
				return res;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}

	@Override
	public List<String> tiraValidateCustomerDetails(UpdateCustomerDetailsReq req) {
		List<String> errorList = new ArrayList<String>();
		try {
			if (StringUtils.isBlank(req.getClientName())) {
				errorList.add("1001");
			} else if (req.getClientName().length() > 250) {
				errorList.add("1002");
			} else if (StringUtils.isNotBlank(req.getClientName()) && !req.getClientName().matches("[a-zA-Z.&() ]+")
					&& !req.getClientName().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")) {
				errorList.add("1003");
			}
			if ("1".equalsIgnoreCase(req.getPolicyHolderType())) {
				if (StringUtils.isBlank(req.getGender())) {
					errorList.add("1087");
				}
			}
			if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
				if (StringUtils.isBlank(req.getEmail1())) {
					errorList.add("1440");
				} else if (StringUtils.isNotBlank(req.getEmail1())) {
					if (req.getEmail1().length() > 100) {
						errorList.add("1032");
					} else if (StringUtils.isNotBlank(req.getEmail1())) {
						boolean b = isValidMail(req.getEmail1());
						if (b == false && (!req.getEmail1().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")
								|| !req.getEmail1().matches("^[.@]+$"))) {
							errorList.add("1033");
						}
					}
				}
			}
			
			if (StringUtils.isBlank(req.getMobileCode1())) {
				errorList.add("1062");
			}
			if (StringUtils.isBlank(req.getMobileNo1())) {
				errorList.add("1026");
			} else if (req.getMobileNo1().length() > 10 || req.getMobileNo1().length() < 8) {
				errorList.add("1027");
			} else if (!req.getMobileNo1().matches("[0-9]+")) {
				errorList.add("1028");
			} else if (req.getMobileNo1().matches("[0-9]+") && Double.valueOf(req.getMobileNo1()) <= 0) {
				errorList.add("1029");
			}
			// if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
			if (StringUtils.isBlank(req.getIdType())) {
				errorList.add("1011");
			}
			if (StringUtils.isBlank(req.getPolicyHolderTypeid())) {
				errorList.add("1012");
			}

			if (StringUtils.isBlank(req.getIdNumber())) {
				errorList.add("1013");
			} else if (req.getIdNumber().length() > 100) {
				errorList.add("1014");
			} else if (req.getIdNumber().matches("[0-9]+") && Double.valueOf(req.getIdNumber()) <= 0) {
				errorList.add("1015");
			} else if (!req.getIdNumber().matches("[a-zA-Z0-9-]+")) {
				errorList.add("1015");
			}
			if (StringUtils.isBlank(req.getCityName())) {
				errorList.add("1082");
			} else if (req.getCityName().length() > 100) {
				errorList.add("1083");
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			errorList.add("01");
			
		}
		return errorList;
	}
}
