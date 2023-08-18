package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public final class UrlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    public static Handler createUrl = ctx -> {
        String url = ctx.formParam("url");
        URL url1 = new URL(url);
        try {
            String protocol = url1.getProtocol();
            String file = url1.getFile();
            int port = url1.getPort();
            String stringPort = String.valueOf(port);
            if (port == -1) {
                stringPort = "";
            }

            url = protocol + ":/" + file + ":" + stringPort;

            Url search = new QUrl()
                .findOne;

            Url newUrl = new Url(url);



            newUrl.save();
        } catch (Exception exception) {
            ctx.sessionAttribute("flash", "Некорректный URL");
        }
    };
}
