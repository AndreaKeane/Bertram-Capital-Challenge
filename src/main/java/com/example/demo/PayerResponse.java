package com.example.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@Jacksonized
public class PayerResponse {

    @Schema(description = "Name of the person responsible for payment.", example = "Jeremy")
    String name;

    @Schema(description = "Dollar amount the payer is responsible for.", example = "10.00")
    Double amount;

    @Schema(
            description = "Balance details for each person on the tab.",
            example = "[" +
                    "{ \"name\": \"Kirsten\", \"hasSpent\": 37.5, \"hasPaid\": 26.25, \"owes\": 11}," +
                    "{ \"name\": \"Greg\", \"hasSpent\": 45, \"hasPaid\": 52.5, \"owes\": -7}," +
                    "{ \"name\": \"Bob\", \"hasSpent\": 42.5, \"hasPaid\": 52.5, \"owes\": -10}]")
    List<PersonResponse> details; // Balance details for each person on the tab

    public static List<PersonResponse> generateDetails(Tab tab) {
        List<PersonResponse> details = new ArrayList<>();
        for (Person person : tab.items().keySet()) {
            details.add(PersonResponse.fromPerson(person));
        }
        return details;
    }

}
