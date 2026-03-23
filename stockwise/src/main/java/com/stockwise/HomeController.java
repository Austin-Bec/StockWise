package com.stockwise;

import com.stockwise.item.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ItemService itemService;

    public HomeController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalItems", itemService.getAllItems().size());
        model.addAttribute("activeItems", itemService.getActiveItems().size());
        model.addAttribute("lowStockItems", itemService.getLowStockItems());
        model.addAttribute("lowStockCount", itemService.getLowStockItemCount());
        model.addAttribute("totalInventoryValue", itemService.getTotalInventoryValue());
        return "home";
    }
}