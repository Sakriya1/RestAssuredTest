package com.rest.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Negative {
    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080"; // Changed port to 8080
    }

    // Positive Test Cases

    /**
     * Test to verify that GET /books returns a list of books.
     * Expected to PASS.
     */
    @Test
    public void testGetBooks() {
        given().auth().basic("user", "password")
                .contentType("application/json")
                .when().get("/books")
                .then().log().all() // Logs the response
                .statusCode(200)
                .body("page", greaterThanOrEqualTo(1))
                .body("limit", greaterThanOrEqualTo(1))
                .body("total", greaterThan(0))
                .body("users", not(empty()));
    }

    /**
     * Test to create a new book.
     * Expected to PASS.
     */
    @Test
    public void testCreateBook() {
        String requestBody = "{\"name\": \"Test Book\", \"author\": \"Author A\", \"price\": 19.99}";
        given().auth().basic("admin", "password")
                .contentType("application/json")
                .body(requestBody)
                .when().post("/books")
                .then().log().all() // Logs the response
                .statusCode(201)
                .body("name", equalTo("Test Book"))
                .body("author", equalTo("Author A"))
                .body("price", equalTo(19.99f));
    }

    /**
     * Test to retrieve a book by ID.
     * Expected to PASS.
     */
    @Test
    public void testGetBookById() {
        int bookId = 1;
        given().auth().basic("admin", "password")
                .contentType("application/json")
                .when().get("/books/" + bookId)
                .then().log().all() // Logs the response
                .statusCode(200)
                .body("id", equalTo(bookId))
                .body("name", not(empty()))
                .body("author", not(empty()))
                .body("price", greaterThan(0f));
    }

    /**
     * Test to update an existing book.
     * Expected to PASS.
     */
    @Test
    public void testUpdateBook() {
        int bookId = 1;
        String updatedRequestBody = "{\"id\": 1, \"name\": \"Updated Book\", \"author\": \"Author B\", \"price\": 25.99}";
        given().auth().basic("admin", "password")
                .contentType("application/json")
                .body(updatedRequestBody)
                .when().put("/books/" + bookId)
                .then().log().all() // Logs the response
                .statusCode(200)
                .body("name", equalTo("Updated Book"))
                .body("author", equalTo("Author B"))
                .body("price", equalTo(25.99f));
    }

    /**
     * Test to delete a book by ID.
     * Expected to PASS.
     */
    @Test
    public void testDeleteBook() {
        int bookId = 1;
        given().auth().basic("admin", "password")
                .contentType("application/json")
                .when().delete("/books/" + bookId)
                .then().log().all() // Logs the response
                .statusCode(200);

        // Verify deletion
        given().auth().basic("admin", "password")
                .when().get("/books/" + bookId)
                .then().log().all() // Logs the response
                .statusCode(404);
    }

    // Negative Test Cases

    /**
     * Test to retrieve a book with an invalid ID.
     * Expected to FAIL (404 Not Found).
     */
    @Test
    public void testGetBookWithInvalidId() {
        given().auth().basic("admin", "password")
                .when().get("/books/9999")
                .then().log().all() // Logs the response
                .statusCode(404);
    }

    /**
     * Test to create a book without authentication.
     * Expected to FAIL (401 Unauthorized).
     */
    @Test
    public void testCreateBookWithoutAuthentication() {
        String requestBody = "{\"name\": \"Unauthorized Book\", \"author\": \"Unknown\", \"price\": 15.99}";
        given().contentType("application/json")
                .body(requestBody)
                .when().post("/books")
                .then().log().all() // Logs the response
                .statusCode(401);
    }

    /**
     * Test to create a book with missing required fields.
     * Expected to FAIL (400 Bad Request).
     */
    @Test
    public void testCreateBookWithMissingFields() {
        String requestBody = "{\"name\": \"Missing Price Book\", \"author\": \"Unknown\"}";
        given().auth().basic("admin", "password")
                .contentType("application/json")
                .body(requestBody)
                .when().post("/books")
                .then().log().all() // Logs the response
                .statusCode(400);
    }

    /**
     * Test to update a non-existent book.
     * Expected to FAIL (404 Not Found).
     */
    @Test
    public void testUpdateNonExistentBook() {
        int bookId = 9999;
        String requestBody = "{\"name\": \"Non-existent Book\", \"author\": \"Ghost\", \"price\": 9.99}";
        given().auth().basic("admin", "password")
                .contentType("application/json")
                .body(requestBody)
                .when().put("/books/" + bookId)
                .then().log().all() // Logs the response
                .statusCode(404);
    }
}