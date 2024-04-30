package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestPersonController {

    @Autowired
	private TestRestTemplate template;

    @Autowired
    private PersonService personService;

    @BeforeEach
    public void setup() {
        personService.resetPeopleMap();
        // TODO: Try @DirtiesContext annotation: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/annotation/D
    }


    /* PERSON SERVICE INTEGRATION TESTS ----------------------------------------------------------------------------- */
    @Test
    public void postPerson() {
        PersonRequest req = new PersonRequest("Andrea");
        ResponseEntity<PersonResponse> res = this.template.postForEntity("/person/", req, PersonResponse.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getName()).isEqualTo("Andrea");
    }

    @Test
    public void postPersonDuplicate() {
        PersonRequest req = new PersonRequest("Andrea");
        ResponseEntity<PersonResponse> res1 = this.template.postForEntity("/person/", req, PersonResponse.class);
        assertThat(res1.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<PersonResponse> res2 = this.template.postForEntity("/person/", req, PersonResponse.class);
        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Disabled
    @Test
    public void putPerson() {
        // Create a Person
        PersonRequest req1 = new PersonRequest("Andrea");
        ResponseEntity<PersonResponse> postRes = this.template.postForEntity("/person/", req1, PersonResponse.class);
        assertThat(postRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Update the Person
        // TODO: Update Person doesn't make sense unless we add additional mutable fields

        // Read and Verify the Update operation
        ResponseEntity<PersonResponse> res2 = this.template.getForEntity("/person/Andrea", PersonResponse.class);
        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Assert on the updated data
    }

    @Test
    public void getPerson() {
        // Create a Person
        PersonRequest req = new PersonRequest("Andrea");
        ResponseEntity<PersonResponse> postRes = this.template.postForEntity("/person/", req, PersonResponse.class);
        assertThat(postRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Read the Tab
        ResponseEntity<PersonResponse> getRes = this.template.getForEntity("/person/Andrea", PersonResponse.class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRes.getBody().getName()).isEqualTo("Andrea");
        assertThat(getRes.getBody().getHasSpent()).isEqualTo(0);
        assertThat(getRes.getBody().getHasPaid()).isEqualTo(0);
    }

    @Test
    public void deletePerson() {
        // Create a Person
        PersonRequest req = new PersonRequest("Andrea");
        ResponseEntity<PersonResponse> postRes = this.template.postForEntity("/person/", req, PersonResponse.class);
        assertThat(postRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Delete the Person
        this.template.delete("/person/Andrea");

        // Read the Person
        ResponseEntity<PersonResponse> getRes = this.template.getForEntity("/person/Andrea", PersonResponse.class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


}
