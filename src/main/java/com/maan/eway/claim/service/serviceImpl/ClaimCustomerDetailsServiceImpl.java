package com.maan.eway.claim.service.serviceImpl;

import com.maan.eway.bean.ClaimCustomerDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.claim.service.ClaimCustomerDetailsService;
import com.maan.eway.repository.ClaimCustomerDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ClaimCustomerDetailsServiceImpl implements ClaimCustomerDetailsService {

    @Autowired
    private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepo;

    @Autowired
    private ClaimCustomerDetailsRepository claimCustomerDetailsRepository;

    @Override
    public String syncCustomer(String customerReferenceNo) {

        log.info("Sync started for CustomerReferenceNo: {}", customerReferenceNo);

        // Fetch from eservice
       EserviceCustomerDetails eserviceCustomer =
                eserviceCustomerDetailsRepo.findByCustomerReferenceNo(customerReferenceNo);

        if (eserviceCustomer == null) {
            log.warn("Customer NOT FOUND for reference: {}", customerReferenceNo);
            return "NOT_FOUND";
        }


        String polCustCode = eserviceCustomer.getPolCustCode();

        if (polCustCode == null || polCustCode.isEmpty()) {
            log.warn("polCustCode is NULL/EMPTY for reference: {}", customerReferenceNo);
            return "POL_CUST_CODE_EMPTY";
        }

        try {
            // Check if already exists by polCustCode
            ClaimCustomerDetails claim = claimCustomerDetailsRepository.findByPolCustCode(polCustCode);

            if (claim == null) {
                // CREATE new record
                claim = new ClaimCustomerDetails();
                claim.setCreatedBy(eserviceCustomer.getCreatedBy());
                claim.setEntryDate(new Date());
                log.info("Creating new ClaimCustomerDetails for polCustCode: {}", polCustCode);
            } else {
                claim.setUpdatedBy(eserviceCustomer.getUpdatedBy());
                claim.setUpdatedDate(new Date());
                log.info("Updating existing ClaimCustomerDetails for polCustCode: {}", polCustCode);
            }

            claim.setCustomerReferenceNo(eserviceCustomer.getCustomerReferenceNo());
            claim.setCompanyId(eserviceCustomer.getCompanyId());

            claim.setClientName(eserviceCustomer.getClientName());
            claim.setFirstName(eserviceCustomer.getFirstName());
            claim.setMiddleName(eserviceCustomer.getMiddleName());
            claim.setLastName(eserviceCustomer.getLastName());
            claim.setFatherName(eserviceCustomer.getFather_name());
            claim.setMotherName(eserviceCustomer.getMother_name());

            claim.setTitle(eserviceCustomer.getTitle());
            claim.setTitleDesc(eserviceCustomer.getTitleDesc());
            claim.setTitleDescLocal(eserviceCustomer.getTitleDescLocal());

            claim.setAddress1(eserviceCustomer.getAddress1());
            claim.setAddress2(eserviceCustomer.getAddress2());
            claim.setAddress3(eserviceCustomer.getAddress3());
            claim.setStreet(eserviceCustomer.getStreet());
            claim.setPinCode(eserviceCustomer.getPinCode());
            claim.setCountry(eserviceCustomer.getCountry());
            claim.setCountryName(eserviceCustomer.getCountryName());

            claim.setRegionCode(eserviceCustomer.getRegionCode());
            claim.setStateCode(eserviceCustomer.getStateCode() != null
                    ? String.valueOf(eserviceCustomer.getStateCode()) : null);
            claim.setStateName(eserviceCustomer.getStateName());
            claim.setStateNameLocal(eserviceCustomer.getStateNameLocal());
            claim.setCityCode(eserviceCustomer.getCityCode() != null
                    ? String.valueOf(eserviceCustomer.getCityCode()) : null);
            claim.setCityName(eserviceCustomer.getCityName());
            claim.setCityNameLocal(eserviceCustomer.getCityNameLocal());
            claim.setZone(eserviceCustomer.getZone());

            claim.setIdType(eserviceCustomer.getIdType());
            claim.setIdTypeDesc(eserviceCustomer.getIdTypeDesc());
            claim.setIdTypeDescLocal(eserviceCustomer.getIdTypeDescLocal());
            claim.setIdNumber(eserviceCustomer.getIdNumber());
            claim.setVrTinNo(eserviceCustomer.getVrTinNo());
            claim.setVrnGst(eserviceCustomer.getVrnGst());
            claim.setKraPin(eserviceCustomer.getKraPin());

            claim.setPolicyHolderType(eserviceCustomer.getPolicyHolderType());
            claim.setPolicyHolderTypeid(eserviceCustomer.getPolicyHolderTypeid());
            claim.setPolicyHolderTypeDesc(eserviceCustomer.getPolicyHolderTypeDesc());
            claim.setPolicyHolderTypeIdDesc(eserviceCustomer.getPolicyHolderTypeIdDesc());
            claim.setPolicyHolderTypeDescLocal(eserviceCustomer.getPolicyHolderTypeDescLocal());
            claim.setPolicyHolderTypeIdDescLocal(eserviceCustomer.getPolicyHolderTypeIdDescLocal());

            claim.setClientStatus(eserviceCustomer.getClientStatus());
            claim.setClientStatusDesc(eserviceCustomer.getClientStatusDesc());

            claim.setDobOrRegDate(eserviceCustomer.getDobOrRegDate());
            claim.setAge(eserviceCustomer.getAge());
            claim.setGender(eserviceCustomer.getGender());
            claim.setGenderDesc(eserviceCustomer.getGenderDesc());
            claim.setGenderDescLocal(eserviceCustomer.getGenderDescLocal());
            claim.setNationality(eserviceCustomer.getNationality());
            claim.setNationalityName(eserviceCustomer.getNationalityName());
            claim.setPlaceOfBirth(eserviceCustomer.getPlaceOfBirth());
            claim.setMaritalStatus(eserviceCustomer.getMaritalStatus());

            claim.setOccupation(eserviceCustomer.getOccupation());
            claim.setOccupationDesc(eserviceCustomer.getOccupationDesc());
            claim.setOccupationDescLocal(eserviceCustomer.getOccupationDescLocal());
            claim.setOtherOccupation(eserviceCustomer.getOtherOccupation());
            claim.setBusinessType(eserviceCustomer.getBusinessType());
            claim.setBusinessTypeDesc(eserviceCustomer.getBusinessTypeDesc());
            claim.setBusinessTypeDescLocal(eserviceCustomer.getBusinessTypeDescLocal());

            claim.setFax(eserviceCustomer.getFax());
            claim.setTelephoneNo1(eserviceCustomer.getTelephoneNo1());
            claim.setTelephoneNo2(eserviceCustomer.getTelephoneNo2());
            claim.setTelephoneNo3(eserviceCustomer.getTelephoneNo3());
            claim.setPhoneNoCode(eserviceCustomer.getPhoneNoCode());

            claim.setMobileCode1(eserviceCustomer.getMobileCode1());
            claim.setMobileCodeDesc1(eserviceCustomer.getMobileCodeDesc1());
            claim.setMobileCodeDesc1Local(eserviceCustomer.getMobileCodeDesc1Local());
            claim.setMobileNo1(eserviceCustomer.getMobileNo1());

            claim.setMobileCode2(eserviceCustomer.getMobileCode2());
            claim.setMobileCodeDesc2(eserviceCustomer.getMobileCodeDesc2());
            claim.setMobileCodeDesc2Local(eserviceCustomer.getMobileCodeDesc2Local());
            claim.setMobileNo2(eserviceCustomer.getMobileNo2());

            claim.setMobileCode3(eserviceCustomer.getMobileCode3());
            claim.setMobileCodeDesc3(eserviceCustomer.getMobileCodeDesc3());
            claim.setMobileCodeDesc3Local(eserviceCustomer.getMobileCodeDesc3Local());
            claim.setMobileNo3(eserviceCustomer.getMobileNo3());

            claim.setWhatsappCode(eserviceCustomer.getWhatsappCode());
            claim.setWhatsappCodeDesc(eserviceCustomer.getWhatsappCodeDesc());
            claim.setWhatsappCodeDescLocal(eserviceCustomer.getWhatsappCodeDescLocal());
            claim.setWhatsappNo(eserviceCustomer.getWhatsappNo());

            claim.setEmail1(eserviceCustomer.getEmail1());
            claim.setEmail2(eserviceCustomer.getEmail2());
            claim.setEmail3(eserviceCustomer.getEmail3());

            claim.setLanguage(eserviceCustomer.getLanguage());
            claim.setLanguageDesc(eserviceCustomer.getLanguageDesc());
            claim.setLanguageDescLocal(eserviceCustomer.getLanguageDescLocal());

            claim.setIsTaxExempted(eserviceCustomer.getIsTaxExempted());
            claim.setTaxExemptedId(eserviceCustomer.getTaxExemptedId());

            claim.setBranchCode(eserviceCustomer.getBranchCode());
            claim.setBrokerBranchCode(eserviceCustomer.getBrokerBranchCode());

            claim.setProductId(eserviceCustomer.getProductId());
            claim.setPolCustCode(eserviceCustomer.getPolCustCode());
            claim.setCustomerCode(eserviceCustomer.getCustomerCode());
            claim.setCustomerType(eserviceCustomer.getCustomerType());

            claim.setLicenseIssuedDate(eserviceCustomer.getLicenseIssuedDate());
            claim.setLicenseDuration(eserviceCustomer.getLicenseDuration());


            claim.setAreaGroup(eserviceCustomer.getAreaGroup());
            claim.setAreaClasification(eserviceCustomer.getAreaClasification());


            claim.setAppointmentDate(eserviceCustomer.getAppointmentDate());
            claim.setExpiryDate(eserviceCustomer.getExpiryDate());
            claim.setRiskAssessmentDate(eserviceCustomer.getRiskAssessmentDate());

            claim.setPreferredNotification(eserviceCustomer.getPreferredNotification());

            claim.setVipFlag(eserviceCustomer.getVipFlag());
            claim.setLeadSeqNo(eserviceCustomer.getLeadSeqNo());
            claim.setQuoteStatus(eserviceCustomer.getQuotestatus());
            claim.setStatus(eserviceCustomer.getStatus());

            claim.setCcCustType(eserviceCustomer.getCcCustType());
            claim.setCcCustCatgCode(eserviceCustomer.getCcCustCatgCode());

            claim.setComplianceStatusId(eserviceCustomer.getComplianceStatusId());
            claim.setComplianceStatus(eserviceCustomer.getComplianceStatus());
            claim.setIndustryTypeId(eserviceCustomer.getIndustryTypeId());
            claim.setIndustryType(eserviceCustomer.getIndustryType());
            claim.setWealthSourceId(eserviceCustomer.getWealthSourceId());
            claim.setWealthSource(eserviceCustomer.getWealthSource());
            claim.setLegalStructureId(eserviceCustomer.getLegalStructureId());
            claim.setLegalStructure(eserviceCustomer.getLegalStructure());
            claim.setOwnersId(eserviceCustomer.getOwnersId());
            claim.setOwners(eserviceCustomer.getOwners());

            claim.setSocioProfessionalCategory(eserviceCustomer.getSocioProfessionalCategory());
            claim.setActivities(eserviceCustomer.getActivities());
            claim.setCustomerAsInsurer(eserviceCustomer.getCustomerAsInsurer());



            claimCustomerDetailsRepository.save(claim);

            log.info("Customer SAVED/UPDATED in Claim table. polCustCode: {}", polCustCode);
            return claim.getEntryDate() != null ? "UPDATED" : "CREATED";

        } catch (Exception e) {
            log.error("ERROR while saving ClaimCustomerDetails. polCustCode: {}", polCustCode, e);
            return "ERROR";
        }
    }

    public String updateClaimCustomerFromEservice(String customerReferenceNo) {

        log.info("Update sync started for CustomerReferenceNo: {}", customerReferenceNo);

       EserviceCustomerDetails eserviceCustomer =
                eserviceCustomerDetailsRepo.findByCustomerReferenceNo(customerReferenceNo);

        if (eserviceCustomer == null) {
            log.warn("Customer NOT FOUND in eservice table for reference: {}", customerReferenceNo);
            return "NOT_FOUND_IN_ESERVICE";
        }


        ClaimCustomerDetails claim =
                claimCustomerDetailsRepository.findByCustomerReferenceNo(customerReferenceNo);

        if (claim == null) {
            log.warn("Customer NOT FOUND in claim table for reference: {}. Skipping update.", customerReferenceNo);
            return "NOT_FOUND_IN_CLAIM";
        }

        try {

            claim.setClientName(eserviceCustomer.getClientName());
            claim.setFirstName(eserviceCustomer.getFirstName());
            claim.setMiddleName(eserviceCustomer.getMiddleName());
            claim.setLastName(eserviceCustomer.getLastName());
            claim.setFatherName(eserviceCustomer.getFather_name());
            claim.setMotherName(eserviceCustomer.getMother_name());

            claim.setTitle(eserviceCustomer.getTitle());
            claim.setTitleDesc(eserviceCustomer.getTitleDesc());
            claim.setTitleDescLocal(eserviceCustomer.getTitleDescLocal());

            claim.setAddress1(eserviceCustomer.getAddress1());
            claim.setAddress2(eserviceCustomer.getAddress2());
            claim.setAddress3(eserviceCustomer.getAddress3());
            claim.setStreet(eserviceCustomer.getStreet());
            claim.setPinCode(eserviceCustomer.getPinCode());
            claim.setCountry(eserviceCustomer.getCountry());
            claim.setCountryName(eserviceCustomer.getCountryName());

            claim.setRegionCode(eserviceCustomer.getRegionCode());
            claim.setStateCode(eserviceCustomer.getStateCode() != null
                    ? String.valueOf(eserviceCustomer.getStateCode()) : null);
            claim.setStateName(eserviceCustomer.getStateName());
            claim.setStateNameLocal(eserviceCustomer.getStateNameLocal());
            claim.setCityCode(eserviceCustomer.getCityCode() != null
                    ? String.valueOf(eserviceCustomer.getCityCode()) : null);
            claim.setCityName(eserviceCustomer.getCityName());
            claim.setCityNameLocal(eserviceCustomer.getCityNameLocal());
            claim.setZone(eserviceCustomer.getZone());

            claim.setIdType(eserviceCustomer.getIdType());
            claim.setIdTypeDesc(eserviceCustomer.getIdTypeDesc());
            claim.setIdTypeDescLocal(eserviceCustomer.getIdTypeDescLocal());
            claim.setIdNumber(eserviceCustomer.getIdNumber());
            claim.setVrTinNo(eserviceCustomer.getVrTinNo());
            claim.setVrnGst(eserviceCustomer.getVrnGst());
            claim.setKraPin(eserviceCustomer.getKraPin());

            claim.setPolicyHolderType(eserviceCustomer.getPolicyHolderType());
            claim.setPolicyHolderTypeid(eserviceCustomer.getPolicyHolderTypeid());
            claim.setPolicyHolderTypeDesc(eserviceCustomer.getPolicyHolderTypeDesc());
            claim.setPolicyHolderTypeIdDesc(eserviceCustomer.getPolicyHolderTypeIdDesc());
            claim.setPolicyHolderTypeDescLocal(eserviceCustomer.getPolicyHolderTypeDescLocal());
            claim.setPolicyHolderTypeIdDescLocal(eserviceCustomer.getPolicyHolderTypeIdDescLocal());

            claim.setClientStatus(eserviceCustomer.getClientStatus());
            claim.setClientStatusDesc(eserviceCustomer.getClientStatusDesc());

            claim.setDobOrRegDate(eserviceCustomer.getDobOrRegDate());
            claim.setAge(eserviceCustomer.getAge());
            claim.setGender(eserviceCustomer.getGender());
            claim.setGenderDesc(eserviceCustomer.getGenderDesc());
            claim.setGenderDescLocal(eserviceCustomer.getGenderDescLocal());
            claim.setNationality(eserviceCustomer.getNationality());
            claim.setNationalityName(eserviceCustomer.getNationalityName());
            claim.setPlaceOfBirth(eserviceCustomer.getPlaceOfBirth());
            claim.setMaritalStatus(eserviceCustomer.getMaritalStatus());

            claim.setOccupation(eserviceCustomer.getOccupation());
            claim.setOccupationDesc(eserviceCustomer.getOccupationDesc());
            claim.setOccupationDescLocal(eserviceCustomer.getOccupationDescLocal());
            claim.setOtherOccupation(eserviceCustomer.getOtherOccupation());
            claim.setBusinessType(eserviceCustomer.getBusinessType());
            claim.setBusinessTypeDesc(eserviceCustomer.getBusinessTypeDesc());
            claim.setBusinessTypeDescLocal(eserviceCustomer.getBusinessTypeDescLocal());

            claim.setFax(eserviceCustomer.getFax());
            claim.setTelephoneNo1(eserviceCustomer.getTelephoneNo1());
            claim.setTelephoneNo2(eserviceCustomer.getTelephoneNo2());
            claim.setTelephoneNo3(eserviceCustomer.getTelephoneNo3());
            claim.setPhoneNoCode(eserviceCustomer.getPhoneNoCode());

            claim.setMobileCode1(eserviceCustomer.getMobileCode1());
            claim.setMobileCodeDesc1(eserviceCustomer.getMobileCodeDesc1());
            claim.setMobileCodeDesc1Local(eserviceCustomer.getMobileCodeDesc1Local());
            claim.setMobileNo1(eserviceCustomer.getMobileNo1());

            claim.setMobileCode2(eserviceCustomer.getMobileCode2());
            claim.setMobileCodeDesc2(eserviceCustomer.getMobileCodeDesc2());
            claim.setMobileCodeDesc2Local(eserviceCustomer.getMobileCodeDesc2Local());
            claim.setMobileNo2(eserviceCustomer.getMobileNo2());

            claim.setMobileCode3(eserviceCustomer.getMobileCode3());
            claim.setMobileCodeDesc3(eserviceCustomer.getMobileCodeDesc3());
            claim.setMobileCodeDesc3Local(eserviceCustomer.getMobileCodeDesc3Local());
            claim.setMobileNo3(eserviceCustomer.getMobileNo3());

            claim.setWhatsappCode(eserviceCustomer.getWhatsappCode());
            claim.setWhatsappCodeDesc(eserviceCustomer.getWhatsappCodeDesc());
            claim.setWhatsappCodeDescLocal(eserviceCustomer.getWhatsappCodeDescLocal());
            claim.setWhatsappNo(eserviceCustomer.getWhatsappNo());

            claim.setEmail1(eserviceCustomer.getEmail1());
            claim.setEmail2(eserviceCustomer.getEmail2());
            claim.setEmail3(eserviceCustomer.getEmail3());

            claim.setLanguage(eserviceCustomer.getLanguage());
            claim.setLanguageDesc(eserviceCustomer.getLanguageDesc());
            claim.setLanguageDescLocal(eserviceCustomer.getLanguageDescLocal());

            claim.setIsTaxExempted(eserviceCustomer.getIsTaxExempted());
            claim.setTaxExemptedId(eserviceCustomer.getTaxExemptedId());

            claim.setBranchCode(eserviceCustomer.getBranchCode());
            claim.setBrokerBranchCode(eserviceCustomer.getBrokerBranchCode());

            claim.setProductId(eserviceCustomer.getProductId());
            claim.setPolCustCode(eserviceCustomer.getPolCustCode());
            claim.setCustomerCode(eserviceCustomer.getCustomerCode());
            claim.setCustomerType(eserviceCustomer.getCustomerType());

            claim.setLicenseIssuedDate(eserviceCustomer.getLicenseIssuedDate());
            claim.setLicenseDuration(eserviceCustomer.getLicenseDuration());

            claim.setAreaGroup(eserviceCustomer.getAreaGroup());
            claim.setAreaClasification(eserviceCustomer.getAreaClasification());

            claim.setAppointmentDate(eserviceCustomer.getAppointmentDate());
            claim.setExpiryDate(eserviceCustomer.getExpiryDate());
            claim.setRiskAssessmentDate(eserviceCustomer.getRiskAssessmentDate());

            claim.setPreferredNotification(eserviceCustomer.getPreferredNotification());

            claim.setVipFlag(eserviceCustomer.getVipFlag());
            claim.setLeadSeqNo(eserviceCustomer.getLeadSeqNo());
            claim.setQuoteStatus(eserviceCustomer.getQuotestatus());
            claim.setStatus(eserviceCustomer.getStatus());


            claim.setCcCustType(eserviceCustomer.getCcCustType());
            claim.setCcCustCatgCode(eserviceCustomer.getCcCustCatgCode());

            claim.setComplianceStatusId(eserviceCustomer.getComplianceStatusId());
            claim.setComplianceStatus(eserviceCustomer.getComplianceStatus());
            claim.setIndustryTypeId(eserviceCustomer.getIndustryTypeId());
            claim.setIndustryType(eserviceCustomer.getIndustryType());
            claim.setWealthSourceId(eserviceCustomer.getWealthSourceId());
            claim.setWealthSource(eserviceCustomer.getWealthSource());
            claim.setLegalStructureId(eserviceCustomer.getLegalStructureId());
            claim.setLegalStructure(eserviceCustomer.getLegalStructure());
            claim.setOwnersId(eserviceCustomer.getOwnersId());
            claim.setOwners(eserviceCustomer.getOwners());

            // ── Socio / Activities ─────────────────────────────────────────────────────
            claim.setActivities(eserviceCustomer.getActivities());
            claim.setCustomerAsInsurer(eserviceCustomer.getCustomerAsInsurer());

            claim.setUpdatedBy(eserviceCustomer.getUpdatedBy());
            claim.setUpdatedDate(new Date());

            claimCustomerDetailsRepository.save(claim);

            log.info("ClaimCustomerDetails UPDATED successfully for CustomerReferenceNo: {}", customerReferenceNo);
            return "UPDATED";

        } catch (Exception e) {
            log.error("ERROR while updating ClaimCustomerDetails for CustomerReferenceNo: {}", customerReferenceNo, e);
            return "ERROR";
        }
    }
}