package org.graniteds.tutorial.data.app;

import org.granite.config.servlet3.ServerFilter;
import org.granite.gravity.config.servlet3.MessagingDestination;
import org.granite.tide.cdi.CDIConfigProvider;

// tag::server-filter[]
@ServerFilter(configProviderClass=CDIConfigProvider.class) // <1>
public class DataApplication {

    @MessagingDestination(noLocal=true, sessionSelector=true)
    public String dataTopic; // <2>
}
// end::server-filter[]
