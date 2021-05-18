package com.credochain.transactionanalysis.repository;

import com.credochain.transactionanalysis.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountEntity, String> {

    List<AccountEntity> findAllByPhone(Long phone);

}
