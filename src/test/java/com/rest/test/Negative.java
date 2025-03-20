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



    // Passed//


    //**1**//

    @Test
    public void testUnauthorizedAccess() {
        given().auth().basic("invalidUser", "invalidPassword")
                .when().get("http://localhost:8080/books")
                .then().statusCode(401); // Unauthorized
    }




     //**2**//

    @Test
    public void testGetBookByInvalidId() {
        int invalidBookId = 9999; // Non-existent book ID
        given().auth().basic("user", "password")
                .when().get("http://localhost:8080/books/" + invalidBookId)
                .then().statusCode(404); // Not Found
    }


//**3**//

    @Test
    public void testCreateBookMissingName() {
        String requestBody = "{ \"author\": \"Test Author\", \"price\": 10.00 }"; // Missing 'name' field
        given().auth().basic("admin", "password")
                .contentType("application/json")
                .body(requestBody)
                .when().post("http://localhost:8080/books")
                .then().statusCode(400); // Bad Request
    }



    //Failed//

//**1**//
    /**
     * Test to create a new book.
     * Expected to FAIL.
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



    //**2**//

    /**
     * Test to retrieve a book by ID.
     * Expected to .
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


    //**3**//

    /**
     * Test to update an existing book.
     * Expected to FAIL.
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


    //**4**//
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


    //**5**//
    /**
     * Test to verify that GET /books returns a list of books.
     * Expected to FAIL.
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


}