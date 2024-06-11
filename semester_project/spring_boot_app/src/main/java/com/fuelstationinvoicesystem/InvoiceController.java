package com.fuelstationinvoicesystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @PostMapping("/{customerId}")
    public ResponseEntity<String> startInvoiceGeneration(@PathVariable String customerId) {
        rabbitMQSender.send(customerId);
        return ResponseEntity.ok("Invoice generation started for customer: " + customerId);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable String customerId) {
        boolean isAvailable = checkInvoiceAvailability(customerId);
        if (isAvailable) {
            String downloadLink = generateDownloadLink(customerId);
            InvoiceResponse response = new InvoiceResponse(downloadLink, new Date());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private boolean checkInvoiceAvailability(String customerId) {
        // Logik zur Überprüfung der Verfügbarkeit der Rechnung ###
        return true; // Beispielhaft als immer verfügbar markiert
    }

    private String generateDownloadLink(String customerId) {
        // nicht sicher ob das hierhin gehört ###
        return "http://localhost:8080/files/invoices/" + customerId + ".pdf"; // Beispielhafter Link
    }
}

class InvoiceResponse {
    private String downloadLink;
    private Date creationTime;

    public InvoiceResponse(String downloadLink, Date creationTime) {
        this.downloadLink = downloadLink;
        this.creationTime = creationTime;
    }

    // Getter und Setter
    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
