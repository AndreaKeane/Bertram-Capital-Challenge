package com.andrea.tabs.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainControllerTests {

    @Autowired
    private TestRestTemplate template;

    @Test
    public void getRoot() {
        // Our root doesn't do much, so this is a mostly canary for application startup success
        ResponseEntity<String> response = this.template.getForEntity("/", String.class);
        assertThat(response.getBody()).isEqualTo("Welcome to the Who Buys Coffee App!");
    }
}
