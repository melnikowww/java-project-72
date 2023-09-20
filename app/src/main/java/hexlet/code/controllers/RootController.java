package hexlet.code.controllers;

import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);
    public static Handler welcome = ctx -> {
        LOGGER.info("Страница загружена");
        ctx.render("mainPage.html");    };
}
