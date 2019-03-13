package hw2.handler.repo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hw2.data.DynamoDbDao;
import hw2.data.util.Tables;
import hw2.handler.util.Response;
import hw2.model.Place;
import hw2.model.User;
import hw2.util.converter.PlacesConverter;
import hw2.util.converter.UserConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class PlacesRepo {

    private DynamoDbDao dao;
    private PlacesConverter converter;
    private UserConverter userConverter;
    private Gson gson;
    private static final String LOCATION = "Location";

    public PlacesRepo() {
        this.dao = new DynamoDbDao();
        this.converter = new PlacesConverter();
        this.gson = new Gson();
        this.userConverter = new UserConverter();
    }

    class UpdatePlace {
        private Double longitude;
        private Double latitude;
        private String name;
        private Boolean danger;

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getDanger() {
            return danger;
        }

        public void setDanger(Boolean danger) {
            this.danger = danger;
        }
    }

    public Response getPlacesOfUser(String username) {

        List<String> places;
        List<Place> placesItem = new ArrayList<>();

        Item item = dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username);
        if (item == null) {
            return new Response("", 404);
        }
        User user = userConverter.fromItem(item);
        places = user.getPlaces();
        for (String place : places) {
            placesItem.add(converter.fromItem(dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), place)));
        }
        return new Response(gson.toJson(placesItem), 200);
    }

    public Response getAllPlaces() {
        Response response;
        List<Item> scanResult = dao.getCollection(Tables.PLACES.getName());
        if (scanResult.size() == 0) {
            return new Response("", 404);
        }
        List<Place> result = new LinkedList<>();
        for (Item item : scanResult) {
            result.add(converter.fromItem(item));
        }
        response = new Response(gson.toJson(result), 200);
        return response;
    }

    public Response getPlaceById(String username, String placeID) {
        Response response = new Response();

        Item item = dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), placeID);
        if (item != null) {
            Place place = converter.fromItem(item);
            response.setCode(200);
            response.setBody(gson.toJson(place));
        } else
            response = new Response("{\"message\" : \"place was not found\"}", 404);
        return response;
    }

    public Response postPlace(String placeJson, String username) {
        Response response;
        Place place = gson.fromJson(placeJson, Place.class);

        if (place.postIsNull()) {
            return new Response("", 400);
        }

        // if there is no user with the given username
        Item item = dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username);
        if (item == null) {
            return new Response("", 404);
        }

        // compute place id
        Integer max = 1;
        User user = userConverter.fromItem(item);

        for (String id : user.getPlaces()) {
            Integer current = Integer.parseInt(id.split("_")[1]);
            if (current > max) {
                max = current;
            }
        }
        Integer id = max + 1;
        String placeID = username + "_" + id.toString();
        place.setId(placeID);

        // override the place in the DB
        dao.addItem(Tables.PLACES.getName(), converter.toItem(place));
        // update the user, adik add the place to the user's list
        user.addPlace(place.getId());
        dao.addItem(Tables.USERS.getName(), new UserConverter().toItem(user));
        response = new Response("", 201);
        response.setHeaders(LOCATION, "/places/" + username + "/" + placeID);
        return response;
    }

    public Response patchPlace(String placeJson, String username, String placeID) {
        Response response;
        System.out.println("update place: " + placeJson);

        // if there is no such place lol
        Item item = dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), placeID);
        if (item == null) {
            return new Response("", 404);
        }

        Place place = converter.fromItem(item);
        UpdatePlace newPlace = gson.fromJson(placeJson, UpdatePlace.class);
        if (newPlace.getDanger() != null) {
            place.setDanger(newPlace.getDanger());
        }
        if (newPlace.getLatitude() != null) {
            place.setLatitude(newPlace.getLatitude());
        }
        if (newPlace.getLongitude() != null) {
            place.setLongitude(newPlace.getLongitude());
        }
        if (newPlace.getName() != null) {
            place.setName(newPlace.getName());
        }
        dao.addItem(Tables.PLACES.getName(), converter.toItem(place));
        response = new Response("", 200);
        return response;
    }

    public Response updatePlace(String placeJson, String username, String placeId) {
        Response response;

        // check if there is an object to be updated
        Item item = dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), placeId);
        if (item == null) {
            return new Response("", 409);
        }

        // aaaand add it
        Place place = gson.fromJson(placeJson, Place.class);

        if (place.isNull()) {
            return new Response("", 400);
        }

        dao.addItem(Tables.PLACES.getName(), converter.toItem(place));
        response = new Response("", 200);
        return response;
    }

    public Response updatePlaces(String placesJson) {
        Response response;
        Type listType = new TypeToken<ArrayList<Place>>() {
        }.getType();
        List<Place> placesToBeUpdated = gson.fromJson(placesJson, listType);
        for (Place place : placesToBeUpdated) {
            if (place.isNull()) {
                return new Response("", 400);
            }
            if (dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), place.getId()) == null) {
                return new Response("", 409);
            }
        }
        for (Place place : placesToBeUpdated) {
            dao.addItem(Tables.PLACES.getName(), converter.toItem(place));
        }

        return new Response("", 200);
    }


    public Response deletePlace(String username, String placeID) {
        Response response = new Response("", 200);
        System.out.println("REPO: " + placeID);
        if (dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username) == null) {
            return new Response("", 404);
        }
        if (dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), placeID) == null) {
            return new Response("", 404);
        }
        dao.deleteItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), placeID);
        User user = userConverter.fromItem(dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username));
        user.getPlaces().remove(placeID);
        dao.addItem(Tables.USERS.getName(), userConverter.toItem(user));
        return response;
    }

    public Response deleteAllPlaces() {
        Response response;
        List<Item> scanResult = dao.getCollection(Tables.PLACES.getName());
        if (scanResult.size() == 0) {
            return new Response("", 404);
        }
        List<Place> result = new LinkedList<>();
        for (Item item : scanResult) {
            result.add(converter.fromItem(item));
        }
        for (Place place : result) {
            deletePlace(place.getUserId(), place.getId());
        }


        // delete all the list of places from all users
        List<Item> userScan = dao.getCollection(Tables.USERS.getName());
        List<User> userResult = new LinkedList<>();
        for (Item item : userScan) {
            userResult.add(userConverter.fromItem(item));
        }
        for (User user : userResult) {
            user.setPlaces(new LinkedList<>());
            dao.addItem(Tables.USERS.getName(), userConverter.toItem(user));
        }
        response = new Response("", 200);
        return response;
    }

    public Response deleteAllUsersPlaces(String username) {
        Response response = new Response("", 200);
        Item item = dao.getItem(Tables.USERS.getName(), Tables.USERS.getName(), username);
        if (item == null) {
            return new Response("", 404);
        }

        User user = userConverter.fromItem(item);
        // delete every entry from places table
        for (String id : user.getPlaces()) {
            dao.deleteItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), id);
        }

        // update user with an empty places list
        user.setPlaces(new LinkedList<>());
        dao.addItem(Tables.USERS.getName(), userConverter.toItem(user));

        return response;
    }

}
