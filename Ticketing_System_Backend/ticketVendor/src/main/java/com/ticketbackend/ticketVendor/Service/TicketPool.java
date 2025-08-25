package com.ticketbackend.ticketVendor.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import com.ticketbackend.ticketVendor.Entity.EventConfiguration;
import com.ticketbackend.ticketVendor.Repository.EventConfigurationRepository;


@Service
public class TicketPool {
    private final Queue<Integer> ticketQueue = new LinkedList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int issuedTickets = 0;
    private int totalTickets;
    private int maxCapacity;

    private final EventConfigurationRepository eventConfigurationRepository;
    private Sinks.Many<String> logSink = Sinks.many().multicast().onBackpressureBuffer();

    public TicketPool(EventConfigurationRepository eventConfigurationRepository) {
        this.eventConfigurationRepository = eventConfigurationRepository;
    }

    @PostConstruct
    public void initializeFromDatabase() {
        EventConfiguration config = eventConfigurationRepository.findFirstByOrderByIdDesc();
        if (config != null) {
            this.totalTickets = config.getTotalTickets();
            this.maxCapacity = config.getMaxTicketCapacity();
            System.out.println("Initialized TicketPool with values from database: " +
                    "Total Tickets: " + totalTickets + ", Max Capacity: " + maxCapacity);
        } else {
            throw new IllegalStateException("No EventConfiguration found in the database!");
        }
    }

    public synchronized boolean addTickets(int count, String vendorName) {
        if (issuedTickets >= totalTickets) {
            return false;
        }

        int ticketsToAdd = Math.min(count, Math.min(maxCapacity - ticketQueue.size(), totalTickets - issuedTickets));
        for (int i = 0; i < ticketsToAdd; i++) {
            ticketQueue.add(++issuedTickets);
        }

        String logEntry = LocalDateTime.now().format(formatter) + " - " + vendorName + " added " + ticketsToAdd + " tickets. Current pool size: " + ticketQueue.size();
        logSink.tryEmitNext(logEntry);

        notifyAll();
        return true;
    }

    public synchronized boolean removeTicket(String customerName) {
        while (ticketQueue.isEmpty()) {
            if (issuedTickets >= totalTickets) {
                return false;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        Integer ticket = ticketQueue.poll();
        String logEntry = LocalDateTime.now().format(formatter) + " - " + customerName + " purchased Ticket-" + ticket + ". Remaining pool size: " + ticketQueue.size();
        logSink.tryEmitNext(logEntry);

        notifyAll();
        return true;
    }

    public synchronized boolean allTicketsIssued() {
        return issuedTickets >= totalTickets;
    }

    public Flux<String> getLogs() {
        return logSink.asFlux()
                .doOnCancel(() -> System.out.println("Stopping log emission due to client disconnection."))
                .doFinally(signalType -> System.out.println("Log stream finalized: " + signalType));
    }

    public synchronized void reset() {
        // Clear ticket-related state
        issuedTickets = 0;
        ticketQueue.clear();

        // Complete the current log stream
        logSink.tryEmitComplete();
        System.out.println("Previous log stream completed.");

        // Reinitialize logSink for the next simulation
        logSink = Sinks.many().multicast().onBackpressureBuffer();
        System.out.println("Log sink reinitialized for the next simulation.");

        // Re-fetch the configuration for the new process
        EventConfiguration config = eventConfigurationRepository.findFirstByOrderByIdDesc();
        if (config != null) {
            this.totalTickets = config.getTotalTickets();
            this.maxCapacity = config.getMaxTicketCapacity();
            System.out.println("Reinitialized TicketPool with values from database: " +
                    "Total Tickets: " + totalTickets + ", Max Capacity: " + maxCapacity);
        } else {
            throw new IllegalStateException("No EventConfiguration found in the database!");
        }

        System.out.println("TicketPool has been reset.");
    }


//    public synchronized void reset() {
//        issuedTickets = 0;
//        ticketQueue.clear();
//        logSink.tryEmitComplete(); // Complete the current log stream
//        System.out.println("TicketPool has been reset.");
//    }
}
