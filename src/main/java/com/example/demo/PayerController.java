package com.example.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;

@Tag(name = "Payer", description = "APIs to Determine Payer")
@RestController
public class PayerController {

    static Logger log = LoggerFactory.getLogger(PayerController.class);

    @Operation(
            summary = "Get who pays by day count",
            description = "Get who pays on the N-th day of using the specified Tab.")
    @GetMapping(value = "/tab/{tab_id}/day/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static PayerResponse getWhoPays(
            @Parameter(description = "Tab ID") @PathVariable String tab_id,
            @Parameter(description = "Day Number, starting from 1") @PathVariable Integer num) {
        try {
            return TabService.getWhoPays(tab_id, num);
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    @Operation(
            summary = "Get who pays today by date",
            description = "Get who pays for coffee today, assuming coffee has been purchased every day since the start date assigned to the tab.")
    // TODO: Add start date to the response
    // TODO: Assume skip days for some days of the week?
    @GetMapping(value = "/tab/{tab_id}/today", produces = MediaType.APPLICATION_JSON_VALUE)
    public static PayerResponse getWhoPays(@Parameter(description = "Tab ID") @PathVariable String tab_id) {
        try {;
            return TabService.getWhoPays(tab_id);
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    @Operation(
            summary = "Get payer schedule",
            description = "Generate a payment schedule for N-number of days using the specified tab.")
    @GetMapping(value = "/tab/{tab_id}/schedule/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    // TODO: Max number of days on the schedule?
    // TODO: Week day logic?
    public static TabScheduleResponse getSchedule(
            @Parameter(description = "Tab ID") @PathVariable String tab_id,
            @Parameter(description = "Number of Days on the Schedule") @PathVariable Integer num) {
        try {
            return TabService.getSchedule(tab_id, num);
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e); // TODO
        }
    }

}
