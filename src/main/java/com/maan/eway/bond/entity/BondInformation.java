package com.maan.eway.bond.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bond_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BondInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_REFERENCE_NO")
	private String requestReferenceNo;

	@Column(name = "QUOTE_NO")
	private String quoteNo;

	@Column(name = "PRODUCT_ID")
	private String productId;

	@Column(name = "COMPANY_ID")
	private String companyId;

	@Column(name = "LOCATION_ID")
	private String locationId;

	@Column(name = "PRINCIPAL_NAME")
	private String principalName;

	@Column(name = "BUSINESS_REGN_NO")
	private String businessRegnNo;

	@Column(name = "TYPE_OF_BUSINESS")
	private String typeOfBusiness;

	@Column(name = "INDUSTRY")
	private String industry;

	@Column(name = "YEARS_BUSINESS")
	private Integer yearsBusiness;

	@Column(name = "PROJECT_NAME")
	private String projectName;

	@Column(name = "PROJECT_DESC")
	private String projectDesc;

	@Column(name = "PROJECT_ADDRESS")
	private String projectAddress;

	@Column(name = "PROJECT_LOCATION")
	private String projectLocation;

	@Column(name = "NAME_OF_OBLIGEE")
	private String nameOfObligee;

	@Column(name = "PROJECT_OWNER_TYPE")
	private String projectOwnerType;

	@Column(name = "PROJECT_OWNER_TYPE_DESC")
	private String projectOwnerTypeDesc;

	@Column(name = "CONTRACT_NO")
	private String contractNo;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PROJECT_START_DATE")
	private Date projectStartDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PROJECT_END_DATE")
	private Date projectEndDate;

	@Column(name = "TOTAL_CONTRACT_VALUE")
	private Integer totalContractValue;

	@Column(name = "LIQUIDATE_DAMAGES_CLAUSE")
	private String liquidateDamagesClause;

	@Column(name = "LIQUIDATE_DAMAGES_CLAU_DESC")
	private String liquidateDamagesClauDesc;

	@Column(name = "ADVANCE_PAYMENT_CLAUSE")
	private String advancePaymentClause;

	@Column(name = "ADVANCE_PAYMENT_CLAU_DESC")
	private String advancePaymentClauseDesc;

	@Column(name = "ADVANCE_PAYMENT_VALUE")
	private Integer advancePaymentValue;

	@Column(name = "BOND_DURATION")
	private String bondDuration;

	@Column(name = "FORM_OF_BOND")
	private String formOfBond;

	@Column(name = "FORM_OF_BOND_DESC")
	private String formOfBondDesc;

	@Column(name = "WORDING_PROVIDER")
	private String wordingProvider;

	@Column(name = "WORDING_PROVIDER_DESC")
	private String wordingProviderDesc;

	@Column(name = "JUSTIFICATION")
	private String jurdication;

	@Column(name = "GOVERNING_LAW")
	private String governingLaw;

	@Column(name = "COLLATERAL_FIXED_DEPOST")
	private Integer collateralFixedDepost;

	@Column(name = "COLLATERAL_PROPERTY")
	private Integer collateralProperty;

	@Column(name = "COLLATERAL_CORPORATE")
	private Integer collateralCorporate;

	@Column(name = "COLLATERAL_PERSONAL")
	private Integer collateralPersonal;

	@Column(name = "ENDORSEMENT_YN")
	private String endorsementYn;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@Column(name = "ENDORSEMENT_DATE")
	private Date endorsementDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@Column(name = "ENDORSEMENT_EFFECTIVE_DATE")
	private Date endorsementEffectiveDate;

	@Column(name = "ENDORSEMENT_REMARKS")
	private String endorsementRemarks;

	@Column(name = "ENDORSEMENT_TYPE")
	private String endorsementType;

	@Column(name = "ENDORSEMENT_TYPE_DESC")
	private String endorsementTypeDesc;

	@Column(name = "ENDT_CATEGORY_DESC")
	private String endtCategoryDesc;

	@Column(name = "ENDT_COUNT")
	private String endtCount;

	@Column(name = "ENDT_PREV_POLICY_NO")
	private String endtPrevPolicyNo;

	@Column(name = "ENDT_PREV_QUOTE_NO")
	private String endtPrevQuoteNo;

	@Column(name = "ENDT_STATUS")
	private String endtStatus;

	@Column(name = "IS_FINANCE_ENDT")
	private String isFinanceEndt;

	@Column(name = "ORGINAL_POLICY_NO")
	private String orginalPolicyNo;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@Column(name = "ENTRY_DATE")
	private Date entryDate;

	@Column(name = "LOGIN_ID")
	private String loginId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "REMARKS")
	private String remarks;

}


