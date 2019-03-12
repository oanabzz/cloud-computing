package hw2.data;

public interface DatabaseDao {
    Object getItem(String tableName, String primaryColumn, String primaryKey);

    void addItem(String tableName, Object item);

    void updateItem(String tableName, String primaryColumn, String primaryKey, Object item);

    void deleteItem(String tableName, String primaryColumn, String primaryKey);

    void deleteTable(String tableName) throws InterruptedException;
}
