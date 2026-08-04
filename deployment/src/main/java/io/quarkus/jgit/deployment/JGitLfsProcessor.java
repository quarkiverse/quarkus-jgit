package io.quarkus.jgit.deployment;

import java.util.function.BooleanSupplier;

import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.BuildSteps;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.jgit.runtime.JGitLfsRecorder;

@BuildSteps(onlyIf = JGitLfsProcessor.IsLfsAvailable.class)
class JGitLfsProcessor {

    static class IsLfsAvailable implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return QuarkusClassLoader.isClassPresentAtRuntime("org.eclipse.jgit.lfs.BuiltinLFS");
        }
    }

    @BuildStep
    void registerLfsClasses(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
            BuildProducer<NativeImageResourceBundleBuildItem> resourceBundles) {
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder("org.eclipse.jgit.lfs.BuiltinLFS")
                .methods().build());
        // LfsText and Protocol inner classes need field access for NLS and Gson serialization respectively
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(
                "org.eclipse.jgit.lfs.internal.LfsText",
                "org.eclipse.jgit.lfs.Protocol$Request",
                "org.eclipse.jgit.lfs.Protocol$Response",
                "org.eclipse.jgit.lfs.Protocol$ObjectSpec",
                "org.eclipse.jgit.lfs.Protocol$ObjectInfo",
                "org.eclipse.jgit.lfs.Protocol$Action",
                "org.eclipse.jgit.lfs.Protocol$ExpiringAction",
                "org.eclipse.jgit.lfs.Protocol$Error")
                .fields().methods().build());
        resourceBundles.produce(new NativeImageResourceBundleBuildItem("org.eclipse.jgit.lfs.internal.LfsText"));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void registerBuiltinLfs(JGitLfsRecorder recorder) {
        recorder.registerBuiltinLfs();
    }
}
