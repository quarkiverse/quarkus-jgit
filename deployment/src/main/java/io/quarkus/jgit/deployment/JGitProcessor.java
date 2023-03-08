package io.quarkus.jgit.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

class JGitProcessor {

    private static final String FEATURE = "jgit";

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
        return new ReflectiveClassBuildItem(true, true,
                "org.eclipse.jgit.api.MergeCommand$FastForwardMode",
                "org.eclipse.jgit.api.MergeCommand$FastForwardMode$Merge",
                "org.eclipse.jgit.internal.JGitText",
                "org.eclipse.jgit.lib.CoreConfig$AutoCRLF",
                "org.eclipse.jgit.lib.CoreConfig$CheckStat",
                "org.eclipse.jgit.lib.CoreConfig$EOL",
                "org.eclipse.jgit.lib.CoreConfig$EolStreamType",
                "org.eclipse.jgit.lib.CoreConfig$HideDotFiles",
                "org.eclipse.jgit.lib.CoreConfig$SymLinks",
                "org.eclipse.jgit.lib.CoreConfig$LogRefUpdates",
                "org.eclipse.jgit.lib.CoreConfig$TrustPackedRefsStat");
    }

    @BuildStep
    void runtimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> producer) {
        producer.produce(new RuntimeInitializedClassBuildItem("org.eclipse.jgit.transport.HttpAuthMethod$Digest"));
        producer.produce(new RuntimeInitializedClassBuildItem("org.eclipse.jgit.lib.GpgSigner"));
    }

    @BuildStep
    NativeImageResourceBundleBuildItem includeResourceBundle() {
        return new NativeImageResourceBundleBuildItem("org.eclipse.jgit.internal.JGitText");
    }
}
