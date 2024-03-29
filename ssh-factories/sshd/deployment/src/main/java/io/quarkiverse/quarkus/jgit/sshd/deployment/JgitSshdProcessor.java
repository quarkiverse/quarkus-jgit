package io.quarkiverse.quarkus.jgit.sshd.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class JgitSshdProcessor {

    private static final String FEATURE = "jgit-sshd";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
