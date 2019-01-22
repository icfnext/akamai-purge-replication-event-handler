package com.icfolson.aem.akamai.purge.client.impl;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridRoutePlanner;
import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icfolson.aem.akamai.purge.client.AkamaiEdgeGridClient;
import com.icfolson.aem.akamai.purge.client.AkamaiEdgeGridClientConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = AkamaiEdgeGridClient.class)
@Designate(ocd = AkamaiEdgeGridClientConfiguration.class)
public final class DefaultAkamaiEdgeGridClient implements AkamaiEdgeGridClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAkamaiEdgeGridClient.class);

    private static final ContentType JSON = ContentType.APPLICATION_JSON;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Externalizer externalizer;

    private volatile CloseableHttpClient httpClient;

    private volatile boolean enabled;

    private volatile String network;

    private volatile String hostname;

    @Override
    public void invalidate(final String path) throws IOException, URISyntaxException {
        purge(path, "invalidate");
    }

    @Override
    public void delete(final String path) throws IOException, URISyntaxException {
        purge(path, "delete");
    }

    @Activate
    @Modified
    protected void activate(final AkamaiEdgeGridClientConfiguration configuration) {
        final ClientCredential credential = ClientCredential.builder()
            .accessToken(configuration.accessToken())
            .clientSecret(configuration.clientSecret())
            .clientToken(configuration.clientToken())
            .host(configuration.hostname())
            .build();

        httpClient = HttpClientBuilder.create()
            .setConnectionManager(new PoolingHttpClientConnectionManager())
            .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(credential))
            .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential))
            .build();

        enabled = configuration.enabled();
        network = configuration.network();
        hostname = configuration.hostname();
    }

    @Deactivate
    protected void deactivate() throws IOException {
        httpClient.close();
    }

    private void purge(final String path, final String operation) throws IOException, URISyntaxException {
        if (enabled) {
            final String json = getJson(path);
            final HttpEntity entity = new StringEntity(json, JSON);

            final URI uri = new URIBuilder()
                .setScheme("https")
                .setHost(hostname)
                .setPath(new StringBuilder()
                    .append("/ccu/v3/")
                    .append(operation)
                    .append("/url/")
                    .append(network)
                    .toString())
                .build();

            LOG.info("sending {} request to URI : {} with JSON entity : {}", operation, uri, json);

            final HttpUriRequest request = RequestBuilder.post(uri)
                .setEntity(entity)
                .addHeader("Accept", JSON.getMimeType())
                .addHeader("Content-Type", JSON.getMimeType())
                .build();

            final HttpResponse response = httpClient.execute(request);

            final StatusLine statusLine = response.getStatusLine();
            final String responseBody = EntityUtils.toString(response.getEntity());

            LOG.info("response body : {}", responseBody);

            if (statusLine.getStatusCode() >= 300) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
        } else {
            LOG.info("akamai client is disabled, purge request not sent for path : {}", path);
        }
    }

    private String getJson(final String path) throws IOException {
        final Map<String, Object> payload = new HashMap<>();

        payload.put("objects", Collections.singletonList(getExternalizedUrl(path)));

        return MAPPER.writeValueAsString(payload);
    }

    private String getExternalizedUrl(final String path) {
        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {
            return externalizer.externalLink(resourceResolver, Externalizer.PUBLISH,
                path + ".html");
        } catch (LoginException e) {
            // re-throw as runtime exception to propagate up to the event framework
            throw new RuntimeException(e);
        }
    }
}
