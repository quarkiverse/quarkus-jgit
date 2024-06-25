package io.quarkiverse.quarkus.jgit.jsch.deployment;

import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class JGitJschProcessor {

    @BuildStep
    ServiceProviderBuildItem serviceProvider() {
        return new ServiceProviderBuildItem(SshSessionFactory.class.getName(), JschConfigSessionFactory.class.getName());
    }
}
