package hw2.handler.repo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import hw2.data.DynamoDbDao;
import hw2.data.util.Tables;
import hw2.handler.util.Response;
import hw2.model.User;
import hw2.util.converter.UserConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class UsersRepo {
    private DynamoDbDao dao;
    private UserConverter converter;
    private Gson gson;

    class PatchUser {
        private String password;
        private String role = "common user";
        private static final String LOCATION = "Location";
        private List<String> places = new LinkedList<>();

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public List<String> getPlaces() {
            return places;
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

    public Response updateUsers(String usersJson) {
        Type listType = new TypeToken<ArrayList<User>>() {
        }.getType();
        List<User> usersToBeUpdated = gson.fromJson(usersJson, listType);
        for (User user : usersToBeUpdated) {
            // if there is a field missing
            if (user.isNull()) {
                return new Response("", 400);
            }
            if (dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), user.getUsername()) == null) {
                return new Response("", 409);
            }
        }
        for (User user : usersToBeUpdated) {
            dao.addItem(Tables.USERS.getName(), converter.toItem(user));
        }
        return new Response("", 200);
    }

    public Response updateUser(String userJson) {
        User userToBeUpdated = gson.fromJson(userJson, User.class);
        if (userToBeUpdated.isNull()) {
            return new Response("", 400);
        }
        if (dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), userToBeUpdated.getUsername()) == null) {
            return new Response("", 429);
        }
        dao.addItem(Tables.USERS.getName(), converter.toItem(userToBeUpdated));
        return new Response("", 200);
    }

    public Response patchUser(String userJson, String username) {
        Response response;
        User user = converter.fromItem(new DynamoDbDao().getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username));
        PatchUser newUser = gson.fromJson(userJson, PatchUser.class);

        if (newUser.getPassword() != null) {
            user.setPassword(newUser.getPassword());
        }
        if (newUser.getPlaces() != null) {
            user.setPlaces(newUser.getPlaces());
            for (String place : newUser.getPlaces()) {
                new PlacesRepo().postPlace(gson.toJson(place), user.getUsername());
            }
        }
        if (newUser.getRole() != null)
            user.setRole(newUser.getRole());
        dao.addItem(Tables.USERS.getName(), converter.toItem(user));
        response = new Response("", 200);
        return response;
    }

    public Response postUser(String userJson) {
        User user = gson.fromJson(userJson, User.class);
        Response response;
        if (checkExistingUser(user.getUsername())) {
            response = new Response("User already exists", 409);
        } else {
            if (user.isNull()) {
                return new Response("", 400);
            }
            dao.addItem(Tables.USERS.getName(), converter.toItem(user));
            response = new Response("", 201);
            //TODO: Location headers
            response.setHeaders("Location", "Location=localhost:6969/users/" + user.getUsername());
        }
        return response;

    }

    public Response deleteUser(String username) {
        Item item = dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username);

        if (item == null) {
            return new Response("", 404);
        }

        User user = converter.fromItem(item);

        // before deleting the user, make sure you delete all the places he has
        for (String place : user.getPlaces()) {
            dao.deleteItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), place);
        }
        dao.deleteItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username);
        return new Response("", 200);
    }

    public Response deleteAllUsers() throws InterruptedException {
        dao.deleteTable(Tables.USERS.getName());
        return new Response("", 200);
    }

    private boolean checkExistingUser(String username) {
        DynamoDbDao dao = new DynamoDbDao();
        if (dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username) == null) {
            return false;
        } else {
            return true;
        }
    }
}
