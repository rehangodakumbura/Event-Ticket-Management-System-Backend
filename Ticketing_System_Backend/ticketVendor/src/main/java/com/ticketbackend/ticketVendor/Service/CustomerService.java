package com.ticketbackend.ticketVendor.Service;

import com.ticketbackend.ticketVendor.Entity.EventConfiguration;
import com.ticketbackend.ticketVendor.Repository.EventConfigurationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final TicketPool ticketPool;
    private final EventConfigurationRepository eventConfigurationRepository;
    private final List<Thread> customerThreads = new ArrayList<>();

    public CustomerService(TicketPool ticketPool, EventConfigurationRepository eventConfigurationRepository) {
        this.ticketPool = ticketPool;
        this.eventConfigurationRepository = eventConfigurationRepository;
    }

    public void startCustomer(String customerName) {
        // Fetch customerRetrievalRate from the database
        EventConfiguration config = eventConfigurationRepository.findFirstByOrderByIdDesc();
        if (config == null) {
            throw new IllegalStateException("No EventConfiguration found in the database!");
        }

        int retrievalRate = config.getCustomerRetrievalRate();

        Thread customerThread = new Thread(() -> {
            while (true) {
                boolean success = ticketPool.removeTicket(customerName);
                if (!success) break;

                try {
                    Thread.sleep(retrievalRate);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println(customerName + " finished purchasing tickets.");
        }, customerName);

        customerThreads.add(customerThread);
        customerThread.start();
    }

    public void stopCustomers() {
        for (Thread thread : customerThreads) {
            if (thread.isAlive()) {
                thread.interrupt(); // Gracefully interrupt the thread
            }
        }
        customerThreads.clear(); // Clear the list after stopping threads
    }

    public boolean areCustomersStopped() {
        return customerThreads.stream().noneMatch(Thread::isAlive);
    }

    public void reset() {
        stopCustomers(); // Ensure all customer threads are stopped
        customerThreads.clear(); // Clear the thread list
        System.out.println("CustomerService has been reset.");
    }


}
