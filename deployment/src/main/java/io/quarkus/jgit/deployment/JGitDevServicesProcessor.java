package io.quarkus.jgit.deployment;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

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

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = { GlobalDevServicesConfig.Enabled.class })
    DevServicesResultBuildItem createContainer(JGitBuildTimeConfig config,
            Optional<GiteaDevServiceRequestBuildItem> devServiceRequest,
            CuratedApplicationShutdownBuildItem closeBuildItem,
            BuildProducer<GiteaDevServiceInfoBuildItem> giteaServiceInfo) {
        if (devService != null) {
            // only produce DevServicesResultBuildItem when the dev service first starts.
            return null;
        }

        if (!config.devservices().enabled() && !devServiceRequest.isPresent()) {
            // JGit Dev Service not enabled
            return null;
        }
        var gitServer = new GiteaContainer(config.devservices(), devServiceRequest);
        gitServer.start();
        String httpUrl = gitServer.getHttpUrl();
        log.infof("Gitea HTTP URL: %s", httpUrl);
        Map<String, String> configOverrides = Map.of("quarkus.jgit.devservices.http-url", httpUrl);

        Optional<String> sharedNetworkHost = config.devservices().networkAlias()
                .or(() -> devServiceRequest.map(GiteaDevServiceRequestBuildItem::getAlias));
        OptionalInt sharedNetworkHttpPort = OptionalInt.of(GiteaContainer.HTTP_PORT);

        ContainerShutdownCloseable closeable = new ContainerShutdownCloseable(gitServer, JGitProcessor.FEATURE);
        closeBuildItem.addCloseTask(closeable::close, true);
        devService = new RunningDevService(JGitProcessor.FEATURE, gitServer.getContainerId(), closeable, configOverrides);

        giteaServiceInfo.produce(new GiteaDevServiceInfoBuildItem(
                gitServer.getHost(),
                gitServer.getHttpPort(),
                sharedNetworkHost,
                sharedNetworkHttpPort,
                config.devservices().adminUsername(),
                config.devservices().adminPassword(),
                config.devservices().organization(),
                gitServer.getRepositories()));
        return devService.toBuildItem();
    }
}
