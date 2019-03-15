# AEM Akamai Purge Replication Event Handler

[ICF Next](http://www.icfnext.com)

## Overview

The Akamai Purge Replication Event Handler is an OSGi bundle for the Adobe Experience Manager (AEM) platform that uses the Akamai Fast Purge API to purge content from Akamai when content is replicated.

The included event handler listens to replication events for a configurable set of content paths.  When content is activated, deactivated, or deleted, the event handler creates a Sling job that sends a request using the Akamai Fast Purge API to invalidate or delete the externalized page/asset URL.

## Compatibility

Akamai Purge Version | AEM Version(s)
------------ | -------------
0.x.x | 6.3, 6.4, 6.5

## Installation

1. Add the bundle as a dependency to an existing AEM project:

```xml
<dependency>
    <groupId>com.icfolson.aem.akamai</groupId>
    <artifactId>akamai-purge-replication-event-handler</artifactId>
    <version>0.0.1</version>
    <scope>provided</scope>
</dependency>
```

2. TODO

## Configuration

### Akamai Purge Replication Event Handler Configuration

TODO

### Akamai Edge Grid Client Configuration

TODO

## Job Cancellation Event Handlers

If the Akamai purge request fails, the underlying Sling job will be cancelled and an event will be generated containing the job topic and affected page/asset path.  

Applications using this bundle may register services implementing the `com.icfolson.aem.akamai.purge.job.delegate.AkamaiPurgeJobCancelledEventHandlerDelegate` interface to provide additional error/failure handling behavior (e.g. email notification).  These services will be automatically bound to the default 
Akamai purge job cancellation event handler by the framework.  

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.