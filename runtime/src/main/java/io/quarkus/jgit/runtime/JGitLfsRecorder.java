package io.quarkus.jgit.runtime;

import org.eclipse.jgit.lfs.BuiltinLFS;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class JGitLfsRecorder {

    public void registerBuiltinLfs() {
        BuiltinLFS.register();
    }
}
