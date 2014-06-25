package org.graniteds.tutorial.data.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Named;

import org.granite.client.javafx.tide.JavaFXApplication;
import org.granite.client.tide.data.DataObserver;
import org.granite.client.tide.impl.SimpleContextManager;
import org.granite.client.tide.server.ServerSession;
import org.graniteds.tutorial.data.client.view.AccountListView;
import org.graniteds.tutorial.data.client.view.AccountView;


public class DataClient extends Application {

    /**
     * Main method which launches the JavaFX application
     */
    public static void main(String[] args) {
        Application.launch(DataClient.class, args);
    }

    public static SimpleContextManager contextManager;
    
    @Inject
    private ServerSession serverSession;
    
    @Inject @Named
    private DataObserver dataTopic;
    
    @Inject
    private AccountListView accountListView;
    
    @Inject
    private AccountView accountView;
    

    @Override
    public void start(Stage stage) throws Exception {
        // tag::client-setup[]
    	contextManager = new SimpleContextManager(new JavaFXApplication(this, stage));	// <1>
    	contextManager.initModules(App.class);	// <2>
    	contextManager.getContext().set(this);	// <3>
        // end::client-setup[]
    	
        serverSession.start();
        
        // tag::data-setup[]
        dataTopic.start();
        
        dataTopic.subscribe();
        // end::data-setup[]
        
        // tag::client-ui[]
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
        
        stackPane.getChildren().add(accountListView);
        stackPane.getChildren().add(accountView);
        
        Scene scene = new Scene(vbox, 360, 400);
        stage.setTitle("GraniteDS Data Tutorial - JavaFX");
        stage.setScene(scene);
        stage.show();
        // end::client-ui[]
    }
    
    // tag::client-close[]
    @Override
    public void stop() throws Exception {
    	dataTopic.stop();
        serverSession.stop();
        
        contextManager.destroyContexts();
        
        super.stop();
    }
    // end::client-close[]
}
