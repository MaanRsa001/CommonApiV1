package com.maan.eway.preinspection;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;


@RestController
@RequestMapping("/preinspection")
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
public class PreinspectionPolicyGenerateController {

    @Autowired
    private PreinspectionService preinspectionService;

    @PostMapping("/generate-policy")
    public ResponseEntity<CommonRes> generatePolicyAfterPreinspection(
            @RequestParam("quoteNo") String quoteNo,
            @RequestHeader("Authorization") String token) {

        CommonRes res = new CommonRes();

        try {
            PolicyIssueRes policyRes =
                    preinspectionService.generatePolicyPushTiraAndNotify(
                            quoteNo,
                            token.replace("Bearer ", "").split(",")[0]
                    );

            res.setCommonResponse(policyRes);
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Success");

            return new ResponseEntity<>(res, HttpStatus.CREATED);

        } catch (Exception e) {

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Failed");

            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }
    
    
    @PostMapping("/push")
    public ResponseEntity<CommonRes> pushPreinspectionFromUI(
            @RequestParam("quoteNo") String quoteNo) {

        CommonRes res = new CommonRes();

        try {
            boolean success =
                    preinspectionService.pushPreinspectionDetailsByQuote(quoteNo, res);

            if (!success) {
                return new ResponseEntity<>(res, HttpStatus.OK);
            }

            res.setCommonResponse("Preinspection pushed successfully");
            res.setIsError(false);
            res.setErrorMessage(Collections.emptyList());
            res.setMessage("Success");

            return new ResponseEntity<>(res, HttpStatus.CREATED);

        } catch (Exception e) {

            res.setCommonResponse(null);
            res.setIsError(true);
            res.setErrorMessage(
                    buildError("PREINSPECTION_INTERNAL_ERROR", "SYSTEM", e.getMessage())
            );
            res.setMessage("Failed");

            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }
    
    private List<Error> buildError(String code, String field, String message) {

        Error err = new Error();
        err.setCode(code);
        err.setField(field);
        err.setMessage(message);
        err.setMessageLocal(message);

        return Collections.singletonList(err);
    }



}
