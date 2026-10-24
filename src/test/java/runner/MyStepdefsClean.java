package runner;

import client.BasicAuth;
import client.FactoryRequest;
import client.IAuth;
import client.NoAuth;
import client.RequestInfo;
import client.TokenAuth;
import config.Configuration;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

public class MyStepdefsClean {
    Response response;
    RequestInfo info;
    Map<String,String> variables = new HashMap<>();

    @Given("generate unique email in {string}")
    public void generateUniqueEmail(String variable) {
        String email = "qa.todoly." + System.currentTimeMillis() + "." + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        variables.put(variable, email);
        System.out.println("Variable "+variable+" = "+email);
    }

    @Given("generate password in {string}")
    public void generatePassword(String variable) {
        String password = "Pw" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        variables.put(variable, password);
        System.out.println("Variable "+variable+" = "+password);
    }

    @When("send {word} request {string} with {word} auth")
    public void sendRequestWithAuth(String method, String url, String authType) {
        sendRequest(method, url, authType, "");
    }

    @When("send {word} request {string} with {word} auth and body")
    public void sendRequestWithAuthAndBody(String method, String url, String authType, String body) {
        sendRequest(method, url, authType, body);
    }

    @Then("code should be {int}")
    public void codeShouldBe(int expectedCode) {
        response.then()
                .statusCode(expectedCode);
    }

    @And("the {string} should be {string}")
    public void theShouldBe(String attribute, String value) {
        response.then()
                .body(attribute,hasToString(replaceVar(value)));
    }

    @And("the response should not contain {string}")
    public void theResponseShouldNotContain(String attribute) {
        response.then()
                .body("$",not(hasKey(attribute)));
    }

    @And("the response should match schema {string}")
    public void theResponseShouldMatchSchema(String schemaFile) {
        response.then()
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }

    @And("save {string} in {string}")
    public void saveAttribute(String attribute, String variable) {
        String value = String.valueOf((Object) response.then().extract().path(attribute));
        variables.put(variable, value);
        System.out.println("Variable "+variable+" = "+value);
    }

    // if the scenario fails halfway, the created user is deleted so it does not stay orphaned
    @After
    public void deleteUserIfScenarioFailed(Scenario scenario) {
        if (scenario.isFailed() && variables.containsKey("EMAIL") && variables.containsKey("PASSWORD")) {
            System.out.println("Cleanup: deleting user "+variables.get("EMAIL"));
            RequestInfo cleanup = new RequestInfo(Configuration.BASE_URL+"api/user/0.json", "", buildAuth("BASIC"));
            FactoryRequest.make("DELETE").send(cleanup).then().log().all();
        }
    }

    private void sendRequest(String method, String url, String authType, String body) {
        info = new RequestInfo(Configuration.BASE_URL+replaceVar(url), replaceVar(body), buildAuth(authType));
        response = FactoryRequest.make(method).send(info);
        response.then().log().all();
    }

    // BASIC uses the EMAIL and PASSWORD variables, TOKEN uses the TOKEN variable
    private IAuth buildAuth(String authType) {
        switch (authType.toUpperCase()) {
            case "NONE":
                return new NoAuth();
            case "BASIC":
                return new BasicAuth(variables.get("EMAIL"), variables.get("PASSWORD"));
            case "TOKEN":
                return new TokenAuth(variables.get("TOKEN"));
            default:
                throw new IllegalArgumentException("Invalid auth type: " + authType);
        }
    }

    private String replaceVar(String value){
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }
        return value;
    }

}
