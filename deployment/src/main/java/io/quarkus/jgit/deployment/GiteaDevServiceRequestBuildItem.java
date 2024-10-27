package io.quarkus.jgit.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * A build item that requests the creation of a Gitea dev service, if not already configured.
 */
public final class GiteaDevServiceRequestBuildItem extends SimpleBuildItem {

    private String alias = "gitea-dev-service";

    public GiteaDevServiceRequestBuildItem() {
    }

    public GiteaDevServiceRequestBuildItem(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
