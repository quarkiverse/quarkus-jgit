package io.quarkiverse.quarkus.jgit.sshd.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JgitSshdResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/jgit-sshd")
                .then()
                .statusCode(200)
                .body(is("Hello jgit-sshd"));
    }
}
