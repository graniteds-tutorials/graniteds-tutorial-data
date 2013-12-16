package org.graniteds.tutorial.data.client;

import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.granite.client.javafx.tide.ManagedEntity;
import org.granite.client.tide.server.TideFaultEvent;
import org.granite.client.tide.server.TideResponder;
import org.granite.client.tide.server.TideResultEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidatorFactory;

/**
* Created by william on 13/12/13.
*/
@Component
@ApplicationScoped
public class AccountForm {

    @Inject
    private ValidatorFactory validatorFactory;

    @Inject
    private ManagedEntity<Account> account;

    @Inject
    private AccountService accountService;

    private VBox vbox = new VBox();

    @PostConstruct
    private void init() {
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.setMaxHeight(Double.MAX_VALUE);
        vbox.setStyle("-fx-background-color: white; -fx-border-color: #97b54b");

        final Label titleLabel = new Label();
        titleLabel.setFont(Font.font("Arial", 16));
        final TextField nameField = new TextField();
        final TextField emailField = new TextField();
        final Button saveButton = new AwesomeButton(AwesomeIcon.SAVE, "Save");
        final Button deleteButton = new AwesomeButton(AwesomeIcon.TRASH, "Delete");
        final Button cancelButton = new AwesomeButton(AwesomeIcon.BAN_CIRCLE, "Cancel");

        final TranslateTransition showTransition = new TranslateTransition(Duration.millis(250), vbox);
        final TranslateTransition hideTransition = new TranslateTransition(Duration.millis(250), vbox);

        vbox.getChildren().add(titleLabel);
        VBox nameBox = new VBox();
        nameBox.setSpacing(2);
        nameBox.getChildren().add(new Label("Name"));
        nameBox.getChildren().add(nameField);
        vbox.getChildren().add(nameBox);
        VBox emailBox = new VBox();
        emailBox.setSpacing(2);
        emailBox.getChildren().add(new Label("E-mail"));
        emailBox.getChildren().add(emailField);
        vbox.getChildren().add(emailBox);

        HBox buttonBar = new HBox();
        buttonBar.setSpacing(10);
        buttonBar.getChildren().add(saveButton);
        buttonBar.getChildren().add(deleteButton);
        buttonBar.getChildren().add(cancelButton);

        vbox.getChildren().add(buttonBar);

        // tag::form-ui[]
        account.instanceProperty().addListener(new ChangeListener<Account>() {

            @Override
            public void changed(ObservableValue<? extends Account> instance, final Account oldValue, final Account newValue) { // <1>
                if (newValue != null) {
                    titleLabel.setText(account.isSaved() ? "Edit account" : "Create account");
                    nameField.textProperty().bindBidirectional(newValue.nameProperty());
                    emailField.textProperty().bindBidirectional(newValue.emailProperty());

                    double width = ((Region)vbox.getParent()).getWidth();
                    double fromX = ((Region)vbox.getParent()).getTranslateX() + width;

                    vbox.setTranslateX(fromX);
                    vbox.setPrefWidth(width);
                    vbox.setVisible(true);
                    vbox.setManaged(true);
                    showTransition.setByX(-width);
                    showTransition.play();
                }
                else {
                    nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                    emailField.textProperty().unbindBidirectional(oldValue.emailProperty());

                    hideTransition.onFinishedProperty().set(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            vbox.setVisible(false);
                            vbox.setManaged(false);
                        }
                    });

                    hideTransition.setByX(vbox.getPrefWidth());
                    hideTransition.play();
                }
            }
        });

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!validatorFactory.getValidator().validate(account.getInstance()).isEmpty()) // <2>
                    return;

                accountService.save(account.getInstance(), new TideResponder<Void>() { // <3>
                    @Override
                    public void result(TideResultEvent<Void> event) {
                        account.setInstance(null); // <4>
                    }

                    @Override
                    public void fault(TideFaultEvent event) {
                        System.out.println("Could not save account: " + event.getFault()); // <5>
                    }
                });
            }
        });
        saveButton.disableProperty().bind(Bindings.not(account.dirtyProperty())); // <6>

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                accountService.remove(account.getInstance(), new TideResponder<Void>() {
                    @Override
                    public void result(TideResultEvent<Void> event) {
                        account.setInstance(null);
                    }

                    @Override
                    public void fault(TideFaultEvent event) {
                        System.out.println("Could not delete account: " + event.getFault());
                    }
                });
            }
        });
        deleteButton.visibleProperty().bind(account.savedProperty()); // <7>
        deleteButton.managedProperty().bind(account.savedProperty());

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) { // <8>
                account.reset();
                account.setInstance(null);
            }
        });
        // end::form-ui[]
    }

    public VBox getForm() {
        return vbox;
    }
}
