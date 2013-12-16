package org.graniteds.tutorial.data.client;

import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import org.granite.client.javafx.tide.ManagedEntity;
import org.granite.client.javafx.tide.collections.PagedQuery;
import org.granite.client.tide.data.DataObserver;
import org.granite.client.tide.server.ServerSession;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;


@Component
@ApplicationScoped
public class App {

    @Inject
    private ServerSession serverSession;

    @Inject
    private PagedQuery<Account, Map<String, String>> accountsQuery;

    @Inject
    private DataObserver dataTopic;

    @Inject
    private ManagedEntity<Account> account;

    @Inject
    private AccountForm accountForm;

    private VBox vbox = new VBox();

    @PostConstruct
    private void start() throws Exception {
        // Start the application
        serverSession.start();

        dataTopic.start();
        dataTopic.subscribe();


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
        VBox form = accountForm.getForm();
        form.setMaxWidth(Double.MAX_VALUE);
        form.setMaxHeight(Double.MAX_VALUE);
        form.setVisible(false);
        form.setManaged(false);

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

        form.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> visible, Boolean oldVisible, Boolean newVisible) { // <3>
                listPane.setDisable(newVisible);
                if (!newVisible)
                    accountsView.getSelectionModel().clearSelection();
            }
        });
        // end::form-bind[]

        stackPane.getChildren().add(listPane);
        stackPane.getChildren().add(form);
    }

    public VBox getUI() {
        return vbox;
    }

    @PreDestroy
    public void stop() throws Exception {
        dataTopic.stop();
        serverSession.stop();
    }
}
