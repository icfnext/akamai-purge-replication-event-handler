package com.icfolson.aem.akamai.purge.replication;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Akamai Purge Replication Event Handler Configuration")
public @interface AkamaiPurgeReplicationEventHandlerConfiguration {

    @AttributeDefinition(name = "Enabled?", description = "Check to enable Akamai purge for page replication events.")
    boolean enabled() default false;

    @AttributeDefinition(name = "Included Paths", description = "List of paths that should be purged.")
    String[] includedPaths() default { "/content" };

    @AttributeDefinition(name = "Excluded Paths", description = "List of paths to exclude from purges.")
    String[] excludedPaths();
}