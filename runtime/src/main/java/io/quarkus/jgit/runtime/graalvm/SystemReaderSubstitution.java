package io.quarkus.jgit.runtime.graalvm;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.SystemReader;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(SystemReader.class)
final class SystemReaderSubstitution {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.NewInstance, declClass = AtomicReference.class)
    private AtomicReference<FileBasedConfig> systemConfig;

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.NewInstance, declClass = AtomicReference.class)
    private AtomicReference<FileBasedConfig> userConfig;

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.NewInstance, declClass = AtomicReference.class)
    private AtomicReference<FileBasedConfig> jgitConfig;

}
