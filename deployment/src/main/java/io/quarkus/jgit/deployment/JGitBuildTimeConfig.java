package io.quarkus.jgit.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.jgit")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface JGitBuildTimeConfig {
    /**
     * Configuration for the development services.
     */
    DevService devservices();

    interface DevService {
        /**
         * Whether devservice is enabled.
         */
        @WithDefault("true")
        boolean enabled();

    }
}
