package com.ticketbackend.ticketVendor.Repository;

import com.ticketbackend.ticketVendor.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}