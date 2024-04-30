package com.example.demo;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder
@Jacksonized
public class TabScheduleResponse {
    Map<Integer, String> schedule;
}
