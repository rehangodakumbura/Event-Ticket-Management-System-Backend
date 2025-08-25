package com.ticketbackend.ticketVendor.Service;

import com.ticketbackend.ticketVendor.Entity.EventConfiguration;
import com.ticketbackend.ticketVendor.Repository.EventConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventConfigurationService {

    @Autowired
    private EventConfigurationRepository eventConfigurationRepository;

    public EventConfiguration saveConfiguration(EventConfiguration eventConfiguration) {
        return eventConfigurationRepository.save(eventConfiguration);
    }
}
