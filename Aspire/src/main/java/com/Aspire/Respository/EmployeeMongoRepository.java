package com.Aspire.Respository;


import java.time.Instant;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.Aspire.model.Employee;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Repository
public class EmployeeMongoRepository implements EmployeeRepository{

    @Autowired
    private MongoTemplate mongoTemplate;


    public List<Employee> findByStream(String stream) {
        // Create a new Query object
        Query query = new Query();

        // Define the criteria for department
        Criteria criteria = new Criteria();
        criteria.and("stream").is(stream);
        criteria.and("managerId").is(0);
        

        // Add the criteria to the query
        query.addCriteria(criteria);

        // Execute the query and return the results
        return mongoTemplate.find(query, Employee.class);
    }

    public Integer findMaxId() {
        GroupOperation groupById = Aggregation.group().max("_id").as("maxId");
        Aggregation aggregation = Aggregation.newAggregation(groupById);
        AggregationResults<MaxIdResult> results = mongoTemplate.aggregate(aggregation, "employee", MaxIdResult.class);
        MaxIdResult maxIdResult = results.getUniqueMappedResult();
        return maxIdResult != null ? maxIdResult.getMaxId() : null;
    }

    // Inner class to map the aggregation result
    public static class MaxIdResult {
        private Integer maxId;

        public Integer getMaxId() {
            return maxId;
        }

        public void setMaxId(Integer maxId) {
            this.maxId = maxId;
        }
    }


    public Employee save(Employee employee) {
        return mongoTemplate.save(employee);
    }

    public Employee insert(Employee employee) {
        return mongoTemplate.insert(employee);
    }
    

    public boolean existsById(Integer id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, Employee.class);
    }


    public Employee findById(Integer id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Employee.class);
    }
}