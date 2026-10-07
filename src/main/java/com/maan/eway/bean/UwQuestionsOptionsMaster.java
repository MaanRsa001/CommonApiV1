package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(UwQuestionsOptionsMasterId.class)
@Table(name="uw_questions_options_master")
public class UwQuestionsOptionsMaster implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	 
	    //--- ENTITY PRIMARY KEY 
	    @Id
	    @Column(name="COMPANY_ID", nullable=false, length=20)
	    private String     companyId ;
	    
	    
	    @Id
		@Column(name="BRANCH_CODE",length=20, nullable=false)
		private String branchCode;

	    @Id
	    @Column(name="PRODUCT_ID", nullable=false)
	    private Integer  productId;
        
	    @Column(name="SECTION_ID",length=100) 
		 private String sectionId ;
	    
	    @Id
	    @Column(name="DEPENDENT_UW_QUESTION_ID", nullable=false)
	    private Integer  dependentUwQuestionId;  //2  QuestionId
	    
	    @Id
	    @Column(name="UW_QUES_OPTION_ID", nullable=false)
	    private Integer  uwQuesOptionId;   //1 Yes
	  
	    
	    //--- ENTITY DATA FIELDS 
	

	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="ENTRY_DATE")
	    private Date       entryDate ;

	    @Column(name="STATUS", length=10)
	    private String     status ;
	    
	    @Column(name="LOADING_PERCENT")
	    private BigDecimal  loadingPercent;

	    
	    @Column(name="DEPENDENT_YN")
	    private String     dependentYn ; // Y (add in 2)
	    
	    @Column(name="DEPENDENT_UNDERWRITER_ID")
	    private String  dependentUnderwriterId; // 3

	    @Column(name="DEPENDENT_UW_ACTION")
	    private String  dependentUwAction;  // show

	    @Column(name="UW_QUES_OPTION_DESC")
	    private String    uwQuesOptionDesc ;
	    
	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="EFFECTIVE_DATE_START")
	    private Date       effectiveDateStart ;

	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="EFFECTIVE_DATE_END")
	    private Date       effectiveDateEnd ;
	    
	    @Column(name="AMEND_ID")
	    private Integer     amendId ;
	    
	    @Column(name="REFERRAL_YN")
	    private String  referralYn;
		
	}