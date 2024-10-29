package io.quarkus.jgit.deployment;

import java.util.List;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * A build item that requests the creation of a Gitea dev service, if not already configured.
 */
public final class GiteaDevServiceRequestBuildItem extends SimpleBuildItem {

    private String alias = "gitea-dev-service";
    private List<String> organizations;
    private List<String> repositories;

    public GiteaDevServiceRequestBuildItem() {
    }

    public GiteaDevServiceRequestBuildItem(String alias, List<String> organizations, List<String> repositories) {
        this.alias = alias;
        this.organizations = organizations;
        this.repositories = repositories;
    }

    public String getAlias() {
        return alias;
    }

    public List<String> getOrganizations() {
        return organizations;
    }

    public List<String> getRepositories() {
        return repositories;
    }
}
