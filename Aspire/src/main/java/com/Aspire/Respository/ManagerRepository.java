package com.Aspire.Respository;

import com.Aspire.model.Manager;

public interface ManagerRepository {
    Manager save(Manager manager);
    Manager insert(Manager manager);
}
