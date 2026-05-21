package ui;

import core.Dictionary;
import core.WordlangoGame;
import model.*;
import ui.buttons.*;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

/**
 * WordlangoGUI es la interfaz gráfica principal del juego WordLango, construida con JavaFX.
 * <p>
 * La GUI incluye:
 * <ul>
 *   <li>Pantalla de configuración para elegir dificultad e idioma</li>
 *   <li>Tablero de intentos con colores tipo Wordle (verde/amarillo/gris)</li>
 *   <li>Teclado virtual con {@link ShapeButton} que cambia de color según las letras usadas</li>
 *   <li>Panel de acciones con {@link GradientButton} para enviar, pedir pista y nuevo juego</li>
 *   <li>Diálogo de pistas con cuatro categorías</li>
 *   <li>Mensaje de victoria/derrota con animación</li>
 * </ul>
 *
 * <p>Las clases de botones utilizadas son subclases de {@link Button}:
 * {@link ShapeButton} (teclado) y {@link GradientButton} (acciones).
 *
 * @author Práctica 7 — Herencia y GUI
 */
public class WordlangoGUI extends Application {

    // ---------------------------------------------------------------
    // Constantes de diseño
    // ---------------------------------------------------------------
    private static final String COLOR_CORRECT   = "#538D4E"; // verde — letra correcta
    private static final String COLOR_MISPLACED = "#B59F3B"; // amarillo — letra en otro lugar
    private static final String COLOR_ABSENT    = "#3A3A3C"; // gris — letra ausente
    private static final String COLOR_EMPTY     = "#1C1C1E"; // fondo celda vacía
    private static final String COLOR_BG        = "#121213"; // fondo general
    private static final String COLOR_TEXT       = "#FFFFFF"; // texto principal

    // Tamaño de cada celda del tablero
    private static final double CELL_SIZE = 56;

    // ---------------------------------------------------------------
    // Estado del juego
    // ---------------------------------------------------------------
    private Dictionary      dictionary;
    private WordlangoGame   game;
    private Difficulty      selectedDifficulty = Difficulty.EASY;
    private Language        selectedLanguage   = Language.SPANISH;

    // ---------------------------------------------------------------
    // Componentes de la UI
    // ---------------------------------------------------------------
    private Stage      primaryStage;
    private VBox       boardGrid;         // filas del tablero
    private Label      messageLabel;      // mensaje de estado
    private TextField  inputField;        // campo de texto
    private FlowPane   keyboardPane;      // teclado virtual
    private Label      attemptsLabel;     // intentos restantes
    private ShapeButton[] keyButtons;     // botones del teclado

    // ---------------------------------------------------------------
    // JavaFX entry point
    // ---------------------------------------------------------------

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        dictionary = new Dictionary("diccionario.txt");
        game       = new WordlangoGame(dictionary);

        stage.setTitle("WordLango — Adivina la palabra");
        stage.setResizable(false);
        showConfigScreen();
        stage.show();
    }

    // ================================================================
    // PANTALLA DE CONFIGURACIÓN
    // ================================================================

    /** Muestra la pantalla inicial para elegir dificultad e idioma. */
    private void showConfigScreen() {
        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // Título
        Label title = new Label("WORDLANGO");
        title.setStyle(
                "-fx-text-fill: " + COLOR_TEXT + ";" +
                        "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 6px;"
        );

        Label subtitle = new Label("Adivina la palabra oculta");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 15px;");

        // --- Dificultad ---
        Label diffLabel = new Label("Dificultad");
        diffLabel.setStyle("-fx-text-fill: #CCC; -fx-font-size: 14px; -fx-font-weight: bold;");

        ToggleGroup diffGroup = new ToggleGroup();
        HBox diffBox = new HBox(12);
        diffBox.setAlignment(Pos.CENTER);

        for (Difficulty d : Difficulty.values()) {
            ToggleButton tb = buildToggle(d.name(), diffGroup);
            if (d == Difficulty.EASY) tb.setSelected(true);
            tb.setOnAction(e -> selectedDifficulty = d);
            diffBox.getChildren().add(tb);
        }

        // --- Idioma ---
        Label langLabel = new Label("Idioma");
        langLabel.setStyle("-fx-text-fill: #CCC; -fx-font-size: 14px; -fx-font-weight: bold;");

        ToggleGroup langGroup = new ToggleGroup();
        HBox langBox = new HBox(12);
        langBox.setAlignment(Pos.CENTER);

        ToggleButton esBtn = buildToggle("Español", langGroup);
        ToggleButton enBtn = buildToggle("English", langGroup);
        esBtn.setSelected(true);
        esBtn.setOnAction(e -> selectedLanguage = Language.SPANISH);
        enBtn.setOnAction(e -> selectedLanguage = Language.ENGLISH);
        langBox.getChildren().addAll(esBtn, enBtn);

        // --- Botón iniciar ---
        GradientButton startBtn = new GradientButton("▶  JUGAR",
                Color.web("#005EB8"), Color.web("#00C0F3"));
        startBtn.setPrefWidth(220);
        startBtn.setPrefHeight(50);
        startBtn.setStyle(startBtn.getStyle() +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 12;"
        );
        startBtn.setOnAction(e -> startGame());

        root.getChildren().addAll(
                title, subtitle,
                new Separator(),
                diffLabel, diffBox,
                langLabel, langBox,
                new Separator(),
                startBtn
        );

        // Separadores con color
        root.getChildren().stream()
                .filter(n -> n instanceof Separator)
                .forEach(n -> ((Separator) n).setStyle("-fx-background-color: #333;"));

        primaryStage.setScene(new Scene(root, 460, 480));
    }

    /** Construye un ToggleButton estilizado para los selectores. */
    private ToggleButton buildToggle(String text, ToggleGroup group) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(group);
        tb.setPrefWidth(120);
        tb.setStyle(
                "-fx-background-color: #2C2C2E;" +
                        "-fx-text-fill: #CCC;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        tb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                tb.setStyle(
                        "-fx-background-color: #538D4E;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 13px;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;"
                );
            } else {
                tb.setStyle(
                        "-fx-background-color: #2C2C2E;" +
                                "-fx-text-fill: #CCC;" +
                                "-fx-font-size: 13px;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;"
                );
            }
        });
        return tb;
    }

    // ================================================================
    // PANTALLA DE JUEGO
    // ================================================================

    /** Inicia el juego y muestra la pantalla principal. */
    private void startGame() {
        game.startGame(selectedDifficulty, selectedLanguage);
        showGameScreen();
    }

    /** Construye y muestra la pantalla de juego completa. */
    private void showGameScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");
        root.setPadding(new Insets(16));

        // --- Header ---
        root.setTop(buildHeader());

        // --- Tablero central ---
        boardGrid = buildBoardGrid();
        VBox center = new VBox(16);
        center.setAlignment(Pos.CENTER);
        center.getChildren().add(boardGrid);

        // Mensaje de estado
        messageLabel = new Label(" ");
        messageLabel.setStyle("-fx-text-fill: #AAA; -fx-font-size: 14px;");
        messageLabel.setAlignment(Pos.CENTER);
        center.getChildren().add(messageLabel);

        root.setCenter(center);

        // --- Panel inferior: input + teclado + botones ---
        root.setBottom(buildBottomPanel());

        double width  = CELL_SIZE * selectedDifficulty.getWordLength() + 80;
        double height = 680;
        primaryStage.setScene(new Scene(root, Math.max(width, 500), height));
    }

    // ---------------------------------------------------------------
    // Header
    // ---------------------------------------------------------------

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 12, 0));

        Label title = new Label("WORDLANGO");
        title.setStyle(
                "-fx-text-fill: " + COLOR_TEXT + ";" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 4px;"
        );

        attemptsLabel = new Label();
        updateAttemptsLabel();
        attemptsLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        // Espaciador izquierdo
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // Espaciador derecho
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // Botón nueva partida
        AnimatedButton newBtn = new AnimatedButton("↺");
        newBtn.setAnimationType("bounce");
        newBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #888;" +
                        "-fx-font-size: 20px;" +
                        "-fx-cursor: hand;"
        );
        newBtn.setTooltip(new Tooltip("Nueva partida"));
        newBtn.setOnAction(e -> showConfigScreen());

        // Usamos spacer1 y spacer2
        header.getChildren().addAll(attemptsLabel, spacer1, title, spacer2, newBtn);

        // Separador inferior
        VBox wrapper = new VBox(0);
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #333;");
        wrapper.getChildren().addAll(header, sep);
        return header;
    }

    // ---------------------------------------------------------------
    // Tablero de intentos
    // ---------------------------------------------------------------

    private VBox buildBoardGrid() {
        VBox grid = new VBox(6);
        grid.setAlignment(Pos.CENTER);

        int rows = selectedDifficulty.getMaxAttempts();
        int cols = selectedDifficulty.getWordLength();

        for (int r = 0; r < rows; r++) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER);
            for (int c = 0; c < cols; c++) {
                Label cell = buildEmptyCell();
                row.getChildren().add(cell);
            }
            grid.getChildren().add(row);
        }

        return grid;
    }

    private Label buildEmptyCell() {
        Label cell = new Label(" ");
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setAlignment(Pos.CENTER);
        cell.setStyle(
                "-fx-background-color: " + COLOR_EMPTY + ";" +
                        "-fx-border-color: #3A3A3C;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-text-fill: " + COLOR_TEXT + ";" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );
        return cell;
    }

    /**
     * Actualiza la fila del tablero correspondiente al intento recién enviado.
     *
     * @param rowIndex índice de la fila (0-based)
     * @param result   resultado del intento
     */
    private void updateBoardRow(int rowIndex, model.GuessResult result) {
        HBox row = (HBox) boardGrid.getChildren().get(rowIndex);
        List<model.LetterResult> results = result.getResults();
        String word = result.getWord();

        for (int i = 0; i < word.length(); i++) {
            Label cell = (Label) row.getChildren().get(i);
            char letter = Character.toUpperCase(word.charAt(i));
            String color;

            switch (results.get(i)) {
                case CORRECT:   color = COLOR_CORRECT;   break;
                case MISPLACED: color = COLOR_MISPLACED; break;
                default:        color = COLOR_ABSENT;    break;
            }

            final int idx   = i;
            final String bg = color;

            // Animación de flip por celda con retardo escalonado
            PauseTransition delay = new PauseTransition(Duration.millis(idx * 120));
            delay.setOnFinished(e -> {
                cell.setText(String.valueOf(letter));
                cell.setStyle(
                        "-fx-background-color: " + bg + ";" +
                                "-fx-border-color: " + bg + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 4;" +
                                "-fx-background-radius: 4;" +
                                "-fx-text-fill: " + COLOR_TEXT + ";" +
                                "-fx-font-size: 24px;" +
                                "-fx-font-weight: bold;"
                );
                ScaleTransition flip = new ScaleTransition(Duration.millis(150), cell);
                flip.setFromY(1); flip.setToY(0);
                flip.setAutoReverse(true);
                flip.setCycleCount(2);
                flip.play();
            });
            delay.play();
        }
    }

    // ---------------------------------------------------------------
    // Panel inferior
    // ---------------------------------------------------------------

    private VBox buildBottomPanel() {
        VBox panel = new VBox(12);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(12, 0, 0, 0));

        // Campo de texto
        inputField = new TextField();
        inputField.setPromptText("Escribe tu palabra...");
        inputField.setPrefWidth(260);
        inputField.setStyle(
                "-fx-background-color: #2C2C2E;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-font-size: 18px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 8 12 8 12;"
        );
        inputField.setOnAction(e -> submitGuess());

        // Botones de acción — GradientButton (subclase #5 usada en la GUI)
        GradientButton submitBtn = new GradientButton("ENVIAR",
                Color.web("#538D4E"), Color.web("#27AE60"));
        submitBtn.setPrefWidth(120);
        submitBtn.setOnAction(e -> submitGuess());

        GradientButton hintBtn = new GradientButton("PISTA",
                Color.web("#B59F3B"), Color.web("#E6B800"));
        hintBtn.setPrefWidth(120);
        hintBtn.setOnAction(e -> showHintDialog());

        HBox actionRow = new HBox(12, submitBtn, hintBtn);
        actionRow.setAlignment(Pos.CENTER);

        // Teclado virtual — ShapeButton (subclase #1 usada en la GUI)
        keyboardPane = buildKeyboard();

        panel.getChildren().addAll(inputField, actionRow, keyboardPane);
        return panel;
    }

    // ---------------------------------------------------------------
    // Teclado virtual con ShapeButton
    // ---------------------------------------------------------------

    private FlowPane buildKeyboard() {
        FlowPane pane = new FlowPane(4, 4);
        pane.setAlignment(Pos.CENTER);
        pane.setPrefWrapLength(460);

        String alphabet = (selectedLanguage == Language.SPANISH)
                ? "abcdefghijklmnñopqrstuvwxyz"
                : "abcdefghijklmnopqrstuvwxyz";

        keyButtons = new ShapeButton[alphabet.length()];

        for (int i = 0; i < alphabet.length(); i++) {
            char letter = alphabet.charAt(i);
            ShapeButton btn = new ShapeButton(String.valueOf(Character.toUpperCase(letter)), "circle");
            btn.setPrefSize(38, 38);
            btn.setStyle(
                    "-fx-background-color: #818384;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
            );
            btn.setOnAction(e -> {
                inputField.setText(inputField.getText() + letter);
                inputField.requestFocus();
                inputField.positionCaret(inputField.getText().length());
            });
            keyButtons[i] = btn;
            pane.getChildren().add(btn);
        }

        return pane;
    }

    /** Actualiza el color de los botones del teclado según las letras usadas. */
    private void updateKeyboard(model.GuessResult result) {
        String word = result.getWord();
        List<model.LetterResult> results = result.getResults();
        String alphabet = (selectedLanguage == Language.SPANISH)
                ? "abcdefghijklmnñopqrstuvwxyz"
                : "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            int idx = alphabet.indexOf(letter);
            if (idx < 0 || idx >= keyButtons.length) continue;

            ShapeButton btn = keyButtons[idx];
            String color;
            switch (results.get(i)) {
                case CORRECT:   color = COLOR_CORRECT;   break;
                case MISPLACED: color = COLOR_MISPLACED; break;
                default:        color = COLOR_ABSENT;    break;
            }
            // Solo escalar hacia colores más informativos (verde > amarillo > gris)
            String current = btn.getStyle();
            if (current.contains(COLOR_CORRECT)) continue; // ya es verde, no degradar
            btn.setStyle(
                    "-fx-background-color: " + color + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    // ================================================================
    // LÓGICA DE JUEGO (desde la GUI)
    // ================================================================

    private void submitGuess() {
        if (game.isGameOver()) return;

        String input = inputField.getText().trim().toLowerCase();
        inputField.clear();

        if (input.isEmpty()) return;

        if (input.length() != selectedDifficulty.getWordLength()) {
            showMessage("La palabra debe tener " + selectedDifficulty.getWordLength() + " letras.", "#E74C3C");
            shakeInput();
            return;
        }

        model.GuessResult result = game.submitGuess(input);

        if (result == null) {
            showMessage("'" + input + "' no está en el diccionario.", "#E74C3C");
            shakeInput();
            return;
        }

        int rowIndex = game.getGuessHistory().size() - 1;
        updateBoardRow(rowIndex, result);
        updateKeyboard(result);
        updateAttemptsLabel();

        if (game.isGameOver()) {
            int delay = selectedDifficulty.getWordLength() * 120 + 300;
            PauseTransition wait = new PauseTransition(Duration.millis(delay));
            wait.setOnFinished(e -> showEndMessage());
            wait.play();
        } else {
            showMessage(" ", COLOR_TEXT);
        }
    }

    private void showMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px;");
    }

    private void updateAttemptsLabel() {
        if (game.isGameOver()) return;
        attemptsLabel.setText("Intentos: " + game.getRemainingAttempts());
    }

    /** Animación de sacudida en el campo de texto al error. */
    private void shakeInput() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), inputField);
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    // ================================================================
    // DIÁLOGO DE PISTAS
    // ================================================================

    private void showHintDialog() {
        if (game.isGameOver()) return;

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Pistas");
        alert.setHeaderText("Elige una pista:");

        ButtonType btnConsonant  = new ButtonType("🔤 Consonante");
        ButtonType btnVowel      = new ButtonType("🔡 Vocal");
        ButtonType btnDefinition = new ButtonType("📖 Definición");
        ButtonType btnLanguage   = new ButtonType("🌍 Idioma");
        ButtonType btnCancel     = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().addAll(btnConsonant, btnVowel, btnDefinition, btnLanguage, btnCancel);

        alert.showAndWait().ifPresent(type -> {
            String hint = null;
            if (type == btnConsonant)  hint = game.getHint('a');
            else if (type == btnVowel) hint = game.getHint('b');
            else if (type == btnDefinition) hint = game.getHint('c');
            else if (type == btnLanguage)   hint = game.getHint('d');

            if (hint != null) {
                showMessage("💡 " + hint, "#B59F3B");
            }
        });
    }

    // ================================================================
    // FIN DE PARTIDA
    // ================================================================

    private void showEndMessage() {
        String msg, color;
        if (game.isWon()) {
            msg   = "🎉 ¡Ganaste! La palabra era: " + game.getSecretWord().toUpperCase();
            color = COLOR_CORRECT;
        } else {
            msg   = "❌ Se acabaron los intentos. Era: " + game.getSecretWord().toUpperCase();
            color = "#E74C3C";
        }
        showMessage(msg, color);

        // Botón "Jugar de nuevo" que aparece al terminar
        GradientButton playAgain = new GradientButton("▶  JUGAR DE NUEVO",
                Color.web("#005EB8"), Color.web("#00C0F3"));
        playAgain.setPrefWidth(220);
        playAgain.setOnAction(e -> showConfigScreen());

        VBox center = (VBox) ((BorderPane) primaryStage.getScene().getRoot()).getCenter();
        if (center.getChildren().size() < 3) {
            center.getChildren().add(playAgain);
        }
    }
}