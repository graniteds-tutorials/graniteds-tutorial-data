package org.graniteds.tutorial.data.client.view;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import org.graniteds.tutorial.data.client.AccountController;

import de.jensd.fx.fontawesome.AwesomeIcon;

public class AccountView extends VBox {
	
    public AccountView(AccountController account) {
		setSpacing(10);
        setPadding(new Insets(10));
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
        setStyle("-fx-background-color: white; -fx-border-color: #97b54b");

        final Label titleLabel = new Label();
        titleLabel.setFont(Font.font("Arial", 16));
        final TextField nameField = new TextField();
        final TextField emailField = new TextField();
        final Button saveButton = new AwesomeButton(AwesomeIcon.SAVE, "Save");
        final Button deleteButton = new AwesomeButton(AwesomeIcon.TRASH, "Delete");
        final Button cancelButton = new AwesomeButton(AwesomeIcon.BAN_CIRCLE, "Cancel");

        final TranslateTransition showTransition = new TranslateTransition(Duration.millis(250), this);
        final TranslateTransition hideTransition = new TranslateTransition(Duration.millis(250), this);

        getChildren().add(titleLabel);
        VBox nameBox = new VBox();
        nameBox.setSpacing(2);
        nameBox.getChildren().add(new Label("Name"));
        nameBox.getChildren().add(nameField);
        getChildren().add(nameBox);
        VBox emailBox = new VBox();
        emailBox.setSpacing(2);
        emailBox.getChildren().add(new Label("E-mail"));
        emailBox.getChildren().add(emailField);
        getChildren().add(emailBox);

        HBox buttonBar = new HBox();
        buttonBar.setSpacing(10);
        buttonBar.getChildren().add(saveButton);
        buttonBar.getChildren().add(deleteButton);
        buttonBar.getChildren().add(cancelButton);

        getChildren().add(buttonBar);
        
        titleLabel.textProperty().bind(Bindings.when(account.savedProperty()).then("Edit account").otherwise("Create account"));

        // tag::form-ui[]
        account.instanceProperty().addListener((instance, oldValue, newValue) -> {
        	if (oldValue != null) {
                nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                emailField.textProperty().unbindBidirectional(oldValue.emailProperty());
        	}
        	
            if (newValue != null) {
                nameField.textProperty().bindBidirectional(newValue.nameProperty());
                emailField.textProperty().bindBidirectional(newValue.emailProperty());

                double width = ((Region)getParent()).getWidth();
                double fromX = ((Region)getParent()).getTranslateX() + width;

                setTranslateX(fromX);
                setPrefWidth(width);
                setVisible(true);
                setManaged(true);
                showTransition.setByX(-width);
                showTransition.play();
            }
            else {
                hideTransition.onFinishedProperty().set(e -> {
                    setVisible(false);
                    setManaged(false);
                });

                hideTransition.setByX(getPrefWidth());
                hideTransition.play();
            }
        });

        saveButton.setOnAction(e -> account.save());
        saveButton.disableProperty().bind(Bindings.not(account.dirtyProperty())); // <6>

        deleteButton.setOnAction(e -> account.remove());
        deleteButton.visibleProperty().bind(account.savedProperty()); // <7>
        deleteButton.managedProperty().bind(account.savedProperty());
        
        cancelButton.setOnAction(e -> account.setInstance(null));
        
        setVisible(false);
        setManaged(false);
        // end::form-ui[]
    }
}