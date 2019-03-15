package com.icfolson.aem.akamai.purge.client;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Edge Grid client for sending invalidate/delete requests to Akamai for a given page path.
 */
public interface AkamaiEdgeGridClient {

    /**
     * Invalidate the given page path.
     *
     * @param path page path
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void invalidate(String path) throws IOException, URISyntaxException;

    /**
     * Delete the given page path.
     *
     * @param path page path
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void delete(String path) throws IOException, URISyntaxException;
}
