package com.ticketbackend.ticketVendor.Repository;

import com.ticketbackend.ticketVendor.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
