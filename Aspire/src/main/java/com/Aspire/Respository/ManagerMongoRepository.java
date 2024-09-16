package com.Aspire.Respository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.Aspire.model.Manager;
@Repository
public class ManagerMongoRepository implements ManagerRepository{
    @Autowired
    private MongoTemplate mongoTemplate;


    public Manager save(Manager manager) {
        return mongoTemplate.save(manager);
    }

    public Manager insert(Manager manager) {
        return mongoTemplate.insert(manager);
    }

}
