package com.maan.eway.realpay.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiEndPoints {

    private static String baseUrl;
    private static String clientId;
    private static String clientSecret;

    @Value("${client.url}")
    public void setBaseUrl(String baseUrl) {
        ApiEndPoints.baseUrl = baseUrl; 
    }

    @Value("${client.id}")
    public void setClientId(String clientId) {
        ApiEndPoints.clientId = clientId;
    }

    @Value("${client.secret}")
    public void setClientSecret(String clientSecret) {
        ApiEndPoints.clientSecret = clientSecret;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getClientId() {
        return clientId;
    }

    public static String getClientSecret() {
        return clientSecret;
    }

    public static String getTokenUrl() {
        return baseUrl + "/oauth/token";
    }

    public static String version = "v1";
    
    public static class General {
        public static final String frequencyCodes = baseUrl + "/general/frequency_codes";
        public static final String products = baseUrl + "/general/products";
        public static final String banks = baseUrl + "/general/banks";
        public static final String tracking = baseUrl + "/general/tracking";
        
        public static final String collectionStatuses = baseUrl + "/general/collection_statuses";
        public static final String bankResponses = baseUrl + "/general/bank_responses";
        public static final String employeeGroups = baseUrl + "/general/employee_groups";
        
        public static final String transactionTypes = baseUrl + "/general/transaction_types";
        public static final String beneficiaryUsers = baseUrl + "/general/beneficiary_users";
        public static final String debitSequenceTypes = baseUrl + "/general/debit_sequence_types";
        public static final String mandateStatuses = baseUrl + "/general/mandate_statuses";
        
        public static final String adjustmentCategories = baseUrl + "/general/adjustment_categories";
        public static final String mandateReasonCodes = baseUrl + "/general/mandate_reason_codes";
        public static final String mandateHistoryActions = baseUrl + "/general/mandate_history_actions";
        public static final String payoutCancelCodes = baseUrl + "/general/payout_cancel_codes";
        
        public static final String client = baseUrl + "/maintain/clients";
        public static final String contract = baseUrl + "/maintain/contracts";
        public static final String instalment = baseUrl + "/maintain/instalments";    

        public static final String mandates = baseUrl + "/maintain/mandates";
    }
    
    public static class Reports {
        public static final String contractChangesReport = baseUrl + "/reports/contract_changes_report";
        public static final String instalmentChangesReport = baseUrl + "/reports/instalment_changes_report"; 
    }
}
