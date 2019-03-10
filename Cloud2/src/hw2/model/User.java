package hw2.model;

import java.util.*;

public class User {

    private static final String COMMON_USER = "common user";
    private static final String ADMIN = "admin";

    private String username;
    private String password;
    private String role;
    private List<String> places;
    private String salt;
    private String loginDate;

    public String getSalt() {
        return salt;
    }

    public String getLoginDate() {
        return loginDate;
    }


    public static class Builder {
        private String username;
        private String password;
        private String role = COMMON_USER;
        private List<String> places = new LinkedList<>();
        private String salt;
        private String loginDate;

        public Builder(String username, String password) {
            this.username = username;
            this.password = password;
            this.salt = UUID.randomUUID().toString();
            this.loginDate = Long.toString(System.currentTimeMillis());
        }

        public Builder places(List<String> places) {
            this.places = places;
            return this;
        }

        public Builder withPlace(String place) {
            this.places.add(place);
            return this;
        }

        public Builder adminRole() {
            this.role = ADMIN;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder loginDate(String loginDate) {
            this.loginDate = loginDate;
            return this;
        }


        public Builder salt(String salt) {
            this.salt = salt;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.role = builder.role;
        this.places = builder.places;

        this.salt = builder.salt;
        this.loginDate = builder.loginDate;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public List<String> getPlaces() {
        return places;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void addPlace(String place){
        this.places.add(place);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", places=" + places +
                ", salt='" + salt + '\'' +
                ", loginDate='" + loginDate + '\'' +
                '}';
    }
}
