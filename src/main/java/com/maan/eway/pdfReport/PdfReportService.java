package com.maan.eway.pdfReport;


import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.viewAll.dto.viewAllReq;

public interface PdfReportService {
	
	CommonRes generateScheduleUganda(JasperDocumentReq req);
}

