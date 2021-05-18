package com.credochain.transactionanalysis.service;

import com.credochain.transactionanalysis.dto.Account;
import com.credochain.transactionanalysis.entity.*;
import com.credochain.transactionanalysis.repository.*;
import org.apache.logging.log4j.util.Strings;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.credochain.transactionanalysis.service.CalculationUtil.*;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private MessageEntityRepository messageEntityRepository;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private AccountBalanceLedgerRepository accountBalanceLedgerRepository;

    @Autowired
    private AccountCreditLedgerRepository accountCreditLedgerRepository;

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

    @Job(name = "Create Accounts and Ledgers", retries = 1)
    public void createAccountsAndLedgers(Long phoneNumber) {
        logger.info("Create Accounts and Ledgers Job Beginning for phone : " + phoneNumber);
        List<MessageEntity> messageEntities = messageEntityRepository.findAllByPhone(phoneNumber);
        Map<String, AccountEntity> accountEntityMap = new HashMap<>();
        messageEntities.forEach(entity -> {
            String message = entity.getMessage();
            String[] wordsList = message.split(" ");
            String[] cleanWordsList = Arrays.stream(wordsList).filter(x -> !Strings.isBlank(x))
                                            .toArray(String[]::new);
            logger.info("Transformed message into clean array of words : " + Arrays.toString(cleanWordsList));
            String accountNumber = InfoExtractionUtil.getAccountNumber(cleanWordsList);
            logger.info("Account Number Detected : " + accountNumber);
            if (accountNumber != null) {
                Double balance = InfoExtractionUtil.getBalance(cleanWordsList);
                logger.info("Balance Detected : " + balance);
                String type = InfoExtractionUtil.getTypeOfTransaction(message);
                logger.info("Transaction Type Detected : " + type);
                Double creditAmount = null;
                if (type != null && type.equals("credit")) {
                    creditAmount = InfoExtractionUtil.getTransactionAmount(cleanWordsList);
                }
                if (balance != null || creditAmount != null) {
                    AccountEntity accountEntity;
                    if(accountEntityMap.containsKey(accountNumber)) {
                        accountEntity = accountEntityMap.get(accountNumber);
                    }
                    else {
                        logger.info("Creating New Account Entity : " + accountNumber);
                        accountEntity = new AccountEntity(accountNumber, phoneNumber);
                    }
                    if (balance != null) {
                        AccountBalanceLedger ledger = new AccountBalanceLedger();
                        ledger.setAmount(balance);
                        ledger.setDate(entity.getDate());
                        accountEntity.getBalanceLedgers().add(ledger);
                    }
                    if (creditAmount != null) {
                        logger.info("Credited amount for this credit transaction : " + creditAmount);
                        AccountCreditLedger creditLedger = new AccountCreditLedger();
                        creditLedger.setAmount(creditAmount);
                        creditLedger.setDate(entity.getDate());
                        accountEntity.getCreditLedgers().add(creditLedger);
                    }
                    accountEntityMap.put(accountNumber, accountEntity);
                }
            }
        });
        accountInfoRepository.saveAll(accountEntityMap.values());
        calculateStatsAndStoreTransactionInfo(phoneNumber);
    }

    @Job(name = "Calculate Stats for accounts and create response", retries = 1)
    public void calculateStatsAndStoreTransactionInfo(Long phone){
        TransactionInfoEntity transactionInfoEntity = new TransactionInfoEntity(phone);
        List<AccountEntity> accountEntities = accountInfoRepository.findAllByPhone(phone);
        logger.info("Calculating Averages...........");
        Set<BankAccountInfoEntity> accountInfoEntities = new HashSet<>();
        accountEntities.forEach(entity -> {
            logger.info("For Account : " + entity.getAccountNumber());
            BankAccountInfoEntity accountInfoEntity = new BankAccountInfoEntity(entity.getAccountNumber(), entity.getPhone());
            double[] balanceAmountArray = getBalanceAmountArray(entity.getBalanceLedgers());
            accountInfoEntity.setAverageBalance(getAverageBalance(balanceAmountArray));
            accountInfoEntity.setThreeMonthAverageBalance(getThreeMonthAverageBalance(balanceAmountArray));
            accountInfoEntity.setSixMonthAverageBalance(getSixMonthAverageBalance(balanceAmountArray));

            double[] creditAmountArray = getCreditAmountArray(entity.getCreditLedgers());
            accountInfoEntity.setTotalCredit(getTotalCredit(creditAmountArray));
            accountInfoEntity.setThreeMonthCredit(getThreeMonthTotalCredit(creditAmountArray));
            accountInfoEntity.setSixMonthCredit(getSixMonthTotalCredit(creditAmountArray));

            accountInfoEntities.add(accountInfoEntity);
        });
        transactionInfoEntity.getAccounts().addAll(accountInfoEntities);
        transactionInfoRepository.save(transactionInfoEntity);
        logger.info("------------------------ Job Finished -----------------------------");
    }
}
