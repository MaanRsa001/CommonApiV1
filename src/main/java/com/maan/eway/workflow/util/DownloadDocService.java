package com.maan.eway.workflow.util;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ApiDocDownloadDetail;
import com.maan.eway.repository.ApiDocDownloadDetailRepository;
import com.maan.eway.workflow.dto.WorkEngine;

@Service
public class DownloadDocService {

	@Autowired
	private ApiDocDownloadDetailRepository detailsRepo;
	
	@Value(value = "${report.file.path}")
	private String policyReportPath;
	@Autowired
	private AzentoApiService azentoService;
	
	private ExecutorService executorService = null;
	
	public void downloadDocs(List<Map<String, Object>> data, WorkEngine engine, Map<String, Object> downloadReq, WorkEngine download) {
		try {
			executorService=Executors.newFixedThreadPool(3);
			
			for (int i=0;i<data.size();i++) {
				final	Map<String, Object> map = new HashMap<String, Object>(data.get(i)) ;
				executorService.submit(() -> {
					handleObjectAsync(map,engine,downloadReq,download);
				});

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			executorService.shutdown();
		}

	}

	
	public synchronized void  handleObjectAsync(Map<String, Object> obj, WorkEngine engine, Map<String, Object> downloadReq, WorkEngine download) {
		ApiDocDownloadDetail d=new ApiDocDownloadDetail();
		d.setQuoteNo(StringUtils.isBlank(engine.getQuoteNo())?engine.getRequestReferenceNo():engine.getQuoteNo());
		d.setEntryDate(new Date());
		try {
			
			d.setDocDate(obj.get("docData")==null?"":obj.get("docData").toString());
			d.setDocName(obj.get("docName")==null?"":obj.get("docName").toString());
			d.setDocType(obj.get("docType")==null?"":obj.get("docType").toString());
			d.setErrorDetails(obj.get("errorDetailsList")==null?"":obj.get("errorDetailsList").toString());
			d.setHasError(obj.get("hasError")==null?"":obj.get("hasError").toString());
			d.setSgsId(obj.get("sgsId")==null?"":obj.get("sgsId").toString());
			d.setSno(obj.get("sgsId")==null?BigDecimal.ZERO:new BigDecimal(obj.get("sgsId").toString()));
			d.setStatusCode(obj.get("statusCode")==null?"":obj.get("statusCode").toString());
			d.setTemplateId(obj.get("templateId")==null?"":obj.get("templateId").toString());
			
			d.setFilePath(null);
			d.setFilePathErr(null);
		
			try {
				
				
				downloadReq.put("sgsId", d.getSgsId());
				//downloadReq.put("sgsId", "3875935");
				
				Map<String, Object> quote = azentoService.createQuote(download, downloadReq);
				if(quote.get("File")!=null) {
					try (FileOutputStream fileOutputStream = new FileOutputStream(policyReportPath+d.getDocName())) {
						fileOutputStream.write((byte[]) quote.get("File"));
						fileOutputStream.close();
					}
					
					d.setFilePath(policyReportPath);
				}
			}catch (Exception e) {
			//	e.printStackTrace();
				d.setFilePathErr(e.getLocalizedMessage());
			}finally {
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			detailsRepo.save(d);
		}
		
	}
	
}
