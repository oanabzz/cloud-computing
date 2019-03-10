package hw2.util.converter;

import com.amazonaws.services.dynamodbv2.document.Item;

public interface ObjectConverter {
    Item toItem(Object object);

    Object fromItem(Item item);

}