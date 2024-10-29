package io.quarkus.jgit.deployment;

import java.util.List;
import java.util.Optional;
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

        /**
         * The organization to be created when the Dev Service starts.
         */
        Optional<List<String>> organizations();

        /**
         * Repositories to be created when the Dev Service starts.
         * A repository may optionally include an organization reference.
         * For example, "my-org/my-repo" will create a repository named "my-repo" in the "my-org" organization.
         * The organization will be created if missing.
         * If no organization is specified, the repository will be created as a user repository.
         */
        @WithDefault("${quarkus.application.name}")
        Optional<List<String>> repositories();

        /**
         * Should the container be reused?
         */
        @WithDefault("false")
        boolean reuse();

        /**
         * The network alias for the container.
         * Other containers in the same network can use this alias to connect to this container.
         */
        Optional<String> networkAlias();
    }
}
