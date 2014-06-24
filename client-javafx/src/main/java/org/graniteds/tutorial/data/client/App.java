package org.graniteds.tutorial.data.client;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.granite.client.javafx.tide.collections.PagedQuery;
import org.granite.client.tide.Module;
import org.granite.client.tide.data.DataObserver;
import org.granite.client.tide.server.ServerSession;
import org.graniteds.tutorial.data.client.view.AccountListView;
import org.graniteds.tutorial.data.client.view.AccountView;

@Module
public class App {
	
	@Singleton
	public static ServerSession serverSession() throws Exception {
		ServerSession serverSession = new ServerSession("/data", "localhost", 8080);
        serverSession.addRemoteAliasPackage("org.graniteds.tutorial.data.client");
        return serverSession;
	}
	
	@Singleton
	public static AccountService accountService(ServerSession serverSession) {
		return new AccountService(serverSession);
	}
	
	@Singleton @Named
	public static AccountController account(AccountService accountService) {
		return new AccountController(accountService);
	}
	
	@Singleton @Named
	public static PagedQuery<Account, Map<String, String>> accountsList(AccountService accountService) {
		return new PagedQuery<Account, Map<String, String>>(accountService, "findByFilter", 20) {};
	}
	
	@Singleton @Named
	public static DataObserver dataTopic(ServerSession serverSession) {
		return new DataObserver(serverSession);
	}
	
	@Singleton
	public static AccountListView accountsListView(@Named("accountsList") PagedQuery<Account, Map<String, String>> accountsList, @Named("account") AccountController account) {
		return new AccountListView(accountsList, account);
	}
	
	@Singleton
	public static AccountView accountsView(@Named("account") AccountController account) {
		return new AccountView(account);
	}
}