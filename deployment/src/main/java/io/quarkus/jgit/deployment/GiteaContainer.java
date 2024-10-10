package io.quarkus.jgit.deployment;

import static org.testcontainers.containers.wait.strategy.Wait.forListeningPorts;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;

class GiteaContainer extends GenericContainer<GiteaContainer> {

    private static final String IMAGE_NAME = "gitea/gitea:latest-rootless";
    /**
     * Logger which will be used to capture container STDOUT and STDERR.
     */
    private static final Logger log = Logger.getLogger(GiteaContainer.class);

    private static final int SSH_PORT = 2222;
    private static final int HTTP_PORT = 3000;

    private JGitBuildTimeConfig.DevService devServiceConfig;

    GiteaContainer(JGitBuildTimeConfig.DevService devServiceConfig) {
        super(IMAGE_NAME);
        this.devServiceConfig = devServiceConfig;
        withEnv("GITEA__security__INSTALL_LOCK", "true");
        withExposedPorts(SSH_PORT, HTTP_PORT);
        waitingFor(forListeningPorts(SSH_PORT, HTTP_PORT));
        devServiceConfig.sshPort().ifPresent(port -> addFixedExposedPort(port, SSH_PORT));
        devServiceConfig.httpPort().ifPresent(port -> addFixedExposedPort(port, HTTP_PORT));
        withLogConsumer(new JBossLoggingConsumer(log));
    }

    public ExecResult createAdminUser() throws IOException, InterruptedException {
        String[] cmd = {
                "/usr/local/bin/gitea",
                "admin",
                "user",
                "create",
                "--username",
                devServiceConfig.adminUsername(),
                "--password",
                devServiceConfig.adminPassword(),
                "--email",
                "quarkus@quarkus.io",
                "--must-change-password=false",
                "--admin"
        };
        ExecResult execResult = execInContainer(cmd);
        log.info(execResult.getStdout());
        if (execResult.getExitCode() != 0) {
            throw new RuntimeException("Failed to create admin user: " + execResult.getStderr());
        }
        return execResult;
    }

    public String getHttpUrl() {
        return "http://" + getHost() + ":" + getMappedPort(HTTP_PORT);
    }

    public String getSshUrl() {
        return "ssh://git@" + getHost() + ":" + getMappedPort(SSH_PORT);
    }

}
