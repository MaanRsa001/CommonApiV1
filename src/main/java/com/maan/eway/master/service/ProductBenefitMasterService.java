package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.common.req.ProductBenefitDropDownReq;
import com.maan.eway.master.req.ProductBenefitChangeStatusReq;
import com.maan.eway.master.req.ProductBenefitGetAllReq;
import com.maan.eway.master.req.ProductBenefitGetReq;
import com.maan.eway.master.req.ProductBenefitSaveReq;
import com.maan.eway.master.res.ProductBenefitGetRes;
import com.maan.eway.res.ProductBenefitDropDownRes;
import com.maan.eway.res.SuccessRes;

public interface ProductBenefitMasterService {

	List<String> validateProductBenefit(ProductBenefitSaveReq req);

	//SuccessRes saveProductBenefit(ProductBenefitSaveReq req, Object file);
	SuccessRes saveProductBenefit(ProductBenefitSaveReq req);

	List<ProductBenefitGetRes> getallProductBenefit(ProductBenefitGetAllReq req);

	List<ProductBenefitGetRes> getActiveProductBenefit(ProductBenefitGetAllReq req);

	ProductBenefitGetRes getByProductBenefitId(ProductBenefitGetReq req);

	SuccessRes changeStatusOfProductBenefit(ProductBenefitChangeStatusReq req);

	List<ProductBenefitDropDownRes> getProductBenefitDropdown(ProductBenefitDropDownReq req);

}
