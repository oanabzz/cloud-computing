package hw2.handler.repo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.gson.Gson;
import hw2.data.DynamoDbDao;
import hw2.data.util.Tables;
import hw2.handler.util.Response;
import hw2.model.Place;
import hw2.model.User;
import hw2.util.converter.PlacesConverter;
import hw2.util.converter.UserConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class PlacesRepo {

    private DynamoDbDao dao;
    private PlacesConverter converter;
    private UserConverter userConverter;
    private Gson gson;

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

    public Response getPlace(String username) {

        List<String> places;
        List<Place> placesItem = new ArrayList<>();

        User user = userConverter.fromItem(dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username));
        places = user.getPlaces();
        for (String place : places) {
            placesItem.add(converter.fromItem(dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), place)));
        }
        return new Response(gson.toJson(placesItem), 200);
    }

    public Response getPlaces() {
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
        if (dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), place.getId()) == null) {
            dao.addItem(Tables.PLACES.getName(), converter.toItem(place));
            User user = userConverter.fromItem(dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), place.getUserId()));
            user.addPlace(place.getId());
            dao.addItem(Tables.USERS.getName(), new UserConverter().toItem(user));
            response = new Response("", 201);
        } else
            response = new Response("", 409);
        return response;
    }

    public Response updatePlace(String placeJson, String username, String placeID) {
        Response response;
        System.out.println("update place: " + placeJson);
        Place place = converter.fromItem(dao.getItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), placeID));
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

    public Response deletePlace(String username, String placeID) {
        Response response = new Response("", 200);
        System.out.println("REPO: " + placeID);
        dao.deleteItem(Tables.PLACES.getName(), Tables.PLACES.primaryKey(), placeID);
        User user = userConverter.fromItem(dao.getItem(Tables.USERS.getName(), Tables.USERS.primaryKey(), username));
        user.getPlaces().remove(placeID);
        dao.addItem(Tables.USERS.getName(), userConverter.toItem(user));
        return response;
    }

}
