package com.Aspire.Respository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.Aspire.model.Stream;

@Repository
public class StreamMongoRepository implements StreamRepository{

    @Autowired
    private MongoTemplate mongoTemplate;
    
    public Stream findByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.findOne(query, Stream.class);
    }

    public Stream save(Stream stream) {
        return mongoTemplate.save(stream);
    }
}
