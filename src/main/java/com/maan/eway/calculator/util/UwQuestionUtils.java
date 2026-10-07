package com.maan.eway.calculator.util;

import java.util.function.Function;

import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.res.calc.UWReferrals;

public class UwQuestionUtils implements Function<UwQuestionsDetails,UWReferrals>{

	@Override
	public UWReferrals apply(UwQuestionsDetails t) {
		try {
			UWReferrals uw=UWReferrals.builder().questionType(t.getQuestionType()).uwQuestionDesc(t.getUwQuestionDesc()).uwQuestionId(t.getUwQuestionId()).value(t.getValue()).build();
			return uw;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
