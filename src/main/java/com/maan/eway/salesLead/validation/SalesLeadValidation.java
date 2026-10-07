package com.maan.eway.salesLead.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.error.Error;
import com.maan.eway.salesLead.req.EserviceLeadSaveReq;
import com.maan.eway.salesLead.req.InsertSalesReq;
import com.maan.eway.salesLead.req.LeadContactPersonReq;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SalesLeadValidation {
	
	@PersistenceContext
	private EntityManager em;

	public List<Error> insertLeadContactVali(List<InsertSalesReq> req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			for(int i=0;i<req.size();i++) {
				if(StringUtils.isBlank(req.get(i).getClientName())) {
					errors.add(new Error("01", "ClientName", "ClientName is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getTitle())) {
					errors.add(new Error("02", "ClientCode", "ClientCode is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getStateCode())) {
					errors.add(new Error("05", "State", "State is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getGstIdentificationNo())) {
					errors.add(new Error("07", "GST Identification Number", "GST Identification Number is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getBranchCode())) {
					errors.add(new Error("08", "RSA Branch", "RSA Branch is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getLeadCreatedOn())) {
					errors.add(new Error("09", "Lead Created On", "Lead Created On is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getIntermediateId())) {
					errors.add(new Error("10", "Intermediary Code", "Intermediary Code is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getIntermediateName())) {
					errors.add(new Error("11", "Intermediary Name", "Intermediary Name is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getChannelId())) {
					errors.add(new Error("12", "Channel", "Channel is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getSectionTypeId())) {
					errors.add(new Error("13", "Section Type", "Section Type is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getPropobabilityOfSuccessId())) {
					errors.add(new Error("14", "Probability Of Success", "Probability Of Success  is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getTypeOfBusinessId())) {
					errors.add(new Error("15", "Type of Business", "Type of Business is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getCurrentInsurer())) {
					errors.add(new Error("16", "Current Insurer", "Current Insurer is Required"));
				}
				
				if(req.get(i).getLeadContactPersonReq()!=null && req.get(i).getLeadContactPersonReq().size()>0) {
					for(int j=0;j<req.get(i).getLeadContactPersonReq().size();j++) {
						LeadContactPersonReq k = req.get(i).getLeadContactPersonReq().get(j);
						if(StringUtils.isBlank(k.getContactType())) {
							errors.add(new Error("01", "ContactType", "ContactType is Required"));
						}
						if(StringUtils.isBlank(k.getContactPersonName())) {
							errors.add(new Error("02", "Contact Person Name", "Contact Person Name is Required"));
						}
						if(StringUtils.isBlank(k.getEmailAddress())) {
							errors.add(new Error("03", "Email Address", "Email Address  is Required"));
						}
						if(StringUtils.isBlank(k.getMobileNo())) {
							errors.add(new Error("04", "Mobile No", "Mobile No is Required"));
						}
						if(StringUtils.isBlank(k.getPhoneNo())) {
							errors.add(new Error("05", "Phone No", "Phone No is Required"));
						}
						if(StringUtils.isBlank(k.getDesignation())) {
							errors.add(new Error("06", "Designation", "Designation is Required"));
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return errors;
	}

	public List<String> validateCustomerDetails(EserviceLeadSaveReq req) {
		List<String> errorList = new ArrayList<String>();
		try {
			if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {
				
				if (StringUtils.isBlank(req.getTitle()))  {
					errorList.add("1047");
				}
				if (StringUtils.isBlank(req.getClientName()) ) {
					errorList.add("1001");
				} else if (req.getClientName().length() > 250) {
				   errorList.add("1002");
				} 
				else if (StringUtils.isNotBlank(req.getClientName())&& !req.getClientName().matches("[a-zA-Z.&() ]+") && !req.getClientName().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")){
					errorList.add("1003");		
				}
				if("1".equalsIgnoreCase(req.getPolicyHolderType())) {
					if(StringUtils.isBlank(req.getGender())) {
						errorList.add("1087");
					}
				}
				if (StringUtils.isBlank(req.getOccupation()) ) {
					errorList.add("1022");
				} else if(req.getOccupation().equalsIgnoreCase("99999")){
					if (StringUtils.isBlank(req.getOtherOccupation()) ) {
						errorList.add("1023");
					}else if (req.getOtherOccupation().length() > 100){
						errorList.add("1024"); 
					}else if(!req.getOtherOccupation().matches("[a-zA-Z\\s]+")){
						errorList.add("1025");
					}
				}
				if("2".equalsIgnoreCase(req.getPolicyHolderType())) {
					if ( StringUtils.isBlank(req.getEmail1()) ) {
						errorList.add("1440");
					}else if ( StringUtils.isNotBlank(req.getEmail1()) ) {
						if( req.getEmail1().length() > 100 ) {
							errorList.add("1032");
						} else if(StringUtils.isNotBlank(req.getEmail1())) {
							boolean b = isValidMail(req.getEmail1());
							if (b == false && (!req.getEmail1().matches("^[a-zA-ZÀ-ÿ\\s'-]+$") || !req.getEmail1().matches("^[.@]+$"))) {
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
				} else if (req.getMobileNo1().length() > 10||req.getMobileNo1().length() < 8) {
					errorList.add("1027");
				} else if (!req.getMobileNo1().matches("[0-9]+") ) {
					errorList.add("1028");
				} else if (req.getMobileNo1().matches("[0-9]+") && Double.valueOf(req.getMobileNo1()) <=0 ) {
					errorList.add("1029");
				}
				//if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
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
				}  else if (req.getIdNumber().matches("[0-9]+") && Double.valueOf(req.getIdNumber()) <=0 ) {
					errorList.add("1015");
				} else if(!req.getIdNumber().matches("[a-zA-Z0-9-]+")) {	
					errorList.add("1015");
				}
					
				//}
				if (StringUtils.isBlank(req.getPreferredNotification())) {
					errorList.add("1049");
				}
				if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
					if(StringUtils.isBlank(req.getVrTinNo())) {
						errorList.add("1051");
					}else if (req.getVrTinNo().length() > 20) {
						errorList.add("1052");
					}
					if(StringUtils.isBlank(req.getStreet()) ) {
						errorList.add("3310");
					}else if (req.getStreet().length() > 100) {
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
				
				if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
						if (StringUtils.isBlank(req.getBusinessType())) {
							//errorList.add("1050");
						}
					}
				}
				
				
				if (StringUtils.isBlank(req.getStatus())) {
					errorList.add("1058");
				} else if (req.getStatus().length() > 1) {
					errorList.add("1059");
				} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())|| "P".equals(req.getStatus()))) {
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
					//	&& (StringUtils.isNotBlank(req.getLanguageDesc()))
						&& (StringUtils.isNotBlank(req.getMobileNo1()))
					//	&& (StringUtils.isNotBlank(req.getMobileNo2()))
					//	&& (StringUtils.isNotBlank(req.getMobileNo3()))
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
					Predicate n3 = (cb.like(cb.lower(b.get("branchCode")), req.getBranchCode().toLowerCase()));
					Predicate n5 =	(cb.equal(b.get("cityCode") ,  null != req.getCityCode() && 
							req.getCityCode().matches("[0-9]+") ? Integer.valueOf(req.getCityCode()) : 0 ));
					Predicate n6 = (cb.like(cb.lower(b.get("cityName")), req.getCityName().toLowerCase()));
					Predicate n7 = (cb.like(cb.lower(b.get("clientName")), req.getClientName().toLowerCase()));
					Predicate n8 = (cb.like(cb.lower(b.get("clientStatus")), req.getClientStatus().toLowerCase()));
					Predicate n9 = (cb.like(cb.lower(b.get("companyId")), req.getCompanyId().toLowerCase()));
					Predicate n10 = (cb.like(cb.lower(b.get("createdBy")), req.getCreatedBy().toLowerCase()));
					Predicate n12 = (cb.equal(b.get("dobOrRegDate"), req.getDobOrRegDate()));
					Predicate n13 = (cb.like(cb.lower(b.get("email1")), req.getEmail1().toLowerCase()));
					Predicate n17 = (cb.like(cb.lower(b.get("gender")), req.getGender().toLowerCase()));
					Predicate n18 = (cb.like(cb.lower(b.get("idNumber")), req.getIdNumber().toLowerCase()));
					Predicate n19 = (cb.like(cb.lower(b.get("isTaxExempted")), req.getIsTaxExempted().toLowerCase()));
					Predicate n22 = (cb.equal(b.get("mobileNo1"), req.getMobileNo1()));
					Predicate n26 = (cb.like(cb.lower(b.get("occupation")), req.getOccupation().toLowerCase()));
					Predicate n27 = (cb.like(cb.lower(b.get("placeOfBirth")), req.getPlaceOfBirth().toLowerCase()));
					Predicate n28 = (cb.like(cb.lower(b.get("policyHolderType")),
							req.getPolicyHolderType().toLowerCase()));
					
					Predicate n30 = (cb.equal(b.get("productId") ,  null != req.getProductId() && 
							req.getProductId().matches("[0-9]+") ? Integer.valueOf(req.getProductId()) : 0 ));
					Predicate n31 = (cb.like(cb.lower(b.get("regionCode")), req.getRegionCode().toLowerCase()));
					Predicate n33 = (cb.like(cb.lower(b.get("stateName")), req.getStateName().toLowerCase()));
					Predicate n34 = (cb.like(cb.lower(b.get("status")), req.getStatus().toLowerCase()));
					Predicate n42 = (cb.like(cb.lower(b.get("preferredNotification")), req.getPreferredNotification().toLowerCase()));

					query.where(n1,  n3,  n6, n7, n8, n9, n10,
							// n11,
							n12, n13,  n17, n18, n19,n22, n26, n27, n28,
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

}
