package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Value
@Builder
@Jacksonized
public class TabResponse {

    String id;
    Map<String, Double> items;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    LocalDate startDate;

    public static TabResponse fromTab(Tab tab) {
        // The response object wants only the String value of the person's name, not the full object
        Map<String, Double> items = tab.items().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getName(),
                        e -> e.getValue()
                ));
        return TabResponse.builder()
                .id(tab.id())
                .startDate(tab.startDate())
                .items(items).build();
    }

}
