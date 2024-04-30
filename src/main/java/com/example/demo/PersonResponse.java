package com.example.demo;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PersonResponse {
    String name;
    Double hasSpent;
    Double hasPaid;
    Double owes;

    protected static PersonResponse fromPerson(Person person) {

        return PersonResponse.builder()
                .name(person.getName())
                .hasSpent(person.getBalance().getAmountSpent())
                .hasPaid(person.getBalance().getAmountPaid())
                .owes(person.owes())
                .build();
    }
}
