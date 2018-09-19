package com.example.supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("messages")
public class MessagesResource {

    @GetMapping
    String getMessage() throws InterruptedException {

        Thread.sleep(1000);

        return "hey duke" + System.currentTimeMillis();
    }

    @Autowired
    ExampleConfig exampleConfig;

    @PostMapping
    public void setMessage(@RequestBody String message) {
        System.out.println( message);
    }

}
