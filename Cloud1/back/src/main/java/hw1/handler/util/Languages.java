package hw1.handler.util;

public enum Languages {
    YODA("yoda"),
    PIRATE("pirate"),
    DOTHRAKI("dothraki"),
    MINION("minion"),
    ORCISH("orcish"),
    SHAKESPEARE("shakespeare");


    private String lang;

    Languages(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }
}
