package com.credochain.transactionanalysis.service;

import com.credochain.transactionanalysis.dto.SMSRequest;
import com.credochain.transactionanalysis.dto.TransactionInfo;

import java.util.List;

public interface TransactionAnalysisService {

    Integer storeMessagesForProcessing(Long phoneNumber, List<SMSRequest> smsRequests);

    TransactionInfo getTransactionInfo(Long phoneNumber);
}
