package io.quarkus.jgit.runtime;

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
         * The URL of the dev services.
         */
        String url();
    }
}
