package io.quarkus.jgit.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * A build item that represents the information required to connect to a Gitea dev service.
 */
public final class GiteaDevServiceInfoBuildItem extends SimpleBuildItem {

    private final String host;
    private final int httpPort;
    private final String adminUsername;
    private final String adminPassword;

    public GiteaDevServiceInfoBuildItem(String host, int httpPort, String adminUsername, String adminPassword) {
        this.host = host;
        this.httpPort = httpPort;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
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

}
