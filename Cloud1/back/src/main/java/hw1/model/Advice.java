package hw1.model;

public class Advice {
    private String original;
    private String translation;
    private String language;
    private String fileName;

    public Advice() {
    }

    public Advice(String original, String translation, String language, String fileName) {
        this.original = original;
        this.translation = translation;
        this.language = language;
        this.fileName = fileName;
    }

    public String getTranslation() {
        return translation;
    }

    public String getFileName() {
        return fileName;
    }

    public String getOriginal() {
        return original;
    }

    public String getLanguage() {
        return language;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
