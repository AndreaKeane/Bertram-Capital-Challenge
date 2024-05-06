package com.andrea.tabs.response;

import com.andrea.tabs.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PersonResponse {

    @Schema(description = "Unique person identifier.", example = "Jeremy")
    String name;

    @Schema(description = "Dollar amount spent by this person.")
    Double hasSpent;

    @Schema(description = "Dollar amount paid by this person.")
    Double hasPaid;

    @Schema(description = "Dollar amount owed by this person. Owes = hasSpent - hasPaid.")
    Double owes;

    public static PersonResponse fromPerson(Person person) {

        return PersonResponse.builder()
                .name(person.getName())
                .hasSpent(person.getBalance().getAmountSpent())
                .hasPaid(person.getBalance().getAmountPaid())
                .owes(person.owes())
                .build();
    }
}
