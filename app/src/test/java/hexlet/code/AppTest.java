package hexlet.code;


import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Database database;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:8080";
        database = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
//        database.script().run("/truncate.sql");
        database.script().run("/seed-test.sql");
    }

    @Test
    public void testNewUrl() {
        HttpResponse<String> response = Unirest
            .get(baseUrl)
            .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Анализатор страниц");
    }

    @Test
    public void testCreateUrl() {
        String name = "https://ebean.io";

        HttpResponse responsePost = Unirest
            .post(baseUrl + "/urls")
            .field("url", name)
            .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse response = Unirest
            .get(baseUrl + "/urls")
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().toString()).contains(name);
        assertThat(response.getBody().toString()).contains("Страница успешно добавлена");

        Url actualUrl = new QUrl()
            .name.equalTo(name)
            .findOne();

        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo(name);
    }

    @Test
    public void testListUrls() {
        HttpResponse response = Unirest
            .get(baseUrl + "/urls")
            .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().toString()).contains("Сайты");
    }

    @Test
    public void testShowUrl() {
        String name = "https://ebean.io";

        HttpResponse responsePost = Unirest
            .post(baseUrl + "/urls")
            .field("url", name)
            .asEmpty();

        HttpResponse response = Unirest
            .get(baseUrl + "/urls/1")
            .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().toString()).contains("Сайт " + name);

    }
}
