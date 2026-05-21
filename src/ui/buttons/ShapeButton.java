package ui.buttons;

import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class ShapeButton extends Button {

    public ShapeButton(String text, String shapeType) {
        super(text);

        double r = 19; // Radio base

        if ("circle".equalsIgnoreCase(shapeType)) {
            setShape(new Circle(r));
        } else if ("triangle".equalsIgnoreCase(shapeType)) {
            setShape(new Polygon(r, 0, r*2, r*2, 0, r*2));
        } else if ("hexagon".equalsIgnoreCase(shapeType)) {
            setShape(new Polygon(r/2, 0, r*1.5, 0, r*2, r, r*1.5, r*2, r/2, r*2, 0, r));
        } else if ("star".equalsIgnoreCase(shapeType)) {
            setShape(new Polygon(r, 0, r*1.5, r*2, 0, r, r*2, r, r/2, r*2));
        }

        setStyle("-fx-background-color: #00C0F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
    }
}