package com.ticketbackend.ticketVendor.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_configuration")
public class EventConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_tickets", nullable = false)
    private int totalTickets;

    @Column(name = "ticket_release_rate", nullable = false)
    private int ticketReleaseRate;

    @Column(name = "customer_retrieval_rate", nullable = false)
    private int customerRetrievalRate;

    @Column(name = "max_ticket_capacity", nullable = false)
    private int maxTicketCapacity;

}
