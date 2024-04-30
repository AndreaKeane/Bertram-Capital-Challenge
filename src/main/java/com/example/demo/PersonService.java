package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PersonService {

    // ASSERTION: The Person.name field is the unique identifier for a person and is treated like an `id`

    static Logger log = LoggerFactory.getLogger(PersonService.class);

    // In-Memory People Storage - Allow CRUD operations for multiple Person objects
    private static final Map<String, Person> PEOPLE = new ConcurrentHashMap<>(8);

    // CREATE a Person instance
    protected static Person createPerson(Person person) throws InstanceAlreadyExistsException {
        Person result = PEOPLE.putIfAbsent(person.getName(), person);
        if (result != null) {
            throw new InstanceAlreadyExistsException("A Person with Name %s already exists.".formatted(person.getName()));
        }
        return person;
    }

    // READ a Person instance
    protected static Person readPerson(String name) throws InstanceNotFoundException {
        Person person = PEOPLE.get(name);
        if (person == null) {
            throw new InstanceNotFoundException("A Person with Name %s was not found.".formatted(name));
        }
        return person;
    }

    // UPDATE a Person instance
    protected static Person updatePerson(Person person) {
        PEOPLE.replace(person.getName(), person);
        return person;
    }

    // DELETE a Person instance
    protected static Person deletePerson(String name) {
        return PEOPLE.remove(name);
    }

    // Visible For Testing - Compromise so we can reset the people map for tests.
    // Prevents running tests in parallel (not a big deal for a small application)
    protected void resetPeopleMap() {
        PEOPLE.clear();
    }
}
