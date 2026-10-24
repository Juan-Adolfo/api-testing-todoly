package client;

import io.restassured.specification.RequestSpecification;

public class NoAuth implements IAuth{
    @Override
    public RequestSpecification apply(RequestSpecification request) {
        return request;
    }
}
