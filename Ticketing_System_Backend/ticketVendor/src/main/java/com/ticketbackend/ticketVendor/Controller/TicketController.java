package com.ticketbackend.ticketVendor.Controller;

import com.ticketbackend.ticketVendor.Service.CustomerService;
import com.ticketbackend.ticketVendor.Service.SimulationService;
import com.ticketbackend.ticketVendor.Service.TicketPool;
import com.ticketbackend.ticketVendor.Service.VendorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api")
public class TicketController {

    @Autowired
    private VendorFactory vendorFactory;

    @Autowired
    private CustomerService customerService;

    private final TicketPool ticketPool;
    private final SimulationService simulationService;

    @Autowired
    public TicketController(TicketPool ticketPool, SimulationService simulationService) {
        this.ticketPool = ticketPool;
        this.simulationService = simulationService;
    }

    @GetMapping("/tickets/start")
    public String startSimulation() {
        if (!vendorFactory.areVendorsStopped() || !customerService.areCustomersStopped()) {
            return "Simulation is already running. Please stop it before starting again.";
        }
//        resetSimulation();

        vendorFactory.startVendors();
        customerService.startCustomer("Customer-1");

        // Start a background thread to monitor and reset
        new Thread(this::monitorAndReset).start();

        return "Simulation started!";
    }

    @GetMapping("/tickets/stop")
    public String stopSimulation() {
        vendorFactory.stopVendors();
        customerService.stopCustomers();
        resetSimulation();
        return "Simulation stopped and reset!";
    }

    @GetMapping(value = "/tickets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamTickets() {
        return ticketPool.getLogs()
                .doOnNext(data -> System.out.println("Streaming data: " + data)) // Log each data item
                .concatWith(Mono.just("Simulation complete.")) // Add a final message
                .doOnCancel(() -> System.out.println("Client disconnected. Stopping stream."))
                .doOnError(error -> System.out.println("Error occurred: " + error.getMessage()))
                .doFinally(signalType -> System.out.println("Streaming finished: " + signalType));
    }

//    @GetMapping(value = "/tickets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> streamTickets() {
//        return ticketPool.getLogs()
//                .concatWith(Mono.just("Simulation complete.")) // Add a final message
//                .doOnCancel(() -> System.out.println("Client disconnected. Stopping stream."))
//                .doOnError(error -> System.out.println("Error occurred: " + error.getMessage()))
//                .doFinally(signalType -> System.out.println("Streaming finished: " + signalType));
//    }

    @GetMapping("/tickets/status")
    public String vendorStatus() {
        boolean stopped = vendorFactory.areVendorsStopped();
        return stopped ? "All vendor threads are stopped." : "Vendor threads are still running.";
    }

    @GetMapping("/customers/status")
    public String customerStatus() {
        boolean stopped = customerService.areCustomersStopped();
        return stopped ? "All customer threads are stopped." : "Customer threads are still running.";
    }

    private void monitorAndReset() {
        try {
            // Wait until both vendors and customers stop, and all tickets are issued
            while (!vendorFactory.areVendorsStopped() || !customerService.areCustomersStopped() || !ticketPool.allTicketsIssued()) {
                Thread.sleep(500); // Check every 500ms
            }

            // Perform reset once the simulation finishes
            resetSimulation();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Monitoring thread interrupted.");
        }
    }

    private void resetSimulation() {
        simulationService.reset();
        System.out.println("Simulation has been reset automatically.");
    }

    @GetMapping("/simulation/status")
    public String simulationStatus() {
        boolean vendorsRunning = !vendorFactory.areVendorsStopped();
        boolean customersRunning = !customerService.areCustomersStopped();
        return vendorsRunning || customersRunning ? "Simulation is running." : "Simulation is stopped.";
    }

}

