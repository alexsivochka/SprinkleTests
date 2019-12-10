import com.jayway.restassured.response.Response;
import io.qameta.allure.Feature;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.Test;

/*
* Обнаружены следующие несоответствия к заданию.
* 1. Токен, согласно заданию должен содержать email
* пользователя (по факту не содержит)
* 2. Поле Id ответа согласно заданию должно быть типа int (в ответе
* приходит String)
* */

/**
 * Запуск тестов - maven - фазы clean test
 * После выполнения тестов можно открыть отчет Allure
 * для этого во вкладке Maven - Plugins - выполнить allure:serve
 * Возможно, необходимо будет установить плагин для Lombok
 */


@Feature(value = "Sprinkle Tests")
public class TestRunner {
    private SimpleConfig config = ConfigFactory.create(SimpleConfig.class);
    private Utils utils = new Utils();
    private User userValid = new User(config.email(), config.password());
    private User userNotValid = new User("non-valid-user@gmail.com", config.password());
    private String token;

    @Test(priority = 1, description = "Негативный сценарий авторизации")
    public void negativeLoginTest(){
        Response apiResponse = utils.loginWithError(userNotValid);
        utils.verifyResponseErrorMessage(apiResponse.asString(), "Invalid E-mail or Password");
    }

    @Test(priority = 2, description = "Позитивный сценарий авторизации")
    public void positiveLoginTest(){
        Response apiResponse = utils.loginSuccess(userValid);
        utils.verifyResponseToken(apiResponse.asString());
        token = utils.getTokenFromResponse(apiResponse);
    }

    @Test(priority = 3, dependsOnMethods = "positiveLoginTest", description = "Валидация JSON схемы userInfo")
    public void schemaValidationTest(){
        Response autorizedUser = utils.getAutorizedUser(token);
        utils.verifyJsonSchema(autorizedUser, "schema.json");
    }

    @Test(priority = 4, dependsOnMethods = "positiveLoginTest", description = "Проверка полученной информации userInfo")
    public void userInfoDataTest(){
        Response autorizedUser = utils.getAutorizedUser(token);
        UserInfo actualUserInfo = autorizedUser.as(UserInfo.class);
        UserInfo expectedUserInfo = utils.getValidUserInfo();
        utils.verifyExpectedUserInfo(actualUserInfo, expectedUserInfo);
    }
}