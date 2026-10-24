package client;

import io.restassured.specification.RequestSpecification;

public interface IAuth {
    RequestSpecification apply(RequestSpecification request);
}
