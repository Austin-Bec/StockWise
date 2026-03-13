package com.stockwise.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ItemController.
 *
 * I am loading the full Spring Boot application context here so the tests
 * exercise the controller, service, repository, validation, and exception handling.
 *
 * This gives me stronger proof that the API is working end to end.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * GET /api/items
     * The seeded data loader should provide starting records.
     */
    @Test
    void getAllItems_returnsOk() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    /**
     * GET /api/items/{id}
     * Requesting an item that does not exist should return 404.
     */
    @Test
    void getItemById_whenMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/items/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    /**
     * GET /api/items/sku/{sku}
     * Requesting an item by SKU should return the matching item when it exists.
     */
    @Test
    void getItemBySku_whenExists_returnsItem() throws Exception {
        mockMvc.perform(get("/api/items/sku/SKU-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hammer"))
                .andExpect(jsonPath("$.sku").value("SKU-001"));
    }

    /**
     * GET /api/items/sku/{sku}
     * Requesting a missing SKU should return 404.
     */
    @Test
    void getItemBySku_whenMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/items/sku/SKU-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    /**
     * POST /api/items
     * Creating a valid item should return 201 Created.
     */
    @Test
    void createItem_withValidPayload_returnsCreated() throws Exception {
        String body = """
                {
                  "name": "Test Drill",
                  "sku": "SKU-900",
                  "quantityOnHand": 12,
                  "unitCost": 49.99,
                  "active": true
                }
                """;

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Drill"))
                .andExpect(jsonPath("$.sku").value("SKU-900"));
    }

    /**
     * POST /api/items
     * Creating an item with a duplicate SKU should return 409 Conflict.
     */
    @Test
    void createItem_withDuplicateSku_returnsConflict() throws Exception {
        String body = """
                {
                  "name": "Duplicate Hammer",
                  "sku": "SKU-001",
                  "quantityOnHand": 4,
                  "unitCost": 10.00,
                  "active": true
                }
                """;

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_SKU"));
    }

    /**
     * POST /api/items
     * Invalid request data should return 400 with validation details.
     */
    @Test
    void createItem_withInvalidPayload_returnsBadRequest() throws Exception {
        String body = """
                {
                  "name": "",
                  "sku": "",
                  "quantityOnHand": -1,
                  "unitCost": -2
                }
                """;

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.sku").exists())
                .andExpect(jsonPath("$.fields.quantityOnHand").exists())
                .andExpect(jsonPath("$.fields.unitCost").exists());
    }

    /**
     * PUT /api/items/{id}
     * Updating an existing item should return 200 and the changed values.
     */
    @Test
    void updateItem_whenValid_returnsOk() throws Exception {
        String body = """
                {
                  "name": "Updated Hammer",
                  "sku": "SKU-001",
                  "quantityOnHand": 30,
                  "unitCost": 11.49,
                  "active": true
                }
                """;

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Hammer"))
                .andExpect(jsonPath("$.quantityOnHand").value(30))
                .andExpect(jsonPath("$.unitCost").value(11.49));
    }

    /**
     * PUT /api/items/{id}
     * Updating an item that does not exist should return 404.
     */
    @Test
    void updateItem_whenMissing_returnsNotFound() throws Exception {
        String body = """
                {
                  "name": "Ghost Item",
                  "sku": "SKU-404",
                  "quantityOnHand": 1,
                  "unitCost": 1.00,
                  "active": true
                }
                """;

        mockMvc.perform(put("/api/items/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    /**
     * DELETE /api/items/{id}
     * Deleting an existing item should return 204 No Content.
     */
    @Test
    void deleteItem_whenExisting_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/items/2"))
                .andExpect(status().isNoContent());
    }

    /**
     * DELETE /api/items/{id}
     * Deleting an item that does not exist should return 404.
     */
    @Test
    void deleteItem_whenMissing_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/items/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}