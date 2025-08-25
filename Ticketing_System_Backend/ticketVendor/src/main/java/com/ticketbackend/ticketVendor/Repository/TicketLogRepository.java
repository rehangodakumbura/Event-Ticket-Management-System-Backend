package com.ticketbackend.ticketVendor.Repository;

import com.ticketbackend.ticketVendor.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketLogRepository extends JpaRepository<Ticket, Long> {
}