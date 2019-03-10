package hw2.handler.repo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;
import hw2.data.DynamoDbDao;
import hw2.data.util.Tables;
import hw2.handler.util.Response;
import hw2.model.User;
import hw2.util.converter.UserConverter;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class UsersRepo {
    private static final String SET_COOKIE = "Set-Cookie";
    private DynamoDbDao dao;
    private UserConverter converter;
    private Gson gson;

    class UpdateUser {
        private String password;
        private String role = "common user";
        private List<String> places = new LinkedList<>();
        private String subscription;

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public List<String> getPlaces() {
            return places;
        }

        public String getSubscription() {
            return subscription;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setPlaces(List<String> places) {
            this.places = places;
        }

        public void setSubscription(String subscription) {
            this.subscription = subscription;
        }

    }

    public UsersRepo() {
        this.dao = new DynamoDbDao();
        this.converter = new UserConverter();
        this.gson = new Gson();
    }

    public Response getUser(String username) {
        Response response;
        Item item = dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username);
        if (item != null) {
            User user = converter.fromItem(item);
            response = new Response(gson.toJson(user), 200);
        } else {
            response = new Response("", 404);
        }
        return response;
    }

    public Response getUsers() {
        Response response;
        List<Item> scanResult = dao.getCollection(Tables.USERS.getName());
        if (scanResult.size() == 0) {
            return new Response("", 404);
        }
        List<User> result = new LinkedList<>();
        for (Item item : scanResult) {
            result.add(converter.fromItem(item));
        }
        response = new Response(gson.toJson(result), 200);
        return response;
    }

//    public Response updateUser(String userJson, String username) {
//        Response response;
//        User user = converter.fromItem(new DynamoDbDao().getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username));
//        UpdateUser newUser = gson.fromJson(userJson, UpdateUser.class);
//
//        if (newUser.getPassword() != null) {
//            user.setPassword(newUser.getPassword());
//        }
//        if (newUser.getPlaces() != null) {
//            user.setPlaces(newUser.getPlaces());
//            for (String place : newUser.getPlaces()) {
//                new PlacesRepo().postPlace(gson.toJson(place), user.getUsername());
//            }
//        }
//        if (newUser.getRole() != null)
//            user.setRole(newUser.getRole());
//        dao.addItem(Tables.USERS.getName(), converter.toItem(user));
//        response = new Response("", 200);
//        return response;
//    }

//    public Response postUser(String userJson) {
//        User user = gson.fromJson(userJson, User.class);
//        Response response;
//        if (checkExistingUser(user.getUsername())) {
//            response = new Response("User already exists", 451);
//        } else {
//            user.setSalt(UUID.randomUUID().toString());
//            String hashedPassword = Hashing.sha256()
//                    .hashString(user.getPassword() + user.getSalt(), StandardCharsets.UTF_8)
//                    .toString();
//            user.setPassword(hashedPassword);
//            user.setLoginDate(Long.toString(System.currentTimeMillis()));
//            dao.addItem(Tables.USERS.getName(), converter.toItem(user));
//            SessionCookie cookie = new SessionCookie();
//            cookie.setUsername(user.getUsername());
//            cookie.setRole(user.getRole());
//            String sessionId = Hashing.sha256()
//                    .hashString(user.getUsername() + user.getPassword() + user.getLoginDate(), StandardCharsets.UTF_8)
//                    .toString();
//            cookie.setSessionId(sessionId);
//            response = new Response("", 201);
//            response.setHeaders(SET_COOKIE, "Session=" + gson.toJson(cookie));
//        }
//        return response;
//
//    }

//    private boolean checkExistingUser(String username) {
//        DynamoDbDao dao = new DynamoDbDao();
//        if (dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username) == null) {
//            return false;
//        } else {
//            return true;
//        }
//    }
}
