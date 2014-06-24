package org.graniteds.tutorial.data.client;

import javax.inject.Inject;

import org.granite.binding.android.Binder;
import org.granite.client.android.tide.AndroidContextManager;
import org.granite.client.tide.server.ServerSession;
import org.graniteds.tutorial.data.client.entities.Account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

/**
 * An activity representing a list of Accounts. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link AccountDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AccountListFragment} and the item details (if present) is a
 * {@link AccountDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link AccountListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class AccountListActivity extends FragmentActivity implements AccountListFragment.Callbacks {
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
    
    @Inject
    private ServerSession serverSession;
    
    @Inject
    private Binder binder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidContextManager.getContext(this).set(this);
		serverSession.start();
		
		setContentView(R.layout.activity_account_list);
        
		AccountListFragment accountListFragment = ((AccountListFragment)getSupportFragmentManager()
				.findFragmentById(R.id.account_list));
		
		if (findViewById(R.id.account_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			
			accountListFragment.setActivateOnItemClick(true);
		}
		
		// TODO: If exposing deep links into your app, handle intents here.
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		AndroidContextManager.releaseContext(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SearchView view = (SearchView)menu.findItem(R.id.menu_search).getActionView();
		binder.bindListener(view, "queryText.submit", "accountsList", "search");
		binder.bindListener(view, "close", "accountsList", "resetSearch");
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		SearchView view = (SearchView)menu.findItem(R.id.menu_search).getActionView();
		binder.unbindListener(view, "queryText.submit");
		binder.unbindListener(view, "close");
		super.onOptionsMenuClosed(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_new:
	            onItemSelected(new Account());
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * Callback method from {@link AccountListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Account account) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.			
			Bundle arguments = new Bundle();
			arguments.putSerializable(App.REF_ACCOUNT_INSTANCE, account);
			AccountDetailFragment fragment = new AccountDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.account_detail_container, fragment).commit();
		}
		else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, AccountDetailActivity.class);
			detailIntent.putExtra(App.REF_ACCOUNT_INSTANCE, account);
			startActivity(detailIntent);
		}
	}
}
