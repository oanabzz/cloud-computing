package hw2.data;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DynamoDbDao implements DatabaseDao {

    private DatabaseConnection connection;

    public DynamoDbDao() {
        this.connection = new DatabaseConnection();
    }

    @Override
    public Item getItem(String tableName, String primaryColumn, String primaryKey) {
        Table table = connection.getTable(tableName);
        return table.getItem(primaryColumn, primaryKey);
    }

    @Override
    public void addItem(String tableName, Object item) {
        Table table = connection.getTable(tableName);
        table.putItem((Item) item);
    }

    @Override
    public void updateItem(String tableName, String primaryColumn, String primaryKey, Object item) {
        deleteItem(tableName, primaryColumn, primaryKey);
        addItem(tableName, item);
    }

    @Override
    public void deleteItem(String tableName, String primaryColumn, String primaryKey) {
        Table table = connection.getTable(tableName);
        System.out.println("DAO: " + primaryKey);
        System.out.println("TABLE: " + tableName);
        System.out.println("PRIMARY KEY: " + primaryColumn);
        table.deleteItem(primaryColumn, primaryKey);
    }

    @Override
    public void deleteTable(String tableName) throws InterruptedException {
        Table table = connection.getTable(tableName);
        table.delete();
        table.waitForDelete();
    }


    public List<Item> getCollection(String tableName) {
        Table table = connection.getTable(tableName);
        List<Item> result = new LinkedList<>();
        ItemCollection<ScanOutcome> items = table.scan();
        System.out.println("Scan of " + tableName);
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
}
