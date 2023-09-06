package io.quarkus.jgit.runtime.graalvm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.eclipse.jgit.lib.internal.WorkQueue;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(WorkQueue.class)
@Substitute
final class WorkQueueSubstitution {

    private static final ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors
            .newScheduledThreadPool(1);

    @Substitute
    public static ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }
}
