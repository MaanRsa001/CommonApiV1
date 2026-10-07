package com.maan.eway.salesLead.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.salesLead.req.EnquiryDetailsDTO;
import com.maan.eway.salesLead.req.EserviceLeadSaveReq;
import com.maan.eway.salesLead.req.GetEnquiryDetailsReq;
import com.maan.eway.salesLead.req.GetUploadDocumentListReq;
import com.maan.eway.salesLead.req.InsertSalesReq;
import com.maan.eway.salesLead.req.SaveUploadDocumentsReq;
import com.maan.eway.salesLead.res.GetLeadDetailsRes;

public interface SalesLeadService {

	boolean insertLeadDetails(List<InsertSalesReq> req);

	CommonRes getSalesLead(String leadId);

	CommonRes insertEnquiry(EnquiryDetailsDTO req);

	CommonRes getEnquirys(GetEnquiryDetailsReq req);

	List<DropDownRes> contactType(LovDropDownReq req);

	List<DropDownRes> customerType(LovDropDownReq req);

	List<DropDownRes> sectionType(LovDropDownReq req);

	List<DropDownRes> typeOfBusiness(LovDropDownReq req);

	List<DropDownRes> currentInsurer(LovDropDownReq req);

	List<DropDownRes> lineOfBusiness(LovDropDownReq req);

	List<DropDownRes> product(LovDropDownReq req);

	List<DropDownRes> businessType(LovDropDownReq req);

	List<DropDownRes> probabilityOfSuccess(LovDropDownReq req);

	SuccessRes saveLeadDetails(EserviceLeadSaveReq req);

	List<GetLeadDetailsRes> getLeadDetails(GetCustomerDetailsReq req);

	List<DropDownRes> getStatusByUserType();

	boolean saveUploadDocuments(SaveUploadDocumentsReq req, List<MultipartFile> fileReq);

	JasperDocumentRes downloadUploadDocuments(String id);

	CommonRes getUploadDocumentList(GetUploadDocumentListReq req);

}
