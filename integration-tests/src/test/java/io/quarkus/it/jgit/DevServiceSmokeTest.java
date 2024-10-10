package io.quarkus.it.jgit;

import static io.restassured.RestAssured.given;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.jgit.runtime.JGitRuntimeConfig;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class DevServiceSmokeTest {

    @Inject
    JGitRuntimeConfig config;

    @Test
    public void serviceRunning() {
        given()
                .get(config.devservices().url())
                .then()
                .statusCode(200);
    }
}
