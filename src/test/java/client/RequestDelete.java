package client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RequestDelete implements IRequest{
    @Override
    public Response send(RequestInfo info) {
        return info.getAuth().apply(given())
                    .log()
                    .all()
                .when()
                    .delete(info.getUrl());
    }
}
