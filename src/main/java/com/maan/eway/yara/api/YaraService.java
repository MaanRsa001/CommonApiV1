package com.maan.eway.yara.api;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.error.Error;

public interface YaraService {

	Object getYaraApi(YaraReq req);

	List<Error> validationYara(YaraReq req);

	List<YaraDocumentResp> documentDetails(YaraDocumentReq req);

	Object uploadDocument(MultipartFile file,String quoteNo);

}
