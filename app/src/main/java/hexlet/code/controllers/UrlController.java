package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.repositories.BaseRepository;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class UrlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;

//        PagedList<Url> pagedList = new QUrl()
//            .setFirstRow(page * rowsPerPage)
//            .setMaxRows(rowsPerPage)
//            .orderBy()
//            .id.asc()
//            .findPagedList();

        List<Url> urls = UrlRepository
            .getEntities(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

        List<UrlCheck> urlChecks = UrlCheckRepository
            .getEntities();

//        Map<Long, UrlCheck> urlChecks = new QUrlCheck()
//            .url.id.asMapKey()
//            .orderBy()
//                .createdAt.desc()
//            .findMap();

        Map<Long, UrlCheck> urlCheckMap = new HashMap<>();

        for (UrlCheck urlCheck: urlChecks) {
            if (!urlCheckMap.containsKey(urlCheck.getUrlId())) {
                urlCheckMap.put(urlCheck.getUrlId(), urlCheck);
            }
        }

        int lastPage = urlChecks.size() + 1;
        int currentPage = page + 1;
        List<Integer> pages = IntStream
            .range(1, lastPage)
            .boxed()
            .collect(Collectors.toList());

        System.out.println(urls.get(0).getCreatedAt());

        ctx.attribute("urls", urls);
        if (!urlChecks.isEmpty()) {
            ctx.attribute("urlChecks", urlChecks);
        }
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("urlsList.html");
    };

    public static Handler showUrl = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(1L);

        Url url = UrlRepository.findById(id)
            .orElseThrow(NotFoundResponse::new);

        if (url == null) {
            throw new NotFoundResponse();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String createdAt = simpleDateFormat.format(url.getCreatedAt());

        String sql = """
        SELECT * FROM url_checks
        WHERE url_id=?;
            """;

        try (Connection conn = BaseRepository.dataSource.getConnection();
             PreparedStatement preStat = conn.prepareStatement(sql)) {
            preStat.setLong(1, id);
            ResultSet urlChecks = preStat.executeQuery();
            if (urlChecks.next()) {
                ctx.attribute("urlChecks", urlChecks);
            }
        }

        ctx.attribute("id", id);
        ctx.attribute("name", url.getName());
        ctx.attribute("createdAt", createdAt);
        ctx.render("showUrl.html");
    };

    public static Handler newUrl = ctx -> {
        Url url = new Url();
        ctx.attribute("url", url);
        ctx.render("mainPage.html");
    };

    public static Handler createUrl = ctx -> {
        String nameOfUrl = ctx.formParam("url");
        URL requestUrl;

        try {
            requestUrl = new URL(nameOfUrl);
        } catch (Exception exception) {
            LOGGER.info("Ошибка при парсинге");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        String protocol = requestUrl.getProtocol() + "://";
        String file = requestUrl.getHost();
        String port = ":" + requestUrl.getPort();

        if (port.equals(":-1")) {
            port = "";
        }

        String name = protocol + file + port;

        boolean existOfUrl = UrlRepository.findByName(name).isPresent();

        if (!existOfUrl) {
            Url url1 = new Url(name);
            UrlRepository.save(url1);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            LOGGER.info("Страница успешно добавлена");
        } else {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
            LOGGER.info("Страница уже существует");
        }

        ctx.redirect("/urls");
    };

    public static Handler makeCheck = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(1L);

        Url url = UrlRepository.findById(id).orElseThrow();

        try {
            HttpResponse response = Unirest
                .get(url.getName())
                .asString();

            Document document = Jsoup.parse(response.getBody().toString());

            UrlCheck urlCheck = new UrlCheck();
            int statusCode = response.getStatus();

            String title = document.title();
            String description = document
                .getElementsByAttributeValue("name", "description")
                .attr("content");
            String h1 = document.select("h1").first().text();

            urlCheck.setStatusCode(statusCode);
            urlCheck.setTitle(title);
            urlCheck.setDescription(description);
            urlCheck.setH1(h1);
            urlCheck.setUrlId(url.getId());
            UrlCheckRepository.save(urlCheck);

            url.setId(id);
            List<UrlCheck> urlChecks = UrlCheckRepository.getEntities();
            urlChecks.add(urlCheck);
            UrlRepository.save(url);

            LOGGER.info("Страница проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.sessionAttribute("flash", "Страница успешно проверена");
        } catch (UnirestException exception) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
        } catch (Exception exception) {
            ctx.sessionAttribute("flash", exception.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
        }
        ctx.redirect("/urls/" + id);
    };
}
