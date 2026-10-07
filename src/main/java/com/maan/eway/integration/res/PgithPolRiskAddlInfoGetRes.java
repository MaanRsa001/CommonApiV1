package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PgithPolRiskAddlInfoGetRes {
	
	@JsonProperty("QuotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("RiskId")
    private String     riskId ;
    
	@JsonProperty("PraiCode23")
    private String     praiCode23;

	@JsonProperty("PraiCode24")
    private String     praiCode24;
    
	@JsonProperty("PraiData01")
    private String     praiData01 ;

	@JsonProperty("PraiData03")
    private String     praiData03 ;
    
	@JsonProperty("PraihSysId")
    private String praihSysId ;
    
	@JsonProperty("PraihPolSysId")
    private String praihPolSysId ;
    
	@JsonProperty("PraihEndNoIdx")
    private String praihEndNoIdx ;

	@JsonProperty("PraiEndSrNo")
    private String praiEndSrNo ;
    
	@JsonProperty("PraiPsecSysId")
    private String praiPsecSysId ;

	@JsonProperty("PraiRiskLvlNo")
    private String praiRiskLvlNo ;

	@JsonProperty("PraihLvl1SysId")
    private String praihLvl1SysId ;
    
	@JsonProperty("PraiLvl1SrNo")
    private String     praiLvl1SrNo ;

	@JsonProperty("PraihLvl2SysId")
    private String     praihLvl2SysId ;

	@JsonProperty("PraihLvl2SrNo")
    private String     praihLvl2SrNo ;
    
	@JsonProperty("PraiLvl3SysId")
    private String     praiLvl3SysId ;

	@JsonProperty("PraiLvl3SrNo")
    private String     praiLvl3SrNo ;
    
	@JsonProperty("PraiLvl4SysId")
    private String     praiLvl4SysId ;

	@JsonProperty("PraiLvl4SrNo")
    private String     praiLvl4SrNo ;
    
	@JsonProperty("PraiLvl5SysId")
    private String     praiLvl5SysId ;

	@JsonProperty("PraiLvl5SrNo")
    private String     praiLvl5SrNo ;
    
	@JsonProperty("PraiSiCurrCode")
    private String     praiSiCurrCode ;

	@JsonProperty("PraiPremCurrCode")
    private String     praiPremCurrCode ;

	@JsonProperty("PraiSiFc")
    private String praiSiFc ;

	@JsonProperty("PraiSiLc1")
    private String praiSiLc1 ;
    
	@JsonProperty("PraiSiLc2")
    private String praiSiLc2 ;
    
	@JsonProperty("PraiSiLc3")
    private String praiSiLc3 ;

	@JsonProperty("PraiOrgSiFc")
    private String     praiOrgSiFc ;
    
    @JsonProperty("PraiOrgSiLc1")
    private String praiOrgSiLc1 ;

    @JsonProperty("PraiOrgSiLc2")
    private String praiOrgSiLc2 ;
    
    @JsonProperty("PraiOrgSiLc3")
    private String praiOrgSiLc3 ;
    
    @JsonProperty("PraiPremFc")
    private String praiPremFc ;   
    
    @JsonProperty("PraiPremLc1")
    private String praiPremLc1 ;
    
    @JsonProperty("PraiPremLc2")
    private String praiPremLc2 ;
    
    @JsonProperty("PraiPremLc3")
    private String praiPremLc3 ;
    
    @JsonProperty("PraiOrgPremFc")
    private String praiOrgPremFc ;
    
    @JsonProperty("PraiOrgPremLc1")
    private String praiOrgPremLc1 ;
    
    @JsonProperty("PraiOrgPremLc2")
    private String praiOrgPremLc2 ;
    
    @JsonProperty("PraiOrgPremLc3")
    private String praiOrgPremLc3 ; 
    
    @JsonProperty("PraiCumBonusPerc")
    private String praiCumBonusPerc ;
    
    @JsonProperty("PraiCumBonusFc")
    private String praiCumBonusFc ;
    
    @JsonProperty("PraiCumBonusLc1")
    private String praiCumBonusLc1 ;
    
    @JsonProperty("PraiCumBonusLc2")
    private String praiCumBonusLc2 ;
    
    @JsonProperty("PraiCumBonusLc3")
    private String praiCumBonusLc3 ;
    
    @JsonProperty("PraiSilentYn")
    private String praiSilentYn ;
 
    @JsonProperty("PraiSilentFmDt")
    private String praiSilentFmDt ;
    
    @JsonProperty("PraiSilentToDt")
    private String praiSilentToDt ;
    
    @JsonProperty("PraiServiceTaxYn")
    private String praiServiceTaxYn ;
    
    @JsonProperty("PraiRecType")
    private String praiRecType ;
   
    @JsonProperty("PraiEffFmDt")
    private String praiEffFmDt ;
 
    @JsonProperty("PraiEffToDt")
    private String praiEffToDt ;
 
    @JsonProperty("PraiEndEffFmDt")
    private String praiEndEffFmDt ;
    
    @JsonProperty("PraiCode01")
    private String praiCode01 ;
    
    @JsonProperty("PraiCode02")
    private String praiCode02 ;
    
    @JsonProperty("PraiCode03")
    private String praiCode03 ;
    
    @JsonProperty("PraiCode04")
    private String praiCode04 ;
    
    @JsonProperty("PraiCode05")
    private String praiCode05 ;
    
    @JsonProperty("PraiCode06")
    private String praiCode06 ;
    
    @JsonProperty("PraiCode07")
    private String praiCode07 ;
    
    @JsonProperty("PraiCode08")
    private String praiCode08 ;
    
    @JsonProperty("PraiCode09")
    private String praiCode09 ;
    
    @JsonProperty("PraiCode10")
    private String praiCode10 ;
    
    @JsonProperty("PraiCode11")
    private String praiCode11 ;
    
    @JsonProperty("PraiCode12")
    private String praiCode12 ;
    
    @JsonProperty("PraiCode13")
    private String praiCode13 ;
    
    @JsonProperty("PraiCode14")
    private String praiCode14 ;
    
    @JsonProperty("PraiCode15")
    private String praiCode15 ;
    
    @JsonProperty("PraiCode16")
    private String praiCode16 ;
    
    @JsonProperty("PraiCode17")
    private String praiCode17 ;
    
    @JsonProperty("PraiCode18")
    private String praiCode18 ;
    
    @JsonProperty("PraiCode19")
    private String praiCode19 ;
    
    @JsonProperty("PraiCode20")
    private String praiCode20 ;
    
    @JsonProperty("PraiYn01")
    private String praiYn01 ;
    
    @JsonProperty("PraiYn02")
    private String praiYn02 ;
    
    @JsonProperty("PraiYn03")
    private String praiYn03 ;
    
    @JsonProperty("PraiYn04")
    private String praiYn04 ;
    
    @JsonProperty("PraiYn05")
    private String praiYn05 ;
    
    @JsonProperty("PraiYn06")
    private String praiYn06 ;
    
    @JsonProperty("PraiYn07")
    private String praiYn07;
    
    @JsonProperty("PraiYn08")
    private String praiYn08;
    
    @JsonProperty("PraiYn09")
    private String praiYn09 ;
    
    @JsonProperty("PraiYn10")
    private String praiYn10 ;
    
    @JsonProperty("PraiString01")
    private String praiString01 ;
    
    @JsonProperty("PraiString02")
    private String praiString02 ;
    
    @JsonProperty("PraiString03")
    private String praiString03 ;
    
    @JsonProperty("PraiString04")
    private String praiString04 ;
    
    @JsonProperty("PraiString05")
    private String praiString05 ;
    
    @JsonProperty("PraiString06")
    private String praiString06 ;
    
    @JsonProperty("PraiString07")
    private String praiString07 ;
    
    @JsonProperty("PraiString08")
    private String praiString08 ;
    
    @JsonProperty("PraiString09")
    private String praiString09 ;
    
    @JsonProperty("PraiString10")
    private String praiString10 ;
    
    @JsonProperty("PraiNum01")
    private String     praiNum01 ;
    
    @JsonProperty("PraiNum02")
    private String     praiNum02 ;
    
    @JsonProperty("PraiNum03")
    private String     praiNum03 ;
    
    @JsonProperty("PraiNum04")
    private String     praiNum04 ;
    
    @JsonProperty("PraiNum05")
    private String     praiNum05 ;
    
    @JsonProperty("PraiData02")
    private String     praiData02 ;
  
    @JsonProperty("PraiData04")
    private String     praiData04 ;

    @JsonProperty("PraiData05")
    private String     praiData05 ;
    
    @JsonProperty("PraiData06")
    private String     praiData06 ;

    @JsonProperty("PraiData07")
    private String     praiData07 ;

    @JsonProperty("PraiData08")
    private String     praiData08 ;
    
    @JsonProperty("PraiData09")
    private String     praiData09 ;

    @JsonProperty("PraiData10")
    private String     praiData10 ;
    
    @JsonProperty("PraiData11")
    private String     praiData11 ;

    @JsonProperty("PraiData12")
    private String     praiData12 ;

    @JsonProperty("PraiData13")
    private String     praiData13 ;
    
    @JsonProperty("PraiData14")
    private String     praiData14 ;

    @JsonProperty("PraiData15")
    private String     praiData15 ;
    
    @JsonProperty("PraiData16")
    private String     praiData16 ;

    @JsonProperty("PraiData17")
    private String     praiData17 ;

    @JsonProperty("PraiData18")
    private String     praiData18 ;
    
    @JsonProperty("PraiData19")
    private String     praiData19 ;

    @JsonProperty("PraiData20")
    private String     praiData20 ;
    
    @JsonProperty("PraiRemarks01")
    private String     praiRemarks01 ;

    @JsonProperty("PraiRemarks02")
    private String     praiRemarks02 ;
    
    @JsonProperty("PraiRemarks03")
    private String     praiRemarks03 ;

    @JsonProperty("PraiRemarks04")
    private String     praiRemarks04 ;
    
    @JsonProperty("PraiRemarks05")
    private String     praiRemarks05 ;
    
    @JsonProperty("PraiCrUid")
    private String     praiCrUid ;  
    
    @JsonProperty("PraiCrDt")
    private String     praiCrDt ;

    @JsonProperty("PraiUpdUid")
    private String     praiUpdUid ;
    
    @JsonProperty("PraiRiskId")
    private String     praiRiskId ;

    @JsonProperty("PraiPeriodUnit")
    private String     praiPeriodUnit ;
    
    @JsonProperty("PraiUpdDt")
    private String     praiUpdDt ;
    
    @JsonProperty("PraiPmlAmtFc")
    private String     praiPmlAmtFc ;
    
    @JsonProperty("PraiPmlAmtLc1")
    private String     praiPmlAmtLc1 ;
    
    @JsonProperty("PraiPmlAmtLc2")
    private String     praiPmlAmtLc2 ;
    
    @JsonProperty("PraiPmlAmtLc3")
    private String     praiPmlAmtLc3;
    
    @JsonProperty("PraiPmlPerc")
    private String     praiPmlPerc;
    
    @JsonProperty("PraiAoaLimitFc")
    private String     praiAoaLimitFc ;

    @JsonProperty("PraiAoaLimitLc1")
    private String     praiAoaLimitLc1 ;

    @JsonProperty("PraiAoaLimitLc2")
    private String     praiAoaLimitLc2 ;

    @JsonProperty("PraiAoaLimitLc3")
    private String     praiAoaLimitLc3 ;
    
    @JsonProperty("PraiOrgAoaLimitFc")
    private String     praiOrgAoaLimitFc ;
    
    @JsonProperty("PraiOrgAoaLimitLc1")
    private String     praiOrgAoaLimitLc1 ;

    @JsonProperty("PraiOrgAoaLimitLc2")
    private String     praiOrgAoaLimitLc2 ;

    @JsonProperty("PraiOrgAoaLimitLc3")
    private String     praiOrgAoaLimitLc3 ;
    
    @JsonProperty("PraiAoyLimitFc")
    private String     praiAoyLimitFc ;
    
    @JsonProperty("PraiAoyLimitLc1")
    private String     praiAoyLimitLc1 ;
    
    @JsonProperty("PraiAoyLimitLc2")
    private String     praiAoyLimitLc2 ;
    
    @JsonProperty("PraiAoyLimitLc3")
    private String     praiAoyLimitLc3 ;
    
    @JsonProperty("PraiOrgAoyLimitFc")
    private String     praiOrgAoyLimitFc ;

    @JsonProperty("PraiOrgAoyLimitLc1")
    private String     praiOrgAoyLimitLc1 ;

    @JsonProperty("PraiOrgAoyLimitLc2")
    private String     praiOrgAoyLimitLc2 ;

    @JsonProperty("PraiOrgAoyLimitLc3")
    private String     praiOrgAoyLimitLc3 ;

    @JsonProperty("PraiRiskSrNo")
    private String     praiRiskSrNo ;
    
    @JsonProperty("PraiCompCode")
    private String     praiCompCode ;

    @JsonProperty("PraiDivnCode")
    private String     praiDivnCode ;

    @JsonProperty("PraiDeptCode")
    private String     praiDeptCode ;

    @JsonProperty("PraiDsType")
    private String     praiDsType ;

    @JsonProperty("PraiProdCode")
    private String     praiProdCode ;
    
    @JsonProperty("PraiPeriod")
    private String     praiPeriod ;

    @JsonProperty("PraiPiSysId")
    private String     praiPiSysId ;
    
    @JsonProperty("PraiNum06")
    private String     praiNum06 ;
    
    @JsonProperty("PraiNum07")
    private String     praiNum07 ;

    @JsonProperty("PraiNum08")
    private String     praiNum08 ;

    @JsonProperty("PraiNum09")
    private String     praiNum09 ;

    @JsonProperty("PraiNum10")
    private String     praiNum10 ;

    @JsonProperty("PraiNum11")
    private String     praiNum11 ;

    @JsonProperty("PraiNum12")
    private String     praiNum12 ;
    
    @JsonProperty("PraiNum13")
    private String     praiNum13 ;

    @JsonProperty("PraiNum14")
    private String     praiNum14 ;

    @JsonProperty("PraiNum15")
    private String     praiNum15 ;

    @JsonProperty("PraiNum16")
    private String     praiNum16 ;

    @JsonProperty("PraiNum17")
    private String     praiNum17 ;

    @JsonProperty("PraiNum18")
    private String     praiNum18 ;

    @JsonProperty("PraiNum19")
    private String     praiNum19 ;

    @JsonProperty("PraiNum20")
    private String     praiNum20 ;

    @JsonProperty("PraiNum21")
    private String     praiNum21 ;

    @JsonProperty("PraiNum22")
    private String     praiNum22 ;

    @JsonProperty("PraiNum23")
    private String     praiNum23 ;

    @JsonProperty("PraiNum24")
    private String     praiNum24 ;

    @JsonProperty("PraiNum25")
    private String     praiNum25 ;
    
    @JsonProperty("PraiYn11")
    private String     praiYn11 ;
    
    @JsonProperty("PraiYn12")
    private String     praiYn12 ;
    
    @JsonProperty("PraiYn13")
    private String     praiYn13 ;
    
    @JsonProperty("PraiYn14")
    private String     praiYn14 ;
    
    @JsonProperty("PraiYn15")
    private String     praiYn15;
    
    @JsonProperty("PraiYn16")
    private String     praiYn16 ;
    
    @JsonProperty("PraiYn17")
    private String     praiYn17 ;
    
    @JsonProperty("PraiYn18")
    private String     praiYn18;
    
    @JsonProperty("PraiYn19")
    private String     praiYn19;

    @JsonProperty("PraiYn20")
    private String     praiYn20;

    @JsonProperty("PraiYn21")
    private String     praiYn21;

    @JsonProperty("PraiYn22")
    private String     praiYn22;

    @JsonProperty("PraiYn23")
    private String     praiYn23;

    @JsonProperty("PraiYn24")
    private String     praiYn24;

    @JsonProperty("PraiYn25")
    private String     praiYn25;
    
    @JsonProperty("PraiData21")
    private String     praiData21;

    @JsonProperty("PraiData22")
    private String     praiData22;

    @JsonProperty("PraiData23")
    private String     praiData23;

    @JsonProperty("PraiData24")
    private String     praiData24;

    @JsonProperty("PraiData25")
    private String     praiData25;
    
    @JsonProperty("PraiData26")
    private String     praiData26;

    @JsonProperty("PraiData27")
    private String     praiData27;

    @JsonProperty("PraiData28")
    private String     praiData28;

    @JsonProperty("PraiData29")
    private String     praiData29;

    @JsonProperty("PraiData30")
    private String     praiData30;  

    @JsonProperty("PraiData31")
    private String     praiData31;

    @JsonProperty("PraiData32")
    private String     praiData32;

    @JsonProperty("PraiData33")
    private String     praiData33;

    @JsonProperty("PraiData34")
    private String     praiData34;

    @JsonProperty("PraiData35")
    private String     praiData35;
    
    @JsonProperty("PraiData36")
    private String     praiData36;

    @JsonProperty("PraiData37")
    private String     praiData37;

    @JsonProperty("PraiData38")
    private String     praiData38;

    @JsonProperty("PraiData39")
    private String     praiData39;

    @JsonProperty("PraiData40")
    private String     praiData40;

    @JsonProperty("PraiCode21")
    private String     praiCode21;

    @JsonProperty("PraiCode22")
    private String     praiCode22;

    @JsonProperty("PraiCode25")
    private String     praiCode25;
    
    @JsonProperty("PraiString11")
    private String     praiString11;
    
    @JsonProperty("PraiString12")
    private String     praiString12;
    
    @JsonProperty("PraiString13")
    private String     praiString13;
  
    @JsonProperty("PraiString14")
    private String     praiString14;
 
    @JsonProperty("PraiString15")
    private String     praiString15;
    
    @JsonProperty("PraiString16")
    private String     praiString16;
    
    @JsonProperty("PraiString17")
    private String     praiString17;
    
    @JsonProperty("PraiString18")
    private String     praiString18;
   
    @JsonProperty("PraiString19")
    private String     praiString19;
  
    @JsonProperty("PraiString20")
    private String     praiString20;

    @JsonProperty("PraiRemarks06")
    private String     praiRemarks06;

    @JsonProperty("PraiRemarks07")
    private String     praiRemarks07;

    @JsonProperty("PraiRemarks08")
    private String     praiRemarks08;

    @JsonProperty("PraiRemarks09")
    private String     praiRemarks09;

    @JsonProperty("PraiRemarks10")
    private String     praiRemarks10;    

    @JsonProperty("PraiOrgCumBonusFc")
    private String praiOrgCumBonusFc ;

    @JsonProperty("PraiOrgCumBonusLc1")
    private String praiOrgCumBonusLc1 ;

    @JsonProperty("PraiOrgCumBonusLc2")
    private String praiOrgCumBonusLc2 ;

    @JsonProperty("PraiOrgCumBonusLc3")
    private String praiOrgCumBonusLc3 ;

    @JsonProperty("PraiRenPraiSysId")
    private String praiRenPraiSysId ;
    
    @JsonProperty("PraiRiskClassCode")
    private String     praiRiskClassCode ;

    @JsonProperty("PraiClmTotalLossYn")
    private String     praiClmTotalLossYn ;

    @JsonProperty("PraiOrgPmlAmtFc")
    private String praiOrgPmlAmtFc ;

    @JsonProperty("PraiOrgPmlAmtLc1")
    private String praiOrgPmlAmtLc1 ; 

    @JsonProperty("PraiOrgPmlAmtLc2")
    private String praiOrgPmlAmtLc2 ; 

    @JsonProperty("PraiOrgPmlAmtLc3")
    private String praiOrgPmlAmtLc3 ; 

    @JsonProperty("PraiOrgPmlPerc")
    private String praiOrgPmlPerc ;

    @JsonProperty("PraiRenSrNo")
    private String praiRenSrNo ; 

    @JsonProperty("PraiTpaCode")
    private String     praiTpaCode ;

    @JsonProperty("PraiPolClassification")
    private String     praiPolClassification ;

    @JsonProperty("PraiCountryCode")
    private String     praiCountryCode ;

    @JsonProperty("PraiLocationCode")
    private String     praiLocationCode ;

    @JsonProperty("PraiNoRenClm")
    private String praiNoRenClm ; 

    @JsonProperty("PraiAnnualPremFc")
    private String praiAnnualPremFc ; 

    @JsonProperty("PraiAnnualPremlc1")
    private String praiAnnualPremlc1 ; 

    @JsonProperty("PraiAnnualPremlc2")
    private String praiAnnualPremlc2 ; 

    @JsonProperty("PraiAnnualPremlc3")
    private String praiAnnualPremlc3 ;
    
    @JsonProperty("PraiCode26")
    private String     praiCode26 ;
    
    @JsonProperty("PraiCode27")
    private String     praiCode27 ;
    
    @JsonProperty("PraiCode28")
    private String     praiCode28 ;
    
    @JsonProperty("PraiCode29")
    private String     praiCode29 ;
    
    @JsonProperty("PraiCode30")
    private String     praiCode30 ;

    @JsonProperty("PraiNcbYrs")
    private String praiNcbYrs ; 

    @JsonProperty("PraiCnctSysId")
    private String praiCnctSysId ;

    @JsonProperty("PraiRaApplYn")
    private String     praiRaApplYn ;
    
    @JsonProperty("PraiRiskRefNo")
    private String     praiRiskRefNo ;

    @JsonProperty("PraiIdvFc")
    private String praiIdvFc ; 

    @JsonProperty("PraiIdvLc1")
    private String praiIdvLc1 ;

    @JsonProperty("PraiIdvLc2")
    private String praiIdvLc2 ;

    @JsonProperty("PraiIdvLc3")
    private String praiIdvLc3 ;

    @JsonProperty("PraiTotSiFc")
    private String praiTotSiFc ;

    @JsonProperty("PraiTotSiLc1")
    private String praiTotSiLc1 ;

    @JsonProperty("PraiTotSiLc2")
    private String praiTotSiLc2 ;
    
    @JsonProperty("PraiTotSiLc3")
    private String praiTotSiLc3;

    @JsonProperty("PraiFirstLossPerc")
    private String praiFirstLossPerc ;
    
    @JsonProperty("PraiOrgTotSiFc")
    private String praiOrgTotSiFc;
    
    @JsonProperty("PraiOrgTotSiLc1")
    private String praiOrgTotSiLc1;
    
    @JsonProperty("PraiOrgTotSiLc2")
    private String praiOrgTotSiLc2;
    
    @JsonProperty("PraiOrgTotSiLc3")
    private String praiOrgTotSiLc3;
    
    @JsonProperty("PraiOurSharePerc")
    private String praiOurSharePerc;
    
    @JsonProperty("PraiOurShareSiFc")
    private String praiOurShareSiFc;

    @JsonProperty("PraiOurShareSiLc1")
    private String praiOurShareSiLc1;

    @JsonProperty("PraiOurShareSiLc2")
    private String praiOurShareSiLc2;

    @JsonProperty("PraiOurShareSiLc3")
    private String praiOurShareSiLc3;

    @JsonProperty("PraiOurSharePremFc")
    private String praiOurSharePremFc;

    @JsonProperty("PraiOurSharePremLc1")
    private String praiOurSharePremLc1;

    @JsonProperty("PraiOurSharePremLc2")
    private String praiOurSharePremLc2;

    @JsonProperty("PraiOurSharePremLc3")
    private String praiOurSharePremLc3;

    @JsonProperty("PraiOrgOurSharePerc")
    private String praiOrgOurSharePerc;

    @JsonProperty("PraiOrgOurShareSiFc")
    private String praiOrgOurShareSiFc;
    
    @JsonProperty("PraiOrgOurShareSiLc1")
    private String praiOrgOurShareSiLc1;
    
    @JsonProperty("PraiOrgOurShareSiLc2")
    private String praiOrgOurShareSiLc2;
    
    @JsonProperty("PraiOrgOurShareSiLc3")
    private String praiOrgOurShareSiLc3;
    
    @JsonProperty("PraiOrgOurSharePremFc")
    private String praiOrgOurSharePremFc;
    
    @JsonProperty("PraiOrgOurSharePremLc1")
    private String praiOrgOurSharePremLc1;
    
    @JsonProperty("PraiOrgOurSharePremLc2")
    private String praiOrgOurSharePremLc2;
    
    @JsonProperty("PraiOrgOurSharePremLc3")
    private String praiOrgOurSharePremLc3;
    
    @JsonProperty("PraiMaintPeriodUnit")
    private String     praiMaintPeriodUnit ;
    
    @JsonProperty("PraiMaintPeriod")
    private String praiMaintPeriod;
    
    @JsonProperty("PraiMaintFmDt")
    private String     praiMaintFmDt ;
  
    @JsonProperty("PraiMaintToDt")
    private String praiMaintToDt;
    
    @JsonProperty("PraiTestingPeriodUnit")
    private String     praiTestingPeriodUnit ;
    
    @JsonProperty("PraiTestingPeriod")
    private String praiTestingPeriod;
    
    @JsonProperty("PraiTestingFmDt")
    private String     praiTestingFmDt ;
    
    @JsonProperty("PraiTestingToDt")
    private String praiTestingToDt;
    
    @JsonProperty("PraiNajmSts")
    private String     praiNajmSts ;

    @JsonProperty("PraiNajmRemarks")
    private String     praiNajmRemarks ;
    
    @JsonProperty("PraiCoinsPoolCode")
    private String     praiCoinsPoolCode ;

    @JsonProperty("PraiCertReqYn")
    private String     praiCertReqYn ;
    
    @JsonProperty("PraiCertType")
    private String     praiCertType ;

    @JsonProperty("PraiCertMode")
    private String     praiCertMode ;
    
    @JsonProperty("PraiCertNo")
    private String     praiCertNo ;   
    
    @JsonProperty("PraiCertFmDt")
    private String     praiCertFmDt ;    
   
    @JsonProperty("PraiCertToDt")
    private String     praiCertToDt ;   
   
    @JsonProperty("PraiString21")
    private String     praiString21 ;
    
    @JsonProperty("PraiString22")
    private String     praiString22 ;
    
    @JsonProperty("PraiString23")
    private String     praiString23 ;
    
    @JsonProperty("PraiString24")
    private String     praiString24 ;   
    
    @JsonProperty("PraiString25")
    private String     praiString25 ;
    
    @JsonProperty("RequestTime")
    @JsonFormat(pattern = "dd/MM/YYYY")
    private Date     requestTime ;
    
    @JsonProperty("ResponseTime")
    @JsonFormat(pattern = "dd/MM/YYYY")
    private Date     responseTime ;
    
    @JsonProperty("Status")
    private String     status ;

    @JsonProperty("ServiceId")
    private String     serviceId ;

    @JsonProperty("PWsResponseType")
    private String     pWsResponseType ;

    @JsonProperty("PWsError")
    private String     pWsError ;

    @JsonProperty("ServiceAction")
    private String     serviceAction ;

    @JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo ;

}
