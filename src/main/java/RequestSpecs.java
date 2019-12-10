import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import static com.jayway.restassured.specification.ProxySpecification.host;
import static org.hamcrest.Matchers.*;

public class RequestSpecs {
    public RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setRelaxedHTTPSValidation()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public ResponseSpecification responseSpecSuccess() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .expectBody(not(containsString("error")))
                .expectBody(containsString("token"))
                .build();
    }

    public ResponseSpecification responseSpecError() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(400)
                .expectBody(containsString("error"))
                .build();
    }
}