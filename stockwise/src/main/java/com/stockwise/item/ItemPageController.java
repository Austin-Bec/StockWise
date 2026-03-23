package com.stockwise.item;

import com.stockwise.item.dto.ItemCreateRequest;
import com.stockwise.item.dto.ItemUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
public class ItemPageController {

    private final ItemService itemService;

    public ItemPageController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "items/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setReorderThreshold(5);

        model.addAttribute("itemForm", request);
        model.addAttribute("formMode", "create");
        return "items/form";
    }

    @PostMapping
    public String createItem(@Valid @ModelAttribute("itemForm") ItemCreateRequest request,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "items/form";
        }

        try {
            Item saved = itemService.createItem(request);
            return "redirect:/items/" + saved.getId();
        } catch (Exception ex) {
            model.addAttribute("formMode", "create");
            model.addAttribute("error", ex.getMessage());
            return "items/form";
        }
    }

    @GetMapping("/{id}")
    public String viewItem(@PathVariable Long id, Model model) {
        Item item = itemService.getRequiredItem(id);
        model.addAttribute("item", item);
        return "items/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Item item = itemService.getRequiredItem(id);

        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName(item.getName());
        request.setSku(item.getSku());
        request.setQuantityOnHand(item.getQuantityOnHand());
        request.setUnitCost(item.getUnitCost());
        request.setReorderThreshold(item.getReorderThreshold());
        request.setActive(item.isActive());

        model.addAttribute("itemId", id);
        model.addAttribute("itemForm", request);
        model.addAttribute("formMode", "edit");
        return "items/form";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable Long id,
                             @Valid @ModelAttribute("itemForm") ItemUpdateRequest request,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("itemId", id);
            model.addAttribute("formMode", "edit");
            return "items/form";
        }

        try {
            itemService.updateItem(id, request);
            return "redirect:/items/" + id;
        } catch (Exception ex) {
            model.addAttribute("itemId", id);
            model.addAttribute("formMode", "edit");
            model.addAttribute("error", ex.getMessage());
            return "items/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        itemService.deleteRequiredItem(id);
        return "redirect:/items";
    }
}