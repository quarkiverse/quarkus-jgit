package io.quarkus.jgit.runtime;

import java.net.URI;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.jgit")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface JGitRuntimeConfig {
    /**
     * Configuration for the development services.
     */
    DevService devservices();

    interface DevService {
        /**
         * The HTTP URL of the dev services. Generated once the service is up and running.
         */
        Optional<URI> httpUrl();
    }
}
