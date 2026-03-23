package com.stockwise.transaction;

import com.stockwise.item.Item;
import com.stockwise.item.ItemRepository;
import com.stockwise.transaction.dto.InventoryTransactionRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;
    private final ItemRepository itemRepository;

    public InventoryTransactionController(InventoryTransactionService transactionService,
                                          ItemRepository itemRepository) {
        this.transactionService = transactionService;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/new/{itemId}")
    public String showTransactionForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item id"));

        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setItemId(itemId);

        model.addAttribute("templates/templates/item", item);
        model.addAttribute("transactionRequest", request);
        return "templates/transactions/form";
    }

    @PostMapping("/save")
    public String saveTransaction(@ModelAttribute("transactionRequest") InventoryTransactionRequest request,
                                  Model model) {
        try {
            transactionService.processTransaction(request);
            return "redirect:/transactions/history/" + request.getItemId();
        } catch (IllegalArgumentException ex) {
            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid item id"));

            model.addAttribute("templates/templates/item", item);
            model.addAttribute("transactionRequest", request);
            model.addAttribute("error", ex.getMessage());
            return "templates/transactions/form";
        }
    }

    @GetMapping("/history/{itemId}")
    public String showTransactionHistory(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item id"));

        model.addAttribute("templates/templates/item", item);
        model.addAttribute("templates/transactions", transactionService.getTransactionsForItem(itemId));
        return "templates/transactions/history";
    }
}