package hexlet.code;


import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import io.javalin.Javalin;
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
    private static Database database;

    @BeforeAll
    public static void beforeAll() throws SQLException, IOException {
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

//    @BeforeEach
//    void static beforeEach() {
////        database.script().run("/truncate.sql");
//        database.script().run("/seed-test.sql");
//    }

    @Transactional
    @Test
    public void testNewUrl() {
        HttpResponse<String> response = Unirest
            .get(baseUrl)
            .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Анализатор страниц");
    }

    @Transactional
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

    @Transactional
    @Test
    public void testListUrls() {
        HttpResponse response = Unirest
            .get(baseUrl + "/urls")
            .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().toString()).contains("Сайты");
    }

    @Transactional
    @Test
    public void testShowUrl() {
        String name = "https://ebean.io";

        HttpResponse responsePost = Unirest
            .post(baseUrl + "/urls")
            .field("url", name)
            .asEmpty();

        Url url = new QUrl()
            .name.equalTo(name)
            .findOne();

        long id = url.getId();

        HttpResponse response = Unirest
            .get(baseUrl + "/urls/" + id)
            .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().toString()).contains("Сайт " + name);
    }

    @Transactional
    @Test
    public void testUrlCheck() throws IOException {
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

        Url url = new QUrl()
            .name.equalTo(serverUrl)
                .findOne();

        assertThat(url).isNotNull();
        assertThat(url.getId()).isEqualTo(url.getId());
        assertThat(url.getName()).isEqualTo(serverUrl);

        Long id = url.getId();

        Unirest
            .post(baseUrl + "/urls/" + id + "/checks")
            .asEmpty();

        server.shutdown();

        UrlCheck urlCheck = new QUrlCheck()
            .url.equalTo(url)
            .orderBy()
            .createdAt.desc()
            .findOne();

        assertThat(urlCheck).isNotNull();
        assertThat(urlCheck.getStatusCode()).isEqualTo(200);
        assertThat(urlCheck.getTitle()).contains("Анализатор страниц");
    }

}
