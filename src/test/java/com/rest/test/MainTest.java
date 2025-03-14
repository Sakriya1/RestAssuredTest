package com.rest.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@Test
public class MainTest {

    RequestSpecification requestSpecification;
    Response response;
    ValidatableResponse validatableResponse;
    @Test
    public void verifyStatusCode() {

        // Base URL of the API
        RestAssured.baseURI = "http://localhost:8080/books";

        // Username and password for Basic Authentication
        String username = "user"; // Replace with the correct username
        String password = "password"; // Replace with the correct password

        // Create the request specification
        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic(username, password) // Use preemptive basic auth
                .log().all(); // Log all request details (headers, body, etc.)

        // Send GET request and get the response
        Response response = requestSpecification.get();

        // Print the response details for debugging
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.prettyPrint());
        System.out.println("Response Headers: " + response.getHeaders());

        // Perform validation on the response
        ValidatableResponse validatableResponse = response.then();

        /* Validate status code */
        validatableResponse.statusCode(200);

        // Validate status line
        validatableResponse.statusLine("HTTP/1.1 200 ");
    }

    @Test
    public void testCreateBook() {
        String requestBody = "{\n" +
                "    \"name\": \"A to the Bodhisattva Way of Life\",\n" +
                "    \"author\": \"Santideva\",\n" +
                "    \"price\": 15.41\n" +
                "}";

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:8080/books")
                .then()
                .statusCode(201)
                .extract().response();

        // Validate the response body
        response.then().body("name", equalTo("A to the Bodhisattva Way of Life"))
                .body("author", equalTo("Santideva"))
                .body("price", equalTo(15.41f));
    }
    @Test
    public void testUpdateBook() {
        int bookId = 1;

        String updatedRequestBody = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"A to the Bodhisattva Way of Life\",\n" +
                "    \"author\": \"Santideva\",\n" +
                "    \"price\": 20.00\n" +
                "}";

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .body(updatedRequestBody)
                .when()
                .put("http://localhost:8080/books/" + bookId)
                .then()
                .statusCode(200)
                .extract().response();

        // Validate the updated book details
        response.then().body("price", equalTo(20.00f));
    }
    @Test
    public void testDeleteBook() {
        int bookId = 1;

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .when()
                .delete("http://localhost:8080/books/" + bookId)
                .then()
                .statusCode(200)
                .extract().response();

        // Optionally, check if the book is deleted by trying to get the same book
        given().auth().basic("admin", "password")
                .when().get("http://localhost:8080/books/" + bookId)
                .then().statusCode(404); // Expecting 404 Not Found
    }

}