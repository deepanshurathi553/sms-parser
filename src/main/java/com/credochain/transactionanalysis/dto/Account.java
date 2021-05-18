package com.credochain.transactionanalysis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Account {

    private String accountNumber;
    private Double averageBalance;
    private Double threeMonthAverageBalance;
    private Double sixMonthAverageBalance;
    private Double totalCredit;
    private Double threeMonthCredit;
    private Double sixMonthCredit;
}
