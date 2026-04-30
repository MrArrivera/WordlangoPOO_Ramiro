package model;

public enum Difficulty {
    EASY(5, 6),
    MEDIUM(6, 7),
    HARD(7, 8);

    private final int wordLength;
    private final int maxAttempts;

    Difficulty(int wordLength, int maxAttempts) {
        this.wordLength = wordLength;
        this.maxAttempts = maxAttempts;
    }

    public int getWordLength() { return wordLength; }
    public int getMaxAttempts() { return maxAttempts; }
}