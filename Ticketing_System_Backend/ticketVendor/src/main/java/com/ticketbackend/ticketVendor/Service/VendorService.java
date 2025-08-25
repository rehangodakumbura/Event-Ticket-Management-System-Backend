package com.ticketbackend.ticketVendor.Service;

import org.springframework.stereotype.Service;

@Service
public class VendorService {
    private final TicketPool ticketPool;

    public VendorService(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void startVendor(String vendorName, int releaseRate) {
        new Thread(() -> {
            while (!ticketPool.allTicketsIssued()) {
                boolean success = ticketPool.addTickets(5, vendorName);
                if (!success) break;

                try {
                    Thread.sleep(releaseRate);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println(vendorName + " finished issuing tickets.");
        }).start();
    }
}
