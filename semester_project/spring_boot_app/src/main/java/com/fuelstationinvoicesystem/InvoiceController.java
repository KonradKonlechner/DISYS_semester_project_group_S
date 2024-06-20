package com.fuelstationinvoicesystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public ResponseEntity<Object> getInvoice(@PathVariable String customerId) {
        String filePath = "semester_project/invoices/customer_" + customerId + "_invoice.pdf";
        File file = new File(filePath);
        System.out.println("Checking file at: " + file.getAbsolutePath());
        if (file.exists()) {
            try {
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File not found exception: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found for customer: " + customerId);
        }
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
