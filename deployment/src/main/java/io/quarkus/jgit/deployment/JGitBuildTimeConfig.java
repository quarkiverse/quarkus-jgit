package io.quarkus.jgit.deployment;

import java.util.OptionalInt;

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
        @WithDefault("false")
        boolean enabled();

        /**
         * If logs should be shown from the Gitea container.
         */
        @WithDefault("false")
        boolean showLogs();

        /**
         * The exposed HTTP port for the Gitea container.
         * If not specified, it will pick a random port
         */
        OptionalInt httpPort();

        /**
         * The Admin username for the Gitea container.
         */
        @WithDefault("quarkus")
        String adminUsername();

        /**
         * The Admin password for the Gitea container.
         */
        @WithDefault("quarkus")
        String adminPassword();

        //        /**
        //         * Create Hello World repository?
        //         */
        //        @WithDefault("true")
        //        boolean createRepository();
    }
}
