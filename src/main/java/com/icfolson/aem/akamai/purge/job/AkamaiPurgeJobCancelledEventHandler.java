package com.icfolson.aem.akamai.purge.job;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.event.jobs.NotificationConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example event handler for Akamai purge job cancellations.
 */
@Component(immediate = true,
    service = EventHandler.class,
    property = {
        EventConstants.EVENT_TOPIC + "=" + NotificationConstants.TOPIC_JOB_CANCELLED,
        EventConstants.EVENT_FILTER + "=(|(" + NotificationConstants.NOTIFICATION_PROPERTY_JOB_TOPIC + "="
            + AkamaiPurgeJobConsumer.JOB_TOPIC_INVALIDATE + ")(" + NotificationConstants.NOTIFICATION_PROPERTY_JOB_TOPIC
            + "=" + AkamaiPurgeJobConsumer.JOB_TOPIC_DELETE + "))"
    })
public final class AkamaiPurgeJobCancelledEventHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AkamaiPurgeJobCancelledEventHandler.class);

    @Override
    public void handleEvent(final Event event) {
        final String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
        final String topic = (String) event.getProperty(NotificationConstants.NOTIFICATION_PROPERTY_JOB_TOPIC);

        LOG.info("job cancelled for page path : {} and topic : {}, sending notification email", path, topic);

        // TODO
    }
}
