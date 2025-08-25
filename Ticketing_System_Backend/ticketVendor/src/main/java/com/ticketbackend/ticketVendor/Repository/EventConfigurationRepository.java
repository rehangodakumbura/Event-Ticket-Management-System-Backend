package com.ticketbackend.ticketVendor.Repository;

import com.ticketbackend.ticketVendor.Entity.EventConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventConfigurationRepository extends JpaRepository<EventConfiguration, Long> {
    EventConfiguration findFirstByOrderByIdDesc();
}
