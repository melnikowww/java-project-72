package hexlet.code;


import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import static org.assertj.core.api.Assertions.assertThat;


public class AppTest {

    private static Javalin app;
    private static String baseUrl;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:8080";
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @Test
    public void mainPageTest() {
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Анализатор страниц");
    }

    @Test
    public void testCreate() {
        String name = "https://ebean.io";

        HttpResponse response = Unirest
            .post(baseUrl + "/urls")
            .field("url", name)
            .asEmpty();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().toString()).contains(name);
        assertThat(response.getBody().toString()).contains("Страница успешно добавлена");

        Url actualUrl = new QUrl()
            .name.equalTo(name)
            .findOne();

        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo(name);
    }
}
