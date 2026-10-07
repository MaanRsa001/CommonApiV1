package com.maan.eway.admin.service;

import java.util.Date;
import java.util.List;

import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.AttachEndtIdsReq;
import com.maan.eway.admin.req.AttachIssuerProductRequest;
import com.maan.eway.admin.req.BrokerCompanyListProductsGetAllRes;
import com.maan.eway.admin.req.BrokerCompanyProductGetReq;
import com.maan.eway.admin.req.BrokerCompanyProductsGetRes;
import com.maan.eway.admin.req.BrokerProductGetReq;
import com.maan.eway.admin.req.IssuerProductGetReq;
import com.maan.eway.admin.req.UserCompanyProductGetReq;
import com.maan.eway.admin.res.BrokerProductGetRes;
import com.maan.eway.admin.res.IssuerProductGetRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.auth.dto.LoginProductCriteriaRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerCompanyListProductReq;
import com.maan.eway.master.req.BrokerCompanyProductReq;
import com.maan.eway.master.req.BrokerProductChangeReq;
import com.maan.eway.master.req.BrokerProductReq;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.master.res.GetAllNonSelectedBrokerProductMasterRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface LoginProductService {

	LoginCreationRes saveBrokerProductDetails(AttachCompnayProductRequest req);

	BrokerProductGetRes getBrokerProducts(BrokerProductGetReq req);

	List<BrokerCompanyProductsGetRes> getBrokerCompanyProducts(BrokerCompanyProductGetReq req);

	List<LoginProductCriteriaRes> getBrokerProductDetails(String loginId, List<String> companyIds, Date today);

	SuccessRes updateBrokerCompanyProductDetails(BrokerCompanyProductReq req);

	List<Error> validateUpdateBrokerCompanyProductDetails(BrokerCompanyProductReq req);

	List<CompanyProductMasterRes> getallNonSelectedBrokerCompanyProducts(BrokerCompanyProductGetReq req);

	SuccessRes changeStatusOfCompanyProduct(BrokerProductChangeReq req);

	List<DropDownRes> getBrokerProductDropdown(BrokerProductReq req);

	List<CompanyProductMasterRes> getallNonSelectedUserCompanyProducts(UserCompanyProductGetReq req);

	LoginCreationRes saveIssuerProductDetails(AttachCompnayProductRequest req);

	LoginCreationRes saveIssuerProducts(AttachIssuerProductRequest req);

	List<IssuerProductGetRes> getIssuerProducts(IssuerProductGetReq req);

	LoginCreationRes saveProductsEndtIds(AttachEndtIdsReq req);

	List<Error> validatebrokerListCompanyProducts(List<BrokerCompanyListProductReq> req);

	SuccessRes brokerListCompanyProducts(List<BrokerCompanyListProductReq> req);

	List<BrokerCompanyListProductsGetAllRes> getAllBrokerCompanyListProducts(BrokerCompanyProductGetReq req);

	List<GetAllNonSelectedBrokerProductMasterRes> getallNonSelectedUserCompanyProductsList(
			UserCompanyProductGetReq req);


	
}
