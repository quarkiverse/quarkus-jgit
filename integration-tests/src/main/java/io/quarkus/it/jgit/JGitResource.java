package io.quarkus.it.jgit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
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

    @GET
    @Path("/diff")
    @Produces(MediaType.TEXT_PLAIN)
    public int diff() throws Exception {
        URL resource = getClass().getClassLoader().getResource("repos/booster-catalog.bundle");
        java.nio.file.Path to = Files.createTempFile("gitRepositoryBundle", ".bundle");
        try (InputStream is = resource.openStream()) {
            Files.copy(is, to, StandardCopyOption.REPLACE_EXISTING);
        }
        File tmpDir = Files.createTempDirectory("tmpgit").toFile();
        try (Git git = Git.cloneRepository().setDirectory(tmpDir).setURI(to.toString()).call()) {
            RevCommit oldCommit = null;
            for (RevCommit commit : git.log().call()) {
                if (oldCommit == null) {
                    oldCommit = commit;
                    continue;
                }
                List<DiffEntry> diffEntries = git.diff()
                        .setOldTree(prepareTreeParser(git.getRepository(), oldCommit))
                        .setNewTree(prepareTreeParser(git.getRepository(), commit))
                        .call();
                return diffEntries.size();
            }
        }
        return -1;
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        try (RevWalk walk = new RevWalk(repository)) {
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }

}
