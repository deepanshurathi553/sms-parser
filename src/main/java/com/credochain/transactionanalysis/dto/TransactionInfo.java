package com.credochain.transactionanalysis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class TransactionInfo {

    private Long phone;
    private Set<Account> accounts;

}
