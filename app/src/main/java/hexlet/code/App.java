package hexlet.code;

import hexlet.code.controllers.UrlController;
import hexlet.code.repositories.BaseRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

public class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "8080");
        return Integer.parseInt(port);
    }

    public static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static boolean isProd() {
        return getMode().equals("production");
    }

    public static void addRoutes(Javalin app) {
        app.get("/", UrlController.newUrl);
        app.get("/urls", UrlController.listUrls);
        app.post("/urls", UrlController.createUrl);
        app.get("/urls/{id}", UrlController.showUrl);
        app.post("/urls/{id}/checks", UrlController.makeCheck);
    }

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
    }

    public static Javalin getApp() throws IOException, SQLException {
//        Properties props = new Properties();

//        if (isProd()) {
//            props.setProperty("jdbcUrl", System.getenv("JDBC_DATABASE_URL"));
//            props.setProperty("dataSource.user", System.getenv("JDBC_DATABASE_USERNAME"));
//            props.setProperty("dataSource.password", System.getenv("JDBC_DATABASE_PASSWORD"));
//            props.setProperty("dataSource.portNumber", "PORT");
//        } else {
//            props.setProperty("jdbcUrl", "jdbc:h2:mem:project4");
//            props.setProperty("dataSource.user", "user");
//            props.setProperty("dataSource.password", "user");
//        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        URL schema = App.class.getClassLoader().getResource("schema.sql");
        File file = new File(schema.getFile());
        String sql = Files.lines(file.toPath())
            .collect(Collectors.joining());

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        Javalin app = Javalin.create(config -> {
            if (!isProd()) {
                config.plugins.enableDevLogging();
            }
            JavalinThymeleaf.init(getTemplateEngine());
        });

        addRoutes(app);
        app.before(ctx -> ctx.attribute("ctx", ctx));
        return app;
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("/templates/");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }

    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        app.start(getPort());
    }

}
