package io.quarkiverse.quarkus.jgit.sshd.deployment;

import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class JGitSshdProcessor {

    @BuildStep
    ServiceProviderBuildItem serviceProvider() {
        return new ServiceProviderBuildItem(SshSessionFactory.class.getName(), SshdSessionFactory.class.getName());
    }
}
