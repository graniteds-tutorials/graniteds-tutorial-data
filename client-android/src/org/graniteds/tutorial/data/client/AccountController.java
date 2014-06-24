package org.graniteds.tutorial.data.client;

import javax.inject.Inject;

import org.granite.client.tide.data.ManagedEntity;
import org.granite.client.tide.server.SimpleTideResponder;
import org.granite.client.tide.server.TideResultEvent;
import org.graniteds.tutorial.data.client.entities.Account;
import org.graniteds.tutorial.data.client.services.AccountService;

import android.content.Intent;
import android.support.v4.app.NavUtils;


public class AccountController extends ManagedEntity<Account> {
	
	@Inject
	private AccountService accountService;
	
	@Inject
	private AccountDetailActivity activity;
	
	public void save() {
		accountService.save(getInstance(), new SimpleTideResponder<Void>() {
			@Override
			public void result(TideResultEvent<Void> tre) {
				if (activity != null)	// One pane mode
					NavUtils.navigateUpTo(activity, new Intent(activity, AccountListActivity.class));
			}
		});
	}
	
	public void remove() {
		accountService.remove(getInstance(), new SimpleTideResponder<Void>() {
			@Override
			public void result(TideResultEvent<Void> tre) {
				if (activity != null)	// One pane mode
					NavUtils.navigateUpTo(activity, new Intent(activity, AccountListActivity.class));
			}
		});
	}
	
	public void cancel() {
		reset();
		if (activity != null)	// One pane mode
			NavUtils.navigateUpTo(activity, new Intent(activity, AccountListActivity.class));
	}
}
