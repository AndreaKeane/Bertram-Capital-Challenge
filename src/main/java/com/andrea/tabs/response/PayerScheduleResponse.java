package com.andrea.tabs.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder
@Jacksonized
public class PayerScheduleResponse {

    @Schema(description = "Payment schedule", example = "{\"1\": \"Greg\", \"2\": \"Bob\", \"3\": \"Karleen\", ... }")
    Map<Integer, String> schedule;
}
