package io.terrakube.registry;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class OpenRegistryApplicationTests {

	ClientAndServer mockServer;

	@LocalServerPort
	int port;

	@BeforeAll
	public void setUp() {
		RestAssured.port = port;
		mockServer = mockServer.startClientAndServer(9999);
	}

	@AfterAll
	public void stopServer() {
		mockServer.stop();
		while (!mockServer.hasStopped(10,100L, TimeUnit.MILLISECONDS)){}
	}

}
