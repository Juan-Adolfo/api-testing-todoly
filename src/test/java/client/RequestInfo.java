package client;

public class RequestInfo {

    private String url;
    private String body;
    private IAuth auth;

    public RequestInfo(String url, String body) {
        this(url, body, new NoAuth());
    }

    public RequestInfo(String url, String body, IAuth auth) {
        this.url = url;
        this.body = body;
        this.auth = auth;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public IAuth getAuth() {
        return auth;
    }
}
