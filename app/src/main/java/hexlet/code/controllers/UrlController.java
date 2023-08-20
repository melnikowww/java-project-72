package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public final class UrlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    public static Handler listUrls = ctx -> {

    };

    public static Handler newUrl = ctx -> {
        Url url = new Url();
        ctx.attribute("url", url);
        ctx.render("mainPage.html");
    };

    public static Handler createUrl = ctx -> {
        String url = ctx.formParam("url");
        URL url1 = new URL(url);
        try {
            String protocol = url1.getProtocol();
            String file = url1.getFile();
            String port = String.valueOf(url1.getPort());

            if (port.equals("-1")) {
                port = "";
            }

            url = protocol + ":/" + file + ":" + port;

            int existOfUrl = new QUrl()
                .name.eq(url)
                .findCount();

            if (existOfUrl == 0) {
                Url newUrl = new Url(url);
                newUrl.save();
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                LOGGER.info("Страница успешно добавлена");
                ctx.render("urlsList.html");
            } else {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "success");
                LOGGER.info("Страница уже существует");
                ctx.render("urlsList.html");
            }
        } catch (Exception exception) {
            LOGGER.info("Ошибка при парсинге");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("mainPage.html");
        }
    };
}
