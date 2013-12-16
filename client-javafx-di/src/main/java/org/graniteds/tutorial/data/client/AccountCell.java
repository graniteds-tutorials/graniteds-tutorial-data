package org.graniteds.tutorial.data.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**
* Created by william on 13/12/13.
*/
class AccountCell extends ListCell<Account> {

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
