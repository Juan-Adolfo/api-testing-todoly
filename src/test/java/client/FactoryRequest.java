package client;

public class FactoryRequest {

    public static IRequest make(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return new RequestGet();
            case "POST":
                return new RequestPost();
            case "PUT":
                return new RequestPut();
            case "DELETE":
                return new RequestDelete();
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }
    }
}
