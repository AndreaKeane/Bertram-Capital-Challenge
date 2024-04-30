package com.example.demo;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PersonRequest {
    String name;

    protected Person toPerson() {
        return new Person(name);
    }
}
