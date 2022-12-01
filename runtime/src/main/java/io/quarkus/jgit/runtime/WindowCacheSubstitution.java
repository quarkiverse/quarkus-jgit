package io.quarkus.jgit.runtime;

import java.util.Random;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "org.eclipse.jgit.internal.storage.file.WindowCache")
public final class WindowCacheSubstitution {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private static Random rng;

}
