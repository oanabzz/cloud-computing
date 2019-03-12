package hw2.model;

public class Place {

    private String id;
    private Double longitude;
    private Double latitude;
    private String name;
    private String userId;
    private Boolean danger;

    public Place(String id, Double longitude, Double latitude, String userId, String name) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
        this.name = name;
        this.danger = false;
    }

    public Place() {
        this.id = null;
        this.latitude = null;
        this.longitude = null;
        this.name = null;
        this.danger = false;
    }

    public String getId() {
        return id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDanger(Boolean danger) {
        this.danger = danger;
    }

    public String getUserId() {

        return userId;
    }

    public Boolean getDanger() {
        return danger;
    }

    public boolean isNull() {
        if (this.danger == null) {
            return true;
        }
        if (this.id == null) {
            return true;
        }
        if (this.latitude == null) {
            return true;
        }
        if (this.longitude == null) {
            return true;
        }
        if (this.name == null) {
            return true;
        }
        if (this.userId == null) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", danger=" + danger +
                '}';
    }
}
