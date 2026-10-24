package client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RequestPost implements IRequest{
    @Override
    public Response send(RequestInfo info) {
        return info.getAuth().apply(given())
                    .body(info.getBody())
                    .log()
                    .all()

                .when()
                    .post(info.getUrl());
    }
}
