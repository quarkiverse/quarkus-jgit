package io.quarkus.jgit.deployment;

import static org.testcontainers.containers.wait.strategy.Wait.forListeningPorts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import com.github.dockerjava.api.command.InspectContainerResponse;

class GiteaContainer extends GenericContainer<GiteaContainer> {

    /**
     * Logger which will be used to capture container STDOUT and STDERR.
     */
    private static final Logger log = Logger.getLogger(GiteaContainer.class);
    static final int HTTP_PORT = 3000;

    private JGitBuildTimeConfig.DevService devServiceConfig;
    private List<String> repositories = new ArrayList<>();

    GiteaContainer(JGitBuildTimeConfig.DevService devServiceConfig,
            Optional<GiteaDevServiceRequestBuildItem> devServiceRequest) {
        super("gitea/gitea:latest-rootless");
        this.devServiceConfig = devServiceConfig;
        withEnv("GITEA__security__INSTALL_LOCK", "true");
        withEnv("GITEA__server__DISABLE_SSH", "true");
        withExposedPorts(HTTP_PORT);
        withReuse(devServiceConfig.reuse());
        waitingFor(forListeningPorts(HTTP_PORT));
        // Needed for podman (see https://github.com/testcontainers/testcontainers-java/issues/7310)
        withStartupAttempts(2);

        Optional<String> networkAlias = devServiceConfig.networkAlias()
                .or(() -> devServiceRequest.map(GiteaDevServiceRequestBuildItem::getAlias));
        networkAlias.ifPresent(alias -> {
            withNetworkAliases(alias);
            withNetwork(Network.SHARED);
        });

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
                createOrganization(this, devServiceConfig.organization());
                for (String repository : devServiceConfig.repositories().orElse(Collections.emptyList())) {
                    createRepository(this, devServiceConfig.organization(), repository);
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

    private void createOrganization(GiteaContainer giteaContainer, String org)
            throws UnsupportedOperationException, IOException, InterruptedException {
        String httpUrl = "http://localhost:" + GiteaContainer.HTTP_PORT + "/api/v1/orgs";
        String data = """
                {"username":"%s"}
                """
                .formatted(org);

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
                httpUrl
        };

        log.debug(String.join(" ", cmd));
        ExecResult execResult = giteaContainer.execInContainer(cmd);
        log.info(execResult.getStdout());
        if (execResult.getExitCode() != 0) {
            throw new RuntimeException("Failed to create organization: " + org + ":" + execResult.getStderr());
        }
        repositories.add(org);
        log.info("Created organization: " + org);
    }

    private void createRepository(GiteaContainer giteaContainer, String org, String repository)
            throws UnsupportedOperationException, IOException, InterruptedException {
        String httpUrl = "http://localhost:" + GiteaContainer.HTTP_PORT + "/api/v1/orgs/" + org + "/repos";
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
                httpUrl
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
