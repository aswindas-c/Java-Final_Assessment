package com.Aspire.Respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Aspire.model.Account;

public interface AccountRepository extends JpaRepository<Account,String>{
    Account findByName(String name);
}
