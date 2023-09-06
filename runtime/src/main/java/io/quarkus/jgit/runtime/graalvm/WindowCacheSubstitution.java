package io.quarkus.jgit.runtime.graalvm;

import java.util.Random;

import org.eclipse.jgit.internal.storage.file.WindowCache;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(WindowCache.class)
final class WindowCacheSubstitution {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private static Random rng;

}
