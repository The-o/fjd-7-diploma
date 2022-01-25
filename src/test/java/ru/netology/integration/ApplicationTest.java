package ru.netology.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.netology.service.FileStorageService;
import ru.netology.service.InMemoryFileStorageService;

@RunWith(SpringRunner.class)
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {ApplicationTest.Initializer.class})
@AutoConfigureMockMvc
public class ApplicationTest {

    private static final String DB_IMAGE_NAME = "postgres";

    @Container
    private static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DB_IMAGE_NAME);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
              "spring.datasource.url=" + db.getJdbcUrl(),
              "spring.datasource.username=" + db.getUsername(),
              "spring.datasource.password=" + db.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @TestConfiguration
    public static class Config{

        @Bean
        @Primary
        public FileStorageService fileStorageService() {
            return new InMemoryFileStorageService();
        }

    }

    @Autowired
    private MockMvc mvc;

    @Test
    public void testLoginEmptyBody() throws Exception {
        mvc.perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.message").value("Login error"));
    }

    @Test
    public void testLoginNoCredentials() throws Exception {
        JSONObject body = new JSONObject();
        mvc.perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(body.toString())
            )
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.message").value("Login error"));
    }


    @Test
    public void testLoginWrongCredentials() throws Exception {
        JSONObject requestBody = new JSONObject()
            .put("login", "wrong")
            .put("password", "credentials");

        mvc.perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestBody.toString())
            )
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.message").value("Login error"));
    }

    @Test
    public void testLogoutNoAuthToken() throws Exception {
        mvc.perform(
                post("/logout")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @Test
    public void testLogoutWrongAuthToken() throws Exception {
        mvc.perform(
                post("/logout")
                    .header("Auth-Token", "Bearer wrong-token")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @ParameterizedTest
    @MethodSource("requestsDataSource")
    public void testMethodsNoAuthToken(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mvc.perform(requestBuilder)
            .andExpect(status().is(401))
            .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @ParameterizedTest
    @MethodSource("requestsDataSource")
    public void testMethodsWrongAuthToken(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mvc.perform(
                requestBuilder
                    .header("Auth-Token", "wrong-token")
            )
            .andExpect(status().is(401))
            .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    private static Stream<MockHttpServletRequestBuilder> requestsDataSource() throws JSONException {
        return Stream.of(
            get("/list"),
            multipart("/file?filename=file.txt")
                .file("file", "TEST".getBytes()),
            put("/file?filename=file.txt")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    new JSONObject()
                        .put("filename", "renamed.txt")
                        .toString()
                ),
            get("/file?filename=file.txt"),
            delete("/file?filename=file.txt")
        );
    }

    @Test
    public void testWorkingScenario() throws Exception {
        String authToken = new JSONObject(
            mvc.perform(
                    post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                            new JSONObject()
                                .put("login", "login")
                                .put("password", "password")
                                .toString()
                        )
                )
                .andExpect(status().isOk())
                .andReturn()
                    .getResponse()
                    .getContentAsString()
        ).getString("auth-token");

        mvc.perform(
                multipart("/file?filename={filename}", "test.txt")
                    .file("file", "TEST".getBytes())
                    .header("Auth-Token", "Bearer " + authToken)
            )
            .andExpect(status().isOk());

        mvc.perform(
                get("/list")
                    .header("Auth-Token", "Bearer " + authToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].filename").value("test.txt"))
            .andExpect(jsonPath("$[0].size").value(4));

        mvc.perform(
                put("/file?filename={filename}", "test.txt")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(
                        new JSONObject()
                            .put("filename", "renamed.txt")
                            .toString()
                    )
                    .header("Auth-Token", "Bearer " + authToken)
            )
            .andExpect(status().isOk());

        mvc.perform(
                get("/list")
                    .header("Auth-Token", "Bearer " + authToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].filename").value("renamed.txt"))
            .andExpect(jsonPath("$[0].size").value(4));

        mvc.perform(
                get("/file?filename={filename}", "renamed.txt")
                    .header("Auth-Token", "Bearer " + authToken)
            )
            .andExpect(status().isOk())
            .andExpect(content().string("TEST"));

        mvc.perform(
                delete("/file?filename={filename}", "renamed.txt")
                    .header("Auth-Token", "Bearer " + authToken)
            ).andExpect(status().isOk());

        mvc.perform(
                get("/list")
                    .header("Auth-Token", "Bearer " + authToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

}