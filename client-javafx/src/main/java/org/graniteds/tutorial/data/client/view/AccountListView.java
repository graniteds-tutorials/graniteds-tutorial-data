package org.graniteds.tutorial.data.client.view;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import org.granite.client.javafx.tide.collections.PagedQuery;
import org.graniteds.tutorial.data.client.Account;
import org.graniteds.tutorial.data.client.AccountController;

import de.jensd.fx.fontawesome.AwesomeIcon;

public class AccountListView extends VBox {
	
	public AccountListView(final PagedQuery<Account, Map<String, String>> accountsList, AccountController account) {
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
        setSpacing(10);
        
        final HBox searchBox = new HBox();
        searchBox.setMaxWidth(Double.MAX_VALUE);
        searchBox.setSpacing(5);

        // tag::list-ui[]
        final TextField searchField = new TextField();
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.setMinHeight(25);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((searchText, oldTest, newText) -> { // <1>
        	accountsList.getFilter().put("searchText", newText);
        	accountsList.refresh();
        });
        searchField.setOnAction(e -> accountsList.refresh()); // <2>
        
        final Button searchButton = new AwesomeButton(AwesomeIcon.SEARCH);
        searchButton.setPrefWidth(40);
        searchButton.setOnAction(searchField.getOnAction());

        searchBox.getChildren().add(searchField);
        searchBox.getChildren().add(searchButton);
        getChildren().add(searchBox);
        
        final ListView<Account> accountsView = new ListView<Account>();
        accountsView.setMaxWidth(Double.MAX_VALUE);
        accountsView.setStyle("-fx-background-color: white; -fx-border-color: #97b54b");
        accountsView.setItems(accountsList); // <3>
        accountsView.setCellFactory(lv -> new AccountCell()); // <4>
        getChildren().add(accountsView);
        
        accountsView.getSelectionModel().selectedItemProperty().addListener((selection, oldValue, newValue) -> account.setInstance(newValue));
        account.instanceProperty().addListener((instance, oldValue, newValue) -> {
        	setDisable(newValue != null);
        	if (newValue == null)
        		accountsView.getSelectionModel().clearSelection();
        });
        
        Button newButton = new AwesomeButton(AwesomeIcon.PLUS, "New");
        newButton.setOnAction(e -> account.setInstance(new Account()));
        getChildren().add(newButton);
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
        
        final ChangeListener<String> urlListener = (url, oldUrl, newUrl) -> {
            if (newUrl != null)
                imageView.setImage(new Image("http://www.gravatar.com/avatar/" + newUrl + "?s=44"));
            else
                imageView.setImage(null);
        };
    }
}
