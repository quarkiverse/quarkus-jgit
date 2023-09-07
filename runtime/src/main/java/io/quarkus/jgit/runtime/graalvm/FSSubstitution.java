package io.quarkus.jgit.runtime.graalvm;

import org.eclipse.jgit.util.FS;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(FS.class)
final class FSSubstitution {

    /**
     * The original method caches the user.home property during build time.
     */
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private volatile Holder userHome;

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private volatile Holder gitSystemConfig;

    @TargetClass(className = "org.eclipse.jgit.util.FS$Holder")
    public static final class Holder {

    }
}
