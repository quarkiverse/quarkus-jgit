package io.quarkus.jgit.deployment;

import java.util.Map;
import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CuratedApplicationShutdownBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem.RunningDevService;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.devservices.common.ContainerShutdownCloseable;

public class JGitDevServicesProcessor {

    private static final Logger log = Logger.getLogger(JGitDevServicesProcessor.class);
    static volatile RunningDevService devService;

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = { GlobalDevServicesConfig.Enabled.class, DevServicesEnabled.class })
    DevServicesResultBuildItem createContainer(JGitBuildTimeConfig config,
            CuratedApplicationShutdownBuildItem closeBuildItem,
            BuildProducer<GiteaDevServiceInfoBuildItem> giteaServiceInfo) {
        if (devService != null) {
            // only produce DevServicesResultBuildItem when the dev service first starts.
            return null;
        }
        var gitServer = new GiteaContainer(config.devservices());
        gitServer.start();
        String httpUrl = gitServer.getHttpUrl();
        log.infof("Gitea HTTP URL: %s", httpUrl);
        Map<String, String> configOverrides = Map.of("quarkus.jgit.devservices.http-url", httpUrl);

        ContainerShutdownCloseable closeable = new ContainerShutdownCloseable(gitServer, JGitProcessor.FEATURE);
        closeBuildItem.addCloseTask(closeable::close, true);
        devService = new RunningDevService(JGitProcessor.FEATURE, gitServer.getContainerId(), closeable, configOverrides);

        giteaServiceInfo.produce(new GiteaDevServiceInfoBuildItem(
                gitServer.getHost(),
                gitServer.getHttpPort(),
                config.devservices().adminUsername(),
                config.devservices().adminPassword()));
        return devService.toBuildItem();
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
