package com.credochain.transactionanalysis.service;

import com.credochain.transactionanalysis.dto.SMSRequest;
import com.credochain.transactionanalysis.dto.TransactionInfo;

import java.util.List;

public interface TransactionAnalysisService {

    TransactionInfo getTransactionInfo(Long phoneNumber, List<SMSRequest> smsRequests);
}
