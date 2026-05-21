package ui.buttons;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class GradientButton extends Button {

    public GradientButton(String text, Color startColor, Color endColor) {
        super(text);

        // Convertimos los colores de JavaFX a formato Hexadecimal para CSS
        String hexStart = toHexString(startColor);
        String hexEnd = toHexString(endColor);

        // Aplicamos el estilo de degradado
        setStyle("-fx-background-color: linear-gradient(to right, " + hexStart + ", " + hexEnd + "); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-cursor: hand;");
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}