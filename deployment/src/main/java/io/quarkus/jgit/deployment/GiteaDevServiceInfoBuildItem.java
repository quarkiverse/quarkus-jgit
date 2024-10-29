package io.quarkus.jgit.deployment;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * A build item that represents the information required to connect to a Gitea dev service.
 */
public final class GiteaDevServiceInfoBuildItem extends SimpleBuildItem {

    private final String host;
    private final int httpPort;

    private final Optional<String> sharedNetworkHost;
    private final OptionalInt sharedNetworkHttpPort;

    private final String adminUsername;
    private final String adminPassword;
    private final String organization;
    private final List<String> repositories;

    public GiteaDevServiceInfoBuildItem(String host, int httpPort, String adminUsername, String adminPassword,
            String organization, List<String> repositories) {
        this(host, httpPort, Optional.empty(), OptionalInt.empty(), adminUsername, adminPassword, organization, repositories);
    }

    public GiteaDevServiceInfoBuildItem(String host, int httpPort, Optional<String> sharedNetworkHost,
            OptionalInt sharedNetworkHttpPort, String adminUsername, String adminPassword, String organization,
            List<String> repositories) {
        this.host = host;
        this.httpPort = httpPort;
        this.sharedNetworkHost = sharedNetworkHost;
        this.sharedNetworkHttpPort = sharedNetworkHttpPort;
        this.adminUsername = adminUsername;
        this.organization = organization;
        this.adminPassword = adminPassword;
        this.repositories = repositories;
    }

    public int httpPort() {
        return httpPort;
    }

    public String host() {
        return host;
    }

    public String adminUsername() {
        return adminUsername;
    }

    public String adminPassword() {
        return adminPassword;
    }

    public String organization() {
        return organization;
    }

    public List<String> repositories() {
        return repositories;
    }

    public Optional<String> sharedNetworkHost() {
        return sharedNetworkHost;
    }

    public OptionalInt sharedNetworkHttpPort() {
        return sharedNetworkHttpPort;
    }
}
