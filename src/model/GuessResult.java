package model;

import java.util.List;

public class GuessResult {
    private final String word;
    private final List<LetterResult> results;

    public GuessResult(String word, List<LetterResult> results) {
        this.word = word;
        this.results = results;
    }

    public String getWord() { return word; }
    public List<LetterResult> getResults() { return results; }
}