package com.test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;

@Test
public class MainTest {

    RequestSpecification requestSpecification;
    Response response;
    ValidatableResponse validatableResponse;
    private int[] books;

    //**1**//
    /*@Test
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

        //Validate status code
        validatableResponse.statusCode(200);

        // Validate status line
        validatableResponse.statusLine("HTTP/1.1 200 ");
    }*/




    //**2**//
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







    //**3**//
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

    //**4**//
/*@Test
    public void testDeleteBook() {
        int bookId = 5;

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
    }*/

    //**5//
    @Test
    public void testGetBookById() {
        int bookId = 1;

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .when()
                .get("http://localhost:8080/books/" + bookId)
                .then()
                .log().all()  // Log response details for debugging
                .extract().response();

        int statusCode = response.getStatusCode();
        System.out.println("Response Status Code: " + statusCode);
        System.out.println("Response Body: " + response.asString());

    }

    //**6**//
    @Test
    public void testGetBooksSortedByName() {
        Response response = given()
                .auth().basic("user", "password")
                .queryParam("sort", "name")
                .queryParam("order", "asc")
                .when()
                .get("http://localhost:8080/books")
                .then()
                .statusCode(200)
                .body("books.size()", greaterThan(1)) // Ensure there are at least 2 books to compare
                .extract().response();
    }
    //**7**//
    public void testGetBooksByAuthor() {
        Response response = given()
                .auth().basic("user", "password")
                .queryParam("author", "Santideva")
                .when()
                .get("http://localhost:8080/books")
                .then()
                .statusCode(200)
                .body("books.size()", greaterThan(0)) // Ensure books exist
                .extract().response();

    }
    //**8**//
   /* @Test
    public void testBookPriceGreaterThanZero() {
        int bookId = 5;

        given()
                .auth().basic("user", "password")
                .when()
                .get("http://localhost:8080/books/" + bookId)
                .then()
                .statusCode(200)
                .body("price", greaterThan(0.0f));
    }*/

    //**9**//
    @Test
    public void testGetBooksByPriceRange() {
        // Define the price range
        double minPrice = 20.0;
        double maxPrice = 50.0;

        // Send the request with the price range as query parameters
        given()
                .auth().basic("user", "password")
                .queryParam("minPrice", minPrice)
                .queryParam("maxPrice", maxPrice)
                .when()
                .get("http://localhost:8080/books")
                .then()
                .statusCode(200)
                .body("books.size()", greaterThan(0)) // Ensure there are books returned
                .body("books.price", everyItem(greaterThanOrEqualTo((float) minPrice))) // Validate that all books have price >= minPrice
                .body("books.price", everyItem(lessThanOrEqualTo((float) maxPrice))); // Validate that all books have price <= maxPrice
    }
    //**10*//
    public void testGetBooksWithPagination() {
        given()
                .auth().basic("user", "password")
                .queryParam("page", 1)  // Specify the page number
                .queryParam("limit", 10) // Specify the number of books per page
                .when()
                .get("http://localhost:8080/books")
                .then()
                .statusCode(200);
    }
    //**11**//
    @Test
    public void testBookNotFound() {
        given().auth().basic("user", "password")
                .when().get("http://localhost:8080/books/9999")
                .then().statusCode(404);
    }

    //**12**//
    @Test
    public void testCreateBookWithMissingName() {
        String requestBody = "{ \"author\": \"Erich Gamma\", \"price\": 55.00 }";
        given().auth().basic("admin", "password").contentType("application/json").body(requestBody)
                .when().post("http://localhost:8080/books")
                .then().statusCode(400);  // Expecting a bad request error
    }
    //**13**//
    @Test
    public void testCreateBookWithInvalidPrice() {
        String requestBody = "{ \"name\": \"Patterns of Enterprise Application Architecture\", \"author\": \"Martin Fowler\", \"price\": -10.00 }";
        given().auth().basic("admin", "password").contentType("application/json").body(requestBody)
                .when().post("http://localhost:8080/books")
                .then().statusCode(400);  // Invalid price should return 400
    }
    //**14**//
    @Test
    public void testGetBooksCount() {
        given().auth().basic("user", "password")
                .when().get("http://localhost:8080/books")
                .then().statusCode(200).body("books.size()", greaterThan(0));
    }
    //**15**//
    /*@Test
    public void testBookPriceAfterDeletion() {
        given().auth().basic("admin", "password").when().delete("http://localhost:8080/books/4")
                .then().statusCode(200);
        given().auth().basic("user", "password").when().get("http://localhost:8080/books/4")
                .then().statusCode(404);
    }*/
    //**16**//
    /*@Test
    public void testCreateBookWithZeroPrice() {
        given()
                .auth().basic("user", "password")
                .contentType(ContentType.JSON)
                .body("{ \"name\": \"Test Book\", \"author\": \"Test Author\", \"price\": 0.0 }")
                .when()
                .post("/books")
                .then()
                .statusCode(400);  // Expect a 400 Bad Request
    }*/

    //**17**//
    @Test
    public void testGetAllBooks() {
        given().auth().basic("user", "password")
                .when().get("http://localhost:8080/books")
                .then().statusCode(200)
                .body("books", not(empty())); // Ensure books list is not empty
    }


}
