package core;

import model.*;

import java.util.*;

public class WordlangoGame {

    // --- Estado del juego ---
    private String secretWord;
    private Difficulty difficulty;
    private Language language;
    private int remainingAttempts;
    private boolean won;
    private boolean gameOver;

    // HashSet para llevar control de letras usadas
    private final HashSet<Character> usedLetters = new HashSet<>();

    // Historial de intentos para mostrar en pantalla
    private final List<GuessResult> guessHistory = new ArrayList<>();

    private final Dictionary dictionary;

    public WordlangoGame(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    // --- Iniciar una nueva partida ---
    public void startGame(Difficulty difficulty, Language language) {
        this.difficulty       = difficulty;
        this.language         = language;
        this.remainingAttempts = difficulty.getMaxAttempts();
        this.won              = false;
        this.gameOver         = false;
        this.usedLetters.clear();
        this.guessHistory.clear();
        this.secretWord = dictionary.getRandomWord(difficulty.getWordLength(), language);
    }

    // --- Enviar un intento ---
    // Devuelve null si la palabra no es válida o tiene longitud incorrecta
    public GuessResult submitGuess(String guess) {
        guess = guess.toLowerCase().trim();

        // Validar longitud
        if (guess.length() != difficulty.getWordLength()) {
            return null;
        }

        // Validar que esté en el diccionario
        if (!dictionary.contains(guess)) {
            return null;
        }

        // Registrar letras usadas en el HashSet
        for (char c : guess.toCharArray()) {
            usedLetters.add(c);
        }

        List<LetterResult> results = evaluateGuess(guess);

        GuessResult guessResult = new GuessResult(guess, results);
        guessHistory.add(guessResult);

        remainingAttempts--;

        // Verificar si ganó
        // Lambda #2: verificar que todos los resultados sean CORRECT
        boolean allCorrect = results.stream()
                .allMatch(r -> r == LetterResult.CORRECT);

        if (allCorrect) {
            won      = true;
            gameOver = true;
        } else if (remainingAttempts == 0) {
            gameOver = true;
        }

        return guessResult;
    }

    private List<LetterResult> evaluateGuess(String guess) {
        LetterResult[] results = new LetterResult[secretWord.length()];
        int[] secretLetterCount = new int[256];

        // marcar las correctas y contar letras disponibles
        for (int i = 0; i < secretWord.length(); i++) {
            if (guess.charAt(i) == secretWord.charAt(i)) {
                results[i] = LetterResult.CORRECT;
            } else {
                secretLetterCount[secretWord.charAt(i)]++;
            }
        }

        // marcar MISPLACED o ABSENT
        for (int i = 0; i < guess.length(); i++) {
            if (results[i] != null) continue;

            char c = guess.charAt(i);
            if (secretLetterCount[c] > 0) {
                results[i] = LetterResult.MISPLACED;
                secretLetterCount[c]--;
            } else {
                results[i] = LetterResult.ABSENT;
            }
        }

        return Arrays.asList(results);
    }

    // --- Agregar palabra nueva al diccionario si el jugador la demuestra ---
    public void addWordToDictionary(String word, String definition) {
        dictionary.addWord(word, definition, language);
    }

    // --- Pistas ---
    // tipo: 'a' = consonante, 'b' = vocal, 'c' = definicion, 'd' = idioma alternativo
    public String getHint(char hintType) {
        switch (hintType) {
            case 'a':
                char consonant = dictionary.getRandomConsonant(secretWord);
                return "Pista: la palabra contiene la consonante '" + consonant + "'";

            case 'b':
                char vowel = dictionary.getRandomVowel(secretWord);
                return "Pista: la palabra contiene la vocal '" + vowel + "'";

            case 'c':
                return "Pista (definición): " + dictionary.getDefinition(secretWord);

            case 'd':
                Language other = (language == Language.SPANISH)
                        ? Language.ENGLISH
                        : Language.SPANISH;
                // Lambda #3: buscar si existe en el otro idioma
                boolean existsInOther = dictionary.existsInLanguage(secretWord, other);
                if (existsInOther) {
                    return "Pista: esta palabra también existe en "
                            + other.name().toLowerCase();
                } else {
                    return "Pista: esta palabra solo existe en "
                            + language.name().toLowerCase();
                }

            default:
                return "Tipo de pista no válido.";
        }
    }

    // --- Getters de estado ---
    public boolean isGameOver()          { return gameOver; }
    public boolean isWon()               { return won; }
    public int getRemainingAttempts()    { return remainingAttempts; }
    public Difficulty getDifficulty()    { return difficulty; }
    public Language getLanguage()        { return language; }
    public String getSecretWord()        { return secretWord; } // solo para mostrar al perder
    public List<GuessResult> getGuessHistory() { return Collections.unmodifiableList(guessHistory); }

    public Set<Character> getUsedLetters() {
        return Collections.unmodifiableSet(usedLetters);
    }

    // Letras NO usadas
    // Lambda #4: filtrar el alfabeto contra el HashSet
    public Set<Character> getUnusedLetters(Language lang) {
        String alphabet = (lang == Language.SPANISH)
                ? "abcdefghijklmnñopqrstuvwxyz"
                : "abcdefghijklmnopqrstuvwxyz";

        HashSet<Character> unused = new HashSet<>();
        alphabet.chars()
                .mapToObj(c -> (char) c)
                .filter(c -> !usedLetters.contains(c))
                .forEach(unused::add);

        return unused;
    }
}