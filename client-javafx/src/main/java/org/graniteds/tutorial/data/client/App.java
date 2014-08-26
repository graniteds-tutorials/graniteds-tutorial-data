package org.graniteds.tutorial.data.client;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.ValidatorFactory;

import org.granite.client.javafx.tide.collections.PagedQuery;
import org.granite.client.tide.Module;
import org.granite.client.tide.data.DataObserver;
import org.granite.client.tide.server.ServerSession;
import org.granite.client.validation.NotifyingValidatorFactory;
import org.graniteds.tutorial.data.client.view.AccountListView;
import org.graniteds.tutorial.data.client.view.AccountView;

@Module
public class App {
	
	// tag::client-setup[]
	@Singleton
	public static ServerSession serverSession() {
		ServerSession serverSession = new ServerSession("/data", "localhost", 8080);	// <1>
        serverSession.addRemoteAliasPackage("org.graniteds.tutorial.data.client");		// <2>
        return serverSession;
	}
	
	@Singleton @Named
	public static AccountService accountService(ServerSession serverSession) {
		return new AccountService(serverSession);		// <3>
	}
	
	@Singleton @Named
	public static PagedQuery<Account, Map<String, String>> accountsList(AccountService accountService) {
		return new PagedQuery<Account, Map<String, String>>(accountService, "findByFilter", 20) {};	// <4>
	}
	
	@Singleton @Named
	public static AccountController account(AccountService accountService, ValidatorFactory validatorFactory) {
		return new AccountController(accountService, validatorFactory);		// <5>
	}
	
	@Singleton @Named
	public static DataObserver dataTopic(ServerSession serverSession) {
		return new DataObserver(serverSession);
	}
	
	@Singleton
	public static AccountListView accountsListView(PagedQuery<Account, Map<String, String>> accountsList, AccountController account) {
		return new AccountListView(accountsList, account);
	}
	
	@Singleton
	public static AccountView accountsView(AccountController account, NotifyingValidatorFactory validatorFactory) {
		return new AccountView(account, validatorFactory);
	}
	// end::client-setup[]
}