package client;

import io.restassured.specification.RequestSpecification;

public class TokenAuth implements IAuth{

    private String token;

    public TokenAuth(String token) {
        this.token = token;
    }

    @Override
    public RequestSpecification apply(RequestSpecification request) {
        return request
                    .header("Token",token);
    }
}
