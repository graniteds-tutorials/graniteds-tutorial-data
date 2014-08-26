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
import javafx.stage.WindowEvent;
import de.jensd.fx.fontawesome.AwesomeIcon;

public class ConflictDialog {
	
	public static boolean show(final Window parent) {
		final boolean[] result = new boolean[]{ false };
		
		final Stage conflict = new Stage(StageStyle.UTILITY);
        conflict.setTitle("Conflict Management");
        conflict.setResizable(false);
        conflict.initModality(Modality.APPLICATION_MODAL);
        conflict.initOwner(parent);
        
        final VBox pane = new VBox();
        conflict.setScene(new Scene(pane));
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setSpacing(10);
        
        final Label label = new Label("Conflict detected with another user modification:");
        pane.getChildren().add(label);

        final HBox buttons = new HBox();
        pane.getChildren().add(buttons);
        buttons.setSpacing(10);
        
        final Button accept = new AwesomeButton(AwesomeIcon.OK_CIRCLE, "Accept incoming data");
        accept.setDefaultButton(true);
        buttons.getChildren().add(accept);
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	result[0] = true;
                conflict.close();
            }
        });
        
        final Button keep = new AwesomeButton(AwesomeIcon.BAN_CIRCLE, "Keep local changes");
        keep.setCancelButton(true);
        buttons.getChildren().add(keep);
        keep.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	result[0] = false;
                conflict.close();
            }
        });

        conflict.setOnShown(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
            	conflict.setX(parent.getX() + (parent.getWidth() - conflict.getWidth()) / 2);
            	conflict.setY(parent.getY() + (parent.getHeight() - conflict.getHeight()) / 2);
			}
		});
        
        conflict.showAndWait();
        
        return result[0];
	}
}
