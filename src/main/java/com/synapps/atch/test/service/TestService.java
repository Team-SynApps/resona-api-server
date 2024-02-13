package com.synapps.atch.test.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.synapps.atch.test.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public void createTest(Test product) {
        dynamoDBMapper.save(product);
    }

    public Test getTest(String productId) {
        return dynamoDBMapper.load(Test.class, productId);
    }

    public void updateTest(Test product) {
        dynamoDBMapper.save(product);
    }

    public void deleteProduct(String testId) {
        Test test = getTest(testId);
        if (test != null) {
            dynamoDBMapper.delete(test);
        }
    }
}

