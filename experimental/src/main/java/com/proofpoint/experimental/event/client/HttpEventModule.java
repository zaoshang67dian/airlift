package com.proofpoint.experimental.event.client;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import static com.proofpoint.configuration.ConfigurationModule.bindConfig;

public class HttpEventModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(HttpEventClient.class).in(Scopes.SINGLETON);
        bindConfig(binder).to(HttpEventClientConfig.class);
    }

    @Provides
    @ForEventClient
    public AsyncHttpClient eventHttpClient(HttpEventClientConfig httpEventClientConfig)
    {
        Preconditions.checkNotNull(httpEventClientConfig, "httpEventClientConfig is null");

        // Build HTTP client config
        AsyncHttpClientConfig.Builder configBuilder = new AsyncHttpClientConfig.Builder()
                .setConnectionTimeoutInMs((int) httpEventClientConfig.getConnectTimeout().toMillis())
                .setMaximumConnectionsTotal(httpEventClientConfig.getMaxConnections())
                .setRequestTimeoutInMs((int) httpEventClientConfig.getRequestTimeout().toMillis());

        if (httpEventClientConfig.isCompress()) {
            configBuilder.setRequestCompressionLevel(6);
        }

        // Create client
        return new AsyncHttpClient(configBuilder.build());
    }
}
