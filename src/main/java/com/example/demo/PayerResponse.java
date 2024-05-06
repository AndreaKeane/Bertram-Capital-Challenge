package com.example.demo;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.*;

@Value
@Builder
@Jacksonized
public class PayerResponse {
    String name; // Name identifier of the paying person
    Double amount; // Dollar amount this person is responsible for
    List<PersonResponse> details; // Balance details for each person on the tab

    public static List<PersonResponse> generateDetails(Tab tab) {
        List<PersonResponse> details = new ArrayList<>();
        for (Person person : tab.items().keySet()){
            details.add(PersonResponse.fromPerson(person));
        }
        return details;
    }

}
