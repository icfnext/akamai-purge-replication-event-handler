package com.icfolson.aem.akamai.purge.client;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Akamai Edge Grid Client Configuration")
public @interface AkamaiEdgeGridClientConfiguration {

    @AttributeDefinition(name = "Enable Akamai Edge Grid Client")
    boolean enabled() default false;

    @AttributeDefinition(name = "Akamai Network", description = "staging or production")
    String network() default "production";

    @AttributeDefinition(name = "Akamai Hostname")
    String hostname() default "akab-zai4si42encz4itc-umutbp23o4zar5su.purge.akamaiapis.net";

    @AttributeDefinition(name = "Akamai Access Token")
    String accessToken() default "";

    @AttributeDefinition(name = "Akamai Client Token")
    String clientToken() default "";

    @AttributeDefinition(name = "Akamai Client Secret")
    String clientSecret() default "";
}