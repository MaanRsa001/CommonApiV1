package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.CertificateDetailsReq;
import com.maan.eway.common.req.GetMachineryContentReq;
import com.maan.eway.common.req.GetOccupationsReq;
import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.common.res.CertificateTypeRes;
import com.maan.eway.common.res.GetMachineryContentRes;
import com.maan.eway.integration.req.QueryKeyReq;
import com.maan.eway.master.req.BrokerSumInsuredRefReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.req.LovPolicyDropDownReq;
import com.maan.eway.master.req.MotDropdownReq;
import com.maan.eway.master.req.PlanTypeReq;
import com.maan.eway.master.req.RelationDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.MotorWithAccessoriesRes;

public interface DropDownService {

	
	
	List<DropDownRes> coverNoteType(LovDropDownReq req);

	List<DropDownRes> paymentmode(LovDropDownReq req);

	List<DropDownRes> endorsementtype(LovDropDownReq req);

	List<DropDownRes> discounttypeoffered(LovDropDownReq req);

	List<DropDownRes> taxexcempted(LovDropDownReq req);

	List<DropDownRes> taxexcemptiontype(LovDropDownReq req);

	List<DropDownRes> policyholdertype(LovDropDownReq req);

	List<DropDownRes> policyholderidtype(LovPolicyDropDownReq req);

	List<DropDownRes> policyholdergender(LovDropDownReq req);

	List<DropDownRes> nametitle(LovDropDownReq req);

	List<DropDownRes> notificationtype(LovDropDownReq req);

	List<DropDownRes> getMotorCategory(LovDropDownReq req);

	List<DropDownRes> getMotorType(LovDropDownReq req);

	//List<DropDownRes> getMotorUsage(LovDropDownReq req);

	List<DropDownRes> ownerCategory(LovDropDownReq req);

	List<DropDownRes> fleetType(LovDropDownReq req);

	List<DropDownRes> reinsuranceCategory(LovDropDownReq req);

	List<DropDownRes> participantType(LovDropDownReq req);

	List<DropDownRes> reinsuranceForm(LovDropDownReq req);

	List<DropDownRes> reinsuranceType(LovDropDownReq req);

	List<DropDownRes> claimformdullyfilled(LovDropDownReq req);

	List<DropDownRes> lostassessmentoption(LovDropDownReq req);

	List<DropDownRes> assessoridtype(LovDropDownReq req);

	List<DropDownRes> claimantCategory(LovDropDownReq req);

	List<DropDownRes> claimantType(LovDropDownReq req);

	List<DropDownRes> claimantIdType(LovDropDownReq req);

	List<DropDownRes> isreassessment(LovDropDownReq req);

	List<DropDownRes> offerAccepted(LovDropDownReq req);

	List<DropDownRes> partiesNotified(LovDropDownReq req);

	List<DropDownRes> claimResultedLitigation(LovDropDownReq req);

	List<DropDownRes> tonnage(LovDropDownReq req);

	List<DropDownRes> getNcdDetails(NcdDetailsGetReq req);

	List<DropDownRes> insuranceType(LovDropDownReq req);

	List<DropDownRes> insuranceClass(LovDropDownReq req);

	List<DropDownRes> title(LovDropDownReq req);

	List<DropDownRes> borrowerType(LovDropDownReq req);

	List<DropDownRes> cityLimit(LovDropDownReq req);

	List<DropDownRes> getLanguage(LovDropDownReq req);

	List<DropDownRes> getFuelType(LovDropDownReq req);

//	List<ColummnDropRes> getTableDetails(LovDropDownReq req);

	List<DropDownRes> constructType(LovDropDownReq req);

	List<DropDownRes> consecutiveDays(LovDropDownReq req);

	List<DropDownRes> buildingType(LovDropDownReq req);

	List<DropDownRes> getPlanType(PlanTypeReq req);

	List<DropDownRes> getSourceType(LovDropDownReq req);

	List<DropDownRes> getRelationType(RelationDropDownReq req);

	List<DropDownRes> getBuildingPurpose(LovDropDownReq req);

	List<DropDownRes> getBuildingUsage(LovDropDownReq req);

	List<DropDownRes> getPaymentType(LovDropDownReq req);

	List<DropDownRes> getContent(LovDropDownReq req);

	List<DropDownRes> getPersonal(LovDropDownReq req);

	List<DropDownRes> getcontentrisk(LovDropDownReq req);

	List<DropDownRes> getallrisk(LovDropDownReq req);

	List<DropDownRes> datatype(LovDropDownReq req);

	List<DropDownRes> termsandcondition(LovDropDownReq req);

	List<DropDownRes> categoryid(LovDropDownReq req);

	List<DropDownRes> benefitcovermonth(LovDropDownReq req);

	List<DropDownRes> quoteStatus(LovDropDownReq req);

	List<DropDownRes> termsType(LovDropDownReq req);

	List<DropDownRes> driverType(LovDropDownReq req);

	List<DropDownRes> getallelectronicItems(LovDropDownReq req);

	List<DropDownRes> getQueryKeyColumns(QueryKeyReq req);

	List<DropDownRes> industryCategory(LovDropDownReq req);

	List<DropDownRes> followupDetailsStatus(LovDropDownReq req);

	List<DropDownRes> endtDependantFields(LovDropDownReq req);

	List<DropDownRes> productBenefitsTypes(LovDropDownReq req);

	List<DropDownRes> brokerSumInsuredRefrral(BrokerSumInsuredRefReq req);

	List<DropDownRes> getWallTypes(LovDropDownReq req);

	List<DropDownRes> getRoofTypes(LovDropDownReq req);

	List<DropDownRes> getAuditentType(LovDropDownReq req);

	List<DropDownRes> getFidelityEmployeeCount(LovDropDownReq req);

	List<DropDownRes> getFidelitySuminsured(LovDropDownReq req);

	List<DropDownRes> getNatureOfTrade(LovDropDownReq req);

	List<DropDownRes> getBurglaryInsuranceFor(LovDropDownReq req);

	List<DropDownRes> getCeilingType(LovDropDownReq req);

	List<DropDownRes> getWindowsMaterials(LovDropDownReq req);

	List<DropDownRes> getDoorsMaterials(LovDropDownReq req);

	List<DropDownRes> getNightLeftDoor(LovDropDownReq req);

	List<DropDownRes> getBuildingOccupied(LovDropDownReq req);

	List<DropDownRes> getOpenoption(LovDropDownReq req);

	List<DropDownRes> getFirstLossPercent(LovDropDownReq req);

	List<DropDownRes> getIndemity(LovDropDownReq req);

	List<DropDownRes> getPlateGlass(LovDropDownReq req);

	List<DropDownRes> getOccupations(GetOccupationsReq req);

	List<DropDownRes> getMotorContent(LovDropDownReq req);


	GetMachineryContentRes getMachineryContent(GetMachineryContentReq req);

	List<DropDownRes> getSectionModifyType(LovDropDownReq req);

	List<DropDownRes> getTaxFor(LovDropDownReq req);

	List<DropDownRes> getPaymentFor(LovDropDownReq req);

	List<DropDownRes> getAdminPortFolioTypes(LovDropDownReq req);

	List<DropDownRes> getPlantAllRisk(LovDropDownReq req);

	List<DropDownRes> getBusinessAllRisk(LovDropDownReq req);

	List<DropDownRes> getPlanBenefits(LovDropDownReq req);

	List<DropDownRes> getCyberInsuranceTypes(LovDropDownReq req);

	List<DropDownRes> getCyberContents(LovDropDownReq req);

	List<DropDownRes> getTaxForDesc(LovDropDownReq req);

	List<MotorWithAccessoriesRes> getMotAccDropdown(MotDropdownReq req);

	List<DropDownRes> getPremiaSourceTypes(LovDropDownReq req);

	List<DropDownRes> lifePaymentTerms(LovDropDownReq req);

	List<DropDownRes> lifePolicyTerms(LovDropDownReq req);

	List<DropDownRes> buildingPropertyTypes(LovDropDownReq req);
	List<DropDownRes> getReportBuissnessTypes(LovDropDownReq req);

	List<DropDownRes> modeOfTransPort(LovDropDownReq req);

	List<DropDownRes> geographicalCoverage(LovDropDownReq req);

	List<DropDownRes> transportedBy(LovDropDownReq req);
	
	List<DropDownRes> errormodules(LovDropDownReq req);

	List<DropDownRes> vehicleClasses(LovDropDownReq req);

	List<DropDownRes> maritalStatus(LovDropDownReq req);

	List<DropDownRes> quotePeriod(LovDropDownReq req);

	List<DropDownRes> ratingRelationType(LovDropDownReq req);

	List<DropDownRes> claimType(LovDropDownReq req);

	List<DropDownRes> brokerlist(String companyId);

	List<DropDownRes> professionalType(LovDropDownReq req);

	List<DropDownRes> indemnityType(LovDropDownReq req);

	List<DropDownRes> characterType(LovDropDownReq req);

	List<DropDownRes> policyEndDateList(String policyStartDate);
	
	List<DropDownRes> socioProfessionalCategory(LovDropDownReq req);
	
	List<DropDownRes> municipalityTraffic(LovDropDownReq req);
	
	List<DropDownRes> aggregatedValue(LovDropDownReq req);

	List<DropDownRes> policyTypeReferral(LovDropDownReq req);

	CertificateTypeRes getcertificateType(CertificateDetailsReq req);

	CertificateTypeRes getBookIds();

	CertificateTypeRes getcertificateNo(CertificateDetailsReq req);

	List<DropDownRes> VehicleAgeReferral(LovDropDownReq req);
	
	List<DropDownRes> issuerSumInsuredRefrral(BrokerSumInsuredRefReq req);
	
	List<DropDownRes> nonMotorSumInsuredRefrral(BrokerSumInsuredRefReq req);

}
