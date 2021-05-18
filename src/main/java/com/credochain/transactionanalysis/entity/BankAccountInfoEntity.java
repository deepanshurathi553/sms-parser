package com.credochain.transactionanalysis.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "bank_accounts")
public class BankAccountInfoEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String account_info_id;
    @NonNull
    private String accountNumber;
    private Double averageBalance;
    private Double threeMonthAverageBalance;
    private Double sixMonthAverageBalance;
    private Double totalCredit;
    private Double threeMonthCredit;
    private Double sixMonthCredit;
    @NonNull
    private Long phone;

}
