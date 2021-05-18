package com.credochain.transactionanalysis.service;

import com.credochain.transactionanalysis.entity.AccountBalanceLedger;
import com.credochain.transactionanalysis.entity.AccountCreditLedger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CalculationUtil {

    private static final Logger logger = LoggerFactory.getLogger(CalculationUtil.class);

    public static double[] getBalanceAmountArray(List<AccountBalanceLedger> balanceLedgers) {
        balanceLedgers.sort(Comparator.comparing(AccountBalanceLedger::getDate));
        if (balanceLedgers.isEmpty()) return new double[]{0.0};
        int size = balanceLedgers.size();
        if (size == 1) return new double[]{balanceLedgers.get(0).getAmount()};
        int diff = (int) ChronoUnit.DAYS.between(balanceLedgers.get(0).getDate(), balanceLedgers.get(size - 1).getDate());
        logger.info("Total Days : " + (diff+1));
        double[] amountArray = new double[diff+1];
        amountArray[0] = balanceLedgers.get(0).getAmount();
        int prev = 0;
        for (int i = 1; i < size; i++) {
            int daysDiff = (int) ChronoUnit.DAYS.between(balanceLedgers.get(i - 1).getDate(),
                                                         balanceLedgers.get(i).getDate());
            if (daysDiff > 0) {
                amountArray[prev + daysDiff] = balanceLedgers.get(i).getAmount();
            } else {
                amountArray[prev] = balanceLedgers.get(i).getAmount();
            }
            prev += daysDiff;
        }
        for (int i = 1; i < amountArray.length; i++) {
            if (amountArray[i] == 0.0) {
                amountArray[i] = amountArray[i - 1];
            }
        }
        logger.info(Arrays.toString(amountArray));
        return amountArray;
    }

    public static double[] getCreditAmountArray(List<AccountCreditLedger> creditLedgers) {
        creditLedgers.sort(Comparator.comparing(AccountCreditLedger::getDate));
        if (creditLedgers.isEmpty()) return new double[]{0.0};
        int size = creditLedgers.size();
        if (size == 1) return new double[]{creditLedgers.get(0).getAmount()};
        int diff = (int) ChronoUnit.DAYS.between(creditLedgers.get(0).getDate(), creditLedgers.get(size - 1).getDate());
        logger.info("Total Days : " + (diff+1));
        double[] amountArray = new double[diff+1];
        amountArray[0] = creditLedgers.get(0).getAmount();
        int prev = 0;
        for (int i = 1; i < size; i++) {
            int daysDiff = (int) ChronoUnit.DAYS.between(creditLedgers.get(i - 1).getDate(),
                                                         creditLedgers.get(i).getDate());
            if (daysDiff > 0) {
                amountArray[prev + daysDiff] = creditLedgers.get(i).getAmount();
            } else {
                amountArray[prev] += creditLedgers.get(i).getAmount();
            }
            prev += daysDiff;
        }
        logger.info(Arrays.toString(amountArray));
        return amountArray;
    }

    public static Double getAverageBalance(double[] amountArray){
        return Arrays.stream(amountArray).average().orElse(0.0);
    }

    public static Double getThreeMonthAverageBalance(double[] amountArray) {
        double last90daysSum = 0.0;
        int size = amountArray.length;
        if (size < 91) {
            return Arrays.stream(amountArray).average().orElse(0.0);
        }
        for (int i = size - 1; i > size - 91; i--) {
            last90daysSum += amountArray[i];
        }
        return last90daysSum / 90;
    }

    public static Double getSixMonthAverageBalance(double[] amountArray) {
        double last180daysSum = 0.0;
        int size = amountArray.length;
        if (size < 181) {
            return Arrays.stream(amountArray).average().orElse(0.0);
        }
        for (int i = size - 1; i > size - 181; i--) {
            last180daysSum += amountArray[i];
        }
        return last180daysSum / 180;
    }

    public static Double getTotalCredit(double[] amountArray) {
        return Arrays.stream(amountArray).sum();
    }

    public static Double getThreeMonthTotalCredit(double[] amountArray) {
        double last90daysSum = 0.0;
        int size = amountArray.length;
        if(size < 91){
            return Arrays.stream(amountArray).sum();
        }
        for (int i = size - 1; i > size - 91; i--) {
            last90daysSum += amountArray[i];
        }
        return last90daysSum;
    }

    public static Double getSixMonthTotalCredit(double[] amountArray) {
        double last180daysSum = 0.0;
        int size = amountArray.length;
        if (size < 181) {
            return Arrays.stream(amountArray).sum();
        }
        for (int i = size - 1; i > size - 181; i--) {
            last180daysSum += amountArray[i];
        }
        return last180daysSum;
    }



}
