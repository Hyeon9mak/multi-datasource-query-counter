package hyeon9mak.multidatasourcequerycounter;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class QueryCountPerRequest {

    private String apiUrl;
    private int totalQueryCount;
    private long totalQueryMilliSeconds;

    public QueryCountPerRequest() {
        this.apiUrl = "";
        this.totalQueryCount = 0;
        this.totalQueryMilliSeconds = 0L;
    }

    public void incrementQueryCount(long executionMilliSeconds) {
        totalQueryCount++;
        totalQueryMilliSeconds += executionMilliSeconds;
    }

    public void updateApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public int getTotalQueryCount() {
        return totalQueryCount;
    }

    public long getTotalQueryMilliSeconds() {
        return totalQueryMilliSeconds;
    }
}
