package com.credochain.transactionanalysis.repository;

import com.credochain.transactionanalysis.entity.TransactionInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionInfoRepository extends JpaRepository<TransactionInfoEntity, String> {

    Optional<TransactionInfoEntity> findByPhone(Long phone);
}
