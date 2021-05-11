package com.credochain.transactionanalysis.service;

import com.credochain.transactionanalysis.dto.SMSRequest;
import com.credochain.transactionanalysis.dto.TransactionInfo;
import com.credochain.transactionanalysis.entity.AccountBalanceLedger;
import com.credochain.transactionanalysis.entity.AccountCreditLedger;
import com.credochain.transactionanalysis.entity.BankAccountInfoEntity;
import com.credochain.transactionanalysis.entity.TransactionInfoEntity;
import com.credochain.transactionanalysis.repository.AccountBalanceLedgerRepository;
import com.credochain.transactionanalysis.repository.AccountCreditLedgerRepository;
import com.credochain.transactionanalysis.repository.BankAccountInfoRepository;
import com.credochain.transactionanalysis.repository.TransactionInfoRepository;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private BankAccountInfoRepository accountInfoRepository;

    @Autowired
    private AccountBalanceLedgerRepository accountBalanceLedgerRepository;

    @Autowired
    private AccountCreditLedgerRepository accountCreditLedgerRepository;

    @Autowired
    private Function<TransactionInfoEntity, TransactionInfo> mapper;

    @Override
    public TransactionInfo getTransactionInfo(Long phoneNumber, List<SMSRequest> smsRequests) {
        // Validations?
        TransactionInfoEntity entity = new TransactionInfoEntity(phoneNumber);
        Set<BankAccountInfoEntity> accountInfoEntities = new HashSet<>();
        smsRequests.forEach(sms -> {
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
                message = message.replaceAll("is", "");
                message = message.replaceAll("by", "");
                message = message.replaceAll("rs|rupees", "inr");
                message = message.replaceAll("inr\\.|inr", " inr ");
                message = message.replaceAll("bal|balance", " balance ");
                logger.info("Message after correction : " + message);
                String[] wordsList = message.split(" ");
                String[] cleanWordsList = Arrays.stream(wordsList).filter(x -> !Strings.isBlank(x))
                                                .toArray(String[]::new);
                logger.info("Transformed message into clean array of words : " + Arrays.toString(cleanWordsList));
                String accountNumber = InfoExtractionUtil.getAccountNumber(cleanWordsList);
                logger.info("Account Number Detected : " + accountNumber);

                if (accountNumber != null) {
                    BankAccountInfoEntity accountInfoEntity =
                            accountInfoRepository.findByAccountNumber(accountNumber)
                                                 .orElse(new BankAccountInfoEntity(accountNumber));
                    Double balance = InfoExtractionUtil.getBalance(cleanWordsList);
                    logger.info("Balance Detected : " + balance);
                    String type = InfoExtractionUtil.getTypeOfTransaction(message);
                    LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(sms.getDate()),
                                                                 ZoneId.systemDefault());
                    if (balance != null) {
                        AccountBalanceLedger ledger = new AccountBalanceLedger();
                        ledger.setAmount(balance);
                        ledger.setDate(date);
                        AccountBalanceLedger ledgerEntity = accountBalanceLedgerRepository.save(ledger);
                        accountInfoEntity.getBalanceLedgers().add(ledgerEntity);
                    }
                    if (type != null && type.equals("credit")) {
                        Double creditAmount = InfoExtractionUtil.getTransactionAmount(cleanWordsList);
                        logger.info("Credited amount for this credit transaction : " + creditAmount);
                        if (creditAmount != null) {
                            AccountCreditLedger creditLedger = new AccountCreditLedger();
                            creditLedger.setAmount(creditAmount);
                            creditLedger.setDate(date);
                            AccountCreditLedger creditLedgerEntity = accountCreditLedgerRepository.save(creditLedger);
                            accountInfoEntity.getCreditLedgers().add(creditLedgerEntity);
                        }
                    }
                    accountInfoEntities.add(accountInfoRepository.save(accountInfoEntity));
                }
            }
        });
        entity.setAccounts(calculateStats(accountInfoEntities));
        TransactionInfoEntity savedEntity = transactionInfoRepository.save(entity);
        return mapper.apply(savedEntity);
    }

    private Set<BankAccountInfoEntity> calculateStats(Set<BankAccountInfoEntity> accountInfoEntities) {
        accountInfoEntities.forEach(entity -> {
            double[] amountArray = getAmountArray(entity.getBalanceLedgers());
            entity.setAverageBalance(Arrays.stream(amountArray).average().orElse(0.0));
            entity.setThreeMonthAverageBalance(getThreeMonthAverageBalance(amountArray));
            entity.setSixMonthAverageBalance(getSixMonthAverageBalance(amountArray));
            entity.setTotalCredit(getTotalCredit(entity.getCreditLedgers()));
        });
        return accountInfoEntities;
    }

    private Double getTotalCredit(List<AccountCreditLedger> creditLedgers) {
        return creditLedgers.stream().mapToDouble(AccountCreditLedger::getAmount).sum();
    }

    private double[] getAmountArray(List<AccountBalanceLedger> balanceLedgers) {
        balanceLedgers.sort(Comparator.comparing(AccountBalanceLedger::getDate));
        if (balanceLedgers.isEmpty()) return new double[]{0.0};
        int size = balanceLedgers.size();
        if (size == 1) return new double[]{balanceLedgers.get(0).getAmount()};
        int diff = (int) ChronoUnit.DAYS
                .between(balanceLedgers.get(0).getDate(), balanceLedgers.get(size - 1).getDate());
        double[] amountArray = new double[diff];
        amountArray[0] = balanceLedgers.get(0).getAmount();
        int prev = 0;
        for (int i = 1; i < size; i++) {
            int daysDiff = (int) ChronoUnit.DAYS.between(balanceLedgers.get(i - 1).getDate(),
                                                         balanceLedgers.get(i).getDate());
            if (daysDiff > 0) {
                amountArray[daysDiff] = balanceLedgers.get(i).getAmount();
                prev += daysDiff;
            } else {
                amountArray[prev] = balanceLedgers.get(i).getAmount();
            }
        }
        for (int i = 1; i < amountArray.length; i++) {
            if (amountArray[i] == 0.0) {
                amountArray[i] = amountArray[i - 1];
            }
        }
        return amountArray;
    }


    private Double getThreeMonthAverageBalance(double[] amountArray) {
        double last90daysSum = 0.0;
        int size = amountArray.length;
        if (size < 91) {
            return Arrays.stream(amountArray).average().orElse(0.0);
        }
        for (int i = size - 1; i > size - 91; i--) {
            last90daysSum += amountArray[i];
        }
        return last90daysSum/90;
    }

    private Double getSixMonthAverageBalance(double[] amountArray) {
        double last180daysSum = 0.0;
        int size = amountArray.length;
        if (size < 181) {
            return Arrays.stream(amountArray).average().orElse(0.0);
        }
        for (int i = size - 1; i > size - 181; i--) {
            last180daysSum += amountArray[i];
        }
        return last180daysSum/180;
    }

    private boolean isTransactionalMessage(String message) {
        return Pattern.matches(IS_TRANSACTIONAL, message);
    }
}