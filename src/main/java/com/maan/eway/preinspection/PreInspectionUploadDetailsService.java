package com.maan.eway.preinspection;

import com.maan.eway.common.res.CommonRes;

public interface PreInspectionUploadDetailsService {

	Object savePreInspectionDetails(PreInspectionUploadDetailSaveReq req);

	GetPreInspectionUploadDetailRes getPreInspectionDetails(GetPreInspectionUploadDetailReq req);

	Object getPreInspectionImageDetails(GetPreInspectionUploadDetailReq req);

	Object deletePreInspectionImage(GetPreInspectionUploadDetailReq req);

	FileUploadRes uploadFile(PreFileUploadReq req);

	CommonRes preInspectionDashboard(GetPreInspectionUploadDetailReq req);

}
