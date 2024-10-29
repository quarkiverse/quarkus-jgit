package io.quarkus.it.jgit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.gitea.ApiClient;
import io.gitea.Configuration;
import io.gitea.api.OrganizationApi;
import io.gitea.auth.HttpBasicAuth;
import io.gitea.model.CreateRepoOption;
import io.quarkus.jgit.runtime.JGitRuntimeConfig;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class DevServiceTest {

    @Inject
    JGitRuntimeConfig config;

    @BeforeAll
    public static void setCredentials() {
        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider("quarkus", "quarkus"));
    }

    @BeforeAll
    public static void createRepository() throws Exception {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        String httpUrl = ConfigProvider.getConfig().getValue("quarkus.jgit.devservices.http-url", String.class);
        defaultClient.setBasePath(httpUrl + "/api/v1");

        HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("BasicAuth");
        basicAuth.setUsername("quarkus");
        basicAuth.setPassword("quarkus");

        OrganizationApi orgApi = new OrganizationApi();
        orgApi.createOrgRepo("dev", new CreateRepoOption()
                .autoInit(true)
                ._private(false)
                .name("test-repo")
                .readme("Default"));

    }

    @Test
    public void serviceRunning() {
        given()
                .get(config.devservices().httpUrl().orElseThrow())
                .then()
                .statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test-repo", // The repository created in the @BeforeAll method
            "quarkus-jgit-integration-tests", //The project repository created by the Dev Service
            "extra-repo1", // An extra repository created by the Dev Service
            "extra-repo2" // An other extra repository created by the Dev Service
    })
    public void shouldCloneFromDevService(String repositoryName, @TempDir Path tempDir) throws Exception {
        try (Git git = Git.cloneRepository().setDirectory(tempDir.toFile())
                .setURI(config.devservices().httpUrl().get() + "/dev/" + repositoryName + ".git").call()) {
            assertThat(tempDir.resolve("README.md")).isRegularFile();
            assertThat(git.log().call()).extracting(RevCommit::getFullMessage).map(String::trim).contains("Initial commit");
        }
    }

    @Test
    public void shouldPushToDevService(@TempDir Path tempDir) throws Exception {
        try (Git git = Git.cloneRepository().setDirectory(tempDir.resolve("original").toFile())
                .setURI(config.devservices().httpUrl().get() + "/dev/test-repo.git").call()) {
            Path readme = tempDir.resolve("original").resolve("README.md");
            Files.writeString(readme, "Hello, World!");
            // Perform commit
            git.commit().setAll(true).setMessage("Update README").setSign(false).call();
            git.push().call();
        }
        try (Git git = Git.cloneRepository().setDirectory(tempDir.resolve("updated").toFile())
                .setURI(config.devservices().httpUrl().get() + "/dev/test-repo.git").call()) {
            assertThat(tempDir.resolve("updated").resolve("README.md")).hasContent("Hello, World!");
        }
    }

}
