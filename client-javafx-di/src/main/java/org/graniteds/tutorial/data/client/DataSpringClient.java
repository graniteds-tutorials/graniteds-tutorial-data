package org.graniteds.tutorial.data.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.granite.client.javafx.tide.JavaFXApplication;
import org.granite.client.javafx.tide.ManagedEntity;
import org.granite.client.javafx.tide.collections.PagedQuery;
import org.granite.client.tide.data.DataObserver;
import org.granite.client.tide.server.ServerSession;
import org.granite.client.tide.spring.SpringContextManager;
import org.granite.client.tide.spring.SpringEventBus;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;


public class DataSpringClient extends Application {

    /**
     * Main method which launches the JavaFX application
     */
    public static void main(String[] args) {
        Application.launch(DataSpringClient.class, args);
    }


    private AnnotationConfigApplicationContext applicationContext;

    @Configuration
    public static class Config {

        @Bean
        public static SpringContextManager contextManager() {
            return new SpringContextManager(new JavaFXApplication());
        }

        @Bean
        public ServerSession serverSession() throws Exception {
            ServerSession serverSession = new ServerSession("/data", "localhost", 8080);
            // Important: indicates the packages to scan for remotely externalizable classes (mostly domain classes)
            serverSession.addRemoteAliasPackage("org.graniteds.tutorial.data.client");
            return serverSession;
        }

        @Bean
        public PagedQuery<Account, Map<String, String>> accountsQuery(ServerSession serverSession) throws Exception {
            PagedQuery<Account, Map<String, String>> accountsQuery = new PagedQuery<Account, Map<String, String>>(serverSession);
            accountsQuery.setMethodName("findByFilter");
            accountsQuery.setMaxResults(40);
            accountsQuery.setRemoteComponentClass(AccountService.class);
            accountsQuery.setElementClass(Account.class);
            return accountsQuery;
        }

        @Bean
        public ManagedEntity<Account> account() {
            return new ManagedEntity<Account>();
        }

        @Bean
        public DataObserver dataTopic(ServerSession serverSession) {
            return new DataObserver(serverSession);
        }

        @Bean
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Override
    public void start(final Stage stage) throws Exception {
        // Bootstrap Spring with an annotation based context
        applicationContext = new AnnotationConfigApplicationContext();
        // Indicates the packages to scan for beans
        applicationContext.scan("org.graniteds.tutorial.data.client");
        applicationContext.refresh();
        applicationContext.registerShutdownHook();
        applicationContext.start();

        Scene scene = new Scene(applicationContext.getBean(App.class).getUI(), 360, 400);
        stage.setTitle("GraniteDS Data Tutorial - JavaFX/Spring");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Stop the Spring container
        applicationContext.stop();
        applicationContext.destroy();

        super.stop();
    }


}
