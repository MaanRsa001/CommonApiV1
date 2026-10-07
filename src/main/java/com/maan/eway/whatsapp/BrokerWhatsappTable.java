package com.maan.eway.whatsapp;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
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
@IdClass(BrokerWhatsappTableId.class)
@Table(name="broker_whatsapp_table")
public class BrokerWhatsappTable implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="LOGIN_ID")
    private String     loginId ;
    
    @Id
    @Column(name="SNO")
    private Long     sno ;
    
    
  //--- OTHER FIELDS 
    
    @Column(name="WHATSAPP_NO")
    private String     whatsappNo ;
    
    @Column(name="BROKER_LOGIN_ID")
    private String     brokerLoginId ;
    
    @Column(name="USER_NAME")
    private String     userName ;
    
    @Column(name="BROKER_NAME")
    private String     brokerName ;
    
    @Column(name="OA_CODE")
    private String     oaCode ;
    
    @Column(name="AGENCY_CODE")
    private String     agencyCode ;
    
    @Column(name="CUSTOMER_CODE")
    private String     customerCode ;
    
    @Column(name="BROKER_CORE_APP_CODE")
    private String     brokerCoreAppCode ;
    
    @Column(name="STATUS")
    private String     status ;
    
    @Column(name="ENTRY_DATE")
    private Date     entryDate ;
    
    @Column(name="SOURCE_TYPE")
    private String     sourceType ;
    
    @Column(name="SUBUSER_TYPE")
    private String     subUserType ;
    
    @Column(name="BRANCH_CODE")
    private String     branchCode ;
    
    @Column(name="BROKER_BRANCH_CODE")
    private String     brokerBranchCode ;
    
    @Column(name="UPDATED_DATE")
    private Date     updatedDate ;
    
    @Column(name="MOBILE_NO")
    private String     mobileNo ;
    
    @Column(name="MOBILE_CODE")
    private String     mobileCode ;

}
