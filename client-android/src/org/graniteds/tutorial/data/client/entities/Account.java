/**
 * Generated by Gfx v3.0.0 (Granite Data Services).
 *
 * NOTE: this file is only generated if it does not exist. You may safely put
 * your custom code here.
 */

package org.graniteds.tutorial.data.client.entities;

import org.granite.client.messaging.RemoteAlias;
import org.granite.client.persistence.Entity;
import org.granite.messaging.annotations.Serialized;

@Entity
@Serialized
@RemoteAlias("org.graniteds.tutorial.data.entities.Account")
public class Account extends AccountBase {

    private static final long serialVersionUID = 1L;
}