package org.graniteds.tutorial.data.client.view;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;


public class AwesomeButton extends Button {

    static final String NORMAL_BUTTON_STYLE = "-fx-padding: 5 5 5 5; -fx-background-radius: 0; -fx-background-color: #97b54b; -fx-text-fill: white; -fx-font-family: FontAwesome";
    static final String HOVERED_BUTTON_STYLE = "-fx-padding: 5 5 5 5; -fx-background-radius: 0; -fx-background-color: #b0d257; -fx-text-fill: white; -fx-font-family: FontAwesome";

    static {
        AwesomeDude.createIconLabel(AwesomeIcon.SEARCH);
    }

    public AwesomeButton(AwesomeIcon icon) {
        this(icon, null);
    }

    public AwesomeButton(AwesomeIcon icon, String text) {
        getStyleClass().add("awesome");
        setText(icon.toString());
        if (text != null) {
            Label label = new Label(text);
            label.setStyle("-fx-text-fill: white");
            setGraphic(label);
        }

        setStyle(NORMAL_BUTTON_STYLE);
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setStyle(HOVERED_BUTTON_STYLE);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setStyle(NORMAL_BUTTON_STYLE);
            }
        });
    }
}