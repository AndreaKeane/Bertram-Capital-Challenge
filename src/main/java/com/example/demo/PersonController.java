package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

@RestController
@RequestMapping("person")
public class PersonController {

    static Logger log = LoggerFactory.getLogger(PersonController.class);

    /* PERSON CRUD--------------------------------------------------------------------------------------------------- */

    // POST to CREATE a Person
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static PersonResponse postPerson(@RequestBody PersonRequest request) {
        try {
            Person person = PersonService.createPerson(request.toPerson());
            return PersonResponse.fromPerson(person);
        } catch (InstanceAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.toString(), e);
        }
    }

    // PUT to UPDATE a Person
    @PutMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static PersonResponse putPerson(@RequestBody PersonRequest request) {
        try {
            Person person = request.toPerson();
            PersonService.updatePerson(person);
            return PersonResponse.fromPerson(person);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person '%s' does not exist.".formatted(request.name), e);
        }
    }

    // GET to READ a Person
    @GetMapping(path = "/{name}")
    public static PersonResponse getPerson(@PathVariable String name) {
        try {
            Person person = PersonService.readPerson(name);
            return PersonResponse.fromPerson(person);
        } catch (InstanceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person '%s' does not exist.".formatted(name), e);
        }
    }

    // DELETE to DELETE a Person
    @DeleteMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static PersonResponse deletePerson(@PathVariable String name) {
        try {
            Person person = PersonService.deletePerson(name);
            return PersonResponse.fromPerson(person);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person '%s' does not exist.".formatted(name), e);
        }
    }

}
