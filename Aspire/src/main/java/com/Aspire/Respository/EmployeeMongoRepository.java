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

    public List<Employee> findByManagerIdAndDateOfJoiningBefore(Integer managerId, Instant minJoiningDate) {
        // Create query
        Query query = new Query();

        // Add criteria
        Criteria criteria = new Criteria();
        criteria.and("managerId").is(managerId);
        criteria.and("dateOfJoining").lte(minJoiningDate);

        query.addCriteria(criteria);

        // Execute query and return results
        return mongoTemplate.find(query, Employee.class);
    }

    public List<Employee> findByManagerId(Integer managerId) {
        // Create a new Query object
        Query query = new Query();

        // Define the criteria for the managerId
        Criteria criteria = Criteria.where("managerId").is(managerId);

        // Add the criteria to the query
        query.addCriteria(criteria);

        // Execute the query and return the results
        return mongoTemplate.find(query, Employee.class);
    }

    public List<Employee> findByDateOfJoiningBefore(Instant minJoiningDate) {
        // Create a new Query object
        Query query = new Query();

        // Define the criteria for yearsOfExperience
        Criteria criteria = Criteria.where("dateOfJoining").lte(minJoiningDate);

        // Add the criteria to the query
        query.addCriteria(criteria);

        // Execute the query and return the results
        return mongoTemplate.find(query, Employee.class);
    }

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


    public Employee save(Employee Employee) {
        return mongoTemplate.save(Employee);
    }

    public Employee insert(Employee Employee) {
        return mongoTemplate.insert(Employee);
    }
    

    public void delete(Employee Employee) {
        mongoTemplate.remove(Employee);
    }

    public boolean existsById(Integer id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, Employee.class);
    }


    public Employee findById(Integer id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Employee.class);
    }

    public List<Employee> findAll() {
        return mongoTemplate.findAll(Employee.class);
    }
}