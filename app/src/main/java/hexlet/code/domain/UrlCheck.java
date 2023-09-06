package hexlet.code.domain;

import java.sql.Timestamp;

public final class UrlCheck {


    private Long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;

    private Long urlId;
    private Timestamp createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description, Long urlId, Timestamp createdAt) {
    }

    public UrlCheck() {

    }
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
