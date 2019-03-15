package com.icfolson.aem.akamai.purge.job.delegate;

import com.icfolson.aem.akamai.purge.enums.PurgeAction;

/**
 * Delegate event handler to provide notification or other action for cancelled Akamai purge jobs.  Multiple event
 * handler delegates can be registered as OSGi services and will be bound to the root event handler by the framework.
 */
public interface AkamaiPurgeJobCancelledEventHandlerDelegate {

    /**
     * Handle the cancellation event for the given resource path and purge action.
     *
     * @param path page or asset path
     * @param purgeAction Akamai purge action
     */
    void handleEvent(String path, PurgeAction purgeAction);
}
