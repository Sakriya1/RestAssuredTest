package com.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
    public void testGetBooks() {
        Response response = given()
                .auth().basic("user", "password")
                .contentType("application/json")
                .when()
                .get("http://localhost:8082/books")
                .then()
                .statusCode(200)
                .extract().response();

        // Validate pagination and books data
        response.then().body("page", equalTo(1))
                .body("limit", equalTo(10))
                .body("total", equalTo(50))
                .body("users", hasSize(greaterThanOrEqualTo(2))); // At least two books

        // Optionally, validate the first book's details
        response.then().body("users[0].name", equalTo("John Doe"))
                .body("users[0].email", equalTo("john.doe@example.com"));
    }


}