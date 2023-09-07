package io.quarkus.jgit.runtime.graalvm;

import java.util.Random;

import org.eclipse.jgit.util.FileUtils;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(FileUtils.class)
final class FileUtilsSubstitution {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private static Random RNG;
}
