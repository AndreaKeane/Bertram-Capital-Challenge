package com.andrea.tabs.controller;

import com.andrea.tabs.model.Tab;
import com.andrea.tabs.request.TabRequest;
import com.andrea.tabs.response.TabResponse;
import com.andrea.tabs.service.TabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;

@Tag(name = "Tab", description = "Tab CRUD APIs")
@RestController
public class TabController {

    static Logger log = LoggerFactory.getLogger(TabController.class);

    /* TABS CRUD ---------------------------------------------------------------------------------------------------- */

    @Operation(
            summary = "Create a Tab",
            description = "Create a new Tab. Requires a unique Tab ID and a non-empty list of tab items. " +
                    "The Tab Start Date (optional) will default to the date of creation unless specified.")
    @PostMapping(value = "/tab", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse postTab(@Parameter @RequestBody TabRequest request) {
        try {
            Tab tab = TabService.createTab(request.toTab());
            return TabResponse.fromTab(tab);
        } catch (InstanceAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.toString(), e);
        } catch (OutOfMemoryError e) {
            throw new ResponseStatusException(HttpStatus.INSUFFICIENT_STORAGE, e.toString(), e);
        } catch (InvalidAttributeValueException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString(), e);
        }
    }

    @Operation(summary = "Update a Tab")
    @PutMapping(path = "/tab", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse putTab(@Parameter @RequestBody TabRequest tabRequest) {
        try {
            Tab tab = tabRequest.toTab();
            TabService.updateTab(tab);
            return TabResponse.fromTab(tab);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID '%s' does not exist.".formatted(tabRequest.getId()), e);
        } catch (InvalidAttributeValueException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString(), e);
        }
    }

    @Operation(summary = "Get a Tab")
    @GetMapping(path = "/tab/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse getTab(@Parameter(description = "Tab ID") @PathVariable String id) {
        try {
            Tab tab = TabService.readTab(id);
            return TabResponse.fromTab(tab);
        } catch (InstanceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID '%s' does not exist.".formatted(id), e);
        }
    }

    @Operation(summary = "Delete a Tab")
    @DeleteMapping(path = "/tab/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse deleteTab(@Parameter(description = "Tab ID") @PathVariable String id) {
        try {
            Tab tab = TabService.deleteTab(id);
            return TabResponse.fromTab(tab);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID '%s' does not exist.".formatted(id), e);
        }
    }
}
