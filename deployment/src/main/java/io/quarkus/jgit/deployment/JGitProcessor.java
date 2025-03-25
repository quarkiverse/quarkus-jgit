package io.quarkus.jgit.deployment;

import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.CommitConfig;
import org.eclipse.jgit.lib.CoreConfig;
import org.eclipse.jgit.lib.GpgConfig;

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
        return ReflectiveClassBuildItem.builder(
                MergeCommand.ConflictStyle.class,
                MergeCommand.FastForwardMode.class,
                MergeCommand.FastForwardMode.Merge.class,
                DiffAlgorithm.SupportedAlgorithm.class,
                JGitText.class,
                CommitConfig.CleanupMode.class,
                CoreConfig.AutoCRLF.class,
                CoreConfig.CheckStat.class,
                CoreConfig.EOL.class,
                CoreConfig.EolStreamType.class,
                CoreConfig.HideDotFiles.class,
                CoreConfig.SymLinks.class,
                CoreConfig.LogRefUpdates.class,
                CoreConfig.TrustStat.class,
                DirCache.DirCacheVersion.class,
                GpgConfig.GpgFormat.class).fields().methods().build();
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
