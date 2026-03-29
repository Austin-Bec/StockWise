package com.stockwise.transaction;

import com.stockwise.item.Item;
import com.stockwise.item.ItemRepository;
import com.stockwise.transaction.dto.InventoryTransactionRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Handles page-based inventory transaction workflows such as
 * showing the transaction form, saving transactions, and viewing transaction history.
 */
@Controller
@RequestMapping("/transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;
    private final ItemRepository itemRepository;

    public InventoryTransactionController(InventoryTransactionService transactionService,
                                          ItemRepository itemRepository) {
        this.transactionService = transactionService;
        this.itemRepository = itemRepository;
    }

    /**
     * Displays the transaction entry form for a specific item.
     *
     * @param itemId the item id
     * @param model the MVC model
     * @return transaction form view
     */
    @GetMapping("/new/{itemId}")
    public String showTransactionForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item id"));

        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setItemId(itemId);

        model.addAttribute("item", item);
        model.addAttribute("transactionRequest", request);
        return "transactions/form";
    }

    /**
     * Processes a submitted inventory transaction form.
     *
     * @param request submitted transaction data
     * @param model the MVC model
     * @return redirect to transaction history on success, or returns form with error on failure
     */
    @PostMapping("/save")
    public String saveTransaction(@ModelAttribute("transactionRequest") InventoryTransactionRequest request,
                                  Model model) {
        try {
            transactionService.processTransaction(request);
            return "redirect:/transactions/history/" + request.getItemId();
        } catch (IllegalArgumentException ex) {
            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid item id"));

            model.addAttribute("item", item);
            model.addAttribute("transactionRequest", request);
            model.addAttribute("error", ex.getMessage());
            return "transactions/form";
        }
    }

    /**
     * Displays transaction history for a specific item.
     *
     * @param itemId the item id
     * @param model the MVC model
     * @return transaction history view
     */
    @GetMapping("/history/{itemId}")
    public String showTransactionHistory(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item id"));

        model.addAttribute("item", item);
        model.addAttribute("transactions", transactionService.getTransactionsForItem(itemId));
        return "transactions/history";
    }
}