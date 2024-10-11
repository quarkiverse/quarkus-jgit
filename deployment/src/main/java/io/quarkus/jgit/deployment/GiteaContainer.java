package io.quarkus.jgit.deployment;

import static org.testcontainers.containers.wait.strategy.Wait.forListeningPorts;

import java.io.IOException;
import java.util.Base64;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;

import com.github.dockerjava.api.command.InspectContainerResponse;

class GiteaContainer extends GenericContainer<GiteaContainer> {

    /**
     * Logger which will be used to capture container STDOUT and STDERR.
     */
    private static final Logger log = Logger.getLogger(GiteaContainer.class);

    private static final int HTTP_PORT = 3000;

    private JGitBuildTimeConfig.DevService devServiceConfig;

    GiteaContainer(JGitBuildTimeConfig.DevService devServiceConfig) {
        super("gitea/gitea:latest-rootless");
        this.devServiceConfig = devServiceConfig;
        withEnv("GITEA__security__INSTALL_LOCK", "true");
        withEnv("GITEA__server__DISABLE_SSH", "true");
        withExposedPorts(HTTP_PORT);
        waitingFor(forListeningPorts(HTTP_PORT));
        devServiceConfig.httpPort().ifPresent(port -> addFixedExposedPort(port, HTTP_PORT));
        if (devServiceConfig.showLogs()) {
            withLogConsumer(new JBossLoggingConsumer(log));
        }
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        try {
            createAdminUser();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to create admin user", e);
        }
    }

    private void createAdminUser() throws IOException, InterruptedException {
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
        log.info(String.join(" ", cmd));
        ExecResult execResult = execInContainer(cmd);
        log.info(execResult.getStdout());
        if (execResult.getExitCode() != 0) {
            throw new RuntimeException("Failed to create admin user: " + execResult.getStderr());
        }
    }

    private void createRepository() throws IOException, InterruptedException {
        String[] cmd = {
                "/usr/bin/curl",
                "-X",
                "POST",
                "http://localhost:3000/api/v1/user/repos",
                "-H",
                "'Accept: application/json'",
                "-H",
                "'Authorization: Basic " + getBasicAuth() + "'",
                "-H",
                "'Content-Type: application/json'",
                "-d",
                "'{\"auto_init\":true,\"default_branch\":\"main\",\"name\":\"hello-world\",\"private\":false,\"readme\":\"Default\"}'"
        };
        log.info(String.join(" ", cmd));
        ExecResult execResult = execInContainer(cmd);
        log.info(execResult.getStdout());
        if (execResult.getExitCode() != 0) {
            throw new RuntimeException("Failed to create repository: " + execResult.getStderr());
        }
    }

    private String getBasicAuth() {
        String auth = devServiceConfig.adminUsername() + ":" + devServiceConfig.adminPassword();
        return Base64.getEncoder().encodeToString(auth.getBytes());
    }

    public String getHttpUrl() {
        return "http://" + getHost() + ":" + getMappedPort(HTTP_PORT);
    }
}
