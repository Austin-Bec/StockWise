package com.stockwise.transaction;

import com.stockwise.item.Item;
import com.stockwise.item.ItemService;
import com.stockwise.transaction.dto.InventoryTransactionRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/templates/transactions")
public class TransactionPageController {

    private final InventoryTransactionService transactionService;
    private final ItemService itemService;

    public TransactionPageController(InventoryTransactionService transactionService,
                                     ItemService itemService) {
        this.transactionService = transactionService;
        this.itemService = itemService;
    }

    @GetMapping("/new/{itemId}")
    public String showTransactionForm(@PathVariable Long itemId, Model model) {
        Item item = itemService.getRequiredItem(itemId);

        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setItemId(itemId);

        model.addAttribute("templates/items", item);
        model.addAttribute("transactionForm", request);
        return "templates/transactions/form";
    }

    @PostMapping
    public String saveTransaction(@Valid @ModelAttribute("transactionForm") InventoryTransactionRequest request,
                                  BindingResult bindingResult,
                                  Model model) {
        Item item = itemService.getRequiredItem(request.getItemId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("templates/items", item);
            return "templates/transactions/form";
        }

        try {
            transactionService.processTransaction(request);
            return "redirect:/transactions/history/" + request.getItemId();
        } catch (Exception ex) {
            model.addAttribute("templates/items", item);
            model.addAttribute("error", ex.getMessage());
            return "templates/transactions/form";
        }
    }

    @GetMapping("/history/{itemId}")
    public String showHistory(@PathVariable Long itemId, Model model) {
        Item item = itemService.getRequiredItem(itemId);

        model.addAttribute("templates/items", item);
        model.addAttribute("templates/transactions", transactionService.getTransactionsForItem(itemId));
        return "templates/transactions/history";
    }
}