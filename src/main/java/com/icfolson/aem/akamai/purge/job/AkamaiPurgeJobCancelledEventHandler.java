package com.icfolson.aem.akamai.purge.job;

import com.icfolson.aem.akamai.purge.enums.PurgeAction;
import com.icfolson.aem.akamai.purge.job.delegate.AkamaiPurgeJobCancelledEventHandlerDelegate;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.event.jobs.NotificationConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event handler for Akamai purge job cancellations.
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    private volatile List<AkamaiPurgeJobCancelledEventHandlerDelegate> eventHandlerDelegates = new CopyOnWriteArrayList<>();

    @Override
    public void handleEvent(final Event event) {
        final String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
        final PurgeAction purgeAction = PurgeAction.fromEvent(event);

        LOG.info("job cancelled for page path : {} and purge action : {}, delegating to {} event handler services",
            path, purgeAction, eventHandlerDelegates.size());

        eventHandlerDelegates.forEach(eventHandlerDelegate -> {
            eventHandlerDelegate.handleEvent(path, purgeAction);
        });
    }
}
