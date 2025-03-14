package com.rest.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Assertations {

    private static final String BASE_URL = "http://localhost:8080/books";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    @Test
    public void verifyStatusCode() {
        RestAssured.baseURI = BASE_URL;

        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic(USERNAME, PASSWORD)
                .log().all();

        Response response = requestSpecification.get();

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.prettyPrint());

        ValidatableResponse validatableResponse = response.then();

        // Validate status code
        validatableResponse.statusCode(200);

        // Validate status line properly (fixing the incorrect assertion)
        validatableResponse.statusLine("HTTP/1.1 200 OK"); // Ensure your API actually returns "200 OK"
    }

    @Test
    public void testCreateBook() {
        String requestBody = "{\n" +
                "    \"name\": \"A Guide to the Bodhisattva Way of Life\",\n" +
                "    \"author\": \"Santideva\",\n" +
                "    \"price\": 15.41,\n" +
                "    \"isbn\": \"978-014044-917-4\",\n" +
                "    \"publisher\": \"Penguin Classics\",\n" +
                "    \"yearPublished\": 1997\n" +
                "}";

        Response response = given()
                .auth().basic(USERNAME, PASSWORD)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .extract().response();

        response.then()
                .body("id", notNullValue())
                .body("name", equalTo("A Guide to the Bodhisattva Way of Life"))
                .body("author", equalTo("Santideva"))
                .body("price", equalTo(15.41f))
                .body("isbn", equalTo("978-014044-917-4"))
                .body("publisher", equalTo("Penguin Classics"))
                .body("yearPublished", equalTo(1997))
                .body("createdAt", matchesPattern("^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z)$")); // Ensuring proper timestamp
    }

    @Test
    public void testUpdateBook() {
        int bookId = 1;

        String updatedRequestBody = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"A Guide to the Bodhisattva Way of Life\",\n" +
                "    \"author\": \"Santideva\",\n" +
                "    \"price\": 20.00,\n" +
                "    \"isbn\": \"978-014044-917-4\",\n" +
                "    \"publisher\": \"Penguin Classics\",\n" +
                "    \"yearPublished\": 1997\n" +
                "}";

        Response response = given()
                .auth().basic(USERNAME, PASSWORD)
                .contentType("application/json")
                .body(updatedRequestBody)
                .when()
                .put(BASE_URL + "/" + bookId)
                .then()
                .statusCode(200)
                .extract().response();

        response.then()
                .body("price", equalTo(20.00f))
                .body("updatedAt", matchesPattern("^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z)$")); // Valid timestamp check
    }

    @Test
    public void testDeleteBook() {
        int bookId = 11;

        Response response = given()
                .auth().basic(USERNAME, PASSWORD)
                .contentType("application/json")
                .when()
                .delete(BASE_URL + "/" + bookId)
                .then()
                .statusCode(200)
                .extract().response();

        response.then().body("message", equalTo("Book deleted successfully"));

        // Verify the book no longer exists
        given()
                .auth().basic(USERNAME, PASSWORD)
                .when()
                .get(BASE_URL + "/" + bookId)
                .then()
                .statusCode(404); // Expecting 404 Not Found
}
}
