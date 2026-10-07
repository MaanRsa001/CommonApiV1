package com.maan.eway.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.master.req.CustomerSaveReq;
import com.maan.eway.master.req.ProductsRiskSaveReq;
import com.maan.eway.req.EserviceAllRisksSaveReq;
import com.maan.eway.req.EserviceBuildingsDetailsSaveReq;
import com.maan.eway.req.EserviceContentsDetailsSaveReq;
import com.maan.eway.req.EservicePersonalAccidentSaveReq;


public interface ValidationService {

	List<Error> validateCustomerSave(CustomerSaveReq req) ;

	List<Error> validateCustomerUpdate(CustomerSaveReq req);




 List<Error> validatePersonalAccident(EservicePersonalAccidentSaveReq req);

	List<Error> validateProductSections(ProductsRiskSaveReq req);



	List<Error> validateEserviceBuildingDetails(EserviceBuildingsDetailsSaveReq req);

	List<Error> validateEserviceContentDetails(EserviceContentsDetailsSaveReq req);

	List<Error> validateEserviceAllRisk(EserviceAllRisksSaveReq req);

	List<Error> validateMotorSchedule(JasperDocumentReq req);

	List<Error> validateMotorSchedule1(JasperDocumentReq req);
	
	public List<Error> validateTaxInvoice(String quoteNo);



}
