package com.example.supplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SupplierApplicationTests {

	private final static String HOST = "http://localhost";

	private String uploadSpreadsheetsEndpoint;
	private String processEndpoint;

	@Before
	public void setUp() {
		uploadSpreadsheetsEndpoint = HOST + ":8080/messages";
        processEndpoint = HOST + ":8070/processor/beautification";

	}

	@Autowired
	private RestTemplate restTemplate;

//	@Autowired
//	RestTemplateBuilder restTemplateBuilder;

//	@Before
//  public void postConstruct() {
//        this.restTemplate = restTemplateBuilder.build();
//    }


	@Test
	public void getMessage() throws ExecutionException, InterruptedException {
		Supplier<String> mesageSupplier = () ->
        this.restTemplate.getForObject(uploadSpreadsheetsEndpoint, String.class);

		ExecutorService executorService = Executors.newFixedThreadPool(5);

		CompletableFuture.supplyAsync(mesageSupplier, executorService).
                thenApply(this::process).exceptionally(this::handle).
                thenAccept(this::consume).get();
	}

    private String handle(Throwable throwable) {
        return "overloaded " + throwable.getMessage();
    }

    void consume(String message) {

        this.restTemplate.postForObject(uploadSpreadsheetsEndpoint, message, String.class);

		System.out.println("message= " + message);

	}

	String process( String message) {

        return this.restTemplate.postForObject(processEndpoint, message, String.class);
    }

}
