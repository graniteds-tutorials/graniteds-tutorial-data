package org.graniteds.tutorial.data.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.granite.client.javafx.tide.ManagedEntity;
import org.granite.client.javafx.tide.cdi.JavaFXTideClientExtension;
import org.granite.client.javafx.tide.collections.PagedQuery;
import org.granite.client.tide.data.DataObserver;
import org.granite.client.tide.server.ServerSession;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.util.Map;


public class DataCDIClient extends Application {

    /**
     * Main method which launches the JavaFX application
     */
    public static void main(String[] args) {
        Application.launch(DataCDIClient.class, args);
    }


    public static class Config {

        @Produces @ApplicationScoped @Named
        public ServerSession getServerSession() throws Exception {
            ServerSession serverSession = new ServerSession("/data", "localhost", 8080);
            // Important: indicates the packages to scan for remotely externalizable classes (mostly domain classes)
            serverSession.addRemoteAliasPackage("org.graniteds.tutorial.data.client");
            return serverSession;
        }

        @Produces @ApplicationScoped @Named
        public PagedQuery<Account, Map<String, String>> accountsQuery(ServerSession serverSession) throws Exception {
            PagedQuery<Account, Map<String, String>> accountsQuery = new PagedQuery<Account, Map<String, String>>(serverSession);
            accountsQuery.setMethodName("findByFilter");
            accountsQuery.setMaxResults(40);
            accountsQuery.setRemoteComponentClass(AccountService.class);
            accountsQuery.setElementClass(Account.class);
            return accountsQuery;
        }

        /**
         * Disposer method for all paged query components
         * Not sure why CDI does not call @PreDestroy on produced objects
         */
        public void destroyPagedQuery(@Disposes PagedQuery<?, ?> pagedQuery) {
            pagedQuery.clear();
        }

        @Produces @ApplicationScoped @Named
        public ManagedEntity<Account> account() {
            return new ManagedEntity<Account>();
        }

        /**
         * A messaging topic to receive data updates from the server
         * @Named is here used to give a default name to the topic (wineshopTopic)
         */
        @Produces @ApplicationScoped @Named
        public DataObserver getDataTopic(ServerSession serverSession) {
            return new DataObserver(serverSession);
        }
    }

    private Weld weld;
    private WeldContainer weldContainer;

    @Override
    public void start(final Stage stage) throws Exception {
        // Bootstrap Weld with the Tide extension which registers all internal Tide beans/scopes
        weld = new Weld();
        weld.addExtension(new JavaFXTideClientExtension());
        weldContainer = weld.initialize();

        Scene scene = new Scene(weldContainer.instance().select(App.class).get().getUI(), 360, 400);
        stage.setTitle("GraniteDS Data Tutorial - JavaFX/CDI");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        weld.shutdown();
    }
}
