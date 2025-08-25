package com.ticketbackend.ticketVendor.Service;

import org.springframework.stereotype.Service;

@Service
public class SimulationService {

    private final TicketPool ticketPool;
    private final CustomerService customerService;
    private final VendorFactory vendorFactory;

    public SimulationService(TicketPool ticketPool, CustomerService customerService, VendorFactory vendorFactory) {
        this.ticketPool = ticketPool;
        this.customerService = customerService;
        this.vendorFactory = vendorFactory;
    }

    public void reset() {
        // Stop and clear customer threads
        customerService.reset();

        // Stop and clear vendor threads
        vendorFactory.stopVendors();
        while (!vendorFactory.areVendorsStopped()) {
            sleep(100); // Ensure all vendor threads are stopped
        }
        System.out.println("All vendor threads stopped.");

        // Reset the ticket pool
        ticketPool.reset();

        // Log reset completion
        System.out.println("Simulation has been fully reset.");
    }



//    public void reset() {
//        customerService.stopCustomers();
//        vendorFactory.stopVendors();
//        ticketPool.reset(); // Reset ticket pool after stopping threads
//        System.out.println("Simulation has been fully reset.");
//    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Reset process interrupted.");
        }
    }

}

