package org.graniteds.tutorial.data.client;

import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import org.granite.client.javafx.tide.JavaFXApplication;
import org.granite.client.javafx.tide.collections.PagedQuery;
import org.granite.client.tide.Context;
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

    // tag::client-init[]
    private Context context; // <1>
    // end::client-init[]
    
    private ServerSession serverSession;
    private DataObserver dataObserver;

    @Override
    public void start(Stage stage) throws Exception {
        // tag::client-setup[]
    	context = new SimpleContextManager(new JavaFXApplication(this, stage)).getContext();
    	
        serverSession = context.set(new ServerSession("/data", "localhost", 8080)); // <2>
        serverSession.addRemoteAliasPackage("org.graniteds.tutorial.data.client"); // <3>
        serverSession.start(); // <4>
        // end::client-setup[]
        
        final AccountService accountService = context.set(new AccountService(serverSession));
        
        // tag::list-setup[]
<<<<<<< HEAD
        final AccountService accountService = context.set("accountService", new AccountService(serverSession));
        
        final PagedQuery<Account, Map<String, String>> accountsQuery = context.set(new PagedQuery<Account, Map<String, String>>(serverSession));
        accountsQuery.setMaxResults(40);
        accountsQuery.setMethodName("findByFilter");
        accountsQuery.setElementClass(Account.class);
        accountsQuery.setRemoteComponentClass(AccountService.class);
=======
        final PagedQuery<Account, Map<String, String>> accountsList = context.set(new PagedQuery<Account, Map<String, String>>(accountService, accountService::findByFilter, 40) {});
>>>>>>> gds310
        // end::list-setup[]
        
        // tag::controller-setup[]
        final AccountController accountController = context.set(new AccountController(accountService));
        // end::controller-setup[]
        
        // tag::data-setup[]
        dataObserver = context.set("dataTopic", new DataObserver(serverSession));
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
        
        AccountView accountView = new AccountView(accountController);
        AccountListView accountListView = new AccountListView(accountsList, accountController); 
        stackPane.getChildren().add(accountListView);
        stackPane.getChildren().add(accountView);
        
        Scene scene = new Scene(vbox, 360, 400);
        stage.setTitle("GraniteDS Data Tutorial - JavaFX 8");
        stage.setScene(scene);
        stage.show();
    }

    // tag::client-close[]
    @Override
    public void stop() throws Exception {
        dataObserver.stop();
        serverSession.stop();
        
        super.stop();
    }
    // end::client-close[]
}
