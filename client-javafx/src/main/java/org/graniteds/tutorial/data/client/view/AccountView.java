package org.graniteds.tutorial.data.client.view;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import org.granite.client.javafx.validation.FormValidator;
import org.granite.client.javafx.validation.ValidationResultEvent;
import org.granite.client.validation.NotifyingValidatorFactory;
import org.graniteds.tutorial.data.client.Account;
import org.graniteds.tutorial.data.client.AccountController;

import de.jensd.fx.fontawesome.AwesomeIcon;

public class AccountView extends VBox {
	
	private final FormValidator formValidator;
	
    public AccountView(final AccountController account, NotifyingValidatorFactory validatorFactory) {
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
        
        // tag::form-ui[]
        account.instanceProperty().addListener(new ChangeListener<Account>() {
			@Override
			public void changed(ObservableValue<? extends Account> obs,
					Account oldValue, Account newValue) {
	        	if (oldValue != null) {
	                nameField.textProperty().unbindBidirectional(oldValue.nameProperty()); // <1>
	                emailField.textProperty().unbindBidirectional(oldValue.emailProperty());
	        	    formValidator.entityProperty().unbind();
	        	}
	        	
	            if (newValue != null) {
	                nameField.textProperty().bindBidirectional(newValue.nameProperty());
	                emailField.textProperty().bindBidirectional(newValue.emailProperty());
	        	    formValidator.entityProperty().bind(account.instanceProperty());
	                
	                double width = ((Region)getParent()).getWidth();
	                double fromX = ((Region)getParent()).getTranslateX() + width;
	                
	                setTranslateX(fromX);
	                setPrefWidth(width);
	                setVisible(true);	// <2>
	                setManaged(true);
	                showTransition.setByX(-width);
	                showTransition.play();
	            }
	            else {
	                hideTransition.onFinishedProperty().set(new EventHandler<ActionEvent>() {						
						@Override
						public void handle(ActionEvent e) {
							setVisible(false);
		                    setManaged(false);
						}
					});
	                
	                hideTransition.setByX(getPrefWidth());
	                hideTransition.play();
	            }
			}
		});
        
        saveButton.disableProperty().bind(Bindings.not(account.dirtyProperty())); // <3>

        titleLabel.textProperty().bind(Bindings.	// <4>
        		when(account.savedProperty()).
        		then("Edit account").otherwise("Create account")
        );
        deleteButton.visibleProperty().bind(account.savedProperty());
        deleteButton.managedProperty().bind(account.savedProperty());        
        
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) { // <5>
				account.save();
			}        	
        });
        
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				account.remove();
			}
        });
        
        cancelButton.setOnAction(new EventHandler<ActionEvent>() { // <6>
			@Override
			public void handle(ActionEvent e) {
				account.setInstance(null);
			}
        });
        
        formValidator = new FormValidator(validatorFactory);
		formValidator.setForm(this);
		
		addEventHandler(ValidationResultEvent.INVALID, new EventHandler<ValidationResultEvent>() {
			@Override
			public void handle(ValidationResultEvent event) {
				((Node)event.getTarget()).setStyle("-fx-border-color: red");
			}
		});
        addEventHandler(ValidationResultEvent.VALID, new EventHandler<ValidationResultEvent>() {
			@Override
			public void handle(ValidationResultEvent event) {
	            ((Node)event.getTarget()).setStyle("-fx-border-color: null");
			}
        });
        
        setVisible(false);	// <7>
        setManaged(false);
        // end::form-ui[]
    }
}