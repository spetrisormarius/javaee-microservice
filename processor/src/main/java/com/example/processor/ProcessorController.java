package com.example.processor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("processor")
public class ProcessorController {


    @PostMapping(path = "/beautification")
    public String processor( @RequestBody String input) throws InterruptedException {

        Thread.sleep(1000);
        return  " + " + input + " + ";
    }
}
