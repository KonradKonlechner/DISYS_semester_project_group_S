package com.fuelstationinvoicesystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitMQSender rabbitMQSender;

    private final String basePath = System.getProperty("user.dir") + "/semester_project";

    @Test
    public void testStartInvoiceGeneration() throws Exception {
        String customerId = "123";

        // Mocking des RabbitMQSender, um nichts zu tun, wenn die send-Methode mit customerId aufgerufen wird
        doNothing().when(rabbitMQSender).send(customerId);

        // Simuliert eine POST-Anfrage, um die Rechnungserstellung zu starten, und prüft auf eine erfolgreiche Antwort
        mockMvc.perform(MockMvcRequestBuilders.post("/invoices/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().string("Invoice generation started for customer: " + customerId));

        // Überprüft, ob die send-Methode des RabbitMQSender mit der richtigen customerId aufgerufen wurde
        verify(rabbitMQSender).send(customerId);
    }

    @Test
    public void testGetInvoiceNotFound() throws Exception {
        String customerId = "123";

        // Simuliert eine GET-Anfrage für eine nicht vorhandene Rechnung und prüft auf eine 404-Antwort
        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/{customerId}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Invoice not found for customer: " + customerId));
    }

    @Test
    public void testGetInvoiceFound() throws Exception {
        String customerId = "123";
        String filePath = basePath + "/invoices/customer_" + customerId + "_invoice.pdf";

        // Simuliert das Vorhandensein der Rechnungsdatei
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();

        // Simuliert eine GET-Anfrage für eine vorhandene Rechnung und prüft auf eine erfolgreiche Antwort mit PDF-Inhaltstyp
        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        // Löscht die erstellte Datei zur Bereinigung
        file.delete();
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Löscht das gesamte Verzeichnis semester_project und alle enthaltenen Dateien und Unterverzeichnisse nach jedem Test
        Path directory = Paths.get(basePath);
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
