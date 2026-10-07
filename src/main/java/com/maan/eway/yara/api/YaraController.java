package com.maan.eway.yara.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;

@RestController
@RequestMapping("/api")
public class YaraController {
	
	@Autowired
	private YaraService yaraService;
	
	@Value("${policy.file.path}")
	private String filepath;
	
	private Logger log = LogManager.getLogger(YaraController.class);
	
	@PostMapping("/yara")
	public ResponseEntity<CommonRes> yaraApi(@RequestBody YaraReq req){
		CommonRes data = new CommonRes();
		this.log.info("Yara Api Req :"+req);
		List<Error> validation = yaraService.validationYara(req);
		if((!validation.isEmpty()) && validation.size()>0 ) {
			data.setCommonResponse(null);
			data.setErrorMessage(validation);
			data.setIsError(true);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else {
			Object res = yaraService.getYaraApi(req);
			
			data.setCommonResponse(res);
			data.setErrorMessage(Collections.EMPTY_LIST);
			data.setIsError(false);
			data.setMessage("Success");
			
			if(res != null) {
				return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
			}else {
				return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			}
		}
		
		
	}
	
	@PostMapping("/yara/document/details")
	public ResponseEntity<CommonRes> yaraDocumentDetails(@RequestBody YaraDocumentReq req){
		CommonRes data = new CommonRes();
		List<YaraDocumentResp> resp = yaraService.documentDetails(req);
		
		data.setCommonResponse(resp);
		data.setErrorMessage(Collections.EMPTY_LIST);
		data.setIsError(false);
		data.setMessage("Success");
		
		if(resp != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/yara/document/upload")
	public Object uploadYaraDocument(@RequestParam("file") MultipartFile file,@RequestParam("QuoteNo") String quoteNo){
		try {
			//Map<String,Object> req = new ObjectMapper().readValue(FileUploadReq, Map.class)
			Object res =yaraService.uploadDocument(file,quoteNo);
			if(res != null) {
				return res;	
			}else {
				
				return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			}
		 
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
			
	}
	
	@GetMapping("/yara/document/download")
	public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) throws IOException{
		try {
			// Path pathFile = Paths.get(filepath).resolve(fileName).normalize();
			// Resource resource = new UrlResource(pathFile.toUri());
			String filePath =  filepath+ fileName;

			 File file = new File(filePath);
			 if (!file.exists()) {
			        throw new FileNotFoundException("File not found: " + fileName);
			    }
			 

			    Path path = file.toPath();
			    Resource resource = new UrlResource(path.toUri());
			    
			    String contentType = Files.probeContentType(path);
			    
			    if (contentType == null) {
			        contentType = "application/octet-stream"; // Fallback for unknown types
			    }
			  
			    return ResponseEntity.ok()
			            .contentType(MediaType.parseMediaType(contentType))
			            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
			            .body(resource);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(null);
		}
	}

}
 