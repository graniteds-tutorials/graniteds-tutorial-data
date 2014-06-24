package org.graniteds.tutorial.data.client;

import javax.inject.Inject;
import javax.inject.Named;

import org.granite.binding.android.Binder;
import org.granite.client.android.tide.AndroidContextManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Account detail screen. This fragment is
 * either contained in a {@link AccountListActivity} in two-pane mode (on
 * tablets) or a {@link AccountDetailActivity} on handsets.
 */
public class AccountDetailFragment extends Fragment {
	
	@Inject @Named
	private AccountController account;
	
	@Inject
	private Binder binder;
	
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AccountDetailFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidContextManager.getContext(getActivity(), getArguments()).set(this);
		
//		if (getArguments().containsKey(AccountDetailActivity.REF_ACCOUNT_INSTANCE)) {
//			Object instance = getArguments().getSerializable(AccountDetailActivity.REF_ACCOUNT_INSTANCE);
//			if (instance instanceof Account)
//				account.setInstance((Account)instance);
//		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_account_detail, container, false);
		binder.bind(rootView);
		
		((TextView)rootView.findViewById(R.id.form_title)).setText(getResources().getString(account.isSaved() ? R.string.edit_account : R.string.create_account));
		
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		binder.unbind(getView());
		
		account.reset();
		
		AndroidContextManager.getContext(getActivity()).remove(this);
		
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
}
