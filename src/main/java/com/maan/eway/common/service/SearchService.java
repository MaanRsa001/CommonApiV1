package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.res.AccessoriesSumInsureDropDownRes;
import com.maan.eway.common.res.AdminViewQuoteCommonRes;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.BuildingSearchRes;
import com.maan.eway.common.res.DocumentDetailsRes;
import com.maan.eway.common.res.PersonalAccidentRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchPaymentInfoRes;
import com.maan.eway.common.res.SearchPremiumDetailsRes;
import com.maan.eway.common.res.SearchROPDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.DropDownRes;

public interface SearchService {

	List<SearchRes> adminSearchOrderByEntryDate(SearchReq req);

	AdminViewQuoteRes adminViewQuoteDetails(SearchReq req);

	List<DropDownRes> searchDropdown(CopyQuoteDropDownReq req);

	List<SearchCustomerDetailsRes> adminCustomerSearch(SearchReq req);

	List<SearchEservieMotorDetailsViewRatingRes> adminViewRatingDetails(SearchReq req);

	SearchPremiumDetailsRes adminPremiumSearch(SearchReq req);

	SearchROPDetailsRes adminROPDriverSearch(SearchReq req);

	SearchROPVehicleDetailsRes adminROPVehicleSearch(SearchReq req);

	List<SearchPaymentInfoRes> viewPaymentInfo(SearchReq req);

	DocumentDetailsRes viewDocumentDetails(SearchReq req);

	List<PersonalAccidentRes> viewPersonalAccidentDetails(SearchReq req);
	List<BuildingSearchRes> adminSearchBuildingDeatails(SearchReq req);

	AccessoriesSumInsureDropDownRes getAccessoriesSuminsuredByQuoteNo(SearchReq req);
	
	AdminViewQuoteCommonRes adminViewQuoteRiskDetails(SearchReq req);

	ViewQuoteDetailsRes viewQuoteDetails(ViewQuoteDetailsReq req);


}
