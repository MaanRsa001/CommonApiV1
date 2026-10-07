package com.maan.eway.admin.service;

import java.util.List;

import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.BrokerBranchGetReq;
import com.maan.eway.admin.req.GetAllBrokerBranchReq;
import com.maan.eway.admin.req.GetBrokerBranchReq;
import com.maan.eway.admin.req.GetallBrokerBranchesReq;
import com.maan.eway.admin.req.IssuerBranchGetReq;
import com.maan.eway.admin.req.LoginBranchReq;
import com.maan.eway.admin.req.LoginBranchesSaveReq;
import com.maan.eway.admin.req.UserCompanyProductGetReq;
import com.maan.eway.admin.res.BrokerCompanyGetRes;
import com.maan.eway.admin.res.GetBrokerBranchRes;
import com.maan.eway.admin.res.GetallBrokerBranchesRes;
import com.maan.eway.admin.res.IssuerCompanyGetRes;
import com.maan.eway.admin.res.LoginBranchRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.bean.BranchMaster;

public interface LoginBranchService {

	LoginCreationRes attachBrokerBranches(AttachCompaniesReq req);
	LoginCreationRes attachIssuerBranches(AttachIssuerBrannchReq req);
	
	List<BrokerCompanyGetRes> getBrokerBranches(BrokerBranchGetReq req);
	List<IssuerCompanyGetRes> getIssuerBranches(IssuerBranchGetReq req);
	LoginCreationRes attachBrokerCompanyBranch(AttachBrokerBranchReq req, String token);
	GetBrokerBranchRes getBrokerCompanyBranch(GetBrokerBranchReq req);
	List<GetBrokerBranchRes> getallBrokerCompanyBranch(GetAllBrokerBranchReq req);
	List<GetBrokerBranchRes> getallNonSelectedUserCompanyBranches(UserCompanyProductGetReq req);
	List<GetallBrokerBranchesRes> getallBrokerBranches(GetallBrokerBranchesReq req);
	LoginCreationRes saveLoginBranches(LoginBranchesSaveReq req);
	List<LoginBranchRes> getLoginbranches(LoginBranchReq req);
	void saveClaimBranch(AttachBrokerBranchReq req, String token, List<BranchMaster> branchname);

}
