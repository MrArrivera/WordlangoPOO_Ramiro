package model;

public class WordEntry {
    private final String definition;
    private final int length;
    private final Language language;

    public WordEntry(String definition, int length, Language language) {
        this.definition = definition;
        this.length = length;
        this.language = language;
    }

    public String getDefinition() { return definition; }
    public int getLength() { return length; }
    public Language getLanguage() { return language; }
}