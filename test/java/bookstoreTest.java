import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class bookstoreTest {

    @Test

    public class testGetBooks {

        //1.
        @Test
        public void testGetBooks() {
            RestAssured.baseURI = "https://bookstore.toolsqa.com";
            Response response = RestAssured
                    .given()
                    .when()
                    .get("/Bookstore/v1/Books");

            Assert.assertEquals(200, response.getStatusCode(), "Expected status code: 200");

            String responseBody = response.getBody().asString();

            String publisher = extractFieldValue(responseBody, "publisher");
            String author = extractFieldValue(responseBody, "author");


            Assert.assertEquals("O'Reilly Media", publisher, "Expected publisher: O'Reilly Media");
            Assert.assertEquals("Richard E. Silverman", author, "Expected author: Richard E. Silverman");

        }

        private String extractFieldValue(String responseBody, String fieldName) {
            String fieldPrefix = "\"" + fieldName + "\":\"";
            int startIndex = responseBody.indexOf(fieldPrefix) + fieldPrefix.length();
            int endIndex = responseBody.indexOf("\"", startIndex);
            return responseBody.substring(startIndex, endIndex);

        }
    }

    @Test
    public void testCreateUser () {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        JSONObject requestBody = new JSONObject();


        requestBody.put("userName", "user123");
        requestBody.put("password", "Password123!");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .get("/Account/v1/User")
                .then()
                .extract().response();


        int statuscode = response.getStatusCode();

        Assert.assertEquals(statuscode, 200, "Expected code 200");

        System.out.println("Response Body: " + response.getBody().asString());







    }

    @Test
    public void testUnacceptablePassword () {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        JSONObject requestBody = new JSONObject();
        requestBody.put("userName", "username");
        requestBody.put("password", "password");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post("/Account/v1/User")
                .then()
                .extract().response();


        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 400, "Expected status code: 400");

        String expectedMessage = "Passwords must have at least one non alphanumeric character, one digit ('0'-'9'), one uppercase ('A'-'Z'), one lowercase ('a'-'z'), one special character and Password must be eight characters or longer.";
        String actualMessage = response.jsonPath().getString("message");

        Assert.assertEquals(actualMessage, expectedMessage, "Expected password error message did not match.");


    }
}



