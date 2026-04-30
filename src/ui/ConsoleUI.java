package ui;

import core.Dictionary;
import core.WordlangoGame;
import model.*;

import java.util.*;

public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private final Dictionary dictionary;
    private final WordlangoGame game;

    public ConsoleUI() {
        System.out.println("Cargando diccionario...");
        this.dictionary = new Dictionary("diccionario.txt");
        this.game = new WordlangoGame(dictionary);
    }

    public void start() {
        printWelcome();

        boolean playAgain = true;
        while (playAgain) {
            Difficulty difficulty = selectDifficulty();
            Language language     = selectLanguage();

            game.startGame(difficulty, language);
            playRound();

            playAgain = askPlayAgain();
        }

        System.out.println("\n¡Gracias por jugar Wordlango! Hasta pronto.");
        scanner.close();
    }

    private void printWelcome() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║      Bienvenido a Wordlango   ║");
        System.out.println("║   Adivina la palabra oculta  ║");
        System.out.println("╚══════════════════════════════╝");
    }

    // --- Selección de dificultad ---
    private Difficulty selectDifficulty() {
        System.out.println("\nSelecciona la dificultad:");
        System.out.println("  1) Fácil   — palabra de 5 letras, 6 intentos");
        System.out.println("  2) Medio   — palabra de 6 letras, 7 intentos");
        System.out.println("  3) Difícil — palabra de 7 letras, 8 intentos");
        System.out.print("Opción: ");

        while (true) {
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": return Difficulty.EASY;
                case "2": return Difficulty.MEDIUM;
                case "3": return Difficulty.HARD;
                default:
                    System.out.print("Opción inválida. Elige 1, 2 o 3: ");
            }
        }
    }

    // --- Selección de idioma ---
    private Language selectLanguage() {
        System.out.println("\nSelecciona el idioma:");
        System.out.println("  1) Español");
        System.out.println("  2) Inglés");
        System.out.print("Opción: ");

        while (true) {
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": return Language.SPANISH;
                case "2": return Language.ENGLISH;
                default:
                    System.out.print("Opción inválida. Elige 1 o 2: ");
            }
        }
    }

    private void playRound() {
        System.out.println("\n¡Que empiece el juego! Tienes "
                + game.getRemainingAttempts() + " intentos.\n");

        while (!game.isGameOver()) {
            printBoard();
            printLetters();
            String input = askInput();

            // El jugador pidió pista
            if (input.equalsIgnoreCase("pista")) {
                handleHint();
                continue;
            }

            if (input.length() != game.getDifficulty().getWordLength()) {
                System.out.println("La palabra debe tener "
                        + game.getDifficulty().getWordLength()
                        + " letras. Intenta de nuevo.\n");
                continue;
            }

            GuessResult result = game.submitGuess(input);

            if (result == null) {
                handleUnknownWord(input);
                continue;
            }
        }

        // Fin de la ronda
        printBoard();
        printEndMessage();
    }

    // --- Pedir input al jugador ---
    private String askInput() {
        System.out.print("Escribe una palabra (o 'pista' para pedir ayuda): ");
        return scanner.nextLine().trim().toLowerCase();
    }

    // --- Manejar palabra desconocida ---
    private void handleUnknownWord(String word) {
        System.out.println("'" + word + "' no está en el diccionario.");
        System.out.print("¿Puedes demostrar que es una palabra válida? (s/n): ");
        String resp = scanner.nextLine().trim().toLowerCase();

        if (resp.equals("s")) {
            System.out.print("Escribe la definición de '" + word + "': ");
            String definition = scanner.nextLine().trim();
            game.addWordToDictionary(word, definition);
            System.out.println("Palabra agregada al diccionario. Ahora intenta de nuevo.\n");
        } else {
            System.out.println("Intenta con otra palabra.\n");
        }
    }

    // --- Manejar pistas ---
    private void handleHint() {
        System.out.println("\nTipos de pista:");
        System.out.println("  a) Una consonante de la palabra");
        System.out.println("  b) Una vocal de la palabra");
        System.out.println("  c) Definición de la palabra");
        System.out.println("  d) ¿Existe en el otro idioma?");
        System.out.print("Elige una pista (a/b/c/d): ");

        String input = scanner.nextLine().trim().toLowerCase();
        if (input.length() == 1 && "abcd".contains(input)) {
            System.out.println(game.getHint(input.charAt(0)) + "\n");
        } else {
            System.out.println("Opción de pista no válida.\n");
        }
    }

    // --- Dibujar el tablero de intentos ---
    private void printBoard() {
        System.out.println("\n--- Tablero ---");
        List<GuessResult> history = game.getGuessHistory();

        for (GuessResult guess : history) {
            StringBuilder row    = new StringBuilder();
            StringBuilder marks  = new StringBuilder();

            for (int i = 0; i < guess.getWord().length(); i++) {
                char letter = guess.getWord().charAt(i);
                LetterResult lr = guess.getResults().get(i);

                row.append("[").append(Character.toUpperCase(letter)).append("]");
                marks.append("[").append(getSymbol(lr)).append("]");
            }

            System.out.println(row);
            System.out.println(marks);
            System.out.println();
        }

        // Filas vacías restantes
        int empty = game.getRemainingAttempts();
        if (!game.isGameOver()) {
            int wordLen = game.getDifficulty().getWordLength();
            for (int i = 0; i < empty; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < wordLen; j++) {
                    row.append("[   ]");
                }
                System.out.println(row);
            }
        }

        System.out.println("---------------");
    }

    // --- Símbolo por resultado ---
    private String getSymbol(LetterResult result) {
        switch (result) {
            case CORRECT:   return "✓";  // letra correcta, posición correcta
            case MISPLACED: return "?";  // letra existe pero posición incorrecta
            case ABSENT:    return "✗";  // letra no está en la palabra
            default:        return " ";
        }
    }

    // --- Mostrar letras usadas y disponibles ---
    private void printLetters() {
        Set<Character> used   = game.getUsedLetters();
        Set<Character> unused = game.getUnusedLetters(game.getLanguage());

        // Lambda #5: imprimir letras usadas ordenadas
        System.out.print("Letras usadas:      ");
        used.stream()
                .sorted()
                .forEach(c -> System.out.print(c + " "));
        System.out.println();

        // Lambda #6: imprimir letras disponibles ordenadas
        System.out.print("Letras disponibles: ");
        unused.stream()
                .sorted()
                .forEach(c -> System.out.print(c + " "));
        System.out.println("\n");
    }

    // --- Mensaje de fin de ronda ---
    private void printEndMessage() {
        if (game.isWon()) {
            System.out.println("¡Felicidades! ¡Adivinaste la palabra!");
        } else {
            System.out.println("Se acabaron los intentos.");
            System.out.println("La palabra era: " + game.getSecretWord().toUpperCase());
        }
    }

    // --- Preguntar si jugar de nuevo ---
    private boolean askPlayAgain() {
        System.out.print("\n¿Quieres jugar otra ronda? (s/n): ");
        return scanner.nextLine().trim().toLowerCase().equals("s");
    }
}