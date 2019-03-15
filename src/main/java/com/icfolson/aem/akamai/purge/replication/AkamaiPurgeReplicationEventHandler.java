package com.icfolson.aem.akamai.purge.replication;

import com.day.cq.replication.ReplicationActionType;
import com.icfolson.aem.akamai.purge.job.AkamaiPurgeJobConsumer;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.NotificationConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Event handler for finished replication events to purge content from Akamai.
 */
@Component(immediate = true,
    service = EventHandler.class,
    property = {
        EventConstants.EVENT_TOPIC + "=" + NotificationConstants.TOPIC_JOB_FINISHED,
        EventConstants.EVENT_FILTER + "=(" + NotificationConstants.NOTIFICATION_PROPERTY_JOB_TOPIC + "=com/day/cq/replication/job/publish)"
    })
@Designate(ocd = AkamaiPurgeReplicationEventHandlerConfiguration.class)
public final class AkamaiPurgeReplicationEventHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AkamaiPurgeReplicationEventHandler.class);

    @Reference
    private JobManager jobManager;

    private volatile boolean enabled;

    private volatile List<String> includedPaths;

    private volatile List<String> excludedPaths;

    @Override
    public final void handleEvent(final Event event) {
        final String path = (String) event.getProperty("cq:path");
        final String type = (String) event.getProperty("cq:type");

        final ReplicationActionType replicationActionType = ReplicationActionType.fromName(type);

        if (enabled && isIncluded(path)) {
            if (replicationActionType.equals(ReplicationActionType.ACTIVATE)) {
                LOG.info("handling activate event for path = {}", path);

                addJob(AkamaiPurgeJobConsumer.JOB_TOPIC_INVALIDATE, path);
            } else if (replicationActionType.equals(ReplicationActionType.DEACTIVATE)) {
                LOG.info("handling deactivate event for path = {}", path);

                addJob(AkamaiPurgeJobConsumer.JOB_TOPIC_DELETE, path);
            } else if (replicationActionType.equals(ReplicationActionType.DELETE)) {
                LOG.info("handling delete event for path = {}", path);

                addJob(AkamaiPurgeJobConsumer.JOB_TOPIC_DELETE, path);
            } else {
                LOG.debug("replication action type = {} not handled for path = {}", type, path);
            }
        }
    }

    @Activate
    @Modified
    protected void activate(final AkamaiPurgeReplicationEventHandlerConfiguration configuration) {
        enabled = configuration.enabled();
        includedPaths = getConfiguredPaths(configuration.includedPaths());
        excludedPaths = getConfiguredPaths(configuration.excludedPaths());
    }

    private List<String> getConfiguredPaths(final String[] paths) {
        return Optional.ofNullable(paths)
            .map(Arrays :: stream)
            .orElseGet(Stream :: empty)
            .collect(Collectors.toList());
    }

    /**
     * Check if the given page path is included according to the rules defined in the OSGi service configuration.
     *
     * @param path replicated page path
     * @return true if path is included, false if not
     */
    private boolean isIncluded(final String path) {
        return includedPaths.stream().anyMatch(path :: startsWith) && excludedPaths.stream().noneMatch(
            path :: startsWith);
    }

    /**
     * Add a job to the queue with the given topic and page path.
     *
     * @param topic job topic
     * @param path page path
     */
    private void addJob(final String topic, final String path) {
        LOG.info("adding job with topic : {} for page path : {}", topic, path);

        jobManager.addJob(topic, getJobProperties(path));
    }

    /**
     * Get a map of job properties for the given page path.
     *
     * @param path page path
     * @return job properties
     */
    private Map<String, Object> getJobProperties(final String path) {
        return Collections.singletonMap(SlingConstants.PROPERTY_PATH, path);
    }
}
