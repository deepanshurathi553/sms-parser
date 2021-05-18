package com.credochain.transactionanalysis.repository;

import com.credochain.transactionanalysis.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageEntityRepository extends JpaRepository<MessageEntity, String> {

    List<MessageEntity> findAllByPhone(Long phone);
}
