package org.asu.group8.repo;

import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<SbsTransaction, Long> {

    @Query("SELECT t FROM SbsTransaction t WHERE (t.fromAccount = :accountNumber OR t.toAccount = :accountNumber) AND t.completed = true")
    List<SbsTransaction> findCompletedByAccount(@Param("accountNumber") Long accountNumber);

    @Query("SELECT t FROM SbsTransaction t WHERE t.fromAccount = :fromAccount AND t.timestamp > :timestamp")
    List<SbsTransaction> findAllFromAfter(@Param("fromAccount") Long fromAccount, @Param("timestamp") Date date);

}
