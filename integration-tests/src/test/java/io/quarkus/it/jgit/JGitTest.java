package io.quarkus.it.jgit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JGitTest {

    @Test
    void shouldClone() {
        given().get("/jgit/clone").then().body(is("master"));
    }

}
