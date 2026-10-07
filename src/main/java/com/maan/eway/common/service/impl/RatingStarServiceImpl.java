package com.maan.eway.common.service.impl;


import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.req.RatingStarReq;
import com.maan.eway.common.service.RatingStarService;
import com.maan.eway.repository.HomePositionMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingStarServiceImpl implements RatingStarService {

    @Autowired
    private HomePositionMasterRepository homePositionMasterRepository;
    @Override
    public String saveRating(RatingStarReq request) {
        try {

            HomePositionMaster homeData =
                    homePositionMasterRepository.findByQuoteNo(request.getQuoteNo());

            if (homeData == null) {
                return "FAILED - Quote Not Found";
            }

            // Set rating and feedback
            homeData.setRating(request.getRating());
            homeData.setFeedback(request.getFeedBack());

            homePositionMasterRepository.save(homeData);

            return "SUCCESS...";

        } catch (Exception e) {
            return "FAILED";
        }
    }
}
