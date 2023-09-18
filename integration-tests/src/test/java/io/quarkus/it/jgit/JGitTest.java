package io.quarkus.it.jgit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.SystemReader;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JGitTest {

    @Test
    void shouldClone() {
        given().get("/jgit/clone").then().body(is("master"));
    }

    @Test
    void shouldUseConfigFromRuntime() throws Exception {
        FileBasedConfig fileBasedConfig = (FileBasedConfig) SystemReader.getInstance().getJGitConfig();
        String expected = fileBasedConfig.getFile().getAbsolutePath();
        given().get("/jgit/config").then().log().ifValidationFails().body(is(expected));
    }

    @Test
    void shouldDiff() {
        given().get("/jgit/diff").then().body(is("153"));
    }

}
