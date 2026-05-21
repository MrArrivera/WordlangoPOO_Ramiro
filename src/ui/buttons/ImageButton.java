package ui.buttons;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageButton extends Button {
    private double imgWidth = 16;
    private double imgHeight = 16;

    public ImageButton(String text) {
        super(text);
    }

    public void setImageSize(double w, double h) {
        this.imgWidth = w;
        this.imgHeight = h;
    }

    public void setImage(String path) {
        try {
            // Intenta cargar la imagen, si no existe o la ruta falla, lo ignora (falla silenciosa)
            Image img = new Image(getClass().getResourceAsStream(path));
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(imgWidth);
            imgView.setFitHeight(imgHeight);
            setGraphic(imgView);
        } catch (Exception e) {
            // Falla silenciosa
        }
    }
}