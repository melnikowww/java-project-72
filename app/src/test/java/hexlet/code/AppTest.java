package hexlet.code;


import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import javassist.NotFoundException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


public class AppTest {

    private static Javalin app;
    private static String baseUrl;

    @BeforeAll
    public static void beforeAll() throws SQLException, IOException {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:8080";
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

//    @BeforeEach
//    void static beforeEach() {
////        database.script().run("/truncate.sql");
//        database.script().run("/seed-test.sql");
//    }

    @Test
    public void testNewUrl() {
        HttpResponse<String> response = Unirest
            .get(baseUrl)
            .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Анализатор страниц");
    }

    @Test
    public void testCreateUrl() throws SQLException {
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

        Url actualUrl = UrlRepository.findByName(name).orElseThrow();

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
    public void testShowUrl() throws SQLException {
        String name = "https://ebean.io";

        HttpResponse responsePost = Unirest
            .post(baseUrl + "/urls")
            .field("url", name)
            .asEmpty();

        Url url = UrlRepository.findByName(name).orElseThrow();

        long id = url.getId();

        HttpResponse response = Unirest
            .get(baseUrl + "/urls/" + id)
            .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().toString()).contains("Сайт " + name);
    }

    @Test
    public void testUrlCheck() throws IOException, SQLException {
        MockWebServer server = new MockWebServer();

        File html = new File("src/test/resources/templates_test/urlCheckTest.html");
        String body = Jsoup.parse(html, "UTF-8").toString();
        server.enqueue(new MockResponse().setBody(body));

        String serverUrl = server.url("/").toString().replaceAll("/$", "");

        HttpResponse responsePost = Unirest
            .post(baseUrl + "/urls")
            .field("url", serverUrl)
            .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        Url url = UrlRepository.findByName(serverUrl).orElseThrow();

        assertThat(url).isNotNull();
        assertThat(url.getName()).isEqualTo(serverUrl);

        Long id = url.getId();

        Unirest
            .post(baseUrl + "/urls/" + id + "/checks")
            .asEmpty();

        server.shutdown();

        UrlCheck urlCheck = UrlCheckRepository.getEntitiesById(url.getId()).get(0);

        assertThat(urlCheck).isNotNull();
        assertThat(urlCheck.getStatusCode()).isEqualTo(200);
        assertThat(urlCheck.getTitle()).contains("Хекслет — онлайн-школа программирования, онлайн-обучение ИТ-профессиям");
        assertThat(urlCheck.getH1()).contains("Лучшая школа");
        assertThat(urlCheck.getDescription()).contains("Хекслет — лучшая школа программирования");
    }
}
