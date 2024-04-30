package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Value
@Builder
@Jacksonized
public class TabRequest {

    String id;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    LocalDate startDate;
    Map<String, Double> items;

    public Tab toTab() {
        Map<Person, Double> peoplizedItems = buildPeople();
        return Tab.builder()
                .id(this.id)
                .startDate(Optional.ofNullable(startDate).orElse(LocalDate.now()))
                .items(peoplizedItems)
                .build();
    }

    private Map<Person, Double> buildPeople() {
        Map<Person, Double> items = new HashMap<>();
        for (Map.Entry<String, Double> item : this.items.entrySet()) {
            Person p = new Person(item.getKey());

            items.put(p, item.getValue());
        }
        return items;
    }

    // Generate a Tab object from a JSON file, instead of through a Post request
    public static TabRequest tabFromJson(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper.readValue(new File(filename), TabRequest.class);
    }

}
