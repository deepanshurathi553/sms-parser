package com.credochain.transactionanalysis.service;

import com.credochain.transactionanalysis.controller.TooSoonException;
import com.credochain.transactionanalysis.dto.SMSRequest;
import com.credochain.transactionanalysis.dto.TransactionInfo;
import com.credochain.transactionanalysis.entity.MessageEntity;
import com.credochain.transactionanalysis.entity.TransactionInfoEntity;
import com.credochain.transactionanalysis.repository.*;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@Service("TransactionAnalysisService")
class TransactionAnalysisServiceImpl implements TransactionAnalysisService {

    private static final String IS_TRANSACTIONAL = ".*account.*|.*a/c.*|.*acct.*|.*ac.*|.*rs.*|.*debit.*|.*credit.*|" +
            ".*bal.*";

    private static final Logger logger = LoggerFactory.getLogger(TransactionAnalysisService.class);

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

    @Autowired
    private Function<TransactionInfoEntity, TransactionInfo> mapper;

    @Autowired
    private MessageEntityRepository messageEntityRepository;

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    private JobService jobService;

    @Override
    public Integer storeMessagesForProcessing(Long phoneNumber, List<SMSRequest> smsRequests) {
        List<MessageEntity> messageEntities = new ArrayList<>();
        for (SMSRequest sms : smsRequests) {
            String message = sms.getMessage().toLowerCase();
            if (isTransactionalMessage(message)) {
                logger.info("Parsing SMS : " + message);
                message = message.replaceAll("[/\\-:]", "");
                message = message.replaceAll("no. |no.", "");
                message = message.replaceAll("no |no", "");
                message = message.replaceAll("number | number.", " ");
                message = message.replaceAll("acct|account", "ac");
                message = message.replaceAll("ending", "");
                message = message.replaceAll("with", "");
                message = message.replaceAll("of", "");
                message = message.replaceAll("transaction", "");
                message = message.replaceAll("is", "");
                message = message.replaceAll("by", "");
                message = message.replaceAll("rs|rupees", "inr");
                message = message.replaceAll("inr\\.|inr", " inr ");
                message = message.replaceAll("bal|balance", " balance ");
                logger.info("Message after correction : " + message);
                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(sms.getDate()),
                                                             ZoneId.systemDefault());
                messageEntities.add(new MessageEntity(phoneNumber, date, message));
            }
        }
        messageEntityRepository.saveAll(messageEntities);
        logger.info("Stored " + messageEntities.size() + " messages successfully for phone : " + phoneNumber);
        jobScheduler.enqueue(() -> jobService.createAccountsAndLedgers(phoneNumber));
        return messageEntities.size();
    }

    @Override
    public TransactionInfo getTransactionInfo(Long phoneNumber) {
        TransactionInfoEntity entity =
                transactionInfoRepository.findByPhone(phoneNumber)
                                         .orElseThrow(() -> new TooSoonException("Calculation In Progress. Please " +
                                                                                         "wait for some time."));
        return mapper.apply(entity);
    }

    private boolean isTransactionalMessage(String message) {
        return Pattern.matches(IS_TRANSACTIONAL, message);
    }
}
