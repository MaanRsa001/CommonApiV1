package com.maan.eway.admin.service;

import java.util.List;

import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.AttachEndtIdsReq;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.AttachIssuerProductRequest;
import com.maan.eway.admin.req.AttachIssuerReferalReq;
import com.maan.eway.admin.req.BrokerCreationReq;
import com.maan.eway.admin.req.IssuerCraeationReq;
import com.maan.eway.admin.req.LoginBranchesSaveReq;
import com.maan.eway.admin.req.UserCreationReq;

public interface LoginValidationService {

List<String> validateBrokerCreation(BrokerCreationReq req);
List<String> validateIssuerCreation(IssuerCraeationReq req);
List<String> validateUserCreation(UserCreationReq req);
List<String> validateBrokerBranchReq(AttachCompaniesReq req);
List<String> validateBrokerProductReq(AttachCompnayProductRequest req);
List<String> validateIssuerBranchReq(AttachIssuerBrannchReq req);
List<String> validateIssuerReferalReq(AttachIssuerReferalReq req);
List<String> validateBrokerCompanyBranchReq(AttachBrokerBranchReq req);
List<String> validateLoginBranches(LoginBranchesSaveReq req);
List<String> validateIssuerProductReq(AttachIssuerProductRequest req);
List<String> validateProductsEndtIds(AttachEndtIdsReq req);


}
