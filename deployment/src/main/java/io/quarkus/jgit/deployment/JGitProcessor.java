package io.quarkus.jgit.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class JGitProcessor {

    static final String FEATURE = "jgit";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    ExtensionSslNativeSupportBuildItem activateSslNativeSupport() {
        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    }

    @BuildStep
    ReflectiveClassBuildItem reflection() {
        //Classes that use reflection
        return ReflectiveClassBuildItem
                .builder("org.eclipse.jgit.api.MergeCommand$ConflictStyle",
                        "org.eclipse.jgit.api.MergeCommand$FastForwardMode",
                        "org.eclipse.jgit.api.MergeCommand$FastForwardMode$Merge",
                        "org.eclipse.jgit.diff.DiffAlgorithm$SupportedAlgorithm",
                        "org.eclipse.jgit.internal.JGitText",
                        "org.eclipse.jgit.lib.CommitConfig$CleanupMode",
                        "org.eclipse.jgit.lib.CoreConfig$AutoCRLF",
                        "org.eclipse.jgit.lib.CoreConfig$CheckStat",
                        "org.eclipse.jgit.lib.CoreConfig$EOL",
                        "org.eclipse.jgit.lib.CoreConfig$EolStreamType",
                        "org.eclipse.jgit.lib.CoreConfig$HideDotFiles",
                        "org.eclipse.jgit.lib.CoreConfig$SymLinks",
                        "org.eclipse.jgit.lib.CoreConfig$LogRefUpdates",
                        "org.eclipse.jgit.lib.CoreConfig$TrustLooseRefStat",
                        "org.eclipse.jgit.lib.CoreConfig$TrustPackedRefsStat")
                .fields().methods()
                .build();
    }

    @BuildStep
    void runtimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> producer) {
        producer.produce(new RuntimeInitializedClassBuildItem("org.eclipse.jgit.internal.storage.file.WindowCache"));
        producer.produce(new RuntimeInitializedClassBuildItem("org.eclipse.jgit.transport.HttpAuthMethod$Digest"));
    }

    @BuildStep
    NativeImageResourceBundleBuildItem includeResourceBundle() {
        return new NativeImageResourceBundleBuildItem("org.eclipse.jgit.internal.JGitText");
    }
}
