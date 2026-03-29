package com.stockwise.item;

import com.stockwise.item.dto.ItemCreateRequest;
import com.stockwise.item.dto.ItemUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Handles page-based item management workflows including
 * listing items, creating items, viewing item details,
 * editing items, and deleting items.
 */
@Controller
@RequestMapping("/items")
public class ItemPageController {

    private final ItemService itemService;

    public ItemPageController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Displays the full item listing page.
     *
     * @param model the MVC model
     * @return item list view
     */
    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "items/list";
    }

    /**
     * Displays the item creation form.
     *
     * @param model the MVC model
     * @return item form view in create mode
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setReorderThreshold(5);

        model.addAttribute("itemForm", request);
        model.addAttribute("formMode", "create");
        return "items/form";
    }

    /**
     * Processes a submitted create-item form.
     *
     * @param request form data
     * @param bindingResult validation result
     * @param model the MVC model
     * @return redirect to detail page on success, or returns form on error
     */
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

    /**
     * Displays the detail page for a single item.
     *
     * @param id the item id
     * @param model the MVC model
     * @return item detail view
     */
    @GetMapping("/{id}")
    public String viewItem(@PathVariable Long id, Model model) {
        Item item = itemService.getRequiredItem(id);
        model.addAttribute("item", item);
        return "items/detail";
    }

    /**
     * Displays the edit form for an existing item.
     *
     * @param id the item id
     * @param model the MVC model
     * @return item form view in edit mode
     */
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

    /**
     * Processes a submitted update-item form.
     *
     * @param id the item id
     * @param request updated item data
     * @param bindingResult validation result
     * @param model the MVC model
     * @return redirect to item detail page on success, or returns form on error
     */
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

    /**
     * Deletes an item and redirects back to the item list.
     *
     * @param id the item id
     * @return redirect to item list page
     */
    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        itemService.deleteRequiredItem(id);
        return "redirect:/items";
    }
}