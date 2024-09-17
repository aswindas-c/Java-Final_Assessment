package com.Aspire.Respository;

import com.Aspire.model.Account;

public interface AccountRepository {
    Account findByName(String name);
}
