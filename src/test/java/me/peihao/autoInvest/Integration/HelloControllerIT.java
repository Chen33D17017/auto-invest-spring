package me.peihao.autoInvest.Integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerIT {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    public void getHello(){
        ResponseEntity<String> response = testRestTemplate.getForEntity("/", String.class);
        assertThat(response.getBody()).isEqualTo("Hello World TEST");
    }

    @Test
    public void getHealth() throws JSONException {
        ResponseEntity<String> responseEntity = testRestTemplate.exchange("/actuator/health",
                HttpMethod.GET, HttpEntity.EMPTY, String.class);
        // https://www.baeldung.com/jsonassert
        JSONAssert.assertEquals("{status:\"UP\"}", responseEntity.getBody(), JSONCompareMode.LENIENT);
    }
}
