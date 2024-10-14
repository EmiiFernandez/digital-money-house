package com.dmh.account_service.repository;

import com.dmh.account_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    Optional<Account> findById(Integer id);

    @Query("SELECT a FROM Account a WHERE a.user_id = ?1")
    Optional<Account> findAccountByUserId(Integer user_id);

}
