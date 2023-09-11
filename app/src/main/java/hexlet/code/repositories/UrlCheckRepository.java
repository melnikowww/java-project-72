package hexlet.code.repositories;

import hexlet.code.domain.UrlCheck;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
//import java.util.Optional;

import java.sql.PreparedStatement;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = """
            INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at)
            VALUES (?, ?, ?, ?, ?, ?);
            """;
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            var datetime = new Timestamp(System.currentTimeMillis());

            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setLong(5, urlCheck.getUrlId());
            preparedStatement.setTimestamp(6, datetime);

            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id");
            }
        }
    }

//    public static Optional<UrlCheck> find(Long id) throws SQLException {
//        var sql = "SELECT * FROM url_checks WHERE id = ?;";
//        try (var conn = dataSource.getConnection();
//             var stmt = conn.prepareStatement(sql)) {
//            stmt.setLong(1, id);
//            var resultSet = stmt.executeQuery();
//            if (resultSet.next()) {
//                int statusCode = resultSet.getInt("status_code");
//                String title = resultSet.getString("title");
//                String h1 = resultSet.getString("h1");
//                String description = resultSet.getString("description");
//                Long urlId = resultSet.getLong("url_id");
//                Timestamp createdAt = resultSet.getTimestamp("created_at");
//                UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
//                urlCheck.setId(id);
//                urlCheck.setCreatedAt(createdAt);
//                return Optional.of(urlCheck);
//            }
//            return Optional.empty();
//        }
//    }

    public static List<UrlCheck> getEntitiesById(Long idOfUrl) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id=? ORDER BY id DESC;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idOfUrl);
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");
                Long urlId = resultSet.getLong("url_id");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
                urlCheck.setId(id);
                urlCheck.setCreatedAt(createdAt);
                result.add(urlCheck);
            }
            return result;
        }
    }

    public static List<UrlCheck> getEntities() throws SQLException {
        var sql = "SELECT * FROM url_checks ORDER BY id DESC;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");
                Long urlId = resultSet.getLong("url_id");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
                urlCheck.setId(id);
                urlCheck.setCreatedAt(createdAt);
                result.add(urlCheck);
            }
            return result;
        }
    }

}
