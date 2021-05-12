package com.credochain.transactionanalysis.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "transaction_info")
public class TransactionInfoEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String transaction_info_id;
    @NonNull
    private Long phone;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<BankAccountInfoEntity> accounts = new HashSet<>();
}
