package io.quarkus.jgit.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

public final class GiteaDevServiceInfoBuildItem extends SimpleBuildItem {

    private final String url;
    private final String host;
    private final String username;
    private final String password;

    public GiteaDevServiceInfoBuildItem(String url, String host, String username, String password) {
        this.url = url;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
