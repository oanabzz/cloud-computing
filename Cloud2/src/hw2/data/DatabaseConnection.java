package hw2.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DatabaseConnection {
    public final static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private final static DynamoDB dynamoDB = new DynamoDB(client);

    public Table getTable(String tableName) {
        return dynamoDB.getTable(tableName);
    }

    public static AmazonDynamoDB getClient(){
        return client;
    }

}
