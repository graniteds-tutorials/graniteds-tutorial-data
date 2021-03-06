/**
 * Generated by Gfx v3.0.0 (Granite Data Services).
 *
 * NOTE: this file is only generated if it does not exist. You may safely put
 * your custom code here.
 */

package org.graniteds.tutorial.data.client;
	
import javax.inject.Inject;
import javax.inject.Named;
import org.granite.client.messaging.RemoteAlias;
import org.granite.client.tide.server.ServerSession;

@Named
@RemoteAlias("org.graniteds.tutorial.data.services.AccountService")
public class AccountService extends AccountServiceBase {
	
	@Inject
	public AccountService(ServerSession serverSession) {
    	super(serverSession);
    }
}
