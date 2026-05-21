package ui.buttons;

import javafx.scene.control.Button;

public class SoundButton extends Button {
    private String hoverSound;
    private String clickSound;
    private double volume = 1.0;

    public SoundButton(String text) {
        super(text);

        // Simulación de sonido para la demostración (falla silenciosa si no hay módulo de audio)
        setOnMouseEntered(e -> {
            if (hoverSound != null) System.out.println("🔊 Reproduciendo hover: " + hoverSound);
        });

        setOnAction(e -> {
            if (clickSound != null) System.out.println("🔊 Reproduciendo click: " + clickSound);
        });
    }

    public void setHoverSound(String path) { this.hoverSound = path; }
    public void setClickSound(String path) { this.clickSound = path; }
    public void setVolume(double vol) { this.volume = vol; }
}