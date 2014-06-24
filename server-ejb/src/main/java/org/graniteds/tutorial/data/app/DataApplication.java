package org.graniteds.tutorial.data.app;

import org.granite.config.servlet3.ServerFilter;
import org.granite.gravity.config.servlet3.MessagingDestination;
import org.granite.tide.ejb.EjbConfigProvider;

// tag::server-filter[]
@ServerFilter(configProviderClass=EjbConfigProvider.class) // <1>
public class DataApplication {

    @MessagingDestination(noLocal=true, sessionSelector=true)
    public String dataTopic; // <2>
}
// end::server-filter[]
