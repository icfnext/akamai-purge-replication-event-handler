package com.icfolson.aem.akamai.purge.client;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Akamai Edge Grid Client Configuration")
public @interface AkamaiEdgeGridClientConfiguration {

    @AttributeDefinition(name = "Akamai Network")
    String network() default "production";

    @AttributeDefinition(name = "Akamai Hostname")
    String hostname() default "";

    @AttributeDefinition(name = "Akamai Access Token")
    String accessToken() default "";

    @AttributeDefinition(name = "Akamai Client Token")
    String clientToken() default "";

    @AttributeDefinition(name = "Akamai Client Secret")
    String clientSecret() default "";
}