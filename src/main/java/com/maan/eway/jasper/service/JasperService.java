package com.maan.eway.jasper.service;

import java.util.List;

import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PortFolioDashBoardRes;
import com.maan.eway.jasper.req.GetApiDocReportReq;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PdfJsonReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.ApiDocListRes;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.res.ViewReportDetailsRes;

public interface JasperService {

	public JasperDocumentRes policyform(JasperDocumentReq req);

	public JasperDocumentRes proposalform(JasperDocumentReq req);

	public JasperDocumentRes policyreportform(JasperReportDocReq req);

	public JasperDocumentRes taxInvoice(String quoteNo);

	public JasperDocumentRes creditNote(String quoteNo);

	public CommonRes getPremiumReport(PremiumReportReq req);

	public CommonRes getPremiumReportDetails(PremiumReportReq req);

	public JasperDocumentRes illustration(String jsonFile);

	public JasperDocumentRes getInalipaSchedule(String policyNo);

	public CommonRes getSchedule(JasperScheduleReq req);

	public CommonRes PdfJsonResponse(PdfJsonReq req);

	public JasperDocumentRes GetKenyaMOTbyRefNo(String requestRefNo);

	public List<ApiDocListRes> getApiDocList(String quoteNo);

	public JasperDocumentRes getApiDocReport(GetApiDocReportReq req);

	public JasperDocumentRes getEagleQuotation(String requestReferenceNo, String computationSheetYn,String vehicleId);

	public JasperDocumentRes GetTravelQuotation(String requestRefNo);

	public ViewReportDetailsRes viewReportDetails(String quoteNo);

	public List<PortFolioDashBoardRes> getAllAdminPortfolio(PortFolioDashBoardReq req);
	
	public JasperDocumentRes policyformByRequestRef(JasperDocumentReq req);

	public String callThirdPartyApi(String tokenUrl, String apiUrl, String userName, String password, String policyNo);

	public JasperDocumentRes downloadPolicyWording(String quoteNo);
}
