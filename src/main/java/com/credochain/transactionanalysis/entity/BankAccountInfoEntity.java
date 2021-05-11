package com.credochain.transactionanalysis.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AccountBalanceLedger> balanceLedgers = new ArrayList<>();
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AccountCreditLedger> creditLedgers = new ArrayList<>();

}
