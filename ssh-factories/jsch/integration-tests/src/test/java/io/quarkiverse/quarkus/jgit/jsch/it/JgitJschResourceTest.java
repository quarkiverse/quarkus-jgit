package io.quarkiverse.quarkus.jgit.jsch.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JgitJschResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/jgit-jsch")
                .then()
                .statusCode(200)
                .body(is("Hello jgit-jsch"));
    }
}
