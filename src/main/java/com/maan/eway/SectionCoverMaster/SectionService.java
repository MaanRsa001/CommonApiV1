package com.maan.eway.SectionCoverMaster;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final JdbcTemplate jdbcTemplate;

    public SectionResponseDto getAllSectionCoverDetails(SectionRequestDto request) {

        SectionResponseDto response = new SectionResponseDto();
        response.setInsuranceId(request.getInsuranceId());
        response.setProductId(request.getProductId());
        response.setProductName(getProductName(request.getInsuranceId(), request.getProductId()));
        response.setSectionList(getSectionList(request.getInsuranceId(), request.getProductId()));

        return response;
    }

    private String getProductName(String companyId, String productId) {

        String sql = """
                SELECT PRODUCT_NAME
                FROM company_product_master cpm
                WHERE cpm.COMPANY_ID = ?
                  AND cpm.PRODUCT_ID = ?
                  AND cpm.STATUS = 'Y'
                  AND cpm.AMEND_ID = (
                      SELECT MAX(cpm2.AMEND_ID)
                      FROM company_product_master cpm2
                      WHERE cpm2.COMPANY_ID = cpm.COMPANY_ID
                        AND cpm2.PRODUCT_ID = cpm.PRODUCT_ID
                        AND cpm2.STATUS = 'Y'
                  )
                """;

        List<String> list = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("PRODUCT_NAME"),
                companyId,
                productId
        );

        return list.isEmpty() ? "" : list.get(0);
    }

    private List<SectionDto> getSectionList(String companyId, String productId) {

        String sectionSql = """
                SELECT
                    psm.SECTION_ID,
                    psm.SECTION_NAME
                FROM product_section_master psm
                WHERE psm.COMPANY_ID = ?
                  AND psm.PRODUCT_ID = ?
                  AND psm.STATUS = 'Y'
                  AND psm.AMEND_ID = (
                      SELECT MAX(psm2.AMEND_ID)
                      FROM product_section_master psm2
                      WHERE psm2.COMPANY_ID = psm.COMPANY_ID
                        AND psm2.PRODUCT_ID = psm.PRODUCT_ID
                        AND psm2.SECTION_ID = psm.SECTION_ID
                        AND psm2.STATUS = 'Y'
                  )
                  AND EXISTS (
                      SELECT 1
                      FROM section_cover_master scm
                      WHERE scm.COMPANY_ID = psm.COMPANY_ID
                        AND scm.PRODUCT_ID = psm.PRODUCT_ID
                        AND scm.SECTION_ID = psm.SECTION_ID
                        AND scm.STATUS = 'Y'
                        AND scm.COVERAGE_TYPE IN ('B','O')
                  )
                ORDER BY psm.SECTION_ID
                """;

        return jdbcTemplate.query(
                sectionSql,
                (rs, rowNum) -> {
                    SectionDto dto = new SectionDto();

                    String sectionId = rs.getString("SECTION_ID");

                    dto.setSectionId(sectionId);
                    dto.setSectionListName(rs.getString("SECTION_NAME"));
                    dto.setCoverList(getCoverList(companyId, productId, sectionId));

                    return dto;
                },
                companyId,
                productId
        );
    }

    private List<CoverDto> getCoverList(String companyId, String productId, String sectionId) {

        String coverSql = """
                SELECT
                    scm.COVER_ID,
                    scm.COVER_NAME,
                    scm.COVERAGE_TYPE,
                    scm.CALC_TYPE,
                    scm.COVER_BASED_ON,
                    scm.FACTOR_TYPE_ID
                FROM section_cover_master scm
                WHERE scm.COMPANY_ID = ?
                  AND scm.PRODUCT_ID = ?
                  AND scm.SECTION_ID = ?
                  AND scm.STATUS = 'Y'
                  AND scm.COVERAGE_TYPE IN ('B','O')
                  AND scm.AMEND_ID = (
                      SELECT MAX(scm2.AMEND_ID)
                      FROM section_cover_master scm2
                      WHERE scm2.COMPANY_ID = scm.COMPANY_ID
                        AND scm2.PRODUCT_ID = scm.PRODUCT_ID
                        AND scm2.SECTION_ID = scm.SECTION_ID
                        AND scm2.COVER_ID = scm.COVER_ID
                        AND scm2.STATUS = 'Y'
                        AND scm2.COVERAGE_TYPE IN ('B','O')
                  )
                ORDER BY scm.COVER_ID
                """;

        return jdbcTemplate.query(
                coverSql,
                (rs, rowNum) -> {
                    CoverDto dto = new CoverDto();

                    String factorTypeId = rs.getString("FACTOR_TYPE_ID");

                    dto.setCoverId(rs.getString("COVER_ID"));
                    dto.setCoverName(rs.getString("COVER_NAME"));
                    dto.setCoverageType(rs.getString("COVERAGE_TYPE"));
                    dto.setCalculationType(rs.getString("CALC_TYPE"));
                    dto.setCoverBasedOn(rs.getString("COVER_BASED_ON"));
                    dto.setFactorDetails(getFactorDetails(companyId, productId, factorTypeId));

                    return dto;
                },
                companyId,
                productId,
                sectionId
        );
    }

    private FactorDetailsDto getFactorDetails(String companyId, String productId, String factorTypeId) {

        if (factorTypeId == null || factorTypeId.trim().isEmpty() || "0".equals(factorTypeId)) {
            return null;
        }

        String factorSql = """
                SELECT
                    ftd.FACTOR_TYPE_ID,
                    ftd.FACTOR_TYPE_NAME
                FROM factor_type_details ftd
                WHERE ftd.COMPANY_ID = ?
                  AND ftd.PRODUCT_ID = ?
                  AND ftd.FACTOR_TYPE_ID = ?
                  AND ftd.STATUS = 'Y'
                  AND ftd.AMEND_ID = (
                      SELECT MAX(ftd2.AMEND_ID)
                      FROM factor_type_details ftd2
                      WHERE ftd2.COMPANY_ID = ftd.COMPANY_ID
                        AND ftd2.PRODUCT_ID = ftd.PRODUCT_ID
                        AND ftd2.FACTOR_TYPE_ID = ftd.FACTOR_TYPE_ID
                        AND ftd2.STATUS = 'Y'
                  )
                """;

        List<FactorDetailsDto> list = jdbcTemplate.query(
                factorSql,
                (rs, rowNum) -> {
                    FactorDetailsDto dto = new FactorDetailsDto();
                    dto.setFactorId(rs.getString("FACTOR_TYPE_ID"));
                    dto.setFactorName(rs.getString("FACTOR_TYPE_NAME"));
                    dto.setRatingFields(getRatingFields(companyId, productId, factorTypeId));
                    return dto;
                },
                companyId,
                productId,
                factorTypeId
        );

        return list.isEmpty() ? null : list.get(0);
    }

    private List<RatingFieldDto> getRatingFields(String companyId, String productId, String factorTypeId) {

		String ratingSql = """
				    SELECT
				    rfm.RATING_FIELD,
				    rfm.INPUT_TABLE_NAME,
				    rfm.INPUT_COLUMN_NAME,
				    ftd.MASTER_YN,
				    ftd.JSON_KEY,
				    ftd.JSON_KEY_DROPDOWN,
				    COALESCE(ftd.API_URL, rfm.API_URL) AS API_URL,
				    COALESCE(ftd.DISCRETE_DISPLAY_NAME, rfm.RATING_DESC) AS LABEL_NAME
				FROM factor_type_details ftd
				INNER JOIN rating_field_master rfm
				        ON rfm.RATING_ID = ftd.RATING_FIELD_ID
				       AND rfm.PRODUCT_ID = ftd.PRODUCT_ID
				       AND rfm.STATUS = 'Y'
				WHERE ftd.COMPANY_ID = ?
				  AND ftd.PRODUCT_ID = ?
				  AND ftd.FACTOR_TYPE_ID = ?
				  AND ftd.STATUS = 'Y'
				  AND ftd.AMEND_ID = (
				      SELECT MAX(ftd2.AMEND_ID)
				      FROM factor_type_details ftd2
				      WHERE ftd2.COMPANY_ID = ftd.COMPANY_ID
				        AND ftd2.PRODUCT_ID = ftd.PRODUCT_ID
				        AND ftd2.FACTOR_TYPE_ID = ftd.FACTOR_TYPE_ID
				        AND ftd2.RATING_FIELD_ID = ftd.RATING_FIELD_ID  -- ✅ fix
				        AND ftd2.STATUS = 'Y'
				  )
				  AND rfm.AMEND_ID = (
				      SELECT MAX(rfm2.AMEND_ID)
				      FROM rating_field_master rfm2
				      WHERE rfm2.PRODUCT_ID = rfm.PRODUCT_ID
				        AND rfm2.RATING_ID = rfm.RATING_ID
				        AND rfm2.STATUS = 'Y'
				  )
				ORDER BY rfm.RATING_FIELD
    	        """;
        return jdbcTemplate.query(
                ratingSql,
                (rs, rowNum) -> {
                    RatingFieldDto dto = new RatingFieldDto();

                    dto.setRatingFieldName(rs.getString("RATING_FIELD"));
                    dto.setInputTableName(rs.getString("INPUT_TABLE_NAME"));
                    dto.setInputColumnName(rs.getString("INPUT_COLUMN_NAME"));
                    dto.setIsDropdown(rs.getString("MASTER_YN"));
                    dto.setApiUrl(rs.getString("API_URL"));
                    dto.setLabelName(rs.getString("LABEL_NAME"));
                    dto.setJsonKey(rs.getString("JSON_KEY"));
                    dto.setJsonKeyDesc(rs.getString("JSON_KEY_DROPDOWN"));
                    System.out.println(rs.getString("JSON_KEY"));
                    return dto;
                },
                companyId,
                productId,
                factorTypeId
        );
    }
}