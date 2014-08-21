package org.graniteds.tutorial.data.client.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class ConflictDialog {
	
	public static boolean show(Window parent) {
		final boolean[] result = new boolean[]{ false };
		
		final Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Conflict Management");
        dialog.setResizable(false);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(parent);
        
        final VBox totalPane = new VBox();
        dialog.setScene(new Scene(totalPane));
        totalPane.setAlignment(Pos.CENTER);
        totalPane.setPadding(new Insets(10, 10, 10, 10));
        totalPane.setSpacing(10);
        
        final Label label = new Label("Conflict detected with another user modification:");
        totalPane.getChildren().add(label);

        final HBox pane = new HBox();
        totalPane.getChildren().add(pane);
        pane.setSpacing(10);
        
        final Button accept = new Button("Accept incoming data");
        accept.setDefaultButton(true);
        pane.getChildren().add(accept);
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	result[0] = true;
                dialog.close();
            }
        });
        
        final Button keep = new Button("Keep local changes");
        keep.setCancelButton(true);
        pane.getChildren().add(keep);
        keep.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	result[0] = false;
                dialog.close();
            }
        });

        
        dialog.showAndWait();
        
        return result[0];
	}
}
