package io.quarkus.jgit.deployment;

import static org.testcontainers.containers.wait.strategy.Wait.forListeningPorts;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;

class GiteaContainer extends GenericContainer<GiteaContainer> {

    /**
     * Logger which will be used to capture container STDOUT and STDERR.
     */
    private static final Logger log = Logger.getLogger(GiteaContainer.class);

    public static final int PORT = 3000;

    public GiteaContainer() {
        super("gitea/gitea:latest-rootless");
        withExposedPorts(PORT);
        withEnv("GITEA__security__INSTALL_LOCK", "true");
        withExposedPorts(PORT);
        waitingFor(forListeningPorts(PORT));
        withLogConsumer(new JBossLoggingConsumer(log));
    }
}
