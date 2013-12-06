package org.graniteds.tutorial.data.client;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import org.granite.client.javafx.tide.JavaFXApplication;
import org.granite.client.javafx.tide.ManagedEntity;
import org.granite.client.javafx.tide.collections.PagedQuery;
import org.granite.client.tide.Context;
import org.granite.client.tide.data.DataObserver;
import org.granite.client.tide.impl.SimpleContextManager;
import org.granite.client.tide.server.ServerSession;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Map;


public class DataClient extends Application {

    /**
     * Main method which launches the JavaFX application
     */
    public static void main(String[] args) {
        Application.launch(DataClient.class, args);
    }

    // tag::client-init[]
    public static Context context = new SimpleContextManager(new JavaFXApplication()).getContext(); // <1>
    // end::client-init[]

    @Override
    public void start(Stage stage) throws Exception {
        // tag::client-setup[]
        final ServerSession serverSession = context.set(
                new ServerSession("/data", "localhost", 8080)); // <2>
        serverSession.addRemoteAliasPackage("org.graniteds.tutorial.data.client"); // <3>
        serverSession.start(); // <4>
        // end::client-setup[]

        // tag::list-setup[]
        final PagedQuery<Account, Map<String, String>> accountsQuery = context.set(new PagedQuery<Account, Map<String, String>>(serverSession));
        accountsQuery.setMaxResults(40);
        accountsQuery.setMethodName("findByFilter");
        accountsQuery.setElementClass(Account.class);
        accountsQuery.setRemoteComponentClass(AccountService.class);
        // end::list-setup[]

        // tag::form-setup[]
        final ManagedEntity<Account> account = context.set(new ManagedEntity<Account>());
        // end::form-setup[]

        // tag::data-setup[]
        final DataObserver dataObserver = context.set("dataTopic", new DataObserver(serverSession));
        dataObserver.start();

        dataObserver.subscribe();
        // end::data-setup[]

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label titleLabel = new Label("Account Manager Example");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setStyle("-fx-padding: 10 10 10 10; -fx-background-color: #97b54b; -fx-text-fill: white");
        titleLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 24));
        vbox.getChildren().add(titleLabel);

        final StackPane stackPane = new StackPane();
        stackPane.setMaxWidth(Double.MAX_VALUE);
        stackPane.setPadding(new Insets(0));
        vbox.getChildren().add(stackPane);

        final VBox listPane = new VBox();
        listPane.setMaxWidth(Double.MAX_VALUE);
        listPane.setMaxHeight(Double.MAX_VALUE);
        listPane.setSpacing(10);

        final HBox searchBox = new HBox();
        searchBox.setMaxWidth(Double.MAX_VALUE);
        searchBox.setSpacing(5);

        // tag::list-ui[]
        final TextField searchField = new TextField();
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.setMinHeight(25);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> searchText, String oldValue, String newValue) { // <1>
                accountsQuery.getFilter().put("searchText", newValue);
                accountsQuery.refresh();
            }
        });
        searchField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                accountsQuery.refresh();
            }
        }); // <2>

        final Button searchButton = new AwesomeButton(AwesomeIcon.SEARCH);
        searchButton.setPrefWidth(40);
        searchButton.setOnAction(searchField.getOnAction());

        searchBox.getChildren().add(searchField);
        searchBox.getChildren().add(searchButton);
        listPane.getChildren().add(searchBox);

        final ListView<Account> accountsView = new ListView<Account>();
        accountsView.setMaxWidth(Double.MAX_VALUE);
        accountsView.setStyle("-fx-background-color: white; -fx-border-color: #97b54b");
        accountsView.setItems(accountsQuery); // <3>
        accountsView.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() { // <4>
            @Override
            public ListCell<Account> call(ListView<Account> userListView) {
                return new AccountCell();
            }
        });
        listPane.getChildren().add(accountsView);
        // end::list-ui[]

        // tag::form-bind[]
        final AccountForm accountForm = new AccountForm(account);
        accountForm.setMaxWidth(Double.MAX_VALUE);
        accountForm.setMaxHeight(Double.MAX_VALUE);
        accountForm.setVisible(false);
        accountForm.setManaged(false);

        accountsView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
            @Override
            public void changed(ObservableValue<? extends Account> selection, Account oldValue, Account newValue) { // <1>
                account.setInstance(newValue);
            }
        });

        Button newButton = new AwesomeButton(AwesomeIcon.PLUS, "New");
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                account.setInstance(new Account());
            }
        }); // <2>
        listPane.getChildren().add(newButton);

        accountForm.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> visible, Boolean oldVisible, Boolean newVisible) { // <3>
                listPane.setDisable(newVisible);
                if (!newVisible)
                    accountsView.getSelectionModel().clearSelection();
            }
        });
        // end::form-bind[]

        stackPane.getChildren().add(listPane);
        stackPane.getChildren().add(accountForm);

        Scene scene = new Scene(vbox, 360, 400);
        stage.setTitle("GraniteDS Data Tutorial");
        stage.setScene(scene);
        stage.show();

        // tag::client-close[]
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    dataObserver.stop();
                    serverSession.stop();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // end::client-close[]
    }


    private class AccountCell extends ListCell<Account> {

        final ImageView imageView = new ImageView();
        final Label nameLabel = new Label();
        final Label emailLabel = new Label();

        public AccountCell() {
            setPrefHeight(48);
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(4));
            grid.setHgap(10);
            grid.add(imageView, 0, 0, 1, 2);
            nameLabel.setFont(Font.font("Arial", 20));
            grid.add(nameLabel, 1, 0);
            grid.add(emailLabel, 1, 1);
            setGraphic(grid);
        }

        @Override
        protected void updateItem(final Account account, boolean empty) {
            if (getItem() != null) {
                getItem().gravatarUrlProperty().removeListener(urlListener);
                nameLabel.textProperty().unbind();
                emailLabel.textProperty().unbind();
            }

            super.updateItem(account, empty);

            if (account != null && !empty) {
                account.gravatarUrlProperty().addListener(urlListener);
                urlListener.changed(account.gravatarUrlProperty(), null, account.getGravatarUrl());
                nameLabel.textProperty().bind(account.nameProperty());
                emailLabel.textProperty().bind(account.emailProperty());
            }
        }

        final ChangeListener<String> urlListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> url, String oldUrl, String newUrl) {
                if (newUrl != null)
                    imageView.setImage(new Image("http://www.gravatar.com/avatar/" + newUrl + "?s=44"));
                else
                    imageView.setImage(null);
            }
        };
    }


    private static class AwesomeButton extends Button {

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

    private class AccountForm extends VBox {

        private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        public AccountForm(final ManagedEntity<Account> account) {
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
                public void changed(ObservableValue<? extends Account> instance, final Account oldValue, final Account newValue) { // <1>
                    if (newValue != null) {
                        titleLabel.setText(account.isSaved() ? "Edit account" : "Create account");
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
                        hideTransition.onFinishedProperty().set(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                setVisible(false);
                                setManaged(false);

                                nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                                emailField.textProperty().unbindBidirectional(oldValue.emailProperty());
                            }
                        });

                        hideTransition.setByX(getPrefWidth());
                        hideTransition.play();
                    }
                }
            });

            saveButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (!validator.validate(account.getInstance()).isEmpty()) // <2>
                        return;

                    context.byType(AccountService.class).save(account.getInstance(), new TideResponder<Void>() { // <3>
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
                    context.byType(AccountService.class).remove(account.getInstance(), new TideResponder<Void>() {
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
    }
}
