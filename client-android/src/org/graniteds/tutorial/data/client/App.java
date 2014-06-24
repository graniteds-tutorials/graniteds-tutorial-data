package org.graniteds.tutorial.data.client;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.granite.binding.android.Binder;
import org.granite.binding.android.IdGetter;
import org.granite.binding.android.Setter;
import org.granite.binding.android.ViewBindingRegistry;
import org.granite.client.tide.Module;
import org.granite.client.tide.collection.PagedQuery;
import org.granite.client.tide.server.ServerSession;
import org.graniteds.tutorial.data.client.entities.Account;
import org.graniteds.tutorial.data.client.services.AccountService;
import org.graniteds.tutorial.data.client.util.AsyncImageLoader;

import android.widget.ImageView;

@Module
public class App {
	
	@Singleton
	public static ServerSession serverSession() {
		ServerSession serverSession = new ServerSession("/data", "192.168.0.14", 8080);
        serverSession.addRemoteAliasPackage("org.graniteds.tutorial.data.client.entities");
        return serverSession;
	}
	
	public static AccountService accountService(ServerSession serverSession) {
		return new AccountService(serverSession);
	}
	
	public static final String REF_ACCOUNT_INSTANCE = "$account.instance";
	
	@Named
	public static AccountController account() {
		return new AccountController();
	}
	
	@Named
	public static PagedQuery<Account, Map<String, String>> accountsList(AccountService accountService) {
		return new AccountListController(accountService);
	}
	
	
	public static void configure(Binder binder) {
		binder.registerIdGetter(new IdGetter() {
			@Override
			public boolean accepts(Object object) {
				return object instanceof Account;
			}
			
			@Override
			public long getId(Object object) {
				return ((Account)object).getId();
			}
		});
	}
	
	@Singleton
	public static AsyncImageLoader imageLoader() {
		return new AsyncImageLoader();
	}
	
	public static void setupGravatarBinding(final AsyncImageLoader imageLoader) {
		ViewBindingRegistry.registerPropertySetter(ImageView.class, "gravatarUrl", new Setter<ImageView>() {
			@Override
			public boolean accepts(Object value) {
				return value == null || value instanceof String;
			}
			
			@Override
			public void setValue(ImageView view, Object value) throws Exception {
				imageLoader.loadImage(view, value != null ? "http://www.gravatar.com/avatar/" + value + "?s=64" : null);
			}			
		});		
	}
}