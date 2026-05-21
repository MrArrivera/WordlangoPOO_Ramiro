package ui.buttons;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class AnimatedButton extends Button {
    private String animationType;

    public AnimatedButton(String text) {
        super(text);
        setOnMouseEntered(e -> playAnimation());
    }

    // Nuevo constructor para aceptar texto y tipo de animación
    public AnimatedButton(String text, String animationType) {
        this(text);
        this.animationType = animationType;
    }

    public void setAnimationType(String type) {
        this.animationType = type;
    }

    // Método ahora es public en lugar de private
    public void playAnimation() {
        if (animationType == null) return;

        switch (animationType.toLowerCase()) {
            case "bounce":
                TranslateTransition bounce = new TranslateTransition(Duration.millis(150), this);
                bounce.setByY(-4);
                bounce.setCycleCount(2);
                bounce.setAutoReverse(true);
                bounce.play();
                break;
            case "pulse":
                ScaleTransition pulse = new ScaleTransition(Duration.millis(150), this);
                pulse.setByX(0.1); pulse.setByY(0.1);
                pulse.setCycleCount(2);
                pulse.setAutoReverse(true);
                pulse.play();
                break;
            case "shake":
                TranslateTransition shake = new TranslateTransition(Duration.millis(50), this);
                shake.setByX(5);
                shake.setCycleCount(4);
                shake.setAutoReverse(true);
                shake.play();
                break;
        }
    }
}