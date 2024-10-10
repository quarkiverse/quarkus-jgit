package io.quarkus.jgit.deployment;

import java.io.IOException;
import java.util.Map;
import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;

class JGitProcessor {

    private static final String FEATURE = "jgit";

    private static final Logger log = Logger.getLogger(JGitProcessor.class);

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

    @SuppressWarnings("resource")
    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = { GlobalDevServicesConfig.Enabled.class, DevServicesEnabled.class })
    DevServicesResultBuildItem createContainer(JGitBuildTimeConfig config) {
        var gitServer = new GiteaContainer(config.devservices());
        gitServer.start();

        // Create the admin user
        try {
            gitServer.createAdminUser();
        } catch (IOException | InterruptedException e) {
            log.error("Could not create admin user", e);
        }

        String httpUrl = gitServer.getHttpUrl();
        String sshUrl = gitServer.getSshUrl();
        log.infof("Gitea HTTP URL: %s", httpUrl);
        log.infof("Gitea SSH URL: %s", sshUrl);
        Map<String, String> configOverrides = Map.of(
                "quarkus.jgit.devservices.http-url", httpUrl,
                "quarkus.jgit.devservices.ssh-url", sshUrl);

        return new DevServicesResultBuildItem.RunningDevService(FEATURE, gitServer.getContainerId(),
                gitServer::close, configOverrides).toBuildItem();
    }

    public static class DevServicesEnabled implements BooleanSupplier {

        final JGitBuildTimeConfig config;

        public DevServicesEnabled(JGitBuildTimeConfig config) {
            this.config = config;
        }

        @Override
        public boolean getAsBoolean() {
            return config.devservices().enabled();
        }
    }

}
