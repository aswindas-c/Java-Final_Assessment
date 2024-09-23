package com.Aspire.Respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Aspire.model.Account;

public interface AccountRepository extends JpaRepository<Account,String>{
    Account findByName(String name);

    @Query("SELECT e FROM Account e WHERE e.id = :id")
    Account findUsingId(String id);
}
