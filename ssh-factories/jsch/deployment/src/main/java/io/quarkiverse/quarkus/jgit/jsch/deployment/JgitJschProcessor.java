package io.quarkiverse.quarkus.jgit.jsch.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class JgitJschProcessor {

    private static final String FEATURE = "jgit-jsch";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
