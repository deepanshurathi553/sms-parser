package com.credochain.transactionanalysis.repository;

import com.credochain.transactionanalysis.entity.BankAccountInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountInfoRepository extends JpaRepository<BankAccountInfoEntity, String> {

    Optional<BankAccountInfoEntity> findByAccountNumber(String accountNumber);
}
