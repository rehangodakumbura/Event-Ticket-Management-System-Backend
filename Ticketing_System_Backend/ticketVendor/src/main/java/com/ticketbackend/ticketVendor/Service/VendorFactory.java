package com.ticketbackend.ticketVendor.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VendorFactory {

    private final VendorService vendorService;
    private final List<Thread> vendorThreads = new ArrayList<>();

    @Autowired
    public VendorFactory(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void startVendors() {
        Thread vendor1 = new Thread(() -> vendorService.startVendor("Vendor-1", 500), "Vendor-1");
        Thread vendor2 = new Thread(() -> vendorService.startVendor("Vendor-2", 500), "Vendor-2");

        vendorThreads.add(vendor1);
        vendorThreads.add(vendor2);

        vendor1.start();
        vendor2.start();
    }

    public void stopVendors() {
        for (Thread thread : vendorThreads) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        vendorThreads.clear();
        System.out.println("All vendor threads stopped.");
    }


    public boolean areVendorsStopped() {
        return vendorThreads.stream().noneMatch(Thread::isAlive);
    }

    public void reset() {
        stopVendors();
        System.out.println("VendorFactory has been reset.");
    }

}
