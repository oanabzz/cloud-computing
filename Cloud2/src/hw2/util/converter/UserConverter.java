package hw2.util.converter;

import com.amazonaws.services.dynamodbv2.document.Item;
import hw2.model.User;

public class UserConverter implements ObjectConverter {
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ROLE = "role";
    private static final String PLACES = "places";
    private static final String SALT = "salt";
    private static final String LOGIN_DATE = "loginDate";

    @Override
    public Item toItem(Object object) {
        User user = (User) object;
        return new Item()
                .withPrimaryKey(USERNAME, user.getUsername())
                .withString(PASSWORD, user.getPassword())
                .withString(ROLE, user.getRole())
                .withList(PLACES, user.getPlaces())
                .withString(SALT, user.getSalt())
                .withString(LOGIN_DATE, user.getLoginDate());
    }

    @Override
    public User fromItem(Item item) {
        return new User.Builder(item.getString(USERNAME), item.getString(PASSWORD))
                .places(item.getList(PLACES))
                .role(item.getString(ROLE))
                .salt(item.getString(SALT))
                .loginDate(item.getString(LOGIN_DATE))
                .build();
    }
}
