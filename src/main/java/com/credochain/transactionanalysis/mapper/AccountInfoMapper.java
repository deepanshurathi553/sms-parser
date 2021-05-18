package com.credochain.transactionanalysis.mapper;

import com.credochain.transactionanalysis.dto.Account;
import com.credochain.transactionanalysis.entity.BankAccountInfoEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("AccountInfoMapper")
public class AccountInfoMapper implements Function<BankAccountInfoEntity, Account> {

    @Override
    public Account apply(BankAccountInfoEntity entity) {
        return Account.builder().accountNumber(entity.getAccountNumber())
                      .averageBalance(entity.getAverageBalance())
                      .totalCredit(entity.getTotalCredit())
                      .threeMonthAverageBalance(entity.getThreeMonthAverageBalance())
                      .sixMonthAverageBalance(entity.getSixMonthAverageBalance())
                      .threeMonthCredit(entity.getThreeMonthCredit())
                      .sixMonthCredit(entity.getSixMonthCredit()).build();
    }
}
