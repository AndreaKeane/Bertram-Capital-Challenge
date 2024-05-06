package com.andrea.tabs.request;

import com.andrea.tabs.model.Person;
import com.andrea.tabs.model.Tab;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Value
@Builder
@Jacksonized
@Schema(description = "Tab JSON Object")
public class TabRequest {

    // TODO: The Swagger docs display the wrong date format, despite the use os @Schema(... example)
    //  Open issue: https://github.com/swagger-api/swagger-ui/issues/5744

    @Schema(description = "Tab ID. Unique user-defined identifier.", example = "Demo")
    @NotBlank(message = "Tab ID is required.")
    String id;

    @Schema(description = "Tab Start Date <MM-dd-yyyy>. First date the tab will be used, defaults to today.", example = "01-01-2024", format="MM-dd-yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    LocalDate startDate;

    @Schema(description = "Tab Items. User-defined map of <Name: Cost> entries.",
            example = "{\"Bob\": 2.50, \"Jeremy\": 3.75, \"Andrea\": 3.25}")
    @NotBlank(message = "Items JSON is required")
    Map<@NotBlank String, @NotNull @Size(min = 0, max = 100) Double> items;


    public Tab toTab() throws InvalidAttributeValueException {
        if (id == null || id.isBlank()) { throw new InvalidAttributeValueException("Tab ID is required."); } // Remove this after figuring out Hibernate validation
        Map<Person, Double> peoplizedItems = buildPeople();
        return Tab.builder()
                .id(this.id)
                .startDate(Optional.ofNullable(startDate).orElse(LocalDate.now()))
                .items(peoplizedItems)
                .build();
    }

    private Map<Person, Double> buildPeople() {
        Map<Person, Double> peopleItems = new HashMap<>();
        for (Map.Entry<String, Double> item : this.items.entrySet()) {
            String personName = item.getKey();
            Person p = new Person(personName);
            peopleItems.put(p, item.getValue());
        }
        return peopleItems;
    }

    // Generate a Tab object from a JSON file, instead of through a Post request
    public static TabRequest tabFromJson(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper.readValue(new File(filename), TabRequest.class);
    }

    // Testing Helper to reduce boilerplate code
    public static Map<String, Double> testItems() {
        Map<String, Double> items = new HashMap<>(4);
        items.put("personA", 1.00);
        items.put("personB", 2.00);
        items.put("personC", 3.00);
        return items;
    }

}
