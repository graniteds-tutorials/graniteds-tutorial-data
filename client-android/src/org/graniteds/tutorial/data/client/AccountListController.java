package org.graniteds.tutorial.data.client;

import java.util.Map;

import org.granite.client.tide.collection.PagedQuery;
import org.graniteds.tutorial.data.client.entities.Account;
import org.graniteds.tutorial.data.client.services.AccountService;

public class AccountListController extends PagedQuery<Account, Map<String, String>> {
	
	public AccountListController(AccountService accountService) {
		super(accountService, "findByFilter", 20);
	}
	
	public boolean search(String searchText) {
		getFilter().put("searchText", searchText);
		refresh();
		return true;
	}
	
	public boolean resetSearch() {
		resetFilter();
		refresh();
		return false;
	}

}
