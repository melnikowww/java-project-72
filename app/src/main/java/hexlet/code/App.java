package hexlet.code;

import hexlet.code.controllers.RootController;
import io.javalin.Javalin;
//import io.javalin.rendering.template.JavalinThymeleaf;
//import static io.javalin.apibuilder.ApiBuilder.path;
//import static io.javalin.apibuilder.ApiBuilder.get;
//import static io.javalin.apibuilder.ApiBuilder.post;
//
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
//import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
//import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

public class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "8080");
        return Integer.valueOf(port);
    }
    public static void addRoutes(Javalin app) {
        app.get("/", RootController.welcome);
    }
    public static Javalin getApp() {
        Javalin app = Javalin.create();
        addRoutes(app);
        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });
        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

}
