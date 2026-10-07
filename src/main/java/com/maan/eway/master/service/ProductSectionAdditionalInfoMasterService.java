package com.maan.eway.master.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetAllSectionAdditionalDetailsReq;
import com.maan.eway.master.req.GetOptedSectionAdditionalInfoReq;
import com.maan.eway.master.req.GetSectionAdditionalDetailsReq;
import com.maan.eway.master.req.InsertAdditionalInfoReq;
import com.maan.eway.master.req.UploadReq;
import com.maan.eway.master.res.GetOptedSectionAdditionalInfoRes;
import com.maan.eway.master.res.GetSectionAdditionalDetailsRes;
import com.maan.eway.res.SuccessRes;

public interface ProductSectionAdditionalInfoMasterService {

	List<GetOptedSectionAdditionalInfoRes> getOptedSectionAdditionalInfo(GetOptedSectionAdditionalInfoReq req);

	List<Error> validateAdditionalInfo(InsertAdditionalInfoReq req);

	SuccessRes insertAdditionalInfo(InsertAdditionalInfoReq req);

	GetSectionAdditionalDetailsRes getSectionAdditionalDetails(GetSectionAdditionalDetailsReq req);

	List<Error> docvalidation( MultipartFile file, UploadReq req);

	CommonRes fileupload(MultipartFile file, UploadReq req);

	List<GetSectionAdditionalDetailsRes> getAllSectionAdditionalDetails(GetAllSectionAdditionalDetailsReq req);


}
