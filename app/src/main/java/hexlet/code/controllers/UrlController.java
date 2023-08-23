package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class UrlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    public static Handler listUrls = ctx -> {
//        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;

        PagedList<Url> pagedList = new QUrl()
            .setFirstRow(page * rowsPerPage)
            .setMaxRows(rowsPerPage)
            .orderBy()
            .id.asc()
            .findPagedList();

        List<Url> urls = pagedList.getList();

        int lastPage = pagedList.getTotalPageCount() + 1;
        int currentPage = pagedList.getPageIndex() + 1;
        List<Integer> pages = IntStream
            .range(1, lastPage)
            .boxed()
            .collect(Collectors.toList());

        ctx.attribute("urls", urls);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("urlsList.html");
    };

    public static Handler showUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(1);

        Url url = new QUrl()
            .id.equalTo(id)
            .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String createdAt = simpleDateFormat.format(Date.from(url.getCreatedAt()));

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
        URL url = ctx.formParamAsClass("url", URL.class).getOrDefault(new URL(""));

        try {
            String protocol = url.getProtocol() + "://";
            String file = url.getHost();
            String port = ":" + url.getPort();

            if (port.equals(":-1")) {
                port = "";
            }

            String name = protocol + file + port;

            int existOfUrl = new QUrl()
                .name.eq(name)
                .findCount();

            if (existOfUrl == 0) {
                Url url1 = new Url(name);
                url1.save();
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                LOGGER.info("Страница успешно добавлена");
                ctx.redirect("/urls");
            } else {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "info");
                LOGGER.info("Страница уже существует");
                ctx.redirect("/urls");
            }
        } catch (Exception exception) {
            LOGGER.info("Ошибка при парсинге");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("mainPage.html");
        }
    };
}
