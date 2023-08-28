package hexlet.code.domain;

import hexlet.code.domain.query.QUrlCheck;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
public final class Url extends Model {
    @Id
    private long id;
    private String name;
    @WhenCreated
    private Instant createdAt;
    @OneToMany
    private List<UrlCheck> urlChecks;

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

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }

    public Object getLastStatus() {
        if (!urlChecks.isEmpty()) {
            int countOfChecks = urlChecks.size();

            UrlCheck urlCheck = new QUrlCheck()
                .id.equalTo(countOfChecks)
                .findOne();

            return urlCheck.getStatusCode();
        } else {
            return null;
        }
    };

    public Object getLastCreatedAt() {
        if (!urlChecks.isEmpty()) {
            int countOfChecks = urlChecks.size();

            UrlCheck urlCheck = new QUrlCheck()
                .id.equalTo(countOfChecks)
                .findOne();

            return urlCheck.getCreatedAt();
        } else {
            return null;
        }
    };

    public void setUrlChecks(List<UrlCheck> urlChecks) {
        this.urlChecks = urlChecks;
    }
}
