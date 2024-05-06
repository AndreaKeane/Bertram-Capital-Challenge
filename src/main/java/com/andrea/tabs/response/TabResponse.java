package com.andrea.tabs.response;

import com.andrea.tabs.model.Tab;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Tab ID. Unique user-defined identifier.", example = "Demo")
    String id;

    @Schema(description = "Tab Items. User-defined map of <Name: Cost> entries.",
            example = "{\"Bob\": 2.50, \"Jeremy\": 3.75, \"Andrea\": 3.25}")
    Map<String, Double> items;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    @Schema(description = "Tab Start Date <MM-dd-yyyy>. First date the tab will be used, defaults to today.", example = "01-01-2024")
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
