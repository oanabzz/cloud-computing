package hw2.util.converter;

import com.amazonaws.services.dynamodbv2.document.Item;
import hw2.model.Place;

public class PlacesConverter implements ObjectConverter {

    private static final String ID = "id";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String NAME = "name";
    private static final String USER_ID = "user_id";
    private static final String DANGER = "danger";

    @Override
    public Item toItem(Object object) {
        Place place = (Place) object;
        return new Item()
                .withPrimaryKey(ID, place.getId())
                .withNumber(LONGITUDE, place.getLongitude())
                .withNumber(LATITUDE, place.getLatitude())
                .withString(NAME, place.getName())
                .withString(USER_ID, place.getUserId())
                .withBoolean(DANGER, place.getDanger());
    }

    @Override
    public Place fromItem(Item item) {
        Place result = new Place(item.getString(ID), item.getDouble(LONGITUDE), item.getDouble(LATITUDE),
                item.getString(USER_ID), item.getString(NAME));
        result.setDanger(item.getBOOL(DANGER));
        return result;
    }
}
