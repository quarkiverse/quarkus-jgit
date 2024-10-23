package io.quarkus.jgit.deployment;

import static org.testcontainers.containers.wait.strategy.Wait.forListeningPorts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private List<String> repositories = new ArrayList<>();

    GiteaContainer(JGitBuildTimeConfig.DevService devServiceConfig) {
        super("gitea/gitea:latest-rootless");
        this.devServiceConfig = devServiceConfig;
        withEnv("GITEA__security__INSTALL_LOCK", "true");
        withEnv("GITEA__server__DISABLE_SSH", "true");
        withExposedPorts(HTTP_PORT);
        withReuse(devServiceConfig.reuse());
        waitingFor(forListeningPorts(HTTP_PORT));
        // Needed for podman (see https://github.com/testcontainers/testcontainers-java/issues/7310)
        withStartupAttempts(2);
        devServiceConfig.httpPort().ifPresent(port -> addFixedExposedPort(port, HTTP_PORT));
        if (devServiceConfig.showLogs()) {
            withLogConsumer(new JBossLoggingConsumer(log));
        }
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo, boolean reused) {
        if (!reused) {
            try {
                createAdminUser();
                for (String repository : devServiceConfig.repositories()) {
                    createRepository(this, repository);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to create admin user", e);
            }
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
        log.debug(String.join(" ", cmd));
        ExecResult execResult = execInContainer(cmd);
        log.info(execResult.getStdout());
        if (execResult.getExitCode() != 0) {
            throw new RuntimeException("Failed to create admin user: " + execResult.getStderr());
        }
    }

    private void createRepository(GiteaContainer giteaContainer, String repository)
            throws UnsupportedOperationException, IOException, InterruptedException {
        String httpUrl = "http://localhost:" + GiteaContainer.HTTP_PORT;
        String data = """
                {"name":"%s", "private":false, "auto_init":true, "readme":"Default"}
                """
                .formatted(repository);

        String[] cmd = {
                "/usr/bin/curl",
                "-X",
                "POST",
                "--user",
                devServiceConfig.adminUsername() + ":" + devServiceConfig.adminPassword(),
                "-H",
                "Content-Type: application/json",
                "-d",
                data,
                httpUrl + "/api/v1/user/repos"
        };

        log.debug(String.join(" ", cmd));
        ExecResult execResult = giteaContainer.execInContainer(cmd);
        log.info(execResult.getStdout());
        if (execResult.getExitCode() != 0) {
            throw new RuntimeException("Failed to create repository: " + repository + ":" + execResult.getStderr());
        }
        repositories.add(repository);
        log.info("Created repository: " + repository);
    }

    public String getHttpUrl() {
        return "http://" + getHost() + ":" + getHttpPort();
    }

    public int getHttpPort() {
        return getMappedPort(HTTP_PORT);
    }

    public List<String> getRepositories() {
        return repositories;
    }
}
