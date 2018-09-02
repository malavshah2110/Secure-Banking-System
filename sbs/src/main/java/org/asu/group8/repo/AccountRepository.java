package org.asu.group8.repo;

import org.asu.group8.entity.SbsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<SbsAccount, Long> {

    @Query("SELECT a FROM SbsAccount a WHERE a.cardNumber = :cardNumber")
    SbsAccount findBycardNumber(@Param("cardNumber") String cardNumber);

    @Query("SELECT a FROM SbsAccount a WHERE a.accountNumber = :accountNumber")
    SbsAccount findByAccountNumber(@Param("accountNumber") Long accountNumber);

}
