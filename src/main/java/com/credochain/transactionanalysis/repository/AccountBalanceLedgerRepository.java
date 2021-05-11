package com.credochain.transactionanalysis.repository;

import com.credochain.transactionanalysis.entity.AccountBalanceLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceLedgerRepository extends JpaRepository<AccountBalanceLedger, String> {
}
