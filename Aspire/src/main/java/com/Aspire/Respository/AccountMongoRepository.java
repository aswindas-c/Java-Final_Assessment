package com.Aspire.Respository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.Aspire.model.Account;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Repository
public class AccountMongoRepository implements AccountRepository{

    @Autowired
    private MongoTemplate mongoTemplate;
    
    public Account findByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.findOne(query, Account.class);
    }

}
