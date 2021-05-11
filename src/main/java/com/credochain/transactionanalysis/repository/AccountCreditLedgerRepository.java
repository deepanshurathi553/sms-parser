package com.credochain.transactionanalysis.repository;

import com.credochain.transactionanalysis.entity.AccountCreditLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCreditLedgerRepository extends JpaRepository<AccountCreditLedger, String> {
}
