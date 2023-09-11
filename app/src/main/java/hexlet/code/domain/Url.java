package hexlet.code.domain;

import java.sql.Timestamp;

public final class Url {

    private long id;
    private String name;
    private Timestamp createdAt;

    public Url() {
    }
    public Url(String name) {
        this.name = name;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
