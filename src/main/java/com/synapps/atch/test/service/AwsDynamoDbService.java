package com.synapps.atch.test.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.synapps.atch.test.entity.Lyric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsDynamoDbService {

    private final AmazonDynamoDBClient amazonDynamoDBClient;

    @Autowired
    public AwsDynamoDbService(AmazonDynamoDBClient amazonDynamoDBClient) {
        this.amazonDynamoDBClient = amazonDynamoDBClient;
    }

    public void createItem(String id, String _lyric){
        try{
            Lyric lyric = new Lyric(id, _lyric);

            // Save Lyric To DynamoDB
            DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
            mapper.save(lyric);

        } catch(Exception e){
        }
    }
}