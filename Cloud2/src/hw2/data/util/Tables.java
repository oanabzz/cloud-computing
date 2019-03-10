package hw2.data.util;

public enum Tables {
    USERS("users","username"),
    CRISES("crises", "id"),
    PLACES("places", "id");

    private String name;
    private String primaryKey;

    Tables(String name, String primaryKey) {
        this.name = name;
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public String primaryKey(){
        return primaryKey;
    }
}
