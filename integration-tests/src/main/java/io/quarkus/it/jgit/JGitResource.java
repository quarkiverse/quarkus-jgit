package io.quarkus.it.jgit;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.SystemReader;

@Path("/jgit")
public class JGitResource {

    @GET
    @Path("/clone")
    @Produces(MediaType.TEXT_PLAIN)
    public String cloneRepository() throws Exception {
        URL resource = getClass().getClassLoader().getResource("repos/booster-catalog.bundle");
        java.nio.file.Path to = Files.createTempFile("gitRepositoryBundle", ".bundle");
        try (InputStream is = resource.openStream()) {
            Files.copy(is, to, StandardCopyOption.REPLACE_EXISTING);
        }
        File tmpDir = Files.createTempDirectory("tmpgit").toFile();
        try (Git git = Git.cloneRepository().setDirectory(tmpDir).setURI(to.toString()).call()) {
            return git.getRepository().getBranch();
        }
    }

    @GET
    @Path("/config")
    @Produces(MediaType.TEXT_PLAIN)
    public String getJGitConfig() throws Exception {
        FileBasedConfig fileBasedConfig = (FileBasedConfig) SystemReader.getInstance().getJGitConfig();
        return fileBasedConfig.getFile().getAbsolutePath();

    }
}
