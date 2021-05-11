package com.credochain.transactionanalysis.mapper;

import com.credochain.transactionanalysis.dto.Account;
import com.credochain.transactionanalysis.dto.TransactionInfo;
import com.credochain.transactionanalysis.entity.TransactionInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("TransactionInfoMapper")
public class TransactionInfoMapper implements Function<TransactionInfoEntity, TransactionInfo> {

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Override
    public TransactionInfo apply(TransactionInfoEntity transactionInfoEntity) {
        Set<Account> accountSet =
                transactionInfoEntity.getAccounts().stream().map(entity -> accountInfoMapper.apply(entity)).collect(
                        Collectors.toSet());
        return TransactionInfo.builder().phone(transactionInfoEntity.getPhone()).accounts(accountSet).build();
    }
}
