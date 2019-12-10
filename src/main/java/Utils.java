import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import io.qameta.allure.Step;
import org.aeonbits.owner.ConfigFactory;
import java.io.*;

import static com.jayway.restassured.RestAssured.given;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class Utils {
    private SimpleConfig config = ConfigFactory.create(SimpleConfig.class);

    private RequestSpecification requestSpecs = new RequestSpecs().requestSpec();
    private ResponseSpecification responseSpecsSuccess = new RequestSpecs().responseSpecSuccess();
    private ResponseSpecification responseSpecsError = new RequestSpecs().responseSpecError();

    @Step("Авторизоваться валидным пользователем {user.email}")
    public Response loginSuccess(User user){
        Response response = given()
                .spec(requestSpecs)
                .relaxedHTTPSValidation()
                .header("Content-Type", ContentType.JSON)
                .baseUri(config.loginUrl())
                .body(user)
                .post().then().spec(responseSpecsSuccess).extract().response();
        return response;
    }

    @Step("Авторизоваться невалидным пользователем {user.email}")
    public Response loginWithError(User user){
        Response response = given()
                .spec(requestSpecs)
                .relaxedHTTPSValidation()
                .header("Content-Type", ContentType.JSON)
                .baseUri(config.loginUrl())
                .body(user)
                .post().then().spec(responseSpecsError).extract().response();
        return response;
    }

    @Step("Получить АПИ токен")
    public String getTokenFromResponse(Response response){
        return response.jsonPath().getString("token");
    }

    @Step("Получить данные о авторизованном пользователе")
    public Response getAutorizedUser(String token){
        Response response = given()
                .spec(requestSpecs)
                .relaxedHTTPSValidation()
                .header("Content-Type", ContentType.JSON)
                .baseUri(config.userUrl())
                .queryParam("jwt", token)
                .get();
        return response;
    }

    @Step("Получить набор валидных данных userInfo")
    public UserInfo getValidUserInfo(){
        UserInfo expectedUser = new UserInfo();
        ObjectMapper mapper = new ObjectMapper();
        try {
            expectedUser = mapper.readValue(new File("src/main/resources/user_valid.json"), UserInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expectedUser;
    }

    @Step("Проверить ответ на наличие ошибки \"{expectedError}\"при авторизации")
    public void verifyResponseErrorMessage(String response, String expectedError){
        assertThat(response,containsString(expectedError));
    }

    @Step("Проверить ответ на наличие токена")
    public void verifyResponseToken(String response){
        assertThat(response, containsString("token"));
    }

    @Step("Сверить данные о авторизованном пользователе с ожидаемыми")
    public void verifyExpectedUserInfo(UserInfo actual, UserInfo expected){
        assertThat(actual, sameBeanAs(expected).ignoring("lastLogin"));
    }

    @Step("Сверить полученные данные с JSON схемой")
    public void verifyJsonSchema(Response response, String schemaPath){
        String user = response.body().asString();
        assertThat(user, matchesJsonSchemaInClasspath(schemaPath));
    }
}