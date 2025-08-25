package com.ticketbackend.ticketVendor.Controller;

import com.ticketbackend.ticketVendor.Entity.EventConfiguration;
import com.ticketbackend.ticketVendor.Service.EventConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configurations")
public class EventConfigurationController {

    @Autowired
    private EventConfigurationService eventConfigurationService;

    @PostMapping
    public ResponseEntity<EventConfiguration> saveConfiguration(@RequestBody EventConfiguration eventConfiguration) {
        EventConfiguration savedConfiguration = eventConfigurationService.saveConfiguration(eventConfiguration);
        return ResponseEntity.ok(savedConfiguration);
    }
}
