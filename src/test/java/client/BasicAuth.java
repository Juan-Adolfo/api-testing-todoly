package client;

import io.restassured.specification.RequestSpecification;

public class BasicAuth implements IAuth{

    private String user;
    private String password;

    public BasicAuth(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public RequestSpecification apply(RequestSpecification request) {
        return request
                    .auth()
                    .preemptive()
                    .basic(user,password);
    }
}
